package com.moviemagic.dpaul.android.app

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.AutoGridRecyclerView
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadCollectionData
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.squareup.picasso.Callback;
import groovy.transform.CompileStatic

@CompileStatic
class CollectionMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CollectionMovieFragment.class.getSimpleName()

    private Uri mCollectionMovieIdUri
    private int mCollectionId
    private CollapsingToolbarLayout mCollapsingToolbar
    private Toolbar mToolbar
    private AppBarLayout mAppBarLayout
    private ImageView mBackdropImageView
    private TextView mCollectionOverviewTextViewHeader
    private TextView mCollectionOverviewTextView
    private static final int COLLECTION_MOVIE_FRAGMENT_LOADER_ID = 0
    private int mPalletePrimaryColor
    private int mPalletePrimaryDarkColor
    private int mPalleteTitleColor
    private int mPalleteBodyTextColor
    private int mPalleteAccentColor
    private LinearLayout mCollectionLinLayout
    private NestedScrollView mNestedScrollView
    private boolean mCollectionDataLoadSuccessFlag = false
    private String mCollectionBackdropPath

    //Columns to fetch from movie_collection table for similar movies
    private static final String[] COLLECTION_MOVIE_COLUMNS = [MovieMagicContract.MovieCollection._ID,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_OVERVIEW,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_POSTER_PATH,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_COLLECTION_MOVIE_ID = 0
    final static int COL_COLLECTION_MOVIE_COLLECTION_ID = 1
    final static int COL_COLLECTION_MOVIE_COLLECTION_NAME = 2
    final static int COL_COLLECTION_MOVIE_COLLECTION_OVERVIEW = 3
    final static int COL_COLLECTION_MOVIE_COLLECTION_POSTER_PATH = 4
    final static int COL_COLLECTION_MOVIE_COLLECTION_BACKDROP_PATH = 5
    final static int COL_COLLECTION_MOVIE_PRESENT_FLAG = 6

    //An empty constructor is needed so that lifecycle is properly handled
    public CollectionMovieFragment() {
        LogDisplay.callLog(LOG_TAG,'CollectionMovieFragment empty constructor is called',LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
    }

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
        inflater.inflate(R.menu.collection_fragment_menu, menu)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.collection_fragment_menu) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreateView is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        Bundle args = getArguments()
        if (args) {
            mCollectionMovieIdUri = args.getParcelable(GlobalStaticVariables.MOVIE_COLLECTION_URI) as Uri
            LogDisplay.callLog(LOG_TAG, "Collection Fragment arguments.Uri -> $mCollectionMovieIdUri", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            mCollectionId = MovieMagicContract.MovieCollection.getCollectionIdFromMovieCollectionUri(mCollectionMovieIdUri)
        }
        //inflate the view before referring any view using id
        View mRootView = inflater.inflate(R.layout.fragment_collection_movie, container, false)
        mBackdropImageView = mRootView.findViewById(R.id.collection_backdrop_image) as ImageView
        mCollectionOverviewTextViewHeader = mRootView.findViewById(R.id.collection_overview_header) as TextView
        mCollectionOverviewTextView = mRootView.findViewById(R.id.collection_overview) as TextView
        mCollectionLinLayout = mRootView.findViewById(R.id.movie_detail_collection_layout) as LinearLayout
        mNestedScrollView = mRootView.findViewById(R.id.collection_detail_scroll) as NestedScrollView

        return mRootView
    }

    @Override
    void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState)
        AppCompatActivity appCompatActivity = getActivity() as AppCompatActivity
        mToolbar = getView().findViewById(R.id.collection_toolbar) as Toolbar
        if (mToolbar) {
            appCompatActivity.setSupportActionBar(mToolbar)
            //Enable back to home button
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }
        mCollapsingToolbar = getView().findViewById(R.id.collection_collapsing_toolbar) as CollapsingToolbarLayout
        if (mCollapsingToolbar) {
            //Just clear off to be on the safe side
            mCollapsingToolbar.setTitle(" ")
        }

        //If it's a fresh start then call init loader
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(COLLECTION_MOVIE_FRAGMENT_LOADER_ID, null, this)
        } else {        //If it's restore then restart the loader
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time, so restart loaders', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().restartLoader(COLLECTION_MOVIE_FRAGMENT_LOADER_ID, null, this)
        }
    }

    @Override
    Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader is called.loader id->$id", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case COLLECTION_MOVIE_FRAGMENT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),              //Parent Activity Context
                        mCollectionMovieIdUri,      //Table to query
                        COLLECTION_MOVIE_COLUMNS,   //Projection to return
                        null,                       //Selection Clause, null->will return all data
                        null,                       //Selection Arg, null-> will return all data
                        null)                       //Sort order, not required
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        if(data.moveToFirst()) {
            LogDisplay.callLog(LOG_TAG, "onLoadFinished.Data present for collection id $mCollectionId", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            mCollectionDataLoadSuccessFlag = true
            mCollectionBackdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                    "${data.getString(COL_COLLECTION_MOVIE_COLLECTION_BACKDROP_PATH)}"
            mCollapsingToolbar.setTitle(data.getString(COL_COLLECTION_MOVIE_COLLECTION_NAME))
            mCollectionOverviewTextView.setText(data.getString(COL_COLLECTION_MOVIE_COLLECTION_OVERVIEW))
            if(data.getInt(COL_COLLECTION_MOVIE_PRESENT_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                //TODO: Call the GridFragment
                //Fragment transaction cannot be done inside onnLoadFinished, so work around is to use a handler as per
                //stackoverflow http://stackoverflow.com/questions/22788684/can-not-perform-this-action-inside-of-onloadfinished
                final int WHAT = 1
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what == WHAT) loadFragment()
                    }
                }
                handler.sendEmptyMessage(WHAT)
                //TODO Transition testing
////                TransitionManager.beginDelayedTransition(mCollectionLinLayout,new Slide())
//                final Animation animation1 = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_bottom_in_animation)
//                animation1.setDuration(700)
//                mCollectionLinLayout.startAnimation(animation1)
//                final AppCompatActivity appCompatActivity = getActivity() as AppCompatActivity
////                appCompatActivity.supportPostponeEnterTransition()
//                final Animation animation2 = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_top_in_animation)
//                animation2.setDuration(700)
//                mBackdropImageView.startAnimation(animation2)
//                mCollapsingToolbar.startAnimation(animation2)

            } else {
                //We shouldn't reach here in ideal scenarios
                LogDisplay.callLog(LOG_TAG, 'onLoadFinished.Collection movie flag is false', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            }
        } else {
            //Load the collection details and associated movies
            LogDisplay.callLog(LOG_TAG, "onLoadFinished.Data not present for collection id $mCollectionId, go and fetch it", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            new LoadCollectionData(getActivity()).execute([mCollectionId] as Integer[])
        }
    }

    @Override
    void onLoaderReset(Loader<Cursor> loader) {
        //Do nothing
    }

    void loadFragment() {
        LogDisplay.callLog(LOG_TAG, 'loadFragment is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        //Set this flag as false so that derived (from collection backdrop) primaryDark color is used in the grid
        MovieGridRecyclerAdapter.collectionGridFlag = true
        final Bundle bundle = new Bundle()
        final Uri uri = MovieMagicContract.MovieBasicInfo
                .buildMovieUriWithMovieCategoryAndCollectionId(GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION,mCollectionId)
        bundle.putParcelable(GlobalStaticVariables.MOVIE_CATEGORY_AND_COLL_ID_URI,uri)
        final GridMovieFragment gridMovieFragment = new GridMovieFragment()
        gridMovieFragment.setArguments(bundle)
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
        //Set the custom animation
//        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_in_animation,0)
        fragmentTransaction.replace(R.id.collection_movie_grid, gridMovieFragment)
        fragmentTransaction.commit()
    }

    void loadCollBackdropAndchangeCollectionMovieGridColor() {
        if(mCollectionDataLoadSuccessFlag) {
            final Callback picassoCollectionImageCallback = new Callback() {
                @Override
                void onSuccess() {
                    LogDisplay.callLog(LOG_TAG, 'Picasso onSuccess is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                    //TODO: Future change - provide a setting option to user to chose if they want this or will use default theme
                    final Bitmap bitmapPoster = ((BitmapDrawable) mBackdropImageView.getDrawable()).getBitmap()
                    Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette p) {
                            LogDisplay.callLog(LOG_TAG, 'onGenerated is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
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
                                LogDisplay.callLog(LOG_TAG, 'onGenerated:not able to pick color, so fallback', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
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
                            //Change the color only if we are able to get hold of recyclerview, otherwise use default color
                            final View view = getView()
                            final AutoGridRecyclerView autoGridRecyclerView = view.findViewById(R.id.auto_grid_recycler_view) as AutoGridRecyclerView
                            if(autoGridRecyclerView) {
                                LogDisplay.callLog(LOG_TAG, 'onGenerated:recycler view is NOT null', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                autoGridRecyclerView.setBackgroundColor(mPalletePrimaryColor)
                                mCollectionLinLayout.setBackgroundColor(mPalletePrimaryColor)
                                mNestedScrollView.setBackgroundColor(mPalletePrimaryColor)
                                mCollectionOverviewTextView.setTextColor(mPalleteBodyTextColor)
                                mCollectionOverviewTextViewHeader.setTextColor(mPalleteTitleColor)
                                mCollapsingToolbar.setStatusBarScrimColor(mPalletePrimaryDarkColor)
                                mCollapsingToolbar.setContentScrimColor(mPalletePrimaryColor)
                                mCollapsingToolbar.setBackgroundColor(mPalletePrimaryColor)
                                mCollapsingToolbar.setCollapsedTitleTextColor(mPalleteBodyTextColor)
//                                mCollapsingToolbar.setExpandedTitleColor(mPalleteTitleColor)

//                            MovieGridRecyclerAdapter.mPrimaryColor = mPalletePrimaryColor
                                MovieGridRecyclerAdapter.mPrimaryDarkColor = mPalletePrimaryDarkColor
//                            MovieGridRecyclerAdapter.mTitleTextColor = mPalleteTitleColor
                                MovieGridRecyclerAdapter.mBodyTextColor = mPalleteBodyTextColor
                                final MovieGridRecyclerAdapter movieGridRecyclerAdapter = autoGridRecyclerView.getAdapter() as MovieGridRecyclerAdapter
                                movieGridRecyclerAdapter.changeColor()
                            } else {
                                LogDisplay.callLog(LOG_TAG, 'onGenerated:recycler view is null, so use default color', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                            }
//                            collectionGridColorCallback.notifyColorChange(mPalletePrimaryColor)
//                            MovieGridRecyclerAdapter.changeColor()
//                            final View view = getView()
//                            (view.findViewById(R.id.collection_movie_grid)).setBackgroundColor(mPalletePrimaryColor)
                        }
                    })
                }

                @Override
                void onError() {

                }
            }
            PicassoLoadImage.loadCollectionBackdropImage(getActivity(), mCollectionBackdropPath, mBackdropImageView, picassoCollectionImageCallback)
        }
        mCollectionDataLoadSuccessFlag = false
    }

    @Override
    void onPause() {
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onPause()
    }
}