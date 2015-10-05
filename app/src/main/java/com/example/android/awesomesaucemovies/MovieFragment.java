package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
                        .putExtra(EXTRA_MESSAGE, text)
                        .putExtra(EXTRA_KEY, API_KEY);

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









    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        // store the sortPreference used to initiate the AsyncTask. The intent is to avoid a
        // conflict between a delay within the AsyncTask and the user updating preferences.
        private String preferenceUsedInRequest;

        public FetchMovieTask(){

        }




//        // adds to or updates MovieArray
//        private ArrayList<MovieItem> getMovieDataFromJSON(String movieJSONStr, String searchParameter) throws JSONException {
//
//            final String MDB_RESULTS = "results";
//            final String MDB_ID = "id";
//            final String MDB_TITLE = "title";
//            final String MDB_OVERVIEW = "overview";
//            final String MDB_RELEASE_DATE = "release_date";
//            final String MDB_POSTER_PATH = "poster_path";
//            final String MDB_POPULARITY = "popularity";
//            final String MDB_VOTE_AVG = "vote_average";
//
////            final String MDB_ORIGINAL_TITLE = "original_title"; // not used
////            final String MDB_BACKDROP_PATH = "backdrop_path"; // not used
////            final String MDB_VOTE_COUNT = "vote_count";
////            final String MDB_GENRE_ID = "genre_ids";  // not used
////            final String MDB_ORIGINAL_LANGUAGE = "original_language";  // not used
//
//            ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();
//
//            JSONObject movieJSON = new JSONObject(movieJSONStr);
//            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);
//
//            int arrayLength = newData.length();
//
//
//            Log.i(LOG_TAG, "JSON length: " + arrayLength + " sort: " + searchParameter);
//
//
//            for(int i = 0; i < arrayLength; i++) {
//
//                JSONObject movieItem = newData.getJSONObject(i);
//
//
//
//                // capture movie detailed data
//                MovieItem newItem = new MovieItem(movieItem.getString(MDB_ID));
//                newItem.setmTitle(movieItem.getString(MDB_TITLE));
//                newItem.setmReleaseDate(movieItem.getString(MDB_RELEASE_DATE));
//                newItem.setmOverview(movieItem.getString(MDB_OVERVIEW));
//                newItem.setmPopularity(movieItem.getString(MDB_POPULARITY));
//                newItem.setmVoteAvg(Double.parseDouble(movieItem.getString(MDB_VOTE_AVG)));
//
//                // store path value
//                Log.v("JSON_PosterPath", movieItem.getString(MDB_POSTER_PATH));
//                newItem.setmPosterPath(movieItem.getString(MDB_POSTER_PATH));
//
//
//
//                Log.v(LOG_TAG, i + " " + newItem.getmTitle());
//
//
//                mMovieItems.add(newItem);
//
//
//
//            }
//
//
//
//            return mMovieItems;
//        }
//

        @Override
        protected ArrayList<MovieItem> doInBackground(String... urls){

            // here so they can be closed in the finally block if connection error
            //HttpURLConnection urlConnection = null;
            //BufferedReader reader = null;

            ArrayList<MovieItem> mMovieItems; // = new ArrayList<MovieItem>();

            // raw JSON response
            String movieJSONStr;
            String searchParameter;


//            try {

//                if (urls[0].equalsIgnoreCase("highestrated")) {
//                    // believe this is what was specified in the requirement
//                    searchParameter = "vote_average.desc";
//
//                } else if (urls[0].equalsIgnoreCase("mostvoted")) {
//                    // prefer this version of high rating - more descriptive
//                    searchParameter = "vote_count.desc";
//
//                } else {
//                    searchParameter = "popularity.desc";
//
//                }

                // PostExecute updates MovieLibrary - the sort order stored aligns with the
                // contents of MovieLibrary mMovieItems.
                preferenceUsedInRequest = urls[0];


//                Log.v(LOG_TAG, "preference  " + preferenceUsedInRequest);
//
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("http")
//                        .authority("api.themoviedb.org")
//                        .appendPath("3")
//                        .appendPath("discover")
//                        .appendPath("movie")
//                        .appendQueryParameter("sort_by", searchParameter)
//                        .appendQueryParameter("api_key", API_KEY);
//
//
//                //URL url = new URL(builder.build().toString());
//
//                String targetURL = builder.build().toString();
//
//
//                Log.v(LOG_TAG, " target URL: " + targetURL);
//

//
//
//                // Connection request
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//

//
//                // read input stream into string
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if(inputStream == null){
//                    return null;
//                }
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while((line = reader.readLine()) != null) {
//                    // newline for debug readabiliy
//                    buffer.append(line + "\n");
//                }
//
//
//                if(buffer.length() == 0) {
//                    // stream is empty
//                    return null;
//                }

                //movieJSONStr = buffer.toString();
                //movieJSONStr = new MovieFetcher().getUrl(targetURL);

                //movieJSONStr = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest);
                mMovieItems = new MovieFetcher().fetchMovieItems(preferenceUsedInRequest);

                Log.i(LOG_TAG, "urlConnection opened and data returned");

                //return getMovieDataFromJSON(movieJSONStr, searchParameter);
                return mMovieItems;


//            }
//            catch (IOException e) {
//                Log.e(LOG_TAG, "IO Error ", e);
//                return null;
//
//            }
//            catch (JSONException e) {
//                Log.e(LOG_TAG, "JSONException", e);
//                return null;
//
//            }
//            finally {
////                if(urlConnection != null) {
////                    urlConnection.disconnect();
////
////                    Log.i(LOG_TAG, "urlConnection closed");
////                }
////
////                if(reader != null) {
////                    try {
////                        reader.close();
////
////                    } catch (final IOException e) {
////                        Log.e(LOG_TAG, "Error Closing stream", e);
////                    }
////                }
//
//                Log.i(LOG_TAG, "end of doInBackgrouned");
//            }



        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            if (movieItems != null) {

                Log.v(LOG_TAG, "mMovieAdapter count after clear  " + mMovieAdapter.getCount());

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

            }


            mMovieAdapter.notifyDataSetChanged();



        }


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
            Uri tmpPath = m.getPosterPathURL(API_KEY);
            Picasso.with(getContext()).load(tmpPath).into(image);


            Log.v(LOG_TAG, "in MovieAdapter " + m.getmTitle() + " at position" + Integer.toString(position) );

            return convertView;
        }

    }


}
