package com.moviemagic.dpaul.android.app.utility

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.GridFragment
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapter
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper;
import groovy.transform.CompileStatic

@CompileStatic
class LoadMoreData extends AsyncTask<String, Void, Void>{
    private static final String LOG_TAG = LoadMoreData.class.getSimpleName()
    ContentResolver mContentResolver
    Context mContext
    int mCurrentPage

    public LoadMoreData (Context ctx, int currPage) {
        mContext = ctx
        mCurrentPage = currPage
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(String... params) {
        String movieCategory = params[0]
        int totalPage
        List<ContentValues> movieList
        //TBDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1
        final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
        final String MOVIE_PATH = 'movie'
        final String API_KEY = 'api_key'
        final String PAGE = 'page'
        try {
            Uri.Builder uriBuilder = Uri.parse(TMDB_MOVIE_BASE_URL).buildUpon()

            Uri uri = uriBuilder.appendPath(MOVIE_PATH)
                    .appendPath(movieCategory)
                    .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(PAGE,Integer.toString(mCurrentPage))
                    .build()

            URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url-> ${uri.toString()}",LogDisplay.LOAD_MORE_DATA_LOG_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
            totalPage = JsonParse.getTotalPages(jsonData)
            //This is to ensure we have valid data page
            if (mCurrentPage <= totalPage) {
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieCategory -> $jsonData",LogDisplay.LOAD_MORE_DATA_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(jsonData, movieCategory,MovieMagicSyncAdapter.MOVIE_LIST_TYPE_PUBLIC)
                ContentValues[] cv = movieList as ContentValues []
                int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,cv)
                LogDisplay.callLog(LOG_TAG,"Total insert for $movieCategory->$insertCount",LogDisplay.LOAD_MORE_DATA_LOG_FLAG)
            }
        } catch (URISyntaxException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, e.message, e)
        } catch (JsonException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, e.message, e)
        } catch (IOException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "Error:", e)
        } finally {
//            //Occassionally the api call may fail, So all we are doing here
//            //is reset the flag, so that it can enter to the below if condition and try again
//            GridFragment.isMoreDataToLoad = false
        }
        return null
    }
}