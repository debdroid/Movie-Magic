package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.adapter.PersonCastAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonCrewAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonImageAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class PersonMovieActivity extends AppCompatActivity implements PersonMovieFragment.CallbackForCastClick,
                PersonMovieFragment.CallbackForCrewClick, PersonMovieFragment.CallbackForImageClick {
    private static final String LOG_TAG = PersonMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_movie)
        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Uri uri = getIntent().getData()
            if (uri) {
                LogDisplay.callLog(LOG_TAG, "Intent Data->${uri.toString()}", LogDisplay.PERSON_MOVIE_ACTIVITY_LOG_FLAG)
                final Bundle bundle = new Bundle()
                bundle.putParcelable(GlobalStaticVariables.MOVIE_PERSON_URI,uri)
                final PersonMovieFragment personMovieFragment = new PersonMovieFragment()
                personMovieFragment.setArguments(bundle)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.person_fragment_container, personMovieFragment)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data.', LogDisplay.PERSON_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person_fragment_menu, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.person_fragment_menu) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }

    /**
     * Fragment callback method for PersonCast - called when a movie item is clicked for person cast
     * @param movieId   Movie id of the selected movie
     * @param viewHolder    PersonCastAdapterViewHolder
     */
    @Override
    void onCastMovieItemSelected(int movieId, PersonCastAdapter.PersonCastAdapterViewHolder viewHolder) {
            final Bundle bundle = new Bundle()
            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
            bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
            final Intent intent = new Intent(this, DetailMovieActivity.class)
            intent.putExtras(bundle)
            startActivity(intent)
            //Start the animation
            overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Fragment callback method for PersonCrew - called when a movie item is clicked for person crew
     * @param movieId   Movie id of the selected movie
     * @param viewHolder    PersonCrewAdapterViewHolder
     */
    @Override
    void onCrewMovieItemSelected(int movieId, PersonCrewAdapter.PersonCrewAdapterViewHolder viewHolder) {
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Fragment callback method for PersonImage - called when a image item is clicked for person image
     * @param title   person name
     * @param imageFilePath   array of image profile path of the person
     * @param viewHolder    PersonImageAdapterViewHolder
     */
    @Override
    void onImageMovieItemSelected(String title, int adapterPosition, String[] imageFilePath, PersonImageAdapter.PersonImageAdapterViewHolder viewHolder) {
        final Bundle bundle = new Bundle()
        bundle.putString(GlobalStaticVariables.IMAGE_VIEWER_TITLE,title)
        bundle.putInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION, adapterPosition)
        bundle.putStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY,imageFilePath as ArrayList<String>)
        bundle.putBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG, false)
        final Intent intent = new Intent(this, ImageViewerActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }
}