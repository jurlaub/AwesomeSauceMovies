package com.example.android.awesomesaucemovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 *
 * Note: Passing API Key
 *  I would prefer a different approach. However, for 'assessment'
 *  purposes, passing allows the Udacity reviewer to add the key 1 time in one place.
 *  Future projects will consider a different approach.
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
    public final static String EXTRA_KEY = "extra_key";  // See MovieFragment Note: Passing API Key

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

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
        inflater.inflate(R.menu.menu_movie_fragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // allows the user to request an update from MovieDatabase - this overrides the MovieLibrary
        // rules related to downloading new data
        if (id == R.id.action_refresh) {
            Log.v(LOG_TAG, " Requesting updated Movie information.");

            updateMovie();  // request new content from API regardless of past setting

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


                Log.e(LOG_TAG, "in ItemClick, position: " + Integer.toString(position));

                //MovieItem mItem =  mMovieAdapter.getItem(position);
                MovieItem mItem = sMovieLibrary.getMovieItem(position);

                CharSequence text = mItem.getmID();

                updateTrailers(text.toString());
                updateReviews(text.toString());

                // See MovieFragment Note: Passing API Key
                Intent movieDetailIntent = new Intent(getActivity(), MovieDetails.class)
                        .putExtra(EXTRA_MESSAGE, text)
                        .putExtra(EXTRA_KEY, API_KEY);

                startActivity(movieDetailIntent);

            }


        });

        return rootView;
    }


    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        ArrayList<MovieItem> mMovieItems = sMovieLibrary.getMovies();


        if (mMovieItems != null) {

            mGridView.setAdapter(new MovieAdapter(mMovieItems));

        } else {
            mGridView.setAdapter(null);
        }
    }




    // will consider making this a separate class in order to simplify maintenance - at a later time.
    private class MovieAdapter extends ArrayAdapter<MovieItem> {
        //private Context iContext;

        public MovieAdapter(ArrayList<MovieItem> movies) {
            super(getActivity(), 0, movies);
            Log.v(LOG_TAG, "MovieAdapter Constructor 1");
        }

//        public MovieAdapter ( Context context, int resourceID, ArrayList<MovieItem> movies ) {
//            super(context, resourceID, movies);
//            //iContext = context;
//            Log.v(LOG_TAG, "MovieAdapter Constructor 2");
//        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent ){

            if (convertView == null) {

                //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_movie, parent, false);

                Log.v(LOG_TAG, "convertView == null; position: " + Integer.toString(position));
            }

            MovieItem m = getItem(position);

            ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_image);

            // MovieItem builds the PosterPath url, if empty or null returns null.
            Uri tmpPath = m.getPosterPathURL(API_KEY);
            Picasso.with(getContext()).load(tmpPath).into(image);


            Log.v(LOG_TAG, "in MovieAdapter " + m.getmTitle() + " at position" + Integer.toString(position));

            return convertView;
        }

    }




    @Override
    public void onStart(){
        super.onStart();

        libraryController();


    }









    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        // store the sortPreference used to initiate the AsyncTask. The intent is to avoid a
        // conflict between a delay within the AsyncTask and the user updating preferences.
        private String preferenceUsedInRequest;

        public FetchMovieTask(){

        }




        @Override
        protected ArrayList<MovieItem> doInBackground(String... urls){

            ArrayList<MovieItem> mMovieItems; // = new ArrayList<MovieItem>();
            preferenceUsedInRequest = urls[0];

            mMovieItems = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest);

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


                }
                Log.v(LOG_TAG, "sMovieLibrary updated, movie count:  " + sMovieLibrary.getMovies().size());
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


            FetchMovieTask movieTask = new FetchMovieTask();
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


        } else {
            Log.i(LOG_TAG, "LibraryController did not update MovieLibrary");
        }


        // Future Sorting Existing Data Here
        // call to reorder MovieLibrary ArrayList according to user preference would go here -
        // implemented by MovieLibrary

    }





    public void updateTrailers(String movieID){

        if (MovieFetcher.networkIsAvailable(getActivity())) {
            String[] vals = {movieID, MovieDetails.TRAILERS};

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask();
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

            FetchMovieDetailsTask movieTask = new FetchMovieDetailsTask();
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

        public FetchMovieDetailsTask(){

        }

        @Override
        protected ArrayList doInBackground(String... urls) {

            ArrayList mItems = new ArrayList();

            movieID = urls[0];
            request = urls[1];

            switch(request) {
                case MovieDetails.TRAILERS:
                    Log.i(LOG_TAG, "match trailers: " + request);
                    mItems = new MovieFetcher().fetchMovieTrailers(movieID);
                    break;

                case MovieDetails.REVIEWS:
                    Log.i(LOG_TAG, "match reviews: " + request);
                    mItems = new MovieFetcher().fetchMovieReviews(movieID);

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
                        break;

                    case MovieDetails.REVIEWS:
                        sMovieLibrary.addMovieReviews(movieID, items);
                        Log.i(LOG_TAG, "in onPostExecute reviews: " + request);
                        break;

                    default:
                        throw new UnsupportedOperationException("onPostExecute: Unknown value : " + request);

                }






            }
        }

    }


}




