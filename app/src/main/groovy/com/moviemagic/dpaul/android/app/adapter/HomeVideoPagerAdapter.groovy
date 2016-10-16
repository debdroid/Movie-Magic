package com.moviemagic.dpaul.android.app.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment;
import groovy.transform.CompileStatic

@CompileStatic
class HomeVideoPagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = HomeVideoPagerAdapter.class.getSimpleName()
    private final int mItemCount
    private final List<String> mYouTubeVideoKey
    private MovieMagicYoutubeFragment mMovieMagicYoutubeFragment

    public HomeVideoPagerAdapter(FragmentManager fm, int itemCount, List<String> youtubeVideoKey) {
        super(fm)
        LogDisplay.callLog(LOG_TAG,'HomeVideoPagerAdapter constructor is called',LogDisplay.HOME_VIDEO_PAGER_ADAPTER_LOG_FLAG)
        mItemCount = itemCount
        mYouTubeVideoKey = youtubeVideoKey
    }
    @Override
    Fragment getItem(int position) {
        final List<String> videoKey = new ArrayList<>()
        videoKey.add(mYouTubeVideoKey.get(position))
        return MovieMagicYoutubeFragment.createMovieMagicYouTubeFragment(videoKey)
//        mMovieMagicYoutubeFragment = new MovieMagicYoutubeFragment()
//        return mMovieMagicYoutubeFragment
    }

    @Override
    int getCount() {
        mItemCount
    }
}