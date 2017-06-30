/*
 * Copyright 2017 Debashis Paul

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView;
import groovy.transform.CompileStatic

@CompileStatic
class SplashScreen extends AppCompatActivity {

    private ImageView imageView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        // Make the splash screen full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        imageView = findViewById(R.id.splash_screen_logo) as ImageView
        final Animation rotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_image_rotate)
        imageView.setAnimation(rotateAnimation)
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            void onAnimationStart(Animation animation) {
            }

            @Override
            void onAnimationEnd(Animation animation) {
                startMainActivity()
            }

            @Override
            void onAnimationRepeat(Animation animation) {
            }
        })
    }

    void startMainActivity() {
        final Intent intent = new Intent(this, MovieMagicMainActivity.class)
        startActivity(intent)
        // Finish the current activity
        finish()
    }
}