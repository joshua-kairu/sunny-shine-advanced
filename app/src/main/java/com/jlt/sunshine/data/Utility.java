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

package com.jlt.sunshine.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jlt.sunshine.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Class to provide utility methods for preference, date and time use, and connectivity checking.
 * */
// begin class Utility
public class Utility {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /**
     * Format for storing dates in the db and
     * for converting the stored dates back into date objects.
     *
     * yyyy - year in 4 digits e.g. 2014
     * MM - month in year in 2 digits e.g. 07
     * dd - day in month in 2 digits e.g. 24
     * */
    private static final String DATE_FORMAT = "yyyMMdd";

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */

    /* statics */

    /** Gets the preferred location from the settings. */
    // begin method getPreferredLocation
    public static String getPreferredLocation( Context context ) {

        // 0. get the shared preferences
        // 1. return the preferred location, or the default one

        // 0. get the shared preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. return the preferred location, or the default one

        return sharedPreferences
                .getString( context.getResources().getString( R.string.pref_location_key ),
                        context.getResources().getString( R.string.pref_location_default ) );

    } // end method getPreferredLocation

    /** Checks if the preferred units are the metric units. */
    // begin method isMetric
    public static boolean isMetric( Context context ) {

        // 0. get the shared preferences
        // 1. return based on if the preferred units are metric

        // 0. get the shared preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. return based on if the preferred units are metric

        return sharedPreferences
                .getString( context.getResources().getString( R.string.pref_temperature_unit_key ),
                        context.getResources().getString( R.string.pref_temperature_unit_metric_value ) )
                .equals( context.getResources().getString( R.string.pref_temperature_unit_metric_value ) );

    } // end method isMetric

    /**
     * Formats the temperature based on whether the temperature is metric or not.
     * @param temperature   The temperature (whose default units are metric)
     * @param isMetric  If we are to convert the temperature to metric units or not
     * @return Returns a string format of the possibly converted temperature
     * */
    // begin method formatTemperature
    public static String formatTemperature( Context context, float temperature, boolean isMetric ) {

        // 0. if necessary convert the temperature to metric form
        // 1. return the temperature in a formatted string

        // 0. if necessary convert the temperature to metric form

        double temp;

        if ( isMetric == false ) { temp = 9.0 / 5.0 * temperature + 32; }
        else { temp = temperature; }

        // 1. return the temperature in a formatted string

        // %.0f means a format of a floating point number with zero decimal points?
        return context.getString( R.string.format_temperature, temp );

    } // end method formatTemperature

    /** Returns a String of a date that represented by a millisecond value. */
    // begin method formatDate
    public static String formatDate( long dateInMillis ) {

        // 0. get a date based on the milliseconds in the parameter
        // 1. return that date formatted

        // 0. get a date based on the milliseconds in the parameter

        // Initializes this Date instance using the specified millisecond value.
        // The value is the number of milliseconds since Jan. 1, 1970 GMT.
        Date date = new Date( dateInMillis );

        // 1. return that date formatted

        // DateFormat - Formats or parses dates and times.
        // getDateInstance - Returns a DateFormat instance for
        //  formatting and parsing dates
        //  in the DEFAULT style for the default locale.
        return DateFormat.getDateInstance().format( date );

    } // end method formatDate

    /**
     * Helper method to convert the db representation of the date (defined by Utility.DATE_FORMAT)
     * into something friendly to users. "20150724" is not friendly, we are told.
     *
     * @param context Context to use for resource localization.
     * @param dateInMillis The date in milliseconds
     * @return A user-friendly representation of the date.
     * */
    // begin method getFriendlyDateString
    public static String getFriendlyDateString ( Context context, long dateInMillis ) {

        // The logic used is:
        // For today: "Today, September 1"
        // For tomorrow: "Tomorrow"
        // For the next five days: "Saturday"
        // For all days after that: "Thu Sep 1"

        // 0. get the current Gregorian day
        // 0a. and month too
        // 1. get the Gregorian day referred to by dateInMillis
        // 1a. and the month too
        // 2. if the date we're building a string for is today's date
        // 3. if the date we're building for is less than a week to come and is within this month
        // 3a. return the day name
        // 4. otherwise (or if we are in a new month)
        // 4a. use the form "Thu Sep 1"

        // 0. get the current Gregorian day

        GregorianCalendar calendar = new GregorianCalendar();
        int currentDay = calendar.get( Calendar.DAY_OF_MONTH );

        // 0a. and month too

        int currentMonth = calendar.get( Calendar.MONTH );

        // 1. get the Gregorian day referred to by dateInMillis

        calendar.setTimeInMillis( dateInMillis );
        int dateInMillisDay = calendar.get( Calendar.DAY_OF_MONTH );

        Log.e( "getFriendlyDateString: ",
                "current day " + currentDay + "| dateInMillisDay " + dateInMillisDay );

        // 1a. and the month too

        int dateInMillisMonth = calendar.get( Calendar.MONTH );

        // 2. if the date we're building a string for is today's date

        // begin if the day we're building for is today
        if ( dateInMillisDay == currentDay ) {

            // 2a. the format is "Today, September 1"

            String today = context.getString( R.string.today );

            int formatId = R.string.format_full_friendly_date;

            // getString - Returns a localized formatted string from the app's package's
            //  default string table, substituting the format arguments as defined in
            // Formatter and format(String, Object...).
            return context.getString(
                            formatId,
                            today,
                            getFormattedMonthDay( dateInMillis )
                    );

        } // end if the day we're building for is today

        // 3. if the date we're building for is less than a week to come and is within this month

        // 3a. return the day name

        else if ( dateInMillisDay < currentDay + 7 && dateInMillisMonth == currentMonth ) {
            return getDayName( context, dateInMillis );
        }

        // 4. otherwise

        // 4a. use the form "Thu Sep 1"

        // begin else we are further from a week or are in another month
        else {

            // EEE - Day of the week in at most three characters e.g. "Thu"
            // MMM - Month of the year in at most three characters e.g. "Jan"
            // dd - Day of the month in at most two digits e.g. 02
            return new SimpleDateFormat( "EEE MMM dd", Locale.ENGLISH ).format( dateInMillis );

        } // end else we are further from a week or are in another month

    } // end method getFriendlyDateString

    /**
     * Given a day, returns just the name to use for that day.
     * For example, "today", "tomorrow", "Saturday"
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     *
     * @return The corresponding day's name
     * */
    // begin method getDayName
    public static String getDayName( Context context, long dateInMillis ) {

        // 0. get the current day
        // 1. get the day referred to in dateInMillis
        // 2. if the day in dateInMillis is today
        // 2a. return the localized version of "Today"
        // 3. if the day in dateInMillis is tomorrow
        // 3a. return the localized version of "Tomorrow"
        // 4. otherwise
        // 4a. return the day's name, e.g. "Saturday"

        // 0. get the current day

        GregorianCalendar calendar = new GregorianCalendar();
        int currentDay = calendar.get( Calendar.DAY_OF_MONTH );

        // 1. get the day referred to in dateInMillis

        calendar.setTimeInMillis( dateInMillis );
        int dateInMillisDay = calendar.get( Calendar.DAY_OF_MONTH );

        Log.e( "getDayName: ", "current day " + currentDay + "| dateInMillisDay " + dateInMillisDay );

        // 2. if the day in dateInMillis is today

        // 2a. return the localized version of "Today"

        if ( dateInMillisDay == currentDay ) { return context.getString( R.string.today ); }

        // 3. if the day in dateInMillis is tomorrow

        // 3a. return the localized version of "Tomorrow"

        else if ( dateInMillisDay == currentDay + 1 ) {
            return context.getString( R.string.tomorrow );
        }

        // 4. otherwise

        // begin else when the day is further down the week
        else {

            // 4a. return the day's name, e.g. "Saturday"

            // EEEE - day name in week in at least four characters e.g. "Tuesday"
            SimpleDateFormat dayFormat = new SimpleDateFormat( "EEEE", Locale.ENGLISH );

            return dayFormat.format( dateInMillis );

        } // end else when the day is further down the week

    } // end method getDayName

    /**
     * Converts db date format to the format "Month day", e.g. "September 1"
     *
     * @param dateInMillis The date in milliseconds
     * @return The day in the form of a string formatted "September 1"
     * */
    // begin method getFormattedMonthDay
    public static String getFormattedMonthDay ( long dateInMillis ) {

        // 0. get the needed date format
        // 1. return the formatted milliseconds

        // 0. get the needed date format

        // MMMM - month in at least four characters e.g. July
        SimpleDateFormat monthDayFormat = new SimpleDateFormat( "MMMM dd", Locale.ENGLISH );

        // 1. return the formatted milliseconds

        return monthDayFormat.format( new Date( dateInMillis ) );

    } // end method getFormattedMonthDay

    /** Gets the wind speed and direction in a form friendly to the user. */
    // begin method getFormattedWind
    public static String getFormattedWind( Context context,
                                           float windSpeed, float windDirectionDegrees ) {

        // 0. format the wind speed based on preferred units
        // 1. determine the readable wind direction based on the wind direction degrees
        // 2. return the formatted wind text

        // 0. format the wind speed based on preferred units

        int windFormat;

        if ( Utility.isMetric( context ) == true ) { windFormat = R.string.format_wind_kmh; }

        // begin else we are not on metric
        else {

            windFormat = R.string.format_wind_mph;
            windSpeed = .621371192237334f * windSpeed; // convert the wind speed from kmh to mph

        } // end else we are not on metric

        // 1. determine the readable wind direction based on the wind direction degrees

        String direction = "Unknown";

        if ( windDirectionDegrees >= 337.5 || windDirectionDegrees < 22.5 ) { direction = "N"; }

        else if ( windDirectionDegrees >= 22.5 && windDirectionDegrees < 67.5 ) { direction = "NE"; }

        else if ( windDirectionDegrees >= 67.5 && windDirectionDegrees < 112.5 ) { direction = "E"; }

        else if ( windDirectionDegrees >= 112.5 && windDirectionDegrees < 157.5 ) { direction = "SE"; }

        else if ( windDirectionDegrees >= 157.5 && windDirectionDegrees < 202.5 ) { direction = "S"; }

        else if ( windDirectionDegrees >= 202.5 && windDirectionDegrees < 247.5 ) { direction = "SW"; }

        else if ( windDirectionDegrees >= 247.5 && windDirectionDegrees < 292.5 ) { direction = "W"; }

        else if ( windDirectionDegrees >= 292.5 && windDirectionDegrees < 337.5 ) { direction = "NW"; }

        // in the gist https://gist.github.com/anonymous/2ebebb679a56284efc68
        // else if (degrees >= 292.5 || degrees < 22.5) { direction = "NW"; }

        // 2. return the formatted wind text

        return String.format( Locale.ENGLISH,
                context.getString( windFormat ), windSpeed, direction );

    } // end method getFormattedWind

    /** Gets the wind speed in a form friendly to the user. */
    // begin method getFormattedWindSpeed
    public static String getFormattedWindSpeed( Context context, float windSpeed ) {

        // 0. format the wind speed based on preferred units
        // 1. return the formatted wind text

        // 0. format the wind speed based on preferred units

        int windFormatWithoutDirection;

        if ( Utility.isMetric( context ) == true ) {
            windFormatWithoutDirection = R.string.format_wind_kmh_sans_direction;
        }

        // begin else we are not on metric
        else {

            windFormatWithoutDirection = R.string.format_wind_mph_sans_direction;
            windSpeed = .621371192237334f * windSpeed; // convert the wind speed from kmh to mph

        } // end else we are not on metric

        // 1. return the formatted wind text

        return String.format( Locale.ENGLISH,
                context.getString( windFormatWithoutDirection ), windSpeed );

    } // end method getFormattedWind

    /**
     * Helper method to provide the icon resource id according to the weather condition id
     * returned by the OpenWeatherMap call.
     *
     * @param weatherConditionId The weatherId attribute from the OpenWeatherMap API response
     * @return The resource id for the corresponding icon, or -1 if no relation is found.
     * */
    // begin method getIconResourceForWeatherCondition
    public static int getIconResourceForWeatherCondition( int weatherConditionId ) {

        // 0. return corresponding weather condition icon or -1 if none is found

        // 0. return corresponding weather condition icon or -1 if none is found

        if ( weatherConditionId >= 200 && weatherConditionId <= 232 ) {
            return R.drawable.ic_storm;
        }

        else if ( weatherConditionId >= 300 && weatherConditionId <= 321 ) {
            return R.drawable.ic_light_rain;
        }

        else if ( weatherConditionId >= 500 && weatherConditionId <= 504 ) {
            return R.drawable.ic_rain;
        }

        else if ( weatherConditionId == 511 ) {
            return R.drawable.ic_snow;
        }

        else if ( weatherConditionId >= 520 && weatherConditionId <= 531 ) {
            return R.drawable.ic_rain;
        }

        else if ( weatherConditionId >= 600 && weatherConditionId <= 622 ) {
            return R.drawable.ic_snow;
        }

        else if ( weatherConditionId >= 701 && weatherConditionId <= 761 ) {
            return R.drawable.ic_fog;
        }

//      in the gist there is
//        else if (weatherId == 761 || weatherId == 781) {
//            return R.drawable.ic_storm;
//        }
//        but http://openweathermap.org/weather-conditions says
//        761 is dust and 781 is tornado

        else if ( weatherConditionId == 781 ) {
            return R.drawable.ic_storm;
        }

        else if ( weatherConditionId == 800 ) {
            return R.drawable.ic_clear;
        }

        else if ( weatherConditionId == 801 ) {
            return R.drawable.ic_light_clouds;
        }

        else if ( weatherConditionId >= 802 && weatherConditionId <= 804 ) {
            return R.drawable.ic_cloudy;
        }
        
        return -1; // otherwise

    } // end method getIconResourceForWeatherCondition

    /**
     * Helper method to provide the art resource id according to the weather condition id
     * returned by the OpenWeatherMap call.
     *
     * @param weatherConditionId The weatherId attribute from the OpenWeatherMap API response
     * @return The resource id for the corresponding image, or -1 if no relation is found.
     * */
    // begin method getArtResourceForWeatherCondition
    public static int getArtResourceForWeatherCondition( int weatherConditionId ) {

        // 0. return corresponding weather condition icon or -1 if none is found

        // 0. return corresponding weather condition icon or -1 if none is found

        if ( weatherConditionId >= 200 && weatherConditionId <= 232 ) {
            return R.drawable.art_storm;
        }

        else if ( weatherConditionId >= 300 && weatherConditionId <= 321 ) {
            return R.drawable.art_light_rain;
        }

        else if ( weatherConditionId >= 500 && weatherConditionId <= 504 ) {
            return R.drawable.art_rain;
        }

        else if ( weatherConditionId == 511 ) {
            return R.drawable.art_snow;
        }

        else if ( weatherConditionId >= 520 && weatherConditionId <= 531 ) {
            return R.drawable.art_rain;
        }

        else if ( weatherConditionId >= 600 && weatherConditionId <= 622 ) {
            return R.drawable.art_snow;
        }

        else if ( weatherConditionId >= 701 && weatherConditionId <= 761 ) {
            return R.drawable.art_fog;
        }

//      in the gist there is
//        else if (weatherId == 761 || weatherId == 781) {
//            return R.drawable.art_storm;
//        }
//        but http://openweathermap.org/weather-conditions says
//        761 is dust and 781 is tornado

        else if ( weatherConditionId == 781 ) {
            return R.drawable.art_storm;
        }

        else if ( weatherConditionId == 800 ) {
            return R.drawable.art_clear;
        }

        else if ( weatherConditionId == 801 ) {
            return R.drawable.art_light_clouds;
        }

        else if ( weatherConditionId >= 802 && weatherConditionId <= 804 ) {
            // in the gist, this is R.drawable.art_clouds
            // but we have renamed the resource to match
            // the resource returned in this section of getIconResourceForWeatherCondition
            return R.drawable.art_cloudy;
        }
        
        return -1; // otherwise

    } // end method getArtResourceForWeatherCondition

    /** Formats the wind direction and speed in a way friendly to the user. */
    // begin method getFormattedWindDirectionAndSpeed
    public static String getFormattedWindDirectionAndSpeed( Context context, float windDirectionDegrees,
                                                            float windSpeed ) {

        // 0. determine the readable wind direction based on the wind direction degrees
        // 1. format the wind speed based on preferred units
        // 2. return the formatted wind text

        // 0. determine the readable wind direction based on the wind direction degrees

        String direction = "Unknown";

        if ( windDirectionDegrees >= 337.5 || windDirectionDegrees < 22.5 ) { direction = "N"; }

        else if ( windDirectionDegrees >= 22.5 && windDirectionDegrees < 67.5 ) { direction = "NE"; }

        else if ( windDirectionDegrees >= 67.5 && windDirectionDegrees < 112.5 ) { direction = "E"; }

        else if ( windDirectionDegrees >= 112.5 && windDirectionDegrees < 157.5 ) { direction = "SE"; }

        else if ( windDirectionDegrees >= 157.5 && windDirectionDegrees < 202.5 ) { direction = "S"; }

        else if ( windDirectionDegrees >= 202.5 && windDirectionDegrees < 247.5 ) { direction = "SW"; }

        else if ( windDirectionDegrees >= 247.5 && windDirectionDegrees < 292.5 ) { direction = "W"; }

        else if ( windDirectionDegrees >= 292.5 && windDirectionDegrees < 337.5 ) { direction = "NW"; }

        // 1. format the wind speed based on preferred units

        int windFormat = -1;

        if ( Utility.isMetric( context ) == true ) {
            windFormat = R.string.format_wind_direction_speed_kmh;
        }

        // begin else we are not on metric
        else {

            windFormat = R.string.format_wind_direction_speed_mph;
            windSpeed = .621371192237334f * windSpeed; // convert the wind speed from kmh to mph

        } // end else we are not on metric

        // 2. return the formatted wind text

        return context.getString( windFormat, direction, windSpeed );

    } // end method getFormattedWindDirectionAndSpeed

    /**
     * Tells if the user wants to see notifications.
     *
     * @param context The {@link Context} we are working in.
     *
     * @return A boolean value dependent on whether or not the user wants to see notifications.
     * */
    // begin method isEnableNotifications
    public static boolean isEnableNotifications ( Context context ) {

        // 0. get the preferences
        // 1. determine if we notifications are enabled or not

        // 0. get the preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. determine if we notifications are enabled or not

        return
                Boolean.parseBoolean( sharedPreferences.getString(
                        context.getString( R.string.pref_enable_notifications_key ),
                        context.getString( R.string.pref_enable_notifications_default_value ) ) );

    } // end method isEnableNotifications

    /**
     * Checks if the device has an active internet connection.
     *
     * @param context The {@link Context} we are working in.
     *
     * @return A boolean value dependent on whether or not
     *         the device has an active internet connection.
     * */
    // begin method isNetworkAvailable
    public static boolean isNetworkAvailable( Context context ) {

        // 0. get the connectivity manager
        // 1. get the current active network's information
        // 2. There is connectivity if the active network is available and
        // is either connected or connecting

        // 0. get the connectivity manager

        ConnectivityManager connectivityManager = ( ConnectivityManager )
                context.getSystemService( Context.CONNECTIVITY_SERVICE );

        // 1. get the current active network's information

        // getActiveNetworkInfo - Returns details about the currently active default data network.
        //  May return null when there is no default network.
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // 2. There is connectivity if the active network is available and
        // is either connected or connecting

        return activeNetworkInfo != null &&
                activeNetworkInfo.isConnectedOrConnecting() == true;

    } // end method isNetworkAvailable

    /* INNER CLASSES */

} // end class Utility