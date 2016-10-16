package com.moviemagic.dpaul.android.app.syncadapter

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SyncResult
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMovieDetails
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.JsonParse
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieMagicSyncAdapter.class.getSimpleName()

    //This variable indicates the number of pages for initial load. It is also
    //used to determine the next page to download during more download
    private final static int MAX_PAGE_DOWNLOAD = 3
    //Define a variable for api page count
    private static int totalPage = 0
    // Define a variable to contain a content resolver instance
    private final ContentResolver mContentResolver
    private final Context mContext

    //Define a flag to control the record insertion / deletion
    private boolean deleteRecords = true

    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1

    MovieMagicSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize)
        mContentResolver = context.getContentResolver()
        mContext = context
    }
    /**
     * -- Needed if the min API level is 11 or above --
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
//    MovieMagicSyncAdapter(
//            Context context,
//            boolean autoInitialize,
//            boolean allowParallelSyncs) {
//        super(context, autoInitialize, allowParallelSyncs)
//        mContentResolver = context.getContentResolver()
//    }

    @Override
    void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        LogDisplay.callLog(LOG_TAG,'onPerformSync is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        List<ContentValues> contentValues = []
        //totalPage is set to 1 so that at least first page is downloaded in downloadMovieList
        // later this variable is overridden by the total page value retrieved from the api
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR, i)
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED, i)
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, i)
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues += downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, i)
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
            contentValues = []
        }
        deleteRecords = true
        //Now load details for home page items
        loadMovieDetailsForHomePageItems()
    }

    private List<ContentValues> downloadMovieList (String category, int page) {
        //TMDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1

        List<ContentValues> movieList

        try {
            Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(category)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,page.toString())
                    .build()

            URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url for $category & page# $page -> ${uri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            //This is intentional so that at lest one page is not loaded in order to make sure
            //at least one (i.e. first) LoadMoreMovies call is always successful
            if (page <= totalPage) {
                def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(jsonData, category, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC)
                totalPage = JsonParse.getTotalPages(jsonData)
            }
            if(deleteRecords) {
                // delete old data except user's records
                //TODO if tmdb user list needs to be kept then modification needed in this delete query
                int deleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE != ?",
                        [GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST] as String []
                )
                LogDisplay.callLog(LOG_TAG,"Total records deleted->$deleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                deleteRecords = false
            }

        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, e.message, e)
        } catch (JsonException e) {
            Log.e(LOG_TAG, e.message, e)
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error:", e)
        }
        return movieList
    }

    private void insertBulkRecords(List<ContentValues> cvList, String category) {
        ContentValues[] cv = cvList as ContentValues []
        if(cv) {
            int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, cv)
            LogDisplay.callLog(LOG_TAG, "Total insert for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            if (insertCount > 0) {
                LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info successful. Total insert for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            } else {
                LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info failed. Insert count for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG,'cv is null. JsonParse.parseMovieListJson returned null',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
    }

    //Load movie details for the movies which are used for Home page (Now playing & upcoming)
    private void loadMovieDetailsForHomePageItems() {
        LogDisplay.callLog(LOG_TAG,'loadMovieDetailsForHomePageItems is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        final movieDataCursor
        final ArrayList<Integer> mMovieIdList = new ArrayList<>()
        final ArrayList<Integer> mMovieRowIdList = new ArrayList<>()
        //First load for now playing
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, '', ''] as String[],
                "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER")

        if(movieDataCursor.moveToFirst()) {
            for (i in 0..(movieDataCursor.getCount() - 1)) {
                mMovieIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_MOVIE_ID))
                mMovieRowIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_ID))
                movieDataCursor.moveToNext()
            }
            //Close the cursor
            movieDataCursor.close()
            LogDisplay.callLog(LOG_TAG, 'Add data for now playing movies', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Now playing.Movie ID list-> $mMovieIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Now playing.Movie row id list-> $mMovieRowIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie-basic_info for now playing', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        //Now load for upcoming
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, '', ''] as String[],
                "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER")

        if(movieDataCursor.moveToFirst()) {
            for (i in 0..(movieDataCursor.getCount() - 1)) {
                mMovieIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_MOVIE_ID))
                mMovieRowIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_ID))
                movieDataCursor.moveToNext()
            }
            //Close the cursor
            movieDataCursor.close()
            LogDisplay.callLog(LOG_TAG, 'Add data for up coming movies', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Up coming.Movie ID list-> $mMovieIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Up coming.Movie row id list-> $mMovieRowIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie-basic_info for up coming', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        if(mMovieIdList.size() > 0 && mMovieRowIdList.size() > 0) {
            LogDisplay.callLog(LOG_TAG, 'Now go and load the details of the movies', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            final ArrayList<Integer> isForHomeList = new ArrayList<>(1)
            isForHomeList.add(0,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            final ArrayList<Integer>[] loadMovieDetailsArg = [mMovieIdList, mMovieRowIdList, isForHomeList] as ArrayList<Integer>[]
            new LoadMovieDetails(mContext).execute(loadMovieDetailsArg)
        }
    }
}