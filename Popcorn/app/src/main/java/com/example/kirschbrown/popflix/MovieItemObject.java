package com.example.kirschbrown.popflix;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jrkirsch on 9/15/2015.
 */
public class MovieItemObject implements Parcelable{
    private String title;
    private String plot;
    private String rating;
    private String popularity;
    private String numVotes;
    private String releaseDate;
    private String posterURL;
    private Boolean favorite = false;

    public MovieItemObject() {

    }

    public MovieItemObject(Parcel in) {
        title = in.readString();
        plot = in.readString();
        popularity = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        numVotes = in.readString();
        posterURL = in.readString();
    }

    public void setData(String posterUrl, String title, String plot, String rating, String popularity, String numVotes, String releaseDate) {
        this.title = title;
        this.plot = plot;
        this.rating = rating;
        this.popularity = popularity;
        this.numVotes = numVotes;
        this.releaseDate = releaseDate;
        this.posterURL = posterUrl;
    }

    public String getPosterURL() {
        return(this.posterURL);
    }

    public String getPopularity() { return this.popularity; }

    public String getRating() { return this.rating; }

    public String getreleaseDate() { return this.releaseDate; }

    public String getPlot() { return this.plot; }

    public String getTitle() {
        return this.title;
    }

    public String getNumVotes() { return this.numVotes; }

    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(title);
        parcel.writeString(plot);
        parcel.writeString(popularity);
        parcel.writeString(rating);
        parcel.writeString(releaseDate);
        parcel.writeString(numVotes);
        parcel.writeString(posterURL);
    }

    public static final Parcelable.Creator<MovieItemObject> CREATOR = new Parcelable.Creator<MovieItemObject>() {
        @Override
        public MovieItemObject createFromParcel(Parcel parcel){ return new MovieItemObject(parcel); }

        @Override
        public MovieItemObject[] newArray(int i) { return new MovieItemObject[i]; }
    };

    public int describeContents(){
        return 0;
    }

}
