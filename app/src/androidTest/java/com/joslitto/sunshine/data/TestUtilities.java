package com.joslitto.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.joslitto.sunshine.data.contract.WeatherContract;

import java.util.Map;
import java.util.Set;

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

/**
* Functions and some test data to assist in testing the database and the Content Providers
* */
// begin class TestUtilities
public class TestUtilities extends AndroidTestCase {

    /* CONSTANTS */

    /* Longs */

    public static final long TEST_DATE = 1468673628L; // Saturday, July 16, 2016

    /* Integers */

    /* Strings */

    public static final String TEST_LOCATION = "99705"; // sample test location

    /* VARIABLES */

    /* CONSTRUCTOR */

    /* METHODS */

    /* Getters and Setters */

    /* Overrides */

    /* Other Methods */

    /**
    * Validates the cursor passed to it with the passed content values
    * */
    // begin method validateCursor
    // ContentValues - Class used to store a set of values that the ContentResolver can process
    public static void validateCursor( String error, Cursor valueCursor, ContentValues expectedContentValues ) {

        // 0. verify the cursor contains something
        // 1. validate the current record
        // 2. close the cursor

        // 0. verify the cursor contains something

        assertTrue( "Empty cursor returned. " + error, valueCursor.moveToFirst() );

        // 1. validate the current record

        validateCurrentRecord( error, valueCursor, expectedContentValues );

        // 2. close the cursor

        valueCursor.close();

    } // end method validateCursor

    /* Validates cursor results against the expected ContentValues */
    // begin method validateCurrentRecord
    public static void validateCurrentRecord( String error, Cursor valueCursor, ContentValues expectedContentValues ) {

        // 0. get the expected content values' set of values
        // 1. for each key-value pair in the set
        // 1a. get the key from the set
        // 1b. get the index of the column having the same name as the key in the set
        // 1c. verify that the cursor has a column having the same name as the key in the set
        // 1d. get the value from the set
        // 1e. verify that the cursor has the same value in the 1b. index as the set has at the same index

        // 0. get the expected content values' set of values

        Set< Map.Entry< String, Object > > expectedValuesSet = expectedContentValues.valueSet();

        // 1. for each key-value pair in the set

        // begin for through each entry in the expected values set
        for ( Map.Entry< String, Object > entry : expectedValuesSet ) {

            // 1a. get the key from the set

            String columnName = entry.getKey(); // the key of the entry should be the same as the column name

            // 1b. get the index of the column having the same name as the key in the set

            // getColumnIndex - returns the zero-based index for the given column name, or -1 if the column doesn't exist.
            int cursorColumnIndex = valueCursor.getColumnIndex( columnName );

            // 1c. verify that the cursor has a column having the same name as the key in the set

            assertFalse( "Error: Column '" + cursorColumnIndex + "' not found. " + error, cursorColumnIndex == -1 );

            // 1d. get the value from the set

            String expectedValue = entry.getValue().toString();

            // 1e. verify that the cursor has the same value in the 1b. index as the set has at the same index

            // getString - Returns the value of the requested column as a String
            assertEquals( "Error: Value '" + valueCursor.getString( cursorColumnIndex ) +
                    " did not match the expected value" + expectedValue + ". " + error ,
                    expectedValue, valueCursor.getString( cursorColumnIndex ) );

        } // end for through each entry in the expected values set

    } // end method validateCurrentRecord

    /* Creates default weather values for testing the weather table */
    // begin method createWeatherValues
    public static ContentValues createWeatherValues( long locationRowId ) {
        
        ContentValues weatherValues = new ContentValues();
        
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_LOCATION_KEY, locationRowId );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_DATE, TEST_DATE );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES, 1.1 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_SHORT_DESCRIPTION, "Asteroids" );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5 );
        weatherValues.put( WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321 );

        return weatherValues;

    } // end method createWeatherValues

    /* Creates a North Pole location */
    // begin method createNorthPoleLocationValues
    public static ContentValues createNorthPoleLocationValues() {

        // Create a new map of values, where column names are the keys

        ContentValues testValues = new ContentValues();

        testValues.put( WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION );
        testValues.put( WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole" );
        testValues.put( WeatherContract.LocationEntry.COLUMN_COORD_LATITUDE, 64.7488 );
        testValues.put( WeatherContract.LocationEntry.COLUMN_COORD_LONGITUDE, -147.353 );

        return testValues;

    } // end method createNorthPoleLocationValues

    /*
    Inserts the North Pole location into the Weather DB
    then returns the row ID of the Location table row having the North Pole location
    * */
    // begin method insertNorthPoleLocationValues
    public static long insertNorthPoleLocationValues( Context context ) {

        // 0. get the weather db in writable form
        // 1. insert the north pole values into the location db and get the row id of the insertion
        // 2. confirm that we actually got a row back
        // 3. return the gotten row ID

        // 0. get the weather db in writable form

        WeatherDbHelper weatherDbHelper = new WeatherDbHelper( context );
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();

        // 1. insert the north pole values into the location db and get the row id of the insertion

        ContentValues northPoleValues = TestUtilities.createNorthPoleLocationValues();

        // insert - inserts values into the db and returns the row ID of the newly inserted row,
        //          or -1 if an error occurred
        long northPoleLocationRowID = db.insert( WeatherContract.LocationEntry.TABLE_NAME, null, northPoleValues );

        // 2. confirm that we actually got a row back

        assertTrue( "Error: Failure inserting the North Pole Location Values", northPoleLocationRowID != -1 );

        // 3. return the gotten row ID

        return northPoleLocationRowID;

    } // end method insertNorthPoleLocationValues

    /** INNER CLASSES */

} // end class TestUtilities