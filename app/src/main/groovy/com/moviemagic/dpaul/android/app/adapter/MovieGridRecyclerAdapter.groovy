package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.GridMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridRecyclerAdapter.MovieGridRecyclerAdapterViewHolder>{
    private static final String LOG_TAG = MovieGridRecyclerAdapter.class.getSimpleName()
    private final Context mContext
    private Cursor mCursor
    private final MovieGridRecyclerAdapterOnClickHandler mMovieGridRecyclerAdapterOnClickHandler
    public static int mPrimaryColor, mPrimaryDarkColor, mTitleTextColor, mBodyTextColor
    //This flag is set as true by CollectionMovieFragment in order to apply color
    //And the same is set as false by MovieMagicMainActivity in order to use defualt color
    public static boolean collectionGridFlag = false

    //Empty constructor
    public MovieGridRecyclerAdapter() {
        LogDisplay.callLog(LOG_TAG, 'MovieGridRecyclerAdapter empty constructor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
    }

    public MovieGridRecyclerAdapter(Context ctx, MovieGridRecyclerAdapterOnClickHandler clickHandler) {
        LogDisplay.callLog(LOG_TAG, 'MovieGridRecyclerAdapter non-empty constructor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        mContext = ctx
        mMovieGridRecyclerAdapterOnClickHandler = clickHandler
    }

    public class MovieGridRecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView movieImageView
        private final TextView movieNameView

        public MovieGridRecyclerAdapterViewHolder(View view) {
            super(view)
            movieImageView = view.findViewById(R.id.grid_image_view) as ImageView
            movieNameView = view.findViewById(R.id.grid_text_view) as TextView
            view.setOnClickListener(this)
        }
        @Override
        void onClick(View v) {
            final int adapterPosition = getAdapterPosition()
            mCursor.moveToPosition(adapterPosition)
            final int movieId = mCursor.getInt(GridMovieFragment.COL_MOVIE_ID)
            mMovieGridRecyclerAdapterOnClickHandler.onClick(movieId, this)
        }
    }

    @Override
    MovieGridRecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG, 'onCreateViewHolder is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_grid_movie_item, parent, false)
        view.setFocusable(true)
        return new MovieGridRecyclerAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(MovieGridRecyclerAdapterViewHolder holder, int position) {
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(GridMovieFragment.COL_MOVIE_POSTER)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.movieImageView)
        holder.movieNameView.setText(mCursor.getString(GridMovieFragment.COL_MOVIE_TITLE))
        if(collectionGridFlag) {
            holder.movieNameView.setBackgroundColor(mPrimaryDarkColor)
            holder.movieNameView.setTextColor(mBodyTextColor)
        } else {
            //This is to ensure the correct color is used when grid is used for main browsing (i.e. popular, upcoming, etc)
            holder.movieNameView.setBackgroundColor(mContext.getColor(R.color.primary_dark))
            holder.movieNameView.setTextColor(mContext.getColor(R.color.white_color))
        }
    }

    @Override
    int getItemCount() {
        if (null == mCursor) {
            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
            return 0
        }
//        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        LogDisplay.callLog(LOG_TAG, 'swapCursor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        mCursor = newCursor
        notifyDataSetChanged()
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor() {
        LogDisplay.callLog(LOG_TAG, 'changeColor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        notifyDataSetChanged()
    }
    /**
     * This is the interface which will be implemented by the host GridMovieFragment
     */
    public interface MovieGridRecyclerAdapterOnClickHandler {
        public void onClick(int movieId, MovieGridRecyclerAdapterViewHolder viewHolder)
    }
}