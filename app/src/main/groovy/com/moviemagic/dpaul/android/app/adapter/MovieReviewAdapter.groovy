package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {
    @SuppressWarnings("GroovyConstantNamingConvention")
    private static final String LOG_TAG = MovieReviewAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext = null
    private final TextView mRecyclerviewEmptyTextView = null
    private int mPrimaryColor, mTitleTextColor, mBodyTextColor

    //Empty constructor
    public MovieReviewAdapter() {
        LogDisplay.callLog(LOG_TAG, 'MovieReviewAdapter empty constructor is called', LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
    }

    public MovieReviewAdapter(final Context ctx, final TextView recyclerviewEmptyTextView) {
        LogDisplay.callLog(LOG_TAG, 'MovieReviewAdapter non-empty constructor is called', LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
        mContext = ctx
        mRecyclerviewEmptyTextView = recyclerviewEmptyTextView
    }

    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        private final TextView movieReviewAuthor
        private final TextView movieReviewContent
        private final ImageButton mShowImageButton
        private final ImageButton mHideImageButton


        public MovieReviewAdapterViewHolder(final View view) {
            super(view)
            movieReviewAuthor = view.findViewById(R.id.single_review_item_author) as TextView
            movieReviewContent = view.findViewById(R.id.single_review_item_review_content) as TextView
            mShowImageButton = view.findViewById(R.id.single_review_item_show_button) as ImageButton
            mHideImageButton = view.findViewById(R.id.single_review_item_hide_button) as ImageButton
            //noinspection GroovyVariableCanBeFinal
            mShowImageButton.setOnClickListener( new View.OnClickListener() {
                @Override
                void onClick(final View v) {
                    mShowImageButton.setVisibility(Button.INVISIBLE)
                    mHideImageButton.setVisibility(Button.VISIBLE)
                    movieReviewContent.setMaxLines(Integer.MAX_VALUE)
                }
            })
            //noinspection GroovyVariableCanBeFinal
            mHideImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                void onClick(final View v) {
                    mShowImageButton.setVisibility(Button.VISIBLE)
                    mHideImageButton.setVisibility(Button.INVISIBLE)
                    movieReviewContent.setMaxLines(mContext.getResources().getString(R.string.single_review_item_collapse_line_item_count) as Integer)
                }
            })
        }
    }

    @Override
    MovieReviewAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG, 'onCreateViewHolder is called', LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
        //noinspection GroovyVariableCanBeFinal
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie_review_item, parent, false)
        view.setFocusable(true)
        return new MovieReviewAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final MovieReviewAdapterViewHolder holder, final int position) {
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
        //Move the cursor to correct position
        mCursor.moveToPosition(position)
        holder.movieReviewAuthor.setText("By ${mCursor.getString(DetailMovieFragment.COL_MOVIE_REVIEW_AUTHOR)}")
        //Remove all empty lines, blanks and tabs
        final String contentText = mCursor.getString(DetailMovieFragment.COL_MOVIE_REVIEW_CONTENT).replaceAll("(?m)^[ \t]*\r?\n", "")
        holder.movieReviewContent.setText(contentText)
        //Apply color only it has got a value
        if(mPrimaryColor && mBodyTextColor && mTitleTextColor) {
            holder.movieReviewAuthor.setBackgroundColor(mPrimaryColor)
            holder.movieReviewAuthor.setTextColor(mTitleTextColor)
            holder.movieReviewContent.setBackgroundColor(mPrimaryColor)
            holder.movieReviewContent.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
        if (null == mCursor) {
            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
            return 0
        }
        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(final Cursor newCursor) {
        LogDisplay.callLog(LOG_TAG, 'swapCursor is called', LogDisplay.MOVIE_REVIEW_ADAPTER_LOG_FLAG)
        mCursor = newCursor
        if (getItemCount() == 0) {
            mRecyclerviewEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mRecyclerviewEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(final int primaryColor, final int titleTextColor, final int bodyTextColor) {
        mPrimaryColor = primaryColor
        mTitleTextColor = titleTextColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            mRecyclerviewEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }
}