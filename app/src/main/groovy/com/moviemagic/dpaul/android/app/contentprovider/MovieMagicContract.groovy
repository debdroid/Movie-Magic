package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import groovy.transform.CompileStatic
import java.text.SimpleDateFormat

@CompileStatic
class MovieMagicContract {
    // To make it easy to sort the list by movie release date, we store the date
    // in the database in milliseconds format using SimpleDateFormat and Date
    //The value is the number of milliseconds since Jan. 1, 1970, midnight GMT.
    static long covertMovieReleaseDate(String releaseDate) {
        //Split the date string which is of format yyyy-mm-dd
        Date simpleReleaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(releaseDate)
        long timeInMilliSeconds = simpleReleaseDate.getTime()
    }

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = 'com.moviemagic.dpaul.android.app'

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    static final Uri BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    // Possible paths (appended to base content URI for possible URI's)
    static final String PATH_MOVIE_BASIC_INFO = 'movie_basic_info'

    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.
     */
    public static final class MovieBasicInfo implements BaseColumns {
        static final String TABLE_NAME = 'movie_basic_info'

        //Column to store movie id
        static final String COLUMN_MOVIE_ID = 'movie_id'
        //Column to store movie backdrop path
        static final String COLUMN_BACKDROP_PATH = 'backdrop_path'
        //Column to store movie original title
        static final String COLUMN_ORIGINAL_TITLE = 'original_title'
        //Column to store movie overview
        static final String COLUMN_OVERVIEW = 'overview'
        //Column to store movie release date
        static final String COLUMN_RELEASE_DATE = 'release_date'
        //Column to store movie poster path
        static final String COLUMN_POSTER_PATH = 'poster_path'
        //Column to store movie popularity
        static final String COLUMN_POPULARITY = 'popularity'
        //Column to store movie title
        static final String COLUMN_TITLE = 'title'
        //Column to store movie video flag
        static final String COLUMN_VIDEO_FLAG = 'video_flag'
        //Column to store movie voting average value
        static final String COLUMN_VOTE_AVG = 'vote_average'
        //Column to store movie vote count
        static final String COLUMN_VOTE_COUNT = 'vote_count'
        //Column to store movie category (Not fetched from API)
        static final String COLUMN_MOVIE_CATEGORY = 'movie_category'

        //Uri for movie table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_BASIC_INFO).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_BASIC_INFO"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_BASIC_INFO"

        static Uri buildMovieUri(long id) {
            ContentUris.withAppendedId(CONTENT_URI, id);
        }

        static Uri buildMovieWithMovieId (int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static Uri buildMovieWithMovieCategory (String movieCategory) {
            CONTENT_URI.buildUpon().appendPath(movieCategory).build()
        }

        static int getMovieIdFromUri (Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }

        static String getMovieCategoryFromUri (Uri uri) {
            uri.getPathSegments().get(1)
        }
    }
}