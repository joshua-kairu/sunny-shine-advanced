package com.jlt.sunshine;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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

// begin preference activity SettingsActivity
// the settings manager
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    /** CONSTANTS */

    /** VARIABLES */

    /** METHODS */

    /** Getters and Setters */

    /**
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