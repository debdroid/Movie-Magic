package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieActivity
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.PersonMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class PersonCastAdapter extends RecyclerView.Adapter<PersonCastAdapter.PersonCastAdapterViewHolder> {
    private static final String LOG_TAG = PersonCastAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mPersonCastGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor

    //Empty constructor
    public PersonCastAdapter(){
        LogDisplay.callLog(LOG_TAG,'PersonCastAdapter empty constructor is called',LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
    }

    public PersonCastAdapter(Context ctx, TextView emptyView){
        LogDisplay.callLog(LOG_TAG,'PersonCastAdapter non-empty constructor is called',LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
        mContext = ctx
        mPersonCastGridEmptyTextView = emptyView
    }

    public class PersonCastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieImageView
        public final TextView movieTextView, charTextView

        public PersonCastAdapterViewHolder(View view) {
            super(view)
            movieImageView = view.findViewById(R.id.person_cast_grid_movie_image) as ImageView
            movieTextView = view.findViewById(R.id.person_cast_grid_movie_name) as TextView
            charTextView = view.findViewById(R.id.person_cast_grid_char_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
            final int movieId = mCursor.getInt(PersonMovieFragment.COL_PERSON_CAST_MOVIE_ID)
            LogDisplay.callLog(LOG_TAG,"Movie id is $movieId",LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
            final Bundle bundle = new Bundle()
            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
            bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
            final Intent intent = new Intent(mContext, DetailMovieActivity.class)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
//            //Start the animation
//            mContext.overridePendingTransition(R.anim.slide_bottom_in_animation,0)

//            final DetailMovieFragment movieDetailFragment = new DetailMovieFragment()
//            movieDetailFragment.setArguments(bundle)
//            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
//            //Used the method enter,exit,popEnter,popExit custom animation. Our cases are enter & popExit
//                    .setCustomAnimations(R.anim.slide_bottom_in_animation,0,0,R.anim.slide_bottom_out_animation)
//                    .replace(R.id.detail_movie_fragment_container,movieDetailFragment)
//            // Add this transaction to the back stack
//                    .addToBackStack(null) //Parameter is optional, so used null
//                    .commit()
        }
    }

    @Override
    PersonCastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_cast_grid,parent,false)
        view.setFocusable(true)
        return new PersonCastAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(PersonCastAdapterViewHolder holder, int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(PersonMovieFragment.COL_PERSON_CAST_MOVIE_POSTER_PATH)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.movieImageView)
        if(mCursor.getString(PersonMovieFragment.COL_PERSON_CAST_CHARACTER_NAME) != '') {
            holder.charTextView.setText("as ${mCursor.getString(PersonMovieFragment.COL_PERSON_CAST_CHARACTER_NAME)}")
        }
        holder.movieTextView.setText(mCursor.getString(PersonMovieFragment.COL_PERSON_CAST_MOVIE_TITLE))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.movieTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.charTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.charTextView.setTextColor(mBodyTextColor)
            holder.movieTextView.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
        if (null == mCursor) {
//            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
            return 0
        }
//        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.PERSON_CAST_ADAPTER_LOG_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mPersonCastGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mPersonCastGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(int primaryDarkColor, int bodyTextColor) {
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            mPersonCastGridEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }
}