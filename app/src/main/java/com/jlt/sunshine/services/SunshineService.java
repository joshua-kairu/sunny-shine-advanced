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

package com.jlt.sunshine.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 *
 * */
// begin class SunshineService
public class SunshineService extends IntentService {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /** The logger. */
    private static final String LOG_TAG = SunshineService.class.getSimpleName();

    /** Constant to put the location parameter in the intent. */
    public static final String EXTRA_LOCATION = "EXTRA_LOCATION";

    /* VARIABLES */

    /* CONSTRUCTOR */

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SunshineService() {
        super( "SunshineService" );
    }

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    // begin onHandleIntent
    protected void onHandleIntent( Intent intent ) {



    } // end onHandleIntent
    
    /* Other Methods */





    /* INNER CLASSES */

} // end class SunshineService