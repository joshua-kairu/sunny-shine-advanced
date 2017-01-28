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

package com.joslittho.sunshine.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joslittho.sunshine.R;
import com.joslittho.sunshine.data.ForecastAdapter;
import com.joslittho.sunshine.data.contract.WeatherContract;

/**
 * {@link android.support.v7.widget.RecyclerView.ViewHolder} to act as cache of the children views
 * for a forecast list item.
 * */
// begin class WeatherViewHolder
public class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */
        
    /* VARIABLES */

    /* Forecast Adapters */

    private ForecastAdapter mHostForecastAdapter; // ditto

    /* Image Views */

    public final ImageView mIconImageView; // ditto

    /* Text Views */

    public final TextView mDateTextView; // ditto
    public final TextView mDescriptionTextView; // ditto
    public final TextView mHighTempTextView; // ditto
    public final TextView mLowTempTextView; // ditto

    /* CONSTRUCTOR */

    // begin constructor
    public WeatherViewHolder( View view, ForecastAdapter forecastAdapter ) {

        // 0. super stuff
        // 1. initialize local views from the parameter view
        // 2. this view holder should listen to clicks from the parameter view
        // 3. initialize the host adapter

        // 0. super stuff

        super( view );

        // 1. initialize local views from the parameter view

        mIconImageView = ( ImageView ) view.findViewById( R.id.list_item_icon );
        mDateTextView = ( TextView ) view.findViewById( R.id.list_item_date_textview );
        mDescriptionTextView = ( TextView ) view.findViewById( R.id.list_item_forecast_textview );
        mHighTempTextView = ( TextView ) view.findViewById( R.id.list_item_high_textview );
        mLowTempTextView = ( TextView ) view.findViewById( R.id.list_item_low_textview );

        // 2. this view holder should listen to clicks from the parameter view

        view.setOnClickListener( this );

        // 3. initialize the host adapter

        mHostForecastAdapter = forecastAdapter;

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onClick
    public void onClick( View view ) {

        // 0. get the date from the adapter's current position using a cursor
        // 1 call the handler with the necessary parameters
        // 2. click the adapter's item choice manager too

        // 0. get the date from the adapter's current position using a cursor

        Cursor cursor = mHostForecastAdapter.getCursor();

        int adapterPosition = getAdapterPosition();

        cursor.moveToPosition( adapterPosition );

        int dateIndex = cursor.getColumnIndex( WeatherContract.WeatherEntry.COLUMN_DATE );

        long date = cursor.getLong( dateIndex );

        // 1 call the handler with the necessary parameters

        mHostForecastAdapter.mForecastAdapterOnClickHandler.onClick( date, this );

        // 2. click the adapter's item choice manager too

        mHostForecastAdapter.mICM.onClick( this );

    } // end onItemClick

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class WeatherViewHolder