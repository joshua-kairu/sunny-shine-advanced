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

package com.jlt.sunshine;

import android.content.Context;
import android.os.AsyncTask;

/** AsyncTask to get the weather from the net */
// begin class FetchWeatherTask
public class FetchWeatherTask extends AsyncTask< String, Void, Void > {

    /* CONSTANTS */

    /* Strings */

    public final String LOG_TAG = FetchWeatherTask.class.getSimpleName(); // the log tag

    /* VARIABLES */

    /* Contexts */

    private Context mHostContext; // the host context

    /* Primitives */

    private boolean DEBUG = true;

    /* CONSTRUCTOR */

    // begin constructor
    public FetchWeatherTask( Context hostContext ) {

        this.mHostContext = hostContext;

    } // end constructor

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // begin doInBackground
    protected Void doInBackground( String... params ) {

        return null;

    } // end doInBackground

    /** Other Methods */




} // end class FetchWeatherTask
