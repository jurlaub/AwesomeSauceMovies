package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

 *
 */
public class MovieFragment extends Fragment {



    public final static String EXTRA_MESSAGE = MovieFragment.class.getCanonicalName();
    //public final static String EXTRA_KEY = "extra_key";  // See MovieFragment Note: Passing API Key

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private MovieLibrary sMovieLibrary;







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

//
        ArrayList<MovieItem> mMovieItems = sMovieLibrary.getMovies();
        mMovieAdapter = new MovieAdapter(getActivity(), R.layout.fragment_movie, mMovieItems);


        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(mMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                MovieItem mItem = (MovieItem) mMovieAdapter.getItem(position);
                CharSequence text = mItem.getmID();

                // See MovieFragment Note: Passing API Key
                Intent movieDetailIntent = new Intent(getActivity(), MovieDetails.class)
                        .putExtra(EXTRA_MESSAGE, text);

                startActivity(movieDetailIntent);

            }


        });

        return rootView;
    }




    @Override
    public void onStart(){
        super.onStart();

        libraryController();


    }











    // based on feedback from code review and Android Dev page: "Check the Network Connection"
    private boolean networkIsAvailable() {

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;

    }

    private String obtainPreference() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        return  sortOrder;
    }



    private void updateMovie() {

        if (networkIsAvailable()) {

            String sortPreference = obtainPreference();
            Log.i(LOG_TAG + ".updateMovie()", "generating a new API request, sort preference: "  + sortPreference);


            FetchMovieTask movieTask = new FetchMovieTask(getActivity());
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
            mMovieAdapter.notifyDataSetChanged();


        } else {
            Log.i(LOG_TAG, "LibraryController did not update MovieLibrary");
        }


        // Future Sorting Existing Data Here
        // call to reorder MovieLibrary ArrayList according to user preference would go here -
        // implemented by MovieLibrary

    }


    // will consider making this a separate class in order to simplify maintenance - at a later time.
    private class MovieAdapter extends ArrayAdapter<MovieItem> {

        //private Context iContext;

        public MovieAdapter ( Context context, int resourceID, ArrayList<MovieItem> movies ) {
            super(context, resourceID, movies);
            //iContext = context;
            Log.v(LOG_TAG, "MovieAdapter Constructor");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent ){

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);

                Log.v(LOG_TAG, "convertView == null; position: " + Integer.toString(position));
            }

            MovieItem m = getItem(position);

            ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_image);

            // MovieItem builds the PosterPath url, if empty or null returns null.
            Uri tmpPath = m.getPosterPathURL();
            Picasso.with(getContext()).load(tmpPath).into(image);


            Log.v(LOG_TAG, "in MovieAdapter " + m.getmTitle() + " at position" + Integer.toString(position) );

            return convertView;
        }

    }


}
