package com.moviemagic.dpaul.android.app.utility

import android.content.ContentResolver
import android.content.Context
import android.os.AsyncTask
import android.widget.LinearLayout
import groovy.transform.CompileStatic

@CompileStatic
class UpdateUserList extends AsyncTask<String, Void, Integer> {
    private static final String LOG_TAG = UpdateUserList.class.getSimpleName()
    private ContentResolver mContentResolver
    private Context mContext
    private LinearLayout mUserDrawableLayout

    public UpdateUserList(Context ctx, LinearLayout userDrawableLayout) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
        mUserDrawableLayout = userDrawableLayout
    }

    @Override
    protected Integer doInBackground(String... params) {
        String
        return null
    }
}