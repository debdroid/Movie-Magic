package com.moviemagic.dpaul.android.app

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName()

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        final Toolbar toolbar = findViewById(R.id.search_activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setTitle(getString(R.string.title_activity_search))

        // Get the intent, verify the action and get the query
        final Intent intent = getIntent()
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY)
            LogDisplay.callLog(LOG_TAG,"Query string -> $query",LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)

//            doMySearch(query)
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Press appbar back button to go to previous activity
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}