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

package com.jlt.sunshine.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.jlt.sunshine.MainActivity;
import com.jlt.sunshine.R;

/**
 * A {@link com.google.android.gms.gcm.GcmListenerService} to listen for downstream messages.
 * */
// begin class MyGcmListenerService
public class MyGcmListenerService extends GcmListenerService {

    /* CONSTANTS */
    
    /* Integers */

    public static final int NOTIFICATION_ID = 1; // for the notification

    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = MyGcmListenerService.class.getSimpleName();

    private static final String EXTRA_DATA = "data";
    private static final String EXTRA_LOCATION = "location";
    private static final String EXTRA_WEATHER = "weather";

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onMessageReceived
    public void onMessageReceived( String senderIdString, Bundle data ) {

        // 0. if the incoming bundle has something
        // 0a. if the something is meant for us
        // 0a1. decode it from the bundle
        // 0a2. display it as a notification

        // 0. if the incoming bundle has something

        // begin if the data is not empty
        if ( ! data.isEmpty() ) {

            // check that the local sender ID has something in it
            String senderId = getString( R.string.gcm_defaultSenderId );

            if ( senderId.length() == 0 ) {
                Toast.makeText( this, "SenderID string needs to be set", Toast.LENGTH_LONG ).show();
            }

            // 0a. if the something is meant for us

            // begin if the sender id is similar to our sender id
            if ( senderIdString.equals( getString( R.string.gcm_defaultSenderId ) ) ) {

                // 0a1. decode it from the bundle

                String weather = data.getString( EXTRA_WEATHER );

                String location = data.getString( EXTRA_LOCATION );

                String alert = getString( R.string.gcm_weather_alert, weather, location );

                // 0a2. display it as a notification

                sendNotification( alert );

            } // end if the sender id is similar to our sender id

        } // end if the data is not empty

    } // end onMessageReceived

    /**
     * Helper method to show the notification gotten from GCM.
     *
     * @param message The alert message to display in the notification
     * */
    // begin sendNotification
    private void sendNotification( String message ) {

        // 0. get the notification manager
        // 1. create a pending intent to take us to the main activity
        // 2. create a storm bitmap for the large icon
        // 3. build the notification
        // 3a. use the app icon as the small icon
        // 3b. use the storm bitmap as the large icon
        // 3c. put the correct title
        // 3d. use a large text style
        // 3e. put the content as the parameter message
        // 3f. priority is high
        // 3g. use the created pending intent as the content intent
        // 4. notify!

        // 0. get the notification manager

        NotificationManager notificationManager = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );

        // 1. create a pending intent to take us to the main activity

        PendingIntent notificationPendingIntent = PendingIntent.getActivity( this, 0,
                new Intent( this, MainActivity.class ), 0 );

        // 2. create a storm bitmap for the large icon

        Bitmap stormBitmap = BitmapFactory.decodeResource( getResources(), R.drawable.art_storm );

        // 3. build the notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder( this )

        // 3a. use the app icon as the small icon
            .setSmallIcon( R.drawable.art_clear )

        // 3b. use the storm bitmap as the large icon
             .setLargeIcon( stormBitmap )

        // 3c. put the correct title
            .setContentTitle( "Weather Alert!" )

        // 3d. use a large text style
            .setStyle( new NotificationCompat.BigTextStyle().bigText( message ) )

        // 3e. put the content as the parameter message
            .setContentText( message )

        // 3f. priority is high
            .setPriority( NotificationCompat.PRIORITY_HIGH )

        // 3g. use the created pending intent as the content intent
            .setContentIntent( notificationPendingIntent );

        // 4. notify!
        notificationManager.notify( NOTIFICATION_ID, builder.build() );

    } // end sendNotification

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class MyGcmListenerService