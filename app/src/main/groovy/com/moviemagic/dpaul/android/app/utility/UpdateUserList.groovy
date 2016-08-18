package com.moviemagic.dpaul.android.app.utility

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.widget.LinearLayout
import android.widget.TextView
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.transform.CompileStatic

@CompileStatic
class UpdateUserList extends AsyncTask<String, Void, Integer> {
    private static final String LOG_TAG = UpdateUserList.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
    private final LinearLayout mUserDrawableLayout
    private final int mMovieBasicInfo_ID, mBackgroundColor, mBodyTextColor
    private final String mMovieTitle
    private String mUserListMsg
    private int mUserFlag
    private final ProgressDialog mProgressDialog


    public UpdateUserList(Context ctx, LinearLayout userDrawableLayout, int _ID_movieBasicInfo, String movieTitle,
                          int backgroundColor, int bodyTextColor) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
        mUserDrawableLayout = userDrawableLayout
        mMovieBasicInfo_ID = _ID_movieBasicInfo
        mMovieTitle = movieTitle
        mBodyTextColor = backgroundColor
        mBodyTextColor = bodyTextColor
        mProgressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER)
    }

    @Override
    protected Integer doInBackground(String... params) {
        final String listType = params[0]
        final String operationType = params[1]
        final ContentValues contentValues = new ContentValues()
        if(operationType == GlobalStaticVariables.USER_LIST_FLAG_ADD) {
            mUserFlag = GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE
        } else {
            mUserFlag = GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE
        }
        switch (listType) {
            case GlobalStaticVariables.USER_LIST_WATCHED:
//                contentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_USER_WATCHED,mUserFlag)
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_watched)
                break
            case GlobalStaticVariables.USER_LIST_WISH_LIST:
//                contentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_USER_WISH_LIST,mUserFlag)
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_wishlist)
                break
            case GlobalStaticVariables.USER_LIST_FAVOURITE:
//                contentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_USER_FAVOURITE,mUserFlag)
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_favourite)
                break
            case GlobalStaticVariables.USER_LIST_COLLECTION:
//                contentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_USER_COLLECTION,mUserFlag)
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_collection)
                break
            default:
                LogDisplay.callLog(LOG_TAG,"Unknown user list type->$listType",LogDisplay.UPDATE_USER_LIST_FLAG)
        }
        final String[] movieIdArg = [Integer.toString(mMovieBasicInfo_ID)]
        final int updatedRow = mContentResolver.update(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                contentValues,
                MovieMagicContract.MovieBasicInfo._ID + "= ?",
                movieIdArg)

        return updatedRow
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute()
        mProgressDialog.show()
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(mProgressDialog) {
            mProgressDialog.dismiss()
        }
        final String snackBarMsg
        if(mUserFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
            snackBarMsg = String.format(mContext.getString(R.string.user_list_add_message),mMovieTitle,mUserListMsg)
        } else {
            snackBarMsg = String.format(mContext.getString(R.string.user_list_del_message),mMovieTitle,mUserListMsg)
        }
        //Expecting a single row update only
        if(result == 1) {
            Snackbar.make(mUserDrawableLayout.findViewById(R.id.movie_detail_user_list_drawable_layout),
                    snackBarMsg, Snackbar.LENGTH_LONG).show()
//            final Snackbar snackbar
//            snackbar = Snackbar.make(mUserDrawableLayout.findViewById(R.id.movie_detail_user_list_drawable_layout),
//                    snackBarMsg, Snackbar.LENGTH_LONG)
//            final snackbarView = snackbar.getView()
////            snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.accent))
//            snackbarView.setBackgroundColor(mBackgroundColor)
//            snackbarView.setAlpha(0.6f)
//            final TextView snackbarTextView = snackbarView.findViewById(android.support.design.R.id.snackbar_text) as TextView
//            snackbarTextView.setTextColor(mBodyTextColor)
//            snackbar.show()
        } else {
            LogDisplay.callLog(LOG_TAG,"Update failed in movie_basic_info table.Row Count->$result",LogDisplay.UPDATE_USER_LIST_FLAG)
        }
    }
}