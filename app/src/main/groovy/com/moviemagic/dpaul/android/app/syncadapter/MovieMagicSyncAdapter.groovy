package com.moviemagic.dpaul.android.app.syncadapter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SyncResult
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMovieDetails
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
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
    // Set the Date & Time stamp which is used for all the new records, this is used while housekeeping so
    // a single constant value is used for all the records
    private String mDateTimeStamp

    //Define a flag to control the record insertion / deletion
//    private boolean deleteRecords = true

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

        mDateTimeStamp = Utility.getTodayDate()
        List<ContentValues> contentValues = []
        //totalPage is set to 1 so that at least first page is downloaded in downloadMovieList
        // later this variable is overridden by the total page value retrieved from the api
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR, i)
            if(contentValues) {
                insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
                contentValues = []
            } else {
                LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_POPULAR",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED, i)
            if(contentValues) {
                insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
                contentValues = []
            } else {
                LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, i)
            if(contentValues) {
                insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
                contentValues = []
            } else {
                LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        }
        totalPage = 1
        for(i in 1..MAX_PAGE_DOWNLOAD) {
            contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, i)
            if(contentValues) {
                insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
                contentValues = []
            } else {
                LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        }
        //Now load details for home page movie items
        loadMovieDetailsForHomePageItems()

        // Check if the account is user's TMDb account(i.e. user is logged in to TMDb)
        // or regular SyncAdapter dummy account
        final boolean isUserAccount = checkAccountType(account)
        if(isUserAccount) {
            LogDisplay.callLog(LOG_TAG,'This is a user account',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            // Get the authToken( TMDb session id) which is needed for TMDb call
            final AccountManager accountManager = AccountManager.get(mContext)
            // This convenience helper synchronously gets an auth token with getAuthToken(Account, String, boolean, AccountManagerCallback, Handler)
            final String authToken = accountManager.blockingGetAuthToken(account,GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS,true)
            // Get the account id from user data
            final String accountId = accountManager.getUserData(account, GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID)
            LogDisplay.callLog(LOG_TAG,"AuthToken & AccountId. AuthToken -> $authToken " +
                    "& AccountID -> $accountId",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            if(authToken && accountId) {
                processTmdbLists(authToken, accountId)
            } else {
                LogDisplay.callLog(LOG_TAG,'Either authToken or accountId or both null. So tmdb library download skipped',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }

        } else {
            LogDisplay.callLog(LOG_TAG,'This is SyncAdapter dummy account. So no further action',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        // Let's do some housekeeping now. This is done at the end so that new records get inserted before deleting existing records
        performHouseKeeping()
    }

    /**
     * This helper method is used to download Tmdb public movie lists (i.e. popular, top rated, now playing, upcoming)
     * @param category The category of the movie which is to be downloaded
     * @param page Page number of the list
     * @return Formatted movie data as content values
     */
    private List<ContentValues> downloadMovieList (String category, int page) {
        //TMDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1

        List<ContentValues> movieList

        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(category)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,page.toString())
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url for $category & page# $page -> ${uri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            //This is intentional so that at lest one page is not loaded in order to make sure
            //at least one (i.e. first) LoadMoreMovies call is always successful
            if (page <= totalPage) {
                def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(jsonData, category, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC, mDateTimeStamp)
                totalPage = JsonParse.getTotalPages(jsonData)
            }

        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
        } catch (JsonException e) {
            Log.e(LOG_TAG, " JsonException Error: ${e.message}", e)
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException Error: ${e.message}", e)
        }
        return movieList
    }

    /**
     * This method checks the type of the account (Dummy SyncAdapter account or user's Tmdb account)
     * @param account The account for which the type needs to be determined
     * @return True if it's a user's Tmdb account
     */
    private boolean checkAccountType(Account account) {
        LogDisplay.callLog(LOG_TAG,'checkAccountType is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"Account name -> ${account.name}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        // Application can have only one account, so if it's SyncAdapter's dummy account then return false otherwise true
        if(account.name == mContext.getString(R.string.app_name)) {
            return false
        } else {
            return true
        }
    }

    /**
     * This method processes the user's Tmdb lists (i.e. Watchlist, Favourite & Rated)
     * @param sessionId The session if which is required to get the data from Tmdb server
     * @param accountId The account id of the user's account, needed to get data from Tmdb server
     */
    private void processTmdbLists(String sessionId, String accountId) {
        LogDisplay.callLog(LOG_TAG,"processTmdbLists:Session id found -> $sessionId",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        List<ContentValues> contentValues = []
        // Download user's Tmdb Watchlist movies
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST, accountId, sessionId)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST)
            contentValues = []
        } else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        // Download user's Tmdb Favourite movies
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE, accountId, sessionId)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE)
            contentValues = []
        }  else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        // Download user's Tmdb Rated movies
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED, accountId, sessionId)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED)
            contentValues = []
        } else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
    }

    /**
     * This helper method is used to download Tmdb user movie lists
     * @param category The category of the movie which is to be downloaded
     * @param accountId The account id of the user's account, needed to get data from Tmdb server
     * @param sessionId The session if which is required to get the data from Tmdb server
     * @return Formatted movie data as content values
     */
    private List<ContentValues> downloadTmdbUserList (String category, String accountId, String sessionId) {
        //TMDB api example
        // https://api.themoviedb.org/3/account/<accountId>/watchlist/movies?api_key=apiKey&session_id=sessionId
        List<ContentValues> tmdbUserMovieList

        try {
            final Uri.Builder tmdbUserUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri tmdbUserUri = tmdbUserUriBuilder.appendPath(GlobalStaticVariables.TMDB_ACCOUNT_PATH)
                    .appendPath(accountId)
                    .appendPath(category)
                    .appendPath(GlobalStaticVariables.TMDB_USER_MOVIES_PATH)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_SESSION_ID_KEY,sessionId)
                    .build()

            final URL tmdbUserUrl = new URL(tmdbUserUri.toString())
            LogDisplay.callLog(LOG_TAG,"Tmdb user movie url for $category -> ${tmdbUserUri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(tmdbUserUrl)
            LogDisplay.callLog(LOG_TAG, "Tmdb user movie JSON data for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            tmdbUserMovieList = JsonParse.parseMovieListJson(jsonData, category, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_USER, mDateTimeStamp)
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, " URISyntaxException Error: ${e.message}", e)
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
        }
        return tmdbUserMovieList
    }

    /**
     * This method inserts the data to the movie_basic_info database
     * @param cvList The content values to ve inserted
     * @param category The category of the data (movie list) is to be inserted (used for display purpose only)
     */
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

    /**
     * Load movie details for the movies which are used for Home page (Now playing & Upcoming)
     */
    private void loadMovieDetailsForHomePageItems() {
        LogDisplay.callLog(LOG_TAG,'loadMovieDetailsForHomePageItems is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        Cursor movieDataCursor
        ArrayList<Integer> mMovieIdList = new ArrayList<>()
        ArrayList<Integer> mMovieRowIdList = new ArrayList<>()
        //First finalise the data to be loaded for now playing
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                /**The conditions used here are same as what used in Home Fragment loader (except COLUMN_CREATE_TIMESTAMP which is used to consider they new data only).
                   If that changes then change this **/
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP >= ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, '', '', mDateTimeStamp] as String[],
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
        //Now finalise the data to be loaded for upcoming
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                /**The conditions used here are same as what used in Home Fragment loader (except COLUMN_CREATE_TIMESTAMP which is used to consider they new data only).
                   If that changes then change this **/
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP >= ? """,
                   [GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, '', '', mDateTimeStamp] as String[],
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
            LogDisplay.callLog(LOG_TAG, "Appended Up coming.Movie ID list-> $mMovieIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Appended Up coming.Movie row id list-> $mMovieRowIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie-basic_info for up coming', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        //Now go and load the detail data for the home screen movies
        if(mMovieIdList.size() > 0 && mMovieRowIdList.size() > 0) {
            LogDisplay.callLog(LOG_TAG, 'Now go and load the details of the movies for home page..', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            ArrayList<Integer> isForHomeList = new ArrayList<>(1)
            //Set this flag to true as the Home page videos are retrieved based on this indicator
            isForHomeList.add(0,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            final ArrayList<Integer>[] loadMovieDetailsArg = [mMovieIdList, mMovieRowIdList, isForHomeList] as ArrayList<Integer>[]
            new LoadMovieDetails(mContext).execute(loadMovieDetailsArg)
        }
    }

    /**
     * This method does the housekeeping of the application's data
     */
    private performHouseKeeping() {
        LogDisplay.callLog(LOG_TAG,'performHouseKeeping is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"performHouseKeeping: Today's DateTimeStamp->$mDateTimeStamp",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        // Delete old data except user's records from movie_basic_info and recommendations movies (recommendations are deleted in the next step)
        final int movieBasicInfoDeleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE != ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY != ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP < ? """,
                    /** To ensure newly inserted records are not deleted, mDateTimeStamp is used and it ensures **/
                    /** that all old records except the one which just inserted as part of this execution are deleted **/
                   [GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST, GlobalStaticVariables.MOVIE_CATEGORY_RECOMMENDATIONS, mDateTimeStamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total records deleted from movie_basic_info (except recommendations) -> $movieBasicInfoDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete recommendation records which are more than 10 days old **/
        String tenDayPriorTimestamp = Utility.getTenDayPriorDate()
        LogDisplay.callLog(LOG_TAG,"performHouseKeeping: Ten day's prior DateTimeStamp->$tenDayPriorTimestamp",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        final int movieBasicInfoRecommendDeleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP < ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_RECOMMENDATIONS, tenDayPriorTimestamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total recommended records deleted from movie_basic_info -> $movieBasicInfoRecommendDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Reset the data already present flags for user local lists movies, so that it's get updated next time it accessed by user  **/
        final ContentValues userListContentValues = new ContentValues()
        userListContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,0)
        int userListMovieBasicInfoUpdateCount = mContentResolver.update(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                userListContentValues,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ?""",
                [Integer.toString(GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE), GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST] as String[] )
        LogDisplay.callLog(LOG_TAG,"Total records updated for user list in movie_basic_info-> $userListMovieBasicInfoUpdateCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete old data from movie_person_info  **/
        int personInfoDeleteCount = mContentResolver.delete(MovieMagicContract.MoviePersonInfo.CONTENT_URI,
                "$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP < ? ",
                [mDateTimeStamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total records deleted from movie_person_info -> $personInfoDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete old data from movie_collection  **/
        int collectionDeleteCount = mContentResolver.delete(MovieMagicContract.MovieCollection.CONTENT_URI,
                "$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP < ? ",
                [mDateTimeStamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total records deleted from movie_collection -> $collectionDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
    }
}