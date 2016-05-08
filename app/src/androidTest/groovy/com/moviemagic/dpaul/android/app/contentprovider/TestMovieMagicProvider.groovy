package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.TestUtilities
import com.moviemagic.dpaul.android.app.javamodule.TestContentObserverUtilities
import groovy.transform.CompileStatic
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieBasicInfo


@CompileStatic
class TestMovieMagicProvider extends AndroidTestCase {

    static final String TEST_UPDATED_MOVIE_CATEGORY = 'upcoming'
    static final String TEST_RELEASE_DATE = '2015-08-13'

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieBasicInfo.CONTENT_URI,
                null,
                null
        )

        Cursor cursor = mContext.getContentResolver().query(
                MovieBasicInfo.CONTENT_URI,
                null,
                null,
                null,
                null
        )
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount())
        cursor.close()
    }

    /*
        This helper function deletes all records from the database tables using the database,
        specially needed when the provider has not implemented the delete function yet
    */
    void deleteAllRecordsFromDB() {
        MovieMagicDbHelper dbHelper = new MovieMagicDbHelper(mContext)
        SQLiteDatabase db = dbHelper.getWritableDatabase()
        db.delete(MovieBasicInfo.TABLE_NAME, null, null)
        db.close()
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp()
        //Now that delete function is implemented, so no more required
        //deleteAllRecordsFromDB()
        //Instead use this one
        deleteAllRecordsFromProvider()
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager()

        // We define the component name based on the package name from the context and the
        // MovieMagicProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),MovieMagicProvider.class.getName())
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0)

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieMagicrProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieMagicContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieMagicContract.CONTENT_AUTHORITY)
        } catch (PackageManager.NameNotFoundException e) {
            // If reaches here it means the provider isn't registered correctly.
            assertTrue("Error: MovieMagicProvider not registered at " + mContext.getPackageName(),
                    false)
        }
    }

    /*
       This test doesn't touch the database.  It verifies that the ContentProvider returns
       the correct type for each type of URI that it can handle.
    */
    void testGetType() {
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/
        def type = mContext.getContentResolver().getType(MovieBasicInfo.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_basic_info
        assertEquals("Error: the MovieBasicInfo CONTENT_URI should return MovieBasicInfo.CONTENT_TYPE",
                MovieBasicInfo.CONTENT_TYPE, type)

        int testMovieId = 43546
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/43546
        type = mContext.getContentResolver().getType(MovieBasicInfo.buildMovieWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_basic_info/43546
        assertEquals("Error: the MovieBasicInfo CONTENT_URI with MovieId should return MovieBasicInfo.CONTENT_ITEM_TYPE",
                MovieBasicInfo.CONTENT_ITEM_TYPE, type)

        String testMovieCategory = 'popular'
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/popular
        type = mContext.getContentResolver().getType(MovieBasicInfo.buildMovieWithMovieCategory(testMovieCategory))
        // vnd.android.cursor.item/com.moviemagic.dpaul.android.app/movie_basic_info/popular
        assertEquals("Error: the MovieBasicInfo CONTENT_URI with category should return MovieBasicInfo.CONTENT_TYPE",
                MovieBasicInfo.CONTENT_TYPE, type)
    }

    /*
    This test uses the database directly to insert and then uses the ContentProvider to
    read out the data.
    */
    void testBasicMovieMagicQuery() {
        // insert our test records into the database
        MovieMagicDbHelper dbHelper = new MovieMagicDbHelper(mContext)
        SQLiteDatabase db = dbHelper.getWritableDatabase()
        ContentValues testValues = TestUtilities.createMovieValues()
        long movieMagicRowId = db.insert(MovieBasicInfo.TABLE_NAME, null, testValues)
        assertTrue("Unable to Insert MovieMagic data into the Database", movieMagicRowId != -1)
        db.close()
        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieBasicInfo.CONTENT_URI,
                null,
                null,
                null,
                null
        )
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieMagicQuery", movieCursor, testValues)
    }

    /*
        This test uses the provider to insert and then update the data.
     */
    void testUpdateMovieMagic() {
        // Create a new record
        ContentValues values = TestUtilities.createMovieValues()
        //TestUtilities uses the date in long format which is used to test raw database and table
        //but this test goes via provider where the expected date is yyyy-mm-dd format, so overriding the date
        values.put(MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)
        Uri movieMagicUri = mContext.getContentResolver().insert(MovieBasicInfo.CONTENT_URI, values)
        long movieMagicRowId = ContentUris.parseId(movieMagicUri)

        // Verify a valid insertion
        assertTrue(movieMagicRowId != -1)

        //Now update a field
        ContentValues updatedValues = new ContentValues(values)
        updatedValues.put(MovieBasicInfo.COLUMN_MOVIE_CATEGORY, TEST_UPDATED_MOVIE_CATEGORY)
        //again update the date as provider expects it in yyyy-mm-dd format
        updatedValues.put(MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieMagicCursor = mContext.getContentResolver().query(MovieBasicInfo.CONTENT_URI, null, null, null, null)
        TestContentObserverUtilities.TestContentObserver tco = TestContentObserverUtilities.getTestContentObserver()
        movieMagicCursor.registerContentObserver(tco)

        String[] testMovieId =  [Integer.toString(TestUtilities.TEST_MOVIE_ID)]
        int count = mContext.getContentResolver().update(
                MovieBasicInfo.CONTENT_URI,
                updatedValues, MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
                testMovieId,
                )
        assertEquals(count, 1)

        // Test to make sure our observer is called.
        // If the code is failing here, it means that the content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        movieMagicCursor.unregisterContentObserver(tco)
        movieMagicCursor.close()

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieBasicInfo.CONTENT_URI,
                null,   // projection
                "$MovieBasicInfo.COLUMN_MOVIE_CATEGORY = '$TEST_UPDATED_MOVIE_CATEGORY'",
                null,   // Values for the "where" clause
                null    // sort order
        )

        TestUtilities.validateCursor("testUpdateMovieMagic.  Error validating MovieMagic entry update.",
                cursor, updatedValues)
        cursor.close()
    }

    /*
        Test the insert and all types of queries of the MovieMagic provider
     */
    void testInsertMovieMagicProvider() {
        ContentValues testValues = TestUtilities.createMovieValues()
        //TestUtilities uses the date in long format which is used to test raw database and table
        //but this test goes via provider where the expected date is yyyy-mm-dd format, so overriding the date
        testValues.put(MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestContentObserverUtilities.TestContentObserver tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieBasicInfo.CONTENT_URI, true, tco)

        //Insert the test record
        Uri movieMagicUri = mContext.getContentResolver().insert(MovieBasicInfo.CONTENT_URI, testValues)

        // Did the content observer get called?  If this fails, then insert
        // isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)

        long movieMagicRowId = ContentUris.parseId(movieMagicUri)

        // Verify a valid insertion
        assertTrue(movieMagicRowId != -1)

        // Data's inserted. Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieBasicInfo.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        )

        TestUtilities.validateCursor("testInsertMovieMagicProvider. Error validating MovieMagicEntry.",
                cursor, testValues)

        // Get the data using MovieId to ensure it works
        cursor = mContext.getContentResolver().query(
                MovieBasicInfo.buildMovieWithMovieId(TestUtilities.TEST_MOVIE_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        )
        TestUtilities.validateCursor("testInsertMovieMagicProvider.  Error validating MovieWithId data.",
                cursor, testValues)

        // Get the data using Category to ensure it works
        cursor = mContext.getContentResolver().query(
                MovieBasicInfo.buildMovieWithMovieCategory(TestUtilities.TEST_MOVIE_CATEGORY),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        )
        TestUtilities.validateCursor("testInsertMovieMagicProvider.  Error validating MovieWithCategory data.",
                cursor, testValues)
        cursor.close()
    }

    /*
    Test that provider can delete the record properly
     */
    void testDeleteRecords() {
        //First insert a record before delete
        testInsertMovieMagicProvider()

        // Register a content observer for our data delete.
        TestContentObserverUtilities.TestContentObserver tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieBasicInfo.CONTENT_URI, true, tco)

        deleteAllRecordsFromProvider()

        // Did the content observer get called?  If this fails, then insert
        // isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
    }

}