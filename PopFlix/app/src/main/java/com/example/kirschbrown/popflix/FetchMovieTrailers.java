package com.example.kirschbrown.popflix;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kirschbrown.popflix.data.MovieContract;
import com.example.kirschbrown.popflix.data.MovieContract.TrailersEntry;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

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

        String[] selectionArgs = new String[]{Long.toString(0)};
        String selection = TrailersEntry.TABLE_NAME + "." + TrailersEntry.COLUMN_TRAILER_FAVORITE + " = ? ";
        int deleted = mContext.getContentResolver().delete(TrailersEntry.CONTENT_URI, selection, selectionArgs);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailersJsonStr = null;

        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            final String API_PARAM = "api_key";

            //Build URI
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(Long.toString(movieID[0]))
                    .appendPath("videos")
                    .appendQueryParameter(API_PARAM, BuildConfig.TMDB_API_KEY)
                    .build();
            String myUri = builtUri.toString();

            //Log.d(LOG_TAG, myUri);

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
        final String TMDB_ID = "id";
        final String TMDB_TRAILER_ID = "id";
        final String TMDB_TRAILER_NAME = "name";
        final String TMDB_TRAILER_KEY = "key";
        final String TMDB_TRAILER_SITE = "site";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            long movieID = movieJson.getLong(TMDB_ID);
            int isFavorite = Utilities.checkForFavorite(mContext, movieID);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                String trailerID;
                String trailerName;
                String trailerKey;
                String trailerURL;
                String trailerSite;

                JSONObject movieObject = movieArray.getJSONObject(i);
                trailerSite = movieObject.getString(TMDB_TRAILER_SITE);
                trailerID = movieObject.getString(TMDB_TRAILER_ID);
                trailerName = movieObject.getString(TMDB_TRAILER_NAME);
                trailerKey = movieObject.getString(TMDB_TRAILER_KEY);

                if (trailerSite.equals("YouTube")) {
                    trailerURL = "http://www.youtube.com/watch?v=" + trailerKey;

                    ContentValues trailerValues = new ContentValues();
                    trailerValues.put(TrailersEntry.COLUMN_MOVIE_ID, movieID);
                    trailerValues.put(TrailersEntry.COLUMN_TRAILER_ID, trailerID);
                    trailerValues.put(TrailersEntry.COLUMN_TRAILER_NAME, trailerName);
                    trailerValues.put(TrailersEntry.COLUMN_TRAILER_URL, trailerURL);
                    trailerValues.put(TrailersEntry.COLUMN_TRAILER_FAVORITE, isFavorite);

                    cVVector.add(trailerValues);
                }
            }

            int inserted = 0;
            //add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(TrailersEntry.CONTENT_URI, cvArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
