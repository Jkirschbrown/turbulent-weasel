package com.example.kirschbrown.popflix.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kirschbrown.popflix.data.MovieContract.MovieEntry;
import com.example.kirschbrown.popflix.data.MovieContract.FavoriteEntry;
import com.example.kirschbrown.popflix.data.MovieContract.TrailersEntry;
import com.example.kirschbrown.popflix.data.MovieContract.ReviewsEntry;

/**
 * Created by jrkirsch on 11/8/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold all movie information.
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_NUM_VOTES + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_FAVORITE + " INTEGER NOT NULL, " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_NUM_VOTES + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_MOVIE_FAVORITE + " INTEGER NOT NULL, " +
                " UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailersEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_FAVORITE + " INTEGER NOT NULL, " +
                " UNIQUE (" + TrailersEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_FAVORITE + " INTEGER NOT NULL, " +
                " UNIQUE (" + ReviewsEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
