package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName()

    @Override
    void onCreate(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.SETTINGS_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit()
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}