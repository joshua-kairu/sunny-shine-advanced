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

package com.joslittho.sunshine.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.joslittho.sunshine.MainActivity;
import com.joslittho.sunshine.R;
import com.joslittho.sunshine.data.Utility;
import com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry;

import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_DATE;
import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_SHORT_DESCRIPTION;
import static com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;

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

    /**
     * The logger.
     */
    private static final String LOG_TAG = TodayWidgetIntentService.class.getSimpleName();

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
        // 2. update the widget
        // 2a. get widget width
        // 2a0. make it zero for devices older than JELLY_BEAN
        // 2a1. make it the actual widget width for devices JELLY_BEAN and above
        // 2b. get a remote view based on widget width
        // 2c. create a pending intent to go to the main activity
        // 2d. use that pending intent for when the widget is clicked
        // 2last. update this widget with the remote view

        // 0. get the app widget manager

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( this );

        // 1. get the app widget IDs

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName( this, TodayWidgetProvider.class ) );

        // 2. update the widget

        // begin for through the widget IDs
        for ( int appWidgetId : appWidgetIds ) {

            // 2a. get widget width
            // 2a0. make it zero for devices older than JELLY_BEAN
            // 2a1. make it the actual widget width for devices JELLY_BEAN and above
            // 2b. get a remote view based on widget width
            // 2c. create a pending intent to go to the main activity
            // 2d. use that pending intent for when the widget is clicked
            // 2last. update this widget with the remote view

            // 2a. get widget width

            // 2a0. make it zero for devices older than JELLY_BEAN

            int widgetWidth = 0;

            // 2a1. make it the actual widget width for devices JELLY_BEAN and above

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
                widgetWidth = appWidgetManager.getAppWidgetOptions( appWidgetId ).getInt(
                        AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH );
            }

            // 2b. get a remote view based on widget width

            RemoteViews views = getRemoteViewsForWidth( getPackageName(), widgetWidth );

            // begin if there is a remote view for the given width
            if ( views != null ) {

                // 2c. create a pending intent to go to the main activity

                PendingIntent pendingIntent = PendingIntent.getActivity( this, 0,
                        new Intent( this, MainActivity.class ), 0 );

                // 2d. use that pending intent for when the widget is clicked

                // setOnClickPendingIntent - when the given view is clicked, fire the given PendingIntent
                views.setOnClickPendingIntent( R.id.widget_today, pendingIntent );

                // 2last. update this widget with the remote view

                appWidgetManager.updateAppWidget( appWidgetId, views );

            } // end if there is a remote view for the given width

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
        views.setContentDescription( R.id.widget_today_iv_icon, description );
    }

    /* Other Methods */

    /**
     * Helper method to get a {@link RemoteViews} based on widget width.
     *
     * @param packageName The name of the package containing the {@link RemoteViews} resource
     * @param widgetMinimumWidth The minimum width of the widget
     *
     * @return A {@link RemoteViews} based on the given width.
     * */
    // begin method getRemoteViewsForWidth
    private RemoteViews getRemoteViewsForWidth( String packageName, int widgetMinimumWidth ) {

        final float SIZE_SMALL =
                getResources().getDimension( R.dimen.widget_today_default_width ) /
                getResources().getDisplayMetrics().density;
                // divide by display density to get the actual dp value

        Log.e( LOG_TAG, "getRemoteViewsForWidth: SIZE_SMALL " + SIZE_SMALL );

        final float SIZE_MEDIUM =
                ( getResources().getDimension( R.dimen.widget_today_default_width ) * 2 ) /
                getResources().getDisplayMetrics().density;
                // divide by display density to get the actual dp value

        Log.e( LOG_TAG, "getRemoteViewsForWidth: SIZE_MEDIUM " + SIZE_MEDIUM );

        // SIZE_LARGE isn't needed since anything larger than SIZE_MEDIUM is large

        RemoteViews views;

        String locationSetting = Utility.getPreferredLocation( this );
        String sortOrder = COLUMN_DATE + " ASC";
        Uri queryUri = WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                locationSetting, System.currentTimeMillis() );
        Cursor todayCursor; String[] projection;

        String forecastDescription, contentDescription;

        // 0. for small widgets < 110dp
        // 0a. from db get
        // 0b. stop if there's no data
        // 0c. stop if we can't get to a row
        // 0c0. the weather id, and thus weather icon
        // 0c1. the high
        // 0c2. the description
        // 0d. put them in the remote view
        // 0e. put a content description, if device version allows
        // 1. for medium widgets >= 110dp < 220dp
        // 1a. from db get
        // 1b. stop if there's no data
        // 1c. stop if we can't get to a row
        // 1c0. the weather id, and thus weather icon
        // 1c1. the high
        // 1c2. the low
        // 1c3. the short description
        // 1d. put them in the remote view
        // 1e. put a content description, if device version allows
        // 2. for large widgets >= 220dp
        // 2a. from db get
        // 2b. stop if there's no data
        // 2c. stop if we can't get to a row
        // 2c0. the weather id, and thus weather icon
        // 2c1. the long forecast description
        // 2c2. the high
        // 2c3. the low
        // 2d. put them in the remote view
        // 0e. put a content description, if device version allows
        // last-a. close the cursor
        // last-b. return the view

        // 0. for small widgets < 110dp

        // begin if we are small widget
        if ( widgetMinimumWidth < SIZE_SMALL ) {

            // 0a. from db get

            projection = new String[] { COLUMN_WEATHER_ID, COLUMN_MAX_TEMP };

            int indexWeatherId = 0, indexMaxTemp = 1;

            todayCursor = getContentResolver().query( queryUri, projection, null, null, sortOrder );

            // 0b. stop if there's no data

            if ( todayCursor == null ) { return null; }

            // 0c. stop if we can't get to a row

            if ( todayCursor.moveToFirst() == false ) { return null; }

            // 0c0. the weather id, and thus weather icon

            int weatherIconId = Utility.getArtResourceForWeatherCondition(
                    todayCursor.getInt( indexWeatherId ) );

            // 0c1. the high

            String high = Utility.formatTemperature( this, todayCursor.getFloat( indexMaxTemp ) );

            // 0c2. the description

            forecastDescription = Utility.getStringForWeatherCondition( this,
                    todayCursor.getInt( indexWeatherId ) );

            // 0d. put them in the remote view

            views = new RemoteViews( packageName, R.layout.widget_today_small );

            views.setImageViewResource( R.id.widget_today_iv_icon, weatherIconId );

            views.setTextViewText( R.id.widget_today_tv_high_temperature, high );

            // 0e. put a content description, if device version allows

            contentDescription = getString( R.string.a11y_widget_today_small, forecastDescription, high );

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
                setRemoteContentDescription( views, contentDescription );
            }

        } // end if we are small widget

        // 1. for medium widgets >= 110 dp < 220dp

        // begin else if we are medium widget
        else if ( widgetMinimumWidth >= SIZE_SMALL && widgetMinimumWidth < SIZE_MEDIUM ) {

            // 1a. from db get

            projection = new String[] { COLUMN_WEATHER_ID, COLUMN_MAX_TEMP, COLUMN_MIN_TEMP,
                    COLUMN_SHORT_DESCRIPTION };

            int indexWeatherId = 0, indexMaxTemp = 1, indexMinTemp = 2, indexShortDescription = 3;

            todayCursor = getContentResolver().query( queryUri, projection, null, null, sortOrder );

            // 1b. stop if there's no data

            if ( todayCursor == null ) { return null; }

            // 1c. stop if we can't get to a row

            if ( todayCursor.moveToFirst() == false ) { return null; }

            // 1c0. the weather id, and thus weather icon

            int weatherIconId = Utility.getArtResourceForWeatherCondition(
                    todayCursor.getInt( indexWeatherId ) );

            // 1c1. the high

            String high = Utility.formatTemperature( this, todayCursor.getFloat( indexMaxTemp ) );

            // 1c2. the low

            String low = Utility.formatTemperature( this, todayCursor.getFloat( indexMinTemp ) );

            // 1c3. the short description

            forecastDescription = todayCursor.getString( indexShortDescription );

            // 1d. put them in the remote view

            views = new RemoteViews( packageName, R.layout.widget_today_medium );

            views.setImageViewResource( R.id.widget_today_iv_icon, weatherIconId );

            views.setTextViewText( R.id.widget_today_tv_high_temperature, high );

            views.setTextViewText( R.id.widget_today_tv_low_temperature, low );

            // 1e. put a content description, if device version allows

            contentDescription = getString( R.string.a11y_widget_today_medium, forecastDescription,
                    high, low );

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
                setRemoteContentDescription( views, contentDescription );
            }

        } // end else if we are medium widget

        // 2. for large widgets >= 220dp

        // begin else we are large
        else {

            // 2a. from db get

            projection = new String[] { COLUMN_WEATHER_ID, COLUMN_MAX_TEMP, COLUMN_MIN_TEMP };

            int indexWeatherId = 0, indexMaxTemp = 1, indexMinTemp = 2;

            todayCursor = getContentResolver().query( queryUri, projection, null, null, sortOrder );

            // 2b. stop if there's no data

            if ( todayCursor == null ) { return null; }

            // 2c. stop if we can't get to a row

            if ( todayCursor.moveToFirst() == false ) { return null; }

            // 2c0. the weather id, and thus weather icon

            int weatherIconId = Utility.getArtResourceForWeatherCondition(
                    todayCursor.getInt( indexWeatherId ) );

            // 2c1. the long forecast description

            forecastDescription = Utility.getStringForWeatherCondition( this,
                    todayCursor.getInt( indexWeatherId ) );

            // 2c2. the high

            String high = Utility.formatTemperature( this, todayCursor.getFloat( indexMaxTemp ) );

            // 2c3. the low

            String low = Utility.formatTemperature( this, todayCursor.getFloat( indexMinTemp ) );

            // 2d. put them in the remote view

            views = new RemoteViews( packageName, R.layout.widget_today_large );

            views.setImageViewResource( R.id.widget_today_iv_icon, weatherIconId );

            views.setTextViewText( R.id.widget_today_tv_forecast_description, forecastDescription );

            views.setTextViewText( R.id.widget_today_tv_high_temperature, high );

            views.setTextViewText( R.id.widget_today_tv_low_temperature, low );

            // 0e. put a content description, if device version allows

            contentDescription = getString( R.string.a11y_widget_today_large, forecastDescription,
                    high, low );

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
                setRemoteContentDescription( views, contentDescription );
            }

        } // end else we are large

        // last-a. close the cursor

        todayCursor.close();

        // last-b. return the view

        return views;

    } // end method getRemoteViewsForWidth
    
    /* INNER CLASSES */

} // end class TodayWidgetIntentService