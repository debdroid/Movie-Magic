package com.moviemagic.dpaul.android.app.syncadapter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Build
import android.os.Bundle
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
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
    public static void initializeSyncAdapter(Context context) {
        LogDisplay.callLog(LOG_TAG,'initializeSyncAdapter is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
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
        LogDisplay.callLog(LOG_TAG,'getSyncAccount is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
        // Get an instance of the Android account manager
        final AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE)

        //Check if any account exists
        final Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.authenticator_account_type))
        final Account newAccount
        if(accounts.size() == 1) {
            LogDisplay.callLog(LOG_TAG,"Existing account. Account name->${accounts[0].name}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
            //Application can have only one account, so safe to use the following line
            newAccount = accounts[0]
        } else if (accounts.size() > 1) {
            LogDisplay.callLog(LOG_TAG,"Got more than one account, investigate. Accounts->${accounts.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
            return null
        } else { // This part will be executed only once - first install of the app
            LogDisplay.callLog(LOG_TAG,'Create a new dummy account',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
            // Create a dummy account - for the first time use
            newAccount = new Account(
                    context.getString(R.string.app_name), context.getString(R.string.authenticator_account_type))
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
//        // If the password doesn't exist, the account doesn't exist
//        if (accountManager.getPassword(newAccount) == null) {
//            /*
//             * Add the account and account type, no password or user data
//             * If successful, return the Account object, otherwise report an error.
//             */
//            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
//                return null
//            }
//            /*
//             * If you don't set android:syncable="true" in
//             * in your <provider> element in the manifest,
//             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
//             * here.
//             */
//            onAccountCreated(newAccount, context)
//        }
        return newAccount
    }

    /**
     * Helper method to set the SyncAdapter execution strategy
     *
     * @param newAccount The account used for SyncAdapter
     * @param context The context used to setup the execution frequency
     */
    public static void onAccountCreated(Account newAccount, Context context) {
        LogDisplay.callLog(LOG_TAG,'onAccountCreated is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
        /*
         * Account is set, so now configure periodic sync
         */
        configurePeriodicSync(context, newAccount)

        /*
         * Without calling setSyncAutomatically, periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)

        /*
         * Finally, let's do a sync to get things started
         */
        //Commenting this out as it seems when the application is fresh installed the sync adapter is called it self
        //and this is causing duplicate call. In immediate sync is needed then anyhow that can be called from
        //MovieMagicMainActivity during testing phase
        //syncImmediately(context)
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     * @param context The context used to setup the execution frequency
     * @param account The account for which the Periodic Sync is to be set
     */
    public static void configurePeriodicSync(Context context, Account account) {
        LogDisplay.callLog(LOG_TAG,'configurePeriodicSync is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
//        Account account = getSyncAccount(context)
        final String authority = context.getString(R.string.content_authority)
        //TODO: Need to look into this Build.VERSION_CODES later
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.JELLY_BEAN= $Build.VERSION_CODES.JELLY_BEAN",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.KITKAT= $Build.VERSION_CODES.KITKAT",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
//        LogDisplay.callLog(LOG_TAG,"Build.VERSION_CODES.LOLLIPOP= $Build.VERSION_CODES.LOLLIPOP",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
       //Somehow while running in Jelly bean it cannot find Build.VERSION_CODES.KITKAT, yet to figure out why!
        //So using the API number (19 - KITKAT)itself here
        if (Build.VERSION.SDK_INT >= 19) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            final SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build()
            ContentResolver.requestSync(request)
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL)
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        LogDisplay.callLog(LOG_TAG,'syncImmediately is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
        final Bundle bundle = new Bundle()
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle)
    }

    /**
     * Helper method to remove periodic sync for a particular account
     *
     * @param account The account for which the Periodic sync needs to be removed
     * @param context The context used to retrieve string value (authority)
     */
    public static void removePeriodicSync(Account account, Context context) {
        LogDisplay.callLog(LOG_TAG,'removePeriodicSync is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG)
        final String authority = context.getString(R.string.content_authority)
        if (Build.VERSION.SDK_INT >= 21) {
            final SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build()
            ContentResolver.cancelSync(request)
        } else {
            ContentResolver.removePeriodicSync(account, authority, new Bundle())
        }
    }
}