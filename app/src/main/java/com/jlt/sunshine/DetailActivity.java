package com.jlt.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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

// begin activity DetailActivity
// shows the details of the weather
public class DetailActivity extends AppCompatActivity {

    /** CONSTANTS */

    /** VARIABLES */

    /** METHODS */

    /** Getters and Setters */

    /** Overrides */

    @Override
    // begin onCreate
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_detail );

        if ( savedInstanceState == null ) {

            getSupportFragmentManager().beginTransaction()
                    .add( R.id.ad_fl_container, new DetailFragment() )
                    .commit();

        }

    } // end onCreate


    @Override
    // begin onOptionsItemSelected
    public boolean onOptionsItemSelected(MenuItem item) {

        // 0. get the selected item
        // 1a. if it is settings
        // 1a1. go to the settings
        // last. terminate

        // 0. get the selected item

        int id = item.getItemId();

        // 1a. if it is settings

        // begin if it's the settings
        if ( id == R.id.action_settings ) {

            // 1a1. go to the settings

            Intent settingsIntent = new Intent( this, SettingsActivity.class );

            startActivity( settingsIntent );

            return true;

        } // end if it's the settings

        // last. terminate

        return super.onOptionsItemSelected( item );

    } // end onOptionsItemSelected

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }


    }

    /** Other Methods */


} // end activity DetailActivity
