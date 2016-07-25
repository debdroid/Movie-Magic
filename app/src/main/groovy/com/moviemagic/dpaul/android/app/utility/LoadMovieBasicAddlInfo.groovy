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
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper;
import groovy.transform.CompileStatic

@CompileStatic
class LoadMovieBasicAddlInfo extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadMovieBasicAddlInfo.class.getSimpleName()
    private ContentResolver mContentResolver
    private Context mContext
    private Uri mMovieBasicMovieIdUri
    public static final String SIMILAR_MOVIE_CATEGORY = 'similar_movie'
    public static final String SIMILAR_MOVIE_LIST_TYPE = 'tmdb_public_similar_movie_list'

    public LoadMovieBasicAddlInfo(Context ctx, Uri movieUri) {
        mContext = ctx
        mMovieBasicMovieIdUri = movieUri
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int movieId = params[0]
        int movieBasicRowId = params[1]
//        int totalPage
//        List<ContentValues> movieList
        //TBDB api example
        //https://api.themoviedb.org/3/movie/240?api_key=key&append_to_response=similar,credits,images,videos,release_dates,reviews
        final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
        final String MOVIE_PATH = 'movie'
        final String API_KEY = 'api_key'
        final String APPEND_TO_RESPONSE_KEY = 'append_to_response'
        final String APPEND_TO_RESPONSE_PARAM = 'similar,credits,images,videos,release_dates,reviews'
        try {
            Uri.Builder uriBuilder = Uri.parse(TMDB_MOVIE_BASE_URL).buildUpon()

            Uri uri = uriBuilder.appendPath(MOVIE_PATH)
                    .appendPath(Integer.toString(movieId))
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(APPEND_TO_RESPONSE_KEY, APPEND_TO_RESPONSE_PARAM)
                    .build()

            URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG, "Movie id append response url-> ${uri.toString()}", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * Load (update) the additional movie details
             * **/
            LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieId -> $jsonData", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
            ContentValues contentMovieBasicInfoUpdateValues = JsonParse.parseAdditionalBasicMovieData(jsonData)
            //Update the indicator to indicate data is loaded
            contentMovieBasicInfoUpdateValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,1)
            String[] movieBasicInfoMovieId =  [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieBasicMovieIdUri))]
            mContentResolver.update(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    contentMovieBasicInfoUpdateValues,
                    MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
                    movieBasicInfoMovieId)

            /**
             * Process and load (insert) the similar movies
             * **/
            ContentValues[] similarMovieContentValues = JsonParse.parseSimilarMovieListJson(jsonData, movieId) as ContentValues []
            int similarMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,similarMovieContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for similar movie->$similarMovieCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie cast data
             * **/
            ContentValues[] movieCastContentValues = JsonParse.praseMovieCastJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieCastCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCast.CONTENT_URI,movieCastContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie cast->$MovieCastCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie crew data
             * **/
            ContentValues[] movieCrewContentValues = JsonParse.praseMovieCrewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieCrewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCrew.CONTENT_URI,movieCrewContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie crew->$MovieCrewCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie image data
             * **/
            ContentValues[] movieImageContentValues = JsonParse.praseMovieImageJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieImageCount = mContentResolver.bulkInsert(MovieMagicContract.MovieImage.CONTENT_URI,movieImageContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie image->$MovieImageCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie video data
             * **/
            ContentValues[] movieVideoContentValues = JsonParse.praseMovieVideoJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieVideoCount = mContentResolver.bulkInsert(MovieMagicContract.MovieVideo.CONTENT_URI,movieVideoContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie video->$MovieVideoCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie release date data
             * **/
            ContentValues[] movieReleaseContentValues = JsonParse.praseMovieReleaseDateJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieReleaseCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReleaseDate.CONTENT_URI,movieReleaseContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie release date->$MovieReleaseCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie review data
             * **/
            ContentValues[] movieReviewContentValues = JsonParse.praseMovieReviewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            int MovieReviewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReview.CONTENT_URI,movieReviewContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie review->$MovieReviewCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

//            /**
//             * All required fields are inserted, now Load (update) the additional movie details
//             * This update is done at the end to ensure all required data is loaded before updating the
//             * COLUMN_DETAIL_DATA_PRESENT_FLAG with '1'
//             * **/
//            LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieId -> $jsonData", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
//            ContentValues contentMovieBasicInfoUpdateValues = JsonParse.parseAdditionalBasicMovieData(jsonData)
//            //Update the indicator to indicate data is loaded
//            contentMovieBasicInfoUpdateValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,1)
//            String[] movieBasicInfoMovieId =  [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieBasicMovieIdUri))]
//            mContentResolver.update(
//                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
//                    contentMovieBasicInfoUpdateValues,
//                    MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
//                    movieBasicInfoMovieId)

//            LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieId -> $jsonData.similar", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
//                movieList = JsonParse.parseMovieListJson(jsonData, movieCategory, MovieMagicSyncAdapter.MOVIE_LIST_TYPE_PUBLIC)
//                ContentValues[] cv = movieList as ContentValues[]
//                int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, cv)
//                LogDisplay.callLog(LOG_TAG, "Total insert for $movieCategory->$insertCount", LogDisplay.LOAD_MORE_DATA_LOG_FLAG)
//
        } catch (URISyntaxException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "URISyntaxException: $e.message", e)
        } catch (JsonException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "JsonException: $e.message", e)
        } catch (IOException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "IOException: $e.message")
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "SQLiteConstraintException: $e.message")
        } catch (android.database.sqlite.SQLiteException e) {
            //Set the boolean to true to indicate API call failed
            GridFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "SQLiteException: $e.message")
        }
        return null
    }
}