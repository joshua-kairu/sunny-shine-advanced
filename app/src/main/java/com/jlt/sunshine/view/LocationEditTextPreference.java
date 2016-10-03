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


package com.jlt.sunshine.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jlt.sunshine.R;

/**
 * A custom {@link android.preference.EditTextPreference} to enforce a
 * 3 character minimum length for preferred location.
 * */
// begin class LocationEditTextPreference
// TextWatcher - When an object of a type is attached to an Editable,
// its methods will be called when the text is changed. Kind of like a text change listener
public class LocationEditTextPreference extends EditTextPreference implements TextWatcher {

    /* CONSTANTS */
    
    /* Integers */

    /** The default minimum length of the location preference string. */
    private static final int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = LocationEditTextPreference.class.getSimpleName();
        
    /* VARIABLES */

    /* Edit Texts */

    /** The {@link EditText} used by this preference for displaying the text. */
    private EditText mEditText;

    /* Primitives */

    private int mMinLength; // ditto

    /* CONSTRUCTOR */

    // begin constructor
    public LocationEditTextPreference( Context context, AttributeSet attrs ) {

        // 0. super stuff
        // 1. get the minimum length from XML
        // 2. recycle the XML

        // 0. super stuff

        super( context, attrs );

        // 1. get the minimum length from XML

        TypedArray a = context.getTheme().obtainStyledAttributes( attrs,
                R.styleable.LocationEditTextPreference, 0, 0 );

        // begin trying to get things from XML
        try {

            mMinLength = a.getInt( R.styleable.LocationEditTextPreference_minLength,
                    DEFAULT_MINIMUM_LOCATION_LENGTH );

            Log.e( LOG_TAG, "LocationEditTextPreference: minLength = " + mMinLength );

        } // end trying to get things from XML

        // 2. recycle the XML

        finally {
            a.recycle();
        }

    } // end constructor

    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    /** Shows the dialog associated with this Preference. */
    // begin showDialog
    protected void showDialog( Bundle state ) {

        // 0. super stuff
        // 1. get the preference edit text
        // 2. disable the positive button if the text in it is less than the minimum
        // 3. register a listener for changes in the preference edit text

        // 0. super stuff

        super.showDialog( state );

        // 1. get the preference edit text

        // getEditText - Returns the EditText widget that will be shown in the dialog.
        // This works because at this time, we have called super so
        // the edit text has already been created.
        mEditText = getEditText();

        int editTextLength = mEditText.getText().length();

        // 2. disable the positive button if the text in it is less than the minimum

        Dialog d = getDialog();

        // begin if the dialog is an alert dialog
        if ( d instanceof AlertDialog && editTextLength < mMinLength ) {

            ( ( AlertDialog ) d ).getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( false );

        } // end if the dialog is an alert dialog

        // 3. register a listener for changes in the preference edit text

        mEditText.addTextChangedListener( this );

    } // end showDialog

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) { }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) { }

    @Override
    // begin afterTextChanged
    public void afterTextChanged( Editable s ) {

        // -1. if the dialog is an alert dialog
        // 0. get the length of the input text
        // 1. if the length of the text is at least the minimum length
        // 1a. enable the positive button
        // 2. if the length of the text is less than the minimum length
        // 2a. disable the positive button

        // -1. if the dialog is an alert dialog

        Dialog d = getDialog();

        // begin if the dialog is an alert dialog
        if ( d instanceof AlertDialog ) {

            AlertDialog alertDialog = ( AlertDialog ) d;

            Button positiveButton = alertDialog.getButton( AlertDialog.BUTTON_POSITIVE );

            // 0. get the length of the input text

            int textLength = mEditText.getText().length();

            // 1. if the length of the text is at least the minimum length

            // 1a. enable the positive button

            if ( textLength >= mMinLength ) { positiveButton.setEnabled( true ); }

            // 2. if the length of the text is less than the minimum length
            // 2a. disable the positive button

            else { positiveButton.setEnabled( false ); }

        } // end if the dialog is an alert dialog

    } // end afterTextChanged

    /* Other Methods */
    
    /* INNER CLASSES */

} // end class LocationEditTextPreference