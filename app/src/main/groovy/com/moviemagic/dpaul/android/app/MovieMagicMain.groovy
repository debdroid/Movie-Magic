package com.moviemagic.dpaul.android.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import groovy.transform.CompileStatic

@CompileStatic
public class MovieMagicMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_magic_main)
    }
}
