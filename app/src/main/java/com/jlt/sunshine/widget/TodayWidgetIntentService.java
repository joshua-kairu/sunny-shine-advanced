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

package com.jlt.sunshine.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import com.jlt.sunshine.MainActivity;
import com.jlt.sunshine.R;
import com.jlt.sunshine.data.Utility;

import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_DATE;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_SHORT_DESCRIPTION;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;
import static com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry.buildWeatherForLocationWithSpecificDateUri;

/**
 * {@link IntentService} to update the widget serviced by {@link TodayWidgetProvider}
 * */
// begin class TodayWidgetIntentService
public class TodayWidgetIntentService extends IntentService {

    /* CONSTANTS */

    /* Arrays */

    private static String[] FORECAST_COLUMNS = {
            COLUMN_WEATHER_ID,
            COLUMN_SHORT_DESCRIPTION,
            COLUMN_MAX_TEMP };

    /* Integers */

    private static int INDEX_WEATHER_ID = 0, INDEX_SHORT_DESCRIPTION = 1, INDEX_MAX_TEMP = 2;
    
    /* Strings */

    /* VARIABLES */
    
    /* CONSTRUCTOR */

    // default constructor
    public TodayWidgetIntentService() {
        super( TodayWidgetIntentService.class.getSimpleName() );
    }
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @SuppressLint( "CommitPrefEdits" )
    @Override
    // begin onHandleIntent
    protected void onHandleIntent( Intent intent ) {

        // 0. get the app widget manager
        // 1. get the app widget IDs
        // 2. get today's data
        // 2a. stop if there's no data
        // 2b. stop if we can't get to a row
        // 2c. get forecast icon
        // 2d. get description
        // 2e. get high temperature
        // 2last. close cursor
        // 3. update the widget
        // 3a. get a remote view for the small today widget
        // 3b. put weather data
        // 3c. create a pending intent to go to the main activity
        // 3d. use that pending intent for when the widget is clicked
        // 3last. update this widget with the remote view

        // 0. get the app widget manager

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( this );

        // 1. get the app widget IDs

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName( this, TodayWidgetProvider.class ) );

        // 2. get today's data

        String locationSetting = Utility.getPreferredLocation( this );

        String sortOrder = COLUMN_DATE + " ASC";

        Cursor todayCursor = getContentResolver().query(
                buildWeatherForLocationWithSpecificDateUri( locationSetting,
                        System.currentTimeMillis() ),
                FORECAST_COLUMNS, null, null, sortOrder );

        // 2a. stop if there's no data

        if ( todayCursor == null ) { return; }

        // 2b. stop if we can't get to a row

        if ( todayCursor.moveToFirst() == false ) { todayCursor.close(); return; }

        // 2c. get forecast icon

        int todayWeatherResourceID = Utility.getArtResourceForWeatherCondition(
                todayCursor.getInt( INDEX_WEATHER_ID ) );

        // 2d. get description

        String todayDescription = todayCursor.getString( INDEX_SHORT_DESCRIPTION );

        // 2e. get high temperature

        String todayMaxTemperature = Utility.formatTemperature( this,
                todayCursor.getFloat( INDEX_MAX_TEMP ) );

        // 2last. close cursor

        todayCursor.close();

        // 3. update the widget

        // begin for through the widget IDs
        for ( int appWidgetId : appWidgetIds ) {

            // 3a. get a remote view for the small today widget
            // 3b. put weather data
            // 3c. create a pending intent to go to the main activity
            // 3d. use that pending intent for when the widget is clicked
            // 3last. update this widget with the remote view

            // 3a. get a remote view for the small today widget

            RemoteViews views = new RemoteViews( getPackageName(), R.layout.widget_today_small );

            // 3b. put weather data

            views.setImageViewResource( R.id.widget_icon, todayWeatherResourceID );

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
                setRemoteContentDescription( views, todayDescription );
            }

            views.setTextViewText( R.id.widget_high_temperature, todayMaxTemperature );

            // 2b4. close cursor

            todayCursor.close();

            // 3c. create a pending intent to go to the main activity

            PendingIntent pendingIntent = PendingIntent.getActivity( this, 0,
                    new Intent( this, MainActivity.class ), 0 );

            // 3d. use that pending intent for when the widget is clicked

            // setOnClickPendingIntent - when the given view is clicked, fire the given PendingIntent
            views.setOnClickPendingIntent( R.id.widget, pendingIntent );

            // 3last. update this widget with the remote view

            appWidgetManager.updateAppWidget( appWidgetId, views );

        } // end for through the widget IDs

    } // end onHandleIntent

    /**
     * Helper method to set content descriptions for {@link RemoteViews}.
     * This feature was added in ICS MR1.
     *
     * @param views The {@link RemoteViews} to describe.
     * @param description The content description for the {@link RemoteViews}.
     * */
    @TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 )
    // method setRemoteContentDescription
    private void setRemoteContentDescription( RemoteViews views, String description ) {
        views.setContentDescription( R.id.widget_icon, description );
    }

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class TodayWidgetIntentService