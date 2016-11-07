package com.joslittho.sunshine.data;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.joslittho.sunshine.utils.PollingCheck;

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

/* Utility class to test the ContentObserver callbacks using the PollingCheck class */
// begin class TestContentObserver
public class TestContentObserver extends ContentObserver {

    /** CONSTANTS */

    /** Integers */

    /** Strings */

    /** VARIABLES */

    /* Handler Threads */

    // HandlerThread
    // Handy class for starting a new thread that has a looper.
    // The looper can then be used to create handler classes.
    // Note that start() must still be called.
    private final HandlerThread mHandlerThread;

    /* Primitives */

    private boolean mContentChanged;

    /** CONSTRUCTOR */
    // begin constructor
    public TestContentObserver( HandlerThread handlerThread ) {

        // 0. give super a handler with the handler thread looper
        // 1. initialize local handler thread

        // 0. give super a handler with the handler thread looper

        super( new Handler( handlerThread.getLooper() ) );

        // 1. initialize local handler thread

        mHandlerThread = handlerThread;

    } // end constructor

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // onChange
    // this method is called when a content change occurs.
    // on earlier Android versions, this kind of onChange is called
    public void onChange( boolean selfChange ) { onChange( selfChange, null ); }

    @Override
    // begin onChange
    public void onChange( boolean selfChange, Uri uri ) {

        // 0. store the changed state

        // 0. store the changed state

        mContentChanged = true;

    } // end onChange

    /** Other Methods */

    // begin method getTestContentObserver
    public static TestContentObserver getTestContentObserver() {

        // 0. initialize a handler thread for content observing
        // 1. start this thread
        // 2. return a new TestContentObserver using this thread

        // 0. initialize a handler thread for content observing

        HandlerThread handlerThread = new HandlerThread( "ContentObserverThread" );

        // 1. start this thread

        handlerThread.start();

        // 2. return a new TestContentObserver using this thread

        return new TestContentObserver( handlerThread );

    } // end method getTestContentObserver

    // begin method waitForNotificationOrFail
    public void waitForNotificationOrFail() {

        // 0. use a polling check to check the status of the content every five seconds
        // 1. stop the handler thread

        // 0. use a polling check to check the status of the content every five seconds

        // begin new PollingCheck
        new PollingCheck( 5000 ) {

            @Override
            protected boolean check() { return mContentChanged; }

        }.run(); // end new PollingCheck

        // 1. stop the handler thread

        // quit - Quits the handler thread's looper, causing the handler thread's looper
        // to terminate without processing any more messages in the message queue.
        mHandlerThread.quit();

    } // end method waitForNotificationOrFail

    /** INNER CLASSES */

} // end class TestContentObserver