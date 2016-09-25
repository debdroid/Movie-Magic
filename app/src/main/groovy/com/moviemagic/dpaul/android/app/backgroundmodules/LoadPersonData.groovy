package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper;
import groovy.transform.CompileStatic

@CompileStatic
class LoadPersonData extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadPersonData.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext

    public LoadPersonData(Context ctx) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(Integer... params) {
        final int personId = params[0]
        //TMDB api example (person with appended response)
        //http://api.themoviedb.org/3/person/1158?api_key=key5&append_to_response=movie_credits
        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_PERSON_PATH)
                    .appendPath(Integer.toString(personId))
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_APPEND_TO_RESPONSE_KEY, GlobalStaticVariables.TMDB_PERSON_APPEND_TO_RESPONSE_PARAM)
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Person url-> ${uri.toString()}",LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * Process and load (insert) the person info data
             * **/
            final ContentValues personInfoDataContentValue = JsonParse.parsePersonInfoDataJson(jsonData, personId) as ContentValues
            final long personInfoRowId
            if(personInfoDataContentValue) {
                final Uri personDataUri = mContentResolver.insert(MovieMagicContract.MoviePersonInfo.CONTENT_URI, personInfoDataContentValue)
                LogDisplay.callLog(LOG_TAG, "Person info data inserted.Uri->$personDataUri", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                if(ContentUris.parseId(personDataUri) == -1) {
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_person_info failed. Uri->$personDataUri",LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_person_info successful. Uri->$personDataUri",LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    personInfoRowId = MovieMagicContract.MoviePersonInfo.getRowIdFromMoviePersonInfoUri(personDataUri)
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'JsonParse.parsePersonInfoDataJson returned null',LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
            }

            /**
             * Process and load (insert) the person crew and cast data
             * **/
            if(personInfoRowId > 0) {
                final ContentValues[] personCastContentValues = JsonParse.parsePersonCastDataJson(jsonData, personId, personInfoRowId) as ContentValues []
                if(personCastContentValues) {
                    final int personCastCount = mContentResolver.bulkInsert(MovieMagicContract.MoviePersonCast.CONTENT_URI, personCastContentValues)
                    LogDisplay.callLog(LOG_TAG, "Total person cast inserted.->$personCastCount", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    if (personCastCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_person_cast successful. Insert count->$personCastCount", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_person_cast failed. Insert count->$personCastCount", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,'JsonParse.parsePersonCastDataJson returned null',LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                }
                final ContentValues[] personCrewContentValues = JsonParse.parsePersonCrewDataJson(jsonData, personId, personInfoRowId) as ContentValues []
                if(personCrewContentValues) {
                    final int personCrewCount = mContentResolver.bulkInsert(MovieMagicContract.MoviePersonCrew.CONTENT_URI, personCrewContentValues)
                    if (personCrewCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_person_crew successful. Insert count->$personCrewCount", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_person_crew failed. Insert count->$personCrewCount", LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,'JsonParse.parsePersonCrewDataJson returned null',LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
                }
            } else {
                LogDisplay.callLog(LOG_TAG,"Person info row id is not valid.->$personInfoRowId",LogDisplay.LOAD_PERSON_DATA_LOG_FLAG)
            }

        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, e.message, e)
        } catch (JsonException e) {
            Log.e(LOG_TAG, e.message, e)
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error:", e)
        }
        return null
    }
}