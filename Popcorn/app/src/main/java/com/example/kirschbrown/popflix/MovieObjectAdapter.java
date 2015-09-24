package com.example.kirschbrown.popflix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jrkirsch on 9/16/2015.
 */
public class MovieObjectAdapter extends ArrayAdapter<MovieItemObject> {

    Context context;
    ArrayList<MovieItemObject> movieArray;

    public MovieObjectAdapter(Context context, int resourceID, ArrayList<MovieItemObject> movies){
        super(context, resourceID, movies);
        this.movieArray = movies;
        this.context = context;
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView poster;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            poster = (ImageView) inflater.inflate(R.layout.grid_item_movie, null);
        } else {
            poster = (ImageView) convertView;
        }

        MovieItemObject movieItem = getItem(position);
        Picasso.with(context).load(movieItem.getPosterURL()).into(poster);

        //imageView.setImageURI(posterURI);
        return poster;
    }
}
