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

package com.joslitto.sunshine.data;

import com.joslitto.sunshine.ui.WeatherViewHolder;

/**
 * Handler for click events in the {@link android.support.v7.widget.RecyclerView}
 * populated by the {@link ForecastAdapter}.
 * */
// begin interface ForecastAdapterOnClickHandler
public interface ForecastAdapterOnClickHandler {

    /* CONSTANTS */
       
    /* VARIABLES */
       
    /* METHODS */
        
    /* Getters and Setters */
        
    /* Overrides */
        
    /* Other Methods */

    /**
     * Click handler for the {@link ForecastAdapter}.
     *
     * @param date The date.
     * @param viewHolder The {@link WeatherViewHolder}.
     * */
    // onClick
    void onClick( Long date, WeatherViewHolder viewHolder );

} // end interface ForecastAdapterOnClickHandler
