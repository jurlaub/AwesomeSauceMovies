package com.example.android.awesomesaucemovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callback {

    private static final String MOVIEDETAILFRAGMENT = "MDF_Tag";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_masterdetail);
        Log.v(LOG_TAG, "layout set");

        if(findViewById(R.id.fragment_detail) != null){

            Log.v(LOG_TAG, "twopane view");
            mTwoPane = true;

            if (savedInstanceState == null) {
                setDetailPane();
            }


        } else {
            mTwoPane = false;

        }


    }



    // sets the Detail Pane to the Empty State view by having No data in the bundle.
    private void setDetailPane() {


        if (mTwoPane) {
            Log.v(LOG_TAG, "resetting Detail Pane");
            MovieDetailsFragment initialDetailFragment = new MovieDetailsFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, initialDetailFragment, MOVIEDETAILFRAGMENT )
                    .commit();


        } else {
            Log.v(LOG_TAG, "mTwoPane: " + mTwoPane);
        }

    }



    @Override
    public void onItemSelected(Uri uri) {
        Log.v(LOG_TAG, "uri: " + uri);

        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailsFragment.DETAIL_URI, uri);

            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, detailsFragment, MOVIEDETAILFRAGMENT )
                    .commit();


        } else {

            Intent movieDetailIntent = new Intent(this, MovieDetails.class)
                            .setData(uri);
            startActivity(movieDetailIntent);

        }


    }


    @Override
    public void resetTwoPane(){
        // set detail pane to the empty state
        setDetailPane();

    }






}
