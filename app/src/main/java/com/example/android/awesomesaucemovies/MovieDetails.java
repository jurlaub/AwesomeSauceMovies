package com.example.android.awesomesaucemovies;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by dev on 7/31/15.
 */

public class MovieDetails extends ActionBarActivity {

    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Log.v(LOG_TAG, "MovieDetails - activity");

        if (savedInstanceState == null) {
//
//            // create detail fragment and add it to th activity
//
//            //Bundle arguments = new Bundle();
        Bundle arguments = new Bundle();
        arguments.putParcelable(MovieDetailsFragment.DETAIL_URI, getIntent().getData());
//
//        DetailFragment fragment = new DetailFragment();
//        fragment.setArguments(arguments);
//
//
//        }
            Log.v(LOG_TAG, "savedInstanceState is null. Bundle: " + arguments.toString());


            FragmentManager fm = getSupportFragmentManager();
           // Fragment fragment = fm.findFragmentById(R.id.fragment_detail);


            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

                fm.beginTransaction()
                        .add(R.id.fragment_detail, fragment)
                        .commit();

        }
    }



//
//    @Override
//    protected Fragment createFragment(){
//        return new MovieDetailsFragment();
//
//    }
//
//    @Override
//    protected int getLayoutResID(){
//
//        return R.layout.activity_details;
//    }
//

}
