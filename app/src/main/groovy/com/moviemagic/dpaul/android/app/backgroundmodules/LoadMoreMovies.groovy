package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.GridMovieFragment
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class LoadMoreMovies extends AsyncTask<String, Void, Void>{
    private static final String LOG_TAG = LoadMoreMovies.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
    private final int mCurrentPage

    public LoadMoreMovies(Context ctx, int currPage) {
        mContext = ctx
        mCurrentPage = currPage
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(String... params) {
        final String movieCategory = params[0]
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
            LogDisplay.callLog(LOG_TAG,"Movie url-> ${uri.toString()}",LogDisplay.LOAD_MORE_MOVIES_LOG_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
            totalPage = JsonParse.getTotalPages(jsonData)
            //This is to ensure we have valid data page
            if (mCurrentPage <= totalPage) {
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieCategory -> $jsonData",LogDisplay.LOAD_MORE_MOVIES_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(mContext, jsonData, movieCategory,GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC,Utility.getTodayDate())
                if(movieList) {
                    final ContentValues[] cv = movieList as ContentValues[]
                    final int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, cv)
                    LogDisplay.callLog(LOG_TAG, "Total insert for $movieCategory->$insertCount", LogDisplay.LOAD_MORE_MOVIES_LOG_FLAG)
                    if (insertCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info successful. Total insert for $movieCategory->$insertCount", LogDisplay.LOAD_MORE_MOVIES_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info fialed. Insert count for $movieCategory->$insertCount", LogDisplay.LOAD_MORE_MOVIES_LOG_FLAG)
                    }
                }
            }
        } catch (URISyntaxException e) {
            //Set the boolean to true to indicate API call failed
//            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "URISyntaxException: ${e.message}", e)
        } catch (JsonException e) {
            //Set the boolean to true to indicate API call failed
//            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (JsonException e) {
            //Set the boolean to true to indicate API call failed
//            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        }
        return null
    }
}