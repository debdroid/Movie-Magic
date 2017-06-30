/*
 * Copyright 2017 Debashis Paul

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moviemagic.dpaul.android.app.syncadapter

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
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
        /**
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_SYNC_SERVICE_LOG_FLAG)
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
    public IBinder onBind(final Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        LogDisplay.callLog(LOG_TAG,'onBind is called',LogDisplay.MOVIE_MAGIC_SYNC_SERVICE_LOG_FLAG)
        return sMovieMagicSyncAdapter.getSyncAdapterBinder()
    }
}