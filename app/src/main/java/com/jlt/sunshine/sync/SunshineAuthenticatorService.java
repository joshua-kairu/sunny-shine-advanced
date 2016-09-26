/*
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

package com.jlt.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * The {@link android.app.Service} that allows
 * the sync adapter framework to access the authenticator.
 *
 * Copied with love from
 * https://developer.android.com/training/sync-adapters/creating-authenticator.html
 * */
// begin class SunshineAuthenticatorService
public class SunshineAuthenticatorService extends Service {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */
        
    /* VARIABLES */

    /* Sunshine Authenticators */

    private SunshineAuthenticator mSunshineAuthenticator; // ditto

    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onCreate
    public void onCreate() {

        // 0. create a new authenticator object

        // 0. create a new authenticator object

        mSunshineAuthenticator = new SunshineAuthenticator( this );

    } // end onCreate

    /**
     * Returns the communication channel to the service.
     *
     * When the system binds to this {@link Service} to make the RPC call,
     * return the authenticator's IBinder.
     * */
    @Nullable
    @Override
    // begin onBind
    // IBinder - Base interface for a remotable object, the core part of a lightweight
    //  remote procedure call mechanism designed for high performance
    //  when performing in-process and cross-process calls.
    public IBinder onBind( Intent intent ) {

        // 0. return the authenticator's IBinder

        // 0. return the authenticator's IBinder

        return mSunshineAuthenticator.getIBinder();

    } // end onBind

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class SunshineAuthenticatorService