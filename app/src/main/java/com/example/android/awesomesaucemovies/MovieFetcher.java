package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dev on 10/4/15.
 *
    Android Programming used as a guide for MovieFetcher.

 Hardy, Brian; Phillips, Bill (2013-04-09). Android Programming: The Big Nerd Ranch Guide (Big Nerd Ranch Guides) Pearson Education. Kindle Edition.
 */
public class MovieFetcher {

    //---------- API Key ------------------------------------------------------------
    //
    //    >>>>  Replace "new API().getAPI();" with API String  <<<<<
    //
    public final static String API_KEY = new API().getAPI();
    //
    //-------------------------------------------------------------------------------


    private final String LOG_TAG = MovieFetcher.class.getSimpleName();



    // based on feedback from code review and Android Dev page: "Check the Network Connection"
    public static boolean networkIsAvailable(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;

    }




    private byte[] getUrlBytes(String urlRequest) throws IOException {
        //from android programming referenced above

        //refactor Url builder
        URL url = new URL(urlRequest);
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();

        } finally {
            if(urlConnection != null) {

                urlConnection.disconnect();
                Log.i(LOG_TAG, "urlConnection closed");

            }

        }




    }

    private String getUrlString(String urlRequest) throws IOException {
        //refactor Url builder
        URL url = new URL(urlRequest);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
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

            return buffer.toString();

        } catch (IOException e) {

            Log.e(LOG_TAG, "IO Error ", e);
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


    }


    public String getUrl (String urlRequest) throws IOException {
        return new String(getUrlBytes(urlRequest));
        //return new String(getUrlString(urlRequest));
    }


    public ArrayList<MovieItem> fetchMovieItems(String sp){

        ArrayList<MovieItem> mMovieItems = new ArrayList<>();
        String searchParameter;

        try {
            if (sp.equalsIgnoreCase("highestrated")) {
                // believe this is what was specified in the requirement
                searchParameter = "vote_average.desc";

            } else if (sp.equalsIgnoreCase("mostvoted")) {
                // prefer this version of high rating - more descriptive
                searchParameter = "vote_count.desc";

            } else {
                searchParameter = "popularity.desc";

            }


            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", searchParameter)
                    .appendQueryParameter("api_key", API_KEY);


            //URL url = new URL(builder.build().toString());

            String targetURL = builder.build().toString();
            Log.v(LOG_TAG, " target URL: " + targetURL);



            String requestData = getUrl(targetURL);

            mMovieItems = getMovieDataFromJSON(requestData, sp);


        } catch (IOException e) {
            Log.e(LOG_TAG, "api request failed", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON  failed", e);

        }

        return mMovieItems;

    }

    // adds to or updates MovieArray
    private ArrayList<MovieItem> getMovieDataFromJSON(String movieJSONStr, String searchParameter) throws JSONException {

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

        ArrayList<MovieItem> mMovieItems = new ArrayList<>();

        JSONObject movieJSON = new JSONObject(movieJSONStr);
        JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

        int arrayLength = newData.length();


        Log.i(LOG_TAG, "JSON length: " + arrayLength + " sort: " + searchParameter);


        for(int i = 0; i < arrayLength; i++) {

            JSONObject movieItem = newData.getJSONObject(i);



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


            mMovieItems.add(newItem);



        }



        return mMovieItems;
    }


    private ArrayList<MovieItem_Video> getMovieVideoLinksFromJSON(String movieJSONStr) throws JSONException{

        final String LOG_TAG = "getMovieVideoLinksFromJSON";

        final String MDB_RESULTS = "results";
        final String MDB_ID = "id"; // movie id

        final String MDB_V_ID = "id"; // result id = i.e. movietrailer id
        final String MDB_V_LANGUAGE = "iso_639_1";
        final String MDB_V_KEY = "key";
        final String MDB_V_NAME = "name";
        final String MDB_V_SITE = "site";
        final String MDB_V_SIZE = "size";
        final String MDB_V_TYPE = "type";

        ArrayList<MovieItem_Video> movieItemsVideo = new ArrayList<>();

        try {
            JSONObject movieJSON = new JSONObject(movieJSONStr);
            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

            int arrayLength = newData.length();


            Log.i("MovieItem_Video", "JSON length: " + arrayLength);

            for(int i = 0; i < arrayLength; i++) {
                JSONObject m_obj = newData.getJSONObject(i);



                // capture movie detailed data
                MovieItem_Video newItem = new MovieItem_Video(m_obj.getString(MDB_V_ID));
                newItem.setVid_language(m_obj.getString(MDB_V_LANGUAGE));
                newItem.setVid_key(m_obj.getString(MDB_V_KEY));
                newItem.setVid_name(m_obj.getString(MDB_V_NAME));
                newItem.setVid_site(m_obj.getString(MDB_V_SITE));
                newItem.setVid_size(m_obj.getDouble(MDB_V_SIZE));
                newItem.setVid_type(m_obj.getString(MDB_V_TYPE));


                Log.v(LOG_TAG, i + " " + newItem.getVid_name());


                movieItemsVideo.add(newItem);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error" + e);

        }

        return movieItemsVideo;




    }


    public ArrayList<MovieItem_Video> fetchMovieTrailers(String movieID) {

        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieID)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", API_KEY);


            String targetURL = builder.build().toString();
            Log.v(LOG_TAG, " target URL: " + targetURL);

            String requestData = getUrl(targetURL);

            return getMovieVideoLinksFromJSON(requestData);

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error: " + e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error: " + e);

        }

        return null;
    }





}
