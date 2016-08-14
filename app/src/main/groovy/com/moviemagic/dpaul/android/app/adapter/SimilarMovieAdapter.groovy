package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieActivity
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieAdapterViewHolder> {
    private static final String LOG_TAG = SimilarMovieAdapter.class.getSimpleName()

    private Cursor mCursor
    private Context mContext
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
            int movieId = mCursor.getInt(DetailMovieFragment.COL_MOVIE_BASIC_MOVIE_ID)
            LogDisplay.callLog(LOG_TAG,"Movie id is $movieId",LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
            //Create an intent for DetailMovieActivity
            Uri movieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(movieId)
            Intent mIntent = new Intent(mContext, DetailMovieActivity.class)
                    .setData(movieIdUri)
//                    .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            mContext.startActivity(mIntent)

//            //Create a Bundle to pass the data to Fragment
//            Bundle args = new Bundle()
//            //Get the data from the intent and put that to Bundle
//            args.putParcelable(DetailMovieFragment.MOVIE_BASIC_INFO_MOVIE_ID_URI, movieIdUri)
//            //Create a movie detail fragment
//            DetailMovieFragment movieDetailFragment = new DetailMovieFragment()
//            movieDetailFragment.setArguments(args)
//            ((DetailMovieActivity)mContext).getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,movieDetailFragment).commit()
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
        String posterPath = "http://image.tmdb.org/t/p/w185${mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_POSTER_PATH)}"
        Picasso.with(mContext)
                .load(posterPath)
                .fit()
                .placeholder(R.drawable.grid_image_placeholder)
                .error(R.drawable.grid_image_error)
                .into(holder.similarMovieImageView)
        holder.similarMovieTextView.setText(mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_TITLE))
        holder.similarMovieTextView.setBackgroundColor(mPrimaryDarkColor)
        holder.similarMovieTextView.setTextColor(mBodyTextColor)
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
        if (null == mCursor) {
            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
            return 0
        }
        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.SIMILAR_MOVIE_ADAPTER_FLAG)
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