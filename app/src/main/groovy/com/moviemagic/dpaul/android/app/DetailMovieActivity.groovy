package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieActivity extends AppCompatActivity implements DetailMovieFragment.CallbackForBackdropImageClick {
    private static final String LOG_TAG = DetailMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)

        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Bundle extras = getIntent().getExtras()
            if (extras) {
                final DetailMovieFragment detailMovieFragment = new DetailMovieFragment()
                detailMovieFragment.setArguments(extras)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_fragment_container,detailMovieFragment)
                        // No need to add the transaction to backstack as this is first transaction and activity will hold it
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    protected void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onStop()
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, "onBackPressed is called.FragmentBackstackCount:${getSupportFragmentManager().getBackStackEntryCount()}", LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }

    @Override
    void onBackdropImageClicked(String title, int position, ArrayList<String> backdropImageFilePath) {
        final Bundle bundle = new Bundle()
        bundle.putString(GlobalStaticVariables.IMAGE_VIEWER_TITLE,title)
        bundle.putInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION, position)
        bundle.putStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY,backdropImageFilePath)
        bundle.putBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG, true)
        final Intent intent = new Intent(this, ImageViewerActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }
}