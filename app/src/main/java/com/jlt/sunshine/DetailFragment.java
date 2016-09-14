package com.jlt.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlt.sunshine.data.Utility;
import com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;

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

/*
* This Fragment shows the details of a selected day in the week of weather
*
* */
// begin fragment DetailFragment
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks< Cursor > {

    /* CONSTANTS */

    /* Arrays */

    // the details should have all the information about the weather on that day
    // so the projection should have the
    // date, short description, max temperature, min temperature, weather icon,
    // humidity, wind speed and direction, and pressure
    private static String[] DETAILS_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_WIND_DIRECTION_DEGREES,
            WeatherEntry.COLUMN_PRESSURE
    };


    /* Integers */

    private static final int DETAIL_LOADER_ID = 0; // ditto

    // indices
    private static final int COLUMN_WEATHER_PRIMARY_KEY_ID = 0;
    private static final int COLUMN_WEATHER_DATE = 1;
    private static final int COLUMN_WEATHER_SHORT_DESCRIPTION = 2;
    private static final int COLUMN_WEATHER_MAX_TEMP = 3;
    private static final int COLUMN_WEATHER_MIN_TEMP = 4;
    private static final int COLUMN_WEATHER_WEATHER_ID = 5;
    private static final int COLUMN_WEATHER_HUMIDITY = 6;
    private static final int COLUMN_WEATHER_WIND_SPEED = 7;
    private static final int COLUMN_WEATHER_WIND_DIRECTION_DEGREES = 8;
    private static final int COLUMN_WEATHER_PRESSURE = 9;

    /* Strings */

    private static final String LOG_TAG = DetailFragment.class.getSimpleName(); // the log tag

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp"; // the share hashtag

    public static final String ARGUMENT_URI_KEY = "ARGUMENT_URI_KEY"; // key used to refer the uri
                                                                      // in the bundle passed into
                                                                      // this fragment during instantiation

    /* VARIABLES */

    /* Image Views */

    private ImageView mWeatherIconImageView; // ditto

    /* Share Action Providers */

    private ShareActionProvider mWeatherDetailShareActionProvider; // app bar presence to allow one to share

    /* Strings */

    private String weatherDetailString; // the string to hold this detail's weather

    /* Text Views */

    private TextView mDayTextView; // ditto
    private TextView mDateTextView; // ditto
    private TextView mHighTempTextView; // ditto
    private TextView mLowTempTextView; // ditto
    private TextView mDescriptionTextView; // ditto
    private TextView mHumidityTextView; // ditto
    private TextView mPressureTextView; // ditto
    private TextView mWindTextView; // ditto

    /* Uris */

    private Uri mDataUri; // uri that we will use to fetch the data we will need for this fragment

    /*
     * CONSTRUCTOR
     */

    // empty constructor for fragment subclasses
    public DetailFragment() {
    }

    /**
     * Create a new {@link DetailFragment} with the given arguments.
     *
     * @param uri A {@link android.net.Uri} we will use to initialize the
     *            {@link DetailFragment} with.
     * */
    // begin instantiating method newInstance
    public static DetailFragment newInstance( Uri uri ) {

        // 0. create a new DetailFragment
        // 1. put the parameter uri in a bundle
        // 2. use the bundle as the arguments for the DetailFragment created at 0
        // 3. return the DetailFragment created at 0

        // 0. create a new DetailFragment

        DetailFragment detailFragment = new DetailFragment();

        // 1. put the parameter uri in a bundle

        Bundle bundle = new Bundle();

        bundle.putParcelable( ARGUMENT_URI_KEY, uri );

        // 2. use the bundle as the arguments for the DetailFragment created at 0

        detailFragment.setArguments( bundle );

        // 3. return the DetailFragment created at 0

        return detailFragment;

    } // end instantiating method newInstance

    /* METHODS */

    /* Getters and Setters */

    /*
     * Overrides
     */

    @Override
    // begin onCreate
    public void onCreate( @Nullable Bundle savedInstanceState ) {

        // 0. super things
        // 1. register the options menu

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. register the options menu

        setHasOptionsMenu( true );

    } // end onCreate

    @Override
    // begin onCreateOptionsMenu
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {

        // 0. use the details menu
        // 1. get the share menu item
        // 2. store the share action provider
        // 3. create an intent that should allow users to send the weather detail

        // 0. use the details menu

        inflater.inflate( R.menu.detail, menu );

        // 1. get the share menu item

        MenuItem menuItem = menu.findItem( R.id.action_share );

        // 2. store the share action provider

        mWeatherDetailShareActionProvider =
                ( ShareActionProvider ) MenuItemCompat.getActionProvider( menuItem );

        // 3. create an intent that should allow users to send the weather detail

        if ( weatherDetailString != null ) {
            mWeatherDetailShareActionProvider.setShareIntent(
                    setWeatherDetailShareIntent() );
        }

    } // end onCreateOptionsMenu

    @Override
    // begin onCreateView
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // 0. initialize the data uri from the arguments
        // 1. use the detail layout
        // 2. get the needed views
        // last. return the inflated detail layout

        // 0. initialize the data uri from the arguments

        if ( getArguments() != null ) {
            mDataUri = getArguments().getParcelable( ARGUMENT_URI_KEY );
        }

        // 1. use the detail layout

        View rootView = inflater.inflate( R.layout.fragment_detail, container, false );

        // 2. get the needed views

        mDayTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_day );
        mDateTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_date );
        mHighTempTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_high_temperature );
        mLowTempTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_low_temperature );
        mWeatherIconImageView = ( ImageView ) rootView.findViewById( R.id.detail_iv_weather_icon );
        mDescriptionTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_description );
        mHumidityTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_humidity );
        mWindTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_wind );
        mPressureTextView = ( TextView ) rootView.findViewById( R.id.detail_tv_pressure );

        // last. return the inflated detail layout

        return rootView;

    } // end onCreateView

    /**
     * Instantiate and return a new Loader for the given ID.
     */
    @Override
    // begin onCreateLoader
    public Loader< Cursor > onCreateLoader( int id, Bundle args ) {

        // 0. if this fragment was created without a Uri, there's no point in creating a loader
        // 1. create a cursor loader to get the details

        // 0. if this fragment was created without a Uri, there's no point in creating a loader

        if ( mDataUri == null ) { return null; }

        // 1. create a cursor loader to get the details

        return new CursorLoader(
                getActivity(),
                mDataUri,
                DETAILS_COLUMNS,
                null,
                null,
                null
        );

    } // end onCreateLoader

    /**
     * Called when a previously created loader has finished its load.
     */
    @Override
    // begin onLoadFinished
    public void onLoadFinished( Loader< Cursor > cursorLoader, Cursor cursor ) {

        // 0. bind the needed details data to the text view

        // 0. bind the needed details data to the text view

        // begin if there is a row in the cursor
        if ( cursor != null && cursor.moveToFirst() == true )  {

            mDayTextView.setText(
                    Utility.getDayName( getActivity(), cursor.getLong( COLUMN_WEATHER_DATE ) )
            );

            mDateTextView.setText(
                    Utility.getFormattedMonthDay( cursor.getLong( COLUMN_WEATHER_DATE ) )
            );

            mHighTempTextView.setText(
                    Utility.formatTemperature( getActivity(),
                            cursor.getFloat( COLUMN_WEATHER_MAX_TEMP ),
                            Utility.isMetric( getActivity() ) )
            );

            mLowTempTextView.setText(
                    Utility.formatTemperature( getActivity(),
                            cursor.getFloat( COLUMN_WEATHER_MIN_TEMP ),
                            Utility.isMetric( getActivity() ) )
            );

            mWeatherIconImageView.setImageResource(
                    Utility.getArtResourceForWeatherCondition(
                            cursor.getInt( COLUMN_WEATHER_WEATHER_ID )
                    )
            );

            // content description for accessibility
            mWeatherIconImageView.setContentDescription(
                    cursor.getString( COLUMN_WEATHER_SHORT_DESCRIPTION )
            );

            mDescriptionTextView.setText( cursor.getString( COLUMN_WEATHER_SHORT_DESCRIPTION ) );

            mHumidityTextView.setText(
                    getActivity().getString( R.string.format_humidity,
                            cursor.getFloat( COLUMN_WEATHER_HUMIDITY ) )
            );

            mWindTextView.setText(
                    Utility.getFormattedWind( getActivity(),
                            cursor.getFloat( COLUMN_WEATHER_WIND_SPEED ),
                            cursor.getFloat( COLUMN_WEATHER_WIND_DIRECTION_DEGREES ) )
            );

            mPressureTextView.setText(
                  getActivity().getString( R.string.format_pressure,
                          cursor.getFloat( COLUMN_WEATHER_PRESSURE ) )
            );

        } // end if there is a row in the cursor

    } // end onLoadFinished

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     */
    @Override
    public void onLoaderReset( Loader< Cursor > loader ) { }

    @Override
    // begin onActivityCreated
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {

        // 0. super stuff
        // 1. initialize the details loader

        // 0. super stuff

        super.onActivityCreated( savedInstanceState );

        // 1. initialize the details loader

        if ( mDataUri != null ) { getLoaderManager().initLoader( DETAIL_LOADER_ID, null, this ); }

    } // end onActivityCreated

    /* Other Methods */

    /*

    * Sets the Intent that will be used to share weather detail
    *
    * @param The detail to be shared. Shared details should have the format <Detail> #SunshineApp,
    * for example "Tue 6/24 - Foggy - 21/8 #SunshineApp" - without double quotes of course.
    *
    * @return The share intent
    *
    * */
    // begin method setWeatherDetailShareIntent
    private Intent setWeatherDetailShareIntent() {

        // 0. create the intent for sharing
        // 0a. should hold text
        // 0b. should have the detail in the appropriate format
        // 0c. called activity should clear when we leave our app
        // 1. return created intent

        // 0. create the intent for sharing

        Intent shareWeatherDetailIntent = new Intent( Intent.ACTION_SEND );

        // 0a. should hold text

        shareWeatherDetailIntent.setType( "text/plain" );

        // 0b. should have the detail in the appropriate format

        shareWeatherDetailIntent.putExtra( Intent.EXTRA_TEXT,
                weatherDetailString + " " + FORECAST_SHARE_HASHTAG );

        // 0c. called activity should clear when we leave our app

        shareWeatherDetailIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET );

        // 1. return created intent

        return shareWeatherDetailIntent;

    } // end method setWeatherDetailShareIntent

    /** Convert the cursor's current row to a UX-worthy format. */
    // begin method convertCursorRowToUXFormat
    private String convertCursorRowToUXFormat( Cursor cursor ) {

        // the needed format is "date - short description - high/low"

        // 0. get needed high/low string
        // 1. return something presentable

        // 0. get needed high/low string

        String highLowString = formatHighLows(
                cursor.getFloat( COLUMN_WEATHER_MAX_TEMP ),
                cursor.getFloat( COLUMN_WEATHER_MIN_TEMP ) );

        // 1. return something presentable

        return Utility.formatDate( cursor.getLong( COLUMN_WEATHER_DATE ) ) + " - " +
                        cursor.getString( COLUMN_WEATHER_SHORT_DESCRIPTION ) + " - " +
                        highLowString;

    } // end method convertCursorRowToUXFormat

    /** Prepare the weather high/low temperatures for presentation. */
    // begin method formatHighLows
    private String formatHighLows( float high, float low ) {

        // 0. are the units metric?
        // 1. return the correct high/low string

        // 0. are the units metric?

        boolean isMetric = Utility.isMetric( getActivity() );

        // 1. return the correct high/low string

        return Utility.formatTemperature( getActivity(), high, isMetric ) + "/"
                        + Utility.formatTemperature( getActivity(), low, isMetric );

    } // end method formatHighLows

    /**
     * Respond to the user's changing their location.
     *
     * This is done by replacing the old uri with a new one based on the new location
     * and restarting the member loader.
     *
     * @param newLocation The new location in {@link String} form.
     * */
    // begin onLocationChanged
    public void onLocationChanged( String newLocation ) {

        // 0. replace the old uri
        // 1. if the old uri existed
        // 1a. get the date that the old uri pointed to
        // 1b. create a new uri based on the date in 1a and the parameter new location
        // 1c. save the new uri created in 1b
        // 1d. restart the loader

        // 0. replace the old uri

        Uri uri = mDataUri;

        // 1. if the old uri existed

        // begin if uri is not null
        if ( uri != null ) {

            // 1a. get the date that the old uri pointed to

            long date = WeatherEntry.getDateFromUri( uri );

            // 1b. create a new uri based on the date in 1a and the parameter new location

            Uri updatedUri = WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                    newLocation, date );

            // 1c. save the new uri created in 1b

            mDataUri = updatedUri;

            // 1d. restart the loader

            getLoaderManager().restartLoader( DETAIL_LOADER_ID, null, this );

        } // end if uri is not null

        // TODO: 9/9/16 But how will the detail fragment get the information to display if the location information is not in the database?

    } // end onLocationChanged

    /* INNER CLASSES */

} // end fragment DetailFragment