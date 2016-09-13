package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
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
class LoadCollectionData extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadCollectionData.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext

    public LoadCollectionData(Context ctx) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
    }

    @Override
    protected Void doInBackground(Integer... params) {
        final int collectionId = params[0]
        //TMDB api example
        //https://api.themoviedb.org/3/collection/10?api_key=key
        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_COLLECTION_PATH)
                    .appendPath(Integer.toString(collectionId))
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Collection url-> ${uri.toString()}",LogDisplay.LOAD_COLLECTION_DATA_FLAG)

            def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * Process and load (insert) the collection data
             * **/
            //TODO - need to add a logic to clean-up data (during sync load??). This is is needed because even thought the flag is used
            //TODO but it is observed that sometimes it is getting set without inserting data to main table!
            final ContentValues collectionDataContentValue = JsonParse.parseCollectionDataJson(jsonData) as ContentValues
            final Uri collectionDataUri = mContentResolver.insert(MovieMagicContract.MovieCollection.CONTENT_URI,collectionDataContentValue)
            LogDisplay.callLog(LOG_TAG,"Collection data inserted.Uri->$collectionDataUri",LogDisplay.LOAD_COLLECTION_DATA_FLAG)

            /**
             * Process and load (insert) the collection movies
             * **/
            final ContentValues[] collectionMoviesContentValues = JsonParse.parseCollectionMovieJson(jsonData) as ContentValues []
            final int collectionMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,collectionMoviesContentValues)
            LogDisplay.callLog(LOG_TAG,"Total collection movie inserted.->$collectionMovieCount",LogDisplay.LOAD_COLLECTION_DATA_FLAG)

            /**
             * Update the flag of collection table to indicate that collection movies are inserted
             * **/
            if(collectionDataUri && collectionMovieCount> 0) {
                final ContentValues collectionData = new ContentValues()
                collectionData.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
                final long collectionRowId = MovieMagicContract.MovieCollection.getCollectionRpwIdFromMovieCollectionUri(collectionDataUri)
                final String[] args = Long.toString(collectionRowId) as String[]
                final int updateCount = mContentResolver.update(
                                        MovieMagicContract.MovieCollection.CONTENT_URI,
                                        collectionData,
                                        MovieMagicContract.MovieCollection._ID + "= ?",
                                        args)
                if(updateCount == 1) {
                    LogDisplay.callLog(LOG_TAG,'Collection movie present flag update successful',LogDisplay.LOAD_COLLECTION_DATA_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG,"Collection movie present flag update NOT successful.Update count->$updateCount",LogDisplay.LOAD_COLLECTION_DATA_FLAG)
                }
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