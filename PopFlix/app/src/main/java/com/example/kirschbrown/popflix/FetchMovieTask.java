package com.example.kirschbrown.popflix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kirschbrown.popflix.Data.MovieContract;
import com.example.kirschbrown.popflix.Data.MovieContract.MovieEntry;
import com.example.kirschbrown.popflix.Data.MovieContract.FavoriteEntry;
import com.example.kirschbrown.popflix.Data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by jrkirsch on 11/9/2015.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Long>{

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    protected void onPostExecute(Long movieID) {
        ((MovieGridFragment.Callback) mContext).onLoadFinished(movieID);
    }

    protected Long doInBackground(String... sort) {

        long firstMovieID = -1;

        if (sort[0].equals("favorites")) {
            int inserted = 0;
            mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);
            Cursor favoritesCursor = mContext.getContentResolver().query(FavoriteEntry.CONTENT_URI, null, null, null, null);
            if (favoritesCursor.moveToFirst()) {
                firstMovieID = favoritesCursor.getLong(favoritesCursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID));
                Vector<ContentValues> cVVector = new Vector<ContentValues>(favoritesCursor.getCount());
                ContentValues map;
                do {
                    map = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(favoritesCursor, map);
                    cVVector.add(map);
                } while(favoritesCursor.moveToNext());
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
                }
            }
            favoritesCursor.close();

        } else {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";
                final String VOTE_COUNT_PARAM = "vote_count.gte";

                //Build URI
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort[0])
                        .appendQueryParameter(VOTE_COUNT_PARAM, "50")
                        .appendQueryParameter(API_PARAM, BuildConfig.TMDB_API_KEY)
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
                firstMovieID = getMovieDataFromJsonString(moviesJsonStr);
                //Log.v(LOG_TAG,"JSON String: " + moviesJsonStr); //debugging

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                moviesJsonStr = null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
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
        }
        return firstMovieID;
    }

    private long getMovieDataFromJsonString(String movieJsonStr) throws JSONException{

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
        String imageSize = "w342";
        long firstMovieID = -1;

        if (movieJsonStr != null) {
            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULT);
                //Movie data is returned in order of the sort parameter. Therefore, the query will
                //define the order of the list, and I will return the list items in that order.

                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

                JSONObject firstMovieObject = movieArray.getJSONObject(0);
                firstMovieID = firstMovieObject.getLong(TMDB_ID);

                for (int i = 0; i < movieArray.length(); i++) {
                    String title;
                    String plot;
                    double rating;
                    double popularity;
                    String releaseDate;
                    double numVotes;
                    String posterURL;
                    long movieID;

                    JSONObject movieObject = movieArray.getJSONObject(i);
                    movieID = movieObject.getLong(TMDB_ID);
                    title = movieObject.getString(TMDB_TITLE);
                    plot = movieObject.getString(TMDB_PLOT);
                    rating = movieObject.getDouble(TMDB_RATING);
                    popularity = movieObject.getDouble(TMDB_POPULARITY);
                    numVotes = movieObject.getDouble(TMDB_NUM_VOTES);
                    releaseDate = movieObject.getString(TMDB_RELEASE_DATE);
                    String poster = movieObject.getString(TMDB_POSTER);
                    posterURL = "http://image.tmdb.org/t/p/" + imageSize + poster;
                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieID);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_PLOT, plot);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_RATING, rating);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_NUM_VOTES, numVotes);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, posterURL);
                    movieValues.put(MovieEntry.COLUMN_MOVIE_FAVORITE, Utilities.checkForFavorite(mContext, movieID));

                    cVVector.add(movieValues);
                }

                int inserted = 0;
                //add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return firstMovieID;
    }
}
