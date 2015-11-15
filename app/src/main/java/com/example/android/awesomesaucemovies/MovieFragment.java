package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.awesomesaucemovies.data.MovieContract;


/**
 * A placeholder fragment containing a simple view.
 *

 *
 */
public class MovieFragment extends Fragment {

    public final static String EXTRA_MESSAGE = MovieFragment.class.getCanonicalName();


    private final String LOG_TAG = MovieFragment.class.getSimpleName();



    //--------------------- SQLite Query requests --------------------------------------
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    //  -- must change if MOVIE_COLUMNS Changes  --
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;

    private static final String MOVIE_ORDER = MovieContract.MovieEntry.COLUMN_NORMAL_RANK + " ASC";




    public static final String[] UPDATE_COLUMNS = {MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_FAVORITE};
    //  -- must change if UPDATE_COLUMNS Changes  --
    public static final int COL_UPDATE_MOVIE_ID = 0;
    public static final int COL_UPDATE_FAVORITE = 1;
    //-------------------------------------------------------------------------------



    private MovieAdapter mMovieAdapter;
    private Cursor mGridCursor;
    GridView mGridView;







    public MovieFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // allows the user to request an update from MovieDatabase - this overrides the MovieLibrary
        // rules related to downloading new data
        if (id == R.id.action_refresh) {
            Log.v(LOG_TAG, " Requesting updated Movie information.");

            updateMovie();  // request new content from API regardless of past setting




        } else if (id == R.id.action_settings) {
        // Allow the user to choose Sort Preference settings.
            Log.v(LOG_TAG, "Settings Activity Selected");

            startActivity(new Intent(getActivity(), SettingsActivity.class));

            return true;





        } else if (id == R.id.action_about_toast) {
        // show Movie Data Information Source
            Log.v(LOG_TAG, "About");

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), R.string.TMDb_notice, duration).show();

        }




        return super.onOptionsItemSelected(item);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        setupAdapter();



        // Display Movie Details when selected
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                Log.i(LOG_TAG, "in ItemClick, position: " + Integer.toString(position));


                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                    if (cursor != null) {
                        CharSequence text = cursor.getString(COL_MOVIE_ID);
                        Log.v(LOG_TAG, "onItemClick, text = '" + text + "'");

                        // See MovieFragment Note: Passing API Key
                        Intent movieDetailIntent = new Intent(getActivity(), MovieDetails.class)
                                .putExtra(EXTRA_MESSAGE, text);

                        startActivity(movieDetailIntent);
                    }


            }


        });

        return rootView;
    }



    // setupAdapter obtains the cursor information used in the adapter. If no information available
    // then adapter is set up as null.
    void setupAdapter() {
        if (getActivity() == null || mGridView == null) {
            Log.v(LOG_TAG, "setupAdapter: activity or mGridView is null");
            return;
        }

        //test for shared preferences
        String sharedPreference = obtainPreference();
        Uri movieUri;


        // decision for the data source to use Favorite data from Database OR to use the other Movie
        // Entries from the Database.
        switch (sharedPreference) {

            case "favorite":
                // obtain Favorite Movie data cursor (may be 'empty' if no MovieEntries marked Favorite.)
                movieUri = MovieContract.MovieFavorites.CONTENT_URI;
                mGridCursor = getActivity().getContentResolver().query(movieUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);

                break;


            default:
                // obtain the MovieEntry data that has a rank.
                movieUri = MovieContract.MovieEntry.CONTENT_URI;

                // obtain MovieEntries where the COLUMN_NORMAL_RANK not marked -1
                mGridCursor = getActivity().getContentResolver().query(movieUri,
                        MOVIE_COLUMNS,
                        MovieContract.MovieEntry.WHERE_NOT_RANKED_CLAUSE,
                        new String[] {Integer.toString(MovieContract.MovieEntry.VAL_OMIT_FROM_RANK)},
                        MOVIE_ORDER);


        }



            // if cursor values exist and have elements then set the adapter to show them
            if(mGridCursor != null && mGridCursor.getCount() > 0) {
                Log.v(LOG_TAG, "MovieEntry Cursor is not null - setting up new adapter; count is " + mGridCursor.getCount());

                mMovieAdapter = new MovieAdapter(getActivity(), mGridCursor, 0);
                mGridView.setAdapter(mMovieAdapter);

            } else {


                // notification if there are no favorites in db
                if (sharedPreference.equalsIgnoreCase(getString(R.string.pref_sort_order_favorite))) {

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.no_favorite_entries), duration);
                    toast.show();

                }


                mGridView.setAdapter(null);
                Log.v(LOG_TAG, "MovieEntry Cursor is null = no adapter");
            }



    }


    @Override
    public void onStart(){
        super.onStart();
        Log.v(LOG_TAG, "onStart ");

        // decision to use internal memory or request information from the internet
        libraryController();

    }

//    @Override
//    public void onResume(){
//        super.onResume();
//
//        Log.v(LOG_TAG, "onResume ");
//
//    }
//
//
//
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        Log.v(LOG_TAG, " onDestroy");
//
//
//
//
//    }
//
//    @Override
//    public void onPause(){
//        super.onPause();
//        Log.v(LOG_TAG, " onPause");
//
//
//    }


    @Override
    public void onStop(){
        super.onStop();
        Log.v(LOG_TAG, "onStop");

        Cursor c;

        // may need to move this to an earlier point.
        if(!mGridCursor.isClosed()){
            mGridCursor.close();
            Log.v(LOG_TAG, "onStop, closing Cursor");
        }


        c = mMovieAdapter.getCursor();
        if (!c.isClosed()) {
            c.close();
            Log.v(LOG_TAG, "onStop, closing Cursor");
        }
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        // store the sortPreference used to initiate the AsyncTask. The intent is to avoid a
        // conflict between a delay within the AsyncTask and the user updating preferences.
        private String preferenceUsedInRequest;
        private Context context;

        public FetchMovieTask(Context c){
            this.context = c;

        }




        @Override
        protected Void doInBackground(String... urls){

            // preference set by user
            preferenceUsedInRequest = urls[0];

            new MovieFetcher().fetchMovieItems(preferenceUsedInRequest, context);


            return null;

        }




        @Override
        protected void onPostExecute(Void v) {

            // trigger to use newly entered data obtained by the MovieFetcher
            setupAdapter();
        }
    }


    // returns the sort order preference set by the user (or default value)
    private String obtainPreference() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        Log.v(LOG_TAG, "Sort Order Preference: " + sortOrder);

        return  sortOrder;
    }


    /*
        This method checks for network connection and initiates the Task to obtain the web data
     */
    private void updateMovie() {

        // check for connectivity and obtain movie data from web
        if (MovieFetcher.networkIsAvailable(getActivity())) {

            String sortPreference = obtainPreference();
            Log.i(LOG_TAG + ".updateMovie()", "generating a new API request, sort preference: "  + sortPreference);

            Context context = getActivity();
            FetchMovieTask movieTask = new FetchMovieTask(context);

            // preference set by user sent to the FetchMovieTask. The MovieFetcher().fetchMovieItems
            // uses this to select which value to request from the MovieDatabase API
            movieTask.execute(sortPreference);



        } else {
            // Network is not detected so publish a toast to user

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateMovie()", "No network connectivity detected.");

        }


    }




    /*
        Determines if local data can be used or a web request should be made for data.

    */
    private void libraryController() {

        // Setup variables for Decision Section
        boolean needsToBeUpdatedFromWeb = true;
        String sortPreference = obtainPreference();



        // obtain non-favorite Movie Entries for use in Decision Section
        String[] columns = new String[] {MovieContract.MovieEntry.COLUMN_MOVIE_KEY, MovieContract.MovieEntry.COLUMN_SORT_TYPE};
        final int ME_KEY = 0;
        final int ME_TYPE = 1;

        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                columns,
                MovieContract.MovieFavorites.WHERE_FAVORITE_CLAUSE,
                new String[] {Integer.toString(MovieContract.MovieFavorites.VAL_IS_NOT_FAVORITE)},
                null);



        // Decision Section
        // This section determines if the database information needsToBeUpdatedFromWeb
        // by comparing the existing MovieEntries Sort Type and the Sort Preference.
        // If  Preference == Favorites  => needsToBeUpdatedFromWeb = False
        // If  Preference == Existing MovieEntry Sort Type =>  needsToBeUpdatedFromWeb = False
        // If  Preference == a different Sort Type => needsToBeUpdatedFromWeb = True
        //
        // precondition of needsToBeUpdatedFromWeb = True
        if(cursor != null && cursor.getCount() > 0 ) {

            // obtain recorded search type from first entry on list
            cursor.moveToFirst();
            String entryPreference = cursor.getString(ME_TYPE);




            // If SortPreference is set to 'favorite'
            if (sortPreference.equalsIgnoreCase(getString(R.string.pref_sort_order_favorite))) {
                needsToBeUpdatedFromWeb = false;
                Log.v(LOG_TAG, "Favorite check - needsToBeUpdatedFromWeb  set to " + needsToBeUpdatedFromWeb);



            // if SortPreference has not changed
            } else if (sortPreference.equalsIgnoreCase(entryPreference)) {
                needsToBeUpdatedFromWeb = false;
                Log.v(LOG_TAG, "Other Preferences check - needsToBeUpdatedFromWeb set to " + needsToBeUpdatedFromWeb);

            }


            cursor.close();
        }





        // decision to pull information from the web or not based on needsToBeUpdatedFromWeb
        if (needsToBeUpdatedFromWeb) {
            updateMovie();
            Log.v(LOG_TAG, "LibraryController updated Movies Database (updateMovie)");


        } else {
            setupAdapter();
            Log.v(LOG_TAG, "LibraryController did not update Movies Database");

        }







    }



}




