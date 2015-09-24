package com.example.kirschbrown.popflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    private MovieObjectAdapter mMovieAdapter;
    public View rootView;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set this to allow for the fragment to have menu options.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate rootView for reference in Fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Globals globals = (Globals)getActivity().getApplicationContext();

        //create spinner for sorting
        Spinner spinner = (Spinner) rootView.findViewById(R.id.sortSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sortChoices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
                SharedPreferences.Editor setEdit = settings.edit();
                setEdit.putInt("spinnerIndex", pos);

                //Globals globals = (Globals) rootView.getApplication().getApplicationContext();
                String option = parent.getItemAtPosition(pos).toString();
                if (option.equals("Sort By: Most Popular")){
                    setEdit.putString("sortString", "popularity.desc");
                }
                if (option.equals("Sort By: Highest Rated")){
                    setEdit.putString("sortString", "vote_average.desc");
                }
                if (option.equals("Sort By: Lowest Rated")){
                    setEdit.putString("sortString","vote_average.asc");
                }
                if (option.equals("Sort By: Least Popular")){
                    setEdit.putString("sortString","popularity.asc");
                }
                setEdit.commit();

                updateMovies(settings.getString("sortString", ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //Get stored preference for spinner
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        //Globals globals = (Globals) getActivity().getApplicationContext();
        spinner.setSelection(settings.getInt("spinnerIndex", 0));

        //Create ArrayAdapter for GridView
        mMovieAdapter = new MovieObjectAdapter(getActivity(), R.layout.grid_item_movie, new ArrayList<MovieItemObject>());

        //Set adapter for GridView to custom adapter
        GridView movieGrid = (GridView) rootView.findViewById(R.id.Gridview_movies);
        movieGrid.setAdapter(mMovieAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        //Set up onclick listener
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent showMovie = new Intent(getActivity(), MovieDetailActivity.class);
            showMovie.putExtra("MovieItemObject", mMovieAdapter.getItem(position));
            startActivity(showMovie);
        }
    });
        return rootView;
    }

    //Start up by grabbing data from api
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        //Globals globals = (Globals) getActivity().getApplicationContext();
        updateMovies(settings.getString("sortString", ""));
    }

    //Function to grab movies
    public void updateMovies(String sort) {
        new FetchMovieTask().execute(sort);
    }

    private class FetchMovieTask extends AsyncTask<String, Void, MovieItemObject[]> {

        String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected MovieItemObject[] doInBackground(String... sort) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            MovieItemObject[] movieObjectArray;

            //Insert API key here-----------------------------------
            String apiKey = "";
            //------------------------------------------------------

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";
                final String VOTE_COUNT_PARAM = "vote_count.gte";

                //Build URI
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort[0])
                        .appendQueryParameter(VOTE_COUNT_PARAM, "50")
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();
                String myUri = builtUri.toString();

                //Log.v(LOG_TAG,myUri); //debugging

                //Make URL object
                URL url = new URL(myUri);

                //Open connection, set to GET, Connect
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Standard JSON reading stuff.
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonStr = null;
                }

                //Create reader object for buffering inputStream
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                //While there are lines to read, append newline character for debugging ease
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();

                //Log.v(LOG_TAG,"JSON String: " + moviesJsonStr); //debugging

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                moviesJsonStr = null;

            } finally {
                //Clean up connections and streams.
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                movieObjectArray = getMovieDataFromJsonString(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                movieObjectArray = null;
            }
            return movieObjectArray;
        }

        @Override
        protected void onPostExecute(MovieItemObject[] movies) {
            if (movies != null) {
                //Load the data into the global variable
                Globals globals = (Globals)(getActivity().getApplication());
                globals.setMovieArray(movies);
                //Load the data into the movie array
                mMovieAdapter.clear();
                for (MovieItemObject singleMovieObj : globals.getMovieArray()) {
                    mMovieAdapter.add(singleMovieObj);
                }
            }
        }

        private MovieItemObject[] getMovieDataFromJsonString(String movieJsonStr) throws JSONException{

            //Defining JSON query terms for TMDB
            final String TMDB_RESULT = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_PLOT = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_NUM_VOTES = "vote_count";
            final String TMDB_POSTER = "poster_path";
            String imageSize = "w342";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);

            //Movie data is returned in order of the sort parameter. Therefore, the query will
            //define the order of the list, and I will return the list items in that order.

            MovieItemObject[] movieObjectArray = new MovieItemObject[movieArray.length()];

            for(int i=0; i < movieArray.length(); i++){
                String title;
                String plot;
                String rating;
                String popularity;
                String releaseDate;
                String numVotes;
                String posterURL;

                JSONObject movieObject = movieArray.getJSONObject(i);
                title = movieObject.getString(TMDB_TITLE);
                plot = movieObject.getString(TMDB_PLOT);
                rating = movieObject.getString(TMDB_RATING) + "/10";
                popularity = movieObject.getString(TMDB_POPULARITY);
                numVotes = movieObject.getString(TMDB_NUM_VOTES);
                numVotes = "(" + numVotes + " " + getString(R.string.votes) + ")";
                releaseDate = movieObject.getString(TMDB_RELEASE_DATE);

                releaseDate = returnMonthString(releaseDate.substring(5,7)) + " " + releaseDate.substring(0,4);
                String poster = movieObject.getString(TMDB_POSTER);
                posterURL = "http://image.tmdb.org/t/p/" + imageSize + poster;

                movieObjectArray[i] = new MovieItemObject();
                movieObjectArray[i].setData(posterURL, title, plot, rating, popularity, numVotes, releaseDate);
            }

            return movieObjectArray;
        }

        private String returnMonthString(String monthVal){
            String monthStr = monthVal;
            switch (monthVal) {
                case "01":
                    monthStr = "Jan";
                case "02":
                    monthStr = "Feb";
                case "03":
                    monthStr = "Mar";
                case "04":
                    monthStr = "Apr";
                case "05":
                    monthStr = "May";
                case "06":
                    monthStr = "June";
                case "07":
                    monthStr = "July";
                case "08":
                    monthStr = "Aug";
                case "09":
                    monthStr = "Sep";
                case "10":
                    monthStr = "Oct";
                case "11":
                    monthStr = "Nov";
                case "12":
                    monthStr = "Dec";
            }
            return monthStr;
        }
    }
}
