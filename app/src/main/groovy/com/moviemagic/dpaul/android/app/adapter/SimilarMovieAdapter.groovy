package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieActivity
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.utility.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import com.moviemagic.dpaul.android.app.utility.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieAdapterViewHolder> {
    private static final String LOG_TAG = SimilarMovieAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    public static int mPrimaryDarkColor, mBodyTextColor

    //Empty constructor
    public SimilarMovieAdapter(){
        LogDisplay.callLog(LOG_TAG,'SimilarMovieAdapter empty constructor is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
    }

    public SimilarMovieAdapter(Context ctx){
        LogDisplay.callLog(LOG_TAG,'SimilarMovieAdapter non-empty constructor is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        mContext = ctx
    }

    public class SimilarMovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView similarMovieImageView
        public final TextView similarMovieTextView

        public SimilarMovieAdapterViewHolder(View view) {
            super(view)
            similarMovieImageView = view.findViewById(R.id.single_similar_movie_grid_image) as ImageView
            similarMovieTextView = view.findViewById(R.id.single_similar_movie_movie_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
            final int movieId = mCursor.getInt(DetailMovieFragment.COL_MOVIE_BASIC_MOVIE_ID)
            final long movieRowId = mCursor.getLong(DetailMovieFragment.COL_MOVIE_BASIC_ID)
            LogDisplay.callLog(LOG_TAG,"Movie row id is $movieRowId & Movie id is $movieId",LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
            //Create an intent for DetailMovieActivity
            final Intent intent = new Intent(mContext, DetailMovieActivity.class)
            final Bundle bundle = new Bundle()
            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
            bundle.putLong(GlobalStaticVariables.MOVIE_BASIC_INFO_ROW_ID,movieRowId)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            //Get a reference of the activity and start the animation
            AppCompatActivity appCompatActivity = (AppCompatActivity)mContext
            appCompatActivity. overridePendingTransition(R.anim.slide_bottom_up_animation,0)
//            final Uri movieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(movieId)
//            final Intent mIntent = new Intent(mContext, DetailMovieActivity.class)
//                    .setData(movieIdUri)
//            mContext.startActivity(mIntent)
        }
    }

    @Override
    SimilarMovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_similar_movie_grid,parent,false)
        view.setFocusable(true)
        return new SimilarMovieAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(SimilarMovieAdapterViewHolder holder, int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_POSTER_PATH)}"
        PicassoLoadImage.loadMoviePosterUsingPicasso(mContext,posterPath,holder.similarMovieImageView)
        holder.similarMovieTextView.setText(mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_TITLE))
        holder.similarMovieTextView.setBackgroundColor(mPrimaryDarkColor)
        holder.similarMovieTextView.setTextColor(mBodyTextColor)
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        if (null == mCursor) {
//            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
            return 0
        }
//        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor
        notifyDataSetChanged()
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor() {
        notifyDataSetChanged()
    }
}