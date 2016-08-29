package com.moviemagic.dpaul.android.app.utility

import groovy.transform.CompileStatic

@CompileStatic
class GlobalStaticVariables {
    //Static variables for movie list type
    public static final String MOVIE_LIST_TYPE_TMDB_PUBLIC = 'tmdb_public'
    public static final String MOVIE_LIST_TYPE_TMDB_USER = 'tmdb_user'
    public static final String MOVIE_LIST_TYPE_TMDB_SIMILAR = 'tmdb_similar'
    public static final String MOVIE_LIST_TYPE_USER_LOCAL_LIST = 'user_local_list'
    public static final String MOVIE_LIST_TYPE_ORPHANED = 'orphaned_list'

    //Static variables for movie category
    public static final String MOVIE_CATEGORY_POPULAR = 'popular'  //tmdb popular category
    public static final String MOVIE_CATEGORY_TOP_RATED = 'top_rated' //tmdb top_ratedcategory
    public static final String MOVIE_CATEGORY_UPCOMING = 'upcoming' //tmdb upcoming category
    public static final String MOVIE_CATEGORY_NOW_PLAYING = 'now_playing' //tmdb now_playing category
    public static final String MOVIE_CATEGORY_SIMILAR = 'similar' //category to store similar movie, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_FAVOURITE = 'tmdb_user_favourite' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_WATCH = 'tmdb_user_watch' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_RATED = 'tmdb_user_rated' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WATCHED = 'local_user_watched' //category to store user wathced movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WISH_LIST = 'local_user_wish_list' //category to store user wish list movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_FAVOURITE = 'local_user_favourite' //category to store user favourite movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_COLLECTION = 'local_user_collection' //category to store user collection movies, internal use only
    public static final String MOVIE_CATEGORY_ORPHANED= 'oprphaned_category'

    //Static variables for TMDB URL and parameters
    public static final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
    public static final String TMDB_MOVIE_PATH = 'movie'
    public static final String TMDB_MOVIE_API_KEY = 'api_key'
    public static final String TMDB_MOVIE_PAGE = 'page'
    public static final String TMDB_APPEND_TO_RESPONSE_KEY = 'append_to_response'
    public static final String TMDB_APPEND_TO_RESPONSE_PARAM = 'similar,credits,images,videos,release_dates,reviews'

    //Static variables for TMDB movie image url and parameters
    public static final String TMDB_IMAGE_BASE_URL = 'http://image.tmdb.org/t/p/'
    public static final String TMDB_IMAGE_SIZE_W185 = 'w185'
    public static final String TMDB_IMAGE_SIZE_W500 = 'w500'

    //IMDb URL - used to create the IMDB intent
    public static final String IMDB_BASE_URL = 'http://www.imdb.com/title/'

    //Static variable for user list
    public static final String USER_LIST_WATCHED = 'watched'
    public static final String USER_LIST_WISH_LIST = 'wish_list'
    public static final String USER_LIST_FAVOURITE = 'favourite'
    public static final String USER_LIST_COLLECTION = 'collection'
    public static final String USER_LIST_USER_RATING = 'user_rating'
    public static final String USER_LIST_ADD_FLAG = 'use_list_add'
    public static final String USER_LIST_REMOVE_FLAG = 'user_list_remove'
    public static final String USER_RATING_ADD_FLAG = 'rating_add'
    public static final String USER_RATING_REMOVE_FLAG = 'rating_remove'

    //Misc variables
    public static final String IMAGE_TYPE_BACKDROP = 'backdrop'
    public static final String IMAGE_TYPE_POSTER = 'poster'
    public static final String MOVIE_BASIC_INFO_MOVIE_ID = 'movie_basic_info_movie_id'
    public static final String MOVIE_BASIC_INFO_ROW_ID = 'movie_basic_info_row_id'
    public static final int MOVIE_MAGIC_FLAG_TRUE = 1
    public static final int MOVIE_MAGIC_FLAG_FALSE = 0
    public static final float MOVIE_MAGIC_ELEVATION = 4f
    public static final float MOVIE_MAGIC_ELEVATION_RESET = 0f
    public static final float MOVIE_MAGIC_ALPHA_FULL_OPAQUE = 1f
    public static final float MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT = 0.4f

}