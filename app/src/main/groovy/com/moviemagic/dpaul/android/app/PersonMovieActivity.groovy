package com.moviemagic.dpaul.android.app

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class PersonMovieActivity extends AppCompatActivity {
    private static final String LOG_TAG = PersonMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_movie)
        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Uri uri = getIntent().getData()
            if (uri) {
                LogDisplay.callLog(LOG_TAG, "Intent Data->${uri.toString()}", LogDisplay.PERSON_MOVIE_ACTIVITY_LOG_FLAG)
                final Bundle bundle = new Bundle()
                bundle.putParcelable(GlobalStaticVariables.MOVIE_PERSON_URI,uri)
                final PersonMovieFragment personMovieFragment = new PersonMovieFragment()
                personMovieFragment.setArguments(bundle)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.person_fragment_container, personMovieFragment)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data.', LogDisplay.PERSON_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person_fragment_menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.person_fragment_menu) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}