package com.joslittho.sunshine;

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

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import static com.joslittho.sunshine.data.contract.WeatherContract.LocationEntry;

/**
 * Tester for the AsyncTask fetching weather data for us
 * */
// begin class TestFetchWeatherTask
public class TestFetchWeatherTask extends AndroidTestCase {

    /* CONSTANTS */

    /* Doubles */

    static final double ADD_LOCATION_LATITUDE = 34.425833;
    static final double ADD_LOCATION_LONGITUDE = -119.714167;

    /* Integers */

    /* Strings */

    public static final String ADD_LOCATION_SETTING = "Sunnydale, CA";
    public static final String ADD_LOCATION_CITY = "Sunnydale";

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */

    /**
     * Test for whether the addLocation function of the FetchWeatherTask works.
     * This tests works for devices running API 11 and above.
     * */
    @TargetApi( 11 )
    // begin method testAddLocation
    public void testAddLocation() {

        // 0. start from a clean slate
        // 1. get a FetchWeatherTask and add a test location to it
        // 2. verify if addLocation returns a valid record ID
        // 3. test all this twice
        // 3a. does our ID point to our location? query to find out and get a cursor
        // 3b. if the cursor has an element
        // 3b1. does the cursor's location id match what we got in 2?
        // 3b2. does the cursor's location setting match what we added in 1?
        // 3b3. does the cursor's location city name match what we added in 1?
        // 3b4. does the cursor's location latitude match what we added in 1?
        // 3b5. does the cursor's location longitude match what we added in 1?
        // 3c. if it does not then there is a problem
        // 3d. there should only be one record
        // 3e. add the location again
        // 4. reset state to normal by removing the location added in 1 from the db
        // 5. clean up the tests so that other tests can use the provider

        // 0. start from a clean slate

        getContext().getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[] { ADD_LOCATION_SETTING } );

        // 1. get a FetchWeatherTask and add a test location to it

        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask( getContext() );

        long locationRowId = fetchWeatherTask.addLocation(
                ADD_LOCATION_SETTING,
                ADD_LOCATION_CITY,
                ADD_LOCATION_LATITUDE,
                ADD_LOCATION_LONGITUDE
                );

        // 2. verify if addLocation returns a valid record ID

        assertTrue( "Error: The location was not added properly into the db.", locationRowId != -1 );

        // 3. test all this twice

        // begin for to test twice
        for ( int i = 0; i < 2; i++ ) {

            // 3a. does our ID point to our location? Query to find out and get a cursor

            Cursor cursor = mContext.getContentResolver().query(
                    LocationEntry.CONTENT_URI,
                    new String[] {
                            LocationEntry._ID,
                            LocationEntry.COLUMN_LOCATION_SETTING,
                            LocationEntry.COLUMN_CITY_NAME,
                            LocationEntry.COLUMN_COORD_LATITUDE,
                            LocationEntry.COLUMN_COORD_LONGITUDE
                    },
                    LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{ ADD_LOCATION_SETTING },
                    null
                    );

            // 3b. if the cursor has an element

            // begin if the cursor has something
            if ( cursor.moveToFirst() == true ) {

                int locationIndex = cursor.getColumnIndex( LocationEntry._ID );
                int locationSettingIndex = cursor.getColumnIndex( LocationEntry.COLUMN_LOCATION_SETTING );
                int cityNameIndex = cursor.getColumnIndex( LocationEntry.COLUMN_CITY_NAME );
                int latitudeIndex = cursor.getColumnIndex( LocationEntry.COLUMN_COORD_LATITUDE );
                int longitudeIndex = cursor.getColumnIndex( LocationEntry.COLUMN_COORD_LONGITUDE );

                // 3b1. does the cursor's location id match what we got in 2?

                assertEquals(
                        "Error: The expected location row ID was " + locationRowId +
                                " and the actual location row ID was " +
                                cursor.getLong( locationIndex ),
                        locationRowId, cursor.getLong( locationIndex ) );

                // 3b2. does the cursor's location setting match what we added in 1?

                assertEquals(
                        "Error: The expected location setting was " + ADD_LOCATION_SETTING +
                                " and the actual location setting was " +
                                cursor.getString( locationSettingIndex ),
                        ADD_LOCATION_SETTING, cursor.getString( locationSettingIndex ) );

                // 3b3. does the cursor's location city name match what we added in 1?

                assertEquals(
                        "Error: The expected location city name was " + ADD_LOCATION_CITY +
                                " and the actual location city name was " +
                                cursor.getString( cityNameIndex ),
                        ADD_LOCATION_CITY, cursor.getString( cityNameIndex ) );

                // 3b4. does the cursor's location latitude match what we added in 1?

                assertEquals(
                        "Error: The expected location latitude was " + ADD_LOCATION_LATITUDE +
                                " and the actual location latitude was " +
                                cursor.getDouble( latitudeIndex ),
                        ADD_LOCATION_LATITUDE, cursor.getDouble( latitudeIndex ) );

                // 3b5. does the cursor's location longitude match what we added in 1?

                assertEquals(
                        "Error: The expected location longitude was " + ADD_LOCATION_LONGITUDE +
                                " and the actual location longitude was " +
                                cursor.getDouble( longitudeIndex ),
                        ADD_LOCATION_LONGITUDE, cursor.getDouble( longitudeIndex ) );

            } // end if the cursor has something

            // 3c. if it does not then there is a problem

            // fail - Fails a test with the given message.
            else { fail( "Error: The cursor needs to have a record." ); }

            // 3d. there should only be one record

            assertTrue( "Error: The cursor should have only one record.", cursor.moveToNext() == false );

            // 3e. add the location again

            long newLocationRowId = fetchWeatherTask.addLocation(
                    ADD_LOCATION_SETTING,
                    ADD_LOCATION_CITY,
                    ADD_LOCATION_LATITUDE,
                    ADD_LOCATION_LONGITUDE );

            // 3f. confirm that adding the same location should return the same row ID

            assertEquals( "Error: Inserting a location again should return the same row ID",
                    locationRowId, newLocationRowId );

            cursor.close();

        } // end for to test twice

        // 4. reset state to normal by removing the location added in 1 from the db

        mContext.getContentResolver().delete( LocationEntry.CONTENT_URI,
                LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ ADD_LOCATION_SETTING } );

        // 5. clean up the tests so that other tests can use the provider

        mContext.getContentResolver()

                // acquireContentProviderClient -
                //  gets a ContentProviderClient that is associated with the ContentProvider
                //  that services the content at the given uri, starting the provider if necessary
                .acquireContentProviderClient( LocationEntry.CONTENT_URI )

                // getLocalContentProvider -
                //  Gets a reference to the ContentProvider that is associated with this client.
                .getLocalContentProvider()

                // shutdown - Shuts down the ContentProvider instance.
                //  You can then invoke this method in unit tests.
                .shutdown();

    } // end method testAddLocation

    /* INNER CLASSES */

} // end class TestFetchWeatherTask