package com.example.android.awesomesaucemovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by dev on 7/31/15.
 */
public class MovieDetails extends ActionBarActivity {

    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";

    //protected MovieLibrary sMovieLibrary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);

        //sMovieLibrary = MovieLibrary.get(getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.moviedetails_container, new MovieDetailsFragment())
                    .commit();
        }


    }


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


    public static class MovieDetailsFragment extends Fragment {

        private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

        public MovieDetailsFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);

            //get intent payload
            MovieDetails myActivity = (MovieDetails) getActivity();
            Intent intent = myActivity.getIntent();
            String movieID = intent.getStringExtra(MovieFragment.EXTRA_MESSAGE);
            final String API_KEY = intent.getStringExtra(MovieFragment.EXTRA_KEY);  //// See MovieFragment Note: Passing API Key

            //updateTrailers(movieID);


            // MovieItem store all detailed movie data
            MovieItem movieItem = MovieLibrary.get(myActivity.getApplicationContext()).getMovieItem(movieID);


            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            title.setText(movieItem.getmTitle());

            TextView overview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            overview.setText(movieItem.getmOverview());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

            Picasso.with(getActivity()).load(movieItem.getPosterPathURL(API_KEY)).into(imageView);

            TextView popularity = (TextView) rootView.findViewById(R.id.movie_popularity);
            popularity.setText(movieItem.getmPopularity().toString());

            TextView averageVote = (TextView) rootView.findViewById(R.id.movie_average_vote);
            averageVote.setText(movieItem.getmVoteAvg().toString());

            TextView releaseDate = (TextView) rootView.findViewById(R.id.movie_release_year);
            releaseDate.setText(movieItem.getmReleaseDate());


            return rootView;

        }


    }

}