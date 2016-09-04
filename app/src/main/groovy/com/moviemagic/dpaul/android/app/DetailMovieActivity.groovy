package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageSwitcher
import android.widget.ImageView
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailMovieActivity.class.getSimpleName()

//    private CollapsingToolbarLayout mCollapsingToolbar
//    private Toolbar mToolbar
//    private AppBarLayout mAppBarLayout
//    private ImageView mBackdropImage1, mBackdropImage2
//    private ImageSwitcher mBackdropImageSwitcher
//    private static boolean picasoLoadComplete = true
//    private static boolean setBackdropAnimation = true

//    private final android.os.Handler mHandler = new android.os.Handler()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)
//        mToolbar = (Toolbar) findViewById(R.id.movie_detail_toolbar)
//        setSupportActionBar(mToolbar)
//        //Enable back to home button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
//
//        mCollapsingToolbar = findViewById(R.id.movie_detail_collapsing_toolbar) as CollapsingToolbarLayout
//        //Just clear off to be on the safe side
//        mCollapsingToolbar.setTitle(" ")
//        mAppBarLayout = findViewById(R.id.movie_detail_app_bar_layout) as AppBarLayout
//        mBackdropImage1 = findViewById(R.id.movie_detail_backdrop_image_1) as ImageView
//        mBackdropImage2 = findViewById(R.id.movie_detail_backdrop_image_2) as ImageView
//        mBackdropImageSwitcher = findViewById(R.id.movie_detail_backdrop_image_switcher) as ImageSwitcher

        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Bundle extras = getIntent().getExtras()
            if (extras) {
                //Create a Bundle to pass the data to Fragment
//                Bundle args = new Bundle()
//                args.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,extras.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID))
//                args.putLong(GlobalStaticVariables.MOVIE_BASIC_INFO_ROW_ID,extras.getLong(GlobalStaticVariables.MOVIE_BASIC_INFO_ROW_ID))
                //Create a movie detail fragment
                final DetailMovieFragment movieDetailFragment = new DetailMovieFragment()
                movieDetailFragment.setArguments(extras)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container,movieDetailFragment)
                        // Add this transaction to the back stack
//                        .addToBackStack(null)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_activity_menu) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //Override the callback methods of DetailMovieFragment
//    @Override
//    public void initializeActivityHostedTitleAndColor(String movieTitle, int primaryColor, int primaryDarkColor, int titleColor) {
//        LogDisplay.callLog(LOG_TAG, 'initializeActivityHostedTitleAndColor is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
////        mCollapsingToolbar.setTitle(movieTitle)
//        mCollapsingToolbar.setStatusBarScrimColor(primaryDarkColor)
//        mCollapsingToolbar.setContentScrimColor(primaryColor)
//        mCollapsingToolbar.setBackgroundColor(primaryColor)
//        mCollapsingToolbar.setCollapsedTitleTextColor(titleColor)
//
//        //Show the title only when image is collapsed
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false
//            int scrollRange = -1
//
//            @Override
//            void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange()
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    mCollapsingToolbar.setTitle(movieTitle)
//                    isShow = true
//                } else if (isShow) {
//                    mCollapsingToolbar.setTitle(" ")
//                    isShow = false
//                }
//            }
//        })
//    }

//    @Override
//    public void initializeActivityHostedBackdrop(List<String> backdropImagePathList) {
//        LogDisplay.callLog(LOG_TAG, 'initializeActivityHostedBackdrop is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//        setBackdropAnimation = true
//        final Animation fadeInAnimation = new AlphaAnimation(0, 1)
//        fadeInAnimation.setInterpolator(new DecelerateInterpolator())
//        fadeInAnimation.setDuration(3000)
//        int counter = 0
//        final Runnable runnable = new Runnable() {
//            @Override
//            void run() {
////                LogDisplay.callLog(LOG_TAG,"initializeActivityHostedBackdrop:picasoLoadComplete:$picasoLoadComplete",LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//                if (picasoLoadComplete) {
//                    final String backdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
//                            "${backdropImagePathList[counter]}"
//                    final int evenOrOdd = counter % 2
////                    LogDisplay.callLog(LOG_TAG, "evenOrOdd value:$evenOrOdd", LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//                    final ImageView imageView
//                    //The order is important, do not alter - otherwise imageswitcher will not work properly!
//                    if (evenOrOdd == 0) {
//                        imageView = mBackdropImageSwitcher.getChildAt(1) as ImageView
//                    } else {
//                        imageView = mBackdropImageSwitcher.getChildAt(0) as ImageView
//                    }
//                    loadBackdropImage(backdropPath, fadeInAnimation, imageView, counter)
//                    counter++
//                    if (counter >= backdropImagePathList.size()) {
//                        //Do not cycle through if the backdrop image count is 1
//                        if (backdropImagePathList.size() > 1) {
//                            counter = 0
//                        }
//                    }
//                }
//                mHandler.postDelayed(this, 10000) //Interval time is 8 seconds
//            }
//        }
//        mHandler.postDelayed(runnable, 100) //Initial delay is 1 seconds
//    }

//    void loadBackdropImage(String backdropPath, Animation animation, ImageView imageView, int counter) {
//        LogDisplay.callLog(LOG_TAG, 'loadBackdropImage is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//        //This is to avoid abrupt flipping of first 2 / 3 images - it happens because of Picasso background call timing
//        //So for first three images not slide animation is used and it ensures no cluttering
//        if (setBackdropAnimation && counter > 3) {
//            mBackdropImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_in_animation))
//            mBackdropImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left_out_animation))
//            setBackdropAnimation = false
//        }
//
//        Picasso.with(this)
//                .load(backdropPath)
//                .noPlaceholder()
//                .centerInside()
//                .fit()
//                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                .into(imageView, new Callback() {
//            @Override
//            void onSuccess() {
//                LogDisplay.callLog(LOG_TAG, 'Picasso:onSuccess is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//                mBackdropImageSwitcher.startAnimation(animation)
//                mBackdropImageSwitcher.showNext()
//            }
//
//            @Override
//            void onError() {
//                //TODO: need to identify if anything is needed
//                LogDisplay.callLog(LOG_TAG, 'Picasso:onError is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//            }
//        })
//    }

    @Override
    protected void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onStop()
//        mHandler.removeCallbacksAndMessages(null)
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, "onBackPressed is called.FragmentBackstackCount:${getSupportFragmentManager().getBackStackEntryCount()}", LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
//        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack()
//        } else {
            super.onBackPressed()
//        }
        overridePendingTransition(0, R.anim.slide_bottom_down_animation)
    }
}