package com.example.android.awesomesaucemovies;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by dev on 7/31/15.
 */

public class MovieDetails extends ActionBarActivity {


    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
//
//            // create detail fragment and add it to th activity
//
//            //Bundle arguments = new Bundle();
//        Bundle arguments = new Bundle();
//        arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
//
//        DetailFragment fragment = new DetailFragment();
//        fragment.setArguments(arguments);
//
//
//        }

            FragmentManager fm = getSupportFragmentManager();
           // Fragment fragment = fm.findFragmentById(R.id.fragment_detail);


            MovieDetailsFragment fragment = new MovieDetailsFragment();
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
