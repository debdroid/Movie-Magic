package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.PersonMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class PersonImageAdapter extends RecyclerView.Adapter<PersonImageAdapter.PersonImageAdapterViewHolder> {
    private static final String LOG_TAG = PersonImageAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mPersonImageGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor
    private final PersonImageAdapterOnClickHandler mMoviePersonImageAdapterOnClickHandler

    //Empty constructor
    public PersonImageAdapter(){
        LogDisplay.callLog(LOG_TAG,'PersonImageAdapter empty constructor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
    }

    public PersonImageAdapter(Context ctx, TextView emptyView, PersonImageAdapterOnClickHandler clickHandler){
        LogDisplay.callLog(LOG_TAG,'PersonImageAdapter non-empty constructor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        mContext = ctx
        mPersonImageGridEmptyTextView = emptyView
        mMoviePersonImageAdapterOnClickHandler = clickHandler
    }

    public class PersonImageAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView personImagesImageView

        public PersonImageAdapterViewHolder(View view) {
            super(view)
            personImagesImageView = view.findViewById(R.id.person_image_grid_movie_image) as ImageView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            final String[] imagePath = new String[mCursor.getCount()]
            LogDisplay.callLog(LOG_TAG,"onClick:Cursor count ${mCursor.getCount()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            mCursor.moveToFirst()
            for(i in 0..mCursor.getCount()-1) {
                imagePath[i] = mCursor.getString(PersonMovieFragment.COL_PERSON_IMAGE_FILE_PATH)
                mCursor.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG,"onClick:imagePath array count ${imagePath.size()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            final int adapterPosition = getAdapterPosition()
            mMoviePersonImageAdapterOnClickHandler.onClick(adapterPosition, imagePath, this)
//            final int adapterPosition = getAdapterPosition()
//            mCursor.moveToPosition(adapterPosition)
//            final int movieId = mCursor.getInt(PersonMovieFragment.COL_PERSON_CREW_MOVIE_ID)
//            mMoviePersonImageAdapterOnClickHandler.onClick(movieId, this)
//            mCursor.moveToPosition(getAdapterPosition())
//            final int movieId = mCursor.getInt(PersonMovieFragment.COL_PERSON_CREW_MOVIE_ID)
//            LogDisplay.callLog(LOG_TAG,"Movie id is $movieId",LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
//            final Bundle bundle = new Bundle()
//            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
//            bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
//            final Intent intent = new Intent(mContext, DetailMovieActivity.class)
//            intent.putExtras(bundle)
//            mContext.startActivity(intent)
        }
    }

    @Override
    PersonImageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_image_grid,parent,false)
        view.setFocusable(true)
        return new PersonImageAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(PersonImageAdapterViewHolder holder, int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        final String imagePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(PersonMovieFragment.COL_PERSON_IMAGE_FILE_PATH)}"
        PicassoLoadImage.loadMoviePersonImage(mContext,imagePath,holder.personImagesImageView)
    }

    @Override
    int getItemCount() {
//        LogDisplay.callLog(LOG_TAG,'Cursor item count is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        if (null == mCursor) {
//            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            return 0
        }
//        LogDisplay.callLog(LOG_TAG, "Cursor item count = ${mCursor.getCount()}", LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        return mCursor.getCount()
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mPersonImageGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mPersonImageGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(int primaryDarkColor, int bodyTextColor) {
        LogDisplay.callLog(LOG_TAG,'changeColor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            mPersonImageGridEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * This is the interface which will be implemented by the host PersonMovieFragment
     */
    public interface PersonImageAdapterOnClickHandler {
        public void onClick(int adapterPosition, String[] imageFilePath, PersonImageAdapterViewHolder viewHolder)
    }
}