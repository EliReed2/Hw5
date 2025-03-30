#include <pthread.h>
#include <stdlib.h>
#include "matchmaker.h"
#include <string.h>
#include <stdio.h>


//Beginning Capacity for condition array and waitingPlayers array
#define ARRAY_SIZE 10


// Global list of waiting players and their information
Player *waitingPlayers;


// Global list of condition variables, one variable per player
pthread_cond_t *conditionArray;


//Global 2 int array to track the indexes of what two players are currently playing
int currentPlayers[2];


//Additional condition variable to handle waiting while opponent is still playing in done playing
pthread_cond_t opponentCond = PTHREAD_COND_INITIALIZER;


//Condition variable to hold waiting threads while game is in progress
pthread_cond_t gameProgressCond = PTHREAD_COND_INITIALIZER;


//int to determine how many players have stopped playing
int currentPlayerCount;


// Boolean tracking if matchmaking has been concluded
bool matchmakingStatus;


// Boolean tracking if player is currently in a match
bool matchStatus;


// Capacity of waitingPlayers array
int playerCapacity;


//Current size of waitingPlayers list
int playerSize;


// Range of skill levels of players
static int gameRange;


// Maximum difference in skill for a pair of players
static int gameGap;


// Mutex for entry and exit from monitor
pthread_mutex_t monitorLock = PTHREAD_MUTEX_INITIALIZER;


//
// Initial Capacity of waitingPlayers array
int playerCapacity;




//Initializes monitor and allocates any neccesary heap memory
void initMonitor(int range, int gap) {
   //Assign assign and gap to variables
   gameRange = range;
   gameGap = gap;
   // Set matchmakingStatus to true, matchStatus to false, and opponentPlaying to false
   matchmakingStatus = true;
   matchStatus = false;
   currentPlayerCount = 0;
   //Initialize waitingPlayers list's capacity and size
   playerCapacity = ARRAY_SIZE;
   playerSize = 0;
   // Malloc space for waitingPlayers list
   waitingPlayers = (Player *) malloc(playerCapacity * sizeof(Player));
   // Set conditionArray variables and allocate space
   conditionArray = (pthread_cond_t *) malloc(playerCapacity * sizeof(pthread_cond_t));
}


//Waits until a player can play a match with anther equally skilled player, returns true if match was found
bool findMatch( char const *name, int skill) {
   //Lock mutex to enter monitor
   pthread_mutex_lock(&monitorLock);
   //Ensure matchmaking is currently going
   if (!matchmakingStatus) {
       //Unlock mutex
       pthread_mutex_unlock(&monitorLock);
       return false;
   }
   //Ensure waiting players list doesn't need to be expanded, if it does so does conditionArray
   if (playerCapacity == playerSize) {
       //Double capacity
       playerCapacity *= 2;
       //Realloc space for lists
       Player *tempList = realloc(waitingPlayers, playerCapacity * sizeof(Player));
       pthread_cond_t *tempCondList = realloc(conditionArray, playerCapacity * sizeof(pthread_cond_t));
       waitingPlayers = tempList;
       conditionArray = tempCondList;
   }
   //Create new Player on the waitingPlayers list
   waitingPlayers[playerSize].skill = skill;
   strcpy(waitingPlayers[playerSize].playerName, name);
   //Save Player's place in array
   int waitingIndex = playerSize;
   playerSize++;
   //Also initialize players condition variable
   pthread_cond_init(&conditionArray[waitingIndex], NULL);
   //Loop forever while matchmaking is occuring
   while (matchmakingStatus) {
       //Wait while game is in progress
       while (matchStatus) {
           pthread_cond_wait(&gameProgressCond, &monitorLock);
           if (!matchmakingStatus) {
               pthread_mutex_unlock(&monitorLock);
               return false;
           }
       }
       //Loop through all waitingPlayers and see if we are compatible with any
       for (int i = 0; i < playerSize; i++) {
           //Check to see if players are compatible
           int skillDifference = abs(waitingPlayers[i].skill - waitingPlayers[waitingIndex].skill);
           if (i != waitingIndex && skillDifference <= gameGap && waitingPlayers[i].skill != waitingPlayers[waitingIndex].skill) {
               //If compatible set matchStatus to true so other thread knows they are matched up
               matchStatus = true;
               currentPlayerCount = 2;
               //Place player in currentPlayers, first player always fills first slot
               currentPlayers[0] = waitingIndex;
               //Place other player in the currentPlayers array, awoken player always fills second slot
               currentPlayers[1] = i;
               //Release other player's condition variable
               pthread_cond_signal(&conditionArray[i]);
               //Give up mutex lock
               pthread_mutex_unlock(&monitorLock);
               return true;
           }
       }
       //If no match is found or a match is happening player must wait until something changes
       pthread_cond_wait(&conditionArray[waitingIndex], &monitorLock);
       //When awoken, check if we have been matched
       if (matchStatus && (currentPlayers[0] == waitingIndex || currentPlayers[1] == waitingIndex)) {
           //Report that players are playing
           printf("Playing: %s vs %s\n", waitingPlayers[currentPlayers[0]].playerName, waitingPlayers[currentPlayers[1]].playerName);
           //If match is already scheduled player must give up their lock and return true
           pthread_mutex_unlock(&monitorLock);
           return true;
       }
       //Otherwise loop through again in search of a compatible partner
   }
   //Exit findMatch
   pthread_mutex_unlock(&monitorLock);
   return false;
}


//Called by a player when they are done playing
void donePlaying() {
   //Enter monitor
   pthread_mutex_lock(&monitorLock);
   //Decrement currentPlayerCount
   currentPlayerCount--;
   //Check if you are first player done
   if (currentPlayerCount == 1) {
       //Wait until other play has finished
       pthread_cond_wait(&opponentCond, &monitorLock);
   }
   else if (currentPlayerCount == 0) {
       //if this is second player, wake up the first player
       pthread_cond_signal(&opponentCond);
       //Then reset the game all wake up all other threads
       matchStatus = false;
       currentPlayers[0] = -1;
       currentPlayers[1] = -1;
       currentPlayerCount = 0;


       // Wake up all threads waiting for next game
       pthread_cond_broadcast(&gameProgressCond);
      
       //Signal all threads that they can start checking compatability again
       for (int i = 0; i < playerSize; i++) {
           pthread_cond_signal(&conditionArray[i]);
       }
   }
   //Unlock monitor
   pthread_mutex_unlock(&monitorLock);
}


//Stops matchmaking but allows those playing to finish their game
void stopMatchmaking() {
   //Get monitor lock
   pthread_mutex_lock(&monitorLock);
   //Threads should eventually notice and exit findMatch
   matchmakingStatus = false;
   //Woke up all waiting threads
   pthread_cond_broadcast(&opponentCond);
   pthread_cond_broadcast(&gameProgressCond);
   for (int i = 0; i < playerCapacity; i++) {
       pthread_cond_signal(&conditionArray[i]);
   }
   //Give up lock
   pthread_mutex_unlock(&monitorLock);
}


//Destroy monitor and free allocated memory
void destroyMonitor() {
   for (int i = 0; i < playerSize; i++) {
       pthread_cond_destroy(&conditionArray[i]);
   }
   free(waitingPlayers);
   free(conditionArray);
}
