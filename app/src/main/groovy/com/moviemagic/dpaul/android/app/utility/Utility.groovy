package com.moviemagic.dpaul.android.app.utility;

import groovy.transform.CompileStatic

import java.text.SimpleDateFormat

@CompileStatic
class Utility {
    static String getTodayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ")
        final String todayDate = simpleDateFormat.format(new Date(0))
        return todayDate
    }
}