package com.moviemagic.dpaul.android.app.syncadapter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Build
import android.os.Bundle
import com.moviemagic.dpaul.android.app.R
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicSyncAdapterUtility {
    private static final String LOG_TAG = MovieMagicSyncAdapterUtility.class.getSimpleName()

    // Interval at which to sync with the movie data, in milliseconds.
    // 60 seconds (1 minute) * 360 = 6 hours
    private static final int SECONDS_PER_MINUTE = 60
    private static final int SYNC_INTERVAL_IN_MINUTES = 360
    private static final int SYNC_INTERVAL = SECONDS_PER_MINUTE * SYNC_INTERVAL_IN_MINUTES
    private static final int SYNC_FLEXTIME = SECONDS_PER_MINUTE * 120

    /**
     * Helper method to initialise the MovieMagic SyncAdapter
     *
     * @param context The context used to initialise the SyncAdapter
     */
    static void initializeSyncAdapter(Context context) {
        getSyncAccount(context)
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE)

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type))

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context)
        }
        return newAccount
    }

    /**
     * Helper method to set the SyncAdapter execution strategy
     *
     * @param newAccount The account used for SyncAdapter
     * @param context The context used to setup the execution frequency
     * @return a fake account.
     */
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Account is set, so now configure periodic sync
         */
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)

        /*
         * Without calling setSyncAutomatically, periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)

        /*
         * Finally, let's do a sync to get things started
         */
        //Commenting this out as it seems when the application is fresh installed the sync adapter is called it self
        //and this is causing duplicate call. In immmediate sync is needed tehn anyhow that can be called from
        //MovieMagicMainActivity during testing phase
        //syncImmediately(context)
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context)
        String authority = context.getString(R.string.content_authority)
        //TODO: Need to look into this Build.VERSION_CODES later
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.JELLY_BEAN= $Build.VERSION_CODES.JELLY_BEAN",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.KITKAT= $Build.VERSION_CODES.KITKAT",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.LOLLIPOP= $Build.VERSION_CODES.LOLLIPOP",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
       //Somehow while running in Jelly bean it cannot find Build.VERSION_CODES.KITKAT, yet to figure out why!
        //So using the API number (19 - KITKAT)itself here
        if (Build.VERSION.SDK_INT >= 19) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build()
            ContentResolver.requestSync(request)
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval)
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle)
    }
}