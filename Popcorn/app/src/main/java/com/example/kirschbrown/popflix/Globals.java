package com.example.kirschbrown.popflix;

import android.app.Application;

/**
 * Created by jrkirsch on 9/17/2015.
 */
public class Globals extends Application {

    //Global variable to store cached movies on Application startup
    private MovieItemObject[] movieArray = null;
    private String sortOrder = "popularity.desc";

    public Globals() {
    }

    public MovieItemObject[] getMovieArray() {
        return this.movieArray;
    }

    public void setMovieArray(MovieItemObject[] movieArray) {
        this.movieArray = movieArray;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder){
        this.sortOrder = sortOrder;
    }
}
