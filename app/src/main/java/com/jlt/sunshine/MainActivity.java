package com.jlt.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jlt.sunshine.data.ForecastCallback;
import com.jlt.sunshine.data.Utility;

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

/** The landing activity. */
// begin activity MainActivity
public class MainActivity extends AppCompatActivity implements ForecastCallback {

    /** CONSTANTS */

    /* Strings */

    private static final String LOG_TAG = MainActivity.class.getSimpleName(); // the logoger

    public static final String DETAILFRAGMENT_TAG = "DFTAG"; // ditto

    /** VARIABLES */

    /** Primitives */

    private boolean mTwoPane; // checks if we are dealing with a one or two pane (phone or tab) UI

    /* Strings */

    private String mCurrentLocation; // stores our current known location

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // begin onCreate
    protected void onCreate( Bundle savedInstanceState ) {

        // 0. super things
        // 1. get the current location
        // 2. use the main activity layout
        // 3. if this is a two pane UI
        // 3a. set the two pane flag to true
        // 3b. if this is the first run,
        // 3b1. replace the detail fragment in the second pane
        // (the forecast fragment is added to the first pane in xml way before runtime)
        // 3c. we shouldn't use the today layout in two pane mode
        // 4. otherwise this is not a two pane UI
        // 4a. set the two pane flag to false
        // 4b. we should use the today layout in one pane mode

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. get the current location

        mCurrentLocation = Utility.getPreferredLocation( this );

        // 2. use the main activity layout

        setContentView( R.layout.activity_main );

        // 3. if this is a two pane UI

        // we know two pane status by checking status of the existence of the detail container view

        // begin if there is a detail container view
        if ( findViewById( R.id.am_f_weather_detail_container ) != null ) {

            // 3a. set the two pane flag to true

            mTwoPane = true;

            // 3b. if this is the first run,

            // in the first run the saved instance state is null since
            // no state, including fragment state,  has been put in there.
            // but when the screen is rotated during the use of this app,
            // state, including fragment state, is saved
            // so saved instance state will not be null.
            // we do not want a new detail fragment to be put
            // just because the screen has been rotated.

            // begin if the saved instance is null
            if ( savedInstanceState == null ) {

                // 3b1. replace the detail fragment in the second pane
                // (the forecast fragment is added to the first pane in xml way before runtime)

                getSupportFragmentManager()

                    .beginTransaction()

                    .replace( R.id.am_f_weather_detail_container,
                            new DetailFragment(),
                            DETAILFRAGMENT_TAG )

                    .commit();

                // 3c. we shouldn't use the today layout in two pane mode

                ForecastFragment forecastFragment = ( ForecastFragment )
                        getSupportFragmentManager().findFragmentById( R.id.am_f_forecast );

                if ( forecastFragment != null ) { forecastFragment.setUseTodayLayout( false ); }

            } // end if the saved instance is null

        } // end if there is a detail container view

        // 4. otherwise this is not a two pane UI

        // begin else there's no detail container
        else {

            // 4a. set the two pane flag to false

            mTwoPane = false;

            // 4b. we should use the today layout in one pane mode

            ForecastFragment forecastFragment = ( ForecastFragment )
                    getSupportFragmentManager().findFragmentById( R.id.am_f_forecast );

            if ( forecastFragment != null ) { forecastFragment.setUseTodayLayout( true ); }

        } // end else there's no detail container

        Log.e( LOG_TAG, "onCreate: " );

        // 3. get the current location

    } // end onCreate

    @Override
    // begin onStart
    protected void onStart() {

        // 0. super things

        // 0. super things

        super.onStart();

        Log.e( LOG_TAG, "onStart: " );

    } // end onStart

    @Override
    // begin onResume
    protected void onResume() {

        // 0. super things
        // 1. if the location has been changed
        // 1a. get the forecast fragment
        // 1b. tell it that the location has changed
        // 1c. get the detail fragment
        // 1d. tell it that the location has changed
        // 1e. update the current location

        // 0. super things

        super.onResume();

        // 1. if the location has been changed

        String currentLocation = Utility.getPreferredLocation( this );

        // begin if the location has been changed
        if ( mCurrentLocation.equals( currentLocation ) == false ) {

            // 1a. get the forecast fragment

            ForecastFragment forecastFragment =
                    ( ForecastFragment ) getSupportFragmentManager()
                            .findFragmentById( R.id.am_f_forecast );

            // 1b. tell it that the location has changed

            if ( forecastFragment != null ) { forecastFragment.onLocationChanged(); }

            // 1c. get the detail fragment

            DetailFragment detailFragment = ( DetailFragment ) getSupportFragmentManager()
                    .findFragmentByTag( DETAILFRAGMENT_TAG );

            // 1d. tell it that the location has changed

            if ( detailFragment != null ) { detailFragment.onLocationChanged( currentLocation ); }

            // 1e. update the current location

            mCurrentLocation = Utility.getPreferredLocation( this );

        } // end if the location has been changed

    } // end onResume

    @Override
    // begin onPause
    protected void onPause() {

        // 0. super things

        // 0. super things

        super.onPause();

        Log.e( LOG_TAG, "onPause: " );

    } // end onPause

    @Override
    // begin onStop
    protected void onStop() {

        // 0. super things

        // 0. super things

        super.onStop();

        Log.e( LOG_TAG, "onStop: " );

    } // end onStop

    @Override
    // begin onDestroy
    protected void onDestroy() {

        // 0. super things

        // 0. super things

        super.onDestroy();

        Log.e( LOG_TAG, "onDestroy: " );

    } // end onDestroy

    @Override
    // begin onCreateOptionsMenu
    public boolean onCreateOptionsMenu( Menu menu ) {

        // 0. use the main menu
        // 1. return true

        // 0. use the main menu

        getMenuInflater().inflate( R.menu.main, menu );

        // 1. return true

        return true;

    } // end onCreateOptionsMenu

    @Override
    // begin onOptionsItemSelected
    public boolean onOptionsItemSelected( MenuItem item ) {

        // 0. if the settings item is selected
        // 0a. show the settings
        // 0b. return true
        // 1. if the view location item is selected
        // 1a. show the map if possible using the correct URI
        // 1last. return true
        // last. return super things

        int selectedId = item.getItemId();

        // 0. if the settings item is selected

        // begin if settings is selected
        if ( selectedId == R.id.action_settings ) {

            // 0a. show the settings

            Intent settingsIntent = new Intent( this, SettingsActivity.class );

            startActivity( settingsIntent );

            // 0b. return true

            return true;

        } // end if settings is selected

        // 1. if the view location item is selected

        // begin if view location is selected
        if ( selectedId == R.id.action_view_location ) {

            // 1a. get the user's preferred location

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

            String preferredLocation = sharedPreferences.getString( getResources().getString( R.string.pref_location_key ), getResources().getString( R.string.pref_location_default ) );

            // 1b. show the map if possible

            Uri mapUri = Uri.parse( "geo:0,0?" ).buildUpon()
                    .appendQueryParameter( "q", preferredLocation )
                    .build();

            showMap( mapUri );

            // 1last. return true

            return true;

        } // end if view location is selected

        // last. return super things

        return super.onOptionsItemSelected( item );

    } // end onOptionsItemSelected

    /**
     * {@link DetailFragment} callback for when an item has been selected.
     *
     * @param dateUri
     */
    @Override
    // begin onForecastItemSelected
    public void onForecastItemSelected( Uri dateUri ) {

        // 0. if we are on two pane mode
        // 0a. replace the current detail fragment with a detail fragment
        // showing the selected item's information
        // 1. otherwise
        // 1a. start the detail activity

        // 0. if we are on two pane mode

        // begin if we are two pane
        if ( mTwoPane == true ) {

            // 0a. replace the current detail fragment with a detail fragment
            // showing the selected item's information

            DetailFragment detailFragment = DetailFragment.newInstance( dateUri );

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.am_f_weather_detail_container, detailFragment )
                    .commit();

        } // end if we are two pane

        // 1. otherwise

        // begin else we are single pane
        else {

            // 1a. start the detail activity

            Intent detailsIntent = new Intent( this, DetailActivity.class ).setData( dateUri );

            startActivity( detailsIntent );

        } // end else we are single pane

    } // end onForecastItemSelected

    /* Other Methods */

    /**
     * Show a map with the passed URI
     * @param mapUri to use to show a map
     * */
    // begin method showMap
    private void showMap( Uri mapUri ) {

        // 0. use an implicit view intent
        // 1. put the Uri data in
        // 2. if there is a map app
        // 2a. start the activity
        // 3. otherwise
        // 3a. tell the user of the sadness

        // 0. use an implicit view intent

        Intent mapIntent = new Intent( Intent.ACTION_VIEW );

        // 1. put the Uri data in

        mapIntent.setData( mapUri );

        // 2. if there is a map app

        // 2a. start the activity

        if ( mapIntent.resolveActivity( getPackageManager() ) != null ) { startActivity( mapIntent ); }

        // 3. otherwise

        // 3a. tell the user of the sadness

        else { Toast.makeText( this, R.string.message_error_no_maps_app, Toast.LENGTH_SHORT ).show(); }

    } // end method showMap

} // end activity MainActivity
