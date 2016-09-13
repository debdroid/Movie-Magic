package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract;
import groovy.transform.CompileStatic

@CompileStatic
class CollectionMovieActivity extends AppCompatActivity implements GridMovieFragment.Callback, GridMovieFragment.CollectionColorChangeCallback {

    private static final String LOG_TAG = CollectionMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_movie)
        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Uri uri = getIntent().getData()
            if (uri) {
                LogDisplay.callLog(LOG_TAG, "Intent Data->${uri.toString()}", LogDisplay.COLLECTION_MOVIE_ACTIVITY_LOG_FLAG)
                final Bundle bundle = new Bundle()
                bundle.putParcelable(GlobalStaticVariables.MOVIE_COLLECTION_URI,uri)
                final CollectionMovieFragment collectionMovieFragment = new CollectionMovieFragment()
                collectionMovieFragment.setArguments(bundle)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.collection_fragment_container, collectionMovieFragment, GlobalStaticVariables.COLLECTION_MOVIE_FRAGMENT_TAG)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data.', LogDisplay.COLLECTION_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.collection_activity_menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.collection_activity_menu) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, 'onBackPressed is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }

    //Override the GridMovieFragment Callback
    @Override
    public void onItemSelected(int movieId, MovieGridRecyclerAdapter.MovieGridRecyclerAdapterViewHolder viewHolder) {
        LogDisplay.callLog(LOG_TAG, 'onItemSelected is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        final Uri movieMagicMovieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(movieId)
        intent.setData(movieMagicMovieIdUri)
        startActivity(intent)
        //Start the animation
//        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    @Override
    public void notifyCollectionColorChange() {
        LogDisplay.callLog(LOG_TAG, 'notifyCollectionColorChange is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        final CollectionMovieFragment fragment = (CollectionMovieFragment)getSupportFragmentManager().findFragmentByTag(GlobalStaticVariables.COLLECTION_MOVIE_FRAGMENT_TAG)
        if(CollectionMovieFragment) {
            fragment.loadCollBackdropAndchangeCollectionMovieGridColor()
        } else {
            LogDisplay.callLog(LOG_TAG, 'notifyCollectionColorChange: CollectionMovieFragment reference is null', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

}