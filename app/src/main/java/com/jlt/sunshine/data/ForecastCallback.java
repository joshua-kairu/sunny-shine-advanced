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

import android.net.Uri;

import com.jlt.sunshine.ui.WeatherViewHolder;

/**
 * A callback interface that all activities containing the {@link com.jlt.sunshine.ForecastFragment}
 * should have. This mechanism allows activities to be informed of item selections
 * */
// begin interface ForecastCallback
public interface ForecastCallback {

    /* CONSTANTS */
       
    /* VARIABLES */
       
    /* METHODS */

    /**
     * {@link com.jlt.sunshine.DetailFragment} callback for when an item has been selected.
     *
     * @param dateUri {@link Uri} for the date.
     * @param weatherViewHolder {@link WeatherViewHolder} to assist in shared activity transitioning.
     * */
    void onForecastItemSelected( Uri dateUri, WeatherViewHolder weatherViewHolder );
        
    /* Getters and Setters */
        
    /* Overrides */
        
    /* Other Methods */

} // end interface ForecastCallback
