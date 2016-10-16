package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment;
import groovy.transform.CompileStatic

@CompileStatic
class HomeVideoDummyAdapter extends PagerAdapter {
    private static final String LOG_TAG = HomeVideoPagerAdapter.class.getSimpleName()
    private final Context mContext
    private final FragmentManager mFragmentManager
    private final int mItemCount
    private final List<String> mYouTubeVideoKey
    private LayoutInflater mLayoutInflater

    public HomeVideoDummyAdapter() {
        LogDisplay.callLog(LOG_TAG,'HomeVideoDummyAdapter empty constructor is called',LogDisplay.HOME_VIDEO_PAGER_ADAPTER_LOG_FLAG)
    }

    public HomeVideoDummyAdapter(Context context, FragmentManager fm, int itemCount, List<String> youtubeVideoKey) {
        LogDisplay.callLog(LOG_TAG,'HomeVideoDummyAdapter constructor is called',LogDisplay.HOME_VIDEO_PAGER_ADAPTER_LOG_FLAG)
        mContext = context
        mFragmentManager = fm
        mItemCount = itemCount
        mYouTubeVideoKey = youtubeVideoKey
    }

    @Override
    int getCount() {
        return mItemCount
    }

    @Override
    boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object)
    }

    @Override
    Object instantiateItem(ViewGroup container, int position) {
        LogDisplay.callLog(LOG_TAG,"instantiateItem is called:$position",LogDisplay.HOME_VIDEO_PAGER_ADAPTER_LOG_FLAG)
        mLayoutInflater = LayoutInflater.from(mContext)
        final View view = mLayoutInflater.inflate(R.layout.single_home_viewpager_item, container, false)
        final List<String> videoKey = new ArrayList<>()
        videoKey.add(mYouTubeVideoKey.get(position))
//        final MovieMagicYoutubeFragment movieMagicYoutubeFragment = MovieMagicYoutubeFragment
//                .createMovieMagicYouTubeFragment(videoKey)
//        mFragmentManager.beginTransaction().replace(R.id.home_youtube_fragment_container, movieMagicYoutubeFragment).commit()

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            void onClick(View v) {
//                LogDisplay.callLog(LOG_TAG,"view pager adapter item is clicked:$position",LogDisplay.HOME_VIDEO_PAGER_ADAPTER_LOG_FLAG)
//            }
//        })

        ((ViewPager) container).addView(view)
        return view
    }

    @Override
    void destroyItem(ViewGroup container, int position, Object object) {
        LogDisplay.callLog(LOG_TAG,'destroyItem is called',LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
        ((ViewPager) container).removeView((RelativeLayout) object)
    }
}