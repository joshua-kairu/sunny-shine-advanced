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


package com.jlt.sunshine.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;

import com.jlt.sunshine.R;

/**
 * A custom {@link android.preference.EditTextPreference} to enforce a
 * 3 character minimum length for preferred location.
 * */
// begin class LocationEditTextPreference
public class LocationEditTextPreference extends EditTextPreference {

    /* CONSTANTS */
    
    /* Integers */

    /** The default minimum length of the location preference string. */
    private static final int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = LocationEditTextPreference.class.getSimpleName();
        
    /* VARIABLES */

    /* Primitives */

    private int mMinLength; // ditto

    /* CONSTRUCTOR */

    // begin constructor
    public LocationEditTextPreference( Context context, AttributeSet attrs ) {

        // 0. super stuff
        // 1. get the minimum length from XML
        // 2. recycle the XML

        // 0. super stuff

        super( context, attrs );

        // 1. get the minimum length from XML

        TypedArray a = context.getTheme().obtainStyledAttributes( attrs,
                R.styleable.LocationEditTextPreference, 0, 0 );

        // begin trying to get things from XML
        try {

            mMinLength = a.getInt( R.styleable.LocationEditTextPreference_minLength,
                    DEFAULT_MINIMUM_LOCATION_LENGTH );

            Log.e( LOG_TAG, "LocationEditTextPreference: minLength = " + mMinLength );

        } // end trying to get things from XML

        // 2. recycle the XML

        finally {
            a.recycle();
        }

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */
    
    /* INNER CLASSES */

} // end class LocationEditTextPreference