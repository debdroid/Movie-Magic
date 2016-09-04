package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import android.support.v7.widget.Toolbar
import com.moviemagic.dpaul.android.app.adapter.MovieGridAdapter
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMoreMovieData
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class GridMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = GridMovieFragment.class.getSimpleName()
    private static final String STATE_MOVIE_CATEGORY = 'movie_Category'
    //This is to indicate the start page for the more load. Driven by the value used in syncadapter (i.e. total
    //number of pages already downloaded)
    private int mStartPage = 0
    //This is to hold the current page of the data.
    private int mCurrentPage = mStartPage
    //To hold the previous count of the total records
    private int mPreviousRecordCount = 0
    //To hold the threashHold to load next pag  e. Currently set to 20 (i.e. one page worth of data)
    private int mThreasholdCount = 20
    //Boolean to indicate if more data is being loaded
    private boolean isMoreDataToLoad = true
    //Boolean to track if the API call was successful
    public static boolean isDataLoadFailed = false
    //Re-try counter in case API call failed
    private int mReTryCounter = 0

    private Callback mCallback
    private GridView mGridView
    private MovieGridAdapter mGridAdapter
    private String mMovieCategory
    private int mMovieCollectionId
    private String mMovieListType
    private static final int MOVIE_GRID_FRAGMENT_LOADER_ID = 0

    //Projection for movie_basic_info table
    private static final String[] MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_ROW_ID = 0
    final static int COL_MOVIE_TITLE = 1
    final static int COL_MOVIE_POSTER = 2
    final static int COL_MOVIE_PAGE_NUM = 3
    final static int COL_MOVIE_ID = 4
    final static int COL_MOVIE_LIST_TYPE = 4

    //An empty constructor is needed so that lifecycle is properly handled
    public GridMovieFragment(){}

    //Collection id is used so that same module can be used for different categories and collection
    //Excpet categories, the collection id is passed as zero and not used
//    public GridMovieFragment(String movieCategory, int collectionId){
//        mMovieCategory = movieCategory
//        mMovieCollectionId = collectionId
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        // Inflate the menu, this adds items to the action bar if it is present.
        inflater.inflate(R.menu.grid_fragment_menu, menu)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.grid_fragment_menu) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreateView is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        Bundle args = getArguments()
        if (args) {
            mMovieCategory = args.getString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY)
            mMovieCollectionId = args.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_COLL_ID)
            LogDisplay.callLog(LOG_TAG,"Grid Fragment arguments.Movie Category -> $mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG,"Grid Fragment arguments.Collection ID -> $mMovieCollectionId",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }

        //inflate the view before referring any view using id
        View mRootView = inflater.inflate(R.layout.fragment_grid_movie,container,false)
        mGridView = mRootView.findViewById(R.id.movie_grid) as GridView
        mGridAdapter = new MovieGridAdapter(getActivity(),null,0)
        mGridView.setAdapter(mGridAdapter)
        //The more load feature is not needed for user list
        LogDisplay.callLog(LOG_TAG,"Movie Category->$mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        if(mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_POPULAR ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING) {
            mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                void onScrollStateChanged(AbsListView view, int scrollState) {
                    LogDisplay.callLog(LOG_TAG, "ScrollState = $scrollState", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                }
                @Override
                void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    LogDisplay.callLog(LOG_TAG, "First visible item=$firstVisibleItem : " +
                            "VisibleItemCount = $visibleItemCount : ToatlItemCount = $totalItemCount",
                            LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    // If the total item count is zero and the previous isn't, assume the
                    // list is invalidated and should be reset back to initial state
                    if (totalItemCount < mPreviousRecordCount) {
                        LogDisplay.callLog(LOG_TAG, 'List invalidated and reset took place.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                        mCurrentPage = mStartPage
                        mPreviousRecordCount = totalItemCount
                        mReTryCounter = 0
                        if (totalItemCount == 0) {
                            isMoreDataToLoad = true
                        }
                    }
                    // If it's still loading, we check to see if the dataset count has
                    // changed, if so we conclude it has finished loading and update the current page
                    // number and total item count.
                    if (isMoreDataToLoad && (totalItemCount > mPreviousRecordCount)) {
                        LogDisplay.callLog(LOG_TAG, 'Just started or loaded a new page and cursor updated.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                        isMoreDataToLoad = false
                        mPreviousRecordCount = totalItemCount
                        mCurrentPage++
                        mReTryCounter = 0
                    }
                    if (!isMoreDataToLoad && (totalItemCount - visibleItemCount) <= firstVisibleItem + mThreasholdCount) {
                        isMoreDataToLoad = true
                        if (mMovieCategory != 'error') {
                            String[] movieCategory = [mMovieCategory] as String[]
                            LogDisplay.callLog(LOG_TAG, 'Going to load more data...', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                            new LoadMoreMovieData(getActivity(), mCurrentPage).execute(movieCategory)
                        }
                    }
                    //Last API called failed, so give it another try but try max 5 times only
                    //If still does not work, then stop
                    if (isDataLoadFailed && mReTryCounter < 5) {
                        isDataLoadFailed = false
                        final String[] movieCategory = [mMovieCategory] as String[]
                        mReTryCounter++
                        LogDisplay.callLog(LOG_TAG, "Last API call failed, going to re-try...try # $mReTryCounter", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                        new LoadMoreMovieData(getActivity(), mCurrentPage).execute(movieCategory)
                    }
                }
            })
        }  else {
            LogDisplay.callLog(LOG_TAG, "User list, so load more logic is skipped.Movie Category->$mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
        //Add onClickListner
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor cursor = mGridAdapter.getCursor()
                cursor.moveToPosition(position)
                final int movieId = cursor.getInt(COL_MOVIE_ID)
//                final long movieRowId = cursor.getLong(COL_MOVIE_ROW_ID)
//                final ImageView imageViewId = view.findViewById(R.id.grid_image_view) as ImageView
//                mCallback.onItemSelected(movieId, movieRowId, imageViewId)
//                mCallback.onItemSelected(movieId, imageViewId)
                mCallback.onItemSelected(movieId)
                Toast.makeText(getActivity(), "Item clicked- positon: $position, id:$id & movieId:$movieId", Toast.LENGTH_SHORT).show()
                }
            })
        return mRootView
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //If savedInstanceState (i.e. in case of restore), restore the value of mMovieCategory
        if (savedInstanceState) {
            mMovieCategory = savedInstanceState.getString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY, 'error')
            mMovieCollectionId = savedInstanceState.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY, 0)
        }
        getLoaderManager().initLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
        super.onActivityCreated(savedInstanceState)
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        //save the mMovieCategory & mMovieCollectionId, so that in case the fragment restores it can retrieve the value properly
        outState.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,mMovieCategory)
        outState.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_COLL_ID,mMovieCollectionId)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Sort Order: Ascending id, this ensures the list is populated as returned by the API
        //Also it ensures last page number is correct
        final String sortOrder = "$MovieMagicContract.MovieBasicInfo._ID ASC"
        //Build the URI with movie category
        final Uri movieCategoryUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(mMovieCategory)

        switch (id) {
            case MOVIE_GRID_FRAGMENT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),          //Parent Activity Context
                        movieCategoryUri,       //Table to query
                        MOVIE_COLUMNS,          //Projection to return
                        null,                   //Selection Clause, null->will return all data
                        null,                   //Selection Arg, null-> will return all data
                        sortOrder)              //Sort order, will be sorted by date in ascending order
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogDisplay.callLog(LOG_TAG,'onLoadFinished is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        data.moveToLast()
        if(data.getCount() > 0) {
            mStartPage = data.getInt(COL_MOVIE_PAGE_NUM)
            mCurrentPage = mStartPage
            mMovieListType = data.getString(COL_MOVIE_LIST_TYPE)
        }
       LogDisplay.callLog(LOG_TAG,"Start Page # $mStartPage",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        mGridAdapter.swapCursor(data)
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null)
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (Callback) activity
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
         * GridFragmentCallback for when an item has been selected.
         */
//        public void onItemSelected(int movieId, long movie_magic_row_ID, ImageView gridImageView)
//        public void onItemSelected(int movieId, ImageView gridImageView)
        public void onItemSelected(int movieId)
    }
}