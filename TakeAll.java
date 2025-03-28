import java.util.Random;

public class TakeAll {
  /** To tell all the chefs when they can quit running. */
  private static boolean running = true;

  /** Superclass for all chefs.  Contains methods to cook and rest and
      keeps a record of how many dishes were prepared. */
  private static class Chef extends Thread {
    /** Number of dishes prepared by this chef. */
    private int dishCount = 0;

    /** Source of randomness for this chef. */
    private Random rand = new Random();

    /** Called after the chef has locked all the required appliances and is
        ready to cook for about the given number of milliseconds. */
    protected void cook( int duration ) {
      System.out.printf( "%s is cooking\n", getClass().getSimpleName() );
      try {
        // Wait for a while (pretend to be cooking)
        Thread.sleep( rand.nextInt( duration / 2 ) + duration / 2 );
      } catch ( InterruptedException e ) {
      }
      dishCount++;
    }

    /** Called between dishes, to let the chef rest before cooking another dish. */
    protected void rest( int duration ) {
      System.out.printf( "%s is resting\n", getClass().getSimpleName() );
      try {
        // Wait for a while (pretend to be resting)
        Thread.sleep( rand.nextInt( duration / 2 ) + duration / 2 );
      } catch ( InterruptedException e ) {
      }
    }
  }
  // Object to handle synchronization of cheifs when modifying the appliance bools
  private static Object applianceLock = new Object();

  // Boolean values represetning if an appliance is available for use by a chef
  private static boolean mixer = false;
  private static boolean blender = false;
  private static boolean oven = false;
  private static boolean grill = false;
  private static boolean fryer = false;
  private static boolean griddle = false;
  private static boolean coffeeMaker = false;
  private static boolean microwave = false;

  /** Tad is a chef needing 60 milliseconds to prepare a dish. */
  private static class Tad extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (griddle || grill || microwave) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            griddle = true;
            grill = true;
            microwave = true;
        }
        //Cook with appliances
        cook( 60 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            griddle = false;
            grill = false;
            microwave = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Merry is a chef needing 15 milliseconds to prepare a dish. */
  private static class Merry extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (mixer || oven || blender) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            mixer = true;
            oven = true;
            blender = true;
        }
        //Cook with appliances
        cook( 15 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            mixer = false;
            blender = false;
            oven = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Charles is a chef needing 90 milliseconds to prepare a dish. */
  private static class Charles extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (coffeeMaker || griddle) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            coffeeMaker = true;
            griddle = true;
        }
        //Cook with appliances
        cook( 90 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            coffeeMaker = false;
            griddle = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Merlin is a chef needing 15 milliseconds to prepare a dish. */
  private static class Merlin extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (mixer || blender) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            mixer = true;
            blender = true;
        }
        //Cook with appliances
        cook( 15 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            mixer = false;
            blender = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Lyn is a chef needing 75 milliseconds to prepare a dish. */
  private static class Lyn extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (griddle || fryer) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            griddle = true;
            fryer = true;
        }
        //Cook with appliances
        cook( 75 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            griddle = false;
            fryer = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Marian is a chef needing 45 milliseconds to prepare a dish. */
  private static class Marian extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (coffeeMaker || microwave || oven) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            microwave = true;
            oven = true;
            coffeeMaker = true;
        }
        //Cook with appliances
        cook( 45 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            microwave = false;
            coffeeMaker = false;
            oven = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Summer is a chef needing 30 milliseconds to prepare a dish. */
  private static class Summer extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (fryer || coffeeMaker || blender) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            fryer = true;
            blender = true;
            coffeeMaker = true;
        }
        //Cook with appliances
        cook( 30 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            fryer = false;
            blender = false;
            coffeeMaker = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }

        rest( 25 );
      }
    }
  }

  /** Sammy is a chef needing 60 milliseconds to prepare a dish. */
  private static class Sammy extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (grill || fryer) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            grill = true;
            fryer = true;
        }
        //Cook with appliances
        cook( 60 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            grill = false;
            fryer = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Lura is a chef needing 15 milliseconds to prepare a dish. */
  private static class Lura extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (mixer || microwave) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            mixer = true;
            microwave = true;
        }
        //Cook with appliances
        cook( 15 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            mixer = false;
            microwave = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  /** Ginny is a chef needing 45 milliseconds to prepare a dish. */
  private static class Ginny extends Chef {
    public void run() {
      while ( running ) {
        // Enter synchronized block to check appliance availability
        synchronized (applianceLock) {
            //while any of needed appliances are in use, wait until they are free
            while (grill || oven) {
                try {
                    applianceLock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception occured");
                }
            }
            //If all appliances are free, lock them so you have exclusive access
            grill = true;
            oven = true;
        }
        //Cook with appliances
        cook( 45 );
        // Re-enter synchronized block when cooking is over to free appliances and notify other chefs
        synchronized (applianceLock) {
            //Free appliance locks
            grill = false;
            oven = false;
            //Notify all threads that some appliances are free
            applianceLock.notifyAll();
        }
        rest( 25 );
      }
    }
  }

  public static void main( String[] args ) throws InterruptedException {
    // Make a thread for each of our chefs.
    Chef chefList[] = {
      new Tad(),
      new Merry(),
      new Charles(),
      new Merlin(),
      new Lyn(),
      new Marian(),
      new Summer(),
      new Sammy(),
      new Lura(),
      new Ginny(),
    };

    // Start running all our chefs.
    for ( int i = 0; i < chefList.length; i++ )
      chefList[ i ].start();

    // Let the chefs cook for a while, then ask them to stop.
    Thread.sleep( 10000 );
    running = false;

    // Wait for all our chefs to finish, and collect up how much
    // cooking was done.
    int total = 0;
    for ( int i = 0; i < chefList.length; i++ ) {
      chefList[ i ].join();
      System.out.printf( "%s cooked %d dishes\n",
                         chefList[ i ].getClass().getSimpleName(),
                         chefList[ i ].dishCount );
      total += chefList[ i ].dishCount;
    }
    System.out.printf( "Total dishes cooked: %d\n", total );
  }
}
