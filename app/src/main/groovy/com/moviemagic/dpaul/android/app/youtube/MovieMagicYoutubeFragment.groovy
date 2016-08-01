package com.moviemagic.dpaul.android.app.youtube

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.utility.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicYoutubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener {
    private static final String LOG_TAG = MovieMagicYoutubeFragment.class.getSimpleName()

    //Error dialog id
    private static final int RECOVERY_ERROR_DIALOG_ID = 1

    public static final String YOUTUBE_VIDEO_ID_KEY = 'youtube_video_id_key'

    private List<String> mVideoIds
    private ArrayList<String> videoIdsArrayList

    //Empty constructor, to be used by the system while creating the fragment when embedded in XML
    MovieMagicYoutubeFragment () {
        LogDisplay.callLog(LOG_TAG,'MovieMagicYoutubeFragment empty constructor is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_FLAG)
    }

    /**
     * Returns a new instance of MovieMagicYoutubeFragment Fragment
     *
     * @param videoId The ID of the YouTube video to play
     */
    public static MovieMagicYoutubeFragment createMovieMagicYouTubeFragment(final String videoId) {
        final MovieMagicYoutubeFragment movieMagicYouTubeFragment = new MovieMagicYoutubeFragment()
        final Bundle bundle = new Bundle()
        bundle.putString(YOUTUBE_VIDEO_ID_KEY, videoId)
        movieMagicYouTubeFragment.setArguments(bundle)
        return movieMagicYouTubeFragment
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle)

        final Bundle arguments = getArguments()

        if (bundle != null && bundle.containsKey(YOUTUBE_VIDEO_ID_KEY)) {
            mVideoIds = bundle.getStringArrayList(YOUTUBE_VIDEO_ID_KEY)
        } else if (arguments != null && arguments.containsKey(YOUTUBE_VIDEO_ID_KEY)) {
            videoIdsArrayList = arguments.getStringArrayList(YOUTUBE_VIDEO_ID_KEY)
        }
        initialize(BuildConfig.YOUTUBE_API_KEY, this)
    }


    /**
     * Set the video id and initialize the player
     * This can be used when including the Fragment in an XML layout
     * @param videoId The ID of the video to play
     */
    public void setVideoId(final List<String> videoId) {
        mVideoIds = videoId
        initialize(BuildConfig.YOUTUBE_API_KEY, this)
    }

    @Override
    void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
        //when leaving fullscreen
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)

        //This flag controls the system UI such as the status and navigation bar, hiding and showing them
        //alongside the player UI
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI)

        if (mVideoIds) {
            if (restored) {
                youTubePlayer.play()
            } else {
                youTubePlayer.loadVideos(mVideoIds)
            }
        }
    }

    @Override
    void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_ERROR_DIALOG_ID).show()
        } else {
            //Handle the failure
            Toast.makeText(getActivity(), R.string.youtube_initialization_error, Toast.LENGTH_LONG).show()
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if(mVideoIds) {
            bundle.putStringArrayList(YOUTUBE_VIDEO_ID_KEY, new ArrayList<String>(mVideoIds))
        }
        super.onSaveInstanceState(bundle)
    }
}