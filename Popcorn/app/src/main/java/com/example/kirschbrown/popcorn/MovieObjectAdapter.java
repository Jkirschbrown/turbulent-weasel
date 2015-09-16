package com.example.kirschbrown.popcorn;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.net.URI;

/**
 * Created by jrkirsch on 9/15/2015.
 */
public class MovieObjectAdapter extends BaseAdapter {
    private String title;
    private String plot;
    private String rating;
    private String popularity;
    private String releaseDate;
    private Uri posterURI;

    public MovieObjectAdapter(Uri posterUrl) {
        posterURI = posterUrl;
    }

    public int getCount() {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_movie, null);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(posterURI);
        return imageView;
    }

}
