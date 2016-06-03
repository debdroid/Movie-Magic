package com.moviemagic.dpaul.android.app.utility

import android.content.ContentValues
import android.util.Log
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieBasicInfo
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapter;
import groovy.transform.CompileStatic

//Since the json field is used dynamically, so this class is not compiled as CompileStatic
//@CompileStatic
class JsonParse {
    private static final String LOG_TAG = JsonParse.class.getSimpleName()

    static int getTotalPages(def jsonData) {
        int currentPage = jsonData.page
        int totalPage = jsonData.total_pages
        LogDisplay.callLog(LOG_TAG,"CurrentPage -> $currentPage",LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"TotalPage -> $totalPage",LogDisplay.JSON_PARSE_LOG_FLAG)
        return totalPage
    }

    static List<ContentValues> parseMovieListJson(def jsonData, String category, String movieListType) {
        List<ContentValues> movieList = []
        def cnt = jsonData.results.size() - 1
        //Ensure that the results is not Null
        if (jsonData.results) {
            for (i in 0..cnt) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.results[i].title}",LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieValue = new ContentValues()
                //if-else is used for all json fields for null safe
                if(jsonData.results[i].id)
                    movieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, jsonData.results[i].id)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, 0)
                if (jsonData.results[i].backdrop_path)
                    movieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, jsonData.results[i].backdrop_path)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, '')
                if(jsonData.results[i].original_title)
                    movieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, jsonData.results[i].original_title)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, '')
                if(jsonData.results[i].overview)
                    movieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, jsonData.results[i].overview)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, '')
                if(jsonData.results[i].release_date)
                    movieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, jsonData.results[i].release_date)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, '1900-01-01') //date format -> yyyy-mm-dd
                if (jsonData.results[i].poster_path)
                    movieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, jsonData.results[i].poster_path)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, '')
                if(jsonData.results[i].popularity)
                    movieValue.put(MovieBasicInfo.COLUMN_POPULARITY, jsonData.results[i].popularity)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_POPULARITY, 0.0)
                if(jsonData.results[i].title)
                    movieValue.put(MovieBasicInfo.COLUMN_TITLE, jsonData.results[i].title)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_TITLE, '')
                if(jsonData.results[i].video)
                    movieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, jsonData.results[i].video)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, 'false')
                if(jsonData.results[i].vote_average)
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, jsonData.results[i].vote_average)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, 0.0)
                if(jsonData.results[i].vote_count)
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, jsonData.results[i].vote_count)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, 0)
                if (jsonData.page)
                    movieValue.put(MovieBasicInfo.COLUMN_PAGE_NUMBER, jsonData.page)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_PAGE_NUMBER, 0)
                //category and movieListType are supplied in the program, so always null safe
                movieValue.put(MovieBasicInfo.COLUMN_MOVIE_CATEGORY, category)
                movieValue.put(MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, movieListType)

                movieList << movieValue
            }
        }
        return movieList
    }
}