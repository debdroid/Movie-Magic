package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.HomeMovieAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment;
import groovy.transform.CompileStatic

@CompileStatic
class HomeMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = HomeMovieFragment.class.getSimpleName()

    private RecyclerView mInCinemaRecyclerView
    private RecyclerView mComingSoonRecyclerView
    private RecyclerView mRecentlyAddedUserListRecyclerView
    private RecyclerView mRecommendationRecyclerView
    private TextView mInCinemaRecyclerViewEmptyTextView
    private TextView mComingSoonRecyclerViewEmptyTextView
    private TextView mRecentlyAddedUserListRecyclerViewEmptyTextView
    private TextView mRecommendationRecyclerViewEmptyTextView
    private TextView mYouTubeFragmentEmptyTextView
    private Button mShowAllInCinemaButton
    private Button mShowAllComingSoonButton
    private HomeMovieAdapter mInCinemaAdapter
    private HomeMovieAdapter mComingSoonAdapter
    private HomeMovieAdapter mRecentlyAddedUserListAdapter
    private HomeMovieAdapter mRecommendationAdapter
    private String[] mMovieVideoArg
    private CallbackForHomeMovieClick mCallbackForHomeMovieClick
    private CallbackForShowAllButtonClick mCallbackForShowAllButtonClick
    private LinearLayout mRecommendationLayout
    private View mRecommendationDivider
    private FrameLayout mYouTubeFragmentContainer

    private static final int HOME_MOVIE_FRAGMENT_VIEW_PAGER_LOADER_ID = 0
    private static final int HOME_MOVIE_FRAGMENT_IN_CINEMA_LOADER_ID = 1
    private static final int HOME_MOVIE_FRAGMENT_COMING_SOON_LOADER_ID = 2
    private static final int HOME_MOVIE_FRAGMENT_RECENTLY_ADDED_USER_LIST_LOADER_ID = 3
    private static final int HOME_MOVIE_FRAGMENT_RECOMMENDATION_LOADER_ID = 4

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

    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_GENRE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG]

    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_BACKDROP_PATH = 2
    final static int COL_MOVIE_BASIC_TITLE = 3
    final static int COL_MOVIE_BASIC_RELEASE_DATE = 4
    final static int COL_MOVIE_BASIC_POSTER_PATH = 5
    final static int COL_MOVIE_BASIC_MOVIE_CATEGORY = 6
    final static int COL_MOVIE_BASIC_MOVIE_LIST_TYPE = 7
    final static int COL_MOVIE_BASIC_GENRE = 8
    final static int COL_MOVIE_BASIC_RUN_TIME = 9
    final static int COL_MOVIE_BASIC_RELEASE_STATUS = 10
    final static int COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG = 11

    //An empty constructor is needed so that lifecycle is properly handled
    public HomeMovieFragment(){
        LogDisplay.callLog(LOG_TAG,'HomeMovieFragment empty constructor is called',LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
//        setHasOptionsMenu(true)
    }

    @Override
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreateView is called',LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        View mRootView = inflater.inflate(R.layout.fragment_home_movie,container,false)
        mYouTubeFragmentEmptyTextView = mRootView.findViewById(R.id.home_youtube_fragment_empty_msg) as TextView
        mInCinemaRecyclerView = mRootView.findViewById(R.id.home_movie_in_cinema_recycler_view) as RecyclerView
        mInCinemaRecyclerViewEmptyTextView = mRootView.findViewById(R.id.home_movie_in_cinema_recycler_view_empty_msg_text_view) as TextView
        mComingSoonRecyclerView = mRootView.findViewById(R.id.home_movie_coming_soon_recycler_view) as RecyclerView
        mComingSoonRecyclerViewEmptyTextView = mRootView.findViewById(R.id.home_movie_coming_soon_recycler_view_empty_msg_text_view) as TextView
        mRecentlyAddedUserListRecyclerView = mRootView.findViewById(R.id.home_movie_recently_added_recycler_view) as RecyclerView
        mRecentlyAddedUserListRecyclerViewEmptyTextView = mRootView.findViewById(R.id.home_movie_recently_added_recycler_view_empty_msg_text_view) as TextView
        mRecommendationRecyclerView = mRootView.findViewById(R.id.home_movie_recommendation_recycler_view) as RecyclerView
        mRecommendationRecyclerViewEmptyTextView = mRootView.findViewById(R.id.home_movie_recommendation_recycler_view_empty_msg_text_view) as TextView
        mYouTubeFragmentContainer = mRootView.findViewById(R.id.home_youtube_fragment_container) as FrameLayout
        //Set this to false so that activity starts the page from the beginning
        mComingSoonRecyclerView.setFocusable(false)
        mRecentlyAddedUserListRecyclerView.setNestedScrollingEnabled(false)
        /**
         * In Cinema Recycler View
         */
        final RecyclerView.LayoutManager inCinemaLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        inCinemaLinearLayoutManager.setAutoMeasureEnabled(true)
        mInCinemaRecyclerView.setLayoutManager(inCinemaLinearLayoutManager)
        //Set this to false for smooth scrolling of recyclerview
        mInCinemaRecyclerView.setNestedScrollingEnabled(false)
        mInCinemaRecyclerView.setFocusable(false)
        mInCinemaAdapter = new HomeMovieAdapter(getActivity(), mInCinemaRecyclerViewEmptyTextView,
                new HomeMovieAdapter.HomeMovieAdapterOnClickHandler(){
                    @Override
                    void onClick(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder) {
                        mCallbackForHomeMovieClick.onHomeMovieItemSelected(movieId,movieCategory,viewHolder)
                    }
                })
        mInCinemaRecyclerView.setAdapter(mInCinemaAdapter)
        /**
         * Coming Soon Recycler View
         */
        final RecyclerView.LayoutManager comingSoonLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        comingSoonLinearLayoutManager.setAutoMeasureEnabled(true)
        mComingSoonRecyclerView.setLayoutManager(comingSoonLinearLayoutManager)
        //Set this to false for smooth scrolling of recyclerview
        mComingSoonRecyclerView.setNestedScrollingEnabled(false)
        //Set this to false so that activity starts the page from the beginning
        mComingSoonRecyclerView.setFocusable(false)
        mComingSoonAdapter = new HomeMovieAdapter(getActivity(), mComingSoonRecyclerViewEmptyTextView,
                new HomeMovieAdapter.HomeMovieAdapterOnClickHandler(){
                    @Override
                    void onClick(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder) {
                        mCallbackForHomeMovieClick.onHomeMovieItemSelected(movieId,movieCategory,viewHolder)
                    }
                })
        mComingSoonRecyclerView.setAdapter(mComingSoonAdapter)
        /**
         * Recently Added User List Recycler View
         */
        final RecyclerView.LayoutManager recentlyAddedUserListLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        recentlyAddedUserListLinearLayoutManager.setAutoMeasureEnabled(true)
        mRecentlyAddedUserListRecyclerView.setLayoutManager(recentlyAddedUserListLinearLayoutManager)
        //Set this to false for smooth scrolling of recyclerview
        mRecentlyAddedUserListRecyclerView.setNestedScrollingEnabled(false)
        //Set this to false so that activity starts the page from the beginning
        mRecentlyAddedUserListRecyclerView.setFocusable(false)
        mRecentlyAddedUserListAdapter = new HomeMovieAdapter(getActivity(), mRecentlyAddedUserListRecyclerViewEmptyTextView,
                new HomeMovieAdapter.HomeMovieAdapterOnClickHandler(){
                    @Override
                    void onClick(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder) {
                        mCallbackForHomeMovieClick.onHomeMovieItemSelected(movieId,movieCategory,viewHolder)
                    }
                })
        mRecentlyAddedUserListRecyclerView.setAdapter(mRecentlyAddedUserListAdapter)
        /**
         * Recommendation Recycler View
         */
        mRecommendationLayout = mRootView.findViewById(R.id.home_movie_recommendation_layout) as LinearLayout
        mRecommendationDivider = mRootView.findViewById(R.id.home_movie_recommendation_divider) as View
        final RecyclerView.LayoutManager recommendationLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        recommendationLinearLayoutManager.setAutoMeasureEnabled(true)
        mRecommendationRecyclerView.setLayoutManager(recommendationLinearLayoutManager)
        //Set this to false for smooth scrolling of recyclerview
        mRecommendationRecyclerView.setNestedScrollingEnabled(false)
        //Set this to false so that activity starts the page from the beginning
        mRecommendationRecyclerView.setFocusable(false)
        mRecommendationAdapter = new HomeMovieAdapter(getActivity(), mRecommendationRecyclerViewEmptyTextView,
                new HomeMovieAdapter.HomeMovieAdapterOnClickHandler(){
                    @Override
                    void onClick(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder) {
                        mCallbackForHomeMovieClick.onHomeMovieItemSelected(movieId,movieCategory,viewHolder)
                    }
                })
        mRecommendationRecyclerView.setAdapter(mRecommendationAdapter)

        mShowAllInCinemaButton = mRootView.findViewById(R.id.show_all_in_cinema_button) as Button
        /**
         * Show all In Cinemas Button click handling
         */
        mShowAllInCinemaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                mCallbackForShowAllButtonClick.onShowAllButtonClicked(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
            }
        })

        mShowAllComingSoonButton = mRootView.findViewById(R.id.show_all_coming_soon_button) as Button
        /**
         * Show all Coming Soon Button click handling
         */
        mShowAllComingSoonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(View v) {
                mCallbackForShowAllButtonClick.onShowAllButtonClicked(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
            }
        })

        return mRootView
    }

    @Override
    void onActivityCreated(Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)
        mMovieVideoArg = [GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE, GlobalStaticVariables.MOVIE_VIDEO_SITE_YOUTUBE, GlobalStaticVariables.MOVIE_VIDEO_SITE_TYPE] as String[]
        //If it's a fresh start then call init loader
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(HOME_MOVIE_FRAGMENT_VIEW_PAGER_LOADER_ID, null, this)
            getLoaderManager().initLoader(HOME_MOVIE_FRAGMENT_IN_CINEMA_LOADER_ID, null, this)
            getLoaderManager().initLoader(HOME_MOVIE_FRAGMENT_COMING_SOON_LOADER_ID, null, this)
            getLoaderManager().initLoader(HOME_MOVIE_FRAGMENT_RECENTLY_ADDED_USER_LIST_LOADER_ID, null, this)
            getLoaderManager().initLoader(HOME_MOVIE_FRAGMENT_RECOMMENDATION_LOADER_ID, null, this)
        } else {        //If it's restore then restart the loader
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time, so restart loaders', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().restartLoader(HOME_MOVIE_FRAGMENT_VIEW_PAGER_LOADER_ID, null, this)
            getLoaderManager().restartLoader(HOME_MOVIE_FRAGMENT_IN_CINEMA_LOADER_ID, null, this)
            getLoaderManager().restartLoader(HOME_MOVIE_FRAGMENT_COMING_SOON_LOADER_ID, null, this)
            getLoaderManager().restartLoader(HOME_MOVIE_FRAGMENT_RECENTLY_ADDED_USER_LIST_LOADER_ID, null, this)
            getLoaderManager().restartLoader(HOME_MOVIE_FRAGMENT_RECOMMENDATION_LOADER_ID, null, this)
        }
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader.loader id->$id", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case HOME_MOVIE_FRAGMENT_VIEW_PAGER_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                        //Parent Activity Context
                        MovieMagicContract.MovieVideo.CONTENT_URI,                            //Table to query
                        MOVIE_VIDEO_COLUMNS,                                                  //Projection to return
                        """$MovieMagicContract.MovieVideo.COLUMN_VIDEO_FOR_HOME_PAGE_USE_FLAG = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE = ? """,         //Selection Clause
                        mMovieVideoArg,                                                       //Selection Arg
                        "$MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID asc ")          //Sorted on foreign key which ensures now playing entries comes first

            case HOME_MOVIE_FRAGMENT_IN_CINEMA_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                          //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                          //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                                               //Projection to return
                        // The conditions used here are same as what used in loadMovieDetailsForHomePageItems
                        // method of MovieMagicSyncAdapter . If this changes then change that
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? """,       //Selection Clause
                        [GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, ''] as String[],     //Selection Arg
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER") //Sorted on release date

            case HOME_MOVIE_FRAGMENT_COMING_SOON_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                          //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                          //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                                               //Projection to return
                        // The conditions used here are same as what used in loadMovieDetailsForHomePageItems
                        // method of MovieMagicSyncAdapter . If this changes then change that
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? """,       //Selection Clause
                        [GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, '', ''] as String[],    //Selection Arg
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER") //Sorted on release date

            case HOME_MOVIE_FRAGMENT_RECENTLY_ADDED_USER_LIST_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                              //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                              //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                                                   //Projection to return
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? """,            //Selection Clause
                        [GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST, '', ''] as String[], //Selection Arg
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER") //Sorted on release date

            case HOME_MOVIE_FRAGMENT_RECOMMENDATION_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                              //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                              //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                                                   //Projection to return
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_GENRE <> ? """, //Selection Clause
                        [GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_RECOMMENDATIONS, '', '', ''] as String[],                    //Selection Arg
                        /** Recommendations has no grid page, so showing 10 instead of standard 6 **/
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP desc limit 10") //Sorted on release date
        }
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId()
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.loader id->$loaderId", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        switch (loaderId) {
            case HOME_MOVIE_FRAGMENT_VIEW_PAGER_LOADER_ID:
                handleTrailerOnLoadFinished(data)
                break
            case HOME_MOVIE_FRAGMENT_IN_CINEMA_LOADER_ID:
                handleInCinemaOnLoadFinished(data)
                break
            case HOME_MOVIE_FRAGMENT_COMING_SOON_LOADER_ID:
                handleComingSoonOnLoadFinished(data)
                break
            case HOME_MOVIE_FRAGMENT_RECENTLY_ADDED_USER_LIST_LOADER_ID:
                recentlyAddedUserListOnLoadFinished(data)
                break
            case HOME_MOVIE_FRAGMENT_RECOMMENDATION_LOADER_ID:
                recommendationOnLoadFinished(data)
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown loader id. id->$loaderId", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        LogDisplay.callLog(LOG_TAG, 'onLoaderReset is called', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        //Reset the adapters
        mInCinemaAdapter.swapCursor(null)
        mComingSoonAdapter.swapCursor(null)
        mRecentlyAddedUserListAdapter.swapCursor(null)
        mRecommendationAdapter.swapCursor(null)
    }

    /**
     * This method handles the video (movie trailer) cursor
     * @param data Cursor
     */
    void handleTrailerOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleTrailerOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        List<String> youtubeVideoKey = new ArrayList<>()
        if (data.moveToFirst()) {
            for (i in 0..(data.count - 1)) {
                youtubeVideoKey.add(data.getString(COL_MOVIE_VIDEO_KEY))
                data.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG, "YouTube now_playing key= $youtubeVideoKey", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        }
        if(youtubeVideoKey.size() > 0) {
            if (!Utility.isReducedDataOn(getActivity())) {
                mYouTubeFragmentEmptyTextView.setVisibility(TextView.GONE)
                mYouTubeFragmentContainer.setVisibility(FrameLayout.VISIBLE)
                final MovieMagicYoutubeFragment movieMagicYoutubeFragment = MovieMagicYoutubeFragment
                        .createMovieMagicYouTubeFragment(youtubeVideoKey)
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.home_youtube_fragment_container, movieMagicYoutubeFragment)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'User selected reduced data use', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
                mYouTubeFragmentContainer.setVisibility(FrameLayout.GONE)
                mYouTubeFragmentEmptyTextView.setVisibility(TextView.VISIBLE)
                mYouTubeFragmentEmptyTextView.setText(getString(R.string.reduced_data_use_on_message))
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'Youtube video id is null', LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    /**
     * This method handles the in cinema (i.e. Now Playing) movie cursor
     * @param data Cursor
     */
    void handleInCinemaOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleInCinemaOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        mInCinemaAdapter.swapCursor(data)
    }

    /**
     * This method handles the coming soon (i.e. Upcoming) movie cursor
     * @param data Cursor
     */
    void handleComingSoonOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleComingSoonOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        mComingSoonAdapter.swapCursor(data)
    }

    /**
     * This method handles the recently added user list (i.e. Watched or Wishlist or Favourite or Collection) movie cursor
     * @param data Cursor
     */
    void recentlyAddedUserListOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "recentlyAddedUserListOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        mRecentlyAddedUserListAdapter.swapCursor(data)
    }

    /**
     * This method handles the recommendation (i.e. recommended movies loaded when user views movie detail) movie cursor
     * @param data Cursor
     */
    void recommendationOnLoadFinished(Cursor data) {
        LogDisplay.callLog(LOG_TAG, "recommendationOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        // Read the user's preference and show / hide recommended movies accordingly
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity())
        final boolean recommendedFlag = sharedPreferences.getBoolean(getString(R.string.pref_recommendation_key),false)
        if(recommendedFlag) {
            mRecommendationLayout.setVisibility(LinearLayout.VISIBLE)
            mRecommendationDivider.setVisibility(View.VISIBLE)
            mRecommendationAdapter.swapCursor(data)
        } else {
            mRecommendationLayout.setVisibility(LinearLayout.GONE)
            mRecommendationDivider.setVisibility(View.GONE)
            mRecommendationAdapter.swapCursor(null)
        }
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        LogDisplay.callLog(LOG_TAG,'onSaveInstanceState is called',LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        super.onSaveInstanceState(outState)
    }


    @Override
    public void onAttach(Context context) {
        LogDisplay.callLog(LOG_TAG,'onAttach is called',LogDisplay.HOME_MOVIE_FRAGMENT_LOG_FLAG)
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if(context instanceof Activity) {
                mCallbackForHomeMovieClick = (CallbackForHomeMovieClick) context
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForHomeMovieClick interface")
        }
        try {
            if(context instanceof Activity) {
                mCallbackForShowAllButtonClick = (CallbackForShowAllButtonClick) context
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForShowAllButtonClick interface")
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of home movie
     * item click.
     */
    public interface CallbackForHomeMovieClick {
        /**
         * HomeMovieFragmentCallback when a movie item has been clicked on home page
         */
        public void onHomeMovieItemSelected(int movieId, String movieCategory, HomeMovieAdapter.HomeMovieAdapterViewHolder viewHolder)
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of show all
     * button clicked for in cinemas or upcoming
     */
    public interface CallbackForShowAllButtonClick {
        /**
         * HomeMovieFragmentCallback when a movie item has been clicked on home page
         */
        public void onShowAllButtonClicked(String movieCategory)
    }
}