package com.example.android.awesomesaucemovies;


import android.support.v4.app.Fragment;

/**
 * Created by dev on 7/31/15.
 */

public class MovieDetails extends SingleFragmentActivity {

//    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";

    //protected MovieLibrary sMovieLibrary;


    @Override
    protected Fragment createFragment(){
        return new MovieDetailsFragment();

    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_details);
//
//        //sMovieLibrary = MovieLibrary.get(getApplicationContext());
//
////        if (savedInstanceState == null) {
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.moviedetails_container, new MovieDetailsFragment())
////                    .commit();
////        }
//
//
//    }



}
