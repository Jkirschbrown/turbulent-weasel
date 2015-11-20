package com.example.kirschbrown.popflix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirschbrown.popflix.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private Uri mUri;

    private static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_PLOT,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_NUM_VOTES,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };

    private ImageView mPosterView;
    private ImageView mFavoritesView;
    private TextView mTitleView;
    private TextView mPlotView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private TextView mNumVotesView;
    private long movieID;

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_INDEX_ID = 0;
    private static final int COL_MOVIE_ID = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_PLOT = 3;
    private static final int COL_MOVIE_RATING = 4;
    private static final int COL_MOVIE_RELEASE = 5;
    private static final int COL_MOVIE_NUMVOTES = 6;
    private static final int COL_MOVIE_POSTER = 7;
    private static final int COL_MOVIE_FAVORITES = 8;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();
        if (arguments != null){
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        } else {
            mUri = Utilities.getFirstMovieId(getActivity());
        }

        //Inflate the rootView for reference
        final View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //Get the relevant Views
        mPosterView = (ImageView) rootView.findViewById(R.id.detail_imageView);
        mRatingView = (TextView) rootView.findViewById(R.id.ratingText);
        mTitleView = (TextView) rootView.findViewById(R.id.titleText);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.releaseDateText);
        mNumVotesView = (TextView) rootView.findViewById(R.id.votesText);
        mPlotView = (TextView) rootView.findViewById(R.id.plotText);
        mFavoritesView = (ImageView) rootView.findViewById(R.id.favoriteButton);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return; }

        movieID = data.getLong(COL_MOVIE_ID);

        String title = data.getString(COL_MOVIE_TITLE);
        mTitleView.setText(title);

        String plot = data.getString(COL_MOVIE_PLOT);
        mPlotView.setText(plot);

        String rating = Utilities.formatRating(getActivity(), data.getDouble(COL_MOVIE_RATING));
        mRatingView.setText(rating);

        String votesStr = Utilities.formatNumVotes(getActivity(), data.getDouble(COL_MOVIE_NUMVOTES));
        mNumVotesView.setText(votesStr);

        String releaseDate = Utilities.formatReleaseDate(data.getString(COL_MOVIE_RELEASE));
        mReleaseDateView.setText(releaseDate);

        String posterURL = data.getString(COL_MOVIE_POSTER);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Log.d("Testing: ", posterURL);
        String sort = settings.getString("sortString", "");
        if (sort.equals("favorites")) {
            File dir = getActivity().getFilesDir();
            //String dirString = dir.getAbsolutePath();
            //Log.d("File dir", dirString);
            File file = new File(dir, Long.toString(movieID) + ".jpg");
            //Log.d("File dir", Long.toString(file.length()));
            Picasso.with(getActivity()).load(file).into(mPosterView);
        } else {
            Picasso.with(getActivity()).load(posterURL).into(mPosterView);
        }

        int favorite = data.getInt(COL_MOVIE_FAVORITES);
        if (favorite == 1) {
            Picasso.with(getActivity()).load(R.drawable.star_on)
                    .placeholder(R.drawable.star_on) //TODO
                    .error(R.drawable.star_on) //TODO
                    .into(mFavoritesView);
        } else {
            Picasso.with(getActivity()).load(R.drawable.star_off)
                    .placeholder(R.drawable.star_off) //TODO
                    .error(R.drawable.star_off) //TODO
                    .into(mFavoritesView);
        }

        mFavoritesView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((DetailCallback) getActivity()).onFavoriteClick(movieID, v);
//                if (newState == 1) {
//                    Picasso.with(getActivity()).load(R.drawable.star_on)
//                            .placeholder(R.drawable.star_on) //TODO
//                            .error(R.drawable.star_on) //TODO
//                            .into(mFavoritesView);
//                } else {
//                    Picasso.with(getActivity()).load(R.drawable.star_off)
//                            .placeholder(R.drawable.star_off) //TODO
//                            .error(R.drawable.star_off) //TODO
//                            .into(mFavoritesView);
//                }
            }
        });
//        /* TODO favorite.setOnClickListener(new ImageView.OnClickListener(){
//            public void OnClick(View view){
//            }
//        });

//        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(createShareForecastIntent());
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

//    void onLocationChanged( String newLocation ) {
//        // replace the uri, since the location has changed
//        Uri uri = mUri;
//        if (null != uri) {
//            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
//            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
//            mUri = updatedUri;
//            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
//        }
//    }

    void restartLoader() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    public interface DetailCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onFavoriteClick(long movieID, View v);
    }

}