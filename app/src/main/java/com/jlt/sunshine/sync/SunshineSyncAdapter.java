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

package com.jlt.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.jlt.sunshine.BuildConfig;
import com.jlt.sunshine.MainActivity;
import com.jlt.sunshine.R;
import com.jlt.sunshine.data.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;

/**
 * The sync adapter we will use for fetching app data.
 * */
// begin class SunshineSyncAdapter
public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {

    /* CONSTANTS */
    
    /* Arrays */

    /** 
     * Projection to assist pull notification data from the db. 
     * */
    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
    };

    /* Longs */

    private static long DAY_IN_MILLIS = 24 * 60 * 60 * 1000; // ditto

    /* Integers */

    /** Interval in seconds at which to sync with the weather. */
    public static final int SYNC_INTERVAL = 60 * 180;

    /** Flex time for weather sync. */
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    // indices for the notification projection
    private static final int COLUMN_WEATHER_CONDITION_ID = 0;
    private static final int COLUMN_WEATHER_MAX_TEMP = 1;
    private static final int COLUMN_WEATHER_MIN_TEMP = 2;
    private static final int COLUMN_WEATHER_SHORT_DESCRIPTION = 3;

    /**
     * ID matched to a notification so that the notification can be reused.
     * Reusing a notification ID causes one more notification to be posted.
     * */
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();

    /* VARIABLES */

    /* Notification Managers */

    private NotificationManager mNotificationManager; // ditto

    /* CONSTRUCTOR */

    /**
     * Creates an {@link AbstractThreadedSyncAdapter}.
     *
     * @param context        the {@link Context} that this is running within.
     * @param autoInitialize if true then sync requests that have
     *                       {@link ContentResolver#SYNC_EXTRAS_INITIALIZE} set will be internally handled by
     *                       {@link AbstractThreadedSyncAdapter} by calling
     *                       {@link ContentResolver#setIsSyncable(Account, String, int)} with 1 if it
     */
    // begin constructor
    public SunshineSyncAdapter( Context context, boolean autoInitialize ) {

        // 0. super stuff
        // 1. initialize the notification manager

        // 0. super stuff

        super( context, autoInitialize );

        // 1. initialize the notification manager

        mNotificationManager = ( NotificationManager ) context.getSystemService(
                Context.NOTIFICATION_SERVICE );

    } // end constructor
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onPerformSync
    public void onPerformSync( Account account, Bundle extras, String authority,
                               ContentProviderClient provider, SyncResult syncResult ) {

        Log.e( LOG_TAG, "onPerformSync: called" );

        // 0. fetch the weather info

        // 0. fetch the weather info

        fetchWeather();

    } // end onPerformSync
    
    /* Other Methods */

    /* statics */

    /**
     * Helper method to have the sync adapter sync immediately.
     *
     * @param context The {@link Context} used to access the account service
     * */
    // begin method syncImmediately
    public static void syncImmediately( Context context ) {

        // 0. create a bundle
        // 1. put an argument to sync asap
        // 2. put an argument to sync manually
        // 3. start sync

        // 0. create a bundle

        Bundle bundle = new Bundle();

        // 1. put an argument to sync asap

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true );

        // 2. put an argument to sync manually

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_MANUAL, true );

        // 3. start sync

        // requestSync - Start an asynchronous sync operation.
        ContentResolver.requestSync( getSyncAccount( context ),
                context.getString( R.string.content_authority ), bundle );

    } // end method syncImmediately

    /**
     * Helper method to get the fake account to be used with the sync adapter, or
     * make one if the fake account doesn't currently exist.
     *
     * If we make a new account, we call onAccountCreated so we can initialize things.
     *
     * @param context The {@link Context} used to access the account service.
     *
     * @return A fake account.
     * */
    // begin method getSyncAccount
    public static Account getSyncAccount( Context context ) {

        // 0. get the accounts manager
        // 1. create the account type and default account
        // 2. if the account doesn't exist
        // 2a. attempt to add it
        // 2a1. if successful
        // 2a1a. notify the sync adapter that a new account has been created
        // 2a1b. return the account created at 1
        // 2a2. otherwise
        // 2a2a. return null
        // 3. otherwise the account exists
        // 3a. return the existing account

        // 0. get the accounts manager

        AccountManager accountManager = ( AccountManager ) context.getSystemService(
                Context.ACCOUNT_SERVICE );

        // 1. create the account type and default account

        Account newAccount = new Account( context.getString( R.string.app_name ),
                context.getString( R.string.sync_account_type ) );

        // begin checking if we have permission to access accounts
        if ( ContextCompat.checkSelfPermission( context, GET_ACCOUNTS ) == PERMISSION_GRANTED ) {

            // 2. if the account doesn't exist

            // if the account doesn't have a password, the account doesn't exist

            // begin if the account has no password
            // getPassword - Gets the saved password associated with the account.
            //  This is intended for authenticators and related code;
            //  applications should get an auth token instead.
            if ( accountManager.getPassword( newAccount ) == null ) {

                // 2a. attempt to add it

                // 2a1. if successful

                // begin if account addition is successful
                // addAccountExplicitly - Adds an account directly to the AccountManager.
                //  Normally used by sign-up wizards associated with authenticators,
                //  not directly by applications.
                if ( accountManager.addAccountExplicitly( newAccount, "", null ) == true ) {

                    // 2a1a. notify the sync adapter that a new account has been created
                    // 2a1b. return the account created at 1

                    // 2a1a. notify the sync adapter that a new account has been created

                    onAccountCreated( newAccount, context );

                    // 2a1b. return the account created at 1

                    return newAccount;

                } // end if account addition is successful

                // 2a2. otherwise

                // 2a2a. return null

                else { return null; }

            } // end if the account has no password

            // 3. otherwise the account exists

            // 3a. return the existing account

            else { return newAccount; }

        } // end checking if we have permission to access accounts

        // begin else we have no permission to access accounts
        else {

            Log.e( LOG_TAG, "getSyncAccount: No permissions to access accounts." );

            return null;

        } // end else we have no permission to access accounts

    } // end method getSyncAccount

    /**
     * Helper method to schedule the sync adapter's periodic execution.
     *
     * @param context The {@link Context} we will be working in
     * @param syncInterval The time interval in seconds between successive syncs
     * @param flexTime The amount of flex time in seconds before {@param syncInterval}
     *                 that you permit for the sync to take place. Must be less than pollFrequency.
     * */
    // begin method configurePeriodicSync
    public static void configurePeriodicSync( Context context, int syncInterval, int flexTime ) {

        // 0. get the account
        // 1. get the authority
        // 2. for Kitkat and above
        // 2a. build a sync request using inexact timers
        // 2b. sync using that request
        // 3. for below Kitkat
        // 3a. sync using the exact sync intervals

        // 0. get the account

        Account account = getSyncAccount( context );

        // 1. get the authority

        String authority = context.getString( R.string.content_authority );

        // 2. for Kitkat and above

        // begin if Kitkat and above
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {

            // 2a. build a sync request using inexact timers

            SyncRequest syncRequest = new SyncRequest.Builder()
                    .setSyncAdapter( account, authority ) // Specify authority and account for this transfer.
                    .syncPeriodic( syncInterval, flexTime ) // Build a periodic sync with some flex time
                    .setExtras( new Bundle() )
                    .build();

            // 2b. sync using that request

            ContentResolver.requestSync( syncRequest );

        } // end if Kitkat and above

        // 3. for below Kitkat

        // begin else below Kitkat
        else {

            // 3a. sync using the exact sync intervals

            // addPeriodicSync -
            //  Specifies that a sync should be requested with the specified the account, authority,
            //  and extras at the given frequency. If there is already another periodic sync
            //  scheduled with the account, authority and extras then a new periodic sync won't be
            //  added, instead the frequency of the previous one will be updated.
            ContentResolver.addPeriodicSync( account, authority, new Bundle(), syncInterval );

        } // end else below Kitkat

    } // end method configurePeriodicSync

    /**
     * Handles some things that need to be done after an account has been created.
     *
     * More specifically, after an account is created,
     * a periodic sync should be configured, enabled, and started.
     *
     * @param newAccount The newly-created {@link Account}.
     * @param context The {@link Context} where this method is running.
     * */
    // begin method onAccountCreated
    private static void onAccountCreated( Account newAccount, Context context ) {

        // 0. configure the periodic sync
        // 1. enable the periodic sync
        // 2. kick off a sync to get things started

        // 0. configure the periodic sync

        SunshineSyncAdapter.configurePeriodicSync( context, SYNC_INTERVAL, SYNC_FLEXTIME );

        // 1. enable the periodic sync

        // setSyncAutomatically - Set whether or not the provider is synced
        //  when it receives a network tickle.
        // a tickle tells the app that there is some new data.
        // the app then decides whether or not to fetch that data.
        // http://android-developers.blogspot.co.ke/2010/05/android-cloud-to-device-messaging.html
        ContentResolver.setSyncAutomatically( newAccount,
                context.getString( R.string.content_authority ), true );

        // 2. kick off a sync to get things started

        SunshineSyncAdapter.syncImmediately( context );

    } // end method onAccountCreated

    /**
     * Helper method to initialize the sync adapter.
     *
     * @param context The {@link Context} we're working in
     * */
    // method initializeSyncAdapter
    public static void initializeSyncAdapter( Context context ) { getSyncAccount( context ); }

    /* non statics */

    /** Helper method to put the fetch weather code in one place. */
    // begin method fetchWeather
    private void fetchWeather() {

        // no point in starting if there is no zip code to work with

        String locationQuery = Utility.getPreferredLocation( getContext() );
        if (  locationQuery == null ) { return; }

        // 0. null initialize
        // 0a. http connection
        // 0b. the buffered reader
        // 0c. the string that will store the received forecast in JSON form
        // 1. construct the url to fetch
        // 2. create a http get request to the url
        // 3. connect to the url
        // 4. buffer read from the url
        // 5. store the read JSON in a string
        // 6. return a string of the resultant forecasts

        // 0. null initialize

        // 0a. http connection

        // 0b. the buffered reader

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        // 0c. the string that will store the received forecast in JSON form

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        try {

            // 1. construct the url to fetch

            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            final String
                    FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?",
                    QUERY_PARAMETER = "q", // use q to mean we will use the postal code to find the city; if the city has no postal code - Nairobi does not - then use id and pass the city's OWM id as the parameter - Nairobi's is 184745
                    FORMAT_PARAMETER = "mode",
                    UNITS_PARAMETER = "units",
                    DAYS_PARAMETER = "cnt", // this was "days" but "days" only returned 7 days worth of forecast. "cnt" gets us the whole 14. :-)
                    ID_PARAMETER = "appid";

            Uri builtUri =
                    Uri.parse( FORECAST_BASE_URL ).buildUpon()
                            .appendQueryParameter( QUERY_PARAMETER,locationQuery )
                            .appendQueryParameter( FORMAT_PARAMETER, format )
                            .appendQueryParameter( UNITS_PARAMETER, units )
                            .appendQueryParameter( DAYS_PARAMETER, String.valueOf( numDays ) )
                            .appendQueryParameter( ID_PARAMETER, BuildConfig.OPEN_WEATHER_MAP_API_KEY )
                            .build();

            URL url = new URL( builtUri.toString() );

            urlConnection = ( HttpURLConnection ) url.openConnection();
            urlConnection.setRequestMethod( "GET" );

            // 3. connect to the url

            urlConnection.connect();

            // 4. buffer read from the url

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if ( inputStream == null ) {
                // Nothing to do.
                return;
            }
            bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );

            String line;
            while ( ( line = bufferedReader.readLine() ) != null ) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                stringBuffer.append( line + "\n" );
            }

            if ( stringBuffer.length() == 0 ) {
                // Stream was empty.  No point in parsing.
                return;
            }

            // 5. store the read JSON in a string

            forecastJsonStr = stringBuffer.toString();

            // 6. return a string of the resultant forecasts

            getWeatherDataFromJson( forecastJsonStr, locationQuery );

        } catch ( IOException e ) {
            Log.e( LOG_TAG, "Error ", e );
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return;
        }

        // catch JSON issues
        catch ( JSONException e ) {

            // 0. log the error
            // 1. terminate

            // 0. log the error

            Log.e( LOG_TAG, "Error ", e );

            // 1. terminate

            return;

        } finally{
            if ( urlConnection != null ) { urlConnection.disconnect(); }

            if ( bufferedReader != null ) {
                try { bufferedReader.close(); }

                catch ( final IOException e ) { Log.e( LOG_TAG, "Error closing stream", e ); }
            }
        }

        return; // only when there is an error getting or parsing the forecast

    } // end method fetchWeather

    /**
     * Takes the String representing the complete forecast in JSON form and
     * gets weather data we need to construct the Strings needed for the wireframes.
     */
    // begin method getWeatherDataFromJson
    private void getWeatherDataFromJson( String forecastJSONString,
                                         String locationSetting )
            throws JSONException {

        // 0. initialize the names of the JSON objects we need to extract
        // 0a. location information
        // 0b. location coordinates
        // 0c. weather information
        // 1. extract the data from the JSON objects for location
        // 1a. location information
        // 1b. location coordinates
        // 2. insert the location information to the db and get the row ID
        // 3. initialize the ContentValues vector where we will put the weather data
        // 4. get the weather JSON array
        // 5. for each weather JSON item in the weather JSON array
        // 5a. get the relevant weather information
        // 5b. put the information in a ContentValues
        // 5c. put the ContentValues in the vector made in step 3
        // 6. if the vector has something,
        // 6a. bulk insert to add the weather entries in the vector to the db
        // 6b. delete any day-old data
        // 6c. send a notification of the current weather

        // 0. initialize the names of the JSON objects we need to extract

        // the JSON looks like this:
        //
        // "city":{
        //      "id":5375480,
        //      "name":"Mountain View",
        //      "coord":{
        //          "lon":-122.083847,
        //          "lat":37.386051
        //      },
        //      "country":"US",
        //      "population":0
        //      },
        //  "list":[
        //      {
        //          "dt":1470254400,
        //          "temp":{
        //              "day":13.3,
        //              "min":13.3,
        //              "max":13.3,
        //              "night":13.3,
        //              "eve":13.3,
        //              "morn":13.3
        //          },
        //          "pressure":988.69,
        //          "humidity":97,
        //          "weather":[
        //              {
        //                  "id":500,
        //                  "main":"Rain",
        //                  "description":"light rain",
        //                  "icon":"10n"
        //              }
        //          ],
        //          "speed":0.81,
        //          "deg":183,
        //          "clouds":0
        //      },

        // "city":{
        //      "id":5375480,
        //      "name":"Mountain View",
        //      "coord":{
        //          "lon":-122.083847,
        //          "lat":37.386051
        //      },
        //      "country":"US",
        //      "population":0
        //      },

        // 0a. location information

        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";

        // 0b. location coordinates

        final String OWM_COORD = "coord";
        final String OWM_LONGITUDE = "lon";
        final String OWM_LATITUDE = "lat";

        //  "list":[
        //      {
        //          "dt":1470254400,
        //          "temp":{
        //              "day":13.3,
        //              "min":13.3,
        //              "max":13.3,
        //              "night":13.3,
        //              "eve":13.3,
        //              "morn":13.3
        //          },
        //          "pressure":988.69,
        //          "humidity":97,
        //          "weather":[
        //              {
        //                  "id":500,
        //                  "main":"Rain",
        //                  "description":"light rain",
        //                  "icon":"10n"
        //              }
        //          ],
        //          "speed":0.81,
        //          "deg":183,
        //          "clouds":0
        //      },

        // 0c. weather information

        final String OWM_LIST = "list";

        final String OWM_TEMPERATURE = "temp";
        final String OWM_MIN = "min";
        final String OWM_MAX = "max";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WIND_SPEED = "speed";
        final String OWM_WIND_DIRECTION_DEGREES = "deg";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        // 1. extract the data from the JSON objects for location

        JSONObject forecastJsonObject = null;

        // begin trying to work on the JSON
        try {

            // 1a. location information

            forecastJsonObject = new JSONObject( forecastJSONString );

            JSONObject cityJsonObject = forecastJsonObject.getJSONObject( OWM_CITY );

            String cityName = cityJsonObject.getString( OWM_CITY_NAME );

            // 1b. location coordinates

            JSONObject coordinatesJsonObject = cityJsonObject.getJSONObject( OWM_COORD );

            double cityLongitude = coordinatesJsonObject.getDouble( OWM_LONGITUDE );
            double cityLatitude = coordinatesJsonObject.getDouble( OWM_LATITUDE );

            // 2. insert the location information to the db and get the row ID

            long locationRowId = addLocation( locationSetting, cityName, cityLatitude, cityLongitude );

            // 3. initialize the ContentValues vector where we will put the weather data

            // 4. get the weather JSON array

            JSONArray weatherJsonArray = forecastJsonObject.getJSONArray( OWM_LIST );

            Vector< ContentValues > weatherValuesVector = new Vector<>( weatherJsonArray.length() );

            // 5. for each weather JSON item in the weather JSON array

            // begin for through the weather items in the JSON array
            for ( int i = 0; i < weatherJsonArray.length(); i++ ) {

                JSONObject currentWeatherJsonObject = weatherJsonArray.getJSONObject( i );

                // 5a. get the relevant weather information

//                final String OWM_TEMPERATURE = "temp";
//                final String OWM_MIN = "min";
//                final String OWM_MAX = "max";
//
//                final String OWM_PRESSURE = "pressure";
//                final String OWM_HUMIDITY = "humidity";
//                final String OWM_WIND_SPEED = "speed";
//                final String OWM_WIND_DIRECTION_DEGREES = "deg";
//
//                final String OWM_WEATHER = "weather";
//                final String OWM_DESCRIPTION = "main";
//                final String OWM_WEATHER_ID = "id";

                JSONObject temperatureJsonObject =
                        currentWeatherJsonObject.getJSONObject( OWM_TEMPERATURE );

                double minTemperature = temperatureJsonObject.getDouble( OWM_MIN );
                double maxTemperature = temperatureJsonObject.getDouble( OWM_MAX );

                double pressure = currentWeatherJsonObject.getDouble( OWM_PRESSURE );
                double humidity = currentWeatherJsonObject.getDouble( OWM_HUMIDITY );
                double windSpeed = currentWeatherJsonObject.getDouble( OWM_WIND_SPEED );
                double windDirectionDegrees =
                        currentWeatherJsonObject.getDouble( OWM_WIND_DIRECTION_DEGREES );

                JSONArray currentWeatherJsonArray = currentWeatherJsonObject.getJSONArray( OWM_WEATHER );

                JSONObject weatherJsonObject = currentWeatherJsonArray.getJSONObject( 0 );

                String description = weatherJsonObject.getString( OWM_DESCRIPTION );

                int weatherId = weatherJsonObject.getInt( OWM_WEATHER_ID );

                GregorianCalendar currentDateCalendar = new GregorianCalendar();

                currentDateCalendar.add( Calendar.DATE, i );

                long dateTime = currentDateCalendar.getTimeInMillis();

                // 5b. put the information in a ContentValues

                ContentValues contentValues = new ContentValues();

                contentValues.put( WeatherEntry.COLUMN_LOCATION_KEY, locationRowId );
                contentValues.put( WeatherEntry.COLUMN_MIN_TEMP, minTemperature );
                contentValues.put( WeatherEntry.COLUMN_MAX_TEMP, maxTemperature );
                contentValues.put( WeatherEntry.COLUMN_PRESSURE, pressure );
                contentValues.put( WeatherEntry.COLUMN_HUMIDITY, humidity );
                contentValues.put( WeatherEntry.COLUMN_WIND_SPEED, windSpeed );
                contentValues.put( WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES, windDirectionDegrees );
                contentValues.put( WeatherEntry.COLUMN_SHORT_DESCRIPTION, description );
                contentValues.put( WeatherEntry.COLUMN_WEATHER_ID, weatherId );
                contentValues.put( WeatherEntry.COLUMN_DATE, dateTime );

                // 5c. put the ContentValues in the vector made in step 3

                weatherValuesVector.add( contentValues );

            } // end for through the weather items in the JSON array

            // 6. if the vector has something,

            // 6a. bulk insert to add the weather entries in the vector to the db

            int numberOfInserts = 0;

            // begin if the weather values vector has something
            if ( weatherValuesVector.size() > 0 ) {

                ContentValues weatherContentValues[] =
                        new ContentValues[ weatherValuesVector.size() ];
                weatherValuesVector.toArray( weatherContentValues );

                numberOfInserts = getContext().getContentResolver().bulkInsert(
                        WeatherEntry.CONTENT_URI, weatherContentValues
                );

                // 6b. delete any day-old data

                deleteOldData();

                // 6c. send a notification of the current weather

                notifyWeather();

            } // end if the weather values vector has something

            Log.d( LOG_TAG,
                    "FetchWeatherTask completed successfully. " + numberOfInserts +
                            " added to the database." );

        } // end trying to work on the JSON

        // catch JSON issues
        catch ( JSONException e ) {
            Log.e( LOG_TAG, e.getMessage(), e );
            e.printStackTrace();
        }

    } // end method getWeatherDataFromJson

    /**
     * Helper method to handle insertion of a new location in the weather db.
     *
     * @param locationSetting The location string used to request updates from server
     * @param cityName A human-readable city name, e.g. "Mountain View"
     * @param cityLatitude The latitude of the city
     * @param cityLongitude The longitude of the city
     * @return the row ID of the added location
     *
     * */
    // begin method addLocation
    public long addLocation( String locationSetting, String cityName,
                             double cityLatitude, double cityLongitude ) {

        // 0. is the location having the city name in the db
        // 0a. if yes, return its ID
        // 0alast. free the cursor
        // 0b. if no, insert it using the content resolver and the base URI
        // 0blast. free the cursor

        // 0. is the location having the city name in the db

        Cursor locationCursor = getContext().getContentResolver().query(
                LocationEntry.CONTENT_URI,
                new String[] { LocationEntry._ID },
                LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ locationSetting },
                null
        );

        // 0a. if yes, return its ID

        // begin if there is a result in the cursor
        if ( locationCursor.moveToFirst() == true ) {

            int idIndex = locationCursor.getColumnIndex( LocationEntry._ID );

            long locationRowId = locationCursor.getLong( idIndex );

            // 0alast. free the cursor

            locationCursor.close();

            return locationRowId;

        } // end if there is a result in the cursor

        // 0b. if no, insert it using the content resolver and the base URI

        // begin else there is no result in the cursor
        else {

            ContentValues locationContentValues = new ContentValues();
            locationContentValues.put( LocationEntry.COLUMN_LOCATION_SETTING, locationSetting );
            locationContentValues.put( LocationEntry.COLUMN_CITY_NAME, cityName );
            locationContentValues.put( LocationEntry.COLUMN_COORD_LATITUDE, cityLatitude );
            locationContentValues.put( LocationEntry.COLUMN_COORD_LONGITUDE, cityLongitude );

            Uri locationUri = getContext().getContentResolver().insert(
                    LocationEntry.CONTENT_URI,
                    locationContentValues
            );

            // 0blast. free the cursor

            locationCursor.close();

            return ContentUris.parseId( locationUri );

        } // end else there is no result in the cursor

    } // end method addLocation

    /**
     * Posts a notification about today's weather.
     *
     * */
    // begin method notifyWeather
    private void notifyWeather() {

        // 0. if we have not shown a notification today and the user is okay with seeing one
        // 0a. connect to db and get a cursor for today
        // 0b. fetch today's data from the db
        // 0c. format the text accordingly
        // 0d. put the needed images
        // 0d1. a small icon to show the weather in the status bar
        // 0d2. a large art to show the weather in the actual notification
        // 0e. put the title to be the app name
        // 0f. build the notification
        // 0f1. Create the Notification using NotificationCompat.builder.
        // 0f2. Create an explicit intent for what the notification should open.
        // 0f3. Create an artificial “backstack” so that when the user clicks the back button,
        // it is clear to Android where the user will go.
        // 0f4. Tell the NotificationManager to show the notification.
        // 0last. put in preferences now as the time when the notification was displayed last

        // 0. if we have not shown a notification today and the user is okay with seeing one

        Context context = getContext();

        // begin if the user prefers to see notifications
        if ( Utility.isEnableNotifications( context ) == true ) {

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences( context );

            String lastNotificationKey = context.getString( R.string.pref_last_notification_key );

            long timeOfLastSync = sharedPreferences.getLong( lastNotificationKey, 0 );

            // begin if the current time is at least a day after the last sync
            // since we show a notification every time we sync, if the last time we synced was at least
            // a day ago, then the last time we showed a notification was at least a day ago.
            if ( System.currentTimeMillis() - timeOfLastSync >= DAY_IN_MILLIS ) {

                // 0a. connect to db and get a cursor for today
                // 0b. fetch today's data from the db
                // 0c. format the text accordingly

                // 0a. connect to db and get a cursor for today

                String locationSetting = Utility.getPreferredLocation( context );

                Cursor todayCursor = context.getContentResolver().query(
                        WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                                locationSetting, System.currentTimeMillis() ),
                        NOTIFY_WEATHER_PROJECTION, null, null, null );

                // 0b. fetch today's data from the db

                // begin if there is a cursor and it has a row
                if ( todayCursor != null && todayCursor.moveToFirst() == true ) {

                    int todayWeatherConditionId = todayCursor.getInt( COLUMN_WEATHER_CONDITION_ID );

                    float todayHigh = todayCursor.getFloat( COLUMN_WEATHER_MAX_TEMP );

                    float todayLow = todayCursor.getFloat( COLUMN_WEATHER_MIN_TEMP );

                    String todayWeatherDescription = todayCursor.getString( COLUMN_WEATHER_SHORT_DESCRIPTION );

                    // 0c. format the text accordingly

                    boolean isMetric = Utility.isMetric( context );

                    String notificationText = context.getString( R.string.format_notification,
                            todayWeatherDescription,
                            Utility.formatTemperature( context, todayHigh, isMetric ),
                            Utility.formatTemperature( context, todayLow, isMetric ) );

                    // 0d. put the needed images

                    // 0d1. a small icon to show the weather in the status bar

                    int todayIconId = Utility.getIconResourceForWeatherCondition(
                            todayWeatherConditionId );

                    // 0d2. a large art to show the weather in the actual notification

                    Bitmap todayArt = BitmapFactory.decodeResource(
                            context.getResources(),
                            Utility.getArtResourceForWeatherCondition( todayWeatherConditionId )
                    );

                    // 0e. put the title to be the app name

                    String notificationTitle = context.getString( R.string.app_name );

                    // 0f. build the notification

                    // 0f1. Create the Notification using NotificationCompat.builder.

                    NotificationCompat.Builder builder = new NotificationCompat.Builder( context )
                            .setSmallIcon( todayIconId )
                            .setLargeIcon( todayArt )
                            .setContentTitle( notificationTitle )
                            .setContentText( notificationText );

                    // 0f2. Create an explicit intent for what the notification should open.

                    Intent mainActivityIntent = new Intent( context, MainActivity.class );

                    // 0f3. Create an artificial “backstack” so that when the user clicks the back button,
                    // it is clear to Android where the user will go.

                    // TaskStackBuilder - Utility class for constructing synthetic back stacks
                    //  for cross-task navigation
                    TaskStackBuilder backStackBuilder = TaskStackBuilder.create( context )
                            // addParentStack - Add the activity parent chain as specified by manifest
                            //  <meta-data> elements to the task stack builder.
                            .addParentStack( MainActivity.class )
                            // addNextIntent - Add a new Intent to the task stack.
                            //  Most recent one gets invoked first.
                            .addNextIntent( mainActivityIntent );

                    // getPendingIntent - Obtain a PendingIntent for launching the task constructed by
                    //  this builder so far.
                    // FLAG_UPDATE_CURRENT - Flag indicating that if the described PendingIntent already
                    //  exists, then keep it but replace its extra data with what is in this new Intent.
                    PendingIntent notificationPendingIntent =
                            backStackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

                    builder.setContentIntent( notificationPendingIntent );

                    // 0f4. Tell the NotificationManager to show the notification.

                    mNotificationManager.notify( WEATHER_NOTIFICATION_ID, builder.build() );

                    // 0last. put in preferences now as the time when the notification was displayed last

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putLong( lastNotificationKey, System.currentTimeMillis() );

                    editor.apply();

                } // end if there is a cursor and it has a row

            } // end if the current time is at least a day after the last sync

        } // end if the user prefers to see notifications

    } // end method notifyWeather

    /** Deletes weather data that is more than a day old. */
    // begin method deleteOldData
    private void deleteOldData() {

        // 0. get the date for yesterday in milliseconds
        // 1. delete all rows in the table that have a date smaller than yesterday's

        // 0. get the date for yesterday in milliseconds

        long todayInMillis = Calendar.getInstance().getTimeInMillis();
        long aDayInMillis = 24 * 60 * 60 * 1000;
        long yesterdayInMillis = todayInMillis - aDayInMillis;

        // 1. delete all rows in the table that have a date smaller than today's

        int rowsDeleted = getContext().getContentResolver().delete( WeatherEntry.CONTENT_URI,
                WeatherEntry.COLUMN_DATE + " <= ?",
                new String[]{ String.valueOf( yesterdayInMillis ) } );

        // or
        // Calendar cal = Calendar.getInstance(); //Get's a calendar object with the current time.
//        cal.add(Calendar.DATE, -1); //Signifies yesterday's date
//        String yesterdayDate = WeatherContract.getDbDateString(cal.getTime());
//        getContext().getContentResolver().delete(WeatherEntry.CONTENT_URI,
//                WeatherEntry.COLUMN_DATETEXT + " <= ?",
//                new String[] {yesterdayDate});

    } // end method deleteOldData

    /* INNER CLASSES */

} // end class SunshineSyncAdapter