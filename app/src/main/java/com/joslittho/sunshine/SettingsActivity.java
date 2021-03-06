package com.joslittho.sunshine;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.joslittho.sunshine.data.Utility;
import com.joslittho.sunshine.data.contract.WeatherContract;
import com.joslittho.sunshine.sync.SunshineSyncAdapter.LocationStatus;

import static com.joslittho.sunshine.sync.SunshineSyncAdapter.LOCATION_STATUS_INVALID;
import static com.joslittho.sunshine.sync.SunshineSyncAdapter.LOCATION_STATUS_OK;
import static com.joslittho.sunshine.sync.SunshineSyncAdapter.LOCATION_STATUS_SERVER_UNKNOWN;
import static com.joslittho.sunshine.sync.SunshineSyncAdapter.syncImmediately;

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

/** The settings manager. */
// begin preference activity SettingsActivity
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    /* CONSTANTS */

    /* Integers */

    /** Identifier for the result from {@link com.google.android.gms.location.places.ui.PlacePicker}. */
    public static final int PLACE_PICKER_REQUEST = 1;

    /* VARIABLES */

    /* Image Views */

    private ImageView mAttributionImageView; // ditto, for Powered by Google when using the Places API

    /* METHODS */

    /* Getters and Setters */

    /*
     * Overrides
     */

    @Override
    // begin onCreate
    protected void onCreate( Bundle savedInstanceState ) {

        // 0. super things
        // 1. add 'general' preferences from XML
        // 2. for all preferences, attach a change listener so that their summaries can be updated on preference change
        // 3. if we are using Places, we need to attribute the creators
        // 3a. if we are on Honeycomb
        // 3a0. initialize the attribution image view
        // 3a1. use the Powered by Google image
        // 3a2. we shouldn't see this if we are not using Places
        // 3a3. put the attribution image at the footer region

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. add 'general' preferences from XML

        addPreferencesFromResource( R.xml.pref_general );

        // 2. for all preferences, attach a change listener so that their summaries can be updated on preference change

        bindPreferenceSummaryToValue( findPreference( getString( R.string.pref_location_key ) ) );
        bindPreferenceSummaryToValue( findPreference( getString( R.string.pref_temperature_unit_key ) ) );
        bindPreferenceSummaryToValue( findPreference( getString( R.string.pref_icon_pack_key ) ) );

        // 3. if we are using Places, we need to attribute the creators

        // 3a. if we are on Honeycomb

        // begin if we are at least honeycomb
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

            // 3a0. initialize the attribution image view

            mAttributionImageView = new ImageView( this );

            // 3a1. use the Powered by Google image

            mAttributionImageView.setImageResource( R.drawable.powered_by_google_light );

            // 3a2. we shouldn't see this if we are not using Places

            if ( Utility.isLocationLatLongAvailable( this ) == false ) {
                mAttributionImageView.setVisibility( View.GONE );
            }

            // 3a3. put the attribution image at the footer region

            // Set a footer that should be shown at the bottom of the header list.
            setListFooter( mAttributionImageView );

        } // end if we are at least honeycomb

    } // end onCreate

    @Override
    // begin onResume
    protected void onResume() {

        // 0. register this as a listener for preference change
        // last. super stuff

        // 0. register this as a listener for preference change

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        sharedPreferences.registerOnSharedPreferenceChangeListener( this );

        // last. super stuff

        super.onResume();

    } // end onResume

    @Override
    // begin onPause
    protected void onPause() {

        // 0. unregister this as a listener for preference change
        // last. super stuff

        // 0. unregister this as a listener for preference change

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        sharedPreferences.unregisterOnSharedPreferenceChangeListener( this );

        // last. super stuff

        super.onPause();

    } // end onPause

    @SuppressLint( "CommitPrefEdits" )
    @Override
    // begin onActivityResult
    // This is where the places request from LocationEditTextPreference is handled
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {

        // 0. super stuff
        // 1. if the request code is the places one
        // 1a. if the result is OK
        // 1a0. get location latitude from the new place and store it
        // 1a1. get location longitude from the new place and store it
        // 1a2. store the place address in shared preferences with a COMMIT not APPLY since
        // the sync will need this information for success
        // 1a3. show the new place in the location preference summary
        // 1a4. reset location status as prep for sync
        // 1a5. resync

        // 0. super stuff

        super.onActivityResult( requestCode, resultCode, data );

        // 1. if the request code is the places one

        // begin if the request code is places
        if ( requestCode == PLACE_PICKER_REQUEST ) {

            // 1a. if the result is OK

            // begin if result of OK
            if ( resultCode == RESULT_OK ) {

                // 1a0. get location latitude from the new place and store it with a COMMIT not APPLY since
                // the sync will need this information for success
                // 1a1. get location longitude from the new place and COMMIT store it
                // 1a2. COMMIT store the place address in shared preferences
                // 1a3. show the new place in the location preference summary
                // 1a3a. if the new place doesn't have an address, show a friendly coordinate combo
                // 1a4. show attribution
                // 1a4a. for Honeycomb and above, use the footer
                // 1a4b. for pre-Honeycomb use a snackbar since footers are not available
                // 1a5. reset location status as prep for sync
                // 1a6. resync

                Place place = PlacePicker.getPlace( data, this );

                // 1a0. get location latitude from the new place and store it with a COMMIT not APPLY since
                // the sync will need this information for success -> storage deferred
                // to when we have the longitude

                float latitude = ( float ) place.getLatLng().latitude;

                // 1a1. get location longitude from the new place and COMMIT store it

                float longitude = ( float ) place.getLatLng().longitude;

                PreferenceManager.getDefaultSharedPreferences( this ).edit()
                        .putFloat( getString( R.string.pref_location_latitude_key ), latitude )
                        .putFloat( getString( R.string.pref_location_longitude_key ), longitude )
                        .commit();

                // 1a2. COMMIT store the place address in shared preferences

                String placeAddress = place.getAddress().toString();

                PreferenceManager.getDefaultSharedPreferences( this ).edit()
                        .putString( getString( R.string.pref_location_key ), placeAddress )
                        .commit();

                // 1a3. show the new place in the location preference summary

                Preference locationPreference = findPreference( getString( R.string.pref_location_key ) );

                // 1a3a. if the new place doesn't have an address, show a friendly coordinate combo

                if ( TextUtils.isEmpty( placeAddress ) == true ) {
                    placeAddress = getString( R.string.pref_location_summary_unnamed_place_address,
                            latitude, longitude );
                }

                locationPreference.setSummary( placeAddress );

                // 1a4. show attribution

                // 1a4a. for Honeycomb and above, use the footer

                if ( mAttributionImageView != null ) {
                    mAttributionImageView.setVisibility( View.VISIBLE );
                }

                // 1a4b. for pre-Honeycomb use a snackbar since footers are not available

                // begin else pre Honeycomb attribution
                else {

                    View rootView = findViewById( android.R.id.content );

                    Snackbar.make( rootView, getString( R.string.message_info_places_attribution_text ),
                            Snackbar.LENGTH_LONG ).show();

                } // end else pre Honeycomb attribution

                // 1a5. reset location status as prep for sync

                Utility.resetLocationStatus( this );

                // 1a6. resync

                syncImmediately( this );

            } // end if result of OK

        } // end if the request code is places

    } // end onActivityResult

    @Override
    // begin onPreferenceChange
    // this is called BEFORE the preference is CHANGEd.
    // newValue - the new value of the preference
    public boolean onPreferenceChange( Preference changedPreference, Object newValue ) {

        // 0. get the new preference value in string form
        // 1. if the changed preference was a list
        // 1a. check the correct value to display from the list's entries
        // 2. if the changed preference was the location
        // 2a. get the edit text preference for the location
        // 2b. put a summary based on location status
        // 2b1. location invalid
        // 2b2. location unknown
        // 2b3. location OK
        // 3. for other prefs
        // 3a. use the new pref value string for the summary
        // last. terminate

        // 0. get the new preference value in string form

        String preferenceString = newValue.toString();

        // 1. if the changed preference was a list

        // begin if the changed preference was list
        if ( changedPreference instanceof ListPreference ) {

            // 1a. check the correct value to display from the list's entries

            ListPreference listPreference = ( ListPreference ) changedPreference;

            // findIndexOfValue - find the index of passed value in the entry values array
            int preferenceIndex = listPreference.findIndexOfValue( preferenceString );

            if ( preferenceIndex >= 0 ) {
                changedPreference.setSummary( listPreference.getEntries()[ preferenceIndex ] );
            }

        } // end if the changed preference was list

        // 2. if the changed preference was the location

        // begin else if the changed preference is the location status
        else if ( changedPreference instanceof EditTextPreference ) {

            // 2a. get the edit text preference for the location

            EditTextPreference locationPreference = ( EditTextPreference ) changedPreference;

            // 2b. put a summary based on location status

            locationPreference.setSummary( getLocationSummary( preferenceString ) );

        } // end else if the changed preference is the location status

        // 3. for other prefs

        // 3a. use the new pref value string for the summary

        else { changedPreference.setSummary( preferenceString ); }

        // last. terminate

        return true;

    } // end onPreferenceChange

    @SuppressLint( "CommitPrefEdits" )
    @Override
    // begin onSharedPreferenceChanged
    // This is called AFTER the preference is CHANGEd
    // Very important since we start our sync here
    public void onSharedPreferenceChanged( SharedPreferences changedSharedPreferences,
                                           String changedSharedPreferenceKey ) {

        // 1. if the changed preference was the location status
        // 1a. reset the location status preference
        // 1b. resync
        // 2. if the changed preference was the units one
        // 2a. update the weather entry list with the new units
        // 3. if the changed preference was the location one
        // 3a. update the summary to show that we are in the process of refreshing
        // 3b. remove latitude and longitude from the preferences since
        // by editing this preference the user wants to use manual locations, and COMMIT, not APPLY
        // since we will sync after this and we want the removal to have finished by then
        // 3c. remove any attributions
        // 3d. reset location status as prep for the sync
        // 3e. resync
        // 4. if the changed preference was the icon pack one
        // 4a. update lists of weather entries accordingly

        // begin if it was location status changed
        if ( changedSharedPreferenceKey
                .equals( getString( R.string.pref_location_status_key ) ) == true ) {

            // 1a. reset the location status preference
            // 1b. resync

            // 1a. reset the location status preference

            Utility.resetLocationStatus( this );

            // 1b. resync

            syncImmediately( this );

        } // end else if it was location status changed

        // 2. if the changed preference is the units one

        // begin else if its the units that have changed
        else if ( changedSharedPreferenceKey
                .equals( getString( R.string.pref_temperature_unit_key ) ) == true ) {

            // 2a. update the weather entry list with the new units

            getContentResolver().notifyChange( WeatherContract.WeatherEntry.CONTENT_URI, null );

        } // end else if its the units that have changed

        // 3. if the changed preference was the location one

        // begin else if its the location that changed
        else if ( changedSharedPreferenceKey.equals( getString( R.string.pref_location_key ) ) ) {

            // 3a. update the summary to show that we are in the process of refreshing

            Preference locationPreference = findPreference( changedSharedPreferenceKey );

            String preferredLocation = Utility.getPreferredLocation( this );

            locationPreference.setSummary(
                    getString( R.string.pref_location_summary_unknown, preferredLocation )
            );

            // 3b. remove latitude and longitude from the preferences since
            // by editing this preference the user wants to use manual locations, and COMMIT, not APPLY
            // since we will sync after this and we want the removal to have finished by then

            PreferenceManager.getDefaultSharedPreferences( this ).edit()
                    .remove( getString( R.string.pref_location_latitude_key ) )
                    .remove( getString( R.string.pref_location_longitude_key ) )
                    .commit();

            // 3c. remove any attributions

            if ( mAttributionImageView != null ) {
                mAttributionImageView.setVisibility( View.GONE );
            }

            // 3d. reset location status as prep for the sync

            Utility.resetLocationStatus( this );

            // 3e. resync

            syncImmediately( this );

        } // end else if its the location that changed

        // 4. if the changed preference was the icon pack one

        // begin else if it's icon pack that's changed
        else if ( changedSharedPreferenceKey.equals( getString( R.string.pref_icon_pack_key ) ) ) {

            // 4a. update lists of weather entries accordingly

            getContentResolver().notifyChange( WeatherContract.WeatherEntry.CONTENT_URI, null );

        } // end else if it's icon pack that's changed

    } // end onSharedPreferenceChanged

    /**
     * Obtain an {@link Intent} that will launch an explicit target activity specified by
     * this activity's logical parent. The logical parent is named in the application's manifest
     * by the {@link android.R.attr#parentActivityName parentActivityName} attribute.
     * Activity subclasses may override this method to modify the Intent returned by
     * super.getParentActivityIntent() or to implement a different mechanism of retrieving
     * the parent intent entirely.
     *
     * @return a new Intent targeting the defined parent of this activity or null if
     * there is no valid parent.
     */
    @TargetApi( Build.VERSION_CODES.JELLY_BEAN )
    @Nullable
    @Override
    // begin getParentActivityIntent
    public Intent getParentActivityIntent() {

        // FLAG_ACTIVITY_CLEAR_TOP
        //  If set, and the activity being launched is already running in the current task,
        //  then instead of launching a new instance of that activity,
        //  all of the other activities on top of it will be closed
        //  and this Intent will be delivered to the (now on top) old activity as a new Intent.
        //
        //  For example, consider a task consisting of the activities: A, B, C, D.
        //  If D calls startActivity() with an Intent that resolves to the component of activity B,
        //  then C and D will be finished and B receive the given Intent,
        //  resulting in the stack now being: A, B.
        return super.getParentActivityIntent().addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

    } // end getParentActivityIntent

    /*
     * Other Methods
     */

    /**
     * Attaches a listener so that the summary is always updated with the preference's value.
     * Also fires the listener once, to initialize the summary
     * (so that the summary shows up before it is changed)
     */
    // begin method bindPreferenceSummaryToValue
    private void bindPreferenceSummaryToValue( Preference preference ) {

        // 0. set the listener to watch for value changes
        // 1. trigger the listener immediately with the preference's current value

        // 0. set the listener to watch for value changes

        preference.setOnPreferenceChangeListener( this );

        // 1. trigger the listener immediately with the preference's current value

        onPreferenceChange(
                preference,
                PreferenceManager
                        .getDefaultSharedPreferences( preference.getContext() )
                        .getString( preference.getKey(), "" )
        );

    } // end method bindPreferenceSummaryToValue


    /**
     * Returns the location summary based on the location status.
     *
     * @param preferenceString The string representing
     *                         what is currently input as location preference.
     *
     * @return Returns a summary based on the location status.
     * */
    // begin method getLocationSummary
    private String getLocationSummary( String preferenceString ) {

        // 0. summary based on location status
        // 0a. location invalid
        // 0b. location unknown
        // 0c. location OK
        // 1. return the summary

        // 0. summary based on location status

        @LocationStatus int locationStatus = Utility.getLocationStatus( this );

        // begin switching the location status
        switch ( locationStatus ) {

            // 0a. location invalid

            case LOCATION_STATUS_INVALID:
                return getString( R.string.pref_location_summary_invalid, preferenceString );

            // 0b. location unknown

            case LOCATION_STATUS_SERVER_UNKNOWN:
                return getString( R.string.pref_location_summary_unknown, preferenceString );

            // 0c. location OK

            case LOCATION_STATUS_OK:
            default:
                return preferenceString;

        } // end switching the location status

    } // end method updateLocationSummary

} // end preference activity SettingsActivity