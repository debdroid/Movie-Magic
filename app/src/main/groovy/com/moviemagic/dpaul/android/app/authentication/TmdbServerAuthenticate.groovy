package com.moviemagic.dpaul.android.app.authentication

import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class TmdbServerAuthenticate implements TmdbAuthenticateInterface {
    private static final String LOG_TAG = TmdbServerAuthenticate.class.getSimpleName()

    @Override
    String tmdbUserSignIn(String userName, String password, String authTokenType) throws Exception {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
        return null
    }
}