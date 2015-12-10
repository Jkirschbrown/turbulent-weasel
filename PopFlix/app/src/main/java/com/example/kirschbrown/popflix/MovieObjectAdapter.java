package com.example.kirschbrown.popflix;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by jrkirsch on 9/16/2015.
 */
public class MovieObjectAdapter extends CursorAdapter {

    public MovieObjectAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        //int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.grid_item_movie;
//        // Determine layoutId from viewType
//        if (viewType == VIEW_TYPE_TODAY) {
//            layoutId = R.layout.list_item_forecast_today;
//        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
//            layoutId = R.layout.list_item_forecast;
//        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterURL = cursor.getString(MovieGridFragment.COL_MOVIE_POSTER);
        Long movieID = cursor.getLong(MovieGridFragment.COL_MOVIE_ID);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = settings.getString("sortString", "");
        if (sort.equals("favorites")) {
            File dir = context.getFilesDir();
            File file = new File(dir, Long.toString(movieID) + ".jpg");
            Picasso.with(context).load(file).into(viewHolder.posterView);
        } else {
            Picasso.with(context).load(posterURL).into(viewHolder.posterView);
        }
    }

    public static class ViewHolder {
        public final ImageView posterView;

        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_movie);
        }
    }
}

