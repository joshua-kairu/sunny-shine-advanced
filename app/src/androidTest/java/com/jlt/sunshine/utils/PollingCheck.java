package com.jlt.sunshine.utils;

import junit.framework.Assert;

/**
 *  Sunshine
 *
 * A simple weather app
 *
 * Copyright (C) 2016 Kairu Joshua Wambugu
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */

// begin class PollingCheck
public abstract class PollingCheck {

    /** CONSTANTS */

    /* Longs */

    private static final long TIME_SLICE = 50;

    /** Integers */

    /** Strings */

    /** VARIABLES */

    /* Primitives */

    private long mTimeout = 3000;

    /** CONSTRUCTOR */

    // null constructor
    public PollingCheck() {}

    // constructor for timeout

    public PollingCheck( long timeout ) { mTimeout = timeout; }

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    /** Other Methods */

    protected abstract boolean check(); // should be implemented by extenders

    /* Do a polling check */
    // begin method run
    public  void run() {

        // 0. if it's check time, stop
        // 1. while the time out is on
        // 1a. attempt to sleep the thread for the given time slice
        // 1b. if it's check time, stop
        // 1c. decrement the timeout by the sleep time slice
        // 2. Failure! Should have been checked by now!

        // 0. if it's check time, stop

        if ( check() == true ) { return; }

        // 1. while the time out is on

        long timeout = mTimeout;

        // begin while we are timing out
        while ( timeout > 0 ) {

            // 1a. attempt to sleep the thread for the given time slice

            try { Thread.sleep( TIME_SLICE ); }

            catch ( InterruptedException e ) { Assert.fail( "Unexpected InterruptedException" ); }

            // 1b. if it's check time, stop

            if ( check() == true ) { return; }

            // 1c. decrement the timeout by the sleep time slice

            timeout -= TIME_SLICE;

        } // end while we are timing out

        // 2. Failure! Should have been checked by now!

        Assert.fail( "Unexpected timeout" );

    } // end method run

    /** INNER CLASSES */

} // end class PollingCheck