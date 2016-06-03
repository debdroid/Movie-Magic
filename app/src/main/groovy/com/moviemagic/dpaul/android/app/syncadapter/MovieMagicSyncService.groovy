package com.moviemagic.dpaul.android.app.syncadapter

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.moviemagic.dpaul.android.app.utility.LogDisplay;
import groovy.transform.CompileStatic

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 */
@CompileStatic
class MovieMagicSyncService extends Service {
    private static final String LOG_TAG = MovieMagicSyncService.class.getSimpleName()

    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object()
    // Storage for an instance of the sync adapter
    private static MovieMagicSyncAdapter sMovieMagicSyncAdapter = null

    /*
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        LogDisplay.callLog(LOG_TAG,'onCreate - MovieMagicSyncService',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        synchronized (sSyncAdapterLock) {
            if (sMovieMagicSyncAdapter == null) {
                sMovieMagicSyncAdapter = new MovieMagicSyncAdapter(getApplicationContext(), true)
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     *
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return sMovieMagicSyncAdapter.getSyncAdapterBinder()
    }
}