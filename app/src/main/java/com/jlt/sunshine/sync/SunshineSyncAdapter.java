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

package com.jlt.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.jlt.sunshine.R;

import static android.Manifest.permission.GET_ACCOUNTS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * The sync adapter we will use for fetching app data.
 * */
// begin class SunshineSyncAdapter
public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {

    /* CONSTANTS */
    
    /* Integers */
    
    /* Strings */

    /**
     * The logger.
     */
    private static final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();

    
    /* VARIABLES */

    /* CONSTRUCTOR */

    /**
     * Creates an {@link AbstractThreadedSyncAdapter}.
     *
     * @param context        the {@link Context} that this is running within.
     * @param autoInitialize if true then sync requests that have
     *                       {@link ContentResolver#SYNC_EXTRAS_INITIALIZE} set will be internally handled by
     *                       {@link AbstractThreadedSyncAdapter} by calling
     *                       {@link ContentResolver#setIsSyncable(Account, String, int)} with 1 if it
     */
    public SunshineSyncAdapter( Context context, boolean autoInitialize ) {
        super( context, autoInitialize );
    }
    
    /* METHODS */
    
    /* Getters and Setters */
    
    /* Overrides */

    @Override
    // begin onPerformSync
    public void onPerformSync( Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult ) {
        Log.d( LOG_TAG, "onPerformSync: called" );
    } // end onPerformSync
    
    /* Other Methods */

    /* statics */

    /**
     * Helper method to have the sync adapter sync immediately.
     *
     * @param context The {@link Context} used to access the account service
     * */
    // begin method syncImmediately
    public static void syncImmediately( Context context ) {

        // 0. create a bundle
        // 1. put an argument to sync asap
        // 2. put an argument to sync manually
        // 3. start sync

        // 0. create a bundle

        Bundle bundle = new Bundle();

        // 1. put an argument to sync asap

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_EXPEDITED, true );

        // 2. put an argument to sync manually

        bundle.putBoolean( ContentResolver.SYNC_EXTRAS_MANUAL, true );

        // 3. start sync

        // requestSync - Start an asynchronous sync operation.
        ContentResolver.requestSync( getSyncAccount( context ),
                context.getString( R.string.content_authority ), bundle );

    } // end method syncImmediately

    /**
     * Helper method to get the fake account to be used with the sync adapter, or
     * make one if the fake account doesn't currently exist.
     *
     * If we make a new account, we call onAccountCreated so we can initialize things.
     *
     * @param context The {@link Context} used to access the account service.
     *
     * @return A fake account.
     * */
    // begin method getSyncAccount
    public static Account getSyncAccount( Context context ) {

        // 0. get the accounts manager
        // 1. create the account type and default account
        // 2. if the account doesn't exist
        // 2a. attempt to add it
        // 2a1. if successful
        // 2a1a. return the account created at 1
        // 2a2. otherwise
        // 2a2a. report an error
        // 3. otherwise the account exists
        // 3a. return the existing account

        // 0. get the accounts manager

        AccountManager accountManager = ( AccountManager ) context.getSystemService(
                Context.ACCOUNT_SERVICE );

        // 1. create the account type and default account

        Account newAccount = new Account( context.getString( R.string.app_name ),
                context.getString( R.string.sync_account_type ) );

        // begin checking if we have permission to access accounts
        if ( ContextCompat.checkSelfPermission( context, GET_ACCOUNTS ) == PERMISSION_GRANTED ) {

            // 2. if the account doesn't exist

            // if the account doesn't have a password, the account doesn't exist

            // begin if the account has no password
            // getPassword - Gets the saved password associated with the account.
            //  This is intended for authenticators and related code;
            //  applications should get an auth token instead.
            if ( accountManager.getPassword( newAccount ) == null ) {

                // 2a. attempt to add it

                // 2a1. if successful
                // 2a1a. return the account created at 1

                // addAccountExplicitly - Adds an account directly to the AccountManager.
                //  Normally used by sign-up wizards associated with authenticators,
                //  not directly by applications.
                if ( accountManager.addAccountExplicitly( newAccount, "", null ) == true ) {
                    return newAccount;
                }

                // 2a2. otherwise
                // 2a2a. report an error

                else { throw new RuntimeException( "Account not added." ); }

            } // end if the account has no password

            // 3. otherwise the account exists

            // 3a. return the existing account

            else { return newAccount; }

        } // end checking if we have permission to access accounts

        // begin else we have no permission to access accounts
        else {

            Log.e( LOG_TAG, "getSyncAccount: No permissions to access accounts." );

            return null;

        } // end else we have no permission to access accounts

    } // end method getSyncAccount

    /* INNER CLASSES */

} // end class SunshineSyncAdapter