package com.example.kirschbrown.popflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kirschbrown.popflix.Data.MovieContract;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback, MovieDetailFragment.DetailCallback{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                MovieDetailFragment fragment = new MovieDetailFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        InitApp app = (InitApp) getApplicationContext();
        if (app.isFirstRun) {
            updateMovies();
            app.isFirstRun = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(Uri contentUri) {
        long movieID = MovieContract.MovieEntry.getMovieIdFromUri(contentUri);
        new FetchMovieReviews(this).execute(movieID);
        new FetchMovieTrailers(this).execute(movieID);
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent showMovie = new Intent(this, MovieDetailActivity.class)
                .setData(contentUri);
            startActivity(showMovie);
        }
    }

    public void onLoadFinished(long movieID){
        if (mTwoPane) {
            MovieDetailFragment fragment = new MovieDetailFragment();
            if (movieID != -1) {
                Uri contentUri = MovieContract.MovieEntry.buildMovieUri(movieID);
                Bundle args = new Bundle();
                args.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    public void onSpinnerItemSelected(String sortString) {
        int deleted = 0;
        deleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        updateMovies();
    }

    public void onFavoriteClick(long movieID, View v) {
        Utilities.updateFavorite(this, movieID, v);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = settings.getString("sortString", "");
        if (sort.equals("favorites")){
            updateMovies();
        }
    }

    public void updateMovies() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = settings.getString("sortString", "");
        new FetchMovieTask(this).execute(sort);
    }
}
