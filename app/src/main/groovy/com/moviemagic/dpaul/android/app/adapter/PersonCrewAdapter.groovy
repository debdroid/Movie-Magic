package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieActivity
import com.moviemagic.dpaul.android.app.PersonMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class PersonCrewAdapter extends RecyclerView.Adapter<PersonCrewAdapter.PersonCrewAdapterViewHolder> {
    private static final String LOG_TAG = PersonCrewAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mPersonCrewGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor

    //Empty constructor
    public PersonCrewAdapter(){
        LogDisplay.callLog(LOG_TAG,'PersonCrewAdapter empty constructor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
    }

    public PersonCrewAdapter(Context ctx, TextView emptyView){
        LogDisplay.callLog(LOG_TAG,'PersonCrewAdapter non-empty constructor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        mContext = ctx
        mPersonCrewGridEmptyTextView = emptyView
    }

    public class PersonCrewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieImageView
        public final TextView movieTextView, jobTextView

        public PersonCrewAdapterViewHolder(View view) {
            super(view)
            movieImageView = view.findViewById(R.id.person_crew_grid_movie_image) as ImageView
            movieTextView = view.findViewById(R.id.person_crew_grid_movie_name) as TextView
            jobTextView = view.findViewById(R.id.person_crew_grid_job_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
            final int movieId = mCursor.getInt(PersonMovieFragment.COL_PERSON_CREW_MOVIE_ID)
            LogDisplay.callLog(LOG_TAG,"Movie id is $movieId",LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
            final Bundle bundle = new Bundle()
            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
            bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
            final Intent intent = new Intent(mContext, DetailMovieActivity.class)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    @Override
    PersonCrewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_crew_grid,parent,false)
        view.setFocusable(true)
        return new PersonCrewAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(PersonCrewAdapterViewHolder holder, int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_MOVIE_POSTER_PATH)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.movieImageView)
        holder.jobTextView.setText(mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_JOB_NAME))
        holder.movieTextView.setText(mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_MOVIE_TITLE))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.movieTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.jobTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.jobTextView.setTextColor(mBodyTextColor)
            holder.movieTextView.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        if (null == mCursor) {
//            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
            return 0
        }
//        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mPersonCrewGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mPersonCrewGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(int primaryDarkColor, int bodyTextColor) {
        LogDisplay.callLog(LOG_TAG,'changeColor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            mPersonCrewGridEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }
}