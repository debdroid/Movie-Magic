package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicProvider extends ContentProvider {
    private static final String LOG_TAG = MovieMagicProvider.class.getSimpleName()

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher()
    private MovieMagicDbHelper mOpenHelper

    static final int MOVIE_BASIC_INFO = 101
    //To access a single item we shall use the primary key (_ID) of movie_basic_info
    static final int MOVIE_BASIC_INFO_WITH_MOVIE_ID = 102
    static final int MOVIE_BASIC_INFO_WITH_CATEGORY = 103
    static final int MOVIE_CAST = 104
    static final int MOVIE_CAST_WITH_MOVIE_ID = 105
    static final int MOVIE_CREW = 106
    static final int MOVIE_CREW_WITH_MOVIE_ID = 107
    static final int MOVIE_IMAGE = 108
    static final int MOVIE_IMAGE_WITH_MOVIE_ID = 109
    static final int MOVIE_VIDEO = 110
    static final int MOVIE_VIDEO_WITH_MOVIE_ID = 111
    static final int MOVIE_COLLECTION = 112
    static final int MOVIE_COLLECTION_WITH_COLECTION_ID = 113
    static final int MOVIE_REVIEW = 114
    static final int MOVIE_REVIEW_WITH_MOVIE_ID = 115
    static final int MOVIE_RELEASE_DATE = 116
    static final int MOVIE_RELEASE_DATE_WITH_MOVIE_ID = 117
    static final int MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO = 118
    static final int MOVIE_PERSON_INFO = 119
    static final int MOVIE_PERSON_INFO_WITH_PERSON_ID = 120
    static final int MOVIE_PERSON_CAST = 121
    static final int MOVIE_PERSON_CAST_WITH_PERSON_ID = 122
    static final int MOVIE_PERSON_CREW = 123
    static final int MOVIE_PERSON_CREW_WITH_PERSON_ID = 124

    private static final SQLiteQueryBuilder sMovieMagicQueryBuilder

    static {
        sMovieMagicQueryBuilder = new SQLiteQueryBuilder()
    }

    //movie_basic_info.movie_id = ?
    private static final String sMovieBasicInfoWithMovieIdSelection =
            "$MovieMagicContract.MovieBasicInfo.TABLE_NAME.$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? "

    //movie_basic_info.movie_category = ?
    private static final String sMovieBasicInfoWithCategorySelection =
            "$MovieMagicContract.MovieBasicInfo.TABLE_NAME.$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? "

    //movie_cast.cast_orig_movie_id = ?
    private static final String sMovieCastWithMovieIdSelection =
            "$MovieMagicContract.MovieCast.TABLE_NAME.$MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID = ? "

    //movie_crew.crew_orig_movie_id = ?
    private static final String sMovieCrewWithMovieIdSelection =
            "$MovieMagicContract.MovieCrew.TABLE_NAME.$MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID = ? "

    //movie_image.image_orig_movie_id = ?
    private static final String sMovieImageWithMovieIdSelection =
            "$MovieMagicContract.MovieImage.TABLE_NAME.$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = ? "

    //movie_video.video_orig_movie_id = ?
    private static final String sMovieVideoWithMovieIdSelection =
            "$MovieMagicContract.MovieVideo.TABLE_NAME.$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = ? "

    //movie_collection.collection_id = ?
    private static final String sMovieCollectionWithCollectionIdSelection =
            "$MovieMagicContract.MovieCollection.TABLE_NAME.$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID = ? "

    //movie_review.review_orig_movie_id = ?
    private static final String sMovieReviewWithMovieIdSelection =
            "$MovieMagicContract.MovieReview.TABLE_NAME.$MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID = ? "

    //movie_release_date.release_orig_movie_id = ?
    private static final String sMovieReleaseWithMovieIdSelection =
            "$MovieMagicContract.MovieReleaseDate.TABLE_NAME.$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? "

    //movie_release_date.release_orig_movie_id = ? and release_iso_country = ?
    private static final String sMovieReleaseWithMovieIdAndCountryISOSelection =
            "$MovieMagicContract.MovieReleaseDate.TABLE_NAME.$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? " +
                    " and $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY = ?"

    //movie_person_info.person_id = ?
    private static final String sMoviePersonInfoWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonInfo.TABLE_NAME.$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID = ? "

    //movie_person_cast.person_cast_orig_person_id = ?
    private static final String sMoviePersonCastWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonCast.TABLE_NAME.$MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID = ? "

    //movie_person_crew.person_crew_orig_person_id = ?
    private static final String sMoviePersonCrewWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonCrew.TABLE_NAME.$MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID = ? "

    //To get data from movie_basic_info where movie_basic_info._id = ?
    private Cursor getMovieBasicInfoByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieBasicInfoWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_basic_info where movie_basic_info.movie_category = ?
    private Cursor getMovieBasicInfoByMovieCategory(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        String[] movieId = [MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieUri(uri)]

        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieBasicInfoWithCategorySelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_cast where movie_cast.cast_orig_movie_id = ?
    private Cursor getMovieCastByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCast.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieCast.getMovieIdFromMovieCastUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCastWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_crew where movie_crew.crew_orig_movie_id = ?
    private Cursor getMovieCrewByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCrew.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieCrew.getMovieIdFromMovieCrewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCrewWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_image where movie_image.image_orig_movie_id = ?
    private Cursor getMovieImageByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieImage.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieImage.getMovieIdFromMovieImageUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieImageWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_video where movie_video.video_orig_movie_id = ?
    private Cursor getMovieVideoByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieVideo.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieVideo.getMovieIdFromMovieVideoUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieVideoWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_collection where movie_collection.collection_id = ?
    private Cursor getMovieCollectionByCollectionId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCollection.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieCollection.getCollectionIdFromMovieCollectionUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCollectionWithCollectionIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_review where movie_review.review_orig_movie_id = ?
    private Cursor getMovieReviewByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReview.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieReview.getMovieIdFromMovieReviewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReviewWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_release_date where movie_release_date.release_orig_movie_id = ?
    private Cursor getMovieReleaseByMovieId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReleaseDate.TABLE_NAME")
        String[] movieId = [Integer.toString(MovieMagicContract.MovieReleaseDate.getMovieIdFromMovieReleaseUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReleaseWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_release_date where movie_release_date.release_orig_movie_id = ? and release_iso_country = ?
    private Cursor getMovieReleaseByMovieIdAndCountryISO(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReleaseDate.TABLE_NAME")
        String movieId = Integer.toString(MovieMagicContract.MovieReleaseDate.getMovieIdFromMovieReleaseUri(uri))
            String countryISO = MovieMagicContract.MovieReleaseDate.getCountryIsoFromMovieReleaseUri(uri)
            String[] selArgs = [movieId,countryISO]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReleaseWithMovieIdAndCountryISOSelection,
                selArgs,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_info where movie_person_info.person_id = ?
    private Cursor getMoviePersonInfoByPersonId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonInfo.TABLE_NAME")
        String[] personId = [Integer.toString(MovieMagicContract.MoviePersonInfo.getPersonIdFromMoviePersonInfoUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonInfoWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_cast where movie_person_cast.person_cast_orig_person_id = ?
    private Cursor getMoviePersonCastByPersonId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonCast.TABLE_NAME")
        String[] personId = [Integer.toString(MovieMagicContract.MoviePersonCast.getPersonIdFromMoviePersonCastUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonCastWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_crew where movie_person_crew.person_crew_orig_person_id = ?
    private Cursor getMoviePersonCrewByPersonId(Uri uri, String[] projection, String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonCrew.TABLE_NAME")
        String[] personId = [Integer.toString(MovieMagicContract.MoviePersonCrew.getPersonIdFromMoviePersonCrewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonCrewWithPersonIdSelection,
                personId,
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
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_BASIC_INFO,MOVIE_BASIC_INFO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_BASIC_INFO/#",MOVIE_BASIC_INFO_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_BASIC_INFO/*",MOVIE_BASIC_INFO_WITH_CATEGORY)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_CAST,MOVIE_CAST)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_CAST/#",MOVIE_CAST_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_CREW,MOVIE_CREW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_CREW/#",MOVIE_CREW_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_IMAGE,MOVIE_IMAGE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_IMAGE/#",MOVIE_IMAGE_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_VIDEO,MOVIE_VIDEO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_VIDEO/#",MOVIE_VIDEO_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_COLLECTION,MOVIE_COLLECTION)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_COLLECTION/#",MOVIE_COLLECTION_WITH_COLECTION_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_REVIEW,MOVIE_REVIEW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_REVIEW/#",MOVIE_REVIEW_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_RELEASE_DATE,MOVIE_RELEASE_DATE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_RELEASE_DATE/#",MOVIE_RELEASE_DATE_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_RELEASE_DATE/#/*",MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_INFO,MOVIE_PERSON_INFO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_INFO/#",MOVIE_PERSON_INFO_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_CAST,MOVIE_PERSON_CAST)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_CAST/#",MOVIE_PERSON_CAST_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_CREW,MOVIE_PERSON_CREW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_CREW/#",MOVIE_PERSON_CREW_WITH_PERSON_ID)

        // 3) Return the new matcher!
        return uriMatcher
    }

    @Override
    boolean onCreate() {
        mOpenHelper = new MovieMagicDbHelper(getContext())
        return true
    }

    @Override
    String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri)

        switch (match) {
            case MOVIE_BASIC_INFO:
                return MovieMagicContract.MovieBasicInfo.CONTENT_TYPE
            case MOVIE_BASIC_INFO_WITH_MOVIE_ID:
                return MovieMagicContract.MovieBasicInfo.CONTENT_ITEM_TYPE
            case MOVIE_BASIC_INFO_WITH_CATEGORY:
                return MovieMagicContract.MovieBasicInfo.CONTENT_TYPE
            case MOVIE_CAST:
                return MovieMagicContract.MovieCast.CONTENT_TYPE
            case MOVIE_CAST_WITH_MOVIE_ID:
                return MovieMagicContract.MovieCast.CONTENT_TYPE
            case MOVIE_CREW:
                return MovieMagicContract.MovieCrew.CONTENT_TYPE
            case MOVIE_CREW_WITH_MOVIE_ID:
                return MovieMagicContract.MovieCrew.CONTENT_TYPE
            case MOVIE_IMAGE:
                return MovieMagicContract.MovieImage.CONTENT_TYPE
            case MOVIE_IMAGE_WITH_MOVIE_ID:
                return MovieMagicContract.MovieImage.CONTENT_TYPE
            case MOVIE_VIDEO:
                return MovieMagicContract.MovieVideo.CONTENT_TYPE
            case MOVIE_VIDEO_WITH_MOVIE_ID:
                return MovieMagicContract.MovieVideo.CONTENT_TYPE
            case MOVIE_COLLECTION:
                return MovieMagicContract.MovieCollection.CONTENT_TYPE
            case MOVIE_COLLECTION_WITH_COLECTION_ID:
                return MovieMagicContract.MovieCollection.CONTENT_ITEM_TYPE
            case MOVIE_REVIEW:
                return MovieMagicContract.MovieReview.CONTENT_TYPE
            case MOVIE_REVIEW_WITH_MOVIE_ID:
                return MovieMagicContract.MovieReview.CONTENT_TYPE
            case MOVIE_RELEASE_DATE:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_PERSON_INFO:
                return MovieMagicContract.MoviePersonInfo.CONTENT_TYPE
            case MOVIE_PERSON_INFO_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonInfo.CONTENT_ITEM_TYPE
            case MOVIE_PERSON_CAST:
                return MovieMagicContract.MoviePersonCast.CONTENT_TYPE
            case MOVIE_PERSON_CAST_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonCast.CONTENT_TYPE
            case MOVIE_PERSON_CREW:
                return MovieMagicContract.MoviePersonCrew.CONTENT_TYPE
            case MOVIE_PERSON_CREW_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonCrew.CONTENT_TYPE
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
    }


    @Override
    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor
        switch(sUriMatcher.match(uri)) {
        // "/movie_basic_info/#"
            case MOVIE_BASIC_INFO_WITH_MOVIE_ID:
                retCursor = getMovieBasicInfoByMovieId(uri, projection, sortOrder)
                break
        // "/movie_basic_info/*"
            case MOVIE_BASIC_INFO_WITH_CATEGORY:
                retCursor = getMovieBasicInfoByMovieCategory(uri, projection, sortOrder)
                break
        // "/movie_basic_info"
            case MOVIE_BASIC_INFO:
                String table = MovieMagicContract.MovieBasicInfo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_cast/#"
            case MOVIE_CAST_WITH_MOVIE_ID:
                retCursor = getMovieCastByMovieId(uri,projection,sortOrder)
                break
        // "/movie_cast"
            case MOVIE_CAST:
                String table = MovieMagicContract.MovieCast.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_crew/#"
            case MOVIE_CREW_WITH_MOVIE_ID:
                retCursor = getMovieCrewByMovieId(uri,projection,sortOrder)
                break
        // "/movie_crew"
            case MOVIE_CREW:
                String table = MovieMagicContract.MovieCrew.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_image/#"
            case MOVIE_IMAGE_WITH_MOVIE_ID:
                retCursor = getMovieImageByMovieId(uri,projection,sortOrder)
                break
        // "/movie_image"
            case MOVIE_IMAGE:
                String table = MovieMagicContract.MovieImage.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_video/#"
            case MOVIE_VIDEO_WITH_MOVIE_ID:
                retCursor = getMovieVideoByMovieId(uri,projection,sortOrder)
                break
        // "/movie_video"
            case MOVIE_VIDEO:
                String table = MovieMagicContract.MovieVideo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_collection/#"
            case MOVIE_COLLECTION_WITH_COLECTION_ID:
                retCursor = getMovieCollectionByCollectionId(uri,projection,sortOrder)
                break
        // "/movie_collection"
            case MOVIE_COLLECTION:
                String table = MovieMagicContract.MovieCollection.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_review/#"
            case MOVIE_REVIEW_WITH_MOVIE_ID:
                retCursor = getMovieReviewByMovieId(uri,projection,sortOrder)
                break
        // "/movie_review"
            case MOVIE_REVIEW:
                String table = MovieMagicContract.MovieReview.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_release_date/#"
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID:
                retCursor = getMovieReleaseByMovieId(uri,projection,sortOrder)
                break
        // "/movie_release_date/#/*"
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO:
                retCursor = getMovieReleaseByMovieIdAndCountryISO(uri,projection,sortOrder)
                break
        // "/movie_release_date"
            case MOVIE_RELEASE_DATE:
                String table = MovieMagicContract.MovieReleaseDate.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_info/#"
            case MOVIE_PERSON_INFO_WITH_PERSON_ID:
                retCursor = getMoviePersonInfoByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_info"
            case MOVIE_PERSON_INFO:
                String table = MovieMagicContract.MoviePersonInfo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_cast/#"
            case MOVIE_PERSON_CAST_WITH_PERSON_ID:
                retCursor = getMoviePersonCastByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_cast"
            case MOVIE_PERSON_CAST:
                String table = MovieMagicContract.MoviePersonCast.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_crew/#"
            case MOVIE_PERSON_CREW_WITH_PERSON_ID:
                retCursor = getMoviePersonCrewByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_crew"
            case MOVIE_PERSON_CREW:
                String table = MovieMagicContract.MoviePersonCrew.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri)
        return retCursor
    }

    private Cursor queryHelperMethod(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //This is another way for writing the query ()
        return mOpenHelper.getReadableDatabase().query(table,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder)
    }

    @Override
    Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        Uri returnUri

        switch (match) {
            case MOVIE_BASIC_INFO:
                convertDate(values)
                long _id = db.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieBasicInfo.buildMovieUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_CAST:
                long _id = db.insert(MovieMagicContract.MovieCast.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCast.buildMovieCastUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_CREW:
                long _id = db.insert(MovieMagicContract.MovieCrew.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCrew.buildMovieCrewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_IMAGE:
                long _id = db.insert(MovieMagicContract.MovieImage.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieImage.buildMovieImageUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_VIDEO:
                long _id = db.insert(MovieMagicContract.MovieVideo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieVideo.buildMovieVideoUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_COLLECTION:
                long _id = db.insert(MovieMagicContract.MovieCollection.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCollection.buildMovieCollectionUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_REVIEW:
                long _id = db.insert(MovieMagicContract.MovieReview.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieReview.buildMovieReviewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_RELEASE_DATE:
                long _id = db.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieReleaseDate.buildMovieReleasewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_INFO:
                long _id = db.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_CAST:
                long _id = db.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonCast.buildMoviePersonCastUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_CREW:
                long _id = db.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        getContext().getContentResolver().notifyChange(uri, null)
        //Was facing issues while accessing database during inserting data into different tables,
        //found in one Stackoverflow that db.close shouldn't be used as content provider handles that
        //automatically, so commenting this here in other places!!
        //db.close()
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
            case MOVIE_BASIC_INFO:
                count = db.delete(MovieMagicContract.MovieBasicInfo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_CAST:
                count = db.delete(MovieMagicContract.MovieCast.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_CREW:
                count = db.delete(MovieMagicContract.MovieCrew.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_IMAGE:
                count = db.delete(MovieMagicContract.MovieImage.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_VIDEO:
                count = db.delete(MovieMagicContract.MovieVideo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_COLLECTION:
                count = db.delete(MovieMagicContract.MovieCollection.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_REVIEW:
                count = db.delete(MovieMagicContract.MovieReview.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_RELEASE_DATE:
                count = db.delete(MovieMagicContract.MovieReleaseDate.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_INFO:
                count = db.delete(MovieMagicContract.MoviePersonInfo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_CAST:
                count = db.delete(MovieMagicContract.MoviePersonCast.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_CREW:
                count = db.delete(MovieMagicContract.MoviePersonCrew.TABLE_NAME, selection, selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            //db.close()
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
            case MOVIE_BASIC_INFO:
                convertDate(values)
                count = db.update(MovieMagicContract.MovieBasicInfo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_CAST:
                count = db.update(MovieMagicContract.MovieCast.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_CREW:
                count = db.update(MovieMagicContract.MovieCrew.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_IMAGE:
                count = db.update(MovieMagicContract.MovieImage.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_VIDEO:
                count = db.update(MovieMagicContract.MovieVideo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_COLLECTION:
                count = db.update(MovieMagicContract.MovieCollection.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_REVIEW:
                count = db.update(MovieMagicContract.MovieReview.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_RELEASE_DATE:
                count = db.update(MovieMagicContract.MovieReleaseDate.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_INFO:
                count = db.update(MovieMagicContract.MoviePersonInfo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_CAST:
                count = db.update(MovieMagicContract.MoviePersonCast.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_CREW:
                count = db.update(MovieMagicContract.MoviePersonCrew.TABLE_NAME,values,selection,selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            //db.close()
        }
        return count
    }

    @Override
    int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        switch (match) {
            case MOVIE_BASIC_INFO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        convertDate(value)
                        long _id = db.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, value)
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
            case MOVIE_CAST:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCast.TABLE_NAME, null, value)
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
            case MOVIE_CREW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCrew.TABLE_NAME, null, value)
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
            case MOVIE_IMAGE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieImage.TABLE_NAME, null, value)
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
            case MOVIE_VIDEO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieVideo.TABLE_NAME, null, value)
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
            case MOVIE_COLLECTION:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCollection.TABLE_NAME, null, value)
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
            case MOVIE_REVIEW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieReview.TABLE_NAME, null, value)
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
            case MOVIE_RELEASE_DATE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, value)
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
            case MOVIE_PERSON_INFO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, value)
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
            case MOVIE_PERSON_CAST:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, value)
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
            case MOVIE_PERSON_CREW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, value)
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
    /**
        Covert the movie release date string to numeric value
     */
    private void convertDate(ContentValues values) {
        // Covert the movie release date
        if (values.containsKey(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)) {
            String movieReleaseDate = values.getAsString(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)
            values.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE, MovieMagicContract.convertMovieReleaseDate(movieReleaseDate))
        }
    }
}