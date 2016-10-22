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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlt.sunshine.R;

/**
 * {@link android.support.v7.widget.RecyclerView.ViewHolder} to act as cache of the children views
 * for a forecast list item.
 * */
// begin class WeatherViewHolder
public class WeatherViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */
        
    /* VARIABLES */

    /* Image Views */

    public final ImageView mIconImageView; // ditto

    /* Text Views */

    public final TextView mDateTextView; // ditto
    public final TextView mDescriptionTextView; // ditto
    public final TextView mHighTempTextView; // ditto
    public final TextView mLowTempTextView; // ditto

    /* CONSTRUCTOR */

    // begin constructor
    public WeatherViewHolder( View view ) {

        // 0. super stuff
        // 1. initialize local views from the parameter view

        // 0. super stuff

        super( view );

        // 1. initialize local views from the parameter view

        mIconImageView = ( ImageView ) view.findViewById( R.id.list_item_icon );
        mDateTextView = ( TextView ) view.findViewById( R.id.list_item_date_textview );
        mDescriptionTextView = ( TextView ) view.findViewById( R.id.list_item_forecast_textview );
        mHighTempTextView = ( TextView ) view.findViewById( R.id.list_item_high_textview );
        mLowTempTextView = ( TextView ) view.findViewById( R.id.list_item_low_textview );

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onItemClick
    public void onItemClick( AdapterView< ? > parent, View view, int position, long id ) {

//                        // 5a. notify the parent activity
//
//                        // 0. get the cursor at the given position
//                        // 1. if there is a cursor there
//                        // 1a. get the location setting
//                        // 1b. pass these to the parent activity
//
//                        // 0. get the cursor at the given position
//
//                        Cursor cursor = ( Cursor ) adapterView.getItemAtPosition( position );
//
//                        // 1. if there is a cursor there
//
//                        // begin if there exists a cursor
//                        if ( cursor != null ) {
//
//                            // 1a. get the location setting
//
//                            String locationSetting = Utility.getPreferredLocation( getActivity() );
//
//                            // 1b. pass these to the parent activity
//
//                            forecastCallbackListener.onForecastItemSelected(
//                                    WeatherEntry.buildWeatherForLocationWithSpecificDateUri(
//                                            locationSetting, cursor.getLong( COLUMN_WEATHER_DATE ) )
//                            );
//
//                        } // end if there exists a cursor
//
//                        // 5b. update the selected position member variable
//
//                        mCurrentScrollPosition = position;

    } // end onItemClick

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class WeatherViewHolder