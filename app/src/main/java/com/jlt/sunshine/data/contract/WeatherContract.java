package com.jlt.sunshine.data.contract;

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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The contract binding the weather database
 * */
// begin class WeatherContract
public class WeatherContract {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    public static final String CONTENT_AUTHORITY = "com.jlt.sunshine.app"; // ditto

    public static final String PATH_WEATHER = "weather"; // path to append to base Uri for the weather
    public static final String PATH_LOCATION = "location"; // path to append to base Uri for the location

    /* Uris */

    /** Get the resultant URI content://com.jlt.sunshine.app */
    public static final Uri BASE_CONTENT_URI = Uri.parse( "content://" + CONTENT_AUTHORITY );

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */

    /* statics */

    /** Normalize the passed date with the beginning of the (UTC) day */
    // begin method normalizeDate
    public static long normalizeDate( long startDate ) {

        // 0. get the Gregorian current date
        // 1. use the start date to set time on the gotten date
        // 2. set to zero the gotten date's day hour, minute, second, and millisecond
        // 3. return the resulting Gregorian date in milliseconds

        // 0. get the Gregorian current date

        GregorianCalendar gregorianCalendar = ( GregorianCalendar ) Calendar.getInstance();

        // 1. use the start date to set time on the gotten date

        gregorianCalendar.setTime( new Date( startDate ) );

        // 2. set to zero the gotten date's day hour, minute, second, and millisecond

        gregorianCalendar.set( Calendar.HOUR_OF_DAY, 0 );
        gregorianCalendar.set( Calendar.MINUTE, 0 );
        gregorianCalendar.set( Calendar.SECOND, 0 );
        gregorianCalendar.set( Calendar.MILLISECOND, 0 );

        // 3. return the resulting Gregorian date in milliseconds

        return gregorianCalendar.getTimeInMillis();

    } // end method normalizeDate

    /* INNER CLASSES */

    /*
    * The contents for the weather table
    * */
    // begin class WeatherEntry
    public static class WeatherEntry implements BaseColumns {

        /* CONSTANTS */

        /* Strings */

        public static final String TABLE_NAME = "weather"; // name of this table

        public static final String COLUMN_LOCATION_KEY = "location_id"; // column with the foreign key pointing to the location table

        public static final String COLUMN_DATE = "date"; // date, stored as long in milliseconds since the epoch

        public static final String COLUMN_WEATHER_ID = "weather_id"; // weather id as returned by the API, used to identify the icon to be displayed

        public static final String COLUMN_SHORT_DESCRIPTION = "short_desc"; // short description of the weather as given by API (For a "sky is cloudy" weather, the short description is "cloudy")

        public static final String COLUMN_MIN_TEMP = "min"; // minimum temperature for the day stored as float

        public static final String COLUMN_MAX_TEMP = "max"; // maximum temperature for the day stored as float

        public static final String COLUMN_HUMIDITY = "humidity"; // day humidity stored as float representing a percentage

        public static final String COLUMN_PRESSURE = "pressure"; // day pressure stored as float representing a percentage

        public static final String COLUMN_WIND_SPEED = "wind"; // day wind speed stored as float representing mph

        public static final String COLUMN_WIND_DIRECTION_DEGREES = "degrees"; // day wind direction stored as float representing meteorological degrees where 0 is north, 180 is south

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        /* Uris */

        /** Get the resultant URI content://com.jlt.sunshine.app/weather */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath( PATH_WEATHER ).build();

        /* VARIABLES */

        /* CONSTRUCTOR */

        /* METHODS */

        /* Getters and Setters */

        /* Overrides */

        /* Other Methods */

        /* statics */

        /** Builds the weather Uri */
        // method buildWeatherUri
        public static Uri buildWeatherUri( long id ) {
            return ContentUris.withAppendedId( CONTENT_URI, id );
        }

        /**
         * Builds a Uri for querying the weather for a location.
         * This looks like:
         *  content://com.jlt.sunshine.app/weather/[locationSetting]
         * */
        // begin method buildWeatherForLocationUri
        public static Uri buildWeatherForLocationUri( String locationSetting ) {

            return CONTENT_URI.buildUpon()
                    .appendPath( locationSetting )
                    .build();

        } // end method buildWeatherForLocationUri

        /**
         * Build a Uri that will get the weather
         * for a given location on a given date.
         * This looks like:
         *  content://com.jlt.sunshine.app/weather/[locationSetting]/[startDate]
         * */
        // begin method buildWeatherForLocationWithSpecificDateUri
        public static Uri buildWeatherForLocationWithSpecificDateUri(
                String locationSetting, long specificDate ) {

            // 0. normalize the start date
            // 1. build the date uri and return it

            // 0. normalize the start date

            long normalizedDate = normalizeDate( specificDate );

            // 1. build the date uri and return it

            return CONTENT_URI.buildUpon()
                    .appendPath( locationSetting )
                    .appendPath( Long.toString( normalizedDate ) )
                    .build();

        } // end method buildWeatherForLocationWithSpecificDateUri

        /**
         * Build a Uri that will get weather
         * for a given location starting from a given date.
         * This looks like:
         *  content://com.jlt.sunshine.app/weather/[locationSetting]?date=[startDate]
         * */
        //  begin method buildWeatherForLocationWithStartDateUri
        public static Uri buildWeatherForLocationWithStartDateUri(
                String locationSetting, long startDate ) {

            // 0. normalize the start date
            // 1. build the start date uri and return it

            // 0. normalize the start date

            long normalizedDate = normalizeDate( startDate );

            // 1. build the start date uri and return it

            return CONTENT_URI.buildUpon()
                        // appendPath - Encodes the given segment and appends it to the path.
                        .appendPath( locationSetting )
                        .appendQueryParameter( COLUMN_DATE, Long.toString( normalizedDate ) )
                        .build();

        } //  end method buildWeatherForLocationWithStartDateUri

        /** Get the location setting from the parameter Uri */
        public static String getLocationSettingFromUri( Uri uri ) {

            // getPathSegments - Gets a list of the decoded path segments
            //      each without a leading or trailing /
            // get - Returns the element at the specified location in this List
            return uri.getPathSegments().get( 1 );

        } // end method getLocationSettingFromUri

        /** Get the date from the passed Uri */
        // method getDateFromUri
        public static long getDateFromUri( Uri uri ) {
            return Long.parseLong( uri.getPathSegments().get( 2 ) );
        }

        /** Get the start date from the passed Uri */
        // begin method getStartDateFromUri
        public static long getStartDateFromUri( Uri uri ) {

            // 0. get the date string from the uri
            // 1. return the date if it is there otherwise return 0

            // 0. get the date string from the uri

            // getQueryParameter - Searches the query string for the first value with the given key.
            String dateString = uri.getQueryParameter( COLUMN_DATE );

            // 1. return the date if it is there otherwise return 0

            if ( dateString != null && dateString.length() > 0 ) { return Long.parseLong( dateString ); }
            else { return 0; }

        } // end method getStartDateFromUri

        /* INNER CLASSES */

    } // end class WeatherEntry

    /*
    * The table contents for the location table
    * */
    // begin class LocationEntry
    public static class LocationEntry implements BaseColumns {

        /* CONSTANTS */

        /* Strings */

        public static final String TABLE_NAME = "location"; // name of this table

        public static final String COLUMN_LOCATION_SETTING = "location_setting"; // column with the location setting to sent to OWM as the location query

        public static final String COLUMN_CITY_NAME = "city_name"; // the city name as a human readable string

        public static final String COLUMN_COORD_LATITUDE = "coord_lat"; // the city's latitude as float

        public static final String COLUMN_COORD_LONGITUDE = "coord_lon"; // the city's longitude as float

        // CURSOR_DIR_BASE_TYPE - The Android platform's base MIME type for a content:
        //      URI containing a Cursor of zero or more items. Applications should use this as
        //      the base type along with their own sub-type of their content:
        //      URIs that represent a directory of items.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // CURSOR_ITEM_BASE_TYPE - The Android platform's base MIME type for a content:
        //      URI containing a Cursor of a single item. Applications should use this as
        //      the base type along with their own sub-type of their content:
        //      URIs that represent a particular item.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        /* Uris */

        /** Get the resultant URI content://com.jlt.sunshine.app/location */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath( PATH_LOCATION ).build();

        /** VARIABLES */

        /** CONSTRUCTOR */

        /** METHODS */

        /** Getters and Setters */

        /** Overrides */

        /** Other Methods */

        /* statics */

        /** Builds the Uri that will get the location referred to by the passed ID. */
        // method buildLocationUri
        public static Uri buildLocationUri( long id ) {
            return ContentUris.withAppendedId( CONTENT_URI, id );
        }

        /** INNER CLASSES */

    } // end class LocationEntry

} // end class WeatherContract