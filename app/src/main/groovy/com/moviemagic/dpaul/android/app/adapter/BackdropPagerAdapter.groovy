//package com.moviemagic.dpaul.android.app.adapter
//
//import android.database.Cursor
//import android.os.Bundle
//import android.support.v4.app.FragmentManager
//import android.support.v4.app.Fragment
//import android.support.v4.app.FragmentStatePagerAdapter
//import android.view.View
//import com.moviemagic.dpaul.android.app.DetailMovieActivity
//import com.moviemagic.dpaul.android.app.DetailMovieFragment
//import com.moviemagic.dpaul.android.app.utility.BackdropImageFragment;
//import groovy.transform.CompileStatic
//
//@CompileStatic
//class BackdropPagerAdapter extends FragmentStatePagerAdapter {
//    private Cursor mCursor
//
//    public BackdropPagerAdapter(FragmentManager fm) {
//        super(fm)
//    }
//    @Override
//    Fragment getItem(int position) {
//        if(mCursor.moveToFirst()) {
//            BackdropImageFragment fragment = new BackdropImageFragment()
//            Bundle args = new Bundle()
//            mCursor.moveToPosition(position)
//            String backdropPath = "http://image.tmdb.org/t/p/w500${mCursor.getString(DetailMovieFragment.COL_MOVIE_IMAGE_FILE_PATH)}"
//            args.putString(BackdropImageFragment.BACKDROP_KEY, backdropPath)
//            fragment.setArguments(args)
//            return fragment
//        } else {
//            return null
//        }
//    }
//
//    @Override
//    int getCount() {
//        if ( null == mCursor ) return 0
//        return mCursor.getCount()
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return false
//    }
//
//    public void swapCursor(Cursor newCursor) {
//        mCursor = newCursor
//        notifyDataSetChanged()
//    }
//}