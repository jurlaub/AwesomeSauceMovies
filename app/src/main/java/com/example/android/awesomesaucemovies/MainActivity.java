package com.example.android.awesomesaucemovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callback {

    private static final String MOVIEDETAILFRAGMENT = "MDF_Tag";

    private boolean mTwoPane;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_masterdetail);

        if(findViewById(R.id.fragment_detail) != null){
            mTwoPane = true;

            MovieDetailsFragment initialDetailFragment = new MovieDetailsFragment();
            Bundle arguments = new Bundle();


            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, initialDetailFragment, MOVIEDETAILFRAGMENT )
                        .commit();
            }

        } else {
            mTwoPane = false;

        }


//        MovieFragment movieFragment = ((MovieFragment) getSupportFragmentManager()
//            .findFragmentById(R.id.fragmentContainer));
        //movieFragment.setUseTodayLayout(!mTwoPane);



//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
//
//        if (fragment == null) {
//            fragment = createFragment();
//            fm.beginTransaction()
//                    .add(R.id.fragmentContainer, fragment)
//                    .commit();
//        }
    }




    @Override
    public void onItemSelected(Uri uri) {
        Log.v("MainActivity", "uri: " + uri);

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


    //    @Override
//    protected Fragment createFragment(){
//        return new MovieFragment();
//    }



//    @Override
//    protected int getLayoutResID(){
//
//        return R.layout.activity_masterdetail;
//    }


}
