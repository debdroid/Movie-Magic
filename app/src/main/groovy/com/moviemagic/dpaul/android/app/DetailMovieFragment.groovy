package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v17.leanback.widget.HorizontalGridView
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.BackdropPagerAdapter
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
    private ImageView mMpaaRatingImageView, mPosterImageView, mCollectionBackdropImageView
    private RatingBar mTmdbRatingBar
    private Uri mMovieIdUri
    private int mMovieId
    private String[] mMovieIdArg
    private String[] mVideoArg
    private String[] mReleaseInfoArg
    private String[] mMovieImageArg
    private String mMovieTitle
    String mOriginalBackdropPath
    private SimilarMovieAdapter mSimilarMovieAdapter
    private MovieCastAdapter mMovieCastAdapter
    private MovieCrewAdapter mMovieCrewAdapter
    private BackdropPagerAdapter mBackdropPagerAdapter
    private HorizontalGridView mHorizontalSimilarMovieGridView, mHorizontalMovieCastGridView, mHorizontalMovieCrewGridView
    private RecyclerView.LayoutManager mSimilarMovieLayoutManager, mMovieCastLayoutManager, mMovieCrewLayoutManager
//    MovieMagicYoutubeFragment movieMagicYoutubeFragment
    private Callback mCallback
    private String mLocale

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
//        mBackdrop = container.findViewById(R.id.movie_detail_backdrop_image) as ImageView
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
        mTotalVoteCountTextView = mRootView.findViewById(R.id.tmdb_rating_vote_count_val) as TextView
        mTaglineTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline) as TextView
        mSynopsisTextView = mRootView.findViewById(R.id.movie_detail_synopsis) as TextView
        mProdCompanyTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy) as TextView
        mProdCountryTextView = mRootView.findViewById(R.id.movie_detail_production_info_country) as TextView
        mCollectionNameTextView = mRootView.findViewById(R.id.movie_detail_collection_name) as TextView
        mCollectionBackdropImageView = mRootView.findViewById(R.id.movie_detail_collection_image) as ImageView
        mHomePageTextView = mRootView.findViewById(R.id.movie_detail_web_links_home_page) as TextView
        mImdbLinkTextView = mRootView.findViewById(R.id.movie_detail_web_links_imdb_link) as TextView
//        movieMagicYoutubeFragment = getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_youtube) as MovieMagicYoutubeFragment
        mHorizontalSimilarMovieGridView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid) as HorizontalGridView
        //Create an instance of linear layout manager
//        mSimilarMovieLayoutManager = new LinearLayoutManager(getActivity())
        mSimilarMovieLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.HORIZONTAL,false)
        // Set the layout manager
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

        mBackdropPagerAdapter = new BackdropPagerAdapter(getActivity().getSupportFragmentManager())
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
//                startOtherLoaders()
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
        //Do nothing
        mSimilarMovieAdapter.swapCursor(null)
        mMovieCastAdapter.swapCursor(null)
        mMovieCrewAdapter.swapCursor(null)
        mBackdropPagerAdapter.swapCursor(null)
    }

    void handleMovieBasicOnLoadFinished(Cursor data) {
        if(data.moveToFirst()) {
            mMovieTitleTextView.setText(data.getString(COL_MOVIE_BASIC_TITLE))
            mGenreTextView.setText(data.getString(COL_MOVIE_BASIC_GENRE))
            mRunTimeTextView.setText(FriendlyDisplay.formatRunTime(getActivity(),data.getInt(COL_MOVIE_BASIC_RUNTIME)))
            String posterPath = "http://image.tmdb.org/t/p/w185${data.getString(COL_MOVIE_BASIC_POSTER_PATH)}"
            Picasso.with(getActivity())
                    .load(posterPath)
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(mPosterImageView)
            mReleaseDateTextView.setText(FriendlyDisplay.formatMiliSecondsToDate(data.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
//            mReleaseDateTextView.setText(data.getString(COL_MOVIE_BASIC_RELEASE_DATE))
//            mBudgetTextView.setText(data.getString(COL_MOVIE_BASIC_BUDGET))
            mBudgetTextView.setText(FriendlyDisplay.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_BUDGET)))
            mRevenueTextView.setText(FriendlyDisplay.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_REVENUE)))
            mPopularityTextView.setText(data.getString(COL_MOVIE_BASIC_POPULARITY))
//            mRevenueTextView.setText(data.getString(COL_MOVIE_BASIC_REVENUE))
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

            mMovieTitle = data.getString(COL_MOVIE_BASIC_TITLE)
            mOriginalBackdropPath = data.getString(COL_MOVIE_BASIC_BACKDROP_PATH)
//            mCallback.initializeActivityHostedFields(mMovieTitle,backdropPath)
//            mMovieId = data.getInt(COL_MOVIE_BASIC_MOVIE_ID)
            LogDisplay.callLog(LOG_TAG,"Cursor Data.Movie id -> ${Integer.toString(mMovieId)}",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)

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
                //Now it's make sense to start all other loaders
//                startOtherLoaders()
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
//        if (data.moveToFirst()) {
//            List<String> backdropList = new ArrayList<String>()
//            //Add the main backdrop first
//            if(mOriginalBackdropPath) {
//                backdropList.add(mOriginalBackdropPath)
//            }
//            for(i in 0..(data.count-1)) {
//                backdropList.add(data.getString(COL_MOVIE_IMAGE_FILE_PATH))
//                data.moveToNext()
//            }
//            mCallback.initializeActivityHostedFields(mMovieTitle, backdropList)
//            LogDisplay.callLog(LOG_TAG,"backdropImageArray-> $backdropList",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//        }
        mBackdropPagerAdapter.swapCursor(data)
        mCallback.initializeActivityHostedFields(mMovieTitle, mBackdropPagerAdapter)

    }

//    public void startOtherLoaders() {
//        mLocale = context.getResources().getConfiguration().locale.getCountry()
//        LogDisplay.callLog(LOG_TAG,"Locale: $mLocale",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//        if(mMovieId) {
//            mMovieIdArg = [Integer.toString(mMovieId)] as String[]
//            mVideoArg = [Integer.toString(mMovieId),MOVIE_VIDEO_SITE_YOUTUBE, MOVIE_VIDEO_SITE_TYPE] as String[]
//            mReleaseInfoArg = [Integer.toString(mMovieId), mLocale] as String[]
//        } else {
//            //this is to safeguard any unwanted data fetch
//            mMovieIdArg = ['ZZZZZZ'] as String[]
//            mVideoArg = ['XXXXXX','YYYYY','ZZZZZZ'] as String[]
//            mReleaseInfoArg = ['YYYYY','ZZZZZZ'] as String[]
//        }
//        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID, null, this)
//        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID, null, this)
//        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID, null, this)
//        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID, null, this)
//        getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID, null, this)
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback interface")
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selection.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
//        public void initializeActivityHostedFields(String movieTitle, List<String> backdropImagePathList)
        public void initializeActivityHostedFields(String movieTitle, BackdropPagerAdapter mBackdropPagerAdapter)
    }
}