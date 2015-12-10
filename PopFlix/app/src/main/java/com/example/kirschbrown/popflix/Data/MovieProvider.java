package com.example.kirschbrown.popflix.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by jrkirsch on 11/8/2015.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_ID = 101;
    static final int FAVORITES = 200;
    static final int FAVORITES_WITH_ID = 201;
    static final int TRAILERS = 300;
    static final int TRAILERS_WITH_ID = 301;
    static final int REVIEWS = 400;
    static final int REVIEWS_WITH_ID = 401;

    // selection for movies
    private static final String sMovieIdSelectionSetting =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    // selection for favorites
    private static final String sFavoriteIdSelectionSetting =
            MovieContract.FavoriteEntry.TABLE_NAME+
                    "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ? ";

    // selection for favorites
    private static final String sTrailersIdSelectionSetting =
            MovieContract.TrailersEntry.TABLE_NAME+
                    "." + MovieContract.TrailersEntry.COLUMN_MOVIE_ID + " = ? ";

    // selection for favorites
    private static final String sReviewsIdSelectionSetting =
            MovieContract.ReviewsEntry.TABLE_NAME+
                    "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_TRAILERS + "/#", TRAILERS_WITH_ID);
        return matcher;
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[] {Long.toString(movieId)};
        String selection = sMovieIdSelectionSetting;

        return mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteById(Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieContract.FavoriteEntry.getFavoriteIdFromUri(uri);

        String[] selectionArgs = new String[] {Long.toString(movieId)};
        String selection = sFavoriteIdSelectionSetting;

        return mOpenHelper.getReadableDatabase().query(MovieContract.FavoriteEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerById(Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[] {Long.toString(movieId)};
        String selection = sTrailersIdSelectionSetting;

        return mOpenHelper.getReadableDatabase().query(MovieContract.TrailersEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewsById(Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[] {Long.toString(movieId)};
        String selection = sReviewsIdSelectionSetting;

        return mOpenHelper.getReadableDatabase().query(MovieContract.ReviewsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MovieContract.TrailersEntry.CONTENT_TYPE;
            case TRAILERS_WITH_ID:
                return MovieContract.TrailersEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_WITH_ID:
                return MovieContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(values.getAsLong(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewsEntry.buildReviewsUri(values.getAsLong(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MovieContract.TrailersEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TrailersEntry.buildTrailersUri(values.getAsLong(MovieContract.TrailersEntry.COLUMN_MOVIE_ID));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        MovieContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES_WITH_ID:
                long movieId = MovieContract.FavoriteEntry.getFavoriteIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sFavoriteIdSelectionSetting;
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS_WITH_ID:
                movieId = MovieContract.TrailersEntry.getTrailerIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sTrailersIdSelectionSetting;
                rowsDeleted = db.delete(MovieContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS_WITH_ID:
                movieId = MovieContract.ReviewsEntry.getReviewIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sReviewsIdSelectionSetting;
                rowsDeleted = db.delete(MovieContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITES:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIES_WITH_ID:
                long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sMovieIdSelectionSetting;
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILERS_WITH_ID:
                movieId = MovieContract.TrailersEntry.getTrailerIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sTrailersIdSelectionSetting;
                rowsUpdated = db.update(MovieContract.TrailersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEWS_WITH_ID:
                movieId = MovieContract.ReviewsEntry.getReviewIdFromUri(uri);
                selectionArgs = new String[]{Long.toString(movieId)};
                selection = sTrailersIdSelectionSetting;
                rowsUpdated = db.update(MovieContract.ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_WITH_ID:
            {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            case FAVORITES_WITH_ID: {
                retCursor = getFavoriteById(uri, projection, sortOrder);
                break;
            }
            case TRAILERS_WITH_ID:
            {
                retCursor = getTrailerById(uri, projection, sortOrder);
                break;
            }
            case REVIEWS_WITH_ID: {
                retCursor = getReviewsById(uri, projection, sortOrder);
                break;
            }
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
}
