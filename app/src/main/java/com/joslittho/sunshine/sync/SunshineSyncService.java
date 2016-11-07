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

package com.joslittho.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * {@link Service} to provide framework access to the {@link SunshineSyncAdapter}.
 * */
// begin class SunshineSyncService
public class SunshineSyncService extends Service {

    /* CONSTANTS */
    
    /* Integers */

    /* Objects */

    /** Object to use as a thread-safe lock. */
    private static final Object sSyncAdapterLock = new Object();

    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = SunshineSyncService.class.getSimpleName();

    /* Sunshine Sync Adapters */

    /** An instance of the {@link SunshineSyncAdapter}. */
    private static SunshineSyncAdapter sSunshineSyncAdapter = null;
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onCreate
    public void onCreate() {

        Log.d( LOG_TAG, "onCreate." );

        // 0. get a lock on the sync adapter
        // 1. if no sync adapter exists
        // 1a. create a new sync adapter as a singleton

        // 0. get a lock on the sync adapter

        // begin synchronized on the adapter lock
        synchronized ( sSyncAdapterLock ) {

            // 1. if no sync adapter exists

            // 1a. create a new sync adapter as a singleton

            if ( sSunshineSyncAdapter == null ) {
                sSunshineSyncAdapter = new SunshineSyncAdapter( getApplicationContext(), true );
            }

        } // end synchronized on the adapter lock

    } // end onCreate

    /**
     * Returns an object that allows the system to invoke the sync adapter.
     *
     * The returned object will allow external processes to call onPerformSync().
     * The object is created in the base class code when
     * the {@link SunshineSyncAdapter}'s constructor calls super.
     * */
    @Nullable
    @Override
    // begin onBind
    public IBinder onBind( Intent intent ) {

        // 0. return the IBinder of the sunshine sync adapter service

        // 0. return the IBinder of the sunshine sync adapter service

        // getSyncAdapterBinder - returns a reference to the IBinder of the SyncAdapter service.
        return sSunshineSyncAdapter.getSyncAdapterBinder();

    } // end onBind

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class SunshineSyncService