package com.example.android.awesomesaucemovies;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by dev on 7/31/15.
 */
public class MovieDetails extends ActionBarActivity {

    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";

    //protected MovieLibrary sMovieLibrary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //sMovieLibrary = MovieLibrary.get(getApplicationContext());

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.moviedetails_container, new MovieDetailsFragment())
//                    .commit();
//        }


    }



}