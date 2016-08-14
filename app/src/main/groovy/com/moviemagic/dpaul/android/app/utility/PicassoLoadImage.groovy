package com.moviemagic.dpaul.android.app.utility

import android.content.Context
import android.widget.ImageView
import com.moviemagic.dpaul.android.app.R
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class PicassoLoadImage {

    //To load the movie poster - used for Grid Adapter, Detail page poster & Similar movie
    static void loadMoviePosterUsingPicasso(final Context context, final String imagePath, final ImageView imageView) {
        Picasso.with(context)
                .load(imagePath)
                .fit()
                .placeholder(R.drawable.grid_image_placeholder)
                .error(R.drawable.grid_image_error)
                .into(imageView)
    }

    //To load the person poster - used for Cast Adapter, Crew Adapter, etc
    static void loadMoviePersonImageUsingPicasso(final Context context, final String imagePath, final ImageView imageView) {
        Picasso.with(context)
                .load(imagePath)
                .fit()
                .placeholder(R.drawable.grid_image_placeholder)
                .error(R.drawable.na_person_icon)
                .into(imageView)
    }
}