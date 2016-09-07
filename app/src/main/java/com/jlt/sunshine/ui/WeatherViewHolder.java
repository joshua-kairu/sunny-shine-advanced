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

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlt.sunshine.R;

/**
 * Cache of the children views for a forecast list item.
 * */
// begin class WeatherViewHolder
public class WeatherViewHolder {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */
        
    /* VARIABLES */

    /* Image Views */

    public final ImageView iconImageView; // ditto

    /* Text Views */

    public final TextView dateTextView; // ditto
    public final TextView descriptionTextView; // ditto
    public final TextView highTempTextView; // ditto
    public final TextView lowTempTextView; // ditto

    /* CONSTRUCTOR */

    // begin constructor
    public WeatherViewHolder( View view ) {

        // 0. initialize local views from the parameter view

        // 0. initialize local views from the parameter view

        iconImageView = ( ImageView ) view.findViewById( R.id.list_item_icon );
        dateTextView = ( TextView ) view.findViewById( R.id.list_item_date_textview );
        descriptionTextView = ( TextView ) view.findViewById( R.id.list_item_forecast_textview );
        highTempTextView = ( TextView ) view.findViewById( R.id.list_item_high_textview );
        lowTempTextView = ( TextView ) view.findViewById( R.id.list_item_low_textview );

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */
    
    /* Other Methods */
    
    /* INNER CLASSES */

} // end class WeatherViewHolder