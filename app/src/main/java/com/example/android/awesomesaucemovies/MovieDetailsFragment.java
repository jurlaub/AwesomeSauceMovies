package com.example.android.awesomesaucemovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by dev on 10/7/15.
 */

    /*
    favorite listener


    Moving on to the CheckBox, get a reference and set a listener that will update the mSolved field of the Crime. Listing 8.7    
    Listening for CheckBox changes (CrimeFragment.java) ...

    mDateButton = (Button) v.findViewById( R.id.crime_date);
    mDateButton.setText( mCrime.getDate(). toString());
    mDateButton.setEnabled( false);
    mSolvedCheckBox = (CheckBox) v.findViewById( R.id.crime_solved);
     mSolvedCheckBox.setOnCheckedChangeListener( new OnCheckedChangeListener() {

     public void onCheckedChanged( CompoundButton buttonView, boolean isChecked) {
     // Set the crime's solved property
     mCrime.setSolved( isChecked);
     } });
     return v;

Hardy, Brian; Phillips, Bill (2013-04-09). Android Programming: The Big Nerd Ranch Guide (Big Nerd Ranch Guides)
(Kindle Locations 3683-3693). Pearson Education. Kindle Edition.


     */


public class MovieDetailsFragment extends Fragment {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private MovieLibrary sMovieLibrary;
    ListView mDetailView;


    public MovieDetailsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sMovieLibrary = MovieLibrary.get(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get intent payload
        MovieDetails myActivity = (MovieDetails) getActivity();
        Intent intent = myActivity.getIntent();
        String movieID = intent.getStringExtra(MovieFragment.EXTRA_MESSAGE);


        View rootView = inflater.inflate(R.layout.activity_moviedetails, container, false);

        mDetailView = (ListView) rootView.findViewById(R.id.moviedetails_container);
        setupDetailAdapter(movieID);


        //View rootView = inflater.inflate(R.layout.list_movie_details, container, false);


        //final String API_KEY = intent.getStringExtra(MovieFragment.EXTRA_KEY);  //// See MovieFragment Note: Passing API Key

        //updateTrailers(movieID);


//        // MovieItem store all detailed movie data
//        MovieItem movieItem = MovieLibrary.get(myActivity.getApplicationContext()).getMovieItem(movieID);
//
//
//        TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
//        title.setText(movieItem.getmTitle());
//
//        TextView overview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
//        overview.setText(movieItem.getmOverview());
//
//        ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
//
//        Picasso.with(getActivity()).load(movieItem.getPosterPathURL(API_KEY)).into(imageView);
//
//        TextView popularity = (TextView) rootView.findViewById(R.id.movie_popularity);
//        popularity.setText(movieItem.getmPopularity().toString());
//
//        TextView averageVote = (TextView) rootView.findViewById(R.id.movie_average_vote);
//        averageVote.setText(movieItem.getmVoteAvg().toString());
//
//        TextView releaseDate = (TextView) rootView.findViewById(R.id.movie_release_year);
//        releaseDate.setText(movieItem.getmReleaseDate());


        return rootView;

    }

    void setupDetailAdapter(String id) {
        if (getActivity() == null || mDetailView == null) return;

        ArrayList mMovieElements = sMovieLibrary.getMovieItemsDetailElements(id);


        if (mDetailView != null) {

            mDetailView.setAdapter(new MovieDetailsAdapter(getActivity(), mMovieElements));

        } else {
            mDetailView.setAdapter(null);
        }
    }


}
