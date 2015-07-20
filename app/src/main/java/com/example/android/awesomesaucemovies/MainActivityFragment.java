package com.example.android.awesomesaucemovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;


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


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CharSequence text = mMovieAdapter.getItem(position);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(v.getContext(), text, duration ).show();
            }


        });

        return rootView;
    }
}
