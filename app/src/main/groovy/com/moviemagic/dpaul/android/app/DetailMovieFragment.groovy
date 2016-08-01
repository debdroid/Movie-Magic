package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v17.leanback.widget.HorizontalGridView
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.MovieCastAdapter
import com.moviemagic.dpaul.android.app.adapter.MovieCrewAdapter
import com.moviemagic.dpaul.android.app.adapter.SimilarMovieAdapter
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.utility.FriendlyDisplay
import com.moviemagic.dpaul.android.app.utility.JsonParse
import com.moviemagic.dpaul.android.app.utility.LoadMovieBasicAddlInfo
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailMovieFragment.class.getSimpleName()

    private TextView mMovieTitleTextView, mGenreTextView, mRunTimeTextView, mReleaseDateTextView, mBudgetTextView,
                     mRevenueTextView, mPopularityTextView, mTotalVoteCountTextView, mTaglineTextView, mSynopsisTextView,
                     mProdCompanyTextView, mProdCountryTextView, mCollectionNameTextView, mHomePageTextView, mImdbLinkTextView
    private TextView mReleaseDateHeaderTextView, mBudgetHeaderTextView, mRevenueHeaderTextView, mPopularityHeaderTextView,
            mTmdbRatingHeaderTextView, mTmdbTotalVoteCountHeaderTextView, mTmdbTotalVoteCountTrailerTextView,
            mUserRatingHeaderTextView, mTaglineHeaderTextView, mSynopsisHeaderTextView, mMovieTrailerHeaderTextView,
            mProdCompanyHeaderTextView, mProdCountryHeaderTextView, mCastHeaderTextView, mCrewHeaderTextView,
            mSimilarMovieHeaderTextView, mCollectionNameHeaderTextView, mReviewHeaderTextView
    private ImageView mMpaaRatingImageView, mPosterImageView, mCollectionBackdropImageView
    private LinearLayout mDetailTitleLayout, mDetailPosterLayout, mDetailTmdbRatingLayout, mDetailUserRatingLayout,
                         mDetailSynopsisLayout, mDetailTrailerLayout, mDetailProductionInfoLayout, mDetailCastHeaderLayout,
                         mDetailCrewHeaderLayout, mDetailSimilarMovieHeaderLayout,
                         mDetailCollectionLayout, mDetailWebLinkLayout, mDetailReviewHeaderLayout,
                         mDetailReviewListViewLayout
    private RatingBar mTmdbRatingBar, mUserRatingBar
    private FrameLayout mDetailsCastGridLayout, mDetailCrewGridLayout, mDetailSimilarMovieGridLayout
    private Uri mMovieIdUri
    private int mMovieId
    private String[] mMovieIdArg
    private String[] mVideoArg
    private String[] mReleaseInfoArg
    private String[] mMovieImageArg
    private String mMovieTitle
    private String mOriginalBackdropPath
    private SimilarMovieAdapter mSimilarMovieAdapter
    private MovieCastAdapter mMovieCastAdapter
    private MovieCrewAdapter mMovieCrewAdapter
    private HorizontalGridView mHorizontalSimilarMovieGridView, mHorizontalMovieCastGridView, mHorizontalMovieCrewGridView
    private RecyclerView.LayoutManager mSimilarMovieLayoutManager, mMovieCastLayoutManager, mMovieCrewLayoutManager
    private MovieTitleAndColorCallback mMovieTitleAndColorCallback
    private BackdropCallback mBackdropCallback
    private String mLocale
    private int mPalletePrimaryColor
    private int mPalletePrimaryDarkColor
    private int mPalleteTitleColor
    private int mPalleteBodyTextColor
    private int mPalleteAccentColor

    public static final String MOVIE_BASIC_INFO_MOVIE_ID_URI = 'movie_basic_info_movie_id_uri'
    private static final String MOVIE_VIDEO_SITE_YOUTUBE = 'YouTube'
    private static final String MOVIE_VIDEO_SITE_TYPE = 'Trailer'
    private static final int MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID = 0
    private static final int MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID = 1
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID = 2
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID = 3
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID = 4
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID = 5
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID = 6

    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_GENRE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_BACKDROP_PATH = 2
    final static int COL_MOVIE_BASIC_TITLE = 3
    final static int COL_MOVIE_BASIC_GENRE = 4
    final static int COL_MOVIE_BASIC_RUNTIME = 5
    final static int COL_MOVIE_BASIC_POSTER_PATH = 6
    final static int COL_MOVIE_BASIC_RELEASE_DATE = 7
    final static int COL_MOVIE_BASIC_BUDGET = 8
    final static int COL_MOVIE_BASIC_REVENUE = 9
    final static int COL_MOVIE_BASIC_TAGLINE = 10
    final static int COL_MOVIE_BASIC_OVERVIEW = 11
    final static int COL_MOVIE_BASIC_VOTE_AVG = 12
    final static int COL_MOVIE_BASIC_VOTE_COUNT = 13
    final static int COL_MOVIE_BASIC_PRODUCTION_COMPANIES = 14
    final static int COL_MOVIE_BASIC_PRODUCTION_COUNTRIES = 15
    final static int COL_MOVIE_BASIC_COLLECTION_ID = 16
    final static int COL_MOVIE_BASIC_COLLECTION_NAME = 17
    final static int COL_MOVIE_BASIC_COLLECTION_POSTER_PATH = 18
    final static int COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH = 19
    final static int COL_MOVIE_BASIC_HOME_PAGE = 20
    final static int COL_MOVIE_BASIC_IMDB_ID = 21
    final static int COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG = 22
    final static int COL_MOVIE_BASIC_POPULARITY = 23

    //Columns to fetch from movie_basic_info table for similar movies
    private static final String[] SIMILAR_MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_SIMILAR_MOVIE_ID = 0
    public final static int COL_SIMILAR_MOVIE_MOVIE_ID = 1
    public final static int COL_SIMILAR_MOVIE_POSTER_PATH = 2
    public final static int COL_SIMILAR_MOVIE_TITLE = 3
    final static int COL_SIMILAR_MOVIE_LINK_ID = 4

    //Columns to fetch from movie_video table
    private static final String[] MOVIE_VIDEO_COLUMNS = [MovieMagicContract.MovieVideo._ID,
                                                           MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,
                                                           MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY,
                                                           MovieMagicContract.MovieVideo.COLUMN_VIDEO_NAME,
                                                           MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE,
                                                           MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_VIDEO_ID = 0
    final static int COL_MOVIE_VIDEO_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_VIDEO_KEY = 2
    final static int COL_MOVIE_VIDEO_NAME = 3
    final static int COL_MOVIE_VIDEO_SITE = 4
    final static int COL_MOVIE_VIDEO_TYPE = 5

    //Columns to fetch from movie_cast table
    private static final String[] MOVIE_CAST_COLUMNS = [MovieMagicContract.MovieCast._ID,
                                                         MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,
                                                         MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER,
                                                         MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID,
                                                         MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME,
                                                         MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_CAST_ID = 0
    final static int COL_MOVIE_CAST_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_CAST_CHARACTER = 2
    final static int COL_MOVIE_CAST_PERSON_ID = 3
    final static int COL_MOVIE_CAST_PERSON_NAME = 4
    final static int COL_MOVIE_CAST_PROFILE_PATH = 5

    //Columns to fetch from movie_crew table
    private static final String[] MOVIE_CREW_COLUMNS = [MovieMagicContract.MovieCrew._ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_JOB,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_CREW_ID = 0
    final static int COL_MOVIE_CREW_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_CREW_PERSON_ID = 2
    final static int COL_MOVIE_CREW_PERSON_NAME = 3
    final static int COL_MOVIE_CREW_CREW_JOB = 4
    final static int COL_MOVIE_CREW_PROFILE_PATH = 5

    //Columns to fetch from movie_release_date_info table
    private static final String[] MOVIE_RELEASE_INFO_COLUMNS = [MovieMagicContract.MovieReleaseDate._ID,
                                                        MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,
                                                        MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,
                                                        MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_RELEASE_INFO_ID = 0
    final static int COL_MOVIE_RELEASE_INFO_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_RELEASE_INFO_ISO_COUNTRY = 2
    final static int COL_MOVIE_RELEASE_INFO_CERTIFICATION = 3

    //Columns to fetch from movie_image table
    private static final String[] MOVIE_IMAGE_COLUMNS = [MovieMagicContract.MovieImage._ID,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_IMAGE_ID = 0
    final static int COL_MOVIE_IMAGE_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_IMAGE_TYPE = 2
    final static int COL_MOVIE_IMAGE_FILE_PATH = 3

    //An empty constructor is needed so that lifecycle is properly handled
    public DetailMovieFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get the bundle from the Fragment
        Bundle args = getArguments()
        if (args != null) {
            mMovieIdUri = args.getParcelable(MOVIE_BASIC_INFO_MOVIE_ID_URI) as Uri
            LogDisplay.callLog(LOG_TAG,"Bundle data -> ${mMovieIdUri.toString()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mMovieId = MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieIdUri)
        }
        //inflate the view before referring any view using id
        View mRootView = inflater.inflate(R.layout.fragment_detail_movie, container, false)

        //All the layouts
        mDetailTitleLayout = mRootView.findViewById(R.id.movie_detail_title_layout) as LinearLayout
        mDetailPosterLayout = mRootView.findViewById(R.id.movie_detail_poster_layout) as LinearLayout
        mDetailTmdbRatingLayout = mRootView.findViewById(R.id.movie_detail_tmdb_rating_layout) as LinearLayout
        mDetailUserRatingLayout = mRootView.findViewById(R.id.movie_detail_user_rating_layout) as LinearLayout
        mDetailSynopsisLayout = mRootView.findViewById(R.id.movie_detail_synopsis_layout) as LinearLayout
        mDetailTrailerLayout = mRootView.findViewById(R.id.movie_detail_trailer_layout) as LinearLayout
        mDetailProductionInfoLayout = mRootView.findViewById(R.id.movie_detail_production_info_layout) as LinearLayout
        mDetailCastHeaderLayout = mRootView.findViewById(R.id.movie_detail_cast_header_layout) as LinearLayout
        mDetailsCastGridLayout = mRootView.findViewById(R.id.movie_detail_cast_grid_layout) as FrameLayout
        mDetailCrewHeaderLayout = mRootView.findViewById(R.id.movie_detail_crew_header_layout) as LinearLayout
        mDetailCrewGridLayout = mRootView.findViewById(R.id.movie_detail_crew_grid_layout) as FrameLayout
        mDetailSimilarMovieHeaderLayout = mRootView.findViewById(R.id.movie_detail_similar_movie_header_layout) as LinearLayout
        mDetailSimilarMovieGridLayout = mRootView.findViewById(R.id.movie_detail_similar_movie_grid_layout) as FrameLayout
        mDetailCollectionLayout = mRootView.findViewById(R.id.movie_detail_collection_layout) as LinearLayout
        mDetailWebLinkLayout = mRootView.findViewById(R.id.movie_detail_web_links_layout) as LinearLayout
        mDetailReviewHeaderLayout = mRootView.findViewById(R.id.movie_detail_review_header_layout) as LinearLayout
        mDetailReviewListViewLayout = mRootView.findViewById(R.id.movie_detail_review_list_view_layout) as LinearLayout

        //All the header (fixed text) fields
        mReleaseDateHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_release_date_header) as TextView
        mBudgetHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_budget_header) as TextView
        mRevenueHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_revenue_header) as TextView
        mPopularityHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_popularity_header) as TextView
        mTmdbRatingHeaderTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_header) as TextView
        mTmdbTotalVoteCountHeaderTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_header) as TextView
        mTmdbTotalVoteCountTrailerTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_trailer) as TextView
        mUserRatingHeaderTextView = mRootView.findViewById(R.id.movie_detail_user_rating_header) as TextView
        mTaglineHeaderTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline_header) as TextView
        mSynopsisHeaderTextView = mRootView.findViewById(R.id.movie_detail_synopsis_header) as TextView
        mMovieTrailerHeaderTextView = mRootView.findViewById(R.id.movie_detail_trailer_header) as TextView
        mProdCompanyHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy_header) as TextView
        mProdCountryHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_country_header) as TextView
        mCastHeaderTextView = mRootView.findViewById(R.id.movie_detail_cast_header) as TextView
        mCrewHeaderTextView = mRootView.findViewById(R.id.movie_detail_crew_header) as TextView
        mSimilarMovieHeaderTextView = mRootView.findViewById(R.id.movie_detail_similar_movie_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mReviewHeaderTextView = mRootView.findViewById(R.id.movie_detail_review_header) as TextView

        //All the dynamic fields (data fields)
        mMovieTitleTextView = mRootView.findViewById(R.id.movie_detail_title) as TextView
        mMpaaRatingImageView = mRootView.findViewById(R.id.movie_detail_mpaa_image) as ImageView
        mGenreTextView = mRootView.findViewById(R.id.movie_detail_title_genre) as TextView
        mRunTimeTextView = mRootView.findViewById(R.id.movie_detail_title_runtime) as TextView
        mPosterImageView = mRootView.findViewById(R.id.movie_detail_poster_image) as ImageView
        mReleaseDateTextView = mRootView.findViewById(R.id.movie_detail_poster_release_date) as TextView
        mBudgetTextView = mRootView.findViewById(R.id.movie_detail_poster_budget) as TextView
        mRevenueTextView = mRootView.findViewById(R.id.movie_detail_poster_revenue) as TextView
        mPopularityTextView = mRootView.findViewById(R.id.movie_detail_poster_popularity) as TextView
        mTmdbRatingBar = mRootView.findViewById(R.id.movie_detail_tmdb_rating_bar) as RatingBar
        mUserRatingBar = mRootView.findViewById(R.id.movie_detail_user_rating_bar) as RatingBar
        mTotalVoteCountTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_val) as TextView
        mTaglineTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline) as TextView
        mSynopsisTextView = mRootView.findViewById(R.id.movie_detail_synopsis) as TextView
        mProdCompanyTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy) as TextView
        mProdCountryTextView = mRootView.findViewById(R.id.movie_detail_production_info_country) as TextView
        mCollectionNameTextView = mRootView.findViewById(R.id.movie_detail_collection_name) as TextView
        mCollectionBackdropImageView = mRootView.findViewById(R.id.movie_detail_collection_image) as ImageView
        mHomePageTextView = mRootView.findViewById(R.id.movie_detail_web_links_home_page) as TextView
        mImdbLinkTextView = mRootView.findViewById(R.id.movie_detail_web_links_imdb_link) as TextView
        mHorizontalSimilarMovieGridView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid) as HorizontalGridView

        mSimilarMovieLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.HORIZONTAL,false)
        mHorizontalSimilarMovieGridView.setLayoutManager(mSimilarMovieLayoutManager)
        mSimilarMovieAdapter = new SimilarMovieAdapter(getActivity())
        mHorizontalSimilarMovieGridView.setAdapter(mSimilarMovieAdapter)
        mHorizontalSimilarMovieGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState)
                LogDisplay.callLog(LOG_TAG,"onScrollStateChanged is called.",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }

            @Override
            void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy)
                LogDisplay.callLog(LOG_TAG,"onScrolled is called.dx=$dx & dy=$dy",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        })
        mHorizontalMovieCastGridView = mRootView.findViewById(R.id.movie_detail_cast_grid) as HorizontalGridView
        mMovieCastLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.HORIZONTAL,false)
        mHorizontalMovieCastGridView.setLayoutManager(mMovieCastLayoutManager)
        mMovieCastAdapter = new MovieCastAdapter(getActivity())
        mHorizontalMovieCastGridView.setAdapter(mMovieCastAdapter)

        mHorizontalMovieCrewGridView = mRootView.findViewById(R.id.movie_detail_crew_grid) as HorizontalGridView
        mMovieCrewLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.HORIZONTAL,false)
        mHorizontalMovieCrewGridView.setLayoutManager(mMovieCrewLayoutManager)
        mMovieCrewAdapter = new MovieCrewAdapter(getActivity())
        mHorizontalMovieCrewGridView.setAdapter(mMovieCrewAdapter)

        return mRootView
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID, null, this)
        mLocale = context.getResources().getConfiguration().locale.getCountry()
        LogDisplay.callLog(LOG_TAG,"Locale: $mLocale",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if(mMovieId) {
            mMovieIdArg = [Integer.toString(mMovieId)] as String[]
            mVideoArg = [Integer.toString(mMovieId),MOVIE_VIDEO_SITE_YOUTUBE, MOVIE_VIDEO_SITE_TYPE] as String[]
            mReleaseInfoArg = [Integer.toString(mMovieId), mLocale] as String[]
            mMovieImageArg = [Integer.toString(mMovieId), JsonParse.IMAGE_TYPE_BACKDROP] as String[]
        } else {
            //this is to safeguard any unwanted data fetch
            mMovieIdArg = ['ZZZZZZ'] as String[]
            mVideoArg = ['XXXXXX','YYYYY','ZZZZZZ'] as String[]
            mReleaseInfoArg = ['YYYYY','ZZZZZZ'] as String[]
            mMovieImageArg = ['YYYYY','ZZZZZZ'] as String[]
        }
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID, null, this)
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID, null, this)
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID, null, this)
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID, null, this)
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID, null, this)
        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID, null, this)
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogDisplay.callLog(LOG_TAG,"onCreateLoader.loader id->$id",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID:
                return new CursorLoader(
                        getActivity(),              //Parent Activity Context
                        mMovieIdUri,                //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,   //Projection to return
                        null,                       //Selection Clause, null->will return all data
                        null,                       //Selection Arg, null-> will return all data
                        null)                       //Only a single row is expected, so not sorted

            case MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                  //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,  //Table to query
                        SIMILAR_MOVIE_COLUMNS,                          //Projection to return
                        MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID + "= ?", //Selection Clause
                        mMovieIdArg,                                    //Selection Arg
//                        null,null,
                        null)                                           //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                  //Parent Activity Context
                        MovieMagicContract.MovieVideo.CONTENT_URI,      //Table to query
                        MOVIE_VIDEO_COLUMNS,                            //Projection to return
                        """$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE = ? """, //Selection Clause
                        mVideoArg,                                     //Selection Arg
                        null)                                           //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                  //Parent Activity Context
                        MovieMagicContract.MovieCast.CONTENT_URI,       //Table to query
                        MOVIE_CAST_COLUMNS,                             //Projection to return
                        MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID + "= ?", //Selection Clause
                        mMovieIdArg,                                    //Selection Arg
                        MovieMagicContract.MovieCast.COLUMN_CAST_ORDER) //Sorted on the order

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                  //Parent Activity Context
                        MovieMagicContract.MovieCrew.CONTENT_URI,       //Table to query
                        MOVIE_CREW_COLUMNS,                             //Projection to return
                        MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID + "= ?", //Selection Clause
                        mMovieIdArg,                                    //Selection Arg
                        MovieMagicContract.MovieCrew.COLUMN_CREW_JOB)   //Sorted on the job

            case MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                    //Parent Activity Context
                        MovieMagicContract.MovieReleaseDate.CONTENT_URI,  //Table to query
                        MOVIE_RELEASE_INFO_COLUMNS,                       //Projection to return
                        """$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY = ? """, //Selection Clause
                        mReleaseInfoArg,                                     //Selection Arg
                        null)                                             //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                    //Parent Activity Context
                        MovieMagicContract.MovieImage.CONTENT_URI,  //Table to query
                        MOVIE_IMAGE_COLUMNS,                       //Projection to return
                        """$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE = ? """, //Selection Clause
                        mMovieImageArg,                                     //Selection Arg
                        null)                                             //Sorting not used
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId()
        LogDisplay.callLog(LOG_TAG,"onLoadFinished.loader id->$loaderId",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        switch (loaderId) {
            case MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID:
                handleMovieBasicOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID:
                handleSimilarMovieOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID:
                initiateYouTubeVideo(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID:
                handleMovieCastOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID:
                handleMovieCrewOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID:
                populateMpaaImage(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID:
                processBackdropImages(data)
                break
            default:
                LogDisplay.callLog(LOG_TAG,"Unknown loader id. id->$loaderId",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        //Reset the adapters
        mSimilarMovieAdapter.swapCursor(null)
        mMovieCastAdapter.swapCursor(null)
        mMovieCrewAdapter.swapCursor(null)
    }

    void handleMovieBasicOnLoadFinished(Cursor data) {
        if(data.moveToFirst()) {
            LogDisplay.callLog(LOG_TAG,"Cursor Data.Movie id -> ${Integer.toString(mMovieId)}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mOriginalBackdropPath = data.getString(COL_MOVIE_BASIC_BACKDROP_PATH)
            mMovieTitle = data.getString(COL_MOVIE_BASIC_TITLE)
            mMovieTitleTextView.setText(mMovieTitle)
            mGenreTextView.setText(data.getString(COL_MOVIE_BASIC_GENRE))
            mRunTimeTextView.setText(FriendlyDisplay.formatRunTime(getActivity(),data.getInt(COL_MOVIE_BASIC_RUNTIME)))

            String posterPath = "http://image.tmdb.org/t/p/w185${data.getString(COL_MOVIE_BASIC_POSTER_PATH)}"
            Picasso.with(getActivity())
                    .load(posterPath)
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(mPosterImageView, new com.squareup.picasso.Callback() {
                @Override
                void onSuccess() {
                    Bitmap bitmapPoster = ((BitmapDrawable)mPosterImageView.getDrawable()).getBitmap()
                    Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette p) {
                            Palette.Swatch vibrantSwatch = p.getVibrantSwatch()
                            Palette.Swatch lightVibrantSwatch = p.getLightVibrantSwatch()
                            Palette.Swatch darkVibrantSwatch = p.getDarkVibrantSwatch()
                            Palette.Swatch mutedSwatch = p.getMutedSwatch()
                            Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch()
                            Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch()

                            //Pick primary, primaryDark, title and body text color
                            if (vibrantSwatch) {
                                mPalletePrimaryColor = vibrantSwatch.getRgb()
                                mPalleteTitleColor = vibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = vibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = vibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                            } else if(lightVibrantSwatch) { //Try another swatch
                                mPalletePrimaryColor = lightVibrantSwatch.getRgb()
                                mPalleteTitleColor = lightVibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = lightVibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = lightVibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                            } else if(darkVibrantSwatch) { //Try last swatch
                                mPalletePrimaryColor = darkVibrantSwatch.getRgb()
                                mPalleteTitleColor = darkVibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = darkVibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = darkVibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                            } else { //Fallback to default
                                mPalletePrimaryColor = ContextCompat.getColor(getActivity(),R.color.primary)
                                mPalletePrimaryDarkColor = ContextCompat.getColor(getActivity(),R.color.primary_dark)
                                mPalleteTitleColor = ContextCompat.getColor(getActivity(),R.color.white_color)
                                mPalleteBodyTextColor = ContextCompat.getColor(getActivity(),R.color.grey_color)
                            }

                            //Pick accent color
                            if(mutedSwatch) {
                                mPalleteAccentColor = mutedSwatch.getRgb()
                            } else if(mutedLightSwatch) { //Try another swatch
                                mPalleteAccentColor = mutedLightSwatch.getRgb()
                            } else if(mutedDarkSwatch) { //Try last swatch
                                mPalleteAccentColor = mutedDarkSwatch.getRgb()
                            } else { //Fallback to default
                                mPalleteAccentColor = ContextCompat.getColor(getActivity(),R.color.accent)
                            }

                            changeLayoutAndTextColor()
                            mMovieTitleAndColorCallback.initializeActivityHostedTitleAndColor(mMovieTitle, mPalletePrimaryColor, mPalletePrimaryDarkColor, mPalleteTitleColor)
                            //Set the color for adapter fields and call method which in turn calls notifyDatasetChanged
                            MovieCastAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
                            MovieCastAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mMovieCastAdapter.changeColor()
                            MovieCrewAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
                            MovieCrewAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mMovieCrewAdapter.changeColor()
                            SimilarMovieAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
                            SimilarMovieAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mSimilarMovieAdapter.changeColor()
                        }
                    })
                }

                @Override
                void onError() {

                }
            })
            mReleaseDateTextView.setText(FriendlyDisplay.formatMiliSecondsToDate(data.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
            mBudgetTextView.setText(FriendlyDisplay.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_BUDGET)))
            mRevenueTextView.setText(FriendlyDisplay.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_REVENUE)))
            mPopularityTextView.setText(data.getString(COL_MOVIE_BASIC_POPULARITY))
            mTmdbRatingBar.setRating(data.getFloat(COL_MOVIE_BASIC_VOTE_AVG))
            mTotalVoteCountTextView.setText(data.getString(COL_MOVIE_BASIC_VOTE_COUNT))
            mTaglineTextView.setText(data.getString(COL_MOVIE_BASIC_TAGLINE))
            mSynopsisTextView.setText(data.getString(COL_MOVIE_BASIC_OVERVIEW))
            mProdCompanyTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COMPANIES))
            mProdCountryTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COUNTRIES))
            mCollectionNameTextView.setText(data.getString(COL_MOVIE_BASIC_COLLECTION_NAME))
            String collectionBackdropPath = "http://image.tmdb.org/t/p/w500${data.getString(COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH)}"
            Picasso.with(getActivity())
                    .load(collectionBackdropPath)
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(mCollectionBackdropImageView)
            mHomePageTextView.setText(data.getString(COL_MOVIE_BASIC_HOME_PAGE))
            mImdbLinkTextView.setText(data.getString(COL_MOVIE_BASIC_IMDB_ID))

            int detailDataPresentFlag = data.getInt(COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG)
            //If the flag is zero then data not present, so go and fetch it
            if(detailDataPresentFlag == 0) {
                LogDisplay.callLog(LOG_TAG,'Additional movie data not present, go and fetch it',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                Integer[] movieIdArray = [mMovieId, data.getInt(COL_MOVIE_BASIC_ID)] as Integer[]
                //long movieRowId = MovieMagicContract.MovieBasicInfo.getRowIdFromUri(mMovieRowIdUri)
                new LoadMovieBasicAddlInfo(getActivity(), mMovieIdUri).execute(movieIdArray)
            }
            else {
                LogDisplay.callLog(LOG_TAG,'Additional movie data already present',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
        else {
            LogDisplay.callLog(LOG_TAG,"Bad cursor.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    void handleSimilarMovieOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"handleSimilarMovieOnLoadFinished.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mSimilarMovieAdapter.swapCursor(data)
    }

    void handleMovieCastOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"handleMovieCastOnLoadFinished.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCastAdapter.swapCursor(data)
    }

    void handleMovieCrewOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"handleMovieCrewOnLoadFinished.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCrewAdapter.swapCursor(data)
    }

    void initiateYouTubeVideo(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"initiateYouTubeVideo.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        List<String> youtubeVideoKey
        if(data.moveToFirst()) {
            youtubeVideoKey = new ArrayList<>()
            for(i in 0..(data.count-1)) {
                youtubeVideoKey.add(data.getString(COL_MOVIE_VIDEO_KEY))
                LogDisplay.callLog(LOG_TAG, "YouTube video key= $youtubeVideoKey", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                data.moveToNext()
            }
            final MovieMagicYoutubeFragment movieMagicYoutubeFragment = getChildFragmentManager()
                    .findFragmentById(R.id.movie_detail_trailer_fragment_youtube) as MovieMagicYoutubeFragment
            if (movieMagicYoutubeFragment) {
                LogDisplay.callLog(LOG_TAG, 'Youtube Fragment id is NOT null', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                movieMagicYoutubeFragment.setVideoId(youtubeVideoKey)
            } else {
                LogDisplay.callLog(LOG_TAG, 'Youtube Fragment id is null', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
    }

    void populateMpaaImage(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"populateMpaaImage.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if(data.moveToFirst()) {
            final String mpaa = data.getString(COL_MOVIE_RELEASE_INFO_CERTIFICATION)
            LogDisplay.callLog(LOG_TAG,"Mpaa certification: $mpaa & Locale: $mLocale",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            final int mpaaIconResId = FriendlyDisplay.getIconResourceForMpaaRating(mpaa, mLocale)
            if(mpaaIconResId != -1) {
                mMpaaRatingImageView.setImageResource(mpaaIconResId)
            } else {
                LogDisplay.callLog(LOG_TAG,'FriendlyDisplay.getIconResourceForMpaaRating returned -1',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mMpaaRatingImageView.setImageResource(R.drawable.not_available)
            }
        } else {
            LogDisplay.callLog(LOG_TAG,'Not able to retrieve mpaa image, set default image',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mMpaaRatingImageView.setImageResource(R.drawable.not_available)
        }
    }

    void processBackdropImages(Cursor data) {
        LogDisplay.callLog(LOG_TAG,"processBackdropImages.Cursor rec count -> ${data.getCount()}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            List<String> backdropList = new ArrayList<String>()
            //Add the main backdrop first
            if(mOriginalBackdropPath) {
                backdropList.add(mOriginalBackdropPath)
            }
            for(i in 0..(data.count-1)) {
                backdropList.add(data.getString(COL_MOVIE_IMAGE_FILE_PATH))
                data.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG,"backdropImageArray-> $backdropList",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mBackdropCallback.initializeActivityHostedBackdrop(backdropList)
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mBackdropCallback = (BackdropCallback) activity
            mMovieTitleAndColorCallback = (MovieTitleAndColorCallback) activity
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback interface")
        }
    }

    void changeLayoutAndTextColor() {
        //Change color for all the layouts
        mDetailTitleLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailPosterLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailTmdbRatingLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailUserRatingLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailSynopsisLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailTrailerLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailProductionInfoLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailCastHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailsCastGridLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailCrewHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailCrewGridLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailSimilarMovieHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailSimilarMovieGridLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailCollectionLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailWebLinkLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailReviewHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
        mDetailReviewListViewLayout.setBackgroundColor(mPalletePrimaryColor)

        //Change color for header text fields
        mReleaseDateHeaderTextView.setTextColor(mPalleteTitleColor)
        mBudgetHeaderTextView.setTextColor(mPalleteTitleColor)
        mRevenueHeaderTextView.setTextColor(mPalleteTitleColor)
        mPopularityHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbRatingHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbTotalVoteCountHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbTotalVoteCountTrailerTextView.setTextColor(mPalleteTitleColor)
        mUserRatingHeaderTextView.setTextColor(mPalleteTitleColor)
        mTaglineHeaderTextView.setTextColor(mPalleteTitleColor)
        mSynopsisHeaderTextView.setTextColor(mPalleteTitleColor)
        mMovieTrailerHeaderTextView.setTextColor(mPalleteTitleColor)
        mProdCompanyHeaderTextView.setTextColor(mPalleteTitleColor)
        mProdCountryHeaderTextView.setTextColor(mPalleteTitleColor)
        mCastHeaderTextView.setTextColor(mPalleteTitleColor)
        mCrewHeaderTextView.setTextColor(mPalleteTitleColor)
        mSimilarMovieHeaderTextView.setTextColor(mPalleteTitleColor)
        mCollectionNameHeaderTextView.setTextColor(mPalleteTitleColor)
        mReviewHeaderTextView.setTextColor(mPalleteTitleColor)

        //Change color for data fields
        mMovieTitleTextView.setTextColor(mPalleteTitleColor) //Movie name is Title color
        mGenreTextView.setTextColor(mPalleteBodyTextColor)
        mRunTimeTextView.setTextColor(mPalleteBodyTextColor)
        mReleaseDateTextView.setTextColor(mPalleteBodyTextColor)
        mBudgetTextView.setTextColor(mPalleteBodyTextColor)
        mRevenueTextView.setTextColor(mPalleteBodyTextColor)
        mPopularityTextView.setTextColor(mPalleteBodyTextColor)
        //Since total vote count is part of rating line, hence use same color
        mTotalVoteCountTextView.setTextColor(mPalleteTitleColor)
        mTaglineTextView.setTextColor(mPalleteBodyTextColor)
        mSynopsisTextView.setTextColor(mPalleteBodyTextColor)
        mProdCompanyTextView.setTextColor(mPalleteBodyTextColor)
        mProdCountryTextView.setTextColor(mPalleteBodyTextColor)
        mCollectionNameTextView.setTextColor(mPalleteBodyTextColor)
        mHomePageTextView.setTextColor(mPalleteBodyTextColor)
        mImdbLinkTextView.setTextColor(mPalleteBodyTextColor)

        //Set ratingbar color
        Drawable tmdbRatingdrawable = mTmdbRatingBar.getProgressDrawable()
        DrawableCompat.setTint(tmdbRatingdrawable, mPalleteAccentColor)
        Drawable userRatingDrawable = mUserRatingBar.getProgressDrawable()
        DrawableCompat.setTint(userRatingDrawable, mPalleteAccentColor)

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified when movie title and color value
     * are determined and ready for activity to update
     */
    public interface MovieTitleAndColorCallback {
        /**
         * DetailFragmentCallback for updating the Movie Title and Theme Color in Activity
         */
        public void initializeActivityHostedTitleAndColor(String movieTitle, int primaryColor, int primaryDarkColor, int titleColor)
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified when Backdrop image loader
     * is finished and processed
     */
    public interface BackdropCallback {
        /**
         * DetailFragmentCallback for updating the Backdrop images in Activity
         */
        public void initializeActivityHostedBackdrop(List<String> backdropImagePathList)
    }
}