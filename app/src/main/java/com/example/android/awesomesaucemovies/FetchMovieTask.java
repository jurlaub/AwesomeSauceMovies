package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dev on 9/12/15.
 */

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final static String API_KEY = new API().getAPI();
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private MovieLibrary sMovieLibrary;
    private final Context mContext;



    // store the sortPreference used to initiate the AsyncTask. The intent is to avoid a
    // conflict between a delay within the AsyncTask and the user updating preferences.
    //private String preferenceUsedInRequest;


    public FetchMovieTask(Context context){
        mContext = context;
        sMovieLibrary = MovieLibrary.get(mContext.getApplicationContext());
    }




    // adds to or updates MovieArray
    private void getMovieDataFromJSON(String movieJSONStr, String searchParameter, String preferenceUsedInRequest ) throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_POPULARITY = "popularity";
        final String MDB_VOTE_AVG = "vote_average";

//            final String MDB_ORIGINAL_TITLE = "original_title"; // not used
//            final String MDB_BACKDROP_PATH = "backdrop_path"; // not used
//            final String MDB_VOTE_COUNT = "vote_count";
//            final String MDB_GENRE_ID = "genre_ids";  // not used
//            final String MDB_ORIGINAL_LANGUAGE = "original_language";  // not used

        //ArrayList<MovieItem> mMovieItems = new ArrayList<MovieItem>();

        JSONObject movieJSON = new JSONObject(movieJSONStr);
        JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

        int arrayLength = newData.length();


        Log.i(LOG_TAG, "JSON length: " + arrayLength + " sort: " + searchParameter);


        sMovieLibrary.setSearchPreference(preferenceUsedInRequest);

        for(int i = 0; i < arrayLength; i++) {

            JSONObject movieItem = newData.getJSONObject(i);

            if( movieItem != null) {

                // capture movie detailed data
                MovieItem newItem = new MovieItem(movieItem.getString(MDB_ID));
                newItem.setmTitle(movieItem.getString(MDB_TITLE));
                newItem.setmReleaseDate(movieItem.getString(MDB_RELEASE_DATE));
                newItem.setmOverview(movieItem.getString(MDB_OVERVIEW));
                newItem.setmPopularity(movieItem.getString(MDB_POPULARITY));
                newItem.setmVoteAvg(Double.parseDouble(movieItem.getString(MDB_VOTE_AVG)));

                // store path value
                Log.v("JSON_PosterPath", movieItem.getString(MDB_POSTER_PATH));
                newItem.setmPosterPath(movieItem.getString(MDB_POSTER_PATH));


                Log.v(LOG_TAG, i + " " + newItem.getmTitle());


                //mMovieItems.add(newItem);

                sMovieLibrary.addMovieItem(newItem);

            }

        }





    }


    @Override
    protected Void doInBackground(String... urls){

        // here so they can be closed in the finally block if connection error
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // raw JSON response
        String movieJSONStr;
        String searchParameter;


        try {

            if (urls[0].equalsIgnoreCase("highestrated")) {
                // believe this is what was specified in the requirement
                searchParameter = "vote_average.desc";

            } else if (urls[0].equalsIgnoreCase("mostvoted")) {
                // prefer this version of high rating - more descriptive
                searchParameter = "vote_count.desc";

            } else {
                searchParameter = "popularity.desc";

            }

            // PostExecute updates MovieLibrary - the sort order stored aligns with the
            // contents of MovieLibrary mMovieItems.
            String preferenceUsedInRequest = urls[0];


            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", searchParameter)
                    .appendQueryParameter("api_key", API_KEY);


            URL url = new URL(builder.build().toString());
            Log.v(LOG_TAG, " URL: " + url);




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


            Log.i(LOG_TAG, "urlConnection opened and data returned");

            getMovieDataFromJSON(movieJSONStr, searchParameter, preferenceUsedInRequest);



        } catch (IOException e) {
            Log.e(LOG_TAG, "IO Error ", e);
            return null;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException", e);
            return null;

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();

                Log.i(LOG_TAG, "urlConnection closed");
            }

            if(reader != null) {
                try {
                    reader.close();

                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error Closing stream", e);
                }
            }
        }

        return null;



    }

//    @Override
//    protected void onPostExecute() {
//
//
//
//
//
//
//    }


}



