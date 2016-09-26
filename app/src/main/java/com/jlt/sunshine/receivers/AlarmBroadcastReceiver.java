/*
 * 
 * com.jlt.sunshine.receivers
 * 
 * <one line to give the program's name and a brief idea of what it does.>
 * 
 * Copyright (C) 2016 Kairu Joshua Wambugu
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */

package com.jlt.sunshine.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jlt.sunshine.R;
import com.jlt.sunshine.sync.SunshineSyncAdapter;

/**
 * A {@link BroadcastReceiver} to receive the alarm sent by the {@link android.app.AlarmManager}
 * in {@link com.jlt.sunshine.ForecastFragment}.
 * */
// begin class AlarmBroadcastReceiver
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /** The logger. */
    private static final String LOG_TAG = AlarmBroadcastReceiver.class.getSimpleName();
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.
     *
     * In our case, it is called when time is right for the sunshine fetch weather service to start.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    // begin onReceive
    public void onReceive( Context context, Intent intent ) {

        // 0. set the bundle we will to sync
        // 0a. put an argument to sync asap
        // 0b. put an argument to sync manually
        // 1. sync up!

        // 0. set the bundle we will to sync

        Bundle bundle = new Bundle();

        // 0a. put an argument to sync asap

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true );

        // 0b. put an argument to sync manually

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_MANUAL, true );

        // 1. sync up!

        ContentResolver.requestSync( SunshineSyncAdapter.getSyncAccount( context ),
                context.getString( R.string.content_authority ), bundle );

//        // 0. create an intent to start the service
//        // 1. put the location query in it
//        // 2. start the service
//
//        // 0. create an intent to start the service
//
//        Intent sendIntent = new Intent( context, SunshineService.class );
//
//        // 1. put the location query in it
//
//        sendIntent.putExtra( SunshineService.EXTRA_LOCATION,
//                intent.getStringExtra( SunshineService.EXTRA_LOCATION ) );
//
//        // 2. start the service
//
//        context.startService( sendIntent );

    } // end onReceive
    
    /* Other Methods */
    
    /* INNER CLASSES */

} // end class AlarmBroadcastReceiver