package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieBasicInfo
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher()
    private MovieMagicDbHelper mOpenHelper

    static final int MOVIE = 101
    static final int MOVIE_WITH_ID = 102
    static final int MOVIE_WITH_CATEGORY = 103

    private static final SQLiteQueryBuilder sMovieMagicQueryBuilder

    static{
        sMovieMagicQueryBuilder = new SQLiteQueryBuilder()
        sMovieMagicQueryBuilder.setTables(MovieBasicInfo.TABLE_NAME)
    }

    //movie_basic_info.movie_id = ?
    private static final String sMovieWithIdSelection =
            "$MovieBasicInfo.TABLE_NAME.$MovieBasicInfo.COLUMN_MOVIE_ID = ? "

    //movie_basic_info.movie_category = ?
    private static final String sMovieWithCategorySelection =
            "$MovieBasicInfo.TABLE_NAME.$MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? "

    private Cursor getMovieById(
            Uri uri, String[] projection, String sortOrder) {
        String[] movieId = [Integer.toString(MovieBasicInfo.getMovieIdFromUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    private Cursor getMovieByCategory(
            Uri uri, String[] projection, String sortOrder) {
        String[] movieId = [MovieBasicInfo.getMovieCategoryFromUri(uri)]

        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithCategorySelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    /*
       This UriMatcher will contain the URI for MOVIE, MOVIE_WITH_ID, MOVIE_WITH_CATEGORY and
       MOVIE_WITH_RELEASE_DATE, the above defined integer constants will be returned when matched
    */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH)

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieMagicContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_BASIC_INFO,MOVIE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,MovieMagicContract.PATH_MOVIE_BASIC_INFO+"/#",MOVIE_WITH_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,MovieMagicContract.PATH_MOVIE_BASIC_INFO+"/*",MOVIE_WITH_CATEGORY)
        // 3) Return the new matcher!
        return uriMatcher
    }

    @Override
    boolean onCreate() {
        mOpenHelper = new MovieMagicDbHelper(getContext())
        return true
    }

    @Override
    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor
        switch(sUriMatcher.match(uri)) {
        // "movie_basic_info/#"
            case MOVIE_WITH_ID:
                    retCursor = getMovieById(uri, projection, sortOrder)
                    break
        // "movie_basic_info/*"
            case MOVIE_WITH_CATEGORY:
                retCursor = getMovieByCategory(uri, projection, sortOrder)
                break
        // "movie_basic_info"
            case MOVIE:
                //This is one way for writing the query ()
                retCursor = mOpenHelper.getReadableDatabase().query(MovieBasicInfo.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri)
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri)
        return retCursor
    }

    @Override
    String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri)

        switch (match) {
            case MOVIE:
                return MovieBasicInfo.CONTENT_TYPE
            case MOVIE_WITH_ID:
                return MovieBasicInfo.CONTENT_ITEM_TYPE
            case MOVIE_WITH_CATEGORY:
                return MovieBasicInfo.CONTENT_TYPE
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri)
        }
    }

    @Override
    Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        Uri returnUri

        switch (match) {
            case MOVIE:
                convertDate(values)
                long _id = db.insert(MovieBasicInfo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieBasicInfo.buildMovieUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri)
        }
        getContext().getContentResolver().notifyChange(uri, null)
        db.close()
        return returnUri
    }

    @Override
    int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        int count
        //this makes delete all rows return the number of rows deleted
        if(selection == null) selection = "1"
        switch (match) {
            case MOVIE:
                count = db.delete(MovieBasicInfo.TABLE_NAME, selection, selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri)
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            db.close()
        }
        //return the actual rows deleted
        return count
    }

    @Override
    int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        int count
        switch (match) {
            case MOVIE:
                convertDate(values)
                count = db.update(MovieBasicInfo.TABLE_NAME,values,selection,selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri)
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            db.close()
        }
        return count
    }

    @Override
    int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        switch (match) {
            case MOVIE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        convertDate(value)
                        long _id = db.insert(MovieBasicInfo.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            default:
                return super.bulkInsert(uri, values)
        }
    }
    /*
        Covert the movie release date string to numeric value
     */
    private void convertDate(ContentValues values) {
        // Covert the movie release date
        if (values.containsKey(MovieBasicInfo.COLUMN_RELEASE_DATE)) {
            String movieReleaseDate = values.getAsString(MovieBasicInfo.COLUMN_RELEASE_DATE)
            values.put(MovieBasicInfo.COLUMN_RELEASE_DATE, MovieMagicContract.covertMovieReleaseDate(movieReleaseDate))
        }
    }
}