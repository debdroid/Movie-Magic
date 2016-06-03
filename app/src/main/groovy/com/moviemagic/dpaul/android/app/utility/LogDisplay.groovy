package com.moviemagic.dpaul.android.app.utility

import android.util.Log;
import groovy.transform.CompileStatic

@CompileStatic
/**
 * * Helper method to display log
 * All logs can be switched on or off by changing the SHOW_LOG boolean variable
 *
 * @param tag The tag to be used for this log
 * @param msg The log message to be displayed
 * @param logFlag The flag to indicate if log needs to be printed
 */

class LogDisplay {
    private static final boolean SHOW_ALL_LOG_FLAG = true
    public static final boolean MOVIE_MAGIC_CONTRACT_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_DB_HELPER_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_PROVIDER_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG = true
    public static final boolean MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_SYNC_SERVICE_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_MAIN_LOG_FLAG = true
    public static final boolean JSON_PARSE_LOG_FLAG = false
    public static final boolean GRID_FRAGMENT_LOG_FLAG = true
    public static final boolean GRID_ADAPTER_LOG_FLAG = false
    public static final boolean LOAD_MORE_DATA_LOG_FLAG = true

    static void callLog(String tag, String msg, Boolean logFlag) {
        if(SHOW_ALL_LOG_FLAG) {
            if(logFlag) {
                Log.v(tag, msg)
            }
        }
    }

}