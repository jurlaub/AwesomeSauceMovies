package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;

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

//    private ArrayList<String> mPopularity;
//    private ArrayList<String> mAverageVote;
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

    //test - will combine this with getMovies
//    public ArrayList<MovieItem> getHighRatedMovies(){
//
//
//    }

    public MovieItem getMovieItem(int id){

        String tmpID = Integer.toString(id);

        this.getMovieItem(tmpID);

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

    // true if mMovieItems is empty or null
    public boolean movieLibraryNeedsToBeUpdated(){

        // false if elements present in mMovieItems
        if (mMovieItems != null) {
            if (mMovieItems.size() > 0) {
                return false;
            }
        }

        return true;

    }


    // stackoverflow.com/questions/14475556/how-to-sort-arraylist-of-objects
    private class MovieComparator implements Comparator<MovieItem> {

        @Override
        public int compare(MovieItem m1, MovieItem m2) {
            if (m1.getmVoteAvg() > m2.getmVoteAvg()) {
                return -1;
            } else if (m1.getmVoteAvg() < m2.getmVoteAvg()) {
                return 1;
            }
            return 0;

        }

    }



}
