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

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.joslittho.sunshine.R;
import com.joslittho.sunshine.data.Utility;
import com.joslittho.sunshine.data.contract.WeatherContract.WeatherEntry;

import java.util.concurrent.ExecutionException;

/**
 * A {@link android.widget.RemoteViewsService.RemoteViewsFactory} to generate views for the
 * detail widget list.
 * */
// begin class DetailWidgetRemoteViewsFactory
@TargetApi( Build.VERSION_CODES.HONEYCOMB )
public class DetailWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    /* CONSTANTS */

    /* Arrays */

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    /* Integers */

    // indices to locate values from the FORECAST_COLUMNS projection.
    // the indices match the arrangement in FORECAST_COLUMNS.
    // if the arrangement in FORECAST_COLUMNS changes, these indices MUST be changed too.

    public static final int COLUMN_WEATHER_DATE = 1;
    public static final int COLUMN_WEATHER_MAX_TEMP = 2;
    public static final int COLUMN_WEATHER_MIN_TEMP = 3;
    public static final int COLUMN_WEATHER_CONDITION_ID = 4;

    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = DetailWidgetRemoteViewsFactory.class.getSimpleName();

    /* VARIABLES */

    /* Contexts */

    private Context mContext; // ditto

    /* Cursors */

    private Cursor mCursor; // ditto

    /* Strings */

    private String mCurrentLocation; // ditto
    
    /* CONSTRUCTOR */

    /**
     * Two-argument constructor.
     *
     * @param context Android {@link Context}
     * */
    // begin constructor
    public DetailWidgetRemoteViewsFactory( Context context ) {

        // 0. initialize members

        // 0. initialize members

        mContext = context;

    } // end constructor
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    /**
     * Called when your factory is first constructed. The same factory may be shared across
     * multiple RemoteViewAdapters depending on the intent passed.
     *
     * It's a good place to set up connections/cursors to data sources. However, heavy lifting,
     * e.g. downloading/creating content should be deferred to
     * {@link RemoteViewsService.RemoteViewsFactory#onDataSetChanged()} or
     * {@link android.widget.RemoteViewsService.RemoteViewsFactory#getViewAt(int)}.
     *
     * Taking more than 20 seconds here results in an ANR.
     * */
    // begin onCreate
    public void onCreate() {

        // 0. set up cursor to get data for all the weather

        // 0. set up cursor to get data for all the weather

        mCurrentLocation = Utility.getPreferredLocation( mContext );

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherForLocationWithStartDateUri(
                mCurrentLocation, System.currentTimeMillis() );

        mCursor = mContext.getContentResolver().query( weatherForLocationUri, FORECAST_COLUMNS,
                null, null, sortOrder );

    } // end onCreate

    /**
     * See {@link Adapter#getView(int, View, ViewGroup)}.
     * Note: expensive tasks can be safely performed synchronously within this method, and a
     * loading view will be displayed in the interim. See {@link #getLoadingView()}.
     *
     * @param position The position of the item within the Factory's data set of the item whose
     *                 view we want.
     * @return A RemoteViews object corresponding to the data at the specified position.
     */
    @Override
    // begin getViewAt
    public RemoteViews getViewAt( int position ) {

        // 0. stop if
        // 0a. the position is invalid, or
        // 0b. the cursor doesn't exist, or
        // 0c. there is nothing at the given position
        // 1. get a remote views based on the XML layout
        // 2. put relevant details in
        // 2a. the forecast icon
        // 2b. the date
        // 2c. the forecast description
        // 2d. the high
        // 2e. the low
        // 3. put a content description if the device version allows it
        // 4. create a fill in intent to respond to taps if the device version allows it
        // 4a. when tapped, send a tapped broadcast
        // which will be received by the detail widget provider
        // last. return the remote views

        // 0. stop if
        // 0a. the position is invalid, or
        // 0b. the cursor doesn't exist, or
        // 0c. there is nothing at the given position

        if ( position == AdapterView.INVALID_POSITION || mCursor == null
                || mCursor.moveToPosition( position ) == false ) {
            return null;
        }

        // 1. get a remote views based on the XML layout

        RemoteViews views = new RemoteViews( mContext.getPackageName(),
                R.layout.widget_detail_list_item );

        // 2. put relevant details in

        // 2a. the forecast icon

        int weatherConditionId = mCursor.getInt( COLUMN_WEATHER_CONDITION_ID );

        int weatherIconId = Utility.getArtResourceForWeatherCondition( weatherConditionId );

        Uri weatherArtResourceUrl = Utility.getArtUriForWeatherCondition( mContext, weatherConditionId );

        try {
            Glide.with( mContext )
                    .load( weatherArtResourceUrl )
                    .asBitmap()
                    .error( Utility.getArtResourceForWeatherCondition( weatherIconId ) )
                    .into( Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL )
                    .get();
        } catch ( InterruptedException | ExecutionException e ) {
            Log.e( LOG_TAG, "Error retrieving large icon from " + weatherArtResourceUrl );
        }

        views.setImageViewResource( R.id.widget_detail_list_item_iv_icon, weatherIconId );

        // 2b. the date

        long dateInMillis = mCursor.getLong( COLUMN_WEATHER_DATE );

        String dateString = Utility.getFriendlyDateString( mContext, dateInMillis, false );

        views.setTextViewText( R.id.widget_detail_list_item_tv_date, dateString );

        // 2c. the forecast description

        String forecastDescription = Utility.getStringForWeatherCondition( mContext,
                weatherConditionId );

        views.setTextViewText( R.id.widget_detail_list_item_tv_forecast, forecastDescription );

        // 2d. the high

        String high = Utility.formatTemperature( mContext,
                mCursor.getFloat( COLUMN_WEATHER_MAX_TEMP ) );

        views.setTextViewText( R.id.widget_detail_list_item_tv_high, high );

        // 2e. the low

        String low = Utility.formatTemperature( mContext,
                mCursor.getFloat( COLUMN_WEATHER_MIN_TEMP ) );

        views.setTextViewText( R.id.widget_detail_list_item_tv_low, low );

        // 0c. put a content description if the device version allows it

        String contentDescription = mContext.getString( R.string.a11y_widget_detail_list_item,
                dateString, forecastDescription, high, low );

        // 3. put a content description if the device version allows it

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ) {
            setRemoteContentDescription( views, contentDescription );
        }

        // 4. create a fill in intent to respond to taps if the device version allows it

        // begin if we are at least on honeycomb
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

            Intent fillInIntent = new Intent();

            Uri dateUri = WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
                    mCurrentLocation, dateInMillis );

            fillInIntent.setData( dateUri );

            // 4a. when tapped, send a tapped broadcast
            // which will be received by the detail widget provider

            views.setOnClickFillInIntent( R.id.widget_detail_list_item, fillInIntent );

        } // end if we are at least on honeycomb

        // last. return the remote views

        return views;

    } // end getViewAt

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
     * RemoteViewsFactory to respond to data changes by updating any internal references.
     *
     * Note: expensive tasks can be safely performed synchronously within this method. In the
     * interim, the old data will be displayed within the widget.
     *
     * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
     */
    @Override
    // begin onDataSetChanged
    public void onDataSetChanged() {

        // 0. refresh the cursor
        // 0a. close the cursor if it is there
        // 0b. since our content provider is not exported, clear the calling identity so that calls
        // to the provider use our process and permissions
        // 0c. fetch data
        // 0d. since our content provider is not exported, restore the calling identity so that
        // calls to the provider use our process and permissions

        // 0. refresh the cursor

        // 0a. close the cursor if it is there

        if ( mCursor != null ) { mCursor.close(); }

        // 0b. since our content provider is not exported, clear the calling identity so that calls
        // to the provider use our process and permissions

        // clearCallingIdentity
        //  Reset the identity of the incoming IPC on the current thread. This can be useful if,
        //  while handling an incoming call, you will be calling on interfaces of other objects
        //  that may be local to your process and need to do permission checks on the calls
        //  coming into them (so they will check the permission of your own local process, and not
        //  whatever process originally called you).
        //  Returns an opaque token that can be used to restore the original calling identity by
        //  passing it to restoreCallingIdentity(long).
        final long identityToken = Binder.clearCallingIdentity();

        // 0c. fetch data

        mCurrentLocation = Utility.getPreferredLocation( mContext );

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherForLocationWithStartDateUri(
                mCurrentLocation, System.currentTimeMillis() );

        mCursor = mContext.getContentResolver().query( weatherForLocationUri, FORECAST_COLUMNS,
                null, null, sortOrder );

        // 0d. since our content provider is not exported, restore the calling identity so that
        // calls to the provider use our process and permissions

        Binder.restoreCallingIdentity( identityToken );

    } // end onDataSetChanged

    /**
     * @return The number of types of Views that will be returned by this factory.
     *         In our case, just one.
     */
    @Override
    public int getViewTypeCount() { return 1; }

    /**
     * See {@link Adapter#getItemId(int)}.
     *
     * @param position The position of the item within the data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    // begin getItemId
    public long getItemId( int position ) {

        // 0. return the weather id at the parameter position if we can get to it
        // 1. otherwise just return the parameter position

        // 0. return the weather id at the parameter position if we can get to it

        if ( mCursor.moveToPosition( position ) == true ) {
            return mCursor.getLong( COLUMN_WEATHER_CONDITION_ID );
        }

        // 1. otherwise just return the parameter position

        return position;

    } // end getItemId

    /**
     * Gets the number of items in the {@link Cursor} used by this adapter
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return ( mCursor == null ) ? 0 : mCursor.getCount();
    }

    /**
     * See {@link Adapter#hasStableIds()}.
     *
     * @return True if the same id always refers to the same object.
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that
     * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
     * view will be used.
     *
     * @return The RemoteViews representing the desired loading view.
     */
    @Override
    // for the loading view use the widget detail list item view
    public RemoteViews getLoadingView() {
        return new RemoteViews( mContext.getPackageName(), R.layout.widget_detail_list_item );
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is
     * unbound.
     */
    @Override
    // begin onDestroy
    public void onDestroy() {

        // 0. close the cursor

        // 0. close the cursor

        if ( mCursor != null ) { mCursor.close(); mCursor = null; }

    } // end onDestroy

    /* Other Methods */

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
    
    /* INNER CLASSES */

} // end class DetailWidgetRemoteViewsFactory