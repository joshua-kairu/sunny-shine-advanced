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

package com.jlt.sunshine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlt.sunshine.data.ForecastAdapter;
import com.jlt.sunshine.data.ForecastAdapterOnClickHandler;
import com.jlt.sunshine.data.ForecastCallback;
import com.jlt.sunshine.data.Utility;
import com.jlt.sunshine.data.contract.WeatherContract.LocationEntry;
import com.jlt.sunshine.data.contract.WeatherContract.WeatherEntry;
import com.jlt.sunshine.sync.SunshineSyncAdapter;
import com.jlt.sunshine.ui.WeatherViewHolder;

/**
 * Fragment to show the weather forecast.
 *
 * It gets informed of changes in the preferences so that
 * it can tell the user reasons for empty lists.
 * */
// begin fragment ForecastFragment
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks< Cursor >,
        SharedPreferences.OnSharedPreferenceChangeListener, ForecastAdapterOnClickHandler {

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

    private static final int FORECAST_LOADER_ID = 0; // ditto

    // indices to locate values from the FORECAST_COLUMNS projection.
    // the indices match the arrangement in FORECAST_COLUMNS.
    // if the arrangement in FORECAST_COLUMNS changes, these indices MUST be changed too.

    public static final int COLUMN_WEATHER_ID = 0;
    public static final int COLUMN_WEATHER_DATE = 1;
    public static final int COLUMN_WEATHER_SHORT_DESCRIPTION = 2;
    public static final int COLUMN_WEATHER_MAX_TEMP = 3;
    public static final int COLUMN_WEATHER_MIN_TEMP = 4;
    public static final int COLUMN_LOCATION_SETTING = 5;
    public static final int COLUMN_WEATHER_CONDITION_ID = 6;
    public static final int COLUMN_COORD_LATITUDE = 7;
    public static final int COLUMN_COORD_LONGITUDE = 8;

    /* Strings */

    public static final String LOG_TAG = ForecastFragment.class.getSimpleName(); // the logger

    private static final String BUNDLE_SCROLL_POSITION = "BUNDLE_SCROLL_POSITION";
        // key to store the current scroll position

    /* VARIABLES */

    /* Forecast Callbacks */

    private ForecastCallback forecastCallbackListener; // listener for any changes
                                                       // done to the forecast fragment

    /* Forecast Adapters */

    private ForecastAdapter mForecastAdapter; // array adapter to fill the weather list view

    /* Primitives */

    private int mCurrentScrollPosition = RecyclerView.NO_POSITION; // current scroll position of
                                                                   // the recycler. starting off
                                                                   // with an invalid no position

    private boolean mUseTodayLayout = true; // ditto

    private boolean mAutoSelectView;
    private boolean mHoldForTransitions;
    private int mChoiceMode;

    /* Recycler Views */

    private RecyclerView mForecastRecyclerView; // ditto

    /*
     * CONSTRUCTOR
     */

    // empty constructor for fragment subclasses
    public ForecastFragment() {
    }

    /* METHODS */

    /* Getters and Setters */

    /**
     * Should we use the special today layout in the forecast list?
     *
     * @param useTodayLayout {@link Boolean} based on whether we should use
     *                       the special today layout in the forecast list.
     * */
    // begin method setUseTodayLayout
    public void setUseTodayLayout( boolean useTodayLayout ) {

        // 0. set the member use today layout variable
        // 1. use the member use today layout variable to communicate to the forecast adapter

        // 0. set the member use today layout variable

        mUseTodayLayout = useTodayLayout;

        // 1. use the member use today layout variable to communicate to the forecast adapter

        // the forecast adapter could be null if
        // this method is called before the fragment's onCreateView,
        // which is where the adapter is initialized
        if ( mForecastAdapter != null ) { mForecastAdapter.setUseTodayLayout( mUseTodayLayout ); }

    } // end method setUseTodayLayout

    /*
     * Overrides
     */

    @Override
    // begin onCreate
    public void onCreate( @Nullable Bundle savedInstanceState ) {

        // 0. super things
        // 1. register the options menu
        // 2. initialize the forecast callback listener

        // 0. super things

        super.onCreate( savedInstanceState );

        // 1. register the options menu

        setHasOptionsMenu( true );

        // 2. initialize the forecast callback listener

        try { forecastCallbackListener = ( ForecastCallback ) getActivity(); }

        catch ( ClassCastException e ) {
            Log.e( LOG_TAG, "The parent activity must implement ForecastCallback." );
        }

    } // end onCreate

    @Override
    // begin onCreateOptionsMenu
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {

        // 0. super things
        // 1. inflate the forecast fragment menu

        // 0. super things

        super.onCreateOptionsMenu( menu, inflater );

        // 1. inflate the forecast fragment menu

        inflater.inflate( R.menu.menu_forecast_fragment, menu );

    } // end onCreateOptionsMenu

    @Override
    // inflate so as to get the XML attributes
    public void onInflate( Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        TypedArray a = activity.obtainStyledAttributes(attrs, R.styleable.ForecastFragment, 0, 0);
        mChoiceMode = a.getInt(R.styleable.ForecastFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        mAutoSelectView = a.getBoolean(R.styleable.ForecastFragment_autoSelectView, false);

        // read the shared element transition status
        mHoldForTransitions = a.getBoolean(
                R.styleable.ForecastFragment_sharedElementTransitions, false );

        a.recycle();
    }

    @Override
    // begin onCreateView
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

        // 0. inflate the main fragment layout
        // 1. initialize the adapter
        // 1a. tell it if to use the special today layout
        // 2. the recycler
        // 2a. find reference to it
        // 2b. use a linear layout manager
        // 2c. has fixed size
        // 2d. add scroll listener
        // 2d0. for parallax scrolling
        // 2d0a. works if there is the parallax bar (found in landscape) and we're at least honeycomb
        // 2d0b. change translationY of parallax bar by .5 * -deltaY, with minimum change being -parallaxBarHeight
        // 2d1. to raise app bar height during scroll
        // 2d1a. works if there is the app bar (found in portrait) and we're on Lollipop
        // 2d1a0. first make the app bar elevation zero
        // 2d1a1. if we are not scrolling, no elevation
        // 2a1a2. if we are scrolling, elevate
        // 3. set adapter to the recycler
        // 4. if there's instance state,
        // 4a. mine it for the scroll position
        // last. return the inflated view

        // 0. inflate the main fragment layout

        View rootView = inflater.inflate( R.layout.fragment_main, container, false );

        // 1. initialize the adapter

        TextView emptyTextView = ( TextView ) rootView.findViewById( R.id.tv_empty );
        mForecastAdapter = new ForecastAdapter( getActivity(), this, emptyTextView,
                AbsListView.CHOICE_MODE_NONE );

        // 2a. tell it if to use the special today layout

        mForecastAdapter.setUseTodayLayout( mUseTodayLayout );

        // 2. the recycler

        // 2a. find reference to it

        mForecastRecyclerView = ( RecyclerView ) rootView.findViewById( R.id.rv_forecast );

        // 2b. use a linear layout manager

        mForecastRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );

        // 2c. has fixed size

        mForecastRecyclerView.setHasFixedSize( true );

        // 2d. add scroll listener

        // 2d0. for parallax scrolling

        // 2d0a. works if there is the parallax bar (found in landscape) and we're at least honeycomb

        final View parallaxBar = rootView.findViewById( R.id.parallax_bar );

        // begin if we have a parallax bar
        if ( parallaxBar != null ) {

            // begin if we're at least honeycomb
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {

                // begin mForecastRecyclerView.addOnScrollListener
                mForecastRecyclerView.addOnScrollListener(

                        // begin new RecyclerView.OnScrollListener
                        new RecyclerView.OnScrollListener() {

                            @Override
                            @TargetApi( Build.VERSION_CODES.HONEYCOMB )
                            // begin onScrolled
                            // dx - The amount of horizontal scroll.
                            // dy - The amount of vertical scroll.
                            public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {

                                // 2d0b. change translationY of parallax bar by .5 * -deltaY, with minimum change being -parallaxBarHeight

                                // 0. super stuff
                                // 1. change parallax bar translationY as needed
                                // 1a. if the translationY is greater than bar height,
                                // make the translationY bar height
                                // 1b. if the translationY is greater than zero, set it to zero -
                                // we don't want the bar going lower than it should

                                // 0. super stuff

                                super.onScrolled( recyclerView, dx, dy );

                                // 1. change parallax bar translationY as needed

                                float translationMultiplier = -0.5f;

                                int parallaxBarHeight = parallaxBar.getHeight(); // this leaves the shadow, ha ha

                                float parallaxBarTranslationY = parallaxBar.getTranslationY() +
                                        ( dy * translationMultiplier );

                                // 1a. if the translationY is greater than bar height,
                                // make the translationY bar height

                                if ( parallaxBarTranslationY <= -parallaxBarHeight ) {
                                    parallaxBarTranslationY = -parallaxBarHeight;
                                }

                                // 1b. if the translationY is greater than zero, set it to zero -
                                // we don't want the bar going lower than it should

                                else if ( parallaxBarTranslationY > 0 ) {
                                    parallaxBarTranslationY = 0;
                                }

                                parallaxBar.setTranslationY( parallaxBarTranslationY );

                            } // end onScrolled

                        } // end new RecyclerView.OnScrollListener

                ); // end mForecastRecyclerView.addOnScrollListener

            } // end if we're at least honeycomb

        } // end if we have a parallax bar

        // 2d1. to raise app bar height during scroll

        // 2d1a. works if there is the app bar (found in portrait) and we're on Lollipop

        final AppBarLayout appBarLayout = ( AppBarLayout ) rootView.findViewById( R.id.main_appbar );

        // begin if there is an app bar
        if ( appBarLayout != null ) {

            // 2d1a0. first make the app bar elevation zero

            ViewCompat.setElevation( appBarLayout, 0 );

            // begin if we are an lollipop
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

                // begin mForecastRecyclerView.addOnScrollListener
                mForecastRecyclerView.addOnScrollListener(

                        // begin new RecyclerView.OnScrollListener
                        new RecyclerView.OnScrollListener() {

                            @Override
                            @TargetApi( Build.VERSION_CODES.LOLLIPOP )
                            // begin onScrolled
                            public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {

                                // 2d1a1. if we are not scrolling, no elevation

                                if ( mForecastRecyclerView.computeVerticalScrollOffset() == 0 ) {
                                    appBarLayout.setElevation( 0 );
                                }

                                // 2a1a2. if we are scrolling, elevate

                                else {
                                    appBarLayout.setElevation( appBarLayout.getTargetElevation() );
                                }

                            } // end onScrolled

                        } // end new RecyclerView.OnScrollListener

                ); // end mForecastRecyclerView.addOnScrollListener

            } // end if we are an lollipop

        } // end if there is an app bar

        // 3. set adapter to the list

        mForecastRecyclerView.setAdapter( mForecastAdapter );

        // 4. if there's instance state,

        // 4a. mine it for the scroll position

        if ( savedInstanceState != null ) {
            if ( savedInstanceState.containsKey( BUNDLE_SCROLL_POSITION ) == true ) {
                mCurrentScrollPosition = savedInstanceState.getInt( BUNDLE_SCROLL_POSITION );
            }
            mForecastAdapter.onRestoreInstanceState(savedInstanceState);
        }

        // lastb. return the inflated view

        return rootView;

    } // end onCreateView

    @Override
    // begin onActivityCreated
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {

        // 0. super things
        // 1. wait before doing a transition, just in case the activity needs to be recreated.
        // In standard return transition, this doesn't actually make a difference.
        // 2. start the weather loader

        // 0. super things

        super.onActivityCreated( savedInstanceState );

        // 1. wait before doing a transition, just in case the activity needs to be recreated.
        // In standard return transition, this doesn't actually make a difference.

        if ( mHoldForTransitions == true ) { getActivity().supportPostponeEnterTransition(); }

        // 2. start the weather loader

        getLoaderManager().initLoader( FORECAST_LOADER_ID, null, this );

    } // end onActivityCreated

    @Override
    // begin onResume
    public void onResume() {

        // 0. super stuff
        // 1. register this class as a shared preference listener

        // 0. super stuff

        super.onResume();

        // 1. register this class as a shared preference listener

        PreferenceManager.getDefaultSharedPreferences( getActivity() )
                .registerOnSharedPreferenceChangeListener( this );

    } // end onResume

    @Override
    // begin onPause
    public void onPause() {

        // 0. super stuff
        // 1. unregister this class as a shared preference listener

        // 0. super stuff

        super.onPause();

        // 1. unregister this class as a shared preference listener

        PreferenceManager.getDefaultSharedPreferences( getActivity() )
                .unregisterOnSharedPreferenceChangeListener( this );

    } // end onPause

    @Override
    // begin onOptionsItemSelected
    public boolean onOptionsItemSelected( MenuItem item ) {

        // 0. if the view location item is selected
        // 0a. show the map if possible using the correct URI
        // 0last. return true
        // last. return super things

        int selectedId = item.getItemId();

        // 0. if the view location item is selected

        // begin if view location is selected
        if ( selectedId == R.id.action_view_location ) {

            // 0a. show the map if possible using the correct URI

            Cursor locationCursor = mForecastAdapter.getCursor();

            // begin if there is a cursor that has something
            if ( locationCursor != null && locationCursor.moveToFirst() == true ) {

                String latitudeCoordinate = String.valueOf(
                        locationCursor.getFloat( COLUMN_COORD_LATITUDE ) );
                String longitudeCoordinate = String.valueOf(
                        locationCursor.getFloat( COLUMN_COORD_LONGITUDE ) );

                // 0b. show the map if possible

                Uri mapUri = Uri.parse( "geo:" + latitudeCoordinate + "," + longitudeCoordinate );

                showMap( mapUri );

            } // end if there is a cursor that has something

            // 0last. return true

            return true;

        } // end if view location is selected

        // last. return super things

        return super.onOptionsItemSelected( item );

    } // end onOptionsItemSelected

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    // begin onCreateLoader
    public Loader< Cursor > onCreateLoader( int id, Bundle args ) {

        // 0. create and return a new weather loader for the weather at a given location

        // 0. create and return a new weather loader for the weather at a given location

        String locationSetting = Utility.getPreferredLocation( getActivity() );

        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = WeatherEntry.buildWeatherForLocationWithStartDateUri(
                locationSetting, System.currentTimeMillis() );

        return new CursorLoader( getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder );

    } // end onCreateLoader

    /**
     * Called when a previously created loader has finished its load.
     */
    @Override
    // begin onLoadFinished
    public void onLoadFinished( Loader< Cursor > cursorLoader, Cursor cursor ) {

        // 0. refresh the list view
        // 1. if there is an valid recycler scroll position
        // 1a. scroll to it
        // 2. update the empty view
        // 3. if there cursor has nothing
        // 3a. start any postponed transitions
        // 4. else the cursor has something,
        // 4a. select a view holder based on the current adapter position using the item choice manager, if needed
        // 4b. if we are to do so, start any pending transitions after the recycler has laid its kids out

        // 0. refresh the list view

        // swapCursor - Swap in a new Cursor, returning the old Cursor
        mForecastAdapter.swapCursor( cursor );

        // 1. if there is an valid recycler scroll position
        // 1a. scroll to it

        if ( mCurrentScrollPosition != ListView.INVALID_POSITION ) {
            mForecastRecyclerView.smoothScrollToPosition( mCurrentScrollPosition );
        }

        // 2. update the empty view

        updateEmptyView();

        // 3. if there cursor has nothing

        // 3a. start any postponed transitions

        if ( cursor.getCount() == 0 ) { getActivity().supportStartPostponedEnterTransition(); }

        // 4. else the cursor has something,

        // begin else the cursor has something
        else {

            // 4a. select a view holder based on the current adapter position using the item choice manager, if needed

            // begin mForecastRecyclerView.getViewTreeObserver().addOnPreDrawListener
            mForecastRecyclerView.getViewTreeObserver().addOnPreDrawListener(

                    // begin new ViewTreeObserver.OnPreDrawListener
                    new ViewTreeObserver.OnPreDrawListener() {

                        @Override
                        // begin onPreDraw
                        public boolean onPreDraw() {

                            // Since we know we're going to get items, we keep the listener around until
                            // we see Children.
                            // begin if the recycler has kids
                            if ( mForecastRecyclerView.getChildCount() > 0 ) {

                                mForecastRecyclerView.getViewTreeObserver().removeOnPreDrawListener( this );

                                int itemPosition = mForecastAdapter.getSelectedItemPosition();

                                if ( RecyclerView.NO_POSITION == itemPosition ) itemPosition = 0;

                                RecyclerView.ViewHolder vh = mForecastRecyclerView
                                        .findViewHolderForAdapterPosition( itemPosition );

                                if ( null != vh && mAutoSelectView ) {
                                    mForecastAdapter.selectView( vh );
                                }

                                // 4b. if we are to do so, start any pending transitions after
                                // the recycler has laid its kids out

                                if ( mHoldForTransitions == true ) {
                                    getActivity().supportStartPostponedEnterTransition();
                                }

                                return true;

                            } // end if the recycler has kids

                            return false;

                        } // end onPreDraw

                    } // end new ViewTreeObserver.OnPreDrawListener

            ); // end mForecastRecyclerView.getViewTreeObserver().addOnPreDrawListener

        } // end else the cursor has something

    } // end onLoadFinished

    /**
     * Called when a previously created loader is being reset.
     */
    @Override
    // begin onLoaderReset
    public void onLoaderReset( Loader< Cursor > cursorLoader ) {

        // 0. remove any cursors being used

        // 0. remove any cursors being used

        mForecastAdapter.swapCursor( null );

    } // end onLoaderReset

    @Override
    // begin onSaveInstanceState
    public void onSaveInstanceState( Bundle outState ) {

        // 0. super stuff
        // 1. if there is an item selected
        // 1a. put the current scroll position in the bundle
        // 2. save state for the adapter

        // 0. super stuff

        super.onSaveInstanceState( outState );

        // 1. if there is an item selected

        // 1a. put the current scroll position in the bundle

        // no item selected will leave the position at NO_POSITION

        if ( mCurrentScrollPosition != RecyclerView.NO_POSITION ) {
            outState.putInt( BUNDLE_SCROLL_POSITION, mCurrentScrollPosition );
        }

        // 2. save state for the adapter

        mForecastAdapter.onSaveInstanceState(outState);

    } // end onSaveInstanceState

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    // begin onSharedPreferenceChanged
    public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {

        // 0. if the changed preference is the location status one
        // 0a. update the empty view based on the location status

        // 0. if the changed preference is the location status one
        // 0a. update the empty view based on the location status

        if ( key.equals( getString( R.string.pref_location_status_key ) ) == true ) {
            updateEmptyView();
        }

    } // end onSharedPreferenceChanged

    @Override
    // begin onClick
    public void onClick( Long date, WeatherViewHolder viewHolder ) {

        // 0. get the uri for weather for this date
        // 1. pass the date uri and the view holder to the callback listener
        // 2. store the view holder's position as the current scroll position

        // 0. get the uri for weather for this date

        String locationSetting = Utility.getPreferredLocation( getActivity() );

        Uri dateUri = WeatherEntry.buildWeatherForLocationWithSpecificDateUri( locationSetting, date );

        // 1. pass the date uri and the view holder to the callback listener

        forecastCallbackListener.onForecastItemSelected( dateUri, viewHolder );

        // 2. store the view holder's position as the current scroll position

        mCurrentScrollPosition = viewHolder.getAdapterPosition();

    } // end onClick

    @Override
    // begin onDestroy
    public void onDestroy() {

        // 0. super stuff
        // 1. if there's the recycler, remove its scroll listeners

        // 0. super stuff

        super.onDestroy();

        // 1. if there's the recycler, remove its scroll listeners

        if ( mForecastRecyclerView != null ) { mForecastRecyclerView.clearOnScrollListeners(); }

    } // end onDestroy

    /* Other Methods */

    /** Refreshes the weather. */
    // begin method updateWeather
    private void updateWeather() {

        // 0. sync up

        // 0. sync up

        SunshineSyncAdapter.syncImmediately( getActivity() );

    } // end method updateWeather

    /**
     * Handles what happens when the user's preferred location is changed.
     * More precisely, when the location is changed, this method updates the weather
     * and then restarts the loader.
     * */
    // begin method onLocationChanged
    public void onLocationChanged() {

        // 0. update the weather
        // 1. restart the loader

        // 0. update the weather

        updateWeather();

        // 1. restart the loader

        // restartLoader - Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader( FORECAST_LOADER_ID, null, this );

    } // end method onLocationChanged

    /**
     * Show a map with the passed URI
     * @param mapUri to use to show a map
     * */
    // begin method showMap
    private void showMap( Uri mapUri ) {

        // 0. use an implicit view intent
        // 1. put the Uri data in
        // 2. if there is a map app
        // 2a. start the activity
        // 3. otherwise
        // 3a. tell the user of the sadness

        // 0. use an implicit view intent

        Intent mapIntent = new Intent( Intent.ACTION_VIEW );

        // 1. put the Uri data in

        mapIntent.setData( mapUri );

        // 2. if there is a map app

        // 2a. start the activity

        if ( mapIntent.resolveActivity( getActivity().getPackageManager() ) != null ) {
            startActivity( mapIntent );
        }

        // 3. otherwise

        // 3a. tell the user of the sadness

        else {
            Toast.makeText( getActivity(), R.string.message_error_no_maps_app, Toast.LENGTH_SHORT )
                    .show();
        }

    } // end method showMap

    /**
     * Updates the empty list view with information
     * to help the user determine why the list is empty. */
    // begin method updateEmptyView
    private void updateEmptyView() {

        // 0. if our adapter's cursor has nothing in it
        // 0a. get the empty view
        // 0a1. if we get the empty view successfully
        // 0a1a. tell the user why the list is empty - it can be empty due to
        // 0a1a0. there simply being no info available or
        // 0a1a1. the server being down or
        // 0a1a2. the server having returned an error or
        // 0a1a3. the location is invalid or
        // 0a1a4. there is a network problem

        // 0. if our adapter's cursor has nothing in it

        // begin if the adapter cursor is empty
        if ( mForecastAdapter.getCursor().getCount() == 0 ) {

            // 0a. get the empty view

            TextView emptyTextView = null;
            if ( getView() != null ) {
                 emptyTextView = ( TextView ) getView().findViewById( R.id.tv_empty );
            }

            // 0a1. if we get the empty view successfully

            // begin if the empty view exists
            if ( emptyTextView != null ) {

                // 0a1a. tell the user why the list is empty - it can be empty due to

                // 0a1a0. there simply being no info available or

                int message = R.string.message_error_no_weather_info;

                @SunshineSyncAdapter.LocationStatus int locationStatus = Utility.getLocationStatus(
                        getActivity() );

                // begin switch to know about the status
                switch ( locationStatus ) {

                    // 0a1a1. the server being down or

                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.message_error_no_weather_info_server_down;
                        break;

                    // 0a1a2. the server having returned an error or

                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.message_error_no_weather_info_server_error;
                        break;

                    // 0a1a3. the location is invalid or

                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        message = R.string.message_error_no_weather_info_invalid_location;
                        break;

                    default:

                        // 0a1a4. there is a network problem

                        if ( Utility.isNetworkAvailable( getActivity() ) == false ) {
                            message = R.string.message_error_no_weather_info_no_connectivity;
                        }

                } // end switch to know about the status

                emptyTextView.setText( message );

            } // end if the empty view exists

        } // end if the adapter cursor is empty

    }  // end method updateEmptyView

} // end fragment ForecastFragment