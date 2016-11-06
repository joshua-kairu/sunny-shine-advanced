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

package com.jlt.sunshine.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * {@link RemoteViewsService} to populate the details widget
 * */
@TargetApi( Build.VERSION_CODES.HONEYCOMB )
// begin class DetailWidgetRemoteViewsService
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
        
    /* VARIABLES */
    
    /* CONSTRUCTOR */
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     *
     * @param intent {@link Intent} for any needed extras.
     */
    @Override
    // begin onGetViewFactory
    public RemoteViewsFactory onGetViewFactory( Intent intent ) {

        // 0. return a detail view widget factory

        // 0. return a detail view widget factory

        Log.e( LOG_TAG, "onGetViewFactory: just in"  );
        return new DetailWidgetRemoteViewsFactory( this.getApplicationContext() );

    } // end onGetViewFactory

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class DetailWidgetRemoteViewsService