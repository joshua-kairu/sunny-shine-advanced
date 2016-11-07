package com.joslittho.sunshine.data;

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

import android.net.Uri;
import android.test.AndroidTestCase;

import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry;

/**
 * Tester for the weather contract
 * */
// begin class TestWeatherContract
public class TestWeatherContract extends AndroidTestCase {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /** Intentionally include a slash to ensure correct Uri matching */
    public static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */

    /**
     * Tester for the correct form of the Uri to query the weather with location
     * The Uri should look like this:
     *  content://com.jlt.sunshine.app/weather/[locationSetting]
     * */
    // begin method testBuildWeatherLocationUri
    public void testBuildWeatherLocationUri() {

        // 0. build a Uri with the test weather location
        // 1. confirm the build uri exists
        // 2. confirm the test weather location was appended to the uri properly
        // 3. confirm that the built uri
        // is the same as content://com.jlt.sunshine.app/weather/%2FNorth%20Pole

        // 0. build a Uri with the test weather location

        Uri locationUri = WeatherEntry.buildWeatherForLocationUri( TEST_WEATHER_LOCATION );

        // 1. confirm the build uri exists

        assertNotNull( "Error: The built location Uri does not exist.", locationUri );

        // 2. confirm the test weather location was appended to the uri properly

        // getLastPathSegment - Gets the decoded last segment in the path.
        assertEquals(
                "Error: The built location Uri does not have the location query appended properly.",
                TEST_WEATHER_LOCATION, locationUri.getLastPathSegment() );

        // 3. confirm that the built uri
        // is the same as content://com.jlt.sunshine.app/weather/%2FNorth%20Pole

        assertEquals(
                "Error: The built location Uri is not content://com.jlt.sunshine.app/weather/%2FNorth%20Pole",
                locationUri.toString(),
                "content://com.jlt.sunshine.app/weather/%2FNorth%20Pole" );

    } // end method testBuildWeatherLocationUri


    /* INNER CLASSES */

} // end class TestWeatherContract