package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.GridFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class MovieGridAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieGridAdapter.class.getSimpleName()

    private final Context mContext
    private final Cursor mCursor

    public MovieGridAdapter(Context ctx, Cursor cursor, int flags) {
        super(ctx, cursor, 0)
        mContext = ctx
        mCursor = cursor
    }


    // The newView method is used to inflate a new view and return it,
    // don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LogDisplay.callLog(LOG_TAG,'newView is called',LogDisplay.GRID_ADAPTER_LOG_FLAG)
        return LayoutInflater.from(context).inflate(R.layout.single_grid_movie_item, parent, false)
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LogDisplay.callLog(LOG_TAG,'bindView is called',LogDisplay.GRID_ADAPTER_LOG_FLAG)
        final ImageView movieImageView = view.findViewById(R.id.grid_image_view) as ImageView
        final TextView movieNameView = view.findViewById(R.id.grid_text_view) as TextView
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${cursor.getString(GridFragment.COL_MOVIE_POSTER)}"
        PicassoLoadImage.loadMoviePosterUsingPicasso(mContext,posterPath,movieImageView)
        movieNameView.setText(cursor.getString(GridFragment.COL_MOVIE_TITLE))
    }
}