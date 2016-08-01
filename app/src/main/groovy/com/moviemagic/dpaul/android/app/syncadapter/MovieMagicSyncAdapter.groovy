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
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.utility.JsonParse
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieMagicSyncAdapter.class.getSimpleName()
    //Movie list type for tmdb public movies
    public static final String MOVIE_LIST_TYPE_PUBLIC = 'tmdb_public'

    //This variable indicates the number of pages for initial load. It is also
    //used to determine the next page to download during more download
    public final static int MAX_PAGE_DOWNLOAD = 3
    //Define a variable for api page count
    private static int totalPage = 0
    // Define a variable to contain a content resolver instance
    private ContentResolver mContentResolver
    private Context mContext

    //Define a flag to control the record insertion / deletion
    private boolean deleteRecords = true

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
        final String POPULAR_PATH = 'popular'
        final String TOP_RATED_PATH = 'top_rated'
        final String UPCOMING_PATH = 'upcoming'
        final String NOW_PLAYING_PATH = 'now_playing'

        List<ContentValues> contentValues = []
        //totalPage is set to 1 so that at least first page is downloaded in downloadMovieList
        // later this variable is overridden by the total page value retrieved from the api
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(POPULAR_PATH, i)
            insertBulkRecords(contentValues, POPULAR_PATH)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(TOP_RATED_PATH, i)
            insertBulkRecords(contentValues, TOP_RATED_PATH)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(UPCOMING_PATH, i)
            insertBulkRecords(contentValues, UPCOMING_PATH)
            contentValues = []
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues += downloadMovieList(NOW_PLAYING_PATH, i)
            insertBulkRecords(contentValues, NOW_PLAYING_PATH)
            contentValues = []
        }
        deleteRecords = true
    }

    private List<ContentValues> downloadMovieList (String category, int page) {
        //TMDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1
        final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
        final String MOVIE_PATH = 'movie'
        final String API_KEY = 'api_key'
        final String PAGE = 'page'

        List<ContentValues> movieList

        try {
            Uri.Builder uriBuilder = Uri.parse(TMDB_MOVIE_BASE_URL).buildUpon()

            Uri uri = uriBuilder.appendPath(MOVIE_PATH)
                    .appendPath(category)
                    .appendQueryParameter(API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(PAGE,page.toString())
                    .build()

            URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url-> ${uri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            //This is intentional so that at lest one page is not loaded in order to make sure
            //at least one (i.e. first) LoadMoreData call is always successful
            if (page <= totalPage) {
                def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(jsonData, category, MOVIE_LIST_TYPE_PUBLIC)
                totalPage = JsonParse.getTotalPages(jsonData)
            }
            if(deleteRecords) {
                // delete old data except user's records
                int deleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE <> ?",
                        ['1'] as String []
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
        int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,cv)
        LogDisplay.callLog(LOG_TAG,"Total insert for $category->$insertCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
    }
}