package com.example.kirschbrown.popflix;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.kirschbrown.popflix.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MovieObjectAdapter mMovieAdapter;
    public View rootView;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };

    static final int COL_INDEX_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_FAVORITE = 3;

    public MovieGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set this to allow for the fragment to have menu options.
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate rootView for reference in Fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

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
                int previousPos = settings.getInt("spinnerIndex", 0);
                if (pos != previousPos) {
                    String sortString = "";
                    SharedPreferences.Editor setEdit = settings.edit();
                    setEdit.putInt("spinnerIndex", pos);

                    String option = parent.getItemAtPosition(pos).toString();
                    if (option.equals("Sort By: Most Popular")) {
                        sortString = "popularity.desc";
                    }
                    if (option.equals("Sort By: Highest Rated")) {
                        sortString = "vote_average.desc";
                    }
                    if (option.equals("Sort By: Lowest Rated")) {
                        sortString = "vote_average.asc";
                    }
                    if (option.equals("Sort By: Least Popular")) {
                        sortString = "popularity.asc";
                    }
                    if (option.equals("Favorites")) {
                        sortString = "favorites";
                    }
                    setEdit.putString("sortString", sortString);
                    setEdit.apply();
                    ((Callback) getActivity()).onSpinnerItemSelected(sortString);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        //Get stored preference for spinner
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        spinner.setSelection(settings.getInt("spinnerIndex", 0));

        //Create ArrayAdapter for GridView
        mMovieAdapter = new MovieObjectAdapter(getActivity(), null, 0);

        //Set adapter for GridView to custom adapter
        GridView movieGrid = (GridView) rootView.findViewById(R.id.Gridview_movies);
        movieGrid.setAdapter(mMovieAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        //Set up onclick listener
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            if (cursor != null) {
                ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)));
                //Log.d("Testing ", MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)).toString());
            }
        }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri moviesUri = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
        public void onSpinnerItemSelected(String sortString);
        public void onLoadFinished(long movieID);
    }
}
