package com.jlt.sunshine;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

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
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    /* CONSTANTS */

    /* VARIABLES */

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

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. add 'general' preferences from XML

        addPreferencesFromResource( R.xml.pref_general );

        // 2. for all preferences, attach a change listener so that their summaries can be updated on preference change

        bindPreferenceSummaryToValue( findPreference( getString( R.string.pref_location_key ) ) );
        bindPreferenceSummaryToValue( findPreference( getString( R.string.pref_temperature_unit_key ) ) );

    } // end onCreate

    @Override
    // begin onPreferenceChange
    // newValue - the new value of the preference
    public boolean onPreferenceChange( Preference changedPreference, Object newValue ) {

        // 0. get the new preference value in string form
        // 1. if the changed preference was a list
        // 1a. check the correct value to display from the list's entries
        // 2. for other prefs
        // 2a. use the new pref value string for the summary
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

            if ( preferenceIndex >= 0 ) { changedPreference.setSummary( listPreference.getEntries()[ preferenceIndex ] ); }

        } // end if the changed preference was list

        // 2. for other prefs

        // 2a. use the new pref value string for the summary

        else { changedPreference.setSummary( preferenceString ); }

        // last. terminate

        return true;

    } // end onPreferenceChange

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

} // end preference activity SettingsActivity