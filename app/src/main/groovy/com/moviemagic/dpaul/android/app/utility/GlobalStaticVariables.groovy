package com.moviemagic.dpaul.android.app.utility

import groovy.transform.CompileStatic

@CompileStatic
class GlobalStaticVariables {
    //Static variables for movie list type
    public static final String MOVIE_LIST_TYPE_TMDB_PUBLIC = 'tmdb_public'
    public static final String MOVIE_LIST_TYPE_TMDB_USER = 'tmdb_user'
    public static final String MOVIE_LIST_TYPE_TMDB_SIMILAR = 'tmdb_similar'
    public static final String MOVIE_LIST_TYPE_USER_LOCAL_LIST = 'user_local_list'

    //Static variables for movie category
    public static final String MOVIE_CATEGORY_POPULAR = 'popular'  //tmdb popular category
    public static final String MOVIE_CATEGORY_TOP_RATED = 'top_rated' //tmdb top_ratedcategory
    public static final String MOVIE_CATEGORY_UPCOMING = 'upcoming' //tmdb upcoming category
    public static final String MOVIE_CATEGORY_NOW_PLAYING = 'now_playing' //tmdb now_playing category
    public static final String MOVIE_CATEGORY_SIMILAR = 'similar' //category to store similar movie, internal use only
    public static final String MOVIE_CATEGORY_USER = 'user' //category to store user movies, internal use only

    //Static variables for TMDB URL and parameters
    public static final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
    public static final String TMDB_MOVIE_PATH = 'movie'
    public static final String TMDB_MOVIE_API_KEY = 'api_key'
    public static final String TMDB_MOVIE_PAGE = 'page'
    public static final String TMDB_APPEND_TO_RESPONSE_KEY = 'append_to_response'
    public static final String TMDB_APPEND_TO_RESPONSE_PARAM = 'similar,credits,images,videos,release_dates,reviews'

    //Static variables for TMDB movie image url and parameters
    public static final String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/"
    public static final String TMDB_IMAGE_SIZE_W185 = "w185"
    public static final String TMDB_IMAGE_SIZE_W500 = "w500"

    //Misc variables
    public static final String IMAGE_TYPE_BACKDROP = 'backdrop'
    public static final String IMAGE_TYPE_POSTER = 'poster'
    public static final String MOVIE_BASIC_INFO_MOVIE_ID_URI = 'movie_basic_info_movie_id_uri'


}