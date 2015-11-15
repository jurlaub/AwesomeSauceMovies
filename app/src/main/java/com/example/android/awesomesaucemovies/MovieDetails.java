package com.example.android.awesomesaucemovies;


import android.support.v4.app.Fragment;

/**
 * Created by dev on 7/31/15.
 */

public class MovieDetails extends SingleFragmentActivity {


    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";




    @Override
    protected Fragment createFragment(){
        return new MovieDetailsFragment();

    }


}
