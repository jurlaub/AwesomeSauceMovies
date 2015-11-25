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


    //--------------------- SQLite Query requests --------------------------------------
    private static final String[] MOVIEDETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_FAVORITE
        };
    // ---!!-- must change if MOVIEDETAIL_COLUMNS Changes ---!!---
    static final int COL_DETAIL_ID = 0;
    static final int COL_DETAIL_TITLE = 1;
    static final int COL_DETAIL_POSTER_PATH = 2;
    static final int COL_DETAIL_VOTE_AVG = 3;
    static final int COL_DETAIL_POPULARITY = 4;
    static final int COL_DETAIL_RELEASE_DATE = 5;
    static final int COL_DETAIL_OVERVIEW = 6;
    static final int COL_DETAIL_FAVORITE = 7;




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

    //--------------------------------------------------------------------------





    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private MovieDetailsAdapter mMovieDetailAdapter;
    private ShareActionProvider mShareActionProvider;
    ListView mDetailView;

    MergeCursor mDetailCursor;

    private boolean mIsEmptyState;




    public MovieDetailsFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mIsEmptyState = false;

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflates actionabar menu

        if (!mIsEmptyState) {
            Log.v(LOG_TAG, "mIsEmptyState: " + mIsEmptyState + " inflating the shareActionProvider");
            inflater.inflate(R.menu.menu_detail_share, menu);


            MenuItem menuItem = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (mShareActionProvider != null) {
                Log.v(LOG_TAG, "ShareActionProvider not null: " + mShareActionProvider.toString());
                mShareActionProvider.setShareIntent(createShareTrailerIntent());

            }

        } else {
            Log.v(LOG_TAG, "onCreateOptionsMenu() mIsEmptyState: " + mIsEmptyState);
        }




    }



    @Override
    public void onStop(){
        super.onStop();
        Log.v(LOG_TAG, "onStop");
//        Cursor c;
//
//
//        if (!mDetailCursor.isClosed()) {
//            mDetailCursor.close();
//            Log.v(LOG_TAG, "onStop mDetailCursor closed");
//        }
//
//
//        // close cursor if not already closed
//        c = mMovieDetailAdapter.getCursor();
//        if (!c.isClosed()) {
//            c.close();
//            Log.v(LOG_TAG, "onDestroy, closing Cursor");
//        }

    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        Log.v(LOG_TAG, "onDestroy");
//
//
//
//
//    }
//
//    @Override
//    public void onPause(){
//        super.onPause();
//        Log.v(LOG_TAG, "onPause");
//
//
//
//    }



    private Intent createShareTrailerIntent(){
        int elementPosition = 1;
        String trailerNotAvailable = "";


        int initialPos = 0;

        try {

            initialPos = mDetailCursor.getPosition();


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


        } finally {

            // return cursor to initial position (may not be necessary)
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

        View rootView;

        Uri movieUri;
        String movieID;
        Bundle arguments = getArguments();
//
        if (arguments != null) {

            movieUri = arguments.getParcelable(DETAIL_URI);
            Log.v(LOG_TAG, "argument has a uri: " + movieUri);


            movieID = MovieContract.MovieEntry.getMovieIDFromUri(movieUri);
            Log.v(LOG_TAG, "onCreateView, movieID: " + movieID);

            updateTrailers(movieID); // request Trailer web data
            updateReviews(movieID); // request Review web data


            rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);

            mDetailView = (ListView) rootView.findViewById(R.id.moviedetails_container);

            setupDetailAdapter(movieID);

        } else {

            rootView = inflater.inflate(R.layout.empty_state, container, false);
            mIsEmptyState = true;

            Log.v(LOG_TAG, "onCreateView - showing EmptyState layout. mIsEmptyState:" + mIsEmptyState);

        }




        return rootView;

    }

    void setupDetailAdapter(String movieID) {
        if (getActivity() == null || mDetailView == null) {
            Log.v("setupDetailAdapter", "In null Zone");
            return;
        }


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


        // check for content and use to set the adapter
        if (mDetailCursor != null && mDetailCursor.getCount() > 0) {
            mDetailCursor.moveToFirst();

            Log.v(LOG_TAG, "MovieDetail Cursor is not null - setting up new adapter");

            mMovieDetailAdapter = new MovieDetailsAdapter(getActivity(), mDetailCursor, 0);
            mDetailView.setAdapter(mMovieDetailAdapter);


        } else {
        //no data available, so set adapter to null

            Log.v(LOG_TAG, "MovieDetail Cursor is null = no adapter");
            mDetailView.setAdapter(null);

        }


    }

    // Refactor
    public void updateTrailers(String movieID){

        // checks for network connection, if present requests trailer information be updated
        if (MovieFetcher.networkIsAvailable(getActivity())) {

            // this tells to request trailer information - only unique element
            String[] vals = {movieID, MovieDetails.TRAILERS};

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask(getActivity());
            movieTask.execute(vals);


        } else {
        // message user if no network connection available.

            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateTrailers()", "No network connectivity detected.");

        }


    }

    // Refactor
    public void updateReviews(String movieID) {

        // checks for network connection, if present requests review information be updated
        if (MovieFetcher.networkIsAvailable(getActivity())) {

            // This tells to request Review information  - only unique element
            String[] vals = {movieID, MovieDetails.REVIEWS};

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask(getActivity());
            movieTask.execute(vals);


        } else {
        // message user if no network connection available.

            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateReviews()", "No network connectivity detected.");

        }

    }


    private class FetchMovieDetailsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

        private String movieID;
        private String request;
        private Context context;

        public FetchMovieDetailsTask(Context context){
            this.context = context;
        }





        @Override
        protected Void doInBackground(String... urls) {


            movieID = urls[0];
            request = urls[1];

            switch(request) {
                case MovieDetails.TRAILERS:
                    Log.i(LOG_TAG, "match trailers: " + request);
                    new MovieFetcher().fetchMovieTrailers(movieID,  context);
                    break;

                case MovieDetails.REVIEWS:
                    Log.i(LOG_TAG, "match reviews: " + request);
                    new MovieFetcher().fetchMovieReviews(movieID, context);

                    break;

                default:
                    throw new UnsupportedOperationException("FetchMovieDetailsTask: Unknown value : " + request);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void v) {

            // trigger the adapter to use Detail Cursor information
            setupDetailAdapter(movieID);

            // set up the share action provider after any trailer information is obtained
            if (mShareActionProvider != null) {
                Log.v(LOG_TAG, "ShareActionProvider not null: " + mShareActionProvider.toString());
                mShareActionProvider.setShareIntent(createShareTrailerIntent());
            }


        }

    }



}
