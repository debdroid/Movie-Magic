package com.moviemagic.dpaul.android.app.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import groovy.transform.CompileStatic
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieBasicInfo

/**
 * Manages a local database for movie data.
 */

@CompileStatic
class MovieMagicDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieMagicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Create the SQL to create location table
        final  String SQL_CREATE_MOVIE_TABLE = """
                CREATE TABLE $MovieBasicInfo.TABLE_NAME (
                $MovieBasicInfo._ID INTEGER PRIMARY KEY,
                $MovieBasicInfo.COLUMN_MOVIE_ID INTEGER NOT NULL,
                $MovieBasicInfo.COLUMN_BACKDROP_PATH TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_ORIGINAL_TITLE TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_OVERVIEW TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_RELEASE_DATE INTEGER NOT NULL,
                $MovieBasicInfo.COLUMN_POSTER_PATH TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_POPULARITY REAL NOT NULL,
                $MovieBasicInfo.COLUMN_TITLE TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_VIDEO_FLAG TEXT NOT NULL,
                $MovieBasicInfo.COLUMN_VOTE_AVG REAL NOT NULL,
                $MovieBasicInfo.COLUMN_VOTE_COUNT INTEGER NOT NULL,
                $MovieBasicInfo.COLUMN_MOVIE_CATEGORY TEXT NOT NULL);
                """
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE)
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieBasicInfo.TABLE_NAME")
        onCreate(sqLiteDatabase);
    }
}