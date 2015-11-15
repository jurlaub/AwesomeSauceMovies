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



    public static final String[] UPDATE_COLUMNS = {MovieContract.MovieEntry.COLUMN_MOVIE_KEY,
            MovieContract.MovieEntry.COLUMN_FAVORITE};
    public static final int COL_UPDATE_MOVIE_ID = 0;
    public static final int COL_UPDATE_FAVORITE = 1;


    private MovieAdapter mMovieAdapter;
    private Cursor mGridCursor;
    GridView mGridView;







    public MovieFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //initialize the MovieLibrary if not already initialized
        //sMovieLibrary = MovieLibrary.get(getActivity());


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
            Log.v(LOG_TAG, "Settings Activity Selected");

            startActivity(new Intent(getActivity(), SettingsActivity.class));

            return true;

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
        setupAdapter();



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


    void setupAdapter() {
        if (getActivity() == null || mGridView == null) {
            Log.v(LOG_TAG, "setupAdapter: activity or mGridView is null");
            return;
        }

        //test for shared preferences
        String sharedPreference = obtainPreference();
        Uri movieUri;

        Log.v(LOG_TAG, "SharedPreference: " + sharedPreference);

        switch (sharedPreference) {

            case "favorite":
                movieUri = MovieContract.MovieFavorites.CONTENT_URI;

                mGridCursor = getActivity().getContentResolver().query(movieUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);


                break;


            default:
                movieUri = MovieContract.MovieEntry.CONTENT_URI;


                // obtain MovieEntries not marked -1
                mGridCursor = getActivity().getContentResolver().query(movieUri,
                        MOVIE_COLUMNS,
                        MovieContract.MovieEntry.WHERE_NOT_RANKED_CLAUSE,
                        new String[] {Integer.toString(MovieContract.MovieEntry.VAL_OMIT_FROM_RANK)},
                        MOVIE_ORDER);


        }



            if(mGridCursor != null && mGridCursor.getCount() > 0) {
                Log.v(LOG_TAG, "MovieEntry Cursor is not null - setting up new adapter; count is " + mGridCursor.getCount());

                mMovieAdapter = new MovieAdapter(getActivity(), mGridCursor, 0);
                mGridView.setAdapter(mMovieAdapter);

            } else {


                // notification if there are no favorites in db
                if (sharedPreference.equalsIgnoreCase(getString(R.string.pref_sort_order_favorite))) {

                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(getActivity(), getString(R.string.no_favorite_entries), duration);
                    toast.show();

                }

                Log.v(LOG_TAG, "MovieEntry Cursor is null = no adapter");
                mGridView.setAdapter(null);
            }



    }


    @Override
    public void onStart(){
        super.onStart();
        Log.v(LOG_TAG, "onStart ");

        libraryController();

    }

    @Override
    public void onResume(){
        super.onResume();

        Log.v(LOG_TAG, "onResume ");

    }



//    @Override
//    public void onStop(){
//        super.onStop();
//        Cursor c;
//
//        // may need to move this to an earlier point.
//        if(!mGridCursor.isClosed()){
//            mGridCursor.close();
//            Log.v(LOG_TAG, "onStop, closing Cursor");
//        }
//
//
//        c = mMovieAdapter.getCursor();
//        if (!c.isClosed()) {
//            c.close();
//            Log.v(LOG_TAG, "onStop, closing Cursor");
//        }
//
//
//    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(LOG_TAG, " onDestroy");




    }

    @Override
    public void onPause(){
        super.onPause();
        Log.v(LOG_TAG, " onPause");



    }


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

            //ArrayList<MovieItem> mMovieItems; // = new ArrayList<MovieItem>();

            preferenceUsedInRequest = urls[0];


            new MovieFetcher().fetchMovieItems(preferenceUsedInRequest, context);
            //movieItems = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest);

            Log.i(LOG_TAG, "urlConnection opened and data returned");

            return null;

        }




        @Override
        protected void onPostExecute(Void v) {
            //super.onPostExecute(movieItems);

//            if (movieItems != null) {
//
//                //Log.v(LOG_TAG, "mMovieAdapter count after clear  " + mMovieAdapter.getCount());
//
//                // Clear MovieLibrary of MovieItems if stored values are present
//                if (!sMovieLibrary.getMovies().isEmpty()) {
//                    sMovieLibrary.clearMovies();
//
//                }
//
//                // Set the searchPreference value in MovieLibrary to the search value used to
//                // initiate the AsyncTask
//                sMovieLibrary.setSearchPreference( preferenceUsedInRequest);
//
//                for(MovieItem s: movieItems) {
//                    sMovieLibrary.addMovieItem(s);
//                    Log.v(LOG_TAG, s.getmTitle() + "- movieID: " + s.getmID());
//
//
//
//                }
//                Log.v(LOG_TAG, "sMovieLibrary updated, movie count:  " + sMovieLibrary.getMovies().size());
//
//                if (movieItems.size() >= 1) {
//                    int locNum = 0;
//
//                    //MovieItem m1 = movieItems.get(locNum);
//
////                    ContentValues testItem = new ContentValues();
////
////                    testItem.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, m1.getmID());
////                    testItem.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, m1.getmOverview());
////                    testItem.put(MovieContract.MovieEntry.COLUMN_TITLE, m1.getmTitle());
////                    testItem.put(MovieContract.MovieEntry.COLUMN_NORMAL_RANK, Integer.toString(locNum));
////
//                    Cursor tmpVal = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//
//                    if (tmpVal != null) {
//
//                        Log.v(LOG_TAG, " query returned " + tmpVal.getCount() + " rows :: should expect > 0 rows");
//                        Log.v(LOG_TAG, tmpVal.toString());
//
//                    } else {
//                        Log.v(LOG_TAG, "query returned a null value ");
//
//
//                    }
//
//
//                    tmpVal.close();
//
//                }
//
//
//            }


            //mMovieAdapter.notifyDataSetChanged();
            setupAdapter();


        }


    }

    private String obtainPreference() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        Log.v(LOG_TAG, "Sort Order Preference: " + sortOrder);

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
        Determines if local data can be used or a web request should be made for data.



       A query will be made according to the current sort preference.

    */
    private void libraryController() {

        String[] columns = new String[] {MovieContract.MovieEntry.COLUMN_MOVIE_KEY, MovieContract.MovieEntry.COLUMN_SORT_TYPE};
        final int ME_KEY = 0;
        final int ME_TYPE = 1;

        boolean needsToBeUpdatedFromWeb = true;

        String sortPreference = obtainPreference();



        // obtain non-favorite Movie Entries
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                columns,
                MovieContract.MovieFavorites.WHERE_FAVORITE_CLAUSE,
                new String[] {Integer.toString(MovieContract.MovieFavorites.VAL_IS_NOT_FAVORITE)},
                null);




        if(cursor != null && cursor.getCount() > 0 ) {

            // obtain recorded search type from first entry on list
            cursor.moveToFirst();
            String entryPreference = cursor.getString(ME_TYPE);



            if (sortPreference.equalsIgnoreCase(getString(R.string.pref_sort_order_favorite))) {
                needsToBeUpdatedFromWeb = false;
                Log.v(LOG_TAG, "Favorite check - needsToBeUpdatedFromWeb  set to " + needsToBeUpdatedFromWeb);


            } else if (sortPreference.equalsIgnoreCase(entryPreference)) {
                needsToBeUpdatedFromWeb = false;
                Log.v(LOG_TAG, "Other Preferences check - needsToBeUpdatedFromWeb set to " + needsToBeUpdatedFromWeb);

            }


            cursor.close();
        }




        if (needsToBeUpdatedFromWeb) {
            updateMovie();
            Log.v(LOG_TAG, "After updateMovie in Library Controller");


        } else {
            setupAdapter();
            Log.v(LOG_TAG, "After setupAdapter in Library Controller");


        }



        Log.v(LOG_TAG, "LibraryController did not update Movies Database");



    }



}




