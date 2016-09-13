package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class MovieCrewAdapter extends RecyclerView.Adapter<MovieCrewAdapter.MovieCrewAdapterViewHolder> {
    private static final String LOG_TAG = MovieCrewAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mCrewGridEmptyTextView
    public static int mPrimaryDarkColor, mBodyTextColor


    //Empty constructor
    public MovieCrewAdapter(){
        LogDisplay.callLog(LOG_TAG,'MovieCrewAdapter empty constructor is called',LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
    }

    public MovieCrewAdapter(Context ctx, TextView emptyView){
        LogDisplay.callLog(LOG_TAG,'MovieCrewAdapter non-empty constructor is called',LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
        mContext = ctx
        mCrewGridEmptyTextView = emptyView
    }

    public class MovieCrewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieCrewImageView
        public final TextView movieCrewJobName
        public final TextView movieCrewName

        public MovieCrewAdapterViewHolder(View view) {
            super(view)
            movieCrewImageView = view.findViewById(R.id.single_movie_crew_grid_image) as ImageView
            movieCrewJobName = view.findViewById(R.id.single_movie_crew_grid_job_name) as TextView
            movieCrewName = view.findViewById(R.id.single_movie_crew_grid_crew_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
        }
    }
    @Override
    MovieCrewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie_crew_grid,parent,false)
        view.setFocusable(true)
        return new MovieCrewAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(MovieCrewAdapterViewHolder holder, int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
        final String profilePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_PROFILE_PATH)}"
        PicassoLoadImage.loadMoviePersonImage(mContext,profilePath,holder.movieCrewImageView)
        holder.movieCrewJobName.setText(mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_CREW_JOB))
        holder.movieCrewName.setText(mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_PERSON_NAME))
        holder.movieCrewJobName.setBackgroundColor(mPrimaryDarkColor)
        holder.movieCrewJobName.setTextColor(mBodyTextColor)
        holder.movieCrewName.setBackgroundColor(mPrimaryDarkColor)
        holder.movieCrewName.setTextColor(mBodyTextColor)
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.MOVIE_CREW_ADAPTER_FLAG)
        if ( null == mCursor ) return 0
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mCrewGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mCrewGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor() {
        if (getItemCount() == 0) {
            mCrewGridEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }
}