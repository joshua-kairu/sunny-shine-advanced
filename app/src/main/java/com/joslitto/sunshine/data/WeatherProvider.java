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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.joslitto.sunshine.data.contract.WeatherContract;

import static com.joslitto.sunshine.data.contract.WeatherContract.*;

/**
 * The ContentProvider that will assist us work with stored weather data
 * */
// begin class WeatherProvider
public class WeatherProvider extends ContentProvider {

    /* CONSTANTS */
    
    /* Integers */

    /* Uri matches */

    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_LOCATION = 101;
    public static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    public static final int LOCATION = 300;


    /* SQLite Query Builders */

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    // begin static
    static {

        // 0. initialize the weather by location setting builder
        // 1. join the weather table to the location table
        //      wherever the location ids on both tables match

        // 0. initialize the weather by location setting builder

        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        // 1. join the weather table to the location table
        //      wherever the location ids on both tables match

        // weather INNER JOIN location ON weather.location_id = location.location_id
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        LocationEntry.TABLE_NAME +
                        " ON " + WeatherEntry.TABLE_NAME +
                        "." + WeatherEntry.COLUMN_LOCATION_KEY +
                        " = " + LocationEntry.TABLE_NAME +
                        "." + LocationEntry._ID
        );

    } // end static

    /* Strings */

    /** location.location_setting = ?  */
    public static final String sLocationSettingSelection =
            LocationEntry.TABLE_NAME + "." +
                    LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    /** location.location_setting = ? AND weather.date >= ?  */
    public static final String sLocationSettingWithStartDateSelection =
            LocationEntry.TABLE_NAME + "." +
                    LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherEntry.COLUMN_DATE + " >= ? ";

    /** location.location_setting = ? AND weather.date = ?  */
    public static final String sLocationSettingAndDaySelection =
            LocationEntry.TABLE_NAME + "." +
                    LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    WeatherEntry.COLUMN_DATE + " = ? ";



    /* Uri Matchers */

    /** The URI Matcher used by this content provider. */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
        
    /* VARIABLES */

    /* Weather DB Helpers */

    private WeatherDbHelper mOpenDbHelper; // ditto

    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onCreate
    public boolean onCreate() {

        // 0. create a new Weather db helper
        // 1. return true

        // 0. create a new Weather db helper

        mOpenDbHelper = new WeatherDbHelper( getContext() );

        // 1. return true

        return true;

    } // end onCreate

    @Nullable
    @Override
    // begin getType
    //  getType - Handles requests for the MIME type of the data at the given URI.
    //      The returned MIME type should start with vnd.android.cursor.item for a single record,
    //      or vnd.android.cursor.dir/ for multiple items.
    public String getType( Uri uri ) {

        // 0. use the local matcher to know which kind of Uri this is
        // 1. if it is
        // 1a. a weather Uri
        // 1a1. return the weather Uri content type
        // 1b. a location Uri
        // 1b1. return the location Uri content type
        // 1c. a weather with location Uri
        // 1c1. return the weather with location content type
        // 1d. a weather with location and date Uri
        // 1d1. return the weather with location and date content type
        // 1last. not matching
        // 1last1. throw an exception

        // 0. use the local matcher to know which kind of Uri this is

        final int match = sUriMatcher.match( uri );

        // 1. if it is

        // begin switch to know match type
        switch ( match ) {

            // 1a. a weather Uri
            case WEATHER:

                // 1a1. return the weather Uri content type

                return WeatherEntry.CONTENT_TYPE;

            // 1b. a location Uri

            case LOCATION:

                // 1b1. return the location Uri content type

                return LocationEntry.CONTENT_TYPE;

            // 1c. a weather with location Uri

            case WEATHER_WITH_LOCATION:

                // 1c1. return the weather with location content type

                return WeatherEntry.CONTENT_TYPE;

            // 1d. a weather with location and date Uri

            case WEATHER_WITH_LOCATION_AND_DATE:

                // 1d1. return the weather with location and date content type

                // only returns a single row
                return WeatherEntry.CONTENT_ITEM_TYPE;

            // 1last. not matching

            default:

                // 1last1. throw an exception

                throw new UnsupportedOperationException( "Unknown Uri: " + uri );

        } // end switch to know match type

    } // end getType

    @Nullable
    @Override
    // begin query
    // query -  handles query requests from clients.
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {

        // 0. have a return cursor
        // 1. determine which kind of request the Uri
        // 1a. weather with location and date requests
        // 1a1. use an appropriate cursor
        // 1b. weather with location requests
        // 1b1. use an appropriate cursor
        // 1c. weather requests
        // 1c1. use an appropriate cursor
        // 1d. location requests
        // 1d1. use an appropriate cursor
        // 1last. unknown requests
        // 1last1. throw an exception
        // 2. register the gotten cursor to watch for any changes on that uri
        // 3. return the cursor

        // 0. have a return cursor

        Cursor returnCursor;

        // 1. determine which kind of request the Uri

        // begin switch to know the uri request
        switch ( sUriMatcher.match( uri ) ) {

            // 1a. weather with location and date requests

            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:

                // 1a1. use an appropriate cursor

                returnCursor = getWeatherByLocationSettingAndDate( uri, projection, sortOrder );

                break;

            // 1b. weather with location requests

            // "weather/*"
            case WEATHER_WITH_LOCATION:

                // 1b1. use an appropriate cursor

                returnCursor = getWeatherByLocationSetting( uri, projection, sortOrder );

                break;

            // 1c. weather requests

            // "weather"
            case WEATHER:

                // 1c1. use an appropriate cursor

                returnCursor = getWeather( projection, selection, selectionArgs, sortOrder ); break;

            // 1d. location requests

            // "location"
            case LOCATION:

                // 1d1. use an appropriate cursor

                returnCursor = getLocation( projection, selection, selectionArgs, sortOrder ); break;

            // 1last. unknown requests

            default:

                // 1last1. throw an exception

                throw new UnsupportedOperationException( "Unknown uri: " + uri );

        } // end switch to know the uri request

        // 2. register the gotten cursor to watch for any changes on that uri

        // setNotificationUri - Register to watch a content URI for changes.
        if ( returnCursor != null ) {
            returnCursor.setNotificationUri( getContext().getContentResolver(), uri );
        }

        // 3. return the cursor

        return returnCursor;

    } // end query

    @Nullable
    @Override
    // begin insert
    /** Add ability to insert Locations in the future */
    public Uri insert( Uri uri, ContentValues values ) {

        // 0. get a writable db
        // 1. have a return uri
        // 2. match uris
        // 2a. if it's a weather uri
        // 2a1. normalize the date for the content values
        // 2a2. attempt to insert the content values into the db
        // 2a2a. if successful, get the uri referring to the inserted values
        // 2a2b. if failed, throw an exception
        // 2b. if it's a location uri
        // 2b1. attempt to insert the ContentValues into the db
        // 2b1a. if successful, get the uri referring to the inserted values
        // 2b1b. if failed, exception!
        // 2last. if it's an unknown uri
        // 2last1. throw an exception
        // 3. tell any registered observers that data in the db has changed
        // 4. return the uri gotten in step 2

        // 0. get a writable db

        SQLiteDatabase database = mOpenDbHelper.getWritableDatabase();

        // 1. have a return uri

        Uri returnUri;

        // 2. match uris

        // begin switching the match
        switch ( sUriMatcher.match( uri ) ) {

            // 2a. if it's a weather uri

            case WEATHER:

                // 2a1. normalize the date for the content values

                normalizeDate( values );

                // 2a2. attempt to insert the content values into the db

                long _id = database.insert( WeatherEntry.TABLE_NAME, null, values );

                // 2a2a. if successful, get the uri referring to the inserted values

                if ( _id > -1 ) { returnUri = WeatherEntry.buildWeatherUri( _id ); }

                // 2a2b. if failed, throw an exception

                else { throw new SQLException(
                        "Error: Failed to insert weather values into the database." ); }

                break;

            // 2b. if it's a location uri

            case LOCATION:

                // 2b1. attempt to insert the ContentValues into the db

                _id = database.insert( LocationEntry.TABLE_NAME, null, values );

                // 2b1a. if successful, get the uri referring to the inserted values

                if ( _id > -1 ) { returnUri = LocationEntry.buildLocationUri( _id ); }

                // 2b1b. if failed, exception!

                else { throw new SQLException(
                        "Error: Failed to insert location values into the database." ); }

                break;

            // 2last. if it's an unknown uri
            // 2last1. throw an exception

            default: throw new UnsupportedOperationException( "Unknown uri: " + uri );

        } // end switching the match

        // 3. tell any registered observers that data in the db has changed

        // notifyChange - Notify registered observers that a row was updated
        //      and attempt to sync changes to the network.
        getContext().getContentResolver().notifyChange( uri, null );

        // 4. return the uri gotten in step 2

        return returnUri;

    } // end insert

    @Override
    // begin delete
    // delete - handle requests to delete one or more rows.
    // returns the number of rows affected.
    public int delete( Uri uri, String selection, String[] selectionArgs ) {

        // 0. get a writable db
        // 1. match the uri
        // 1a. if it is the weather
        // 1a1. delete all rows of weather
        // 1a2. notify any listeners
        // 1b. if it is the location
        // 1b1. delete all rows of location
        // 1b2. notify any listeners
        // 1last. no matches?
        // 1last1. exception!
        // 2. return actual deleted rows

        // 0. get a writable db

        SQLiteDatabase database = mOpenDbHelper.getWritableDatabase();

        if ( selection == null ) { selection = "1"; }
        // so that the delete function
        // actually returns the number of rows deleted

        int rowsDeleted;

        // 1. match the uri

        // begin switch for matching
        switch ( sUriMatcher.match( uri ) ) {

            // 1a. if it is the weather

            case WEATHER:

                // 1a1. delete all rows of weather

                rowsDeleted = database.delete( WeatherEntry.TABLE_NAME, selection, selectionArgs );

                // 1a2. notify any listeners

                if ( rowsDeleted != 0 ) {
                    getContext().getContentResolver().notifyChange( WeatherEntry.CONTENT_URI, null );
                }

                break;

            // 1b. if it is the location

            case LOCATION:

                // 1b1. delete all rows of location

                rowsDeleted = database.delete( LocationEntry.TABLE_NAME, selection, selectionArgs );

                // 1b2. notify any listeners

                if ( rowsDeleted != 0 ) {
                    getContext().getContentResolver().notifyChange( LocationEntry.CONTENT_URI, null );
                }

                break;

            // 1last. no matches?
            // 1last1. exception!

            default: throw new UnsupportedOperationException( "Unknown Uri: " + uri );

        } // end switch for matching

        // 2. return actual deleted rows

        return rowsDeleted;

    } // end delete

    @Override
    // begin update
    // update - handle requests to update one or more rows
    // returns the number of rows affected.
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {

        // 0. get a writable db
        // 1. match the uri
        // 1a. if it is the weather
        // 1a1. updated all impacted rows in weather
        // 1a2. notify any listeners
        // 1b. if it is the location
        // 1b1. updated all impacted rows in location
        // 1b2. notify any listeners
        // 1last. no matches?
        // 1last1. exception!
        // 2. return actual updated rows

        // 0. get a writable db

        SQLiteDatabase database = mOpenDbHelper.getWritableDatabase();

        int rowsUpdated;

        // 1. match the uri

        // begin switch for matching
        switch ( sUriMatcher.match( uri ) ) {

            // 1a. if it is the weather

            case WEATHER:

                // 1a1. updated all impacted rows in weather

                normalizeDate( values );

                rowsUpdated = database.update( WeatherEntry.TABLE_NAME, values, selection, selectionArgs );

                // 1a2. notify any listeners

                if ( rowsUpdated != 0 ) {
                    getContext().getContentResolver().notifyChange( WeatherEntry.CONTENT_URI, null );
                }

                break;

            // 1b. if it is the location

            case LOCATION:

                // 1b1. updated all impacted rows in location

                rowsUpdated = database.update( LocationEntry.TABLE_NAME, values, selection, selectionArgs );

                // 1b2. notify any listeners

                if ( rowsUpdated != 0 ) {
                    getContext().getContentResolver().notifyChange( LocationEntry.CONTENT_URI, null );
                }

                break;

            // 1last. no matches?
            // 1last1. exception!

            default: throw new UnsupportedOperationException( "Unknown Uri: " + uri );

        } // end switch for matching

        // 2. return actual updated rows

        return rowsUpdated;

    } // end update

    @Override
    // begin bulkInsert
    // bulkInsert - handle requests to insert a set of new rows
    // returns the number of values that were inserted.
    public int bulkInsert( Uri uri, ContentValues[] values ) {

        // 0. get a writable db
        // 1. do match
        // 1a. if weather
        // 1a1. start a transaction on the db
        // 1a2. have the return count
        // 1a3. for each ContentValues
        // 1a3a. normalize its date
        // 1a3b. attempt to insert it into the weather db
        // 1a3c. if successful, increment the return count
        // 1a4. transaction went well
        // 1a5. end the transaction
        // 1a6. notify listeners
        // 1a7. return the count
        // 1last. if no match
        // 1last1. use the super method

        // 0. get a writable db

        SQLiteDatabase database = mOpenDbHelper.getWritableDatabase();

        // 1. do match

        // begin switching for the matching
        switch ( sUriMatcher.match( uri ) ) {

            // 1a. if weather
            // 1a1. start a transaction on the db
            // 1a2. have the return count
            // 1a3. for each ContentValues
            // 1a3a. normalize its date
            // 1a3b. attempt to insert it into the weather db
            // 1a3c. if successful, increment the return count
            // 1a4. transaction went well
            // 1a5. end the transaction
            // 1a6. notify listeners
            // 1a7. return the count

            // 1a. if weather

            case WEATHER:

                // 1a1. start a transaction on the db

                // beginTransaction - Begins a transaction in EXCLUSIVE mode
                database.beginTransaction();

                // 1a2. have the return count

                int returnCount = 0;

                // 1a3. for each ContentValues

                // begin for through all content values
                for ( int i = 0; i < values.length; i++ ) {

                    // 1a3a. normalize its date

                    normalizeDate( values[ i ] );

                    // 1a3b. attempt to insert it into the weather db

                    long _id = database.insert(
                            WeatherEntry.TABLE_NAME, null, values[ i ] );

                    // 1a3c. if successful, increment the return count

                    if ( _id > -1 ) { returnCount++; }

                } // end for through all content values

                // 1a4. transaction went well

                // setTransactionSuccessful - Marks the current transaction as successful.
                //      Do not do any more database work between calling this and calling endTransaction.
                database.setTransactionSuccessful();

                // 1a5. end the transaction

                // endTransaction - End a transaction.
                database.endTransaction();

                // 1a6. notify listeners

                getContext().getContentResolver().notifyChange( uri, null );

                // 1a7. return the count

                return returnCount;

            // 1last. if no match

            default:

                // 1last1. use the super method

                return super.bulkInsert( uri, values );

        } // end switching for the matching

    } // end bulkInsert

    @Override
    @TargetApi( 11 )
    // TargetApi - Lint should treat this type as targeting a given API level,
    //      no matter what the project target is.
    // begin shutdown
    // shutdown - shuts down the ContentProvider instance manually. Good when unit testing.
    public void shutdown() {

        // 0. close the db helper
        // 1. super things

        // 0. close the db helper

        mOpenDbHelper.close();

        // 1. super things

        super.shutdown();

    } // end shutdown

    /* Other Methods */

    /** Retrieve the weather using the location setting */
    // begin method getWeatherByLocationSetting
    private Cursor getWeatherByLocationSetting( Uri uri, String[] projection, String sortOrder ) {

        // 0. get the location setting from the Uri
        // 1. get the start date from the Uri
        // 2. if there is no start date
        // 2a. use the location setting selection
        // 2b. use the location setting as the selection argument
        // 3. if there is a start date
        // 3a. use the location setting with start date selection
        // 3b. use the location setting and the start date as the selection arguments
        // 4. query the db and return a cursor

        // 0. get the location setting from the Uri

        String locationSetting = WeatherEntry.getLocationSettingFromUri( uri );

        // 1. get the start date from the Uri

        long startDate = WeatherEntry.getStartDateFromUri( uri );

        String selection;
        String selectionArguments[];

        // 2. if there is no start date

        // begin if start date is zero
        if ( startDate == 0 ) {

            // 2a. use the location setting selection

            selection = sLocationSettingSelection;

            // 2b. use the location setting as the selection argument

            selectionArguments = new String[] { locationSetting };

        } // end if start date is zero

        // 3. if there is a start date

        // begin else the start date is not zero
        else {

            // 3a. use the location setting with start date selection

            selection = sLocationSettingWithStartDateSelection;

            // 3b. use the location setting and the start date as the selection arguments

            selectionArguments = new String[] { locationSetting, Long.toString( startDate ) };

        } // end else the start date is not zero

        // 4. query the db and return a cursor

        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArguments,
                null,
                null,
                sortOrder
        );

    } // end method getWeatherByLocationSetting

    /** Retrieve the weather using the location setting and the date */
    // begin method getWeatherByLocationSettingAndDate
    private Cursor getWeatherByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder ) {

        // 0. get the location setting from the Uri
        // 1. get the date from the Uri
        // 2. query the db and return the cursor

        // 0. get the location setting from the Uri

        String locationSetting = WeatherEntry.getLocationSettingFromUri( uri );

        // 1. get the date from the Uri

        long date = WeatherEntry.getDateFromUri( uri );

        // 2. query the db and return the cursor

        return sWeatherByLocationSettingQueryBuilder.query(
                mOpenDbHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{ locationSetting, Long.toString( date ) },
                null,
                null,
                sortOrder
        );

    } // end method getWeatherByLocationSettingAndDate

    /** Retrieve a cursor having all the weather entries in the database. */
    // begin method getWeather
    private Cursor getWeather( String[] projection, String selection,
                               String[] selectionArgs, String sortOrder ) {

        // 0. query the db and return a cursor

        SQLiteDatabase database = mOpenDbHelper.getReadableDatabase();

        return database.query(
                WeatherEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    } // end method getWeather

    /** Retrieve a cursor having all the location entries in the database. */
    // begin method getLocation
    private Cursor getLocation( String[] projection, String selection,
                                String[] selectionArgs, String sortOrder ) {

        // 0. query the db and return a cursor

        SQLiteDatabase database = mOpenDbHelper.getReadableDatabase();

        return database.query(
                LocationEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    } // end method getWeather

    /** Normalize the date value */
    // begin method normalizeDate
    private void normalizeDate( ContentValues contentValues ) {

        // 0. if the ContentValues has a date
        // 0a. get the date
        // 0b. put it back as a normalized one

        // 0. if the ContentValues has a date

        // begin if there is a date in there
        if ( contentValues.containsKey( WeatherEntry.COLUMN_DATE ) == true ) {

            // 0a. get the date

            long date = contentValues.getAsLong( WeatherEntry.COLUMN_DATE );

            // 0b. put it back as a normalized one

            contentValues.put( WeatherEntry.COLUMN_DATE,
                               WeatherContract.normalizeDate( date ) );

        } // end if there is a date in there

    } // end method normalizeDate

    /* statics */

    /**
     * Build the URI Matcher
     *
     * Matches against content://authority/weather for weather Uris
     * Matches against content://authority/weather/[locationQueryString]
     *  for weather with location Uris
     * Matches against content://authority/weather/[locationQueryString]/[dateNumber]
     *  for weather with location and date Uris
     * Matches against content://authority/location for location Uris
     * */
    // begin method buildUriMatcher
    public static UriMatcher buildUriMatcher() {

        // 0. create the constructor

        UriMatcher uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );

        // 1. add a URI to match each of the types needed

        String authority = CONTENT_AUTHORITY;
        String weatherPath = PATH_WEATHER;
        String weatherWithLocationPath = PATH_WEATHER + "/*";
        String weatherWithLocationAndDatePath = PATH_WEATHER + "/*/#";
        String locationPath = PATH_LOCATION;

        uriMatcher.addURI( authority, weatherPath, WEATHER );
        uriMatcher.addURI( authority, weatherWithLocationPath, WEATHER_WITH_LOCATION );
        uriMatcher.addURI( authority, weatherWithLocationAndDatePath, WEATHER_WITH_LOCATION_AND_DATE );
        uriMatcher.addURI( authority, locationPath, LOCATION );

        // 2. return the new matcher

        return uriMatcher;

    } // end method UriMatcher

    /* INNER CLASSES */

} // end class WeatherProvider