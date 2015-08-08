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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    public final static String EXTRA_MESSAGE = MovieFragment.class.getCanonicalName();
    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private MovieLibrary sMovieLibrary;



    //---------- API Key --------------------
    //    Replace with API String
    //
    private final String API_KEY = new API().getAPI();
    //
    //---------------------------------------




    public MovieFragment() {
    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //initialize the MovieLibrary if not already initialized
        sMovieLibrary = MovieLibrary.get(getActivity());

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//
//
//        return super.onOptionsItemSelected(item);
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        sMovieLibrary = MovieLibrary.get(getActivity());
        ArrayList<MovieItem> mMovieItems = sMovieLibrary.getMovies();

        mMovieAdapter = new MovieAdapter(mMovieItems);

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(mMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                CharSequence text = mMovieAdapter.getItem(position).getmID();
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast.makeText(v.getContext(), text + " " + position, duration).show();


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

        //updateMovie();
        libraryController();


    }






    public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<MovieItem>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        public FetchMovieTask(){

        }

        // Assemble the image Path
        private String getMoviePosterUrl(String posterPath) {
            String imgSize = "w185";
            String baseURL = "http://image.tmdb.org/t/p/";
            String a ="?api_key=";


            String tmpPath =  baseURL + imgSize + "/" + posterPath + a + API_KEY;

            return tmpPath;
        }


        // adds to or updates MovieArray
        private ArrayList<MovieItem> getMovieDataFromJSON(String movieJSONStr) throws JSONException {

            final String MDB_RESULTS = "results";
            final String MDB_ID = "id";
            final String MDB_ORIGINAL_TITLE = "original_title";
            final String MDB_TITLE = "title";
            final String MDB_OVERVIEW = "overview";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_BACKDROP_PATH = "backdrop_path";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_POPULARITY = "popularity";
            final String MDB_VOTE_AVG = "vote_average";
            final String MDB_VOTE_COUNT = "vote_count";
            final String MDB_GENRE_ID = "genre_ids";
            final String MDB_ORIGINAL_LANGUAGE = "original_language";

            ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();

            JSONObject movieJSON = new JSONObject(movieJSONStr);
            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

            int arrayLength = newData.length();

            String [] imageUrls = new String[arrayLength];



            for(int i = 0; i < arrayLength; i++) {

                JSONObject movieItem = newData.getJSONObject(i);


                MovieItem newItem = new MovieItem(movieItem.getString(MDB_ID));

                newItem.setmTitle(movieItem.getString(MDB_TITLE));
//                newItem.setmReleaseDate(movieItem.getString(MDB_RELEASE_DATE));
                newItem.setmOverview(movieItem.getString(MDB_OVERVIEW));
                newItem.setmPopularity(movieItem.getString(MDB_POPULARITY));
                newItem.setmURL(getMoviePosterUrl(movieItem.getString(MDB_POSTER_PATH)));
                newItem.setmVoteAvg(Double.parseDouble(movieItem.getString(MDB_VOTE_AVG)));




                //Log.v(LOG_TAG, i + " " + movieItem.getString(MDB_TITLE));
                Log.v(LOG_TAG, i + " " + newItem.getmTitle());

                //sMovieLibrary.addMovieItem(newItem);
                mMovieItems.add(newItem);

            }



            return mMovieItems;
        }


        @Override
        protected ArrayList<MovieItem> doInBackground(Void... urls){

            // here so they can be closed in the finally block if connection error
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // raw JSON response
            String movieJSONStr;



            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .appendQueryParameter("page", "2")
                        .appendQueryParameter("api_key", API_KEY);


                URL url = new URL(builder.build().toString());
                Log.i(LOG_TAG, " URL: " + url);

                //URL url = new URL(testUrl);


                // Connection request
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // read input stream into string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    // newline for debug readabiliy
                    buffer.append(line + "\n");
                }


                if(buffer.length() == 0) {
                    // stream is empty
                    return null;
                }

                movieJSONStr = buffer.toString();

                //Log.v(LOG_TAG, movieJSONStr);



                return getMovieDataFromJSON(movieJSONStr);



            } catch (IOException e) {
                Log.e(LOG_TAG, "IO Error ", e);
                return null;

            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException", e);
                return null;

            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }

                if(reader != null) {
                    try {
                        reader.close();

                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Closing stream", e);
                    }
                }
            }



        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {

            if (movieItems != null) {

                for(MovieItem s: movieItems) {
                    sMovieLibrary.addMovieItem(s);

                }
            }

            mMovieAdapter.notifyDataSetChanged();

        }


    }


    private void updateMovie() {

        FetchMovieTask movieTask = new FetchMovieTask();

        movieTask.execute();
    }



    private void libraryController() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_sort_order_default));

        // check MovieLibrary - does it have data, (later is it current)
        if (sMovieLibrary.movieLibraryNeedsToBeUpdated()){
            updateMovie();


        } else {
            Log.i(LOG_TAG, "LibraryController did not update MovieLibrary");
        }

        // reorder MovieLibrary ArrayList according to user preference



    }

    private class MovieAdapter extends ArrayAdapter<MovieItem> {

        public MovieAdapter (ArrayList<MovieItem> movies ) {
            super(getActivity(), 0, movies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent ){

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_item_movie, parent, false);
            }

            MovieItem m = getItem(position);


            ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_image);


            Picasso.with(getContext()).load(m.getmURL()).into(image);
            //Log.i(LOG_TAG, "end of MovieAdapter");

            return convertView;
        }

    }


}
