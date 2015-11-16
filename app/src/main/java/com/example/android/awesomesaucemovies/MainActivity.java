package com.example.android.awesomesaucemovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity implements MovieFragment.Callbacks {

    private static final String MOVIEDETAILFRAGMENT = "MDF_Tag";

    private boolean mTwoPane;

    public void onMovieDetailSelected(Cursor movieDetailCursor) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_masterdetail);

        if(findViewById(R.id.fragment_detail) != null){
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, new MovieDetailsFragment(), MOVIEDETAILFRAGMENT )
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
