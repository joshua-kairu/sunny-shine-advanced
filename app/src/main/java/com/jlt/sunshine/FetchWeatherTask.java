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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import static com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;

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

        // no point in starting if there is no zip code to work with
        if ( params.length == 0 ) { return null; }

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
//            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,USA&mode=json&units=metric&cnt=7&appid=<appid>");

            final String
                    FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?",
                    QUERY_PARAMETER = "q", // use q to mean we will use the postal code to find the city; if the city has no postal code - Nairobi does not - then use id and pass the city's OWM id as the parameter - Nairobi's is 184745
                    FORMAT_PARAMETER = "mode",
                    UNITS_PARAMETER = "units",
                    DAYS_PARAMETER = "cnt", // this was "days" but "days" only returned 7 days worth of forecast. "cnt" gets us the whole 14. :-)
                    ID_PARAMETER = "appid";

            Uri builtUri =
                    Uri.parse( FORECAST_BASE_URL ).buildUpon()
                            .appendQueryParameter( QUERY_PARAMETER, params[ 0 ] )
                            .appendQueryParameter( FORMAT_PARAMETER, format )
                            .appendQueryParameter( UNITS_PARAMETER, units )
                            .appendQueryParameter( DAYS_PARAMETER, String.valueOf( numDays ) )
                            .appendQueryParameter( ID_PARAMETER, BuildConfig.OPEN_WEATHER_MAP_API_KEY )
                            .build();

            URL url = new URL( builtUri.toString() );

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // 3. connect to the url

            urlConnection.connect();

            // 4. buffer read from the url

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            // 5. store the read JSON in a string

            forecastJsonStr = stringBuffer.toString();

            // 6. return a string of the resultant forecasts

            String locationQuery = params[ 0 ];
            getWeatherDataFromJson( forecastJsonStr, locationQuery );

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        }

        // catch JSON issues
        catch ( JSONException e ) {

            // 0. log the error
            // 1. terminate

            // 0. log the error

            Log.e( LOG_TAG, "Error ", e );

            // 1. terminate

            return null;

        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null; // only when there is an error getting or parsing the forecast

    } // end doInBackground

    /** Other Methods */

    // begin method getWeatherDataFromJson
    /**
     * Takes the String representing the complete forecast in JSON form and
     * gets weather data we need to construct the Strings needed for the wireframes.
     */
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
                numberOfInserts = mHostContext.getContentResolver().bulkInsert(
                        WeatherEntry.CONTENT_URI, weatherContentValues );

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
        // 0b. if no, insert it using the content resolver and the base URI

        // 0. is the location having the city name in the db

        Cursor locationCursor = mHostContext.getContentResolver().query(
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
            return locationCursor.getLong( idIndex );

        } // end if there is a result in the cursor

        // 0b. if no, insert it using the content resolver and the base URI

        // begin else there is no result in the cursor
        else {

            ContentValues locationContentValues = new ContentValues();
            locationContentValues.put( LocationEntry.COLUMN_LOCATION_SETTING, locationSetting );
            locationContentValues.put( LocationEntry.COLUMN_CITY_NAME, cityName );
            locationContentValues.put( LocationEntry.COLUMN_COORD_LATITUDE, cityLatitude );
            locationContentValues.put( LocationEntry.COLUMN_COORD_LONGITUDE, cityLongitude );

            Uri locationUri = mHostContext.getContentResolver().insert(
                    LocationEntry.CONTENT_URI,
                    locationContentValues
            );

            return ContentUris.parseId( locationUri );

        } // end else there is no result in the cursor

    } // end method addLocation

} // end class FetchWeatherTask
