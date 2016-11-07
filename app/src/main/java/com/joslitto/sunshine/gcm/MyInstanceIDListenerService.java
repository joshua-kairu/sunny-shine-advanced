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

package com.joslitto.sunshine.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * {@link InstanceIDListenerService} to get us the tokens for GCM communication.
 * */
// begin class MyInstanceIDListenerService
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    /**
     * Called if the {@link com.google.android.gms.iid.InstanceID} is updated.
     * This may occur if the previous token becomes insecure.
     * This method is called by the {@link com.google.android.gms.iid.InstanceID} provider.
     * */
    @Override
    // begin onTokenRefresh
    public void onTokenRefresh() {

        // 0. fetch updated InstanceID token via registration service

        // 0. fetch updated InstanceID token via registration service

        Intent intent = new Intent( this, RegistrationIntentService.class );
        startService( intent );

    } // end onTokenRefresh

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class MyInstanceIDListenerService