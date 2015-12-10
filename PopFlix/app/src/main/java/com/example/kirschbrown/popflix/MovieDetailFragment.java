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
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirschbrown.popflix.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private Uri mUri;
    private Uri mTrailerUri;
    private Uri mReviewUri;

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;

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

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT
    };

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailersEntry.TABLE_NAME + "." + MovieContract.TrailersEntry._ID,
            MovieContract.TrailersEntry.TABLE_NAME + "." + MovieContract.TrailersEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailersEntry.TABLE_NAME + "." + MovieContract.TrailersEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailersEntry.TABLE_NAME + "." + MovieContract.TrailersEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailersEntry.TABLE_NAME + "." + MovieContract.TrailersEntry.COLUMN_TRAILER_URL
    };

    private ShareActionProvider mShareActionProvider;
    private ImageView mPosterView;
    private ImageView mFavoritesView;
    private TextView mTitleView;
    private TextView mPlotView;
    private TextView mRatingView;
    private TextView mReleaseDateView;
    private TextView mNumVotesView;
    private LinearLayout mReviewsListView;
    private LinearLayout mTrailersView;
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

    static final int COL_REVIEW_ID = 2;
    static final int COL_REVIEW_AUTHOR = 3;
    static final int COL_REVIEW_CONTENT = 4;

    static final int COL_TRAILER_ID = 2;
    static final int COL_TRAILER_NAME = 3;
    static final int COL_TRAILER_URL = 4;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        if (mUri != null) {
            long movieID = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            mReviewUri = MovieContract.ReviewsEntry.buildReviewsUri(movieID);
            mTrailerUri = MovieContract.TrailersEntry.buildTrailersUri(movieID);
            new FetchMovieReviews(getActivity()).execute(movieID);
            new FetchMovieTrailers(getActivity()).execute(movieID);
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

        mReviewsListView = (LinearLayout) rootView.findViewById(R.id.reviewsList);

        mTrailersView = (LinearLayout) rootView.findViewById(R.id.trailersList);
        return rootView;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        switch (id) {
            case 0:
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
                break;
            case 1:
                if ( null != mTrailerUri ) {
                    return new CursorLoader(
                            getActivity(),
                            mTrailerUri,
                            TRAILER_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;
            case 2:
                if ( null != mReviewUri ) {
                    return new CursorLoader(
                            getActivity(),
                            mReviewUri,
                            REVIEW_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return; }

        LayoutInflater inflater;

        switch(loader.getId()) {

            case 0:
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
                String sort = settings.getString("sortString", "");
                if (sort.equals("favorites")) {
                    File dir = getActivity().getFilesDir();
                    File file = new File(dir, Long.toString(movieID) + ".jpg");
                    Picasso.with(getActivity()).load(file).into(mPosterView);
                } else {
                    Picasso.with(getActivity()).load(posterURL).into(mPosterView);
                }

                int favorite = data.getInt(COL_MOVIE_FAVORITES);
                if (favorite == 1) {
                    Picasso.with(getActivity()).load(R.drawable.star_on)
                            .placeholder(R.drawable.star_on)
                            .error(R.drawable.star_on)
                            .into(mFavoritesView);
                } else {
                    Picasso.with(getActivity()).load(R.drawable.star_off)
                            .placeholder(R.drawable.star_off)
                            .error(R.drawable.star_off)
                            .into(mFavoritesView);
                }

                mFavoritesView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DetailCallback) getActivity()).onFavoriteClick(movieID, v);
                    }
                });
                break;
            case 1:
                inflater = LayoutInflater.from(getActivity());
                data.moveToFirst();
                setShare(data.getString(COL_TRAILER_URL));
                data.moveToPosition(-1);
                if (mTrailersView.getChildCount() > 0) {
                    mTrailersView.removeAllViews();
                }
                while (data.moveToNext()) {
                    final View trailerItem = inflater.inflate(R.layout.trailer_list_item, null, false);
                    final String trailerURL = data.getString(COL_TRAILER_URL);
                    TextView trailerName = (TextView) trailerItem.findViewById(R.id.trailerNameTextView);
                    String trailerText = data.getString(COL_TRAILER_NAME);
                    trailerName.setText(trailerText);
                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(trailerURL);
                            uri = Uri.parse("vnd.youtube:" + uri.getQueryParameter("v"));
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    mTrailersView.addView(trailerItem);
                }
                break;

            case 2:
                inflater = LayoutInflater.from(getActivity());
                if (mReviewsListView.getChildCount() > 0) {
                    mReviewsListView.removeAllViews();
                }
                data.moveToPosition(-1);
                while (data.moveToNext()) {
                    final View reviewItem = inflater.inflate(R.layout.review_list_item, null, false);
                    TextView author = (TextView) reviewItem.findViewById(R.id.reviewAuthorText);
                    ReviewTextView content = (ReviewTextView) reviewItem.findViewById(R.id.reviewContentText);
                    String authorText = data.getString(COL_REVIEW_AUTHOR);
                    author.setText(authorText);
                    String contentText = data.getString(COL_REVIEW_CONTENT);
                    content.setReviewContent(contentText);
                    reviewItem.post(new Runnable() {
                        @Override
                        public void run() {
                            ReviewTextView content = (ReviewTextView) reviewItem.findViewById(R.id.reviewContentText);
                            TextView readMore = (TextView) reviewItem.findViewById(R.id.readFurther);
                            boolean isCompressed = content.setInitialState();
                            if (isCompressed) {
                                readMore.setVisibility(View.VISIBLE);
                            } else {
                                readMore.setVisibility(View.GONE);
                            }
                        }
                    });
                    //content.setMaxLines(4);
                    reviewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReviewTextView content = (ReviewTextView) v.findViewById(R.id.reviewContentText);
                            TextView readMore = (TextView) reviewItem.findViewById(R.id.readFurther);
                            boolean isCompressed = content.changeState();
                            if (isCompressed) {
                                readMore.setVisibility(View.VISIBLE);
                            } else {
                                readMore.setVisibility(View.GONE);
                            }
                        }
                    });
                    mReviewsListView.addView(reviewItem);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (getId()) {
            case 1:

                break;
            case 2:
                //mReviewAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }

    private void setShare(String trailerUrl) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        share.putExtra(Intent.EXTRA_SUBJECT, "Movie Trailer!");
        share.putExtra(Intent.EXTRA_TEXT, "Check out this movie trailer!!\n" + trailerUrl);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(share);
        }
    }

    void restartLoader() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
        getLoaderManager().restartLoader(TRAILER_LOADER, null, this);
    }

    public interface DetailCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onFavoriteClick(long movieID, View v);
    }

}