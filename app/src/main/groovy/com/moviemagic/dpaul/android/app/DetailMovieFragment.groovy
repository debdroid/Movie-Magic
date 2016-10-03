package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v17.leanback.widget.HorizontalGridView
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.LayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.MovieCastAdapter
import com.moviemagic.dpaul.android.app.adapter.MovieCrewAdapter
import com.moviemagic.dpaul.android.app.adapter.MovieReviewAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonImageAdapter
import com.moviemagic.dpaul.android.app.adapter.SimilarMovieAdapter
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMovieDetails
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import com.moviemagic.dpaul.android.app.backgroundmodules.UpdateUserListChoiceAndRating
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailMovieFragment.class.getSimpleName()

    private TextView mMovieTitleTextView, mGenreTextView, mRunTimeTextView, mReleaseDateTextView, mBudgetTextView,
                     mRevenueTextView, mPopularityTextView, mTotalVoteCountTextView, mTaglineTextView, mSynopsisTextView,
                     mProdCompanyTextView, mProdCountryTextView, mCollectionNameTextView, mExternalLinkHeader

    private TextView mReleaseDateHeaderTextView, mBudgetHeaderTextView, mRevenueHeaderTextView, mPopularityHeaderTextView,
                     mTmdbRatingHeaderTextView, mTmdbTotalVoteCountHeaderTextView, mTmdbTotalVoteCountTrailerTextView,
                     mUserRatingHeaderTextView, mTaglineHeaderTextView, mSynopsisHeaderTextView, mMovieTrailerHeaderTextView,
                     mProdCompanyHeaderTextView, mProdCountryHeaderTextView, mCastHeaderTextView, mCrewHeaderTextView,
                     mSimilarMovieHeaderTextView, mCollectionNameHeaderTextView, mReviewHeaderTextView, mRecyclerViewEmptyMsgTextView,
                     mUserListDrawableTitle, mCastGridEmptyMsgTextView, mCrewGridEmptyMsgTextView, mSimilarMovieGridEmptyMsgTextView,
                     mMovieTrailerEmptyMsgTextView
    private ImageView mMpaaRatingImageView, mPosterImageView, mCollectionBackdropImageView
    private LinearLayout mDetailMovieLayout,mDetailTitleLayout, mDetailPosterLayout, mDetailTmdbRatingLayout, mDetailUserRatingLayout,
                         mDetailSynopsisLayout, mDetailTrailerLayout, mDetailProductionInfoLayout, mDetailCastHeaderLayout,
                         mDetailCrewHeaderLayout, mDetailSimilarMovieHeaderLayout, mUserListDrawableLayout,
                         mDetailCollectionLayout, mDetailWebLinkLayout, mDetailReviewHeaderLayout
    private ImageButton mImageButtonWatched, mImageButtonWishList, mImageButtonFavourite, mImageButtonCollection
    private RatingBar mTmdbRatingBar, mUserRatingBar
    private FrameLayout mDetailsCastGridLayout, mDetailCrewGridLayout, mDetailSimilarMovieGridLayout, mDetailReviewRecyclerViewLayout
    private Button mHomePageButton, mImdbLinkButton
    private Uri mMovieMagicMovieIdUri
    private int _ID_movie_basic_info
    private int mMovieId
    private int mCollectionId
//    private String[] mMovieRowIdArg
    private String[] mMovieIdArg
    private String[] mMovieIdCategoryArg
    private String[] mVideoArg
    private String[] mReleaseInfoArg
    private String[] mMovieImageArg
    private String mMovieTitle, mOriginalBackdropPath, mMovieHomePageUrl, mMovieImdbId
    private SimilarMovieAdapter mSimilarMovieAdapter
    private MovieCastAdapter mMovieCastAdapter
    private MovieCrewAdapter mMovieCrewAdapter
    private HorizontalGridView mHorizontalSimilarMovieGridView, mHorizontalMovieCastGridView, mHorizontalMovieCrewGridView
//    private RecyclerView.LayoutManager mSimilarMovieLayoutManager, mMovieCastLayoutManager, mMovieCrewLayoutManager
//    private MovieTitleAndColorCallback mMovieTitleAndColorCallback
//    private BackdropCallback mBackdropCallback
//    private UserListButtonClickCallback mUserListButtonClickCallback
    private String mLocale
    private int mPalletePrimaryColor
    private int mPalletePrimaryDarkColor
    private int mPalleteTitleColor
    private int mPalleteBodyTextColor
    private int mPalleteAccentColor
    private RecyclerView mMovieReviewRecyclerView
//    private RecyclerView.LayoutManager mMovieReviewLayoutManager
    private MovieReviewAdapter mMovieReviewAdapter
    private String mMovieListType
    private String mMovieCategory
    private boolean mUserListWatchedFlag = false
    private boolean mUserListWishListdFlag = false
    private boolean mUserListFavouriteFlag = false
    private boolean mUserListCollectionFlag = false
    private CollapsingToolbarLayout mCollapsingToolbar
    private Toolbar mToolbar
    private AppBarLayout mAppBarLayout
    private ImageView mBackdropImage1, mBackdropImage2
    private ImageSwitcher mBackdropImageSwitcher
    private static boolean picasoLoadComplete = true
    private static boolean setBackdropAnimation = true
    private final android.os.Handler mHandler = new android.os.Handler()
    private Runnable mRunnable
    private static final String MOVIE_TITLE = 'movie_title'
    private GridLayoutManager mSimilarMovieGridLayoutManager
    private int mBackdropImageCounter = 0
    private List<String> mBackdropList
    private CallbackForBackdropImageClick mCallbackForBackdropImageClick



//    public static final String MOVIE_BASIC_INFO_MOVIE_ID_URI = 'movie_basic_info_movie_id_uri'
    private static final String MOVIE_VIDEO_SITE_YOUTUBE = 'YouTube'
    private static final String MOVIE_VIDEO_SITE_TYPE = 'Trailer'
    private static final int MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID = 0
    private static final int MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID = 1
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID = 2
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID = 3
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID = 4
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID = 5
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID = 6
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID = 7
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID = 8

    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_GENRE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_ADULT_FLAG = 2
    final static int COL_MOVIE_BASIC_BACKDROP_PATH = 3
    final static int COL_MOVIE_BASIC_ORIG_TITLE = 4
    final static int COL_MOVIE_BASIC_OVERVIEW = 5
    final static int COL_MOVIE_BASIC_RELEASE_DATE = 6
    final static int COL_MOVIE_BASIC_POSTER_PATH = 7
    final static int COL_MOVIE_BASIC_POPULARITY = 8
    final static int COL_MOVIE_BASIC_TITLE = 9
    final static int COL_MOVIE_BASIC_VIDEO_FLAG = 10
    final static int COL_MOVIE_BASIC_VOTE_AVG = 11
    final static int COL_MOVIE_BASIC_VOTE_COUNT = 12
    final static int COL_MOVIE_BASIC_PAGE_NUM = 13
    final static int COL_MOVIE_BASIC_MOVIE_CATEGORY = 14
    final static int COL_MOVIE_BASIC_MOVIE_LIST_TYPE = 15
    final static int COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG = 16
    final static int COL_MOVIE_BASIC_SIMILAR_MOVIE_LINK_ID = 17
    final static int COL_MOVIE_BASIC_COLLECTION_ID = 18
    final static int COL_MOVIE_BASIC_COLLECTION_NAME = 19
    final static int COL_MOVIE_BASIC_COLLECTION_POSTER_PATH = 20
    final static int COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH = 21
    final static int COL_MOVIE_BASIC_BUDGET = 22
    final static int COL_MOVIE_BASIC_GENRE = 23
    final static int COL_MOVIE_BASIC_HOME_PAGE = 24
    final static int COL_MOVIE_BASIC_IMDB_ID = 25
    final static int COL_MOVIE_BASIC_PRODUCTION_COMPANIES = 26
    final static int COL_MOVIE_BASIC_PRODUCTION_COUNTRIES = 27
    final static int COL_MOVIE_BASIC_REVENUE = 28
    final static int COL_MOVIE_BASIC_RUNTIME = 29
    final static int COL_MOVIE_BASIC_RELEASE_STATUS = 30
    final static int COL_MOVIE_BASIC_TAGLINE = 31
    final static int COL_MOVIE_BASIC_CREATE_TIMESTAMP = 32
    final static int COL_MOVIE_BASIC_UPDATE_TIMESTAMP = 33

    //Columns to fetch from movie_basic_info table for similar movies
    private static final String[] SIMILAR_MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_SIMILAR_MOVIE_ID = 0
    final static int COL_SIMILAR_MOVIE_MOVIE_ID = 1
    final static int COL_SIMILAR_MOVIE_POSTER_PATH = 2
    final static int COL_SIMILAR_MOVIE_TITLE = 3
    final static int COL_SIMILAR_MOVIE_CATEGORY = 4
    final static int COL_SIMILAR_MOVIE_LINK_ID = 5

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
    private static
    final String[] MOVIE_RELEASE_INFO_COLUMNS = [MovieMagicContract.MovieReleaseDate._ID,
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

    //Columns to fetch from movie_review table
    private static final String[] MOVIE_REVIEW_COLUMNS = [MovieMagicContract.MovieReview._ID,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_REVIEW_ID = 0
    final static int COL_MOVIE_REVIEW_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_REVIEW_AUTHOR = 2
    final static int COL_MOVIE_REVIEW_CONTENT = 3

    //Columns to fetch from movie_user_list_flag table
    private static
    final String[] MOVIE_USER_LIST_FLAG_COLUMNS = [MovieMagicContract.MovieUserListFlag._ID,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_USER_LIST_FLAG_ID = 0
    final static int COL_MOVIE_USER_LIST_FLAG_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG = 2
    final static int COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG = 3
    final static int COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG = 4
    final static int COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG = 5
    final static int COL_MOVIE_USER_LIST_FLAG_USER_RATING = 6

    //An empty constructor is needed so that lifecycle is properly handled
    public DetailMovieFragment() {
        LogDisplay.callLog(LOG_TAG, 'DetailMovieFragment empty constructor is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu, this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment_menu, menu)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.detail_fragment_menu) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreateView is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        final Bundle args = getArguments()
        if (args) {
            mMovieId = args.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID)
            mMovieCategory = args.getString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY)
//            mMovieMagicMovieIdUri = args.getParcelable(GlobalStaticVariables.MOVIE_BASIC_INFO_URI) as Uri
//            mMovieId = MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(mMovieMagicMovieIdUri)
//            mMovieId = args.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID)
            //_ID_movie_basic_info is used as Integer in the subsequent calls, so it's also defined as
            //integer even though actually it's long
//            _ID_movie_basic_info = args.getLong(GlobalStaticVariables.MOVIE_BASIC_INFO_ROW_ID) as Integer
//            LogDisplay.callLog(LOG_TAG, "Fragment arguments.Movie Uri -> ${mMovieMagicMovieIdUri.toString()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Fragment arguments.Movie ID -> $mMovieId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Fragment arguments.Movie Category -> $mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//            LogDisplay.callLog(LOG_TAG,"Fragment arguments.Movie Row ID -> $_ID_movie_basic_info",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Could not parse fragment data', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
        //inflate the view before referring any view using id
        View mRootView = inflater.inflate(R.layout.fragment_detail_movie, container, false)
        //TODO: Need to verify if this animation is working or not
        //Set a fade animation
        final Animation fadeIn = new AlphaAnimation(0, 1)
        fadeIn.setInterpolator(new DecelerateInterpolator())
        fadeIn.setDuration(2000)
//        fadeIn.setFillAfter(true)
        mRootView.setAnimation(fadeIn)

        mAppBarLayout = mRootView.findViewById(R.id.movie_detail_app_bar_layout) as AppBarLayout
        mBackdropImage1 = mRootView.findViewById(R.id.movie_detail_backdrop_image_1) as ImageView
        mBackdropImage2 = mRootView.findViewById(R.id.movie_detail_backdrop_image_2) as ImageView
        mUserListDrawableTitle = mRootView.findViewById(R.id.movie_detail_user_list_drawable_title) as TextView
        /**
         * Image switcher onClick handling
         */
        mBackdropImageSwitcher = mRootView.findViewById(R.id.movie_detail_backdrop_image_switcher) as ImageSwitcher
        mBackdropImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'Backdrop imageSwitcher is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                LogDisplay.callLog(LOG_TAG, "Backdrop imageSwitcher is clicked.mBackdropImageCounter-> $mBackdropImageCounter", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mBackdropImageCounter--
                if(mBackdropList && mBackdropImageCounter >= 0) {
                    mCallbackForBackdropImageClick.onBackdropImageClicked(mMovieTitle, mBackdropImageCounter, mBackdropList as ArrayList<String>)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Backdrop image list is null or image loading not started', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
        })

        //All the layouts
        mDetailMovieLayout = mRootView.findViewById(R.id.fragment_detail_movie_layout) as LinearLayout
//        mDetailTitleLayout = mRootView.findViewById(R.id.movie_detail_title_layout) as LinearLayout
//        mDetailPosterLayout = mRootView.findViewById(R.id.movie_detail_poster_layout) as LinearLayout
//        mDetailTmdbRatingLayout = mRootView.findViewById(R.id.movie_detail_tmdb_rating_layout) as LinearLayout
//        mDetailUserRatingLayout = mRootView.findViewById(R.id.movie_detail_user_rating_layout) as LinearLayout
//        mDetailSynopsisLayout = mRootView.findViewById(R.id.movie_detail_synopsis_layout) as LinearLayout
//        mDetailTrailerLayout = mRootView.findViewById(R.id.movie_detail_trailer_layout) as LinearLayout
//        mDetailProductionInfoLayout = mRootView.findViewById(R.id.movie_detail_production_info_layout) as LinearLayout
//        mDetailCastHeaderLayout = mRootView.findViewById(R.id.movie_detail_cast_header_layout) as LinearLayout
//        mDetailsCastGridLayout = mRootView.findViewById(R.id.movie_detail_cast_grid_layout) as FrameLayout
//        mDetailCrewHeaderLayout = mRootView.findViewById(R.id.movie_detail_crew_header_layout) as LinearLayout
//        mDetailCrewGridLayout = mRootView.findViewById(R.id.movie_detail_crew_grid_layout) as FrameLayout
//        mDetailSimilarMovieHeaderLayout = mRootView.findViewById(R.id.movie_detail_similar_movie_header_layout) as LinearLayout
//        mDetailSimilarMovieGridLayout = mRootView.findViewById(R.id.movie_detail_similar_movie_grid_layout) as FrameLayout
//        mDetailCollectionLayout = mRootView.findViewById(R.id.movie_detail_collection_layout) as LinearLayout
//        mDetailWebLinkLayout = mRootView.findViewById(R.id.movie_detail_web_links_layout) as LinearLayout
//        mDetailReviewHeaderLayout = mRootView.findViewById(R.id.movie_detail_review_header_layout) as LinearLayout
//        mDetailReviewRecyclerViewLayout = mRootView.findViewById(R.id.movie_detail_review_recycler_view_layout) as FrameLayout
        mUserListDrawableLayout = mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout) as LinearLayout

        //All the header (fixed text) fields & buttons
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
        mMovieTrailerEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_trailer_empty_msg_text_view) as TextView
        mProdCompanyHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy_header) as TextView
        mProdCountryHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_country_header) as TextView
        mCastHeaderTextView = mRootView.findViewById(R.id.movie_detail_cast_header) as TextView
        mCrewHeaderTextView = mRootView.findViewById(R.id.movie_detail_crew_header) as TextView
        mSimilarMovieHeaderTextView = mRootView.findViewById(R.id.movie_detail_similar_movie_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mReviewHeaderTextView = mRootView.findViewById(R.id.movie_detail_review_header) as TextView
        mRecyclerViewEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_review_recycler_view_empty_msg_text_view) as TextView
        mExternalLinkHeader = mRootView.findViewById(R.id.movie_detail_web_links_header) as TextView
        mUserListDrawableTitle = mRootView.findViewById(R.id.movie_detail_user_list_drawable_title) as TextView
        /**
         * User list button handling
         */
        mImageButtonWatched = mRootView.findViewById(R.id.movie_detail_user_list_drawable_watched) as ImageButton
        mImageButtonWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Watched Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, mPalletePrimaryColor, mPalleteBodyTextColor)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonWatched.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WATCHED, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonWatched.setColorFilter(null)
                        //If user list movie then go back to previous activity as user already unchecked it
                        if (mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED) {
                            //TODO: will decide later if this function is needed. If not needed then remove the callback
//                            mUserListButtonClickCallback.finishCurrentActivity()
                        }
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WATCHED, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonWatched.setColorFilter(mPalleteAccentColor)
                    }
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getActivity().getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        mImageButtonWishList = mRootView.findViewById(R.id.movie_detail_user_list_drawable_wish_list) as ImageButton
        mImageButtonWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton WishList Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, mPalletePrimaryColor, mPalleteBodyTextColor)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonWishList.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WISH_LIST, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonWishList.setColorFilter(null)
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WISH_LIST, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonWishList.setColorFilter(mPalleteAccentColor)
                    }
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getActivity().getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
        mImageButtonFavourite = mRootView.findViewById(R.id.movie_detail_user_list_drawable_favourite) as ImageButton
        mImageButtonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Favourite Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, mPalletePrimaryColor, mPalleteBodyTextColor)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonFavourite.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_FAVOURITE, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonFavourite.setColorFilter(null)
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_FAVOURITE, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonFavourite.setColorFilter(mPalleteAccentColor)
                    }
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getActivity().getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
        mImageButtonCollection = mRootView.findViewById(R.id.movie_detail_user_list_drawable_collection) as ImageButton
        mImageButtonCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Favourite Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, mPalletePrimaryColor, mPalleteBodyTextColor)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonCollection.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_COLLECTION, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonCollection.setColorFilter(null)
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_COLLECTION, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonCollection.setColorFilter(mPalleteAccentColor)
                    }
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getActivity().getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }
            }
        })
        //All the dynamic fields (data fields) & ratingbar
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
        /**
         * User rating bar handling
         */
        mUserRatingBar = mRootView.findViewById(R.id.movie_detail_user_rating_bar) as RatingBar
        mUserRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                LogDisplay.callLog(LOG_TAG, "onRatingChanged:User rating bar value->$rating", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, mPalletePrimaryColor, mPalleteBodyTextColor)
                    final String[] updateUserListArgs
                    //If the rating value is zero then remove it
                    if (rating == 0.0) {
                        //Pass the third parameter as rating param
                        LogDisplay.callLog(LOG_TAG, 'onRatingChanged:User rating remove', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_USER_RATING, GlobalStaticVariables.USER_RATING_REMOVE_FLAG, String.valueOf(rating)]
                        updateUserList.execute(updateUserListArgs)
                    } else { //Else if rating > 0.0 then add / update it
                        //Pass the third parameter as rating param
                        LogDisplay.callLog(LOG_TAG, 'onRatingChanged:User rating add', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_USER_RATING, GlobalStaticVariables.USER_RATING_ADD_FLAG, String.valueOf(rating)]
                        updateUserList.execute(updateUserListArgs)
                    }
                }
            }
        })
        mTotalVoteCountTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_val) as TextView
        mTaglineTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline) as TextView
        mSynopsisTextView = mRootView.findViewById(R.id.movie_detail_synopsis) as TextView
        mProdCompanyTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy) as TextView
        mProdCountryTextView = mRootView.findViewById(R.id.movie_detail_production_info_country) as TextView
        mCollectionNameTextView = mRootView.findViewById(R.id.movie_detail_collection_name) as TextView
        /**
         * Collection backdrop image handling
         */
        mCollectionBackdropImageView = mRootView.findViewById(R.id.movie_detail_collection_image) as ImageView
        mCollectionBackdropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                if(mCollectionId > 0) {
                    LogDisplay.callLog(LOG_TAG, 'Collection backdrop image is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    //Create an intent for collection activity
                    final Uri uri = MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(mCollectionId)
                    final Intent intent = new Intent(getActivity(),CollectionMovieActivity.class)
                    intent.setData(uri)
                    getActivity().startActivity(intent)
                    //Start the animation
                    getActivity().overridePendingTransition(R.anim.slide_bottom_in_animation,0)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Invalid collection id.id->$mCollectionId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
        })
        /**
         * Movie Cast Grid handling
         */
        mHorizontalMovieCastGridView = mRootView.findViewById(R.id.movie_detail_cast_grid) as HorizontalGridView
        mCastGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_cast_grid_empty_msg_text_view) as TextView
//        mMovieCastLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
//        mHorizontalMovieCastGridView.setLayoutManager(mMovieCastLayoutManager)
        final GridLayoutManager castGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalMovieCastGridView.setLayoutManager(castGridLayoutManager)
        mMovieCastAdapter = new MovieCastAdapter(getActivity(), mCastGridEmptyMsgTextView)
        mHorizontalMovieCastGridView.setAdapter(mMovieCastAdapter)

        /**
         * Movie Crew Grid handling
         */
        //Create a layout manager which is used for similar movie, movie cast & crew grid
        mHorizontalMovieCrewGridView = mRootView.findViewById(R.id.movie_detail_crew_grid) as HorizontalGridView
        mCrewGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_crew_grid_empty_msg_text_view) as TextView
//        mMovieCrewLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
//        mHorizontalMovieCrewGridView.setLayoutManager(mMovieCrewLayoutManager)
        final GridLayoutManager crewGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalMovieCrewGridView.setLayoutManager(crewGridLayoutManager)
        mMovieCrewAdapter = new MovieCrewAdapter(getActivity(), mCrewGridEmptyMsgTextView)
        mHorizontalMovieCrewGridView.setAdapter(mMovieCrewAdapter)

        /**
         * Similar movie Grid handling
         */
        mHorizontalSimilarMovieGridView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid) as HorizontalGridView
        mSimilarMovieGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid_empty_msg_text_view) as TextView
//        mSimilarMovieLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
//        mHorizontalSimilarMovieGridView.setLayoutManager(mSimilarMovieLayoutManager)
        mSimilarMovieGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalSimilarMovieGridView.setLayoutManager(mSimilarMovieGridLayoutManager)
        mSimilarMovieAdapter = new SimilarMovieAdapter(getActivity(), mSimilarMovieGridEmptyMsgTextView)
        mHorizontalSimilarMovieGridView.setAdapter(mSimilarMovieAdapter)
        mHorizontalSimilarMovieGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState)
                LogDisplay.callLog(LOG_TAG, "onScrollStateChanged is called.", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }

            @Override
            void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy)
                LogDisplay.callLog(LOG_TAG, "onScrolled is called.dx=$dx & dy=$dy", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        })
        /**
         * External web link button handling
         */
        mHomePageButton = mRootView.findViewById(R.id.movie_detail_web_links_home_page_button) as Button
        mHomePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'Home Page Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                startHomePageIntent()
            }
        })
        mImdbLinkButton = mRootView.findViewById(R.id.movie_detail_web_links_imdb_link_button) as Button
        mImdbLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                LogDisplay.callLog(LOG_TAG, 'IMDb Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                startImdbIntent()
            }
        })
        /**
         * Review recycler view handling
         */
        mMovieReviewRecyclerView = mRootView.findViewById(R.id.movie_detail_review_recycler_view) as RecyclerView
        //Set this to false for smooth scrolling of recyclerview
        mMovieReviewRecyclerView.setNestedScrollingEnabled(false)
//        mMovieReviewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
//        mMovieReviewLayoutManager.setAutoMeasureEnabled(true)
//        mMovieReviewRecyclerView.setLayoutManager(mMovieReviewLayoutManager)
        final LayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.setAutoMeasureEnabled(true)
        mMovieReviewRecyclerView.setLayoutManager(linearLayoutManager)
        mMovieReviewAdapter = new MovieReviewAdapter(getActivity(), mRecyclerViewEmptyMsgTextView)
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter)

        return mRootView
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)
        //If savedInstanceState (i.e. in case of restore), restore the value of mMovieId
        if (savedInstanceState) {
            //TODO: looks like this is not needed, so commented out
//            mMovieId = savedInstanceState.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID, 0)
        }

        AppCompatActivity appCompatActivity = getActivity() as AppCompatActivity
        mToolbar = getView().findViewById(R.id.movie_detail_toolbar) as Toolbar
        if (mToolbar) {
            appCompatActivity.setSupportActionBar(mToolbar)
            //Enable back to home button
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }

        mCollapsingToolbar = getView().findViewById(R.id.movie_detail_collapsing_toolbar) as CollapsingToolbarLayout
        if (mCollapsingToolbar) {
            //Just clear off to be on the safe side
            mCollapsingToolbar.setTitle(" ")
        }

        mLocale = context.getResources().getConfiguration().locale.getCountry()
        LogDisplay.callLog(LOG_TAG, "Locale: $mLocale", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (mMovieId && mMovieCategory) {
//            mMovieRowIdArg = [Integer.toString(_ID_movie_basic_info)] as String[]
            mMovieIdArg = [Integer.toString(mMovieId)] as String[]
            mMovieIdCategoryArg = [Integer.toString(mMovieId), mMovieCategory] as String[]
            mVideoArg = [Integer.toString(mMovieId), MOVIE_VIDEO_SITE_YOUTUBE, MOVIE_VIDEO_SITE_TYPE] as String[]
            mReleaseInfoArg = [Integer.toString(mMovieId), mLocale] as String[]
            mMovieImageArg = [Integer.toString(mMovieId), GlobalStaticVariables.IMAGE_TYPE_BACKDROP] as String[]
        } else {
            //this is to safeguard any unwanted data fetch
//            mMovieRowIdArg = ['ZZZZZZ'] as String[]
            mMovieIdArg = ['ZZZZZZ'] as String[]
            mMovieIdCategoryArg = ['XXXXXX', 'YYYYY'] as String[]
            mVideoArg = ['XXXXXX', 'YYYYY', 'ZZZZZZ'] as String[]
            mReleaseInfoArg = ['YYYYY', 'ZZZZZZ'] as String[]
            mMovieImageArg = ['YYYYY', 'ZZZZZZ'] as String[]
        }
        //init all the loaders for fresh start or restart (for restore cases)
        if (savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID, null, this)
            getLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID, null, this)
        } else {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time, so restart loaders', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID, null, this)
            getLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID, null, this)
        }
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        //save the mMovieId, so that in case the fragment restores it can retrieve the value properly
        //TODO: looks like this is not needed, so commented out
//        outState.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,mMovieId)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader.loader id->$id", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID:
                //TODO: this cursor can still return more than one row for similar movies
                return new CursorLoader(
                        getActivity(),                                  //Parent Activity Context
//                        mMovieMagicMovieIdUri,                          //Uri of the movie id
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,  //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                       //Projection to return
//                        null,                                           //null as used movie id Uri
//                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? ",  //Selection Clause
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                           $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,  //Selection Clause
//                        null,                                           //null as used movie id Uri
//                        mMovieIdArg,                                 //Selection Arg
                        mMovieIdCategoryArg,                                 //Selection Arg
                        null)                                           //Only a single row is expected, so not sorted

            case MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                          //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                          //Table to query
                        SIMILAR_MOVIE_COLUMNS,                                                  //Projection to return
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID = ?", //Selection Clause
                        mMovieIdArg,                                                            //Selection Arg
                        null)                                                                   //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                        //Parent Activity Context
                        MovieMagicContract.MovieVideo.CONTENT_URI,                            //Table to query
                        MOVIE_VIDEO_COLUMNS,                                                  //Projection to return
                        """$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE = ? """,         //Selection Clause
                        mVideoArg,                                                            //Selection Arg
                        null)                                                                 //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                  //Parent Activity Context
                        MovieMagicContract.MovieCast.CONTENT_URI,                       //Table to query
                        MOVIE_CAST_COLUMNS,                                             //Projection to return
                        "$MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID = ?",  //Selection Clause
                        mMovieIdArg,                                                    //Selection Arg
                        MovieMagicContract.MovieCast.COLUMN_CAST_ORDER)                 //Sorted on the order

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                  //Parent Activity Context
                        MovieMagicContract.MovieCrew.CONTENT_URI,                       //Table to query
                        MOVIE_CREW_COLUMNS,                                             //Projection to return
                        "$MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID = ?",  //Selection Clause
                        mMovieIdArg,                                                    //Selection Arg
                        """$MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH desc,
                           $MovieMagicContract.MovieCrew.COLUMN_CREW_JOB asc""")        //Sorted on the profile path(desc), job(asc)

            case MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                               //Parent Activity Context
                        MovieMagicContract.MovieReleaseDate.CONTENT_URI,                             //Table to query
                        MOVIE_RELEASE_INFO_COLUMNS,                                                  //Projection to return
                        """$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY = ? """, //Selection Clause
                        mReleaseInfoArg,                                                             //Selection Arg
                        null)                                                                        //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                     //Parent Activity Context
                        MovieMagicContract.MovieImage.CONTENT_URI,                         //Table to query
                        MOVIE_IMAGE_COLUMNS,                                               //Projection to return
                        """$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE = ? """,      //Selection Clause
                        mMovieImageArg,                                                    //Selection Arg
                        null)                                                              //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                      //Parent Activity Context
                        MovieMagicContract.MovieReview.CONTENT_URI,                         //Table to query
                        MOVIE_REVIEW_COLUMNS,                                               //Projection to return
                        "$MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID = ?", //Selection Clause
                        mMovieIdArg,                                                        //Selection Arg
                        MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR)                //Sorted on the author

            case MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                                   //Parent Activity Context
                        MovieMagicContract.MovieUserListFlag.CONTENT_URI,                                //Table to query
                        MOVIE_USER_LIST_FLAG_COLUMNS,                                                    //Projection to return
                        "$MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID = ?", //Selection Clause
                        mMovieIdArg,                                                                     //Selection Arg
                        null)                                                                            //Sorting not used
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId()
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.loader id->$loaderId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
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
            case MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID:
                handleMovieReviewOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID:
                handleMovieUSerListFlagOnLoadFinished(data)
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown loader id. id->$loaderId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        LogDisplay.callLog(LOG_TAG, 'onLoaderReset is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //Reset the adapters
        mSimilarMovieAdapter.swapCursor(null)
        mMovieCastAdapter.swapCursor(null)
        mMovieCrewAdapter.swapCursor(null)
        mMovieReviewAdapter.swapCursor(null)
    }

    void handleMovieBasicOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            //_ID_movie_basic_info is used as Integer in the subsequent calls, so it's also defined as
            //integer even though actually it's long
            _ID_movie_basic_info = data.getInt(COL_MOVIE_BASIC_ID)
            LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Movie row id -> $_ID_movie_basic_info", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//            mMovieListType = data.getString(COL_MOVIE_BASIC_MOVIE_LIST_TYPE)
//            mMovieCategory = data.getString(COL_MOVIE_BASIC_MOVIE_CATEGORY)
            LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Movie Category -> ${data.getString(COL_MOVIE_BASIC_MOVIE_CATEGORY)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mOriginalBackdropPath = data.getString(COL_MOVIE_BASIC_BACKDROP_PATH)
            //TODO: some optimisation is needed here for this picasso call
            //Load the first backdrop, so that there is not much lag while displaying the first image
            //also it is cached
//            Picasso.with(getActivity())
//                    .load(mOriginalBackdropPath)
//                    .fit()
//                    .placeholder(R.drawable.grid_image_placeholder)
//                    .error(R.drawable.grid_image_error)
//                    .into(mBackdropImageSwitcher.getChildAt(0) as ImageView)
//
            mMovieTitle = data.getString(COL_MOVIE_BASIC_TITLE)
            mMovieTitleTextView.setText(mMovieTitle)
            if (data.getString(COL_MOVIE_BASIC_GENRE)) {
                mGenreTextView.setText(data.getString(COL_MOVIE_BASIC_GENRE))
            } else {
                mGenreTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getInt(COL_MOVIE_BASIC_RUNTIME) > 0) {
                mRunTimeTextView.setText(Utility.formatRunTime(getActivity(), data.getInt(COL_MOVIE_BASIC_RUNTIME)))
            } else {
                mRunTimeTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }

            final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                    "${data.getString(COL_MOVIE_BASIC_POSTER_PATH)}"
            //Create a picasso Callback
            final Callback picassoPosterCallback = new Callback() {
//                @Override
//                void onSuccess() {
//
//                }
//
//                @Override
//                void onError() {
//
//                }
//            }
//            Picasso.with(getActivity())
//                    .load(posterPath)
//                    .fit()
//                    .placeholder(R.drawable.grid_image_placeholder)
//                    .error(R.drawable.grid_image_error)
//                    .into(mPosterImageView, new Callback() {
                @Override
                void onSuccess() {
                    //TODO: Future change - provide a setting option to user to chose if they want this or will use default theme
                    final Bitmap bitmapPoster = ((BitmapDrawable) mPosterImageView.getDrawable()).getBitmap()
                    Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette p) {
                            Palette.Swatch vibrantSwatch = p.getVibrantSwatch()
                            Palette.Swatch lightVibrantSwatch = p.getLightVibrantSwatch()
                            Palette.Swatch darkVibrantSwatch = p.getDarkVibrantSwatch()
                            Palette.Swatch mutedSwatch = p.getMutedSwatch()
                            Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch()
                            Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch()
                            final boolean pickSwatchColorFlag = false
                            //Pick primary, primaryDark, title and body text color
                            if (vibrantSwatch) {
                                mPalletePrimaryColor = vibrantSwatch.getRgb()
                                mPalleteTitleColor = vibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = vibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = vibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                pickSwatchColorFlag = true
                            } else if (lightVibrantSwatch) { //Try another swatch
                                mPalletePrimaryColor = lightVibrantSwatch.getRgb()
                                mPalleteTitleColor = lightVibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = lightVibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = lightVibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                pickSwatchColorFlag = true
                            } else if (darkVibrantSwatch) { //Try last swatch
                                mPalletePrimaryColor = darkVibrantSwatch.getRgb()
                                mPalleteTitleColor = darkVibrantSwatch.getTitleTextColor()
                                mPalleteBodyTextColor = darkVibrantSwatch.getBodyTextColor()
                                //Produce Dark color by changing the value (3rd parameter) of HSL value
                                float[] primaryHsl = darkVibrantSwatch.getHsl()
                                primaryHsl[2] = primaryHsl[2] * 0.9f
                                mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                pickSwatchColorFlag = true
                            } else { //Fallback to default
                                LogDisplay.callLog(LOG_TAG, 'onGenerated:not able to pick color, so fallback', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                                mPalletePrimaryColor = ContextCompat.getColor(getActivity(), R.color.primary)
                                mPalletePrimaryDarkColor = ContextCompat.getColor(getActivity(), R.color.primary_dark)
                                mPalleteTitleColor = ContextCompat.getColor(getActivity(), R.color.white_color)
                                mPalleteBodyTextColor = ContextCompat.getColor(getActivity(), R.color.grey_color)
                                //This is needed as we are not going pick accent colour if falling back
                                mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                            }
                            //Pick accent color only if Swatch color is picked, otherwise do not pick accent color
                            if (pickSwatchColorFlag) {
                                if (mutedSwatch) {
                                    mPalleteAccentColor = mutedSwatch.getRgb()
                                } else if (mutedLightSwatch) { //Try another swatch
                                    mPalleteAccentColor = mutedLightSwatch.getRgb()
                                } else if (mutedDarkSwatch) { //Try last swatch
                                    mPalleteAccentColor = mutedDarkSwatch.getRgb()
                                } else { //Fallback to default
                                    mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                }
                            }
                            changeLayoutAndTextColor()
//                            mMovieTitleAndColorCallback.initializeActivityHostedTitleAndColor(mMovieTitle, mPalettePrimaryColor, mPalettePrimaryDarkColor, mPaletteTitleColor)
                            initializeTitleAndColor()
                            //Set the color for adapter fields and call method which in turn calls notifyDatasetChanged
//                            MovieCastAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
//                            MovieCastAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mMovieCastAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
//                            MovieCrewAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
//                            MovieCrewAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mMovieCrewAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
//                            SimilarMovieAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
//                            SimilarMovieAdapter.mBodyTextColor = mPalleteBodyTextColor
//                            mSimilarMovieAdapter.changeColor()
                            mSimilarMovieAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
//                            MovieReviewAdapter.mPrimaryColor = mPalletePrimaryColor
//                            MovieReviewAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
//                            MovieReviewAdapter.mTitleTextColor = mPalleteTitleColor
//                            MovieReviewAdapter.mBodyTextColor = mPalleteBodyTextColor
                            mMovieReviewAdapter.changeColor(mPalletePrimaryColor, mPalleteTitleColor, mPalleteBodyTextColor)
                            //Set the image button color
                            setImageButtonColor()
                        }
                    })
                }

                @Override
                void onError() {

                }
            }
            //Pass the Picasso Callback and load the image
            PicassoLoadImage.loadDetailFragmentPosterImage(getActivity(),posterPath,mPosterImageView,picassoPosterCallback)
//            final Animation fadeIn = new AlphaAnimation(0,1)
//            fadeIn.setInterpolator(new DecelerateInterpolator())
//            fadeIn.setDuration(2000)
//            mPosterImageView.setAnimation(fadeIn)
            //Default date is 1900-01-01 which is less than Unix epoc 1st Jan 1970, so converted milliseconds is negative
            if (data.getLong(COL_MOVIE_BASIC_RELEASE_DATE) > 0) {
                mReleaseDateTextView.setText(Utility.formatMilliSecondsToDate(data.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
            } else {
                mReleaseDateTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getInt(COL_MOVIE_BASIC_BUDGET) > 0) {
                mBudgetTextView.setText(Utility.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_BUDGET)))
            } else {
                mBudgetTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getInt(COL_MOVIE_BASIC_REVENUE)) {
                mRevenueTextView.setText(Utility.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_REVENUE)))
            } else {
                mRevenueTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getFloat(COL_MOVIE_BASIC_POPULARITY) > 0) {
                mPopularityTextView.setText(Float.toString(data.getFloat(COL_MOVIE_BASIC_POPULARITY)))
            } else {
                mPopularityTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getFloat(COL_MOVIE_BASIC_VOTE_AVG) > 0) {
                mTmdbRatingBar.setRating(data.getFloat(COL_MOVIE_BASIC_VOTE_AVG))
            } else {
                mTmdbRatingBar.setRating(0)
            }
            mTotalVoteCountTextView.setText(data.getString(COL_MOVIE_BASIC_VOTE_COUNT))
            if (data.getString(COL_MOVIE_BASIC_TAGLINE)) {
                mTaglineTextView.setText(data.getString(COL_MOVIE_BASIC_TAGLINE))
            } else {
                mTaglineTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getString(COL_MOVIE_BASIC_OVERVIEW) != '') {
                mSynopsisTextView.setText(data.getString(COL_MOVIE_BASIC_OVERVIEW))
            } else {
                mSynopsisTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getString(COL_MOVIE_BASIC_PRODUCTION_COMPANIES)) {
                mProdCompanyTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COMPANIES))
            } else {
                mProdCompanyTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            if (data.getString(COL_MOVIE_BASIC_PRODUCTION_COUNTRIES)) {
                mProdCountryTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COUNTRIES))
            } else {
                mProdCountryTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            mCollectionId = data.getInt(COL_MOVIE_BASIC_COLLECTION_ID)
            if (mCollectionId > 0) {
                mCollectionNameTextView.setText(data.getString(COL_MOVIE_BASIC_COLLECTION_NAME))
                mCollectionBackdropImageView.setVisibility(LinearLayout.VISIBLE)
                final String collectionBackdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                        "${data.getString(COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH)}"
                PicassoLoadImage.loadDetailFragmentCollectionBackdropImage(getActivity(), collectionBackdropPath, mCollectionBackdropImageView)
            } else {
                mCollectionNameTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                mCollectionBackdropImageView.setVisibility(LinearLayout.GONE)
            }

            LogDisplay.callLog(LOG_TAG, "homePage:${data.getString(COL_MOVIE_BASIC_HOME_PAGE)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            if (data.getString(COL_MOVIE_BASIC_HOME_PAGE)) {
                mMovieHomePageUrl = data.getString(COL_MOVIE_BASIC_HOME_PAGE)
                mHomePageButton.setText(getActivity().getString(R.string.movie_detail_web_links_home_page))
                mHomePageButton.setClickable(true)
                //TODO: Need to look into this Build.VERSION_CODES issue later
                //Somehow while running in Jelly bean & KITKAT it cannot find Build.VERSION_CODES.LOLLIPOP, yet to figure out why!
                //So using the API number (21 - LOLLIPOP)itself here and other places below
                if (Build.VERSION.SDK_INT >= 21) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                }
            } else {
                mHomePageButton.setText(getActivity().getString(R.string.movie_detail_web_links_home_page_not_available))
                mHomePageButton.setClickable(false)
                if (Build.VERSION.SDK_INT >= 21) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                }
            }
            LogDisplay.callLog(LOG_TAG, "IMDb ID:${data.getString(COL_MOVIE_BASIC_IMDB_ID)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            if (data.getString(COL_MOVIE_BASIC_IMDB_ID)) {
                mMovieImdbId = data.getString(COL_MOVIE_BASIC_IMDB_ID)
                mImdbLinkButton.setText(getActivity().getString(R.string.detail_web_links_imdb_link))
                mImdbLinkButton.setClickable(true)
                if (Build.VERSION.SDK_INT >= 21) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                }
            } else {
                mImdbLinkButton.setText(getActivity().getString(R.string.movie_detail_web_links_imdb_link_not_available))
                mImdbLinkButton.setClickable(false)
                if (Build.VERSION.SDK_INT >= 21) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                }
            }
            int detailDataPresentFlag = data.getInt(COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG)
            //If the flag is zero then all movie data not present, so go and fetch it
            if (detailDataPresentFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE) {
                LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Additional movie data not present, go and fetch it', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                final Integer[] movieIdArray = [mMovieId, _ID_movie_basic_info] as Integer[]
                new LoadMovieDetails(getActivity()).execute(movieIdArray)
//                new LoadMovieDetails(getActivity(), mMovieIdUri).execute(movieIdArray)
            } else {
                LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Additional movie data already present', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Record not found, should reach here only when movie is clicked on person screen - Movie id:$mMovieId, Category:$mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            if(mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_PERSON) {
                //Movie does not exists, go and fetch then insert into movie basic info table
                LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Movie does not exists, go and fetch then insert into movie basic info table', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                new LoadMovieDetails(getActivity()).execute([mMovieId,0] as Integer[])
            } else {
                LogDisplay.callLog(LOG_TAG, "Investigate how reached here - Movie id:$mMovieId, Category:$mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
    }

    void handleSimilarMovieOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleSimilarMovieOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //Show two rows if the count is greater than 6 otherwise show single row
        if(data.count >= 6) {
            mSimilarMovieGridLayoutManager.setSpanCount(2)
        }
        mSimilarMovieAdapter.swapCursor(data)
    }

    void handleMovieCastOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieCastOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCastAdapter.swapCursor(data)
    }

    void handleMovieCrewOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieCrewOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCrewAdapter.swapCursor(data)
    }

    void initiateYouTubeVideo(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "initiateYouTubeVideo.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        List<String> youtubeVideoKey
        if (data.moveToFirst()) {
            youtubeVideoKey = new ArrayList<>()
            for (i in 0..(data.count - 1)) {
                youtubeVideoKey.add(data.getString(COL_MOVIE_VIDEO_KEY))
                LogDisplay.callLog(LOG_TAG, "YouTube now_playing key= $youtubeVideoKey", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                data.moveToNext()
            }
            if(youtubeVideoKey.size() > 0) {
                mMovieTrailerEmptyMsgTextView.setVisibility(TextView.INVISIBLE)
                final MovieMagicYoutubeFragment movieMagicYoutubeFragment = MovieMagicYoutubeFragment
                        .createMovieMagicYouTubeFragment(youtubeVideoKey)
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.youtube_fragment_container, movieMagicYoutubeFragment)
                        .commit()
            } else {
                mMovieTrailerEmptyMsgTextView.setVisibility(TextView.VISIBLE)
                LogDisplay.callLog(LOG_TAG, 'Youtube video id is null', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
//            final MovieMagicYoutubeFragment movieMagicYoutubeFragment = getChildFragmentManager()
//                    .findFragmentById(R.id.movie_detail_trailer_fragment_youtube) as MovieMagicYoutubeFragment
//            if (movieMagicYoutubeFragment) {
//                LogDisplay.callLog(LOG_TAG, 'Youtube Fragment id is NOT null', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//                movieMagicYoutubeFragment.setVideoId(youtubeVideoKey)
//            } else {
//                LogDisplay.callLog(LOG_TAG, 'Youtube Fragment id is null', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//            }
        }
    }

    void populateMpaaImage(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "populateMpaaImage.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            final String mpaa = data.getString(COL_MOVIE_RELEASE_INFO_CERTIFICATION)
            LogDisplay.callLog(LOG_TAG, "Mpaa certification: $mpaa & Locale: $mLocale", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            final int mpaaIconResId = Utility.getIconResourceForMpaaRating(mpaa, mLocale)
            if (mpaaIconResId != -1) {
                mMpaaRatingImageView.setImageResource(mpaaIconResId)
            } else {
                LogDisplay.callLog(LOG_TAG, 'Utility.getIconResourceForMpaaRating returned -1', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mMpaaRatingImageView.setImageResource(R.drawable.not_available)
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'Not able to retrieve mpaa image, set default image', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mMpaaRatingImageView.setImageResource(R.drawable.not_available)
        }
    }

    void processBackdropImages(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "processBackdropImages.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            mBackdropList = new ArrayList<String>()
            //Add the main_activity_menu backdrop first
            if (mOriginalBackdropPath) {
                mBackdropList.add(mOriginalBackdropPath)
            }
            for (i in 0..(data.count - 1)) {
                mBackdropList.add(data.getString(COL_MOVIE_IMAGE_FILE_PATH))
                data.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG, "backdropImageArray-> $mBackdropList", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//            mBackdropCallback.initializeActivityHostedBackdrop(mBackdropList)
            initializeBackdrop(mBackdropList)
        }
    }

    void handleMovieReviewOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieReviewOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieReviewAdapter.swapCursor(data)
    }

    void handleMovieUSerListFlagOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieUSerListFlagOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //TODO: need to add the rest of the logic later
        mUserListWatchedFlag = false
        mUserListWishListdFlag = false
        mUserListFavouriteFlag = false
        mUserListCollectionFlag = false
        if (data.moveToFirst()) {
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListWatchedFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListWishListdFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListFavouriteFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListCollectionFlag = true
            }
            if (data.getFloat(COL_MOVIE_USER_LIST_FLAG_USER_RATING) > 0) {
                mUserRatingBar.setRating(data.getFloat(COL_MOVIE_USER_LIST_FLAG_USER_RATING))
            }
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity)
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mBackdropCallback = (BackdropCallback) activity
//            mMovieTitleAndColorCallback = (MovieTitleAndColorCallback) activity
//            mUserListButtonClickCallback = (UserListButtonClickCallback) activity
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement Callback interface")
//        }
//    }

    void changeLayoutAndTextColor() {
        //Change color for all the layouts
        mDetailMovieLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailTitleLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailPosterLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailTmdbRatingLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailUserRatingLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailSynopsisLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailTrailerLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailProductionInfoLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailCastHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailsCastGridLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailCrewHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailCrewGridLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailSimilarMovieHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailSimilarMovieGridLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailCollectionLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailWebLinkLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailReviewHeaderLayout.setBackgroundColor(mPalletePrimaryColor)
//        mDetailReviewRecyclerViewLayout.setBackgroundColor(mPalletePrimaryColor)
//        mUserListDrawableLayout.setBackgroundColor(mPalletePrimaryColor)

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
        mExternalLinkHeader.setTextColor(mPalleteTitleColor)
        mUserListDrawableTitle.setTextColor(mPalleteTitleColor)

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

        //Set the color of empty movie trailer message if it's visible
        if(mMovieTrailerEmptyMsgTextView.getVisibility() == TextView.VISIBLE) {
            mMovieTrailerEmptyMsgTextView.setTextColor(mPalleteBodyTextColor)
        }

        //Set user list ImageButton color
        mImageButtonWatched.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonWishList.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonFavourite.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonCollection.setBackgroundColor(mPalletePrimaryDarkColor)

        //Set button color
        final ColorStateList colorStateList = ColorStateList.valueOf(mPalletePrimaryDarkColor)
        //Somehow while running in Jelly bean & KITKAT it cannot find Build.VERSION_CODES.LOLLIPOP, yet to figure out why!
        //So using the API number (21 - LOLLIPOP)itself here
//        if (Build.VERSION.SDK_INT >= 21) {
//            mHomePageButton.setBackgroundTintList(colorStateList)
//            mImdbLinkButton.setBackgroundTintList(colorStateList)
//        } else {
//            ViewCompat.setBackgroundTintList(mHomePageButton, colorStateList)
//            ViewCompat.setBackgroundTintList(mImdbLinkButton, colorStateList)
//        }
        mHomePageButton.setBackgroundColor(mPalletePrimaryDarkColor)
        mImdbLinkButton.setBackgroundColor(mPalletePrimaryDarkColor)
        mHomePageButton.setTextColor(mPalleteBodyTextColor)
        mImdbLinkButton.setTextColor(mPalleteBodyTextColor)

        //Set ratingbar color. Used Compat so that can work below lollipop.
        //Above one - didn't use. No reason, just an example how to handle lollipop and below separately
        final Drawable tmdbRatingdrawable = mTmdbRatingBar.getProgressDrawable()
        DrawableCompat.setTint(tmdbRatingdrawable, mPalleteAccentColor)
        final Drawable userRatingDrawable = mUserRatingBar.getProgressDrawable()
        DrawableCompat.setTint(userRatingDrawable, mPalleteAccentColor)
    }

    void setImageButtonColor() {
        if (mUserListWatchedFlag) {
            mImageButtonWatched.setColorFilter(mPalleteAccentColor)
        }
        if (mUserListWishListdFlag) {
            mImageButtonWishList.setColorFilter(mPalleteAccentColor)
        }
        if (mUserListFavouriteFlag) {
            mImageButtonFavourite.setColorFilter(mPalleteAccentColor)
        }
        if (mUserListCollectionFlag) {
            mImageButtonCollection.setColorFilter(mPalleteAccentColor)
        }
    }

    /**
     * Intent to open a web browser when user clicks on movie home page button
     */
    void startHomePageIntent() {
        final Intent intent = new Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(mMovieHomePageUrl))
        startActivity(intent)
    }

    /**
     * Intent to open the movie in imdb app(if installed) or in web browser when user clicks on imdb button
     */
    void startImdbIntent() {
        final String imdbUrl = "$GlobalStaticVariables.IMDB_BASE_MOVIE_TITLE_URL$mMovieImdbId/"
        final Intent intent = new Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(imdbUrl))
        startActivity(intent)
    }

//    /**
//     * A callback interface that all activities containing this fragment must
//     * implement. This mechanism allows activities to be notified when movie title and color value
//     * are determined and ready for activity to update
//     */
//    public interface MovieTitleAndColorCallback {
//        /**
//         * DetailFragmentCallback for updating the Movie Title and Theme Color in Activity
//         */
//        public void initializeActivityHostedTitleAndColor(String movieTitle, int primaryColor, int primaryDarkColor, int titleColor)
//    }

//    /**
//     * A callback interface that all activities containing this fragment must
//     * implement. This mechanism allows activities to be notified when Backdrop image loader
//     * is finished and processed
//     */
//    public interface BackdropCallback {
//        /**
//         * DetailFragmentCallback for updating the Backdrop images in Activity
//         */
//        public void initializeActivityHostedBackdrop(List<String> backdropImagePathList)
//    }

//    /**
//     * A callback interface that all activities containing this fragment must
//     * implement. This mechanism allows activities to be notified when user list button is clicked
//     */
//    public interface UserListButtonClickCallback {
//        /**
//         * DetailFragmentCallback for updating the Backdrop images in Activity
//         */
//        //TODO:not using this, so may need cleanup at the end
//        public void finishCurrentActivity()
//    }

    void initializeTitleAndColor() {
        LogDisplay.callLog(LOG_TAG, 'initializeTitleAndColor is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//        mCollapsingToolbar.setTitle(movieTitle)
        mCollapsingToolbar.setStatusBarScrimColor(mPalletePrimaryDarkColor)
        mCollapsingToolbar.setContentScrimColor(mPalletePrimaryColor)
        mCollapsingToolbar.setBackgroundColor(mPalletePrimaryColor)
        mCollapsingToolbar.setCollapsedTitleTextColor(mPalleteTitleColor)

        //Show the title only when image is collapsed
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false
            int scrollRange = -1

            @Override
            void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                LogDisplay.callLog(LOG_TAG, 'onOffsetChanged is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange()
                }
//                LogDisplay.callLog(LOG_TAG, "scrollRange + verticalOffset:$scrollRange & $verticalOffset", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mMovieTitle)
                    isShow = true
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ")
                    isShow = false
                }
            }
        })
    }

    void initializeBackdrop(List<String> backdropImagePathList) {
        LogDisplay.callLog(LOG_TAG, 'initializeBackdrop is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        setBackdropAnimation = true
        final Animation fadeInAnimation = new AlphaAnimation(0, 1)
        fadeInAnimation.setInterpolator(new DecelerateInterpolator())
        fadeInAnimation.setDuration(3000)
        final int counter = 0
//        mBackdropImageCounter = 0
        Callback backdropPicassoCallback = new Callback() {
            @Override
            void onSuccess() {
                LogDisplay.callLog(LOG_TAG, 'Picasso:onSuccess is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mBackdropImageSwitcher.startAnimation(fadeInAnimation)
                mBackdropImageSwitcher.showNext()
                mBackdropImageCounter++
            }

            @Override
            void onError() {
                //TODO: need to identify if anything is needed
                LogDisplay.callLog(LOG_TAG, 'Picasso:onError is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
        mRunnable = new Runnable() {
            @Override
            void run() {
//                LogDisplay.callLog(LOG_TAG,"initializeBackdrop:picasoLoadComplete:$picasoLoadComplete",LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//                if (picasoLoadComplete) {
//                }
                final String backdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                        "${backdropImagePathList[counter]}"
                final int evenOrOdd = counter % 2
//                    LogDisplay.callLog(LOG_TAG, "evenOrOdd value:$evenOrOdd", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                final ImageView imageView
                //The order is important, do not alter - otherwise imageswitcher will not work properly!
                if (evenOrOdd == 0) {
                    imageView = mBackdropImageSwitcher.getChildAt(1) as ImageView
                } else {
                    imageView = mBackdropImageSwitcher.getChildAt(0) as ImageView
                }
                loadBackdropImage(backdropPath, imageView, counter, backdropPicassoCallback)
//                loadBackdropImage(backdropPath, fadeInAnimation, imageView, counter)
                counter++
                if (counter >= backdropImagePathList.size()) {
                    //Do not cycle through if the backdrop image count is 1
                    if (backdropImagePathList.size() > 1) {
                        counter = 0
                    }
                }
                mHandler.postDelayed(this, 10000) //Interval time is 10 seconds
            }
        }
        mHandler.postDelayed(mRunnable, 10) //Initial delay is 10 mili seconds
    }

    //TODO: Future change - provide a setting option to user to chose single backdrop for mobile data
    //TODO: Future change - provide a setting option to user to chose no image donwload over mobile data & fo better
    //TODO: implementation, put all picasso call in a single place(class that exists)
    void loadBackdropImage(String backdropPath, ImageView imageView, int counter, Callback callback) {
//    void loadBackdropImage(String backdropPath, Animation animation, ImageView imageView, int counter) {
        LogDisplay.callLog(LOG_TAG, 'loadBackdropImage is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //This is to avoid abrupt flipping of first 2 / 3 images - it happens because of Picasso background call timing
        //So for first three images no slide animation is used and it ensures no cluttering
        if (setBackdropAnimation && counter > 3) {
            mBackdropImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_in_animation))
            mBackdropImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_out_animation))
            setBackdropAnimation = false
        }
        //Load the backdrop using Picasso
        PicassoLoadImage.loadDetailFragmentBackdropImage(getActivity(),backdropPath,imageView,callback)
//        Picasso.with(getActivity())
//                .load(backdropPath)
//                .noPlaceholder()
//                .centerInside()
//                .fit()
//                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
//                .into(imageView, new Callback() {
//            @Override
//            void onSuccess() {
//                LogDisplay.callLog(LOG_TAG, 'Picasso:onSuccess is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//                mBackdropImageSwitcher.startAnimation(animation)
//                mBackdropImageSwitcher.showNext()
//            }
//
//            @Override
//            void onError() {
//                //TODO: need to identify if anything is needed
//                LogDisplay.callLog(LOG_TAG, 'Picasso:onError is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//            }
//        })
    }

    @Override
    void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onResume()
        mHandler.postDelayed(mRunnable,10000)
    }

    @Override
    void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStop()
        if(mHandler) {
            mHandler.removeCallbacksAndMessages(null)
        }
        //Cancel Picasso requests - required where callback (hard reference) is used
        //TODO this is not done everywhere, so need to do in other places
        Picasso.with(getActivity()).cancelRequest(mPosterImageView)
        Picasso.with(getActivity()).cancelRequest(mBackdropImageSwitcher.getChildAt(0) as ImageView)
        Picasso.with(getActivity()).cancelRequest(mBackdropImageSwitcher.getChildAt(1) as ImageView)
    }

    @Override
    void onDetach() {
        LogDisplay.callLog(LOG_TAG, 'onDetach is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDetach()
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG, 'onDestroyView is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDestroyView()
    }

    @Override
    void onDestroy() {
        LogDisplay.callLog(LOG_TAG, 'onDestroy is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDestroy()
    }

    //Overriding the animation for better performance
    //Reference - http://daniel-codes.blogspot.co.uk/2013/09/smoothing-performance-on-fragment.html
    //TODO: need to perform and see if it improves the transition or not
    @Override
    Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        LogDisplay.callLog(LOG_TAG, 'onCreateAnimation is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        final Animation animation = super.onCreateAnimation(transit, enter, nextAnim)
        //Hardware layer supported above 11
        if (Build.VERSION.SDK_INT >= 11) {
            if (animation == null && nextAnim != 0) {
                animation = AnimationUtils.loadAnimation(getActivity(), nextAnim)
            }
            if (animation != null) {
                getView().setLayerType(View.LAYER_TYPE_HARDWARE, null)
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    void onAnimationStart(Animation anim) {
                        getView().setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                    @Override
                    void onAnimationEnd(Animation anim) {
                    }
                    @Override
                    void onAnimationRepeat(Animation anim) {
                    }
                })
            }
        }
        return animation
    }

    @Override
    public void onAttach(Context context) {
        LogDisplay.callLog(LOG_TAG,'onAttach is called',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if(context instanceof Activity) {
                mCallbackForBackdropImageClick = (CallbackForBackdropImageClick) context
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForImageClick interface")
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of person crew movie
     * item click.
     */
    public interface CallbackForBackdropImageClick {
        /**
         * BackdropImageClickCallback when backdrop image switcher item is clicked
         */
        public void onBackdropImageClicked(String title, int position, ArrayList<String> backdropImageFilePath)
    }
}