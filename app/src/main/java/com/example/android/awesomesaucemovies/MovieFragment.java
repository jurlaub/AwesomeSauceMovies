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

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 *

 *
 */
public class MovieFragment extends Fragment {

//    //---------- API Key ------------------------------------------------------------
//    //
//    //    >>>>  Replace "new API().getAPI();" with API String  <<<<<
//    //
    public final static String API_KEY = new API().getAPI();
//    //
//    //-------------------------------------------------------------------------------



    public final static String EXTRA_MESSAGE = MovieFragment.class.getCanonicalName();
    //public final static String EXTRA_KEY = "extra_key";  // See MovieFragment Note: Passing API Key

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // --!!!! -- must change if MOVIE_COLUMNS Changes  --!!!! --
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;

    private static final String MOVIE_ORDER = MovieContract.MovieEntry.COLUMN_NORMAL_RANK + " ASC";


    private MovieAdapter mMovieAdapter;
    private MovieLibrary sMovieLibrary;
    GridView mGridView;







    public MovieFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //initialize the MovieLibrary if not already initialized
        sMovieLibrary = MovieLibrary.get(getActivity());


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
            Log.i(LOG_TAG, "Settings");

            startActivity(new Intent(getActivity(), SettingsActivity.class));

            return true;

        } else if (id == R.id.action_about_toast) {
            Log.i(LOG_TAG, "About");

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



        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                Log.i(LOG_TAG, "in ItemClick, position: " + Integer.toString(position));


                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                    if (cursor != null) {
                        CharSequence text = cursor.getString(COL_MOVIE_ID);

                        // See MovieFragment Note: Passing API Key
                        Intent movieDetailIntent = new Intent(getActivity(), MovieDetails.class)
                                .putExtra(EXTRA_MESSAGE, text);

                        startActivity(movieDetailIntent);
                    }


            }


        });

        return rootView;
    }


    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

//        ArrayList<MovieItem> mMovieItems = sMovieLibrary.getMovies();
//
//
//        if (mMovieItems != null) {
//
//            Log.v(LOG_TAG, "movieItems are not null - setting up new adapter");
//
//            mGridView.setAdapter(new MovieAdapter(getActivity(), mMovieItems));
//
//        } else {
//            Log.v(LOG_TAG, "movieItems are null no adapter");
//            mGridView.setAdapter(null);
//        }

        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;


        Cursor cursor = getActivity().getContentResolver().query(movieUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    MOVIE_ORDER);

            if(cursor != null) {
                Log.v(LOG_TAG, "MovieEntry Cursor is not null - setting up new adapter");

                mMovieAdapter = new MovieAdapter(getActivity(), cursor, 0);
                mGridView.setAdapter(mMovieAdapter);

            } else {
                Log.v(LOG_TAG, "MovieEntry Cursor is null = no adapter");
                mGridView.setAdapter(null);
            }



    }


    @Override
    public void onStart(){
        super.onStart();

        libraryController();


    }




    @Override
    public void onDestroy(){
        super.onDestroy();
        Cursor c = null;
        try {
             c = mMovieAdapter.getCursor();
        } finally {
            if (c != null) {
                c.close();
            }
        }



    }




    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        // store the sortPreference used to initiate the AsyncTask. The intent is to avoid a
        // conflict between a delay within the AsyncTask and the user updating preferences.
        private String preferenceUsedInRequest;
        private Context context;

        public FetchMovieTask(Context c){
            this.context = c;

        }




        @Override
        protected ArrayList<MovieItem> doInBackground(String... urls){

            ArrayList<MovieItem> mMovieItems; // = new ArrayList<MovieItem>();

            preferenceUsedInRequest = urls[0];


            mMovieItems = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest, context);
            //movieItems = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest);

            Log.i(LOG_TAG, "urlConnection opened and data returned");

            return mMovieItems;

        }




        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            //super.onPostExecute(movieItems);

            if (movieItems != null) {

                //Log.v(LOG_TAG, "mMovieAdapter count after clear  " + mMovieAdapter.getCount());

                // Clear MovieLibrary of MovieItems if stored values are present
                if (!sMovieLibrary.getMovies().isEmpty()) {
                    sMovieLibrary.clearMovies();

                }

                // Set the searchPreference value in MovieLibrary to the search value used to
                // initiate the AsyncTask
                sMovieLibrary.setSearchPreference( preferenceUsedInRequest);

                for(MovieItem s: movieItems) {
                    sMovieLibrary.addMovieItem(s);
                    Log.v(LOG_TAG, s.getmTitle() + "- movieID: " + s.getmID());



                }
                Log.v(LOG_TAG, "sMovieLibrary updated, movie count:  " + sMovieLibrary.getMovies().size());

                if (movieItems.size() >= 1) {
                    int locNum = 0;

                    //MovieItem m1 = movieItems.get(locNum);

//                    ContentValues testItem = new ContentValues();
//
//                    testItem.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, m1.getmID());
//                    testItem.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, m1.getmOverview());
//                    testItem.put(MovieContract.MovieEntry.COLUMN_TITLE, m1.getmTitle());
//                    testItem.put(MovieContract.MovieEntry.COLUMN_NORMAL_RANK, Integer.toString(locNum));
//
                    Cursor tmpVal = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

                    if (tmpVal != null) {

                        Log.v(LOG_TAG, " query returned " + tmpVal.getCount() + " rows :: should expect > 0 rows");
                        Log.v(LOG_TAG, tmpVal.toString());

                    } else {
                        Log.v(LOG_TAG, "query returned a null value ");


                    }


                    tmpVal.close();

                }


            }


            //mMovieAdapter.notifyDataSetChanged();
            setupAdapter();


        }


    }

    private String obtainPreference() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        return  sortOrder;
    }



    private void updateMovie() {

        if (MovieFetcher.networkIsAvailable(getActivity())) {

            String sortPreference = obtainPreference();
            Log.i(LOG_TAG + ".updateMovie()", "generating a new API request, sort preference: "  + sortPreference);

            Context context = getActivity();
            FetchMovieTask movieTask = new FetchMovieTask(context);

            movieTask.execute(sortPreference);

        } else {

            int duration = Toast.LENGTH_LONG;
            Toast.makeText(getActivity(), getString(R.string.network_not_detected), duration).show();

            Log.i(LOG_TAG + ".updateMovie()", "No network connectivity detected.");

        }


    }




    /*
       Query to MovieLibrary to determine if a request should be made to update data from the
       Movie Database. For a future enhancement

       A query will be made according to the current sort preference.

    */
    private void libraryController() {

        String sortPreference = obtainPreference();

        // check MovieLibrary - does it have data, (later is it current)
        if (sMovieLibrary.movieLibraryNeedsToBeUpdated(sortPreference)){
            updateMovie();
            //mMovieAdapter.notifyDataSetChanged();


        } else {
            Log.i(LOG_TAG, "LibraryController did not update MovieLibrary");
        }


        // Future Sorting Existing Data Here
        // call to reorder MovieLibrary ArrayList according to user preference would go here -
        // implemented by MovieLibrary

    }



}




