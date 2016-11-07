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

package com.joslitto.sunshine.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.joslitto.sunshine.sync.SunshineSyncAdapter;

/**
 * An {@link android.appwidget.AppWidgetProvider} for the today widget.
 * */
// begin class TodayWidgetProvider
public class TodayWidgetProvider extends AppWidgetProvider {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onUpdate
    // called as many times as stated in the AppWidgetProvider XML's android:updatePeriodMillis
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {

        // 0. update the widget by starting the today widget intent service

        // 0. update the widget by starting the today widget intent service

        Intent todayWidgetUpdateIntent = new Intent( context, TodayWidgetIntentService.class );

        context.startService( todayWidgetUpdateIntent );

    } // end onUpdate

    @Override
    /*
    Called in response to the AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED broadcast when
    this widget has been laid out at a new size.
    */
    // begin onAppWidgetOptionsChanged
    public void onAppWidgetOptionsChanged( Context context, AppWidgetManager appWidgetManager,
                                           int appWidgetId, Bundle newOptions ) {

        // seems to be called during a widget resize

        // 0. update the widget with the latest weather

        // 0. update the widget with the latest weather

        context.startService( new Intent( context, TodayWidgetIntentService.class ) );

    } // end onAppWidgetOptionsChanged

    @Override
    // begin onReceive
    public void onReceive( Context context, Intent intent ) {

        // 0. super stuff
        // 1. if we have received the data updated broadcast from the sync adapter
        // 1a. update the widget by starting the today widget intent service

        // 0. super stuff

        super.onReceive( context, intent );

        // 1. if we have received the data updated broadcast from the sync adapter

        // 1a. update the widget by starting the today widget intent service

        if ( intent.getAction() != null &&
                intent.getAction().equals( SunshineSyncAdapter.ACTION_DATA_UPDATED ) == true ) {
            context.startService( new Intent( context, TodayWidgetIntentService.class ) );
        }

    } // end onReceive

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class TodayWidgetProvider