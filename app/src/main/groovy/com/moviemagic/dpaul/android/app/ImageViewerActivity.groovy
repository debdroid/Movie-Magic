package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.ImagePagerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class ImageViewerActivity extends AppCompatActivity {
    private static final String LOG_TAG = ImageViewerActivity.class.getSimpleName()
    private ArrayList<String> mImageFilePath
    private String mTitle
    private int mAdapterPostion
    private boolean mBackdropImageFlag
    private RelativeLayout mImageViewerMainLayout
    private View decorView
//    private Toolbar mToolbar

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer)
        ViewPager viewPager = (ViewPager) findViewById(R.id.image_viewer_pager)
        mImageViewerMainLayout = findViewById(R.id.image_viewer_main_layout) as RelativeLayout

        //Allow layout to appear under status bar
//        decorView = getWindow().getDecorView()
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE)

        if(savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras()
            if(extras) {
                mTitle = extras.getString(GlobalStaticVariables.IMAGE_VIEWER_TITLE)
                mAdapterPostion = extras.getInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION,0)
                mImageFilePath = extras.getStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY)
                mBackdropImageFlag = extras.getBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG,false)
            }
        } else { //Retrieve it from onSaveInstanceState
            mTitle = savedInstanceState.getString(GlobalStaticVariables.IMAGE_VIEWER_TITLE)
            mAdapterPostion = savedInstanceState.getInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION,0)
            mImageFilePath = savedInstanceState.getStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY)
            mBackdropImageFlag = savedInstanceState.getBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG,false)
        }
        final Toolbar myToolbar = findViewById(R.id.image_viewer_toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setTitle(mTitle)

//        final AppBarLayout appBarLayout = findViewById(R.id.image_viewer_app_bar_layout) as AppBarLayout

        //Create and set PagerAdapter
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, mTitle, mImageFilePath as String[],
                new ImagePagerAdapter.ImagePagerAdapterOnClickHandler(){
                    @Override
                    void onClick(int position) {
                        LogDisplay.callLog(LOG_TAG, "ImagePagerAdapter clicked.Position->$position", LogDisplay.IMAGE_VIEWER_ACTIVITY_LOG_FLAG)
                        if(getSupportActionBar() && getSupportActionBar().isShowing()) {
//                            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN)
                            final Animation animOut = new TranslateAnimation(0,0,0,-100)
                            animOut.setDuration(100)
                            animOut.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                void onAnimationStart(Animation animation) {}
                                @Override
                                void onAnimationEnd(Animation animation) {
                                    getSupportActionBar().hide()
                                }
                                @Override
                                void onAnimationRepeat(Animation animation) {}
                            })
                            myToolbar.startAnimation(animOut)

                        } else {
//                            decorViews.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                            getSupportActionBar().show()
                            final Animation animIn = new TranslateAnimation(0,0,-100,0)
                            animIn.setDuration(80)
                            myToolbar.startAnimation(animIn)
                        }
                    }
                }, mBackdropImageFlag)

        viewPager.setAdapter(adapter)
        //Position it at correct place (the image which is clicked)
        viewPager.setCurrentItem(mAdapterPostion)
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY,mImageFilePath)
        outState.putString(GlobalStaticVariables.IMAGE_VIEWER_TITLE,mTitle)
        outState.putBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG,mBackdropImageFlag)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    protected void onResume() {

        super.onResume()
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        //Start the exit animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}