package com.moviemagic.dpaul.android.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeInitializationResult
import com.moviemagic.dpaul.android.app.adapter.HomeMovieAdapter
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapterUtility
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
public class MovieMagicMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GridMovieFragment.Callback, GridMovieFragment.CollectionColorChangeCallback, HomeMovieFragment.CallbackForHomeMovieClick {
    private static final String LOG_TAG = MovieMagicMainActivity.class.getSimpleName()
    private static final String STATE_APP_TITLE = 'app_title'
    private NavigationView navigationView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        setContentView(R.layout.activity_movie_magic_main)
        MovieMagicSyncAdapterUtility.initializeSyncAdapter(this)
        //*** Comment before release **********************
        //MovieMagicSyncAdapterUtility.syncImmediately(this)

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar)
        setSupportActionBar(toolbar)

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        })

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout)
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        navigationView = (NavigationView) findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
//        setMenuCounter(R.id.nav_home,40)
        //Update the user list menu counter
        final UpdateMenuCounter updateMenuCounter = new UpdateMenuCounter(this)
        //Execute the asynctask
        //program fails if 'Void' is used for parameter, could be because of groovy compiler??
        //So to get rid of the problem a 'dummy' value is passed
        //TODO: Need to fix this later
        updateMenuCounter.execute(['dummy'] as String[])
        //Check to ensure Youtube exists on the device
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this)
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(this, 0).show()
        }

        //Load the Home Fragment
        loadHomeFragment()
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogDisplay.callLog(LOG_TAG,'onSaveInstanceState is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        outState.putString(STATE_APP_TITLE,getSupportActionBar().getTitle().toString())
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onRestoreInstanceState is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Always call the superclass first, so that Bundle is retrieved properly
        super.onRestoreInstanceState(savedInstanceState)
        getSupportActionBar().setTitle(savedInstanceState.getCharSequence(STATE_APP_TITLE,'error'))
    }

    @Override
    protected void onStart() {
        super.onStart()
        LogDisplay.callLog(LOG_TAG,'onStart is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onPause() {
        super.onPause()
        LogDisplay.callLog(LOG_TAG,'onPause is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onResume() {
        super.onResume()
        LogDisplay.callLog(LOG_TAG,'onResume is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onRestart() {
        super.onRestart()
        LogDisplay.callLog(LOG_TAG,'onRestart is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onStop() {
        super.onStop()
        LogDisplay.callLog(LOG_TAG,'onStop is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy()
        LogDisplay.callLog(LOG_TAG,'onDestroy is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId()

        if (id == R.id.nav_home) {
            showSnackBar(getString(R.string.drawer_menu_home))
            setItemTitle(getString(R.string.drawer_menu_home))
            loadHomeFragment()
        } else if (id == R.id.nav_tmdb_popular) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_popular))
            setItemTitle(getString(R.string.drawer_menu_tmdb_popular))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
        } else if (id == R.id.nav_tmdb_toprated) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_toprated))
            setItemTitle(getString(R.string.drawer_menu_tmdb_toprated))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
        } else if (id == R.id.nav_tmdb_nowplaying) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_nowplaying))
            setItemTitle(getString(R.string.drawer_menu_tmdb_nowplaying))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
        } else if (id == R.id.nav_tmdb_upcoming) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_upcoming))
            setItemTitle(getString(R.string.drawer_menu_tmdb_upcoming))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
        } else if (id == R.id.nav_user_watched) {
            showSnackBar(getString(R.string.drawer_menu_user_watched))
            setItemTitle(getString(R.string.drawer_menu_user_watched))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED)
        } else if (id == R.id.nav_user_wishlist) {
            showSnackBar(getString(R.string.drawer_menu_user_wishlist))
            setItemTitle(getString(R.string.drawer_menu_user_wishlist))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST)
        } else if (id == R.id.nav_user_favourite) {
            showSnackBar(getString(R.string.drawer_menu_user_favourite))
            setItemTitle(getString(R.string.drawer_menu_user_favourite))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE)
        } else if (id == R.id.nav_user_collection) {
            showSnackBar(getString(R.string.drawer_menu_user_collection))
            setItemTitle(getString(R.string.drawer_menu_user_collection))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION)
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Load the Home Fragment
     */
    private void loadHomeFragment() {
        final HomeMovieFragment homeMovieFragment = new HomeMovieFragment()
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
        //Set the custom animation
        fragmentTransaction.replace(R.id.content_movie_magic_main_layout, homeMovieFragment)
        fragmentTransaction.commit()
    }

    /**
     * Load the Grid Fragment of the movie selected movie category
     * @param category Movie category
     */
    private void loadGridFragment(String category) {
//        final GridMovieFragment fragment = new GridMovieFragment(category, 0)
//        final FragmentManager fragmentManager = getSupportFragmentManager()
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
//        //Set the custom animation
//        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_in_animation,0)
//        fragmentTransaction.replace(R.id.main_content_layout, fragment)
//        fragmentTransaction.commit()
        //Set this flag as false so that theme primaryDark color is used in the grid
        MovieGridRecyclerAdapter.collectionGridFlag = false
        final Bundle bundle = new Bundle()
        //Since it's not for collection category, so collection id is passed as zero
        final Uri uri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(category,0)
//        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,category)
        //Collection id is not required here, so passed on as zero
//        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_COLL_ID,0)
        bundle.putParcelable(GlobalStaticVariables.MOVIE_CATEGORY_AND_COLL_ID_URI,uri)
        final GridMovieFragment gridMovieFragment = new GridMovieFragment()
        gridMovieFragment.setArguments(bundle)
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
        //Set the custom animation
        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_in_animation,0)
        fragmentTransaction.replace(R.id.content_movie_magic_main_layout, gridMovieFragment)
        fragmentTransaction.commit()
    }

//    private void setMenuCounter(@IdRes int itemId, int count) {
//        final TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView()
//        view.setText(count > 0 ? String.valueOf(count) : null)
//    }

    private void setItemTitle(CharSequence title){
        LogDisplay.callLog(LOG_TAG,"The drawer menu $title is called",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        getSupportActionBar().setTitle(title)
    }

    private showSnackBar(String menuItem) {
        Snackbar.make(findViewById(R.id.content_movie_magic_main_layout), menuItem + " is clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    /**
     * Fragment callback method for HomeMovie Item - called when a movie item is clicked
     * @param movieId  Movie id of the selected movie
     * @param viewHolder HomeMovieApterViewHolder
     */
    @Override
    void onHomeMovieItemSelected(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder) {
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,movieCategory)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }
/**
     * Updating of menu counter is tightly coupled with main_activity_menu activity, so no separate class is
     * created for the AsyncTask
     */

    public class UpdateMenuCounter extends AsyncTask<String, Void, Integer[]> {
        private final Context mContext

        public UpdateMenuCounter(Context ctx) {
            mContext = ctx
        }
        @Override
        protected Integer[] doInBackground(String... params) {
            final Integer[] result = new Integer[4]
            final cursor
            //Get the count for watched
            cursor = mContext.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED] as String[],
                    null)
            if(cursor.moveToFirst()) result[0] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for wish list
            cursor = mContext.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST] as String[],
                    null)
            if(cursor.moveToFirst()) result[1] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for favourite
            cursor = mContext.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE] as String[],
                    null)
            if(cursor.moveToFirst()) result[2] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for wish list
            cursor = mContext.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION] as String[],
                    null)
            if(cursor.moveToFirst()) result[3] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()

            return result
        }

        @Override
        protected void onPostExecute(Integer[] result) {
            //Set the Watched counter
            final TextView watchedView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_watched).getActionView()
            watchedView.setText(result[0] > 0 ? String.valueOf(result[0]) : null)
            //Set the wish list counter
            final TextView wishListView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_wishlist).getActionView()
            wishListView.setText(result[1] > 0 ? String.valueOf(result[1]) : null)
            //Set the favourite counter
            final TextView favouriteView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_favourite).getActionView()
            favouriteView.setText(result[2] > 0 ? String.valueOf(result[2]) : null)
            //Set the collection counter
            final TextView collectionView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_collection).getActionView()
            collectionView.setText(result[3] > 0 ? String.valueOf(result[3]) : null)
        }
    }

    //Override the callback method of GridMovieFragment
    @Override
//    public void onItemSelected(int movieId, long movie_magic_row_ID, ImageView gridImageView) {
//    public void onItemSelected(int movieId, ImageView gridImageView) {
    public void onMovieGridItemSelected(int movieId, String movieCategory, MovieGridRecyclerAdapter.MovieGridRecyclerAdapterViewHolder viewHolder) {
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        final Bundle bundle = new Bundle()
//        final Uri movieMagicMovieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(movieId)
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,movieCategory)
//        intent.setData(movieMagicMovieIdUri)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    @Override
    public void notifyCollectionColorChange() {
        //Do nothing. This is not called for main activity
        //Implemented, otherwise application will give error as GridFragment onAttach has check if it is implemented
    }
}
