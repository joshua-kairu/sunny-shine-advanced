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
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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
import static com.jlt.sunshine.ForecastFragment.LOG_TAG;
import static com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}
 * */
// begin class ForecastAdapter
public class ForecastAdapter extends CursorAdapter {

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
    private static final int VIEW_TYPE_COUNT = 2; // ditto

    /* Strings */
        
    /* VARIABLES */

    /* Primitives */

    private boolean mUseTodayLayout = true; // tells if to use the enlarged today layout
                                           // should be true in phones but false in tabs

    /* CONSTRUCTOR */

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public ForecastAdapter( Context context, Cursor c, int flags ) {
        super( context, c, flags );
    }

    /* METHODS */
    
    /* Getters and Setters */

    // setter for mUseTodayLayout
    public void setUseTodayLayout( boolean useTodayLayout ) {
        this.mUseTodayLayout = useTodayLayout;
    }

    /* Overrides */

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    // begin newView
    public View newView( Context context, Cursor cursor, ViewGroup parent ) {

        // 0. choose the layout type
        // 1. determine the layout id from the view type
        // 2. inflate the correct layout
        // 3. put the inflated layout inside a view holder
        // 4. return the inflated layout

        // 0. choose the layout type

        int viewType = getItemViewType( cursor.getPosition() );

        // 1. determine the layout id from the view type

        int layoutId = -1;

        if ( viewType == VIEW_TYPE_TODAY  ) { layoutId = R.layout.list_item_forecast_today; }
        else if ( viewType == VIEW_TYPE_FUTURE_DAY ) { layoutId =  R.layout.list_item_forecast; }

        // 2. inflate the correct layout

        View inflatedView = LayoutInflater.from( context ).inflate( layoutId, parent, false );

        // 3. put the inflated layout inside a view holder

        inflatedView.setTag( R.id.view_holder_tag, new WeatherViewHolder( inflatedView ) );

        // 4. return the inflated layout

        return inflatedView;

    } // end newView

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    // begin bindView
    public void bindView( View view, Context context, Cursor cursor ) {

        // 0. get the view holder from the tag
        // 1. read weather icon ID from cursor and display appropriate icon using view holder data
        // 1a. show art for both today's and the future's weather
        // 2. read date from cursor and display it appropriately using view holder data
        // 3. read forecast from cursor and display it using view holder data
        // 3a. also make the forecast the content description for the image shown in 1a
        // 4. read high temperature from cursor and display it in appropriate units using view holder data
        // 4a. provide content description for the high temperature
        // 5. read low temperature from cursor and display it in appropriate units using view holder data
        // 5a. provide content description for the low temperature

        // 0. get the view holder from the tag

        WeatherViewHolder weatherViewHolder =
                ( WeatherViewHolder ) view.getTag( R.id.view_holder_tag );

        // 1. read weather icon ID from cursor and display appropriate icon using view holder data

        // 1a. show art for both today's and the future's weather

        int weatherIconId = cursor.getInt( COLUMN_WEATHER_CONDITION_ID );
Uri uri = Utility.getArtUriForWeatherCondition( context, weatherIconId );

        Log.e( LOG_TAG, "bindView: uri " + uri.toString() );
        Glide.with( context )
                .load( Utility.getArtUriForWeatherCondition( context, weatherIconId ) )
                .error( Utility.getArtResourceForWeatherCondition( weatherIconId ) )
                .into( weatherViewHolder.iconImageView );

        // 2. read date from cursor and display it appropriately using view holder data

        long date = cursor.getLong( COLUMN_WEATHER_DATE );

        String dateString = Utility.getFriendlyDateString( context, date );

        weatherViewHolder.dateTextView.setText( dateString );

        // 3. read forecast from cursor and display it using view holder data

        String forecastString = Utility.getStringForWeatherCondition( context, weatherIconId );

        weatherViewHolder.descriptionTextView.setText( forecastString );

        // 3a. also make the forecast the content description for the image shown in 1a

        weatherViewHolder.descriptionTextView.setContentDescription( forecastString );

        // 4. read high temperature from cursor and display it in appropriate units using view holder data

        float high = cursor.getFloat( COLUMN_WEATHER_MAX_TEMP );

        String highString = Utility.formatTemperature( context, high, Utility.isMetric( mContext ) );

        weatherViewHolder.highTempTextView.setText( highString );

        // 4a. provide content description for the high temperature

        weatherViewHolder.highTempTextView.setContentDescription(
                context.getString( R.string.a11y_high_temperature_format, highString )
        );

        // 5. read low temperature from cursor and display it in appropriate units using view holder data

        float low = cursor.getFloat( COLUMN_WEATHER_MIN_TEMP );

        String lowString = Utility.formatTemperature( mContext, low, Utility.isMetric( mContext ) );

        weatherViewHolder.lowTempTextView.setText( lowString );

        // 5a. provide content description for the high temperature

        weatherViewHolder.lowTempTextView.setContentDescription(
                context.getString( R.string.a11y_low_temperature_format, lowString )
        );

    } // end bindView

    @Override
    // begin getItemViewType
    public int getItemViewType( int position ) {

        // we should use a different layout for today only if we are in a phone
        // mUseTodayLayout is true when we are in a phone

        return ( position == 0 && mUseTodayLayout == true ) ?
                VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;

    } // end getItemViewType

    @Override
    // getViewTypeCount
    public int getViewTypeCount() { return VIEW_TYPE_COUNT; }

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

    /* INNER CLASSES */

} // end class ForecastAdapter