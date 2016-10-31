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

package com.jlt.sunshine.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.jlt.sunshine.MainActivity;
import com.jlt.sunshine.R;
import com.jlt.sunshine.data.Utility;

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
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {

        // 0. update the widget
        // 0a. for the today widget this is easy since it is a static widget
        // 0a0. get a remote view for the small today widget
        // 0a1. put some static data
        // 0a2. create a pending intent to go to the main activity
        // 0a3. use that pending intent for when the widget is clicked
        // last. update this widget with the remote view

        // 0. update the widget

        // 0a. for the today widget this is easy since it is a static widget

        // begin for through the widget IDs
        for ( int appWidgetId : appWidgetIds ) {

            // 0a0. get a remote view for the small today widget
            // 0a1. put some static data
            // 0a2. create a pending intent to go to the main activity
            // 0a3. use that pending intent for when the widget is clicked

            // 0a0. get a remote view for the small today widget

            RemoteViews views = new RemoteViews( context.getPackageName(),
                    R.layout.widget_today_small );

            // 0a1. put some static data

            int todayWeatherResourceID = R.drawable.art_clear;

            String todayMaxTemperature = Utility.formatTemperature( context, 24.0f );

            views.setImageViewResource( R.id.widget_icon, todayWeatherResourceID );

            views.setTextViewText( R.id.widget_high_temperature, todayMaxTemperature );

            // 0a2. create a pending intent to go to the main activity

            PendingIntent pendingIntent = PendingIntent.getActivity( context, 0,
                    new Intent( context, MainActivity.class ), 0 );

            // 0a3. use that pending intent for when the widget is clicked

            // setOnClickPendingIntent - when the given view is clicked, fire the given PendingIntent
            views.setOnClickPendingIntent( R.id.widget, pendingIntent );

            // last. update this widget with the remote view

            appWidgetManager.updateAppWidget( appWidgetId, views );

        } // end for through the widget IDs

    } // end onUpdate

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class TodayWidgetProvider