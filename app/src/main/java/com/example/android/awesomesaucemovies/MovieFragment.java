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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //public final static String EXTRA_MESSAGE = MovieFragment.class.getCanonicalName();


    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private final String PREFERENCE_CURSOR_POSITION = "cursor_position";
    private final int DEFAULT_ZERO = 0; // default cursor position;

    private static final int MOVIEFRAGMENT_LOADER = 0;


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




    public static final String[] UPDATE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_FAVORITE};

    //  -- must change if UPDATE_COLUMNS Changes  --
    public static final int COL_UPDATE_MOVIE_ID = 0;
    public static final int COL_UPDATE_FAVORITE = 1;
    //-------------------------------------------------------------------------------



    private MovieAdapter mMovieAdapter;
    GridView mGridView;
    private int mPosition;



    /*
        Callback interface that MainActivity must implement
     */
    public interface Callback {

        // for when an item has been selected
        public void onItemSelected(Uri dateUri);

        // use to reset the TwoPane view to the EmptyState view
        public void resetTwoPane();


    }




    public MovieFragment() {
    }







    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.v(LOG_TAG, "onActivityCreated - just before initializing the loaderManager");
        getLoaderManager().initLoader(MOVIEFRAGMENT_LOADER, null, this);
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

            setCursorPosition(DEFAULT_ZERO); // refresh includes resetting the cursor location. Movie list could have changed.

            ((Callback) getActivity()).resetTwoPane();
            updateMovie();  // request new content from API regardless of past setting



        // Allow the user to choose Sort Preference settings.
        } else if (id == R.id.action_settings) {

            Log.v(LOG_TAG, "Settings Activity Selected");

            ((Callback) getActivity()).resetTwoPane();
            startActivity(new Intent(getActivity(), SettingsActivity.class));

            return true;




        // show Movie Data Information Source
        } else if (id == R.id.action_about_toast) {

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

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        mGridView.setAdapter(mMovieAdapter);

        setCursorPosition();



        // Display Movie Details when selected
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                Log.i(LOG_TAG, "in ItemClick, position: " + Integer.toString(position));


                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    String selectedMovie = cursor.getString(COL_MOVIE_ID);
                    //cursor.close();
                    Log.v(LOG_TAG, "onItemClick, movieID = '" + selectedMovie + "'");

                    // save the Cursor Position
                    setCursorPosition(position);

                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(selectedMovie));


                }
            }
        });

        return rootView;
    }



    // setupAdapter obtains the cursor information used in the adapter. If no information available
    // then adapter is set up as null.
    void setupAdapter() {


        Log.v(LOG_TAG, "setupAdapter restarting Loader");
        getLoaderManager().restartLoader(MOVIEFRAGMENT_LOADER, null, this);

    }



    @Override
    public void onResume(){
        super.onResume();
        Log.v(LOG_TAG, "onResume ");

        mPosition = getCursorPosition();

        if (mPosition != GridView.INVALID_POSITION) {

            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);

            Log.v(LOG_TAG, "restoring to mPosition: " + mPosition);
        }

        libraryController();

    }



    @Override
    public void onPause(){
        super.onPause();
        Log.v(LOG_TAG, "onPause ");

        // saving cursor position
        setCursorPosition();
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



    // saving cursor position helper method
    private void setCursorPosition(){

        // save position if cursor exists
        if (mMovieAdapter.getCursor() != null) {

            int cursorPosition = mMovieAdapter.getCursor().getPosition();
            setCursorPosition(cursorPosition);


        } else {
            Log.v(LOG_TAG, "mMovieAdapter does not have a cursor");
        }


    }

    // sets the cursor position to the provided value
    private void setCursorPosition(int cursorPosition){

        int maxPosition = mMovieAdapter.getCursor().getCount();
        Log.v(LOG_TAG, "setCursorPosition maxCursor: " + maxPosition + " requested cursor position: " + cursorPosition);


        // Determine if cursorPosition is within bounds
        if (cursorPosition >= DEFAULT_ZERO && cursorPosition <= maxPosition) {

            Log.v(LOG_TAG, "setCursorPosition @ " + cursorPosition);

            SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
            editor.putInt(PREFERENCE_CURSOR_POSITION, cursorPosition);
            editor.apply();


        } else {

            // if not set, use preference default
            Log.e(LOG_TAG, "Selected cursor position outside cursor range");
        }


    }


    // obtain saved cursor position,
    //  --- defaults to Zero if value is not present  ---
    private int getCursorPosition(){

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int cursorPosition = preferences.getInt(PREFERENCE_CURSOR_POSITION, DEFAULT_ZERO);

        Log.v(LOG_TAG, "getting Cursor Position: " + cursorPosition);

        return cursorPosition;
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



        // Network is not detected so publish a toast to user
        } else {

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateMovie()", "No network connectivity detected.");

        }


    }




    /*
        Determines if local data can be used or a web request should be made for data.

    */
    private void libraryController() {

        // Decision Section Default is to update the DB from the web.
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

            int cursorPosition = cursor.getPosition(); // store cursor position

            // obtain recorded search type from first entry on list
            cursor.moveToFirst();
            String entryPreference = cursor.getString(ME_TYPE);

            cursor.moveToPosition(cursorPosition);  // restore cursor position


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


        Log.v("libraryController()", "Decision Section - DB needsToBeUpdatedFromWeb: " + needsToBeUpdatedFromWeb);


        // decision to pull information from the web or not based on needsToBeUpdatedFromWeb
        if (needsToBeUpdatedFromWeb) {
            updateMovie();
            Log.v(LOG_TAG, "LibraryController updated Movies Database (updateMovie)");


        // pull data directly from internal memory
        } else {

            setupAdapter();
            Log.v(LOG_TAG, "LibraryController did not update Movies Database");

        }


    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // obtain preference
        String sharedPreference = obtainPreference();
        Uri movieUri;
        String selection = null;
        String[] selectionArgs = null;

        // decision for the data source to use Favorite data from Database OR to use the other Movie
        // Entries from the Database.
        switch (sharedPreference) {
            case "favorite":
                // obtain Favorite Movie data cursor (may be 'empty' if no MovieEntries marked Favorite.)
                movieUri = MovieContract.MovieFavorites.CONTENT_URI;

                break;


            default:
                // obtain the MovieEntry data that has a rank.
                movieUri = MovieContract.MovieEntry.CONTENT_URI;
                selection =  MovieContract.MovieEntry.WHERE_NOT_RANKED_CLAUSE;
                selectionArgs =  new String[] {Integer.toString(MovieContract.MovieEntry.VAL_OMIT_FROM_RANK)};

        }


        Log.v(LOG_TAG, "onCreateLoader: movieURI: " + movieUri);
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                selection,
                selectionArgs,
                MOVIE_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        Log.v(LOG_TAG, "onLoadFinished, Cursor data count = " + data.getCount());

        if (data.getCount() == 0) {
            String sharedPreference = obtainPreference();
            String messageText = "";

            if (sharedPreference.equalsIgnoreCase(getString(R.string.pref_sort_order_favorite))) {
                messageText = getString(R.string.no_favorite_entries);

            }

            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), messageText, duration);
            toast.show();



        }




        mMovieAdapter.swapCursor(data);

        mGridView.smoothScrollToPosition(getCursorPosition());

//        if (mPosition != GridView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mGridView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Log.v(LOG_TAG, "onLoaderReset");
        mMovieAdapter.swapCursor(null);

    }



}




