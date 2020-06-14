package com.example.kirschbrown.popflix;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.kirschbrown.popflix.Data.MovieContract;
import com.example.kirschbrown.popflix.Data.MovieProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jrkirsch on 11/1/2015.
 */
public class Utilities {

    public static String formatReleaseDate(String monthVal){
        String monthStr = "Error";
        switch (monthVal.substring(5, 7)) {
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

        String outString = monthStr + " " + monthVal.substring(0,4);

        return outString;
    }

    public static String formatRating(Context context, double rating){
        return context.getString(R.string.format_rating, rating);
    }

    public static String formatPopularity(Context context, double popularity){
        return context.getString(R.string.format_popularity, popularity);
    }

    public static String formatNumVotes(Context context, double numVotes){
        return context.getString(R.string.format_numVotes, numVotes);
    }

    public static Uri getFirstMovieId(Context context) {
        Uri outUri = null;
        Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            Long movieId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            outUri = MovieContract.MovieEntry.buildMovieUri(movieId);
        }
        cursor.close();
        return outUri;
    }

    public static int checkForFavorite(Context mContext, long movieId) {
        Cursor favoriteCursor = mContext.getContentResolver().query(
                MovieContract.FavoriteEntry.buildFavoriteUri(movieId),
                null, null, null, null);
        int state;
        if (favoriteCursor.moveToFirst()){
            state = 1;
        } else {
            state = 0;
        }
        favoriteCursor.close();
        return state;
    }

    public static void updateFavorite(Context context, long movieID, View v) {

        Cursor cursor = context.getContentResolver().query(MovieContract.FavoriteEntry.buildFavoriteUri(movieID), null, null, null, null);
        Cursor cursor2 = context.getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movieID), null, null, null, null);
        if (cursor2.moveToFirst()) {
            ContentValues values = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor2, values);
            values.remove(MovieContract.MovieEntry._ID);
            String posterURL = cursor2.getString(cursor2.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
            //Log.d("URL", posterURL);
            // Get file object for movie poster ready
            File dir = context.getFilesDir();
            String fileName = movieID + ".jpg";
            File file = new File(dir, fileName);
            if (cursor.moveToFirst()) {
                // Already a favorite so delete it and update movie table
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, 0);
                boolean check = file.delete();
                //Log.d("File delete", Boolean.toString(check));
                int deleted = context.getContentResolver().delete(MovieContract.FavoriteEntry.buildFavoriteUri(movieID), null, null);
                context.getContentResolver().update(MovieContract.MovieEntry.buildMovieUri(movieID), values, null, null);
                //Log.d("In onFavoriteClick", deleted + " Favorite removed.");
            } else {
                // Not a favorite, so add it and update movie table
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, 1);
                context.getContentResolver().update(MovieContract.MovieEntry.buildMovieUri(movieID), values, null, null);
                // Put poster image in storage
                String[] inputStr = new String[]{Long.toString(movieID), posterURL};
                new FetchMoviePoster(context).execute(inputStr);
                // Get new path to poster image in storage
                String posterLocation = file.getAbsolutePath();
                // Update favorites database with poster image location in storage
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterLocation);
                //Log.d("URL", values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
                context.getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
                //Log.d("In onFavoriteClick", "1 Favorite added.");
            }
            cursor2.close();
            cursor.close();
        }
    }
}
