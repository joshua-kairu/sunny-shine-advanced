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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jlt.sunshine.data.ForecastAdapter;
import com.jlt.sunshine.data.ForecastCallback;
import com.jlt.sunshine.data.Utility;
import com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;
import com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;
import com.jlt.sunshine.sync.SunshineSyncAdapter;

/** Fragment to show the weather forecast. */
// begin fragment ForecastFragment
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks< Cursor > {

    /* CONSTANTS */

    /* Arrays */

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            // fully qualify the table id since the content provider
            // joins the location and weather tables, both of which have an _id column.
            // this qualification allows us to
            // get the weather using the location set by the user.
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LATITUDE,
            LocationEntry.COLUMN_COORD_LONGITUDE
    };

    /* Integers */

    private static final int FORECAST_LOADER_ID = 0; // ditto

    // indices to locate values from the FORECAST_COLUMNS projection.
    // the indices match the arrangement in FORECAST_COLUMNS.
    // if the arrangement in FORECAST_COLUMNS changes, these indices MUST be changed too.

    public static final int COLUMN_WEATHER_ID = 0;
    public static final int COLUMN_WEATHER_DATE = 1;
    public static final int COLUMN_WEATHER_SHORT_DESCRIPTION = 2;
    public static final int COLUMN_WEATHER_MAX_TEMP = 3;
    public static final int COLUMN_WEATHER_MIN_TEMP = 4;
    public static final int COLUMN_LOCATION_SETTING = 5;
    public static final int COLUMN_WEATHER_CONDITION_ID = 6;
    public static final int COLUMN_COORD_LATITUDE = 7;
    public static final int COLUMN_COORD_LONGITUDE = 8;

    /* Strings */

    public static final String LOG_TAG = ForecastFragment.class.getSimpleName(); // the logger

    private static final String BUNDLE_SCROLL_POSITION = "BUNDLE_SCROLL_POSITION";
        // key to store the current scroll position

    /* VARIABLES */

    /* Alarm Managers */

    private AlarmManager mAlarmManager; // ditto

    /* Forecast Callbacks */

    private ForecastCallback forecastCallbackListener; // listener for any changes
                                                       // done to the forecast fragment

    /* Forecast Adapters */

    private ForecastAdapter mForecastAdapter; // array adapter to fill the weather list view

    /* List Views */

    private ListView mForecastListView; // ditto

    /* P */

    private PendingIntent mAlarmPendingIntent; // ditto

    /* Primitives */

    private int mCurrentScrollPosition = ListView.INVALID_POSITION; // current scroll position of
                                                                    // the list view. starting off
                                                                    // with an invalid position

    private boolean mUseTodayLayout = true; // ditto

    /*
     * CONSTRUCTOR
     */

    // empty constructor for fragment subclasses
    public ForecastFragment() {
    }

    /* METHODS */

    /* Getters and Setters */

    /**
     * Should we use the special today layout in the forecast list?
     *
     * @param useTodayLayout {@link Boolean} based on whether we should use
     *                       the special today layout in the forecast list.
     * */
    // begin method setUseTodayLayout
    public void setUseTodayLayout( boolean useTodayLayout ) {

        // 0. set the member use today layout variable
        // 1. use the member use today layout variable to communicate to the forecast adapter

        // 0. set the member use today layout variable

        mUseTodayLayout = useTodayLayout;

        // 1. use the member use today layout variable to communicate to the forecast adapter

        // the forecast adapter could be null if
        // this method is called before the fragment's onCreateView,
        // which is where the adapter is initialized
        if ( mForecastAdapter != null ) { mForecastAdapter.setUseTodayLayout( mUseTodayLayout ); }

    } // end method setUseTodayLayout

    /*
     * Overrides
     */

    @Override
    // begin onCreate
    public void onCreate( @Nullable Bundle savedInstanceState ) {

        // 0. super things
        // 1. register the options menu
        // 2. initialize the forecast callback listener

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. register the options menu

        setHasOptionsMenu( true );

        // 2. initialize the forecast callback listener

        try { forecastCallbackListener = ( ForecastCallback ) getActivity(); }

        catch ( ClassCastException e ) {
            Log.e( LOG_TAG, "The parent activity must implement ForecastCallback." );
        }

    } // end onCreate

    @Override
    // begin onCreateOptionsMenu
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {

        // 0. super things
        // 1. inflate the forecast fragment menu

        // 0. super things

        super.onCreateOptionsMenu( menu, inflater );

        // 1. inflate the forecast fragment menu

        inflater.inflate( R.menu.menu_forecast_fragment, menu );

    } // end onCreateOptionsMenu

    @Override
    // begin onCreateView
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // 0. inflate the main fragment layout
        // 1. query the content provider for the current weather at
        // the location specified by the location setting sorted by ascending date
        // and get a cursor
        // 2. initialize the adapter
        // 2a. tell it if to use the special today layout
        // 3. find reference to the list view
        // 4. set adapter to the list
        // 5. when an item in the list view is clicked
        // 5a. notify the parent activity
        // 5b. update the selected position member variable
        // 6. if there's instance state,
        // 6a. mine it for the scroll position
        // lasta. close the cursor opened in 1
        // lastb. return the inflated view

        // 0. inflate the main fragment layout

        View rootView = inflater.inflate( R.layout.fragment_main, container, false );

        // 1. query the content provider for the current weather at
        // the location specified by the location setting sorted by ascending date
        // and get a cursor

        String locationSetting = Utility.getPreferredLocation( getActivity() );

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherForLocationWithStartDateUri(
                locationSetting, System.currentTimeMillis() );

        Cursor cursor = getActivity().getContentResolver().query(
                weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder );

        // 2. initialize the adapter

        mForecastAdapter = new ForecastAdapter( getActivity(), cursor, 0 );

        // 2a. tell it if to use the special today layout

        mForecastAdapter.setUseTodayLayout( mUseTodayLayout );

        // 3. find reference to the list view

        mForecastListView = ( ListView ) rootView.findViewById( R.id.fm_lv_forecast );

        // 4. set adapter to the list

        mForecastListView.setAdapter( mForecastAdapter );

        // 5. when an item in the list view is clicked

        // begin listView.setOnItemClickListener
        mForecastListView.setOnItemClickListener(

                // begin new AdapterView.OnItemClickListener
                new AdapterView.OnItemClickListener() {

                    @Override
                    // begin onItemClick
                    public void onItemClick( AdapterView< ? > adapterView, View view,
                                             int position, long id ) {

                        // 5a. notify the parent activity

                        // 0. get the cursor at the given position
                        // 1. if there is a cursor there
                        // 1a. get the location setting
                        // 1b. pass these to the parent activity
                        // 1last. close the cursor opened in 0

                        // 0. get the cursor at the given position

                        Cursor cursor = ( Cursor ) adapterView.getItemAtPosition( position );

                        // 1. if there is a cursor there

                        // begin if there exists a cursor
                        if ( cursor != null ) {

                            // 1a. get the location setting

                            String locationSetting = Utility.getPreferredLocation( getActivity() );

                            // 1b. pass these to the parent activity

                            forecastCallbackListener.onForecastItemSelected(
                                    WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                                            locationSetting, cursor.getLong( COLUMN_WEATHER_DATE ) )
                            );

                            // 1last. close the cursor opened in 0

                            cursor.close();

                        } // end if there exists a cursor

                        // 5b. update the selected position member variable

                        mCurrentScrollPosition = position;

                    } // end onItemClick

                } // end new AdapterView.OnItemClickListener

        ); // end listView.setOnItemClickListener

        // 6. if there's instance state,

        /// 6a. mine it for the scroll position

        if ( savedInstanceState != null &&
                savedInstanceState.containsKey( BUNDLE_SCROLL_POSITION ) == true ) {
            mCurrentScrollPosition = savedInstanceState.getInt( BUNDLE_SCROLL_POSITION );
        }

        // lasta. close the cursor opened in 1

        if ( cursor != null ) { cursor.close(); }

        // lastb. return the inflated view

        return rootView;

    } // end onCreateView

    @Override
    // begin onActivityCreated
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {

        // 0. super things
        // 1. start the weather loader

        // 0. super things

        super.onActivityCreated( savedInstanceState );

        // 1. start the weather loader

        getLoaderManager().initLoader( FORECAST_LOADER_ID, null, this );

    } // end onActivityCreated

    @Override
    // begin onOptionsItemSelected
    public boolean onOptionsItemSelected( MenuItem item ) {

        // 0. if the refresh item is selected
        // 0a. update the weather
        // 0last. return true
        // last. return super things

        int selectedId = item.getItemId();

        // 0. if the refresh item is selected

        // begin if refresh is selected
        if ( selectedId == R.id.action_refresh ) {

            // 0a. update the weather

            updateWeather();

            // 0last. return true

            return true;

        } // end if refresh is selected

        // last. return super things

        return super.onOptionsItemSelected( item );

    } // end onOptionsItemSelected

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    // begin onCreateLoader
    public Loader< Cursor > onCreateLoader( int id, Bundle args ) {

        // 0. create and return a new weather loader for the weather at a given location

        // 0. create and return a new weather loader for the weather at a given location

        String locationSetting = Utility.getPreferredLocation( getActivity() );

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherForLocationWithStartDateUri(
                locationSetting, System.currentTimeMillis() );

        return new CursorLoader( getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder );

    } // end onCreateLoader

    /**
     * Called when a previously created loader has finished its load.
     */
    @Override
    // begin onLoadFinished
    public void onLoadFinished( Loader< Cursor > cursorLoader, Cursor cursor ) {

        // 0. refresh the list view
        // 1. if there is an valid list scroll position
        // 1a. scroll to it

        // 0. refresh the list view

        // swapCursor - Swap in a new Cursor, returning the old Cursor
        mForecastAdapter.swapCursor( cursor );

        // 1. if there is an valid list scroll position
        // 1a. scroll to it

        if ( mCurrentScrollPosition != ListView.INVALID_POSITION ) {
            mForecastListView.smoothScrollToPosition( mCurrentScrollPosition );
        }

    } // end onLoadFinished

    /**
     * Called when a previously created loader is being reset.
     */
    @Override
    // begin onLoaderReset
    public void onLoaderReset( Loader< Cursor > cursorLoader ) {

        // 0. remove any cursors being used

        // 0. remove any cursors being used

        mForecastAdapter.swapCursor( null );

    } // end onLoaderReset

    @Override
    // begin onSaveInstanceState
    public void onSaveInstanceState( Bundle outState ) {

        // 0. super stuff
        // 1. if there is an item selected
        // 1a. put the current scroll position in the bundle

        // 0. super stuff

        super.onSaveInstanceState( outState );

        // 1. if there is an item selected

        // 1a. put the current scroll position in the bundle

        // no item selected will leave the position at INVALID_POSITION

        if ( mCurrentScrollPosition != ListView.INVALID_POSITION ) {
            outState.putInt( BUNDLE_SCROLL_POSITION, mCurrentScrollPosition );
        }

    } // end onSaveInstanceState

    /* Other Methods */

    // begin method updateWeather
    // refreshes the weather
    private void updateWeather() {

        // 0a. get the user's preferred location
        // 0b. put an alarm to send a broadcast to fetch the weather info
        // 0b1. alarm should go off after 5 seconds
        // 0b2. should fire only once
        // 0b3. should wake the phone up if it is asleep

        SunshineSyncAdapter.syncImmediately( getActivity() );
//        // 0a. get the user's preferred location
//
//        // 0b. put an alarm to send a broadcast to fetch the weather info
//
//        mAlarmManager = ( AlarmManager ) getActivity().getSystemService( Context.ALARM_SERVICE );
//
//        Intent alarmReceiverIntent = new Intent( getActivity(), AlarmBroadcastReceiver.class )
//                .putExtra( SunshineService.EXTRA_LOCATION,
//                        Utility.getPreferredLocation( getActivity() ) );
//
//        // PendingIntent - A description of an Intent and target action to perform with it.
//        // getBroadcast - Retrieve a PendingIntent that will perform a broadcast,
//        //  like calling Context.sendBroadcast().
//        // FLAG_ONE_SHOT - Flag indicating that this PendingIntent can be used only once.
//        mAlarmPendingIntent = PendingIntent.getBroadcast( getActivity(), 0, alarmReceiverIntent,
//                PendingIntent.FLAG_ONE_SHOT );
//
//        // 0b1. alarm should go off after 5 seconds
//
//        long fiveSeconds = 5 * 1000;
//
//        // 0b2. should fire only once -> PendingIntent.FLAG_ONE_SHOT
//
//        // 0b3. should wake the phone up if it is asleep -> AlarmManager.RTC_WAKEUP
//
//        // set - Schedule an alarm
//        // RTC_WAKEUP - wall clock time in UTC which will wake up the device when it goes off.
//        mAlarmManager.set( AlarmManager.RTC_WAKEUP,
//                System.currentTimeMillis() + fiveSeconds,
//                mAlarmPendingIntent );

    } // end method updateWeather

    /**
     * Handles what happens when the user's preferred location is changed.
     * More precisely, when the location is changed, this method updates the weather
     * and then restarts the loader.
     * */
    // begin method onLocationChanged
    public void onLocationChanged() {

        // 0. update the weather
        // 1. restart the loader

        // 0. update the weather

        updateWeather();

        // 1. restart the loader

        // restartLoader - Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader( FORECAST_LOADER_ID, null, this );

    } // end method onLocationChanged

} // end fragment ForecastFragment