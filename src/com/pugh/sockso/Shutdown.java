
package com.pugh.sockso;

public class Shutdown extends Thread {

    /**
     *  We don't have long, so try and do the most important cleanup (shutting
     *  down the database connection cleanly atm)
     *
     */

    @Override
    public void run() {

        Main.shutdownDatabase();

    }

}