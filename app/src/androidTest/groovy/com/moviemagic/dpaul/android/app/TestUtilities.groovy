package com.moviemagic.dpaul.android.app

import android.content.ContentValues
import android.database.Cursor
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract;
import groovy.transform.CompileStatic

@CompileStatic
class TestUtilities extends AndroidTestCase {

    static final long TEST_DATE = 1471042800385 //2016-08-13

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieInfoValues = new ContentValues();
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,1)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE,'Troy')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,'Troy is a good movie')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,'/troy.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,20)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,7.8f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG,'True')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY,5.6f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,'troyposter.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_DATE)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,'popular')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,'Helen of Troy')
        movieInfoValues
    }
}