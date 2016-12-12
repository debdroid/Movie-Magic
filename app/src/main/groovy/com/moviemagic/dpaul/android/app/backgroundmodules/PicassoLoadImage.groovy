package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.Context
import android.widget.ImageView
import com.moviemagic.dpaul.android.app.R
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class PicassoLoadImage {

    //To load the movie poster - used for Grid Adapter & Similar movie
    static void loadMoviePosterImage(final Context context, final String imagePath, final ImageView imageView) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .tag(GlobalStaticVariables.PICASSO_POSTER_IMAGE_TAG)
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(imageView)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the person poster - used for Cast Adapter, Crew Adapter, etc
    static void loadMoviePersonImage(final Context context, final String imagePath, final ImageView imageView) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.na_person_icon)
                    .into(imageView)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the movie poster for detail fragment - used callback
    static void loadDetailFragmentPosterImage(final Context context, final String imagePath,
                                              final ImageView imageView, final Callback callback) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(imageView, callback)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the movie poster for detail fragment viewpager adapter
    static void loadDetailFragmentPagerAdapterImage(final Context context, final String imagePath,
                                                    final ImageView imageView) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .priority(Picasso.Priority.HIGH) //Picasso will treat this as high priority
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_STORE, NetworkPolicy.NO_CACHE)
                    .into(imageView)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the collection backdrop for detail fragment
    static void loadDetailFragmentCollectionBackdropImage(final Context context, final String imagePath, final ImageView imageView) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(imageView)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the collection backdrop - used callback
    static void loadCollectionBackdropImage(final Context context, final String imagePath,
                                            final ImageView imageView, final Callback callback) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .fit()
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(imageView, callback)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .fit()
                    .into(imageView)
        }
    }

    //To load the image for ViewPage adapter - used to display full screen images
    static void loadViewPagerImage(final Context context, final String imagePath, final ImageView imageView) {
        if (Utility.isReadyToDownload(context) && !Utility.isReducedDataOn(context)) {
            Picasso.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.grid_image_placeholder)
                    .error(R.drawable.grid_image_error)
                    .into(imageView)
        } else {
            Picasso.with(context)
                    .load(R.drawable.grid_image_placeholder)
                    .into(imageView)
        }
    }
}