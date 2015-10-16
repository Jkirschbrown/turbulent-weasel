package com.example.kirschbrown.popflix;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private View rootView;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        //Inflate the rootView for reference
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ImageView favorite = (ImageView) rootView.findViewById(R.id.favoriteButton);
        /* TODO favorite.setOnClickListener(new ImageView.OnClickListener(){
            public void OnClick(View view){
            }
        });
        */

        if (intent!=null && intent.hasExtra("MovieItemObject")) {
            //Get the Movie Object
            MovieItemObject movieObject = intent.getExtras().getParcelable("MovieItemObject");

            //Get the relevant Views
            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.detail_imageView);
            TextView ratingText = (TextView) rootView.findViewById(R.id.ratingText);
            TextView titleText = (TextView) rootView.findViewById(R.id.titleText);
            TextView releaseDateText = (TextView) rootView.findViewById(R.id.releaseDateText);
            TextView votesText = (TextView) rootView.findViewById(R.id.votesText);
            TextView plotText = (TextView) rootView.findViewById(R.id.plotText);

            Picasso.with(rootView.getContext()).load(movieObject.getPosterURL())
                    .placeholder(R.drawable.star_on) //TODO
                    .error(R.drawable.star_on) //TODO
                    .into(moviePoster);
            ratingText.setText(movieObject.getRating());
            titleText.setText(movieObject.getTitle());
            releaseDateText.setText(movieObject.getreleaseDate());
            votesText.setText(movieObject.getNumVotes());
            plotText.setText(movieObject.getPlot());
        }

        return rootView;
    }
}
