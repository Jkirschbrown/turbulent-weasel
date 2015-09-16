package com.example.kirschbrown.popcorn;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    private ArrayAdapter<String> mMovieAdapter;
    private View rootView;

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate rootView for reference in Fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

/*        //Create Fake Data to populate
        String[] testList = {"Test1", "Test2", "Test3", "Test4", "Test5", "Test6", "Test7"};
        List<String> testData = new ArrayList<String>(Arrays.asList(testList));*/

        //Create ArrayAdapter for GridView
        mMovieAdapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item_movie, new ArrayList<String>());

        GridView movieGrid = (GridView) rootView.findViewById(R.id.Gridview_movies);
        movieGrid.setAdapter(mMovieAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public void updateMovies() {
        new FetchMovieTask().execute("");
    }

    private class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected String[] doInBackground(String... sort) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            String[] outStr = null;
            String apiKey = "";

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                //Build URI
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort[0])
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();
                String myUri = builtUri.toString();

                Log.v(LOG_TAG,myUri); //debugging

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

                Log.v(LOG_TAG,"JSON String: " + moviesJsonStr);

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
                outStr = getMovieDataFromJsonString(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getMovieDataFromJsonString: ", e);
                outStr = null;
            }
            System.out.print(outStr);
            return outStr;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mMovieAdapter.clear();
                for (String singleMovieStr : strings){
                    mMovieAdapter.add(singleMovieStr);
                }
            }
        }

        private String[] getMovieDataFromJsonString(String movieJsonStr) throws JSONException{

            //Defining JSON query terms for TMDB
            final String TMDB_RESULT = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_PLOT = "overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER = "poster_path";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);

            //Movie data is returned in order of the sort parameter. Therefore, the query will
            //define the order of the list, and I will return the list items in that order.

            String[] resultStr = new String[movieArray.length()];
            for(int i=0; i < movieArray.length(); i++){
                String title;
                String plot;
                String rating;
                String popularity;
                String releaseDate;
                Uri posterURI;

                JSONObject movieObject = movieArray.getJSONObject(i);
                title = movieObject.getString(TMDB_TITLE);
                plot = movieObject.getString(TMDB_PLOT);
                rating = movieObject.getString(TMDB_RATING);
                popularity = movieObject.getString(TMDB_POPULARITY);
                releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
                String poster = movieObject.getString(TMDB_POSTER);
                posterURI = Uri.parse("http://image.tmdb.org/t/p/w185/").buildUpon()
                        .appendPath(poster)
                        .build();

                resultStr[i] = poster;
            }

            return resultStr;
        }
    }
}
