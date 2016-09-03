package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.GridFragment
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class LoadMoreMovieData extends AsyncTask<String, Void, Void>{
    private static final String LOG_TAG = LoadMoreMovieData.class.getSimpleName()
    ContentResolver mContentResolver
    Context mContext
    int mCurrentPage

    public LoadMoreMovieData(Context ctx, int currPage) {
        mContext = ctx
        mCurrentPage = currPage
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(String... params) {
        String movieCategory = params[0]
        int totalPage
        List<ContentValues> movieList
        //TMDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1

        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(movieCategory)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,Integer.toString(mCurrentPage))
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url-> ${uri.toString()}",LogDisplay.LOAD_MORE_DATA_LOG_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
            totalPage = JsonParse.getTotalPages(jsonData)
            //This is to ensure we have valid data page
            if (mCurrentPage <= totalPage) {
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieCategory -> $jsonData",LogDisplay.LOAD_MORE_DATA_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(jsonData, movieCategory,GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC)
                final ContentValues[] cv = movieList as ContentValues []
                final int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,cv)
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
        }
        return null
    }
}