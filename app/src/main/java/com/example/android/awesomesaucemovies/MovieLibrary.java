package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by dev on 7/20/15.
 *
 * Singleton will be used to store data gathered from Movie API.
 *
 * adapted from Android Programming: The Big Nerd Ranch Guide -- chapter 9 Setting up a singleton
 *
 *
 * A future project would be to store data in the app to reduce network traffic.
 *
 *
 *
 */
public class MovieLibrary {

    private final static String LOG_TAG = MovieLibrary.class.getSimpleName();

    // JSONObject entries  &  order of popular ids
    private ArrayList<MovieItem> mMovieItems;



    private static MovieLibrary sMovieLibrary;
    private Context mAppContext;


    public MovieLibrary(Context appContext) {
        mAppContext = appContext;
        mMovieItems = new ArrayList<MovieItem>();



    }

    // only return the singleton instance of the class
    public static MovieLibrary get(Context context) {
        if (sMovieLibrary == null) {
            sMovieLibrary = new MovieLibrary(context.getApplicationContext());
            Log.i("MovieLibrary", "Created new MovieLibrary");

        } else {
            Log.i("MovieLibrary", " Used Existing MovieLibrary");
        }

        return sMovieLibrary;
    }

    public void clearMovies() {
        mMovieItems.clear();
        Log.i(LOG_TAG, "mMovieItems cleared; ");
    }


    public ArrayList<MovieItem> getMovies() {
        return mMovieItems;
    }

    public void restoreMovieLibrary(ArrayList<MovieItem> movieItems) {


        if (movieItems != null) {
            if (mMovieItems != null) {
                mMovieItems = movieItems;
                Log.i(LOG_TAG, "restoreMovieLibrary");
            }
        }
    }


    public MovieItem getMovieItem(int id) {

        String tmpID = Integer.toString(id);

        this.getMovieItem(tmpID);

        return null;
    }


    public MovieItem getMovieItem(String id) {

        if (id != null) {

            for (MovieItem m : mMovieItems) {
                if (m.getmID().equals(id)) {
                    Log.i(LOG_TAG, "Obtained Movie id:" + id + ",  " + m.getmTitle());
                    return m;
                }
            }
        }

        Log.e(LOG_TAG, "No Movie Obtained");
        return null;
    }


    public void addMovieItem(MovieItem m) {

        if (mMovieItems == null) {
            mMovieItems = new ArrayList<MovieItem>();
        }

        mMovieItems.add(m);
    }

}