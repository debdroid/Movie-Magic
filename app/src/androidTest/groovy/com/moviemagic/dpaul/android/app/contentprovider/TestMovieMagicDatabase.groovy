package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.TestUtilities
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicDatabase extends AndroidTestCase {

    //public static final String LOG_TAG = TestDb.class.getSimpleName()

    // Delete the database so that each test starts with a clean slate
    def deleteTheDatabase() {
        mContext.deleteDatabase(MovieMagicDbHelper.DATABASE_NAME)
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    void setUp() {
        deleteTheDatabase()
    }

    void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieMagicDbHelper.DATABASE_NAME)
        SQLiteDatabase db = new MovieMagicDbHelper(this.mContext).getWritableDatabase()
        assertEquals(true, db.isOpen())

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

        assertTrue('Error: This means that the database has not been created correctly',c.moveToFirst())

        // verify that the tables have been created. Metadata table name is "android_metadata" but
        // database table name starts with "m", so access the second element (i.e. [1])
        c.moveToNext()
        assertEquals(c.getString(0),MovieMagicContract.MovieBasicInfo.TABLE_NAME)

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieBasicInfo.TABLE_NAME)", null)

        assertTrue('Error: This means that we were unable to query the database for table information.',c.moveToFirst())

        // Build a Map of all of the column names we want to look for
        def columeList = []
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT

        int columnNameIndex = c.getColumnIndex('name')
        while(columeList) {
            String columnName = c.getString(columnNameIndex)
            columeList.remove(columnName)
            c.moveToNext()
        }

        // if this fails, it means that your database doesn't contain all of the required MovieMagic
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required MovieMagic entry columns",columeList.isEmpty())
        db.close()
    }

    void testMovieTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()

        // Create ContentValues of what you want to insert
        ContentValues contentValues = TestUtilities.createMovieValues()

        // Insert ContentValues into database and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)

        //Verify record inserted and record received
        assertTrue(rowId != -1)

        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)

        // Move the cursor to a valid database row
        assertTrue('Error: No record returned from the MovieMagic insert query',cursor.moveToFirst())

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord('Error: MovieMagic query data verification failed',cursor,contentValues)

        //Move the cursor to ensure there is only once record returned
        assertFalse('Error: more than one record returned by the MovieMagic query',cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()

    }

}