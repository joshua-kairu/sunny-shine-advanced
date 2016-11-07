package com.joslitto.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.joslitto.sunshine.data.contract.WeatherContract;

import java.util.HashSet;

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

/* Class to test the Sunshine DB */

// begin class TestDb
public class TestDb extends AndroidTestCase {

    /** CONSTANTS */

    public static final String LOG_TAG = TestDb.class.getSimpleName(); // the logger

    /** VARIABLES */

    /** CONSTRUCTOR */

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // begin setUp
    // called before each test
    protected void setUp() throws Exception {

        // 0. delete the db

        // 0. delete the db

        deleteTheDatabase();

    } // end setUp

    /** Other Methods */

    // begin method testCreateDb
    // tests if the db is created properly
    public void testCreateDb() throws Throwable {

        // 0. build a hash set of all the table names we wish to search for
        // 1. delete any weather db instances
        // 2. get a new weather db that we can write to
        // 2a. confirm that the db is opened
        // 3. confirm that we have created the tables we want
        // 4. verify that the tables have been created
        // 4a. if this fails then the db does not have all the tables we want
        // 5. verify that the tables have the correct columns
        // 5a. for the location entry table
        // 5a1. get the column names in the db
        // 5a1a. if this fails then we cannot access the db for the names
        // 5a2. build a hash set of all the column names we are looking for
        // 5a3. confirm that we have the columns that we need in the table
        // 5a3a. if this fails we have a columnar issue
        // last. close the db

        // 0. build a hash set of all the table names we wish to search for

        final HashSet< String > tableNameHashSet = new HashSet< String >();

        tableNameHashSet.add( WeatherContract.WeatherEntry.TABLE_NAME );
        tableNameHashSet.add( WeatherContract.LocationEntry.TABLE_NAME );

        // 1. delete any weather db instances

        mContext.deleteDatabase( WeatherDbHelper.DATABASE_NAME );

        // 2. get a new weather db that we can write to

        SQLiteDatabase database = new WeatherDbHelper( mContext ).getWritableDatabase();

        // 2a. confirm that the db is opened

        // assertEquals - asserts that the two booleans are equal
        assertEquals( true,database.isOpen() );

        // 3. confirm that we have created the tables we want

        Cursor cursor = database.rawQuery( "SELECT name FROM sqlite_master WHERE type='table'",
                null );

        // 4. verify that the tables have been created

        assertTrue( "Error: This means that the database has not been created correctly.", cursor.moveToFirst() );

        // if the current cursor position has the same name as what
        // is in the table hash set then we can remove the name
        // from the table hash set
        // an empty table hash set means we have the names we need in the db

        // we do this using a do while

        // begin do while to verify table names
        do {

            tableNameHashSet.remove( cursor.getString( 0 ) );

            // moveToNext - moves the cursor to the next row. Returns false if we are at the last row
        } while ( cursor.moveToNext() == true ); // end do while to verify table names

        // 4a. if this fails then the db does not have all the tables we want

        // moveToFirst - moves the cursor to the first row, returns false if the cursor is empty
        // assertTrue - asserts that a condition is true; if it isn't it throws an AssertionError
        // with the given message
        assertTrue( "Error: This means the database has not been created correctly with the tables we need",
                cursor.moveToFirst() );

        // 5. verify that the tables have the correct columns

        // 5a. for the location entry table

        // 5a1. get the column names in the db

        cursor = database.rawQuery( "PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")", null );

        // 5a1a. if this fails then we cannot access the db for the names

        assertTrue( "Error: This means we are unable to query the database for location table information",
                cursor.moveToFirst() );

        // 5a2. build a hash set of all the column names we are looking for

        final HashSet< String > locationColumnHashSet = new HashSet< String >();

        locationColumnHashSet.add( WeatherContract.LocationEntry._ID );
        locationColumnHashSet.add( WeatherContract.LocationEntry.COLUMN_CITY_NAME );
        locationColumnHashSet.add( WeatherContract.LocationEntry.COLUMN_COORD_LATITUDE );
        locationColumnHashSet.add( WeatherContract.LocationEntry.COLUMN_COORD_LONGITUDE );
        locationColumnHashSet.add( WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING );

        // 5a3. confirm that we have the columns that we need in the table

        // getColumnIndex - returns the zero-based index for the given column name,
        // or -1 if the column doesn't exist.
        int columnNameIndex = cursor.getColumnIndex( "name" );

        // use the do while idea from above to confirm the existence of the columns in the table

        // begin do while to verify columns in the location table
        do {

            String columnName = cursor.getString( columnNameIndex );

            locationColumnHashSet.remove( columnName );

        } while ( cursor.moveToNext() == true ); // end do while to verify columns in the location table

        // 5a3a. if this fails we have a columnar issue

        assertTrue( "Error: The database doesn't contain all of the needed location entry columns",
                locationColumnHashSet.isEmpty() );

        // last. close the db

        cursor.close();
        database.close();

    } // end method testCreateDb

    /* Delete the database so that we start on a new page each test */

    // begin method deleteTheDatabase
    void deleteTheDatabase() { mContext.deleteDatabase( WeatherDbHelper.DATABASE_NAME ); }

    // begin method testLocationTable
    public long testLocationTable() {

        // 0. get reference to the writable db
        // 1. create ContentValues of what is to be inserted
        // 2. insert the ContentValues into db and get a row ID back
        // 3. query the db and get a cursor back
        // 4. move the cursor to a valid db row
        // 5. validate data in resulting cursor with the original ContentValues
        // 5a. check that the db has only one record
        // 6. close up shop
        // 7. return the row id of the location

        // 0. get reference to the writable db

        SQLiteDatabase sqLiteDatabase = new WeatherDbHelper( mContext ).getWritableDatabase();

        // 1. create ContentValues of what is to be inserted

        ContentValues northPoleLocationContentValues = TestUtilities.createNorthPoleLocationValues();

        // 2. insert the ContentValues into db and get a row ID back

        long locationRowId = sqLiteDatabase.insert( WeatherContract.LocationEntry.TABLE_NAME, null, northPoleLocationContentValues );

        assertTrue( "Error: Insert did not work", locationRowId != -1 );

        // 3. query the db and get a cursor back

        // SELECT * FROM location;
        Cursor cursor = sqLiteDatabase.query( WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null );

        // 4. move the cursor to a valid db row

        assertTrue( "Error: This means the query has not returned anything",
                cursor.moveToFirst() );

        // 5. validate data in resulting cursor with the original ContentValues

//        int indexLocationSetting = cursor.getColumnIndex( LocationEntry.COLUMN_LOCATION_SETTING );
//        int indexCityName = cursor.getColumnIndex( LocationEntry.COLUMN_CITY_NAME );
//        int indexCoordLatitude = cursor.getColumnIndex( LocationEntry.COLUMN_COORD_LATITUDE );
//        int indexCoordLongitude = cursor.getColumnIndex( LocationEntry.COLUMN_COORD_LONGITUDE );
//
//        assertEquals( "The location setting was " +
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_LOCATION_SETTING ) +
//                " but what is in the database is " + cursor.getString( indexLocationSetting ),
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_LOCATION_SETTING ),
//                cursor.getString( indexLocationSetting ) );
//
//        assertEquals( "The city name was " +
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_CITY_NAME ) +
//                " but what is in the database is " + cursor.getString( indexCityName ),
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_CITY_NAME ),
//                cursor.getString( indexCityName ) );
//
//        assertEquals( "The latitude coordinate was " +
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_COORD_LATITUDE ) +
//                " but what is in the database is " + cursor.getDouble( indexCoordLatitude ),
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_COORD_LATITUDE ),
//                cursor.getDouble( indexCoordLatitude ) );
//
//        assertEquals( "The longitude coordinate was " +
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_COORD_LONGITUDE ) +
//                " but what is in the database is " + cursor.getDouble( indexCoordLongitude ),
//                northPoleLocationContentValues.get( LocationEntry.COLUMN_COORD_LONGITUDE ),
//                cursor.getDouble( indexCoordLongitude ) );

        TestUtilities.validateCurrentRecord( "Error: Location query validation failed.", cursor, northPoleLocationContentValues );

        // 5a. check that the db has only one record

        assertFalse( "Error: More than one record returned from location query.", cursor.moveToNext() );

        // 6. close up shop

        cursor.close();

        sqLiteDatabase.close();

        // 7. return the row id of the location

        return locationRowId;

    } // end method testLocationTable

    // begin method testWeatherTable
    public void testWeatherTable() {

        // 0. insert the location
        // 0a. get the location row ID
        // 0b. use the location row ID to insert the weather
        // 0c. cover as many bases as possible
        // 1. get reference to the writable db
        // 2. create ContentValues of what is to be inserted
        // 3. insert the ContentValues into db and get a row ID back
        // 4. query the db and get a cursor back
        // 5. move the cursor to a valid db row
        // 6. validate data in resulting cursor with the original ContentValues
        // 6a. check that the db has only one record
        // 7. close up shop

        // 0. insert the location

        // 0a. get the location row ID

        // 0b. use the location row ID to insert the weather

        // 0c. cover as many bases as possible

        long locationRowId = insertLocation();

        assertTrue( "Error inserting a location into the database", locationRowId != -1 );

        // 1. get reference to the writable db

        SQLiteDatabase sqLiteDatabase = new WeatherDbHelper( mContext ).getWritableDatabase();

        // 2. create ContentValues of what is to be inserted

        ContentValues northPoleWeatherValues = TestUtilities.createWeatherValues( locationRowId );

        // 3. insert the ContentValues into db and get a row ID back

        long weatherRowId = sqLiteDatabase.insert( WeatherContract.WeatherEntry.TABLE_NAME, null, northPoleWeatherValues );

        assertTrue( "Error: Could not insert weather data into weather table", weatherRowId != -1 );

        // 4. query the db and get a cursor back

        // SELECT * FROM weather;
        Cursor cursor = sqLiteDatabase.query( WeatherContract.WeatherEntry.TABLE_NAME, null, null, null,null, null, null );

        // 5. move the cursor to a valid db row

        assertTrue( "Error: No results came from querying the weather table.", cursor.moveToFirst() );

        // 6. validate data in resulting cursor with the original ContentValues

        TestUtilities.validateCurrentRecord( "Error: Weather Query did not match expected values", cursor, northPoleWeatherValues );

        // 6a. check that the db has only one record

        assertFalse( "Error: Weather table should only have one entry.", cursor.moveToNext() );

        // 7. close up shop

        cursor.close();

        sqLiteDatabase.close();

    } // end method testWeatherTable

    /*
    * Gets a row ID of a location entry so that it can be put in the weather table
    * */
    // begin method insertLocation
    public long insertLocation() {

        // 0. get reference to the writable db
        // 1. create ContentValues of what is to be inserted
        // 2. insert the ContentValues into db and get a row ID back
        // 3. query the db and get a cursor back
        // 4. close up shop
        // 5. return the row id of the location

        // 0. get reference to the writable db

        SQLiteDatabase sqLiteDatabase = new WeatherDbHelper( mContext ).getWritableDatabase();

        // 1. create ContentValues of what is to be inserted

        ContentValues northPoleLocationContentValues = TestUtilities.createNorthPoleLocationValues();

        // 2. insert the ContentValues into db and get a row ID back

        long locationRowId = sqLiteDatabase.insert( WeatherContract.LocationEntry.TABLE_NAME, null, northPoleLocationContentValues );

        // 3. query the db and get a cursor back

        // SELECT * FROM location;
        Cursor cursor = sqLiteDatabase.query( WeatherContract.LocationEntry.TABLE_NAME, null, null, null, null, null, null );

        // 4. close up shop

        cursor.close();

        sqLiteDatabase.close();

        // 5. return the row id of the location

        return locationRowId;


    }

    /** INNER CLASSES */

} // end class TestDb