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

package com.joslittho.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.joslittho.sunshine.data.contract.WeatherContract;

/** Class to handle DB interaction for us. */

// begin class WeatherDbHelper
public class WeatherDbHelper extends SQLiteOpenHelper {

    /** CONSTANTS */

    /* Integers */

    public static final int DATABASE_VERSION = 2; // should be updated every time db schema is changed.

    /* Strings */

    public static final String DATABASE_NAME = "weather.db"; // name of the db in the file system

    /** VARIABLES */

    /** CONSTRUCTOR */

    // constructor
    // takes context, db name, cursor factory, and db version
    public WeatherDbHelper( Context context )
    { super( context, DATABASE_NAME, null, DATABASE_VERSION ); }

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // begin onCreate
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {

        // 2. build the SQL for creating the location table
        // 3. execute this
        // 0. build the SQL string for creating the weather table
        // 1. execute this

        // 2. build the SQL for creating the location table

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +

                WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL," + // UNIQUE to ensure same location setting not have multiple IDs

                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL," +

                WeatherContract.LocationEntry.COLUMN_COORD_LATITUDE + " FLOAT NOT NULL," +
                WeatherContract.LocationEntry.COLUMN_COORD_LONGITUDE + " FLOAT NOT NULL);";

        // 3. execute this

        sqLiteDatabase.execSQL( SQL_CREATE_LOCATION_TABLE );

        // 0. build the SQL string for creating the weather table

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +

                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // ID of the location entry associated with this weather data

                WeatherContract.WeatherEntry.COLUMN_LOCATION_KEY + " INTEGER NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," + // should this be an integer? Yes. It says which icon we will use

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES + " REAL NOT NULL," +

                // set up the location column as a foreign key to the location table
                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOCATION_KEY + ") REFERENCES " +
                WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +

                // to ensure there is only one weather entry per day per location
                // tell SQL to enforce only one weather date per location
                // in case there happens to be more than one row containing the same date and/or location
                // use the latest row and delete the earlier one
                "UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
                WeatherContract.WeatherEntry.COLUMN_LOCATION_KEY + ") ON CONFLICT REPLACE);";

        // 1. execute this

        sqLiteDatabase.execSQL( SQL_CREATE_WEATHER_TABLE );

    } // end onCreate

    @Override
    // begin onUpgrade
    // this method only fires if the db version number, NOT the app version number, changes
    // if there is need to update db schema without wiping data, ensure any lines in this method
    // involving the deletion of tables have been commented out
    public void onUpgrade( SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion ) {

        // since this db is simply caching what is online, upgrading this db
        // should simply involve discarding the old db and starting over

        // 0. delete the location table
        // 1. delete the weather table
        // 2. create anew

        // 0. delete the location table

        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME );

        // 1. delete the weather table

        sqLiteDatabase.execSQL( "DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME );

        // 2. create anew

        onCreate( sqLiteDatabase );

    } // end onUpgrade

    /** Other Methods */

    /** INNER CLASSES */

} // end class WeatherDbHelper