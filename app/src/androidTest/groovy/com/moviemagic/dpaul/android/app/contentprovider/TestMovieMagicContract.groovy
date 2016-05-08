package com.moviemagic.dpaul.android.app.contentprovider

import android.net.Uri
import android.test.AndroidTestCase;
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicContract extends AndroidTestCase {

    public void testBuildWeatherLocation() {
        final String TEST_BASE_URI = 'content://com.moviemagic.dpaul.android.app/movie_basic_info'
        final long TEST_MOVIE_ID = 12
  // intentionally includes a slash to make sure Uri is getting quoted correctly (i.e. getting %2F or '/' & %20 for space)
        final String TEST_MOVIE_CATEGORY = "/popular movie"
        final long TEST_DATE = 1471042800000L //2016-08-13

        Uri movidIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUri(TEST_MOVIE_ID)
        assertEquals(TEST_BASE_URI+'/12',movidIdUri.toString())
        Uri movieCategoryUri = MovieMagicContract.MovieBasicInfo.buildMovieWithMovieCategory(TEST_MOVIE_CATEGORY)
        assertEquals(TEST_BASE_URI+'/%2Fpopular%20movie', movieCategoryUri.toString())
        assertEquals(new Date(TEST_DATE).toString(), new Date(MovieMagicContract.covertMovieReleaseDate('2016-08-13')).toString())

        assertEquals(TEST_MOVIE_ID,MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(movidIdUri))
        assertEquals(TEST_MOVIE_CATEGORY,MovieMagicContract.MovieBasicInfo.getMovieCategoryFromUri(movieCategoryUri))
    }
}