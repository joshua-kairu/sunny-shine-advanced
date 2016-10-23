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

package com.jlt.sunshine.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jlt.sunshine.R;
import com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;
import com.jlt.sunshine.ui.WeatherViewHolder;

import static com.jlt.sunshine.ForecastFragment.COLUMN_WEATHER_CONDITION_ID;
import static com.jlt.sunshine.ForecastFragment.COLUMN_WEATHER_DATE;
import static com.jlt.sunshine.ForecastFragment.COLUMN_WEATHER_MAX_TEMP;
import static com.jlt.sunshine.ForecastFragment.COLUMN_WEATHER_MIN_TEMP;
import static com.jlt.sunshine.ForecastFragment.COLUMN_WEATHER_SHORT_DESCRIPTION;
import static com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link RecyclerView}
 * */
// begin class ForecastAdapter
public class ForecastAdapter extends RecyclerView.Adapter< WeatherViewHolder > {

    /* CONSTANTS */

    /* Arrays */

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            // fully qualify the table id since the content provider
            // joins the location and weather tables, both of which have an _id column.
            // this qualification allows us to
            // get the weather using the location set by the user.
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESCRIPTION,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LATITUDE,
            LocationEntry.COLUMN_COORD_LONGITUDE
    };

    /* Integers */

    private static final int VIEW_TYPE_TODAY = 0; // constant to tell when we are to inflate the today view
    private static final int VIEW_TYPE_FUTURE_DAY = 1; // constant to tell when we are to inflate the future day view

    /* Strings */
        
    /* VARIABLES */

    /* Contexts */

    private Context mContext; // ditto

    /* Cursors */

    private Cursor mCursor; // ditto

    /* Forecast Adapter On Click Handlers */

    public final ForecastAdapterOnClickHandler mForecastAdapterOnClickHandler; // ditto

    /* Item Choice Managers */

    public final ItemChoiceManager mICM; // ditto

    /* Primitives */

    private boolean mUseTodayLayout = true; // tells if to use the enlarged today layout
                                           // should be true in phones but false in tabs

    /* Views */

    private final View mEmptyView; // ditto

    /* CONSTRUCTOR */

    /**
     * Default constructor.
     *
     * @param context Android {@link Context}
     * @param handler A {@link ForecastAdapterOnClickHandler} to handle item clicks
     * @param emptyView The empty view
     * @param choiceMode The choice mode
     * */
    // begin constructor
    public ForecastAdapter( Context context, ForecastAdapterOnClickHandler handler, View emptyView,
                            int choiceMode ) {

        // 0. initialize members

        // 0. initialize members

        mContext = context;

        mForecastAdapterOnClickHandler = handler;

        mEmptyView = emptyView;

        mICM = new ItemChoiceManager( this );
        mICM.setChoiceMode( choiceMode );

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */

    // setter for mUseTodayLayout
    public void setUseTodayLayout( boolean useTodayLayout ) { mUseTodayLayout = useTodayLayout; }

    // getter for the cursor
    public Cursor getCursor() { return mCursor; }

    /* Overrides */

    @Override
    // begin onCreateViewHolder
    public WeatherViewHolder onCreateViewHolder( ViewGroup parentViewGroup, int viewType ) {

        // 0. if the parent view group is recycler
        // 0a. choose the layout type from the view type
        // 0b. inflate the correct layout
        // 0b1. from the parent context
        // 0b2. make the inflated view focusable
        // 0b3. return the inflated layout
        // 1. else exception!

        // 0. if the parent view group is recycler

        // begin if the parent view group is a recycler
        if ( parentViewGroup instanceof RecyclerView ) {

            // 0a. choose the layout type from the view type

            int layoutId = -1;

            if ( viewType == VIEW_TYPE_TODAY ) { layoutId = R.layout.list_item_forecast_today; }
            else if ( viewType == VIEW_TYPE_FUTURE_DAY ) { layoutId =  R.layout.list_item_forecast; }

            // 0b. inflate the correct layout

            // 0b1. from the parent context

            View view = LayoutInflater.from( parentViewGroup.getContext() )
                    .inflate( layoutId, parentViewGroup, false );

            // 0b2. make the inflated view focusable

            view.setFocusable( true );


            // 0b3. return the inflated layout

            return new WeatherViewHolder( view, this );

        } // end if the parent view group is a recycler

        // 1. else exception!

        else {
            throw new RuntimeException( "Not bound to RecyclerView." );
        }

    } // end onCreateViewHolder

    @Override
    // begin onBindViewHolder
    public void onBindViewHolder( WeatherViewHolder weatherViewHolder, int position ) {

        // 0. if the cursor exists and has something
        // 0a. first move it to the given position
        // 1. read weather icon ID from cursor and display appropriate icon using view holder data
        // 1a. show art for both today's and the future's weather
        // 2. read date from cursor and display it appropriately using view holder data
        // 3. read forecast from cursor and display it using view holder data
        // 3a. also make the forecast the content description for the image shown in 0a
        // 4. read high temperature from cursor and display it in appropriate units using view holder data
        // 4a. provide content description for the high temperature
        // 5. read low temperature from cursor and display it in appropriate units using view holder data
        // 5a. provide content description for the low temperature
        // 6. bind the item choice manager

        // 0. if the cursor exists and has something

        // begin if there cursor is and is not empty
        if ( mCursor != null && mCursor.moveToFirst() ) {

            // 0a. first move it to the given position

            mCursor.moveToPosition( position );

            // 1. read weather icon ID from cursor and display appropriate icon using view holder data
            // 1a. show art for both today's and the future's weather

            int weatherIconId = mCursor.getInt( COLUMN_WEATHER_CONDITION_ID );

            Glide.with( mContext )
                    .load( Utility.getArtUriForWeatherCondition( mContext, weatherIconId ) )
                    .error( Utility.getArtResourceForWeatherCondition( weatherIconId ) )
                    .crossFade()
                    .into( weatherViewHolder.mIconImageView );

            // 2. read date from cursor and display it appropriately using view holder data

            long date = mCursor.getLong( COLUMN_WEATHER_DATE );

            String dateString = Utility.getFriendlyDateString( mContext, date );

            weatherViewHolder.mDateTextView.setText( dateString );

            // 3. read forecast from cursor and display it using view holder data

            String forecastString = Utility.getStringForWeatherCondition( mContext, weatherIconId );

            weatherViewHolder.mDescriptionTextView.setText( forecastString );

            // 3a. also make the forecast the content description for the image shown in 0a

            weatherViewHolder.mIconImageView.setContentDescription( forecastString );

            // 4. read high temperature from cursor and display it in appropriate units using view holder data

            float high = mCursor.getFloat( COLUMN_WEATHER_MAX_TEMP );

            String highString = Utility.formatTemperature( mContext, high, Utility.isMetric( mContext ) );

            weatherViewHolder.mHighTempTextView.setText( highString );

            // 4a. provide content description for the high temperature

            weatherViewHolder.mHighTempTextView.setContentDescription(
                    mContext.getString( R.string.a11y_high_temperature_format, highString )
            );

            // 5. read low temperature from cursor and display it in appropriate units using view holder data

            float low = mCursor.getFloat( COLUMN_WEATHER_MIN_TEMP );

            String lowString = Utility.formatTemperature( mContext, low, Utility.isMetric( mContext ) );

            weatherViewHolder.mLowTempTextView.setText( lowString );

            // 5a. provide content description for the low temperature

            weatherViewHolder.mLowTempTextView.setContentDescription(
                    mContext.getString( R.string.a11y_low_temperature_format, lowString )
            );

            // 6. bind the item choice manager

            mICM.onBindViewHolder( weatherViewHolder, position );

        } // end if there cursor is and is not empty

    } // end onBindViewHolder

    @Override
    /** return the number of items in the cursor, or zero if cursor is not there.  */
    // getItemCount
    public int getItemCount() { return ( mCursor == null ) ? 0 : mCursor.getCount(); }

    @Override
    // begin getItemViewType
    public int getItemViewType( int position ) {

        // we should use a different layout for today only if we are in a phone
        // mUseTodayLayout is true when we are in a phone

        return ( position == 0 && mUseTodayLayout == true ) ?
                VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;

    } // end getItemViewType

    /* Other Methods */

    /** Prepare the weather high/low temperatures for presentation. */
    // begin method formatHighLows
    private String formatHighLows( float high, float low ) {

        // 0. are the units metric?
        // 1. get the correct high/low string
        // 2. and return it

        // 0. are the units metric?

        boolean isMetric = Utility.isMetric( mContext );

        // 1. get the correct high/low string

        String highLowString =
                Utility.formatTemperature( mContext, high, isMetric ) + "/"
                        + Utility.formatTemperature( mContext, low, isMetric );

        // 2. and return it

        return highLowString;

    } // end method formatHighLows

    /** Convert the cursor's current row to a UX-worthy format. */
    // begin method convertCursorRowToUXFormat
    private String convertCursorRowToUXFormat( Cursor cursor ) {

        // the needed format is "date - short description - high/low"

        // 0. get needed high/low string
        // 1. return something presentable

        // 0. get needed high/low string

        String highLowString = formatHighLows(
                cursor.getFloat( COLUMN_WEATHER_MAX_TEMP ),
                cursor.getFloat( COLUMN_WEATHER_MIN_TEMP ) );

        // 1. return something presentable

        return
                Utility.formatDate( cursor.getLong( COLUMN_WEATHER_DATE ) ) + " - " +
                        cursor.getString( COLUMN_WEATHER_SHORT_DESCRIPTION ) + " - " +
                        highLowString;

    } // end method convertCursorRowToUXFormat

    /**
     *
     * Replaces the member {@link Cursor} with the one passed in and refreshes the data.
     *
     * @param newCursor The {@link Cursor} to replace the member cursor with.
     * */
    // begin method swapCursor
    public void swapCursor( Cursor newCursor ) {

        // 0. swap cursors
        // 1. tell of data change
        // 2. set empty view visibility based on item count

        // 0. swap cursors

        mCursor = newCursor;

        // 1. tell of data change

        notifyDataSetChanged();

        // 2. set empty view visibility based on item count

        mEmptyView.setVisibility( getItemCount() == 0 ? View.VISIBLE : View.GONE );

    } // end method swapCursor

    /** Restores instance state for the member {@link ItemChoiceManager}. */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    /** Saves instance state for the member {@link ItemChoiceManager}. */
    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    /** Gets the selected item's position from the member {@link ItemChoiceManager}. */
    public int getSelectedItemPosition() { return mICM.getSelectedItemPosition(); }

    /** Invokes the {@link android.view.View.OnClickListener} for the given {@link WeatherViewHolder}. */
    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof WeatherViewHolder ) {
            WeatherViewHolder vfh = (WeatherViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    /* INNER CLASSES */

} // end class ForecastAdapter