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

package com.joslittho.sunshine.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.joslittho.sunshine.MainActivity;
import com.joslittho.sunshine.R;

import java.io.IOException;

/**
 * An {@link IntentService} to allow one to register for GCM
 * */
// begin class RegistrationIntentService
public class RegistrationIntentService extends IntentService {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();
        
    /* VARIABLES */

    /* CONSTRUCTOR */

    // default empty constructor
    public RegistrationIntentService() {
        super( LOG_TAG );
    }
    
    /* METHODS */
    
    /* Getters and Setters */

    /* Overrides */

    @Override
    // begin onHandleIntent
    protected void onHandleIntent( Intent intent ) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        // 0. get an instance id
        // 1. get the token for this instance id
        // 2. send token to server
        // 3. store boolean true we have sent the token to the server

        // begin trying to register
        try {

            // begin synchronized around LOG_TAG
            // in the (unlikely) event that multiple refreshes occur,
            // this will ensure that they are processed sequentially
            synchronized ( LOG_TAG ) {

                // 0. get an instance id

                // getInstance - first time called from net to retrieve the token
                //  subsequent calls are local
                InstanceID instanceID = InstanceID.getInstance( this );

                // 1. get the token for this instance id

                String token = instanceID.getToken( getString( R.string.gcm_defaultSenderId ),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null );

                // 2. send token to server

                sendTokenToServer( token );

                // 3. store boolean true we have sent the token to the server

                sharedPreferences.edit()
                        .putBoolean( MainActivity.PREF_SENT_TOKEN_KEY, true ).apply();

            } // end synchronized around LOG_TAG

        } // end trying to register

        // begin catching IO issues
        catch ( IOException | IncompatibleClassChangeError e ) {

            // 0. log
            // 1. store boolean false we haven't sent the token

            // 0. log

            Log.d( LOG_TAG, "onHandleIntent: Cannot get the GCM token.", e );

            // 1. store boolean false we haven't sent the token

            sharedPreferences.edit()
                    .putBoolean( MainActivity.PREF_SENT_TOKEN_KEY, false ).apply();

        } // end catching IO issues

    } // end onHandleIntent

    /**
     * Helper method to send the token to our server so that the server can send messages to just
     * this device. Normally you want to persist registration to third-party servers.
     *
     * We do not have a server now so we will just log the token. But TOKENS are not to be LOGGED
     * WILLY-NILLY.
     *
     * @param token The GCM token
     * */
    // begin method sendTokenToServer
    private void sendTokenToServer( String token ) {

        Log.i( LOG_TAG, "sendTokenToServer: Token: " + token );

    } // end method sendTokenToServer

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class RegistrationIntentService