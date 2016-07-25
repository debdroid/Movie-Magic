package com.moviemagic.dpaul.android.app.utility

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.moviemagic.dpaul.android.app.R
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class BackdropImageFragment extends Fragment {

    BackdropImageFragment() {

    }

    public static final String BACKDROP_KEY = 'backdrop_key'
    @Override
    View onCreateView(LayoutInflater inflater,
                      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.backdrop_single_image_view,container,false)
        ImageView backdropImageView = rootView.findViewById(R.id.movie_detail_backdrop_image) as ImageView
        Bundle args = getArguments()
        String backdropPath = args.getString(BACKDROP_KEY)
        Picasso.with(getActivity())
                .load(backdropPath)
                .placeholder(R.drawable.grid_image_placeholder)
                .error(R.drawable.na_person_icon)
//                .resize(400,300)
                .into(backdropImageView)
        backdropImageView.setImageResource(R.drawable.grid_image_placeholder)
        return rootView
    }
}