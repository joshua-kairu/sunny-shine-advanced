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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.joslitto.sunshine.DetailActivity;
import com.joslitto.sunshine.MainActivity;
import com.joslitto.sunshine.R;
import com.joslitto.sunshine.sync.SunshineSyncAdapter;

/**
 * {@link android.appwidget.AppWidgetProvider} for the detail widget.
 * */
@TargetApi( Build.VERSION_CODES.HONEYCOMB )
// begin class DetailWidgetProvider
public class DetailWidgetProvider extends AppWidgetProvider {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = DetailWidgetProvider.class.getSimpleName();

    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onUpdate
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {

        // 0. for each widget managed by this provider
        // 0a. get a remote views for the detail widget
        // 0b. create a pending intent to launch the main activity
        // 0c. set up the collection
        // 0d. create the intent template
        // 0d0. for phones show the details activity
        // 0d1. for tabs show the main activity
        // 0e. put the intent template into the remote views
        // 0f. set remote views empty view
        // 0g. update widget

        // 0. for each widget managed by this provider

        // begin for through all widget IDs
        for ( int appWidgetId : appWidgetIds ) {

            // 0a. get a remote views for the detail widget

            RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget_detail );

            // 0b. create a pending intent to launch the main activity

            PendingIntent launchIntent = PendingIntent.getActivity( context, 0,
                    new Intent( context, MainActivity.class ), 0 );

            views.setOnClickPendingIntent( R.id.widget_detail_fl_logo_container, launchIntent );

            // 0c. set up the collection

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
                setRemoteAdapter( context, views );
            }
            else {
                setRemoteAdapterV11( context, views );
            }

            // 0d. create the intent template

            boolean useDetailActivity = context.getResources().getBoolean( R.bool.use_detail_activity );

            // 0d0. for phones show the details activity

            // 0d1. for tabs show the main activity

            Intent clickIntentTemplate = useDetailActivity == true ?
                    new Intent( context, DetailActivity.class )
                    : new Intent( context, MainActivity.class );

            // 0e. put the intent template into the remote views

            // create - Return a new TaskStackBuilder for launching a fresh task stack
            //  consisting of a series of activities.
            PendingIntent templatePendingIntent = TaskStackBuilder.create( context )
                    // Add a new Intent with the resolved chain of parents
                    // for the target activity to the task stack.
                    .addNextIntentWithParentStack( clickIntentTemplate )
                    // getPendingIntent - Obtain a PendingIntent for launching the task
                    //  constructed by this builder so far.
                    // FLAG_UPDATE_CURRENT - If the described PendingIntent already exists, then
                    //  keep it but replace its extra data with what is in this new Intent.
                    .getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

            views.setPendingIntentTemplate( R.id.widget_detail_lv_forecasts, templatePendingIntent );

            // 0f. set remote views empty view

            views.setEmptyView( R.id.widget_detail_lv_forecasts, R.id.widget_detail_tv_empty );

            // 0g. update widget

            appWidgetManager.updateAppWidget( appWidgetId, views );

        } // end for through all widget IDs

    } // end onUpdate

    @Override
    // begin onReceive
    public void onReceive( Context context, Intent intent ) {

        // 0. super stuff
        // 1. if we receive the update action
        // 1a. get the widget manager
        // 1b. get the widget IDs
        // 1c. notify the list that data has changed

        // 0. super stuff

        super.onReceive( context, intent );

        // 1. if we receive the update action

        // begin if we have an intent with the update broadcast
        if ( intent != null
                && intent.getAction().equals( SunshineSyncAdapter.ACTION_DATA_UPDATED ) == true ) {

            // 1a. get the widget manager

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );

            // 1b. get the widget IDs

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds( new ComponentName( context,
                    DetailWidgetProvider.class ) );

            // 1c. notify the list that data has changed

            appWidgetManager.notifyAppWidgetViewDataChanged( appWidgetIds,
                    R.id.widget_detail_lv_forecasts );

            Log.e( LOG_TAG, "onReceive: Received " + intent.getAction() + " and updated the widget" );

        } // end if we have an intent with the update broadcast

    } // end onReceive

    /* Other Methods */

    /**
     * Sets the remote adapter used to fill in the list items, for ICE_CREAM_SANDWICH and above.
     *
     * @param context Android {@link Context}
     * @param views The {@link RemoteViews} that we will set the adapter for
     */
    @TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
    private void setRemoteAdapter( Context context, @NonNull final RemoteViews views ) {
        Log.e( LOG_TAG, "setRemoteAdapter: just in" );
        views.setRemoteAdapter( R.id.widget_detail_lv_forecasts,
                new Intent( context, DetailWidgetRemoteViewsService.class ) );
        Log.e( LOG_TAG, "setRemoteAdapter: set adapter" );
    }

    /**
     * Sets the remote adapter used to fill in the list items, for devices below ICE_CREAM_SANDWICH.
     *
     * @param context Android {@link Context}
     * @param views The {@link RemoteViews} that we will set the adapter for
     */
    @SuppressWarnings( "deprecation" )
    private void setRemoteAdapterV11( Context context, @NonNull final RemoteViews views ) {
        views.setRemoteAdapter( 0, R.id.widget_detail_lv_forecasts,
                new Intent( context, DetailWidgetRemoteViewsService.class ) );
    }

    /* INNER CLASSES */

} // end class DetailWidgetProvider