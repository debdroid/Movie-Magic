package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import com.moviemagic.dpaul.android.app.R;
import groovy.transform.CompileStatic

import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
class Utility {
    private static final String LOG_TAG = JsonParse.class.getSimpleName()

    static String getTodayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String todayDate = simpleDateFormat.format(new Date())
        LogDisplay.callLog(LOG_TAG, "Date stamp-> $todayDate", LogDisplay.UTILITY_LIST_LOG_FLAG)
        return todayDate
    }

    static String convertMilliSecsToOrigReleaseDate(long timeInMilis) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        String dateString = simpleDateFormat.format(new Date(timeInMilis))
        return dateString
    }

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

    public static String formatRunTime(Context ctx, int runTime) {
        int hourVal
        def minVal
        hourVal = runTime / 60 as Integer
        minVal = runTime % 60
        return String.format(ctx.getString(R.string.movie_run_time),hourVal,minVal)
    }

    public static String formatCurrencyInDollar(int val) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US)
        def formattedValue = formatter.format(val)
        return formattedValue
    }

    public static String formatMilliSecondsToDate(long timeInMilliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy")
        String dateString = formatter.format(new Date(timeInMilliSeconds))
        return dateString
    }

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