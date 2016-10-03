package com.moviemagic.dpaul.android.app.backgroundmodules

import groovy.transform.CompileStatic

@CompileStatic
class GlobalStaticVariables {
    //Static variables for movie list type
    public static final String MOVIE_LIST_TYPE_TMDB_PUBLIC = 'tmdb_public'
    public static final String MOVIE_LIST_TYPE_TMDB_USER = 'tmdb_user'
    public static final String MOVIE_LIST_TYPE_TMDB_SIMILAR = 'tmdb_similar'
    public static final String MOVIE_LIST_TYPE_TMDB_COLLECTION = 'tmdb_collection'
    public static final String MOVIE_LIST_TYPE_TMDB_PERSON = 'tmdb_person'
    public static final String MOVIE_LIST_TYPE_USER_LOCAL_LIST = 'user_local_list'
    public static final String MOVIE_LIST_TYPE_ORPHANED = 'orphaned_list'

    //Static variables for movie category
    public static final String MOVIE_CATEGORY_POPULAR = 'popular'  //tmdb popular category
    public static final String MOVIE_CATEGORY_TOP_RATED = 'top_rated' //tmdb top_ratedcategory
    public static final String MOVIE_CATEGORY_UPCOMING = 'upcoming' //tmdb upcoming category
    public static final String MOVIE_CATEGORY_NOW_PLAYING = 'now_playing' //tmdb now_playing category
    public static final String MOVIE_CATEGORY_SIMILAR = 'similar_category' //category to store similar movie, internal use only
    public static final String MOVIE_CATEGORY_COLLECTION = 'collection_category' //category to store collection movie, internal use only
    public static final String MOVIE_CATEGORY_PERSON = 'person_category' //category to store person cast & crew movie, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_FAVOURITE = 'tmdb_user_favourite_category' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_WATCH = 'tmdb_user_watch_category' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_RATED = 'tmdb_user_rated_category' //category to store tmdb user movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WATCHED = 'local_user_watched_category' //category to store user wathced movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WISH_LIST = 'local_user_wish_list_category' //category to store user wish list movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_FAVOURITE = 'local_user_favourite_category' //category to store user favourite movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_COLLECTION = 'local_user_collection_category' //category to store user collection movies, internal use only
    public static final String MOVIE_CATEGORY_ORPHANED = 'oprphaned_category' //category for orphaned records (not needed user list movie)

    //Static variables for TMDB URL and parameters
    public static final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
    public static final String TMDB_MOVIE_PATH = 'movie'
    public static final String TMDB_MOVIE_API_KEY = 'api_key'
    public static final String TMDB_MOVIE_PAGE = 'page'
    public static final String TMDB_APPEND_TO_RESPONSE_KEY = 'append_to_response'
    public static final String TMDB_MOVIE_APPEND_TO_RESPONSE_PARAM = 'similar,credits,images,videos,release_dates,reviews'
    public static final String TMDB_PERSON_APPEND_TO_RESPONSE_PARAM = 'movie_credits'
    public static final String TMDB_COLLECTION_PATH = 'collection'
    public static final String TMDB_PERSON_PATH = 'person'
    public static final String TMDB_PERSON_IMAGE_PATH = 'images'


    //Static variables for TMDB movie image url and parameters
    public static final String TMDB_IMAGE_BASE_URL = 'http://image.tmdb.org/t/p/'
    public static final String TMDB_IMAGE_SIZE_W185 = 'w185'
    public static final String TMDB_IMAGE_SIZE_W500 = 'w500'

    //IMDb URL - used to create the IMDB intent
    public static final String IMDB_BASE_MOVIE_TITLE_URL = 'http://www.imdb.com/title/'
    public static final String IMDB_BASE_PERSON_URL = 'http://www.imdb.com/name/'

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
    public static final String MOVIE_BASIC_INFO_CATEGORY = 'movie_basic_info_category'
    public static final String MOVIE_BASIC_INFO_ROW_ID = 'movie_basic_info_row_id'
    public static final String MOVIE_BASIC_INFO_COLL_ID = 'movie_basic_info_collection_id'
    public static final int MOVIE_MAGIC_FLAG_TRUE = 1
    public static final int MOVIE_MAGIC_FLAG_FALSE = 0
    public static final float MOVIE_MAGIC_ELEVATION = 4f
    public static final float MOVIE_MAGIC_ELEVATION_RESET = 0f
    public static final float MOVIE_MAGIC_ALPHA_FULL_OPAQUE = 1f
    public static final float MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT = 0.4f
    public static final String MOVIE_COLLECTION_ID = 'collection_id'
    public static final String MOVIE_BASIC_INFO_URI = 'movie_basic_info_uri'
    public static final String MOVIE_COLLECTION_URI = 'collection_uri'
    public static final String MOVIE_CATEGORY_AND_COLL_ID_URI = 'category_and_collection_id_uri'
    public static final String PICASSO_POSTER_IMAGE_TAG = 'picasso_poster_image_tag'
    public static final String COLLECTION_MOVIE_FRAGMENT_TAG = 'collection_movie_fragment_tag'
    public static final String MOVIE_PERSON_URI = 'collection_uri'
    public static final String IMAGE_VIEWER_IMAGE_PATH_ARRAY = 'imageviewer_image_path_array'
    public static final String IMAGE_VIEWER_TITLE = 'imageviewer_title'
    public static final String IMAGE_VIEWER_ADAPTER_POSITION = 'imageviewer_adapter_position'
    public static final String IMAGE_VIEWER_BACKDROP_IMAGE_FLAG = 'imageviewer_backdrop_image_flag'
}