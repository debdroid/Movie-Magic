package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class CollectionMovieActivity extends AppCompatActivity {

    private static final String LOG_TAG = CollectionMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_collection)
        final Toolbar toolbar = (Toolbar) findViewById(R.id.collection_activity_toolbar)
        setSupportActionBar(toolbar)
        //Enable back to home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Bundle extras = getIntent().getExtras()
            if (extras) {
              final String category = extras.getInt(GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION)
                final int collectionId = extras.getInt(GlobalStaticVariables.MOVIE_COLLECTION_ID)
                //Collection id is required here, so passed on that value
                final GridFragment fragment = new GridFragment(category, collectionId)
                final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                //Set the custom animation
//                fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_up_animation,0)
                fragmentTransaction.replace(R.id.movie_collection_grid, fragment)
                fragmentTransaction.commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data', LogDisplay.COLLECTION_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}