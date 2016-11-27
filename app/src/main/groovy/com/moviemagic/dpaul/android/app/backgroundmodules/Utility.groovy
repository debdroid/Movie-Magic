package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager
import com.moviemagic.dpaul.android.app.R;
import groovy.transform.CompileStatic

import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
class Utility {
    private static final String LOG_TAG = JsonParse.class.getSimpleName()

    /**
     * This utility method returns the current date
     * @return Today's date
     */
    static String getTodayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String todayDate = simpleDateFormat.format(new Date())
        LogDisplay.callLog(LOG_TAG, "Date stamp-> $todayDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return todayDate
    }

    /**
     * This utility method returns the timestamp 10 days prior to current date
     * @return Today's date
     */
    static String getTenDayPriorDate() {
        // Set the calendar to current date
        final Calendar calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        final Date date = calendar.getTime()
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String tenDayPriorDate = simpleDateFormat.format(date)
        LogDisplay.callLog(LOG_TAG, "Date stamp-> $tenDayPriorDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return tenDayPriorDate
    }

    /**
     * This utility method converts the date representation of milliseconds to regular date format
     * @param timeInMillis Date represented in milliseconds
     * @return Formatted date value
     */
    static String convertMilliSecsToOrigReleaseDate(long timeInMillis) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        final String dateString = simpleDateFormat.format(new Date(timeInMillis))
        return dateString
    }

    /**
     * This utility method formats the date for friendly user display
     * @param date Date to be formatted
     * @return Formatted date value
     */
    static String formatFriendlyDate(String date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy")
        if(date.size() == 10) {
            if (date.getAt(4) == '-' && date.getAt(7) == '-') {
                String dateString = simpleDateFormat.format(new SimpleDateFormat("yyyy-MM-dd").parse(date))
                return dateString
            } else
                return date
        } else
            return date
    }

    /**
     * This utility method converts the minutes to hour and minutes
     * @param ctx Application context
     * @param runTime Minute value
     * @return Converted hour and minute value
     */
    public static String formatRunTime(Context ctx, int runTime) {
        int hourVal
        def minVal
        hourVal = runTime / 60 as Integer
        minVal = runTime % 60
        return String.format(ctx.getString(R.string.movie_run_time),hourVal,minVal)
    }

    /**
     * This utility method formats the dollar value in US currency
     * @param val Dollar value to be formatted
     * @return Formattd dollar value in us currency
     */
    public static String formatCurrencyInDollar(int val) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US)
        def formattedValue = formatter.format(val)
        return formattedValue
    }

    /**
     * This utility method converts the date representation of milliseconds to formatted date
     * @param timeInMilliSeconds Date represented in milliseconds
     * @return Formatted friendly display date
     */
    public static String formatMilliSecondsToDate(long timeInMilliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy")
        String dateString = formatter.format(new Date(timeInMilliSeconds))
        return dateString
    }

    /**
     * This utility method read the shared preference for theme settings and returns true if theme is set as 'Dynamic Theme'
     * @param context The application Context
     * @return True if the theme is set as 'Dynamic Theme' otherwise False
     */
    public static boolean isDynamicTheme(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final String themeType = sharedPreferences.getString(context.getString(R.string.pref_theme_key),
                                    context.getString(R.string.pref_theme_default_value))
        if(themeType.equals(context.getString(R.string.pref_theme_default_value))) {
            // If default app theme (i.e. Dynamic then return true
            return true
        } else {
            // If Static theme is set then return false
            return false
        }
    }

    /**
     * This utility method determines if the application can download data (i.e. internet connection is available or
     * if user selected download only on WiFi & user is connected to internet using WiFi)
     * @param context The application Context
     * @return Returns true if all set to download data otherwise returns False
     */
    public static boolean isReadyToDownload(Context context) {
        final boolean isUserOnline = isOnline(context)
        final boolean isUserSelectedOnlyOnWifi = isOnlyWifi(context)

        if(isUserOnline) {
            if((!isUserSelectedOnlyOnWifi) || (isUserSelectedOnlyOnWifi && GlobalStaticVariables.WIFI_CONNECTED)) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }

    /**
     * This utility method read the shared preference checks if user selected to use reduce data
     * @param context The application Context
     * @return Returns the Reduce Data flag
     */
    public static boolean isReducedDataOn(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final boolean reduceDataFlag = sharedPreferences.getBoolean(context.getString(R.string.pref_reduce_data_use_key),false)
        return reduceDataFlag
    }

    /**
     * This utility method read the shared preference and checks if user selected to use WiFi only for loading data
     * @param context The application Context
     * @return Returns the WiFiFlag
     */
    public static boolean isOnlyWifi(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        final boolean wifiFlag = sharedPreferences.getBoolean(context.getString(R.string.pref_wifi_download_key),false)
        return wifiFlag
    }

    /**
     * This utility method checks if the mobile is connected to a network to perfrom network operation. It also
     * sets the WiFi or Mobile data flag accordingly based on the network type
     * @param ctx Application Context
     * @return True if WiFi or Mobile network is available otherwise returns False
     */
    public static boolean isOnline(Context ctx) {
        final ConnectivityManager connMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        final NetworkInfo activeInfo = connMgr.getActiveNetworkInfo()
        if (activeInfo != null && activeInfo.isConnected()) {
            GlobalStaticVariables.WIFI_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_WIFI
            GlobalStaticVariables.MOBILE_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE
            return true
        } else {
            GlobalStaticVariables.WIFI_CONNECTED = false
            GlobalStaticVariables.MOBILE_CONNECTED = false
            return false
        }
    }

    /**
     * This utility method determines the mpaa for different countries - at this moment it only supports US & UK
     * @param mpaa MPAA indicator
     * @param locale Country
     * @return Logo to be displayed as determined by mpaa indicator and locale
     */
    public static int getIconResourceForMpaaRating(String mpaa, String locale) {
        //Remove any white spaces from mpaa and locale string
        if(mpaa) {
            mpaa.replaceAll("\\s", "")
        }
        if(locale) {
            locale.replaceAll("\\s", "")
        }

        if(locale == 'US') {
            if (mpaa == 'G') {
                return R.drawable.mpaa_us_g
            } else if (mpaa == 'PG') {
                return R.drawable.mpaa_us_pg
            } else if (mpaa == 'PG-13') {
                return R.drawable.mpaa_us_pg13
            } else if (mpaa == 'R') {
                return R.drawable.mpaa_us_r
            } else if (mpaa == 'NC-17') {
                return R.drawable.mpaa_us_nc17
            }
        }

        if(locale == 'GB') {
            if (mpaa == 'U') {
                return R.drawable.mpaa_uk_u
            } else if (mpaa == 'PG') {
                return R.drawable.mpaa_uk_pg
            } else if (mpaa == '12A') {
                return R.drawable.mpaa_uk_12a
            } else if (mpaa == '12') {
                return R.drawable.mpaa_uk_12
            } else if (mpaa == '15') {
                return R.drawable.mpaa_uk_15
            } else if (mpaa == '18') {
                return R.drawable.mpaa_uk_18
            } else if (mpaa == 'R18') {
                return R.drawable.mpaa_uk_r18
            }
        }

        return -1
    }
}