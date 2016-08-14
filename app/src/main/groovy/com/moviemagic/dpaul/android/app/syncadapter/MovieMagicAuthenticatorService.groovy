package com.moviemagic.dpaul.android.app.syncadapter

import android.app.Service
import android.content.Intent
import android.os.IBinder
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
        mAuthenticator = new MovieMagicAuthenticator(this)
    }

    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder()
    }
}
