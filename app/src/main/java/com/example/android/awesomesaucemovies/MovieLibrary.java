package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //TODO
    // Stores sort order: Key = Sort Preference option; Value = Movie ID list
    private Map<String, List<String>> mSortOrder;


    private static MovieLibrary sMovieLibrary;
    private Context mAppContext;


    public MovieLibrary(Context appContext ) {
        mAppContext = appContext;
        mMovieItems = new ArrayList<MovieItem>();
        mSortOrder = new HashMap<String, List<String>>();


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

    public void clearMovies(){
        mMovieItems.clear();
        Log.i(LOG_TAG, "mMovieItems cleared; ");
    }


    public ArrayList<MovieItem> getMovies(){
        return mMovieItems;
    }

    // TODO
    // test - will combine this with getMovies
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

        if (mMovieItems == null) {
            mMovieItems = new ArrayList<MovieItem>();
        }

        mMovieItems.add(m);
    }


    /* TODO
        remove from mMovieItems

        input: sortPreferenceString, newListOfIDs

        find oldList associated with sortPreferenceString
        oldList. removeAll(Collection<?> c)
        oldList. all other lists in mSortOrder
        for every item left in oldList remove id from mMovieItems

        mSortOrder(sortPreferenceString to newListOfIDs)


     */




    /* TODO
        Has dataset been captured before?

     */


    /* TODO
        Update dataset

     */



    /* TODO
        input:

        return: (boolean)
            true
            - if movieLibrary is null or empty
            - if a movieLibrary requires an update (i.e. MovieDatabase API request)

            false
            - if data currently in movieLibrary is sufficient


     */
    public boolean movieLibraryNeedsToBeUpdated(){

        // false if elements present in mMovieItems
        if (mMovieItems != null) {
            if (mMovieItems.size() > 0) {
                return false;
            }
        }

        return true;

    }


    // TODO
    // //stackoverflow.com/questions/14475556/how-to-sort-arraylist-of-objects
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
