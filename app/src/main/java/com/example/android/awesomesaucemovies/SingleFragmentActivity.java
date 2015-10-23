package com.example.android.awesomesaucemovies;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;
import android.app.FragmentManager;



/**
 * Created by dev on 10/23/15.
 *
 * follows example provided by Android Programming the Big Nerd Ranch Guide
 */
public abstract class SingleFragmentActivity extends Activity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

}
