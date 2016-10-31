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
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;

import com.jlt.sunshine.R;
import com.jlt.sunshine.sync.SunshineSyncAdapter;

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

    private static final float DEFAULT_LAT_LONG = 0f; // ditto

    /* Strings */

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
     * @return Returns a string format of the possibly converted temperature
     * */
    // begin method formatTemperature
    public static String formatTemperature( Context context, float temperature ) {

        // 0. if necessary convert the temperature to metric form
        // 1. return the temperature in a formatted string

        // 0. if necessary convert the temperature to metric form

        double temp;

        if ( Utility.isMetric( context ) == false ) { temp = 9.0 / 5.0 * temperature + 32; }
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
    static String getFriendlyDateString ( Context context, long dateInMillis ) {

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
    private static String getDayName( Context context, long dateInMillis ) {

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
    private static String getFormattedMonthDay ( long dateInMillis ) {

        // 0. get the needed date format
        // 1. return the formatted milliseconds

        // 0. get the needed date format

        // MMMM - month in at least four characters e.g. July
        SimpleDateFormat monthDayFormat = new SimpleDateFormat( "MMMM dd", Locale.ENGLISH );

        // 1. return the formatted milliseconds

        return monthDayFormat.format( new Date( dateInMillis ) );

    } // end method getFormattedMonthDay

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    // begin getFullFriendlyDayString
    public static String getFullFriendlyDayString( Context context, long dateInMillis ) {

        // 0. get the day
        // 1. return a formatted day string based on the day and the month

        // 0. get the day

        String day = getDayName( context, dateInMillis );

        // 1. return a formatted day string based on the day and the month

        int formatId = R.string.format_full_friendly_date;

        return context.getString( formatId, day, getFormattedMonthDay( dateInMillis ) );

    } // end getFullFriendlyDayString

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

    /**
     * Helper method to provide the string corresponding to the weather condition id
     * returned by the OpenWeatherMap call.
     *
     * @param context The Android {@link Context}
     * @param weatherId The weather condition ID returned by OpenWeatherMap
     *
     * @return A string representing the weather condition, or null if no relation was found.
     * */
    // begin method getStringForWeatherCondition
    public static String getStringForWeatherCondition( Context context, int weatherId ) {

        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes

        // 0. return a string based on the weather id, or null if none is suitable

        // 0. return a string based on the weather id, or null if none is suitable

        int stringId;
        if ( weatherId >= 200 && weatherId <= 232 ) {
            stringId = R.string.condition_2xx;
        }

        else if ( weatherId >= 300 && weatherId <= 321 ) {
            stringId = R.string.condition_3xx;
        }
        else
            // begin switching the weather id
            switch ( weatherId ) {
            case 500:
                stringId = R.string.condition_500;
                break;
            case 501:
                stringId = R.string.condition_501;
                break;
            case 502:
                stringId = R.string.condition_502;
                break;
            case 503:
                stringId = R.string.condition_503;
                break;
            case 504:
                stringId = R.string.condition_504;
                break;
            case 511:
                stringId = R.string.condition_511;
                break;
            case 520:
                stringId = R.string.condition_520;
                break;
            case 531:
                stringId = R.string.condition_531;
                break;
            case 600:
                stringId = R.string.condition_600;
                break;
            case 601:
                stringId = R.string.condition_601;
                break;
            case 602:
                stringId = R.string.condition_602;
                break;
            case 611:
                stringId = R.string.condition_611;
                break;
            case 612:
                stringId = R.string.condition_612;
                break;
            case 615:
                stringId = R.string.condition_615;
                break;
            case 616:
                stringId = R.string.condition_616;
                break;
            case 620:
                stringId = R.string.condition_620;
                break;
            case 621:
                stringId = R.string.condition_621;
                break;
            case 622:
                stringId = R.string.condition_622;
                break;
            case 701:
                stringId = R.string.condition_701;
                break;
            case 711:
                stringId = R.string.condition_711;
                break;
            case 721:
                stringId = R.string.condition_721;
                break;
            case 731:
                stringId = R.string.condition_731;
                break;
            case 741:
                stringId = R.string.condition_741;
                break;
            case 751:
                stringId = R.string.condition_751;
                break;
            case 761:
                stringId = R.string.condition_761;
                break;
            case 762:
                stringId = R.string.condition_762;
                break;
            case 771:
                stringId = R.string.condition_771;
                break;
            case 781:
                stringId = R.string.condition_781;
                break;
            case 800:
                stringId = R.string.condition_800;
                break;
            case 801:
                stringId = R.string.condition_801;
                break;
            case 802:
                stringId = R.string.condition_802;
                break;
            case 803:
                stringId = R.string.condition_803;
                break;
            case 804:
                stringId = R.string.condition_804;
                break;
            case 900:
                stringId = R.string.condition_900;
                break;
            case 901:
                stringId = R.string.condition_901;
                break;
            case 902:
                stringId = R.string.condition_902;
                break;
            case 903:
                stringId = R.string.condition_903;
                break;
            case 904:
                stringId = R.string.condition_904;
                break;
            case 905:
                stringId = R.string.condition_905;
                break;
            case 906:
                stringId = R.string.condition_906;
                break;
            case 951:
                stringId = R.string.condition_951;
                break;
            case 952:
                stringId = R.string.condition_952;
                break;
            case 953:
                stringId = R.string.condition_953;
                break;
            case 954:
                stringId = R.string.condition_954;
                break;
            case 955:
                stringId = R.string.condition_955;
                break;
            case 956:
                stringId = R.string.condition_956;
                break;
            case 957:
                stringId = R.string.condition_957;
                break;
            case 958:
                stringId = R.string.condition_958;
                break;
            case 959:
                stringId = R.string.condition_959;
                break;
            case 960:
                stringId = R.string.condition_960;
                break;
            case 961:
                stringId = R.string.condition_961;
                break;
            case 962:
                stringId = R.string.condition_962;
                break;
            default:
                return context.getString( R.string.condition_unknown, weatherId );

            } // end switching the weather id

        return context.getString( stringId );

    } // end method getStringForWeatherCondition

    /**
     * Helper method to get the preferred icon pack.
     *
     * @param context The {@link Context} we're working in
     *
     * @return A string containing the value of the preferred icon pack
     * (which should be a URL pointing to the location of the preferred icon pack -
     * look at strings.xml for more details)
     * */
    // begin getPreferredIconPack
    private static String getPreferredIconPack( Context context ) {

        // 0. return a string having the link to the preferred icon pack

        // 0. return a string having the link to the preferred icon pack

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        return sharedPreferences.getString(
                context.getString( R.string.pref_icon_pack_key ),
                context.getString( R.string.pref_icon_pack_colored_value )
        );

    } // end getPreferredIconPack

    /**
     * Helper method to get the art uri for a given weather condition based on the
     * OpenWeatherMap response.
     *
     * @param context The {@link Context} we're working in
     * @param weatherConditionId The weatherId attribute from the OpenWeatherMap API response
     *
     * @return A {@link android.net.Uri} pointing to the location of art for the given weather.
     * */
    // begin method getArtUriForWeatherCondition
    public static Uri getArtUriForWeatherCondition( Context context, int weatherConditionId ) {

        // 0. get the preferred icon pack
        // 1. return a Uri based on the weather condition and the preferred icon pack,
        // or null if the weather condition doesn't match

        // 0. get the preferred icon pack

        String preferredIconPackString = getPreferredIconPack( context );

        // 1. return a Uri based on the weather condition and the preferred icon pack,
        // or null if the weather condition doesn't match

        if ( weatherConditionId >= 200 && weatherConditionId <= 232 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "storm" ) );
        } else if ( weatherConditionId >= 300 && weatherConditionId <= 321 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "light_rain" ) );
        } else if (weatherConditionId >= 500 && weatherConditionId <= 504 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "rain" ) );
        } else if ( weatherConditionId == 511 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "snow" ) );
        } else if ( weatherConditionId >= 520 && weatherConditionId <= 531 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "rain" ) );
        } else if ( weatherConditionId >= 600 && weatherConditionId <= 622 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "snow" ) );
        } else if ( weatherConditionId >= 701 && weatherConditionId <= 761 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "fog" ) );
        } else if ( weatherConditionId == 761 || weatherConditionId == 781 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "storm" ) );
        } else if ( weatherConditionId == 800 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "clear" ) );
        } else if ( weatherConditionId == 801 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "light_clouds" ) );
        } else if ( weatherConditionId >= 802 && weatherConditionId <= 804 ) {
            return Uri.parse( String.format( Locale.getDefault(), preferredIconPackString, "clouds" ) );
        }

        return null;

    } // end method getArtUriForWeatherCondition

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
                sharedPreferences.getBoolean(
                        context.getString( R.string.pref_enable_notifications_key ),
                        Boolean.parseBoolean(
                                context.getString( R.string.pref_enable_notifications_default_value )
                        )
                );

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

    /**
     * Sets the location status in the preferences.
     *
     * @param context   The {@link Context} we are working in.
     * @param locationStatus A {@link com.jlt.sunshine.sync.SunshineSyncAdapter.LocationStatus}
     *                       constant defining the current location.
     * */
    // begin method setLocationStatus
    public static void setLocationStatus( Context context,
                                          @SunshineSyncAdapter.LocationStatus int locationStatus ) {

        // 0. get the preferences
        // 1. get the location status key
        // 2. put the location status parameter into the preferences

        // 0. get the preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. get the location status key

        String locationStatusKey = context.getString( R.string.pref_location_status_key );

        // 2. put the location status parameter into the preferences

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt( locationStatusKey, locationStatus );

        editor.apply();

    } // end method setLocationStatus

    /**
     * Gets the location status stored in the preferences.
     *
     * @param context The {@link Context} we are working in.
     *
     * @return The {@link com.jlt.sunshine.sync.SunshineSyncAdapter.LocationStatus} that is in the
     *         preferences.
     * */
    // begin method getLocationStatus
    @SuppressWarnings( "ResourceType" ) // suppress any warnings due to the gotten IntDef being out of range
    @SunshineSyncAdapter.LocationStatus
    public static int getLocationStatus( Context context ) {

        // 0. get the shared preferences
        // 1. return the location status from the preferences

        // 0. get the shared preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. return the location status from the preferences

        int locationStatus = sharedPreferences.getInt(
                context.getString( R.string.pref_location_status_key ),
                SunshineSyncAdapter.LOCATION_STATUS_SERVER_UNKNOWN
        );

        // begin switching the location status
        switch ( locationStatus ) {

            case SunshineSyncAdapter.LOCATION_STATUS_OK:
                return SunshineSyncAdapter.LOCATION_STATUS_OK;

            case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                return SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN;

            case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                return SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID;

            case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                return SunshineSyncAdapter.LOCATION_STATUS_INVALID;

            default:
                return SunshineSyncAdapter.LOCATION_STATUS_SERVER_UNKNOWN;

        } // end switching the location status

    } // end method getLocationStatus

    /**
     * Resets the location status in the preferences to an unknown status.
     *
     * @param context The {@link Context} we are working in.
     * */
    // begin method resetLocationStatus
    public static void resetLocationStatus( Context context ) {

        // 0. get the preferences
        // 1. put the unknown location status in the preferences

        // 0. get the preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. put the unknown location status in the preferences

        String locationStatusKey = context.getString( R.string.pref_location_status_key );

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt( locationStatusKey, SunshineSyncAdapter.LOCATION_STATUS_SERVER_UNKNOWN );

        editor.apply();

    } // end method resetLocationStatus

    /**
     * Helper method to generate a transition name for an item in a {@link ViewHolder}.
     *
     * The name is generated by concatenating the {@link android.support.v7.widget.RecyclerView.Adapter}
     * class's name to the position of the said view holder in the said adapter.
     *
     * @param context Android {@link Context}
     * @param adapterClass The {@link android.support.v7.widget.RecyclerView.Adapter} class
     * @param viewHolder The {@link ViewHolder} holding the item
     * */
    // begin method generateTransitionName
    public static String generateTransitionName( Context context, Class adapterClass, ViewHolder viewHolder ) {

        // 0. return a string concatenating the class and the view holder position

        return String.format( Locale.getDefault(),
                context.getString( R.string.format_weather_icon_transition_name ),
                adapterClass.getSimpleName(), viewHolder.getAdapterPosition() );

    } // end method generateTransitionName

    /**
     * Helper method to check if location latitude and longitude are available.
     *
     * @param context Android {@link Context}
     *
     * @return Whether or not there is a location latitude and longitude pair in preferences.
     */
    // begin method isLocationLatLongAvailable
    public static boolean isLocationLatLongAvailable( Context context ) {

        // 0. get preferences
        // 1. return if location latitude and longitude exist

        // 0. get preferences

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        // 1. return if location latitude and longitude exist

        return sharedPreferences.contains( context.getString( R.string.pref_location_latitude_key ) )
                && sharedPreferences.contains( context.getString( R.string.pref_location_longitude_key ) );

    } // end method isLocationLatLongAvailable

    /**
     * Helper method to get the location latitude.
     *
     * @param context Android {@link Context}
     *
     * @return The location latitude.
     */
    // begin method getLocationLatitude
    public static float getLocationLatitude( Context context ) {

        return PreferenceManager.getDefaultSharedPreferences( context )
                .getFloat( context.getString( R.string.pref_location_latitude_key ), DEFAULT_LAT_LONG );

    } // end method getLocationLatitude

    /**
     * Helper method to get the location longitude.
     *
     * @param context Android {@link Context}
     *
     * @return The location longitude.
     */
    // begin method getLocationLongitude
    public static float getLocationLongitude( Context context ) {

        return PreferenceManager.getDefaultSharedPreferences( context )
                .getFloat( context.getString( R.string.pref_location_longitude_key ), DEFAULT_LAT_LONG );

    } // end method getLocationLongitude

    /* INNER CLASSES */

} // end class Utility