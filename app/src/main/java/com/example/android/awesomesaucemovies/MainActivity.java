package com.example.android.awesomesaucemovies;

import android.support.v4.app.Fragment;


public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new MovieFragment();
    }



    @Override
    protected int getLayoutResID(){

        return R.layout.activity_masterdetail;
    }





}
