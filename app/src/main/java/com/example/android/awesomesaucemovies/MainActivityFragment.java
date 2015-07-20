package com.example.android.awesomesaucemovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] dList = {"pepper", "Die Hard", "Right Round", "Holder", "Flo Rida", "Shawtie"};

        mMovieAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.list_item_movie, R.id.list_item_movie_textview );

        for(String s: dList) {
            mMovieAdapter.add(s);
        }


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }
}
