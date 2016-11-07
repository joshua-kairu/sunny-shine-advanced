package com.joslitto.sunshine.data;

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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.joslitto.sunshine.data.contract.WeatherContract;

import static com.joslitto.sunshine.data.contract.WeatherContract.LocationEntry;
import static com.joslitto.sunshine.data.contract.WeatherContract.WeatherEntry;

/**
 * Tester for the Sunshine Content Provider
 * */
// begin class TestContentProvider
public class TestContentProvider extends AndroidTestCase {

    /* CONSTANTS */
    
    /* Integers */

    public static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    /* Strings */

    public static final String LOG_TAG = TestContentProvider.class.getSimpleName(); // ditto

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // We want to start on a clean slate every test
    // so we delete everything in the db when setting up.
    // begin setUp
    protected void setUp() throws Exception {

        // 0. super things
        // 1. delete all records

        // 0. super things

        super.setUp();

        // 1. delete all records

        deleteAllRecords();

    } // end setUp

    /* Other Methods */

    /**
     * Test to check if the weather provider has been correctly registered.
     *
     * */
    // begin method testProviderRegistry
    public void testProviderRegistry() {

        // 0. get the package manager
        // 1. define the component name based on the package name from the context
        // and the WeatherProvider class
        // 2. fetch the provider info using the component name gotten in step 1
        // 3. ensure the registered authority
        // matches the authority found in the weather contract
        // last. report any issues

        // 0. get the package manager

        PackageManager packageManager = mContext.getPackageManager();

        // 1. define the component name based on the package name from the context
        // and the WeatherProvider class

        // ComponentName - Identifier for a specific application component
        //      (Activity, Service, BroadcastReceiver, or ContentProvider) that is available
        // getPackageName - Returns the name of this application's package.
        //
        // The name of the provider as defined in the manifest should match
        // exactly what we pass as the class name we pass here
        ComponentName componentName = new ComponentName( mContext.getPackageName(),
                                                         WeatherProvider.class.getName() );

        // 2. fetch the provider info using the component name gotten in step 1

        // begin trying to get the provider info
        try {

            ProviderInfo providerInfo = packageManager.getProviderInfo( componentName, 0 );

            // 3. ensure the registered authority
            // matches the authority found in the weather contract

            assertTrue(
                    "Error: The registered provider has the authority " +
                            providerInfo.authority +
                            " while the contract has the authority " +
                            WeatherContract.CONTENT_AUTHORITY +
                            ". They should be the same.",
                    providerInfo.authority.equals( WeatherContract.CONTENT_AUTHORITY ) );

        } // end trying to get the provider info

        // last. report any issues

        // begin catching no name issues
        catch ( PackageManager.NameNotFoundException e ) {

            assertTrue( "Error: " + e.toString(), false );

            e.printStackTrace();

        } // end catching no name issues

    } // end method testProviderRegistry

    /**
     * Test that the ContentProvider is returning the current type of each URI that it can handle.
     * */
    // begin method testGetType
    public void testGetType() {

        // 0. confirm that the weather URI is being returned right
        // 1. confirm that the weather with location URI is being returned right
        // 2. confirm that the weather with location and date URI is being returned right
        // 3. confirm that the location URI is being returned right

        // 0. confirm that the weather URI is being returned right

        // content://com.jlt.sunshine.app/weather

        // getType - Return the MIME type of the given content URL.
        String type = mContext.getContentResolver().getType( WeatherEntry.CONTENT_URI );

        // vnd.android.cursor.dir/com.jlt.sunshine.app/weather
        assertEquals( "Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE.",
                WeatherEntry.CONTENT_TYPE, type );

        // 1. confirm that the weather with location URI is being returned right

        String testLocation = "94074";

        // content://com.jlt.sunshine.app/weather/94074

        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherForLocationUri( testLocation ) );

        // vnd.android.cursor.dir/com.jlt.sunshine.app/weather/
        assertEquals( "Error: the WeatherEntry CONTENT_URI with location should return WeatherEntry.CONTENT_TYPE.",
                WeatherEntry.CONTENT_TYPE, type );

        // 2. confirm that the weather with location and date URI is being returned right

        long testDate =  1419120000L; // December 21st, 2014;

        // content://com.jlt.sunshine.app/weather/94074/1419120000

        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherForLocationWithSpecificDateUri( testLocation, testDate ) );

        // vnd.android.cursor.dir/com.jlt.sunshine.app/weather/1419120000L
        assertEquals( "Error: the WeatherEntry CONTENT_URI with location and date should return WeatherEntry.CONTENT_ITEM_TYPE.",
                WeatherEntry.CONTENT_ITEM_TYPE, type );

        // 3. confirm that the location URI is being returned right

        // content://com.jlt.sunshine.app/location

        type = mContext.getContentResolver().getType( LocationEntry.CONTENT_URI );

        // vnd.android.cursor.dir/com.jlt.sunshine.app/location
        assertEquals( "Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE.",
                LocationEntry.CONTENT_TYPE, type );

    } // end method testGetType

    /**
     * Test to confirm if a basic weather query works. It uses the db directly to insert data
     * and then uses the ContentProvider to read out the data.
     * */
    // begin method testBasicWeatherQuery
    public void testBasicWeatherQuery() {

        // 0. attempt to insert test records to the db
        // 0a. attempt to insert a location entry
        // 0b. attempt to insert a weather entry
        // 0c. close the db
        // 1. test the basic content provider query
        // 2. ensure we get the correct cursor out of the db

        // 0. attempt to insert test records to the db

        WeatherDbHelper dbHelper =new WeatherDbHelper( mContext );
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // 0a. attempt to insert a location entry

        ContentValues locationContentValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues( mContext );

        assertTrue( "Error: Could not insert location data into db.", locationRowId != -1 );

        // 0b. attempt to insert a weather entry

        ContentValues weatherContentValues = TestUtilities.createWeatherValues( locationRowId );
        long weatherRowId = database.insert( WeatherEntry.TABLE_NAME, null,
                weatherContentValues );

        assertTrue( "Error: Could not insert weather data into db.", weatherRowId != -1 );

        // 0c. close the db

        database.close();

        // 1. test the basic content provider query

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 2. ensure we get the correct cursor out of the db

        TestUtilities.validateCursor( "testBasicWeatherQuery", weatherCursor, weatherContentValues );

    } // end method testBasicWeatherQuery

    /**
     * Test to confirm if basic location queries work. It uses the db directly to insert data
     * and then uses the ContentProvider to read out the data.
     * */
    // begin method testBasicLocationQueries
    public void testBasicLocationQueries() {

        // 0. insert location records into db
        // 1. test the basic content provider query
        // 2. ensure the db has given us the correct cursor
        // 3. for API >= 19, ensure the notification Uri has been set correctly

        // 0. insert location records into db

        ContentValues locationContentValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId = TestUtilities.insertNorthPoleLocationValues( mContext );
        assertTrue( "Error: Could not insert location data into db.", locationRowId != -1 );

        // 1. test the basic content provider query

        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 2. ensure the db has given us the correct cursor

        TestUtilities.validateCursor( "testBasicLocationQueries, location query",
                locationCursor, locationContentValues );

        // 3. for API >= 19, ensure the notification Uri has been set correctly

        // begin if we're at least at KITKAT
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {

            // getNotificationUri - Returns the URI at which notifications of changes
            //      in this Cursor's data will be delivered,
            //      as previously set by setNotificationUri(ContentResolver, Uri).
            assertEquals( "Error: The notification Uri has not been set right.",
                    locationCursor.getNotificationUri(), LocationEntry.CONTENT_URI );

        } // end if we're at least at KITKAT

    } // end method testBasicLocationQueries

    /**
     * Tests the insert function of the ContentProvider
     * */
    // begin method testInsertReadProvider
    public void testInsertReadProvider() {

        // 0. create a test ContentValues for location
        // 1. register a content observer that we will use for the weather insert using the ContentResolver
        // 2. do the insert into the location table
        // 3. check if the content observer got called
        // 4. unregister the content observer
        // 5. get the row in which the insertion was done and verify it
        // 6. verify the data was inserted into the location table
        // 6a. query the location table and get a cursor
        // 6b. verify the cursor
        // 7. create a test ContentValues for weather
        // 8. register a content observer for use during the weather insert
        // 9. insert the weather data
        // 10. confirm successful weather insert
        // 11. check if the content observer got called
        // 12. unregister the content observer
        // 13. verify the data was inserted into the weather table
        // 13a. query the weather table and get  a cursor
        // 13b. verify the cursor
        // 14. confirm the join
        // 14a. add the location values in with the weather data
        // 14b. get the joined weather and location data in a cursor
        // 14c. verify the cursor
        // 14d. get the joined weather and location data with start date in a cursor
        // 14e. verify the cursor
        // 14f. get the joined weather and location data for a specific date in a cursor
        // 14g. verify the cursor

        // 0. create a test ContentValues for location

        ContentValues locationContentValues = TestUtilities.createNorthPoleLocationValues();

        // 1. register a content observer that we will use for the weather insert using the ContentResolver

        TestContentObserver testContentObserver = TestContentObserver.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(
                LocationEntry.CONTENT_URI, true, testContentObserver );

        // 2. do the insert into the location table

        Uri locationUri = mContext.getContentResolver().insert(
                LocationEntry.CONTENT_URI, locationContentValues );

        // 3. check if the content observer got called

        testContentObserver.waitForNotificationOrFail();

        // 4. unregister the content observer

        mContext.getContentResolver().unregisterContentObserver( testContentObserver );

        // 5. get the row in which the insertion was done and verify it

        long locationRowId = ContentUris.parseId( locationUri );

        assertTrue( "Error: ContentResolver could not insert the location data into the db.",
                locationRowId != -1 );

        // 6. verify the data was inserted into the location table

        // 6a. query the location table and get a cursor

        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 6b. verify the cursor

        TestUtilities.validateCursor( "testInsertReadProvider, locationCursor.",
                locationCursor, locationContentValues );

        // 7. create a test ContentValues for weather

        ContentValues weatherContentValues = TestUtilities.createWeatherValues( locationRowId );

        // 8. register a content observer for use during the weather insert

        testContentObserver = TestContentObserver.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(
                WeatherEntry.CONTENT_URI, true, testContentObserver );

        // 9. insert the weather data

        Uri weatherUri = mContext.getContentResolver().insert(
                WeatherEntry.CONTENT_URI, weatherContentValues );

        // 10. confirm successful weather insert

        assertTrue( "Error: ContentResolver could not insert the weather data into the db.",
                weatherUri != null );

        // 11. check if the content observer got called

        testContentObserver.waitForNotificationOrFail();

        // 12. unregister the content observer

        mContext.getContentResolver().unregisterContentObserver( testContentObserver );

        // 13. verify the data was inserted into the weather table

        // 13a. query the weather table and get  a cursor

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 13b. verify the cursor

        TestUtilities.validateCursor( "testInsertReadProvider, weatherCursor.",
                weatherCursor, weatherContentValues );

        if ( weatherCursor != null ) { weatherCursor.close(); }

        // 14. confirm the join

        // 14a. add the location values in with the weather data

        weatherContentValues.putAll( locationContentValues );

        // 14b. get the joined weather and location data in a cursor

        Cursor weatherAndLocationCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherForLocationUri( TestUtilities.TEST_LOCATION ),
                null,
                null,
                null,
                null
        );

        // 14c. verify the cursor

        TestUtilities.validateCursor(
                "Error: ContentResolver could not insert the joined weather and location data into the db.",
                weatherAndLocationCursor,
                weatherContentValues );

        // 14d. get the joined weather and location data with start date in a cursor

        Cursor weatherAndLocationWithStartDateCursor =
                mContext.getContentResolver().query(
                        WeatherEntry.buildWeatherForLocationWithStartDateUri( TestUtilities.TEST_LOCATION,
                                TestUtilities.TEST_DATE ),
                        null,
                        null,
                        null,
                        null
                );

        // 14e. verify the cursor

        TestUtilities.validateCursor(
                "Error: ContentResolver could not insert the joined weather and location data with start date into the db.",
                weatherAndLocationWithStartDateCursor,
                weatherContentValues );

        // 14f. get the joined weather and location data for a specific date in a cursor

        Cursor weatherAndLocationWithSpecificDateCursor =
                mContext.getContentResolver().query(
                        WeatherEntry.buildWeatherForLocationWithSpecificDateUri( TestUtilities.TEST_LOCATION,
                                TestUtilities.TEST_DATE ),
                        null,
                        null,
                        null,
                        null
                );

        // 14g. verify the cursor

        TestUtilities.validateCursor(
                "Error: ContentResolver could not insert the weather and location data with specific date into the db.",
                weatherAndLocationWithSpecificDateCursor,
                weatherContentValues
        );

    } // end method testInsertReadProvider

    /**
     * Tests the update function of the ContentProvider
     * by inserting and then updating the data.
     * */
    // begin method testUpdateLocation
    public void testUpdateLocation() {

        // 0. create a test location ContentValues
        // 1. insert the location ContentValues into the db and get a uri
        // 2. get the row ID from the uri
        // 3. verify the row ID and log it
        // 4. create a new location ContentValues with
        // 4a. the row ID gotten in 3
        // 4b. a different city name
        // 5. query the db for the location and get a cursor
        // 6. register an observer on the gotten cursor
        // 7. update the db with the new ContentValues made in 4
        // 8. ensure the update has been done
        // 9. ensure our observer is being called
        // 10. unregister the observer
        // 11. close the location cursor
        // 12. query the db for rows whose id is the location ID gotten in 3 and get a cursor
        // 13. check if the gotten cursor in 12 has the same values as
        // those in the ContentValues at 4
        // 14. close the cursor gotten at 12

        // 0. create a test location ContentValues

        ContentValues locationContentValues = TestUtilities.createNorthPoleLocationValues();

        // 1. insert the location ContentValues into the db and get a uri

        Uri locationUri = mContext.getContentResolver().insert(
                LocationEntry.CONTENT_URI, locationContentValues );

        // 2. get the row ID from the uri

        long locationRowId = ContentUris.parseId( locationUri );

        // 3. verify the row ID and log it

        assertTrue( "Error: Could not insert location data into db.", locationRowId != -1 );

        Log.e( "New row id: ", String.valueOf( locationRowId ) );

        // 4. create a new location ContentValues with

        ContentValues updatedContentValues = new ContentValues( locationContentValues );

        // 4a. the row ID gotten in 3

        updatedContentValues.put( LocationEntry._ID, locationRowId );

        // 4b. a different city name

        updatedContentValues.put( LocationEntry.COLUMN_CITY_NAME, "Bethel" );

        // 5. query the db for the location and get a cursor

        Cursor locationCursor = mContext.getContentResolver().query( LocationEntry.CONTENT_URI,
                null, null, null, null );

        // 6. register an observer on the gotten cursor

        TestContentObserver testContentObserver = TestContentObserver.getTestContentObserver();
        locationCursor.registerContentObserver( testContentObserver );

        // 7. update the db with the new ContentValues made in 4

        int updateCount =  mContext.getContentResolver().update( LocationEntry.CONTENT_URI,
                updatedContentValues,
                LocationEntry._ID + " = ?",
                new String[]{ String.valueOf( locationRowId ) }
        );

        // 8. ensure the update has been done

        assertEquals( "Error: Not one update has been done on the db.", updateCount, 1 );

        // 9. ensure our observer is being called

        testContentObserver.waitForNotificationOrFail();

        // 10. unregister the observer

        locationCursor.unregisterContentObserver( testContentObserver );

        // 11. close the location cursor

        locationCursor.close();

        // 12. query the db for rows whose id is the location ID gotten in 3 and get a cursor

        Cursor updateCursor = mContext.getContentResolver().query( LocationEntry.CONTENT_URI,
                null,
                LocationEntry._ID + " = " + locationRowId,
                null,
                null
        );

        // 13. check if the gotten cursor in 12 has the same values as
        // those in the ContentValues at 4

        TestUtilities.validateCursor( "testUpdateLocation, updateCursor.", updateCursor,
                updatedContentValues );

        // 14. close the cursor gotten at 12

        updateCursor.close();

    } // end method testUpdateLocation

    /**
     * Test the delete function of the ContentProvider.
     * Works only if the insert and query functions also work.
     * */
    // begin method testDeleteRecords
    public void testDeleteRecords() {

        // 0. insert items into the db via the provider
        // 1. register content observers for
        // 1a. location delete
        // 1b. weather delete
        // 2. delete everything from the provider
        // 3. verify the delete worked on the
        // 3a. location uri
        // 3b. weather uri
        // 4. unregister content observers for
        // 4a. the location
        // 4b. the weather

        // 0. insert items into the db via the provider

        testInsertReadProvider();

        // 1. register content observers for

        // 1a. location delete

        TestContentObserver locationObserver = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                LocationEntry.CONTENT_URI, true, locationObserver );

        // 1b. weather delete

        TestContentObserver weatherObserver = TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                WeatherEntry.CONTENT_URI, true, weatherObserver );

        // 2. delete everything from the provider

        deleteAllRecordsFromProvider();

        // 3. verify the delete worked on the

        // 3a. location uri

        locationObserver.waitForNotificationOrFail();

        // 3b. weather uri

        weatherObserver.waitForNotificationOrFail();

        // 4. unregister content observers for

        // 4a. the location

        mContext.getContentResolver().unregisterContentObserver( locationObserver );

        // 4b. the weather

        mContext.getContentResolver().unregisterContentObserver( weatherObserver );

    } // end method testDeleteRecords

    /**
     * Test the bulk insert function of the ContentProvider.
     * Works only if the insert and query functions also work.
     * */
    // begin method testBulkInsert
    public void testBulkInsert() {

        // 0. create a location ContentValues
        // 1. insert the location ContentValues and verify getting a row back
        // 2. query the db for the inserted ContentValues and get a cursor
        // 3. validate the cursor
        // 4. create weather ContentValues items to bulk insert
        // 5. register a content observer for the bulk insert
        // 6. do the bulk insert and get the number of items inserted
        // 7. verify if the content observer was called
        // 8. unregister the content observer
        // 9. verify that the number of inserted items
        //  are equal to the number of items created in 4
        // 10. query the db for the weather items in it sorted ascending and get a cursor
        // 11. verify that the number of items in the cursor
        //  are equal to the number of items created in 4
        // 12. verify the items in the cursor match the items in 4
        // 12a. move to the first item in the cursor
        // 12b. for each weather ContentValues that was bulk inserted in 4,
        //  test if it is in the cursor
        // 13. close the cursor

        // 0. create a location ContentValues

        ContentValues locationContentValues = TestUtilities.createNorthPoleLocationValues();

        // 1. insert the location ContentValues and verify getting a row back

        Uri locationUri = mContext.getContentResolver().insert(
                LocationEntry.CONTENT_URI, locationContentValues );

        long locationRowId = ContentUris.parseId( locationUri );

        assertTrue( "Error: Could not insert location data into db.", locationRowId != -1 );

        // 2. query the db for the inserted ContentValues and get a cursor

        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 3. validate the cursor

        TestUtilities.validateCursor( "testBulkInsert, location cursor.",
                locationCursor,  locationContentValues );

        // 4. create weather ContentValues items to bulk insert

        ContentValues weatherContentValues[] = createBulkInsertWeatherValues( locationRowId );

        // 5. register a content observer for the bulk insert

        TestContentObserver weatherObserver = TestContentObserver.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(
                WeatherEntry.CONTENT_URI, true, weatherObserver );

        // 6. do the bulk insert and get the number of items inserted

        int insertCount = mContext.getContentResolver().bulkInsert(
                WeatherEntry.CONTENT_URI, weatherContentValues );

        // 7. verify if the content observer was called

        weatherObserver.waitForNotificationOrFail();

        // 8. unregister the content observer

        mContext.getContentResolver().unregisterContentObserver( weatherObserver );

        // 9. verify that the number of inserted items
        //  are equal to the number of items created in 4

        assertEquals(
                "Error: We expected " + weatherContentValues.length + " items inserted but " +
                insertCount + " items got inserted.",
                BULK_INSERT_RECORDS_TO_INSERT, insertCount );

        // 10. query the db for the weather items in it sorted ascending and get a cursor

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                WeatherEntry.COLUMN_DATE + " ASC"
        );

        // 11. verify that the number of items in the cursor
        //  are equal to the number of items created in 4

        assertEquals(
                "Error: We expected " + weatherContentValues.length +
                        " items in the cursor but we got "+
                        weatherCursor.getCount() + " items.",
                BULK_INSERT_RECORDS_TO_INSERT, weatherCursor.getCount() );

        // 12. verify the items in the cursor match the items in 4

        // 12a. move to the first item in the cursor

        weatherCursor.moveToFirst();

        // 12b. for each weather ContentValues that was bulk inserted in 4,
        //  test if it is in the cursor

        // begin for through items in the weather ContentValues array
        for ( int i = 0; i < weatherContentValues.length; i++, weatherCursor.moveToNext() ) {

            TestUtilities.validateCurrentRecord(
                    "testBulkInsert, weatherCursor.",
                    weatherCursor,
                    weatherContentValues[ i ]
            );

        } // end for through items in the weather ContentValues array

        // 13. close the cursor

        weatherCursor.close();

    } // end method testBulkInsert

    /*
        This helper method deletes all records from both db tables using the ContentProvider.
        It also queries the ContentProvider to ensure the db has been successfully deleted, so
        it can be used only when the Query and Delete functions have been put into the ContentProvider.

        All calls to deleteAllRecordsFromDB should be replaced with this method after delete
        functionality is implemented in the ContentProvider.
    */
    // begin method deleteAllRecordsFromProvider
    public void deleteAllRecordsFromProvider() {

        // 0. delete all records in the weather table using the ContentProvider
        // 1. delete all records in the location table using the ContentProvider
        // 2. get a cursor from the weather table using the ContentProvider
        // 3. confirm that the weather table has nothing
        // 4. close the cursor
        // 5. get a cursor from the location table using the ContentProvider
        // 6. confirm that the location table has nothing
        // 7. close the cursor

        // 0. delete all records in the weather table using the ContentProvider

        mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );

        // 1. delete all records in the location table using the ContentProvider

        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        // 2. get a cursor from the weather table using the ContentProvider

        Cursor weatherTableCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 3. confirm that the weather table has nothing

        // getCount - returns the numbers of rows in the cursor
        assertEquals( "Error: The Weather table still has some records in it.",
                0, weatherTableCursor.getCount() );

        // 4. close the cursor

        weatherTableCursor.close();

        // 5. get a cursor from the location table using the ContentProvider

        Cursor locationTableCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // 6. confirm that the location table has nothing

        assertEquals( "Error: The Location table still has some records in it.",
                0, locationTableCursor.getCount() );


        // 7. close the cursor

        locationTableCursor.close();

    } // end method deleteAllRecordsFromProvider

    /*
        This helper function deletes all records from both db tables using db functions only.
        It should be replaced as soon as delete functionality is available in our ContentProvider.
    */
    // begin method deleteAllRecordsFromDB
    public void deleteAllRecordsFromDB() {

        // 0. get a writable database
        // 1. delete the weather table
        // 2. delete the location table
        // 3. close the database

        // 0. get a writable database

        WeatherDbHelper weatherDbHelper = new WeatherDbHelper( mContext );
        SQLiteDatabase database = weatherDbHelper.getWritableDatabase();

        // 1. delete the weather table

        database.delete( WeatherEntry.TABLE_NAME, null, null );

        // 2. delete the location table

        database.delete( LocationEntry.TABLE_NAME, null, null );

        // 3. close the database

        database.close();

    } // end method deleteAllRecordsFromDB

    /*
        This method should call deleteAllRecordsFromProvider
        as soon as deletion is enabled in our ContentProvider
    */

    public void deleteAllRecords() { deleteAllRecordsFromProvider(); }

    /* statics */

    /* Create weather values to be inserted into the weather table */
    // begin method createBulkInsertWeatherValues
    // ContentValues - class used to store a set of values that the ContentResolver can process.
    public static ContentValues[] createBulkInsertWeatherValues( long locationRowId ) {

        // 0. get the current test date
        // 1. get the number of milliseconds in a day
        // 2. create an array of ContentValues which will store the ContentValues we will return
        // 3. create random ContentValues to insert into the array
        // 4. return the array

        // 0. get the current test date

        long currentTestDate = TestUtilities.TEST_DATE;

        // 1. get the number of milliseconds in a day

        long millisecondsInADay = 24 * 60 * 60 * 1000;

        // 2. create an array of ContentValues which will store the ContentValues we will return

        ContentValues returnContentValues[] = new ContentValues[ BULK_INSERT_RECORDS_TO_INSERT ];

        // 3. create random ContentValues to insert into the array

        // begin for through all the records we will insert
        for ( int i = 0; i < returnContentValues.length; i++, currentTestDate += millisecondsInADay ) {

            ContentValues weatherContentValues = new ContentValues();

            weatherContentValues.put( WeatherEntry.COLUMN_LOCATION_KEY, locationRowId );
            weatherContentValues.put( WeatherEntry.COLUMN_DATE, currentTestDate );
            weatherContentValues.put( WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES, 1.1 );
            weatherContentValues.put( WeatherEntry.COLUMN_HUMIDITY, 1.2 + 0.01 * ( float ) i );
            weatherContentValues.put( WeatherEntry.COLUMN_PRESSURE, 1.3 - 0.01 * ( float ) i );
            weatherContentValues.put( WeatherEntry.COLUMN_MAX_TEMP, 75 + i );
            weatherContentValues.put( WeatherEntry.COLUMN_MIN_TEMP, 65 - i );
            weatherContentValues.put( WeatherEntry.COLUMN_SHORT_DESCRIPTION, "Asteroid " + i );
            weatherContentValues.put( WeatherEntry.COLUMN_WIND_SPEED, 5.5 + 0.2 * ( float ) i );
            weatherContentValues.put( WeatherEntry.COLUMN_WEATHER_ID, 321 );

            returnContentValues[ i ] = weatherContentValues;

        } // end for through all the records we will insert

        // 4. return the array

        return returnContentValues;

    } // end method createBulkInsertWeatherValues

    /* INNER CLASSES */

} // end class TestContentProvider