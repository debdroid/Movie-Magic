package com.moviemagic.dpaul.android.app.contentprovider

import android.content.UriMatcher
import android.net.Uri
import android.test.AndroidTestCase
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicUriMatcher extends AndroidTestCase {
    private static final int TEST_MOVIE_ID = 123
    private static final String TEST_MOVIE_CATEGORY = 'popular'
    private static final long TEST_RELEASE_DATE = 1471042800385L //2016-08-13

    // content://com.moviemagic.dpaul.android.app/movie_basic_info"
    private static final Uri TEST_MOVIE_DIR = MovieMagicContract.MovieBasicInfo.CONTENT_URI
    private static final Uri TEST_MOVIE_WITH_ID_ITEM = MovieMagicContract.MovieBasicInfo.buildMovieWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_WITH_CATEGORY_DIR = MovieMagicContract.MovieBasicInfo.buildMovieWithMovieCategory(TEST_MOVIE_CATEGORY)

    /*
        Test that the UriMatcher returns the correct integer value
        for each of the Uri types that the ContentProvider can handle.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieMagicProvider.buildUriMatcher()

        assertEquals('Error: The MOVIE URI was matched incorrectly.',
                testMatcher.match(TEST_MOVIE_DIR), MovieMagicProvider.MOVIE)
        assertEquals('Error: The MOVIE WITH ID URI was matched incorrectly.',
                testMatcher.match(TEST_MOVIE_WITH_ID_ITEM), MovieMagicProvider.MOVIE_WITH_ID)
        assertEquals('Error: The MOVIE WITH CATEGORY was matched incorrectly.',
                testMatcher.match(TEST_MOVIE_WITH_CATEGORY_DIR), MovieMagicProvider.MOVIE_WITH_CATEGORY)
    }
}