package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class DonateActivity extends AppCompatActivity {
    private static final String LOG_TAG = DonateActivity.class.getSimpleName()

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.DONATE_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
    }
}