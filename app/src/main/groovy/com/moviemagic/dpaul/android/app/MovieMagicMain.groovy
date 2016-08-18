package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
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
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapterUtility
import com.moviemagic.dpaul.android.app.utility.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
public class MovieMagicMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GridFragment.Callback {
    private static final String LOG_TAG = MovieMagicMain.class.getSimpleName()
    private static final String STATE_APP_TITLE = 'app_title'
    NavigationView navigationView

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
        setMenuCounter(R.id.nav_home,4)

        //Check to ensure Youtube exists on the device
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this)
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(this, 0).show()
        }
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
        getMenuInflater().inflate(R.menu.main, menu)
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
        } else if (id == R.id.nav_tmdb_popular) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_popular))
            setItemTitle(getString(R.string.drawer_menu_tmdb_popular))
            startFragment(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
        } else if (id == R.id.nav_tmdb_toprated) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_toprated))
            setItemTitle(getString(R.string.drawer_menu_tmdb_toprated))
            startFragment(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
        } else if (id == R.id.nav_tmdb_nowplaying) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_nowplaying))
            setItemTitle(getString(R.string.drawer_menu_tmdb_nowplaying))
            startFragment(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
        } else if (id == R.id.nav_tmdb_upcoming) {
            showSnackBar(getString(R.string.drawer_menu_tmdb_upcoming))
            setItemTitle(getString(R.string.drawer_menu_tmdb_upcoming))
            startFragment(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
        } else if (id == R.id.nav_user_favourite) {
            showSnackBar(getString(R.string.drawer_menu_user_favourite))
            setItemTitle(getString(R.string.drawer_menu_user_favourite))
        } else if (id == R.id.nav_user_watched) {
            showSnackBar(getString(R.string.drawer_menu_user_watched))
            setItemTitle(getString(R.string.drawer_menu_user_watched))
        } else if (id == R.id.nav_user_wishlist) {
            showSnackBar(getString(R.string.drawer_menu_user_wishlist))
            setItemTitle(getString(R.string.drawer_menu_user_wishlist))
        } else if (id == R.id.nav_user_collection) {
            showSnackBar(getString(R.string.drawer_menu_user_collection))
            setItemTitle(getString(R.string.drawer_menu_user_collection))
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private void startFragment (String category) {
        final GridFragment fragment = new GridFragment(category)
        final FragmentManager fragmentManager = getSupportFragmentManager()
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_content_layout, fragment)
        fragmentTransaction.commit()
    }

    private void setMenuCounter(@IdRes int itemId, int count) {
        final TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView()
        view.setText(count > 0 ? String.valueOf(count) : null)
    }

    private void setItemTitle(CharSequence title){
        LogDisplay.callLog(LOG_TAG,"The drawer menu $title is called",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        getSupportActionBar().setTitle(title)
    }

    private showSnackBar(String menuItem) {
        Snackbar.make(findViewById(R.id.main_content_layout), menuItem + " is clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    //Override the callback method of GridFragment
    @Override
    public void onItemSelected(Uri movieIdUri) {
        final Intent mIntent = new Intent(this, DetailMovieActivity.class).setData(movieIdUri)
        startActivity(mIntent)
    }
}
