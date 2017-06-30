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

package com.moviemagic.dpaul.android.app.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */

@CompileStatic
class MovieMagicAuthenticatorService extends Service {
    private static final String LOG_TAG = MovieMagicAuthenticatorService.class.getSimpleName()

    // Instance field that stores the authenticator object
    private MovieMagicAuthenticator mAuthenticator

    @Override
    void onCreate() {
        // Create a new authenticator object
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG)
        mAuthenticator = new MovieMagicAuthenticator(this)
    }

    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    IBinder onBind(final Intent intent) {
        LogDisplay.callLog(LOG_TAG,'onBind is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG)
        return mAuthenticator.getIBinder()
    }
}
