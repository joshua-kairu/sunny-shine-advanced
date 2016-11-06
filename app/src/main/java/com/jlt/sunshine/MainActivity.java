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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jlt.sunshine.data.ForecastCallback;
import com.jlt.sunshine.data.Utility;
import com.jlt.sunshine.gcm.RegistrationIntentService;
import com.jlt.sunshine.sync.SunshineSyncAdapter;
import com.jlt.sunshine.ui.WeatherViewHolder;

/** The landing activity. */
// begin activity MainActivity
public class MainActivity extends AppCompatActivity implements ForecastCallback {

    /* CONSTANTS */

    /* Integers */

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /* Strings */

    private static final String LOG_TAG = MainActivity.class.getSimpleName(); // the logger

    public static final String DETAILFRAGMENT_TAG = "DFTAG"; // ditto

    /** Key to use to store boolean informing whether or not the GCM token has been sent. */
    public static final String PREF_SENT_TOKEN_KEY = "PREF_SENT_TOKEN_KEY";

    /* VARIABLES */

    /* Primitives */

    private boolean mTwoPane; // checks if we are dealing with a one or two pane (phone or tab) UI

    /* Strings */

    private String mCurrentLocation; // stores our current known location

    /* METHODS */

    /* Getters and Setters */

    /* Overrides */

    @Override
    // begin onCreate
    protected void onCreate( Bundle savedInstanceState ) {

        // 0. super things
        // 1. initialize things
        // 1a. the current location
        // 1b. content Uri
        // 2. use the main activity layout
        // 3. set up toolbar if it exists
        // 3a. set it as action bar
        // 3b. should not show a title
        // 4. if this is a two pane UI
        // 4a. set the two pane flag to true
        // 4b. if this is the first run,
        // 4b1. replace the detail fragment in the second pane
        // (the forecast fragment is added to the first pane in xml way before runtime)
        // 4c. we shouldn't use the today layout in two pane mode
        // 5. otherwise this is not a two pane UI
        // 5a. set the two pane flag to false
        // 5b. we should use the today layout in one pane mode
        // 5c. we should make the action bar as high as the today section
        // 6. start the sync
        // 7. start Google Play Services if possible
        // 7a. know if we have the token
        // 7b. if we don't have the token
        // 7b1. start the registration service

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. initialize things

        // 1a. the current location

        mCurrentLocation = Utility.getPreferredLocation( this );

        // 1b. content Uri

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;

        // 2. use the main activity layout

        setContentView( R.layout.activity_main );

        // 3. set up toolbar if it exists

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.main_toolbar );

        // begin if there be toolbar
        if ( toolbar != null ) {

            // 3a. set it as action bar

            setSupportActionBar( toolbar );

            // 3b. should not show a title

            getSupportActionBar().setDisplayShowTitleEnabled( false );

        } // end if there be toolbar

        // 4. if this is a two pane UI

        // we know two pane status by checking status of the existence of the detail container view

        // begin if there is a detail container view
        if ( findViewById( R.id.am_cv_weather_detail_container ) != null ) {

            // 4a. set the two pane flag to true

            mTwoPane = true;

            // 4b. if this is the first run,

            // in the first run the saved instance state is null since
            // no state, including fragment state,  has been put in there.
            // but when the screen is rotated during the use of this app,
            // state, including fragment state, is saved
            // so saved instance state will not be null.
            // we do not want a new detail fragment to be put
            // just because the screen has been rotated.

            // begin if the saved instance is null
            if ( savedInstanceState == null ) {

                // 4b1. replace the detail fragment in the second pane
                // (the forecast fragment is added to the first pane in xml way before runtime)

                DetailFragment detailFragment = contentUri != null ?
                        DetailFragment.newInstance( contentUri, false )
                        : new DetailFragment();

                getSupportFragmentManager()

                    .beginTransaction()

                    .replace( R.id.am_cv_weather_detail_container, detailFragment,
                            DETAILFRAGMENT_TAG )

                    .commit();

            } // end if the saved instance is null

            // 4c. we shouldn't use the today layout in two pane mode

            ForecastFragment forecastFragment = ( ForecastFragment )
                    getSupportFragmentManager().findFragmentById( R.id.am_f_forecast );

            if ( forecastFragment != null ) { forecastFragment.setUseTodayLayout( false ); }

        } // end if there is a detail container view

        // 5. otherwise this is not a two pane UI

        // begin else there's no detail container
        else {

            // 5a. set the two pane flag to false

            mTwoPane = false;

            // 5b. we should use the today layout in one pane mode

            ForecastFragment forecastFragment = ( ForecastFragment )
                    getSupportFragmentManager().findFragmentById( R.id.am_f_forecast );

            if ( forecastFragment != null ) { forecastFragment.setUseTodayLayout( true ); }

            // 5c. we should make the action bar as high as the today section

            if ( getSupportActionBar() != null ) { getSupportActionBar().setElevation( 0f ); }

        } // end else there's no detail container

        // 6. start the sync

        SunshineSyncAdapter.initializeSyncAdapter( this );

        // 7. start Google Play Services if possible

        // begin if Play Services are enabled
        if ( checkPlayServices() ) {

            // 7a. know if we have the token

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

            boolean haveToken = sharedPreferences.getBoolean( PREF_SENT_TOKEN_KEY, false );

            // 7b. if we don't have the token

            // begin if we have not the token
            if ( ! haveToken ) {

                // 7b1. start the registration service

                Intent registrationIntent = new Intent( this, RegistrationIntentService .class );
                startService( registrationIntent );

            } // end if we have not the token

        } // end if Play Services are enabled

        Log.e( LOG_TAG, "onCreate: " );

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

        // last. return super things

        return super.onOptionsItemSelected( item );

    } // end onOptionsItemSelected

    /**
     * {@link com.jlt.sunshine.DetailFragment} callback for when an item has been selected.
     *
     * @param dateUri {@link Uri} for the date.
     * @param weatherViewHolder {@link WeatherViewHolder} to assist in shared activity transitioning.
     * */
    @Override
    // begin onForecastItemSelected
    public void onForecastItemSelected( Uri dateUri, WeatherViewHolder weatherViewHolder ) {

        // 0. if we are on two pane mode
        // 0a. replace the current detail fragment with a detail fragment
        // showing the selected item's information
        // 1. otherwise
        // 1a. start the detail activity
        // 1a0. pair the correct items for shared element transitioning
        // 1a1. use the transition bundle with the pair
        // 1a2. tell the detail fragment to start shared transitions

        // 0. if we are on two pane mode

        // begin if we are two pane
        if ( mTwoPane == true ) {

            // 0a. replace the current detail fragment with a detail fragment
            // showing the selected item's information

            // I think we do not use shared transitions in tab, no?
            DetailFragment detailFragment = DetailFragment.newInstance( dateUri, false );

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace( R.id.am_cv_weather_detail_container, detailFragment )
                    .commit();

        } // end if we are two pane

        // 1. otherwise

        // begin else we are single pane
        else {

            // 1a. start the detail activity

            // 1a0. pair the correct items for shared element transitioning

            // 1a1. use the transition bundle the pair

            Bundle transitionBundle = ActivityOptionsCompat.makeSceneTransitionAnimation( this,
                    new Pair< View, String >( weatherViewHolder.mIconImageView,
                            getString( R.string.detail_icon_transition_name ) ) ).toBundle();

            // 1a2. tell the detail fragment to start shared transitions

            Intent detailsIntent = new Intent( this, DetailActivity.class ).setData( dateUri );

            ActivityCompat.startActivity( this, detailsIntent, transitionBundle );

        } // end else we are single pane

    } // end onForecastItemSelected

    /**
     * Helper method to check if Google Play Services are enabled on the device.
     *
     * If there is no available Google Play Services APK, display a dialog that allows users to
     * download it from Google Play Store or enable it from system settings.
     *
     * @return boolean dependent on if Google Play Services are enabled
     * */
    // begin method checkPlayServices
    private boolean checkPlayServices() {

        // 0. get an availability checker
        // 1. get a result code for the availability
        // 2. if the service is not available
        // 2a. if there is something the user can do about it,
        // 2a1. tell them
        // 2b. otherwise
        // 2b1. log
        // 2b2. terminate the activity
        // 2c. return false
        // 3. return true since the service must be available by this point

        // 0. get an availability checker

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        // 1. get a result code for the availability

        int resultCode = apiAvailability.isGooglePlayServicesAvailable( this );

        // 2. if the service is not available

        // begin if the services are not in the device
        if ( resultCode != ConnectionResult.SUCCESS ) {

            // 2a. if there is something the user can do about it,
            // 2a1. tell them

            // if there is something the user can do
            if ( apiAvailability.isUserResolvableError( resultCode ) ) {
                apiAvailability.getErrorDialog( this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST ).show();
            }

            // 2b. otherwise

            // begin else nothing the user can do
            else {

                // 2b1. log

                Log.i( LOG_TAG, "checkPlayServices: This device is not supported." );

                // 2b2. terminate the activity

                finish();

            } // end else nothing the user can do

            // 2c. return false

            return false;

        } // end if the services are not in the device

        // 3. return true since the service must be available by this point

        return true;

    } // end method checkPlayServices

    /* Other Methods */

} // end activity MainActivity
