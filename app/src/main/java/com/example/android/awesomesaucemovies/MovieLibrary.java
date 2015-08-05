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
 */
public class MovieLibrary {

    private final static String LOG_TAG = MovieLibrary.class.getSimpleName();

    // JSONObject entries  &  order of popular ids
    private ArrayList<MovieItem> mMovieItems;

    // order of vote average


    private static MovieLibrary sMovieLibrary;
    private Context mAppContext;


    public MovieLibrary(Context appContext ) {
        mAppContext = appContext;
        mMovieItems = new ArrayList<MovieItem>();


    }

    // only return the singleton instance of the class
    public static MovieLibrary get(Context context) {
        if (sMovieLibrary == null) {
            sMovieLibrary = new MovieLibrary(context.getApplicationContext());
            Log.i("MovieLibrary", "Created new MovieLibrary" );

        } else {
            Log.i("MovieLibrary", " Used Existing MovieLibrary");
        }

        return sMovieLibrary;
    }


    public ArrayList<MovieItem> getMovies(){
        return mMovieItems;
    }

    public MovieItem getMovieItem(int id){

        String tmpID = Integer.toString(id);

        this.getMovieItem(tmpID);

//        for (MovieItem m: mMovieItems){
//            if (m.getmID().equals(tmpID)) {
//                return m;
//            }
//        }


        return null;
    }


    public MovieItem getMovieItem(String id){

        if(id != null) {

            for (MovieItem m: mMovieItems){
                if (m.getmID().equals(id)) {
                    Log.i(LOG_TAG, "Obtained Movie id:"+id + ",  " + m.getmTitle());
                    return m;
                }
            }
        }

        Log.e(LOG_TAG, "No Movie Obtained");
        return null;
    }


    public void addMovieItem(MovieItem m) {



        mMovieItems.add(m);
    }


}
