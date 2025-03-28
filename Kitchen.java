import java.util.Random;

public class Kitchen {
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

  // An object representing the lock on each appliance.
  // Locking the needed objects before cooking prevents two
  // chefs from trying to use the same appliance at the same time.
  private static Object mixer = new Object();
  private static Object blender = new Object();
  private static Object oven = new Object();
  private static Object grill = new Object();
  private static Object fryer = new Object();
  private static Object griddle = new Object();
  private static Object coffeeMaker = new Object();
  private static Object microwave = new Object();

  /** Tad is a chef needing 60 milliseconds to prepare a dish. */
  private static class Tad extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( griddle ) {
          synchronized ( grill ) {
            synchronized ( microwave ) {
              cook( 60 );
            }
          }
        }

        rest( 25 );
      }
    }
  }

  /** Merry is a chef needing 15 milliseconds to prepare a dish. */
  private static class Merry extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( mixer ) {
          synchronized ( oven ) {
            synchronized ( blender ) {
              cook( 15 );
            }
          }
        }

        rest( 25 );
      }
    }
  }

  /** Charles is a chef needing 90 milliseconds to prepare a dish. */
  private static class Charles extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( coffeeMaker ) {
          synchronized ( griddle ) {
            cook( 90 );
          }
        }

        rest( 25 );
      }
    }
  }

  /** Merlin is a chef needing 15 milliseconds to prepare a dish. */
  private static class Merlin extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( blender ) {
          synchronized ( mixer ) {
            cook( 15 );
          }
        }

        rest( 25 );
      }
    }
  }

  /** Lyn is a chef needing 75 milliseconds to prepare a dish. */
  private static class Lyn extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( griddle ) {
          synchronized ( fryer ) {
            cook( 75 );
          }
        }

        rest( 25 );
      }
    }
  }

  /** Marian is a chef needing 45 milliseconds to prepare a dish. */
  private static class Marian extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( coffeeMaker ) {
          synchronized ( microwave ) {
            synchronized ( oven ) {
              cook( 45 );
            }
          }
        }

        rest( 25 );
      }
    }
  }

  /** Summer is a chef needing 30 milliseconds to prepare a dish. */
  private static class Summer extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( fryer ) {
          synchronized ( blender ) {
            synchronized ( coffeeMaker ) {
              cook( 30 );
            }
          }
        }

        rest( 25 );
      }
    }
  }

  /** Sammy is a chef needing 60 milliseconds to prepare a dish. */
  private static class Sammy extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( grill ) {
          synchronized ( fryer ) {
            cook( 60 );
          }
        }

        rest( 25 );
      }
    }
  }

  /** Lura is a chef needing 15 milliseconds to prepare a dish. */
  private static class Lura extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( microwave ) {
          synchronized ( mixer ) {
            cook( 15 );
          }
        }

        rest( 25 );
      }
    }
  }

  /** Ginny is a chef needing 45 milliseconds to prepare a dish. */
  private static class Ginny extends Chef {
    public void run() {
      while ( running ) {
        // Get the appliances this chef uses.
        synchronized ( grill ) {
          synchronized ( oven ) {
            cook( 45 );
          }
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
