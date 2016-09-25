package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentUris
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
class LoadMovieDetails extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadMovieDetails.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
//    private final Uri mMovieBasicMovieIdUri

    public LoadMovieDetails(Context ctx) {
//    public LoadMovieDetails(Context ctx, Uri movieUri) {
        mContext = ctx
//        mMovieBasicMovieIdUri = movieUri
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(Integer... params) {
        final int movieId = params[0]
        final int movieBasicRowId = params[1]

        //TMDb api example (movie with appended response)
        //https://api.themoviedb.org/3/movie/240?api_key=key&append_to_response=similar,credits,images,videos,release_dates,reviews

        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(Integer.toString(movieId))
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_APPEND_TO_RESPONSE_KEY, GlobalStaticVariables.TMDB_MOVIE_APPEND_TO_RESPONSE_PARAM)
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG, "Movie id append response url-> ${uri.toString()}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * Load (update / insert) the additional movie details
             * **/
            LogDisplay.callLog(LOG_TAG, "JSON DATA for $movieId -> $jsonData", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            final ContentValues contentMovieBasicInfoValues = JsonParse.parseAdditionalBasicMovieData(jsonData)
//            //Update the indicator to indicate data is loaded
//            contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            if(contentMovieBasicInfoValues) {
                if (movieId && movieBasicRowId == 0) {
                    //This part of the code will be executed for person's crew and cast movie only, so safe to use the
                    //following 3 lines
                    contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
                    contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PERSON)
                    contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER, 0)
                    //Populate timestamp for new record
                    contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP, Utility.getTodayDate())
                    final Uri movieBasicInfoUri = mContentResolver.insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, contentMovieBasicInfoValues)
                    if (ContentUris.parseId(movieBasicInfoUri) == -1) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info failed. Uri->$movieBasicInfoUri", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info successful. Uri->$movieBasicInfoUri", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        //Populate the movieBasicRowId - used while inserting records to other dependant tables
                        movieBasicRowId = ContentUris.parseId(movieBasicInfoUri) as Integer
                        LogDisplay.callLog(LOG_TAG, "movieBasicRowId->$movieBasicRowId", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    }
                } else {
//                final String[] movieBasicInfoMovieRowId = [Integer.toString(movieBasicRowId)]
//              final String[] movieBasicInfoMovieId =  [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieBasicMovieIdUri))]
                    final int movieBasicInfoUpdateCount = mContentResolver.update(
                            MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                            contentMovieBasicInfoValues,
                            "$MovieMagicContract.MovieBasicInfo._ID = ?",
//                    MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
                            [Integer.toString(movieBasicRowId)] as String[])
//                    movieBasicInfoMovieId)
                    if (movieBasicInfoUpdateCount != 1) {
                        LogDisplay.callLog(LOG_TAG, "Update in movie_basic_info failed. Update Count->$movieBasicInfoUpdateCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else { //If the return value to 1, indicate successful insert
                        LogDisplay.callLog(LOG_TAG, "Update in movie_basic_info successful. Update Count->$movieBasicInfoUpdateCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    }
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.parseAdditionalBasicMovieData returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            final boolean allOperationSuccessullFlag = true
            /**
             * Process and load (insert) the similar movies
             * **/
            final ContentValues[] similarMovieContentValues = JsonParse.parseSimilarMovieListJson(jsonData, movieId) as ContentValues []
            if(similarMovieContentValues) {
                final int similarMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, similarMovieContentValues)
                if (similarMovieCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert similar movie in movie_basic_info successful. Insert count->$similarMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert similar movie in movie_basic_info failed. Insert count->$similarMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.parseSimilarMovieListJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie cast data
             * **/
            final ContentValues[] movieCastContentValues = JsonParse.praseMovieCastJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieCastContentValues) {
                final int movieCastCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCast.CONTENT_URI, movieCastContentValues)
                if (movieCastCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_cast successful. Insert count->$movieCastCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_cast failed. Insert count->$movieCastCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieCastJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie crew data
             * **/
            final ContentValues[] movieCrewContentValues = JsonParse.praseMovieCrewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieCrewContentValues) {
                final int movieCrewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCrew.CONTENT_URI, movieCrewContentValues)
                if (movieCrewCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_crew successful. Insert count->$movieCrewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_crew failed. Insert count->$movieCrewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieCrewJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie image data
             * **/
            final ContentValues[] movieImageContentValues = JsonParse.praseMovieImageJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieImageContentValues) {
                final int movieImageCount = mContentResolver.bulkInsert(MovieMagicContract.MovieImage.CONTENT_URI, movieImageContentValues)
                if (movieImageCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_image successful. Insert count->$movieImageCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_image failed. Insert count->$movieImageCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieImageJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie video data
             * **/
            final ContentValues[] movieVideoContentValues = JsonParse.praseMovieVideoJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieVideoContentValues) {
                final int movieVideoCount = mContentResolver.bulkInsert(MovieMagicContract.MovieVideo.CONTENT_URI, movieVideoContentValues)
                if (movieVideoCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_video successful. Insert count->$movieVideoCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_video failed. Insert count->$movieVideoCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieVideoJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie release date data
             * **/
            final ContentValues[] movieReleaseContentValues = JsonParse.praseMovieReleaseDateJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieReleaseContentValues) {
                final int movieReleaseCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReleaseDate.CONTENT_URI, movieReleaseContentValues)
                if (movieReleaseCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_release_date successful. Insert count->$movieReleaseCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_release_date failed. Insert count->$movieReleaseCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieReleaseDateJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            /**
             * Process and load (insert) the movie review data
             * **/
            final ContentValues[] movieReviewContentValues = JsonParse.praseMovieReviewJson(jsonData, movieId, movieBasicRowId) as ContentValues []
            if(movieReviewContentValues) {
                final int movieReviewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReview.CONTENT_URI, movieReviewContentValues)
                if (movieReviewCount > 0) {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_review successful. Insert count->$movieReviewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Insert in movie_review failed. Insert count->$movieReviewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    allOperationSuccessullFlag = false
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.praseMovieReviewJson returned null',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            }

            //Reset the data present flag of movie_basic_info if any problem faced during operation
            if(!allOperationSuccessullFlag) {
                final ContentValues movieBasicInfoUpdateData = new ContentValues()
                movieBasicInfoUpdateData.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                final int movieBasicInfoFlagResetCount = mContentResolver.update(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        movieBasicInfoUpdateData,
                        "$MovieMagicContract.MovieBasicInfo._ID = ?",
                        [Integer.toString(movieBasicRowId)] as String[])
                if(movieBasicInfoFlagResetCount != 1) {
                    LogDisplay.callLog(LOG_TAG,"Update to reset COLUMN_DETAIL_DATA_PRESENT_FLAG in movie_basic_info failed. Update Count->$movieBasicInfoFlagResetCount",LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else { //If the return value to 1, indicate successful insert
                    LogDisplay.callLog(LOG_TAG,"Update to reset COLUMN_DETAIL_DATA_PRESENT_FLAG in movie_basic_info successful. Update Count->$movieBasicInfoFlagResetCount",LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }
            }

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
        LogDisplay.callLog(LOG_TAG,'Additional movie data loaded finished',LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
    }
}