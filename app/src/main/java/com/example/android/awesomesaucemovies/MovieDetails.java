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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.moviedetails_container, new MovieDetailsFragment())
                    .commit();
        }



    }




    public static class MovieDetailsFragment extends Fragment {

        private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

        public MovieDetailsFragment() {

        }

        
        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,
                                 Bundle savedInstanceState ) {

            View rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);

            //get intent payload
            MovieDetails myActivity = (MovieDetails) getActivity();
            Intent intent = myActivity.getIntent();
            String movieID = intent.getStringExtra(MovieFragment.EXTRA_MESSAGE);

            MovieItem movieItem = MovieLibrary.get(myActivity.getApplicationContext()).getMovieItem(movieID);


            TextView title = (TextView) rootView.findViewById(R.id.movie_detail_title);
            title.setText(movieItem.getmTitle());

            TextView overview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            overview.setText(movieItem.getmOverview());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);

            Picasso.with(getActivity()).load(movieItem.getmURL()).into(imageView);

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
