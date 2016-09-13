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
class LoadMovieBasicAddlInfo extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadMovieBasicAddlInfo.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
//    private final Uri mMovieBasicMovieIdUri

    public LoadMovieBasicAddlInfo(Context ctx) {
//    public LoadMovieBasicAddlInfo(Context ctx, Uri movieUri) {
        mContext = ctx
//        mMovieBasicMovieIdUri = movieUri
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(Integer... params) {
        final int movieId = params[0]
        final int movieBasicRowId = params[1]
        //TMDB api example
        //https://api.themoviedb.org/3/movie/240?api_key=key&append_to_response=similar,credits,images,videos,release_dates,reviews

        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(Integer.toString(movieId))
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_APPEND_TO_RESPONSE_KEY, GlobalStaticVariables.TMDB_APPEND_TO_RESPONSE_PARAM)
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG, "Movie id append response url-> ${uri.toString()}", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * Load (update) the additional movie details
             * **/
            LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieId -> $jsonData", LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
            final ContentValues contentMovieBasicInfoUpdateValues = JsonParse.parseAdditionalBasicMovieData(jsonData)
            //Update the indicator to indicate data is loaded
            contentMovieBasicInfoUpdateValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            final String[] movieBasicInfoMovieRowId =  [Integer.toString(movieBasicRowId)]
//            final String[] movieBasicInfoMovieId =  [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieBasicMovieIdUri))]
            mContentResolver.update(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    contentMovieBasicInfoUpdateValues,
                    MovieMagicContract.MovieBasicInfo._ID + "= ?",
//                    MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
                    movieBasicInfoMovieRowId)
//                    movieBasicInfoMovieId)

            /**
             * Process and load (insert) the similar movies
             * **/
            final ContentValues[] similarMovieContentValues = JsonParse.parseSimilarMovieListJson(jsonData, movieId) as ContentValues []
            final int similarMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,similarMovieContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for similar movie->$similarMovieCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie cast data
             * **/
            final ContentValues[] movieCastContentValues = JsonParse.praseMovieCastJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int movieCastCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCast.CONTENT_URI,movieCastContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie cast->$movieCastCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie crew data
             * **/
            final ContentValues[] movieCrewContentValues = JsonParse.praseMovieCrewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int movieCrewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCrew.CONTENT_URI,movieCrewContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie crew->$movieCrewCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie image data
             * **/
            final ContentValues[] movieImageContentValues = JsonParse.praseMovieImageJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int movieImageCount = mContentResolver.bulkInsert(MovieMagicContract.MovieImage.CONTENT_URI,movieImageContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie image->$movieImageCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie video data
             * **/
            final ContentValues[] movieVideoContentValues = JsonParse.praseMovieVideoJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int movieVideoCount = mContentResolver.bulkInsert(MovieMagicContract.MovieVideo.CONTENT_URI,movieVideoContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie now_playing->$movieVideoCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie release date data
             * **/
            final ContentValues[] movieReleaseContentValues = JsonParse.praseMovieReleaseDateJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int MovieReleaseCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReleaseDate.CONTENT_URI,movieReleaseContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie release date->$MovieReleaseCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

            /**
             * Process and load (insert) the movie review data
             * **/
            final ContentValues[] movieReviewContentValues = JsonParse.praseMovieReviewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            final int movieReviewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReview.CONTENT_URI,movieReviewContentValues)
            LogDisplay.callLog(LOG_TAG,"Total insert for movie review->$movieReviewCount",LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)

        } catch (URISyntaxException e) {
            //Set the boolean to true to indicate API call failed
            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "URISyntaxException: $e.message", e)
        } catch (JsonException e) {
            //Set the boolean to true to indicate API call failed
            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "JsonException: $e.message", e)
        } catch (IOException e) {
            //Set the boolean to true to indicate API call failed
            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "IOException: $e.message")
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            //Set the boolean to true to indicate API call failed
            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "SQLiteConstraintException: $e.message")
        } catch (android.database.sqlite.SQLiteException e) {
            //Set the boolean to true to indicate API call failed
            GridMovieFragment.isDataLoadFailed = true
            Log.e(LOG_TAG, "SQLiteException: $e.message")
        }
        return null
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid)
        LogDisplay.callLog(LOG_TAG,'Additional movie data loaded finished',LogDisplay.LOAD_MOVIE_BASIC_ADDL_INFO_FLAG)
    }
}