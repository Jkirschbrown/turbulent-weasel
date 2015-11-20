package com.example.kirschbrown.popflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.kirschbrown.popflix.data.MovieContract;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback, MovieDetailFragment.DetailCallback{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public static final String apiKey = "b7d4648e474d5a25d9ab25c884c9dc8c";

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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int firstLoad = settings.getInt("firstLoad", 0);
        if (firstLoad == 1) {
            updateMovies();
            SharedPreferences.Editor setEdit = settings.edit();
            setEdit.putInt("firstLoad", 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor setEdit = settings.edit();
        setEdit.putInt("firstLoad", 1);
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
            } else {
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        MovieGridFragment mgf = (MovieGridFragment)getSupportFragmentManager().findFragmentById(R.id.Gridview_movies);
//        if (null != mgf){
//            mgf.restartLoader();
//        }
//        MovieDetailFragment mdf = (MovieDetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
//        if (null != mdf) {
//            mdf.restartLoader();
//        }
//    }

    public void onSpinnerItemSelected(String sortString) {
        int deleted = 0;
        deleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        Log.d("In onSpinnerSelected", deleted + "rows deleted.");
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
        Log.d("updateMovies", " run");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = settings.getString("sortString", "");
        new FetchMovieTask(this).execute(sort);
    }
}
