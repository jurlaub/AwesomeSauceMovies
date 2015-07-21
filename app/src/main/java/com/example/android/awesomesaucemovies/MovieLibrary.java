package com.example.android.awesomesaucemovies;

import android.content.Context;

/**
 * Created by dev on 7/20/15.
 *
 * Singleton will be used to store data gathered from Movie API.
 *
 * adapted from Android Programming: The Big Nerd Ranch Guide -- chapter 9 Setting up a singleton
 *
 *
 */
public class MovieLibrary {

    // JSONObject entries

    // order of popular ids

    // order of vote average


    private static MovieLibrary sMovieLibrary;
    private Context mAppContext;


    public MovieLibrary(Context appContext ) {
        mAppContext = appContext;


    }

    // only return the singleton instance of the class
    public static MovieLibrary get(Context context) {
        if (sMovieLibrary == null) {
            sMovieLibrary = new MovieLibrary(context.getApplicationContext());
        }

        return sMovieLibrary;
    }


}
