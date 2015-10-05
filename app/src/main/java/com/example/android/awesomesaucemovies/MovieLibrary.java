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

    // JSONObject entries  &  order of popular Movies
    private ArrayList<MovieItem> mMovieItems;



    private static MovieLibrary sMovieLibrary;
    private Context mAppContext;

    /*
        mSearchPreference

        When an API request is generated & MovieItems are added to the MovieLibrary,
        Search preference should be stored. This tracks the search associated with the MovieItems in
        the Library.

     */
    private String mSearchPreference;




    public MovieLibrary(Context appContext) {
        mAppContext = appContext;
        mMovieItems = new ArrayList<MovieItem>();



        // store default when MovieLibrary Singleton is created.
        mSearchPreference = appContext.getString(R.string.pref_sort_order_default);


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


    public void clearMovies(){
        mMovieItems.clear();
        Log.i(LOG_TAG, "mMovieItems cleared; ");
    }


    public ArrayList<MovieItem> getMovies(){
        return mMovieItems;
    }

//    public long getMovieCount(){
//        return mMovieItems.size();
//    }


    public void restoreMovieLibrary(ArrayList<MovieItem> movieItems) {


        if (movieItems != null) {
            if (mMovieItems != null) {
                mMovieItems = movieItems;
                Log.i(LOG_TAG, "restoreMovieLibrary");
            }
        }
    }



//
//    public MovieItem getMovieItem(int id){
//
//        String tmpID = Integer.toString(id);
//
//        this.getMovieItem(tmpID);
//
//        return null;
//    }



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


    public MovieItem getMovieItem(int pos) {

        return mMovieItems.get(pos);

    }



    public void addMovieItem(MovieItem m) {

        if (mMovieItems == null) {
            mMovieItems = new ArrayList<MovieItem>();
        }

        mMovieItems.add(m);
    }

    public void addMovieTrailers(String id, ArrayList<MovieItem_Video> items) {

        if (id != null && items != null) {

            MovieItem movieItem = getMovieItem(id);

            if(movieItem != null){

                movieItem.setMovieItem_videos(items);


            }

        }

    }

    public void addMovieReviews(String id, ArrayList<MovieItem_Reviews> items) {

        if (id != null && items != null) {

            MovieItem movieItem = getMovieItem(id);

            if(movieItem != null){

                movieItem.setMovieReviews(items);


            }

        }

    }


    public void setSearchPreference(String searchPreference) {


        if (searchPreference != null) {
            mSearchPreference = searchPreference;


            Log.i(LOG_TAG, "mSearchPreference updated to " + searchPreference);
        }

    }




    // test - will combine this with getMovies
//    public ArrayList<MovieItem> getHighRatedMovies(){
//
//
//    }

    /*
        remove from mMovieItems

        input: sortPreferenceString, newListOfIDs

        find oldList associated with sortPreferenceString
        oldList. removeAll(Collection<?> c)
        oldList. all other lists in mSortOrder
        for every item left in oldList remove id from mMovieItems

        mSortOrder(sortPreferenceString to newListOfIDs)


     */




    /*
        Has dataset been captured before?

     */


    /*
        Update dataset

     */



    /*
        input:

        return: (boolean)
            true
            - if movieLibrary is null or empty
            - if a movieLibrary requires an update (i.e. MovieDatabase API request)

            false
            - if data currently in movieLibrary is sufficient


     */
    public boolean movieLibraryNeedsToBeUpdated(String sortPreference){


        if (mMovieItems != null) {

            // If there is a sort preference change AND mMovieItems already contain items
            // then there is no need to update MovieLibrary
            if (mSearchPreference.equalsIgnoreCase(sortPreference) && mMovieItems.size() > 0) {

                return false;
            }

        }

        return true;

    }


    //
    // //stackoverflow.com/questions/14475556/how-to-sort-arraylist-of-objects
//    private class MovieComparator implements Comparator<MovieItem> {
//
//        @Override
//        public int compare(MovieItem m1, MovieItem m2) {
//            if (m1.getmVoteAvg() > m2.getmVoteAvg()) {
//                return -1;
//            } else if (m1.getmVoteAvg() < m2.getmVoteAvg()) {
//                return 1;
//            }
//            return 0;
//
//        }
//
//    }



}

