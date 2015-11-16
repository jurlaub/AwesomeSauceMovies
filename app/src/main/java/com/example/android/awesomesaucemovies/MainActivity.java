package com.example.android.awesomesaucemovies;

import android.database.Cursor;
import android.support.v4.app.Fragment;


public class MainActivity extends SingleFragmentActivity implements MovieFragment.Callbacks {

    @Override
    protected Fragment createFragment(){
        return new MovieFragment();
    }



    @Override
    protected int getLayoutResID(){

        return R.layout.activity_masterdetail;
    }


    public void onMovieDetailSelected(Cursor movieDetailCursor) {

    }



}
