package com.example.kirschbrown.popflix;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jrkirsch on 11/19/2015.
 */
public class FetchMovieTrailers extends AsyncTask<Long, Void, Void>{

    private final String LOG_TAG = FetchMovieTrailers.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTrailers(Context context) {
        mContext = context;
    }

    protected Void doInBackground(Long... movieID) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailersJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            final String SORT_PARAM = "sort_by";
            final String API_PARAM = "api_key";
            final String VOTE_COUNT_PARAM = "vote_count.gte";

            //Build URI
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(Long.toString(movieID[0]))
                    .appendPath("videos")
                    .build();
            String myUri = builtUri.toString();

            Log.d(LOG_TAG, myUri);

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
                trailersJsonStr = null;
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
                trailersJsonStr = null;
            }
            trailersJsonStr = buffer.toString();
            getTrailerDataFromJsonString(trailersJsonStr);
            //Log.v(LOG_TAG,"JSON String: " + moviesJsonStr); //debugging

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
        return null;
    }

    private void getTrailerDataFromJsonString(String movieJsonStr) throws JSONException{
        //Defining JSON query terms for TMDB
        final String TMDB_RESULT = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_PLOT = "overview";
        final String TMDB_RATING = "vote_average";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_NUM_VOTES = "vote_count";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_ID = "id";
    }
}
