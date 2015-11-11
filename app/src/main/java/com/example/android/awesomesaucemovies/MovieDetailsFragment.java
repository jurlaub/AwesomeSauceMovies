package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MergeCursor;
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

    private static final String[] MOVIEDETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW
        };
    // ---!!-- must change if MOVIEDETAIL_COLUMNS Changes ---!!---
    static final int COL_DETAIL_ID = 0;
    static final int COL_DETAIL_TITLE = 1;
    static final int COL_DETAIL_POSTER_PATH = 2;
    static final int COL_DETAIL_VOTE_AVG = 3;
    static final int COL_DETAIL_POPULARITY = 4;
    static final int COL_DETAIL_RELEASE_DATE = 5;
    static final int COL_DETAIL_OVERVIEW = 6;

    private static final String[] MOVIETRAILER_COLUMNS = {
            MovieContract.MovieTrailers.COLUMN_TRAILER_KEY,
            MovieContract.MovieTrailers.COLUMN_TRAILER_NAME, // title
            MovieContract.MovieTrailers.COLUMN_TRAILER_URI
    };

    // ---!!-- must change if MOVIETRAILER_COLUMNS Changes ---!!---
    static final int COL_TRAILER_ID = 0;
    static final int COL_TRAILER_TITLE = 1;
    static final int COL_TRAILER_URI = 2;

    // ---!!-- must change if MOVIEDETAIL_COLUMNS Changes ---!!---
    private static final String[] MOVIEREVIEWS_COLUMNS = {
            MovieContract.MovieReviews.COLUMN_REVIEW_KEY,
            MovieContract.MovieReviews.COLUMN_REVIEW_AUTHOR,
            MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT
    };

    // ---!!-- must change if MOVIEREVIEWS_COLUMNS Changes ---!!---
    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;


    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private MovieLibrary sMovieLibrary;
    private MovieDetailsAdapter mMovieDetailAdapter;
    private ShareActionProvider mShareActionProvider;
    ListView mDetailView;
    //ArrayList mMovieElements;   // includes Detail View, any Trailers, any Reviews

    MergeCursor mDetailCursor;




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
//            int duration = Toast.LENGTH_LONG;
//            Toast.makeText(getActivity(), " testing shareActionProvider", duration).show();
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

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (mDetailCursor != null) {
            mDetailCursor.close();
            Log.v(LOG_TAG, "mDetailCursor closed");
        }



    }



    private Intent createShareTrailerIntent(){
        int elementPosition = 1;
        String trailerNotAvailable = "";

        // try/catch structure would be more appropriate
        int initialPos = 0;
        Log.v(LOG_TAG, "initialPos = " + initialPos);

        try {

            initialPos = mDetailCursor.getPosition();

            Log.v(LOG_TAG, "mDetailCursor at " + initialPos);

            if(mDetailCursor.moveToPosition(elementPosition)) {


                String trailerUriSegment = mDetailCursor.getString(COL_TRAILER_URI);

                Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + trailerUriSegment);
                Log.v(LOG_TAG, "Trailer Segment may not be a valid uri: " + uri );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());

                return shareIntent;
            }

        } catch (CursorIndexOutOfBoundsException e) {
            Log.e(LOG_TAG, "mDetailCursor " + e);
            trailerNotAvailable = elementPosition + " out of cursor index";
        } catch (Exception e) {
            Log.e(LOG_TAG, "mDetailCursor " + e);
            trailerNotAvailable = "Unexpected Type - Expected a Exception";

        }
        finally {
            if(mDetailCursor.moveToPosition(initialPos)) {
                Log.v(LOG_TAG, "mDetailCursor returned to initial position " + initialPos);
            } else {
                Log.v(LOG_TAG, "mDetailCursor at" + mDetailCursor.getPosition() + "and did not return to initial position " + initialPos);
            }
        }

        if (!trailerNotAvailable.contentEquals("")) {
            // toast displaying that the String was not available
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), trailerNotAvailable, duration).show();


        }



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

    void setupDetailAdapter(String movieID) {
        if (getActivity() == null || mDetailView == null) {
            Log.v("setupDetailAdapter", "In null Zone");
            return;
        }

//        mMovieElements = sMovieLibrary.getMovieItemsDetailElements(id);
//
//        Log.v("setupDetailAdapter", "ArrayList count:" + mMovieElements.size());
//
//        if (mDetailView != null) {
//
//            mMovieDetailAdapter = new MovieDetailsAdapter(getActivity(), mMovieElements);
//
//            mDetailView.setAdapter(mMovieDetailAdapter);
//
//            Log.v("setupDetailAdapter", "Adapter Set count:" + mMovieElements.size());
//
//
//        } else {
//            mDetailView.setAdapter(null);
//            Log.v("setupDetailAdapter", "adapter null " );
//
//        }

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(movieID);
        Uri trailerUri = MovieContract.MovieTrailers.buildMovieTrailersUri(movieID);
        Uri reviewUri = MovieContract.MovieReviews.buildMovieReviewsUri(movieID);

        // cursor for movie details
        Cursor movieCursor = getActivity().getContentResolver().query(movieUri,
                MOVIEDETAIL_COLUMNS,
                null,
                null,
                null);

        // cursor for trailers
        Cursor trailerCursor = getActivity().getContentResolver().query(trailerUri,
                MOVIETRAILER_COLUMNS,
                null,
                null,
                null);
        // cursor for reviews
        Cursor reviewCursor = getActivity().getContentResolver().query(reviewUri,
                MOVIEREVIEWS_COLUMNS,
                null,
                null,
                null);

        // mergeCursor
        Cursor[] tmpCursor = new Cursor[] {movieCursor, trailerCursor, reviewCursor};
        mDetailCursor = new MergeCursor(tmpCursor);
        Log.v(LOG_TAG, "mDetailCursor length = " + mDetailCursor.getCount() + "mDetailCursor at position: " +mDetailCursor.getPosition());

        mDetailCursor.moveToFirst();
        Log.v(LOG_TAG,  "mDetailCursor at position: " + mDetailCursor.getPosition());


        if (mDetailCursor.getCount() > 0) {
            Log.v(LOG_TAG, "MovieDetail Cursor is not null - setting up new adapter");

            mMovieDetailAdapter = new MovieDetailsAdapter(getActivity(), mDetailCursor, 0);
            mDetailView.setAdapter(mMovieDetailAdapter);
        } else {
            Log.v(LOG_TAG, "MovieDetail Cursor is null = no adapter");
            mDetailView.setAdapter(null);
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
