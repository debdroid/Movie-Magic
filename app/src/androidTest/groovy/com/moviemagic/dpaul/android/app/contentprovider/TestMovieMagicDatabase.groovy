package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.test.AndroidTestCase
import android.util.Log
import com.moviemagic.dpaul.android.app.TestUtilities
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicDatabase extends AndroidTestCase {

    //public static final String LOG_TAG = TestDb.class.getSimpleName()

    private static final long TEST_FOREIGN_KEY = 1

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

        // have we created all the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

        assertTrue('Error: This means that the database has not been created correctly',c.moveToFirst())

        // verify that all the tables have been created.
        //Create a list with all the table names
        def tables = []
        tables << MovieMagicContract.MovieBasicInfo.TABLE_NAME
        tables << MovieMagicContract.MovieCast.TABLE_NAME
        tables << MovieMagicContract.MovieCrew.TABLE_NAME
        tables << MovieMagicContract.MovieImage.TABLE_NAME
        tables << MovieMagicContract.MovieVideo.TABLE_NAME
        tables << MovieMagicContract.MovieCollection.TABLE_NAME
        tables << MovieMagicContract.MovieReview.TABLE_NAME
        tables << MovieMagicContract.MovieReleaseDate.TABLE_NAME
        tables << MovieMagicContract.MoviePersonInfo.TABLE_NAME
        tables << MovieMagicContract.MoviePersonCast.TABLE_NAME
        tables << MovieMagicContract.MoviePersonCrew.TABLE_NAME
        // System creates another metadata table "android_metadata" which stores db version info
        // So at the end of the following loop, the tables list should be empty
        // verify that the tables have been created
        for(i in 1..c.getCount()) {
            tables.remove(c.getString(0))
            c.moveToNext()
        }

        // if this fails, it means that the database doesn't contain all the required tables
        assertTrue('Error: The database does not contain all the tables', tables.isEmpty())

        // Now, do our tables contain the correct columns?
        //Check all the tables one at a time
        //Check the movie_basic_info table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieBasicInfo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_basic_info table information.',c.moveToFirst())
        // Build a list of all of the columns
        def columeList = []
        columeList << MovieMagicContract.MovieBasicInfo._ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_GENRE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_WATCHED
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_WISH_LIST
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_FAVOURITE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_COLLECTION
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_EXPORTED
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_1
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_2
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_3
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_4
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_5

        int columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_basic_info does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_basic_info table does not contain all the fields',columeList.isEmpty())

        //Check the movie_cast table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCast.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_cast table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCast._ID
        columeList << MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_CREDIT_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ORDER
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_cast table does not contain all the fields',columeList.isEmpty())

        //Check the movie_crew table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCrew.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_crew table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCrew._ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_CREDIT_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_DEPARTMENT
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_JOB
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_crew table does not contain all the fields',columeList.isEmpty())

        //Check the movie_image table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieImage.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_image table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieImage._ID
        columeList << MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_HEIGHT
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_WIDTH
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_image does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_image table does not contain all the fields',columeList.isEmpty())

        //Check the movie_video table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieVideo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_video table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieVideo._ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_NAME
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_SIZE
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_video does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_video table does not contain all the fields',columeList.isEmpty())

        //Check the movie_collection table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCollection.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_collection table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCollection._ID
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_OVERVIEW
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_POSTER_PATH
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_collection does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_collection table does not contain all the fields',columeList.isEmpty())

        //Check the movie_review table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieReview.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_review table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieReview._ID
        columeList << MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_URL

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_review does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_review table does not contain all the fields',columeList.isEmpty())

        //Check the movie_release_date table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieReleaseDate.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_release_date table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieReleaseDate._ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_LANGUAGE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_NOTE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_TYPE

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_release_date does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_release_date table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_info table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonInfo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_info table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonInfo._ID
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ALSO_KNOWN_AS
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIOGRAPHY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIRTHDAY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_DEATHDAY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_HOMEPAGE
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PLACE_OF_BIRTH
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PROFILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_info does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_info table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_cast table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonCast.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_cast table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonCast._ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CREDIT_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_TITLE
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_RELEASE_DATE
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_cast table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_crew table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonCrew.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_crew table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonCrew._ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_CREDIT_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_DEPARTMENT
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_TITLE
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_RELEASE_DATE
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE

        columnNameIndex = c.getColumnIndex('name')
        for(i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_crew does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_crew table does not contain all the fields',columeList.isEmpty())

        //Close the database
        db.close()
    }

    void testMovieBasicInfoTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieValues()
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieBasicInfo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieBasicInfo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieBasicInfo.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieCastTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieCastValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCast.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieCast.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCast.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCast.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCast.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieCrewTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieCrewValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCrew.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieCrew.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCrew.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCrew.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCrew.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieImageTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieImageValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieImage.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieImage.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieImage.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieImage.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieImage.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieVideoTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieVideoValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieVideo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieVideo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieVideo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieVideo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieVideo.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieCollectionTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieCollectionValues()
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCollection.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieCollection.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCollection.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCollection.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCollection.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieReviewTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieReviewValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReview.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieReview.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieReview.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieReview.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieReview.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMovieReleaseDateTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieReleaseDateValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieReleaseDate.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieReleaseDate.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieReleaseDate.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMoviePersonInfoTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMoviePersonInfoValues()
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonInfo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonInfo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonInfo.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMoviePersonCastTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMoviePersonCastValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonCast.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonCast.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonCast.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMoviePersonCrewTable() {
        // First step: Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMoviePersonCrewValues(TEST_FOREIGN_KEY)
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue(rowId != -1)
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonCrew.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonCrew.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonCrew.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }
}