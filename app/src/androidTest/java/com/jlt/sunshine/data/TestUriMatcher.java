package com.jlt.sunshine.data;

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

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import static com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;

/**
 * Tester for the Content Provider's URI matching
 * */
// begin class TestUriMatcher
public class TestUriMatcher extends AndroidTestCase {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    public static final String LOCATION_QUERY = "London, UK";
    public static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    public static final long TEST_LOCATION_ID = 10L;

    /* Uri */

    // "content://com.jlt.sunshine.app/weather"
    private static final Uri TEST_WEATHER_DIR = WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR =
            WeatherEntry.buildWeatherForLocationUri( LOCATION_QUERY );
    private static final Uri TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR =
            WeatherEntry.buildWeatherForLocationWithSpecificDateUri( LOCATION_QUERY, TEST_DATE );

    // "content://com.jlt.sunshine.app/location"
    private static final Uri TEST_LOCATION_DIR = LocationEntry.CONTENT_URI;

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    /* Other Methods */

    /**
     * Tester for if the Uri Matcher we have created matches the constants correctly.
     * */
    // begin method testUriMatcher
    public void testUriMatcher() {

        // 0. get the created Uri matcher
        // 1. test the match for the weather
        // 2. test the match for the weather with location
        // 3. test the match for the weather with location and date
        // 4. test the match for the location

        // 0. get the created Uri matcher

        UriMatcher uriMatcher = WeatherProvider.buildUriMatcher();

        // 1. test the match for the weather

        assertEquals( "Error: WEATHER was not matched." ,
                WeatherProvider.WEATHER,
                uriMatcher.match( TEST_WEATHER_DIR ) );

        // 2. test the match for the weather with location

        assertEquals( "Error: WEATHER WITH LOCATION was not matched." ,
                WeatherProvider.WEATHER_WITH_LOCATION,
                uriMatcher.match( TEST_WEATHER_WITH_LOCATION_DIR ) );

        // 3. test the match for the weather with location and date

        assertEquals( "Error: WEATHER WITH LOCATION AND DATE was not matched." ,
                WeatherProvider.WEATHER_WITH_LOCATION_AND_DATE,
                uriMatcher.match( TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR ) );

        // 4. test the match for the location

        assertEquals( "Error: LOCATION was not matched." ,
                WeatherProvider.LOCATION,
                uriMatcher.match( TEST_LOCATION_DIR ) );

    } // end method testUriMatcher

    /* INNER CLASSES */

} // end class TestUriMatcher