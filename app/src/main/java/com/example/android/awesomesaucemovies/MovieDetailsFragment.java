package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.awesomesaucemovies.data.MovieContract;

import java.util.ArrayList;

//import android.support.v4.view.MenuItemCompat;

//import android.support.v4.view.MenuItemCompat;

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
    private MovieDetailsAdapter mMovieDetailAdapter;
    private ShareActionProvider mShareActionProvider;
    ListView mDetailView;
    ArrayList mMovieElements;   // includes Detail View, any Trailers, any Reviews


    public MovieDetailsFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        sMovieLibrary = MovieLibrary.get(getActivity());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflates actionabar menu
        inflater.inflate(R.menu.menu_detail_share, menu);
        //getMenuInflater().inflate(R.menu.menu_movie_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            Log.v(LOG_TAG, "ShareActionProvider not null: " + mShareActionProvider.toString());
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
//        else {
//            Log.d(LOG_TAG, "Share Action Provider is null?");
//        }


    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch(item.getItemId()) {
//            case R.id.action_share:
//
//                mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//
//                if (mShareActionProvider != null) {
//                    Log.v(LOG_TAG, "ShareActionProvider not null: " + mShareActionProvider.toString());
//                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
//                }
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }




    private Intent createShareTrailerIntent(){
        int elementPosition = 1;
        String trailerNotAvailable = "";

        // try/catch structure would be more appropriate

        if (mMovieElements.size() > 1) {

            // position 1 should be the first trailer entry
            if(mMovieElements.get(elementPosition).getClass() == MovieItem_Video.class ) {
                Log.v(LOG_TAG, "MovieElementget[1].Class ShareTrailerIntent");

                MovieItem_Video itemVideo = (MovieItem_Video) mMovieElements.get(elementPosition);
                Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + itemVideo.getVid_key());

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());


//                if(shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivity(shareIntent);
//                }


                return shareIntent;

            } else {
                // element 1 is not a trailer class - no trailers?
                trailerNotAvailable = "Unexpected Type - Expected a Trailer Object";
            }

        } else {
            // size is not greater than 1.
            trailerNotAvailable = "Elements were not larger then 1";
        }



        // toast displaying that the String was not available
        int duration = Toast.LENGTH_LONG;
        Toast.makeText(getActivity(), trailerNotAvailable, duration).show();

        return null;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get intent payload
        MovieDetails myActivity = (MovieDetails) getActivity();
        Intent intent = myActivity.getIntent();
        String movieID = intent.getStringExtra(MovieFragment.EXTRA_MESSAGE);

        updateTrailers(movieID);
        updateReviews(movieID);


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
        if (getActivity() == null || mDetailView == null) {
            Log.v("setupDetailAdapter", "In null Zone");
            return;
        }

        mMovieElements = sMovieLibrary.getMovieItemsDetailElements(id);

        Log.v("setupDetailAdapter", "ArrayList count:" + mMovieElements.size());

        if (mDetailView != null) {

            mMovieDetailAdapter = new MovieDetailsAdapter(getActivity(), mMovieElements);

            mDetailView.setAdapter(mMovieDetailAdapter);

            Log.v("setupDetailAdapter", "Adapter Set count:" + mMovieElements.size());


        } else {
            mDetailView.setAdapter(null);
            Log.v("setupDetailAdapter", "adapter null " );

        }
    }


    public void updateTrailers(String movieID){

        if (MovieFetcher.networkIsAvailable(getActivity())) {
            String[] vals = {movieID, MovieDetails.TRAILERS};

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask(getActivity());
            movieTask.execute(vals);

        } else {

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateMovie()", "No network connectivity detected.");

        }


    }

    public void updateReviews(String movieID) {

        if (MovieFetcher.networkIsAvailable(getActivity())) {
            String[] vals = {movieID, MovieDetails.REVIEWS};

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask(getActivity());
            movieTask.execute(vals);

        } else {

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateMovie()", "No network connectivity detected.");

        }

    }


    public class FetchMovieDetailsTask extends AsyncTask<String, Void, ArrayList> {

        private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

        private String movieID;
        private String request;
        private Context context;

        public FetchMovieDetailsTask(Context context){
            this.context = context;
        }





        @Override
        protected ArrayList doInBackground(String... urls) {

            ArrayList mItems = new ArrayList();

            movieID = urls[0];
            request = urls[1];

            switch(request) {
                case MovieDetails.TRAILERS:
                    Log.i(LOG_TAG, "match trailers: " + request);
                    mItems = new MovieFetcher().fetchMovieTrailers(movieID,  context);
                    break;

                case MovieDetails.REVIEWS:
                    Log.i(LOG_TAG, "match reviews: " + request);
                    mItems = new MovieFetcher().fetchMovieReviews(movieID, context);

                    break;

                default:
                    throw new UnsupportedOperationException("FetchMovieDetailsTask: Unknown value : " + request);
            }

            return mItems;

        }

        @Override
        protected void onPostExecute(ArrayList items) {

            if (items != null){
                Log.v("onPostExecute", " here at last! " + items.toString());

                switch (request) {
                    case MovieDetails.TRAILERS:
                        sMovieLibrary.addMovieTrailers(movieID, items);
                        Log.v("FetchMovieDetailsTask", "in onPostExecute trailers: " + request);
                        Log.v("FetchMovieDetailsTask", "ID ::: " + getView().toString());

//                        if(mMovieDetailAdapter != null){
//                            mMovieDetailAdapter.notifyDataSetChanged();
//                        } else {
//
//                        }


                        if (items.size() > 0) {
                            int locNum = 0;
                            Uri movieUri = MovieContract.MovieTrailers.buildMovieTrailersUri(movieID);

                            Cursor tmpVal = getActivity().getContentResolver().query(movieUri, null, null, null, null);

                            if (tmpVal != null) {

                                Log.v(LOG_TAG, " Trailers query returned " + tmpVal.getCount() + " :: maybe 0 to n trailers");
                                Log.v(LOG_TAG, tmpVal.toString());

                            } else {
                                Log.v(LOG_TAG, "query returned a null value ");


                            }


                            tmpVal.close();

                        }


                        break;

                    case MovieDetails.REVIEWS:
                        sMovieLibrary.addMovieReviews(movieID, items);
                        Log.v("FetchMovieDetailsTask", "in onPostExecute reviews: " + request);
                        break;

                    default:
                        throw new UnsupportedOperationException("onPostExecute: Unknown value : " + request);

                }

                setupDetailAdapter(movieID);


                if (mShareActionProvider != null) {
                    Log.v(LOG_TAG, "ShareActionProvider not null: " +mShareActionProvider.toString());
                    mShareActionProvider.setShareIntent(createShareTrailerIntent());
                }



            }
        }

    }



}
