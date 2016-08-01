package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
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
import com.moviemagic.dpaul.android.app.adapter.GridAdapter
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.utility.LoadMoreData
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class GridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = GridFragment.class.getSimpleName()
    private static final String STATE_MOVIE_CATEGORY = 'movie_Category'
    //This is to indicate the start page for the more load. Driven by the value used in syncadapter (i.e. total
    //number of pages already downloaded)
    public int mStartPage = 0
    //This is to hold the current page of the data.
    public int mCurrentPage = mStartPage
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

    GridView mGridView
    GridAdapter mGridAdapter
    String mMovieCategory
    private static final int MOVIE_GRID_FRAGMENT_LOADER_ID = 0
    private static final String[] MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_REC_ID = 0
    final static int COL_MOVIE_TITLE = 1
    final static int COL_MOVIE_POSTER = 2
    final static int COL_MOVIE_PAGE_NUM = 3
    final static int COL_MOVIE_ID = 4

    //An empty constructor is needed so that lifecycle is properly handled
    public GridFragment(){}

    public GridFragment(String movieCategory){
        mMovieCategory = movieCategory
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //If savedInstanceState (i.e. in case of restore), restore the value of mMovieCategory
        if (savedInstanceState) {
            mMovieCategory = savedInstanceState.getString(STATE_MOVIE_CATEGORY, 'error')
        }
        getLoaderManager().initLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the view before referring any view using id
        View mRootView = inflater.inflate(R.layout.fragment_grid,container,false)
        mGridView = mRootView.findViewById(R.id.gridview_fragment) as GridView
        mGridAdapter = new GridAdapter(getActivity(),null,0)
        mGridView.setAdapter(mGridAdapter)
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            void onScrollStateChanged(AbsListView view, int scrollState) {
                LogDisplay.callLog(LOG_TAG,"ScrollState = $scrollState",
                        LogDisplay.GRID_FRAGMENT_LOG_FLAG)
            }

            @Override
            void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                LogDisplay.callLog(LOG_TAG,"First visible item=$firstVisibleItem : " +
                        "VisibleItemCount = $visibleItemCount : ToatlItemCount = $totalItemCount",
                LogDisplay.GRID_FRAGMENT_LOG_FLAG)
                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < mPreviousRecordCount) {
                    LogDisplay.callLog(LOG_TAG,'List invalidated and reset took place.',LogDisplay.GRID_FRAGMENT_LOG_FLAG)
                    mCurrentPage = mStartPage
                    mPreviousRecordCount = totalItemCount
                    mReTryCounter = 0
                    if(totalItemCount == 0) {
                        isMoreDataToLoad = true
                    }
                }
                // If it's still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if(isMoreDataToLoad && (totalItemCount > mPreviousRecordCount)) {
                    LogDisplay.callLog(LOG_TAG,'Just started or loaded a new page and cursor updated.',LogDisplay.GRID_FRAGMENT_LOG_FLAG)
                    isMoreDataToLoad = false
                    mPreviousRecordCount = totalItemCount
                    mCurrentPage++
                    mReTryCounter = 0
                }
                if(!isMoreDataToLoad && (totalItemCount - visibleItemCount) <= firstVisibleItem + mThreasholdCount) {
                    isMoreDataToLoad = true
                    if(mMovieCategory != 'error') {
                        String[] movieCategory = [mMovieCategory] as String[]
                        LogDisplay.callLog(LOG_TAG,'Going to load more data...',LogDisplay.GRID_FRAGMENT_LOG_FLAG)
                        new LoadMoreData(getActivity(),mCurrentPage).execute(movieCategory)
                    }
                }
                //Last API called failed, so give it another try but try max 5 times only
                //If still does not work, then stop
                if(isDataLoadFailed && mReTryCounter < 5) {
                    isDataLoadFailed = false
                    String[] movieCategory = [mMovieCategory] as String[]
                    mReTryCounter++
                    LogDisplay.callLog(LOG_TAG,"Last API call failed, going to re-try...try # $mReTryCounter",LogDisplay.GRID_FRAGMENT_LOG_FLAG)
                    new LoadMoreData(getActivity(),mCurrentPage).execute(movieCategory)
                }
            }
        })
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mGridAdapter.getCursor()
                cursor.moveToPosition(position)
                int movieId = cursor.getInt(COL_MOVIE_ID)
                Toast.makeText(getActivity(), "Item clicked- positon: $position, id:$id & movieId:$movieId", Toast.LENGTH_SHORT).show()
                Uri movieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(movieId)
                mCallback.onItemSelected(movieIdUri)
            }
        })
        return mRootView
    }

    @Override
    void onSaveInstanceState(Bundle outState) {
        //save the mMovieCategory, so that in case the fragment restores it can retrieve the value properly
        outState.putString(STATE_MOVIE_CATEGORY,mMovieCategory)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Sort Order: Ascending id, this ensures the list is populated as returned by the API
        //Also it ensures last page number is correct
        String sortOrder = "$MovieMagicContract.MovieBasicInfo._ID ASC"
        //Build the URI with movie category
        Uri movieCategoryUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(mMovieCategory)

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
        LogDisplay.callLog(LOG_TAG,'onLoadFinished is called',LogDisplay.GRID_FRAGMENT_LOG_FLAG)
        data.moveToLast()
        if(data.getCount() > 0) {
            mStartPage = data.getInt(COL_MOVIE_PAGE_NUM)
            mCurrentPage = mStartPage
        }
       LogDisplay.callLog(LOG_TAG,"Start Page # $mStartPage",LogDisplay.GRID_FRAGMENT_LOG_FLAG)
        mGridAdapter.swapCursor(data)
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null)
    }

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
        public void onItemSelected(Uri movieUri)
    }
}