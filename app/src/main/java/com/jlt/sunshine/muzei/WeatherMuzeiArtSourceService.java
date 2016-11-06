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

package com.jlt.sunshine.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.jlt.sunshine.MainActivity;
import com.jlt.sunshine.data.Utility;
import com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;
import com.jlt.sunshine.sync.SunshineSyncAdapter;

import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;

/**
 * {@link android.app.Service} to provide art for Muzei
 * */
// begin activity WeatherMu`zeiArtSourceService
public class WeatherMuzeiArtSourceService extends MuzeiArtSource {

    /* CONSTANTS */

    /* Arrays */

    private final static String WEATHER_PROJECTION[] = { COLUMN_WEATHER_ID }; // ditto

    /* Integers */

    private final static int INDEX_WEATHER_CONDITION_ID = 0; // ditto
    
    /* Strings */
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */

    // default empty constructor
    public WeatherMuzeiArtSourceService() {
        super( "WeatherMuzeiArtSourceService" );
    }
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    /**
     * Called on occasions where the source should probably publish an artwork update.
     * */
    // begin onUpdate
    protected void onUpdate( int reason ) {

        // 0. get the weather for today from db
        // 1. if the resulting cursor has something
        // 1a. get the weather condition id
        // 1b. get a url corresponding to the weather condition
        // 1c. if we have a corresponding url
        // 1c0. publish the artwork
        // 1c0a. use the url
        // 1c0b. use the weather description as artwork title
        // 1c0c. use the current location as artwork byline
        // 1c0d. should this artwork be tapped, intent to the main activity
        // 1c0e. publish!
        // 1d. close the cursor

        // 0. get the weather for today from db

        String currentLocation = Utility.getPreferredLocation( this );

        Uri queryUri = WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                currentLocation, System.currentTimeMillis() );

        Cursor cursor = getContentResolver().query( queryUri, WEATHER_PROJECTION, null, null, null );

        // 1. if the resulting cursor has something

        // begin if the cursor is and has something
        if ( cursor != null && cursor.moveToFirst() == true ) {

            // 1a. get the weather condition id

            int weatherConditionId = cursor.getInt( INDEX_WEATHER_CONDITION_ID );

            // 1b. get a url corresponding to the weather condition

            String imageUrl = Utility.getImageUrlForWeatherCondition( weatherConditionId );

            // 1c. if we have a corresponding url

            // begin if there is a url
            if ( imageUrl != null ) {

                // 1c0. publish the artwork

                String description = Utility.getStringForWeatherCondition( this, weatherConditionId );

                publishArtwork( new Artwork.Builder()

                        // 1c0a. use the url
                        .imageUri( Uri.parse( imageUrl ) )

                        // 1c0b. use the weather description as artwork title
                        .title( description )

                        // 1c0c. use the current location as artwork byline
                        .byline( currentLocation )

                        // 1c0d. should this artwork be tapped, intent to the main activity
                        .viewIntent( new Intent( this, MainActivity.class ) )

                        // 1c0e. publish!
                        .build()

                );

            } // end if there is a url

            // 1d. close the cursor

            cursor.close();

        } // end if the cursor is and has something

    } // end onUpdate

    @Override
    // begin onHandleIntent
    protected void onHandleIntent( Intent intent ) {

        // 0. super stuff
        // 1. know if we got the action data updated broadcast
        // 2. if we have the broadcast and Muzei is enabled
        // 2a. trigger update

        // 0. super stuff

        super.onHandleIntent( intent );

        // 1. know if we got the action data updated broadcast

        boolean dataUpdated = ( intent != null &&
                intent.getAction().equals( SunshineSyncAdapter.ACTION_DATA_UPDATED ) == true );

        // 2. if we have the broadcast and Muzei is enabled

        // 2a. trigger update

        // isEnabled - Returns true if this source is enabled; that is, if there is
        //  at least one active subscriber.
        if ( dataUpdated == true && isEnabled() == true ) {
            // UPDATE_REASON_OTHER - onUpdate(int) was triggered for some reason not represented by
            //  another known reason constant.
            onUpdate( UPDATE_REASON_OTHER );
        }


    } // end onHandleIntent

    /* Other Methods */
    
    /* INNER CLASSES */

} // end activity WeatherMuzeiArtSourceService