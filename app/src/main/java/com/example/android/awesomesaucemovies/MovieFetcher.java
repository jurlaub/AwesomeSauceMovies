package com.example.android.awesomesaucemovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.awesomesaucemovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    // determines if network connection is available
    // based on feedback from code review and Android Dev page: "Check the Network Connection"
    public static boolean networkIsAvailable(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;

    }



    // makes HTTP request
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



    // helper method that calls the method that actually retrieves the web requests
    public String getUrl (String urlRequest) throws IOException {
        return new String(getUrlBytes(urlRequest));

    }



    // obtain the Movie data from the web
    public void fetchMovieItems(String sp, Context context){

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



            String targetURL = builder.build().toString();
            Log.v(LOG_TAG, " target URL: " + targetURL);


            // used to obtain the web data
            String requestData = getUrl(targetURL);

            // parse the string from the web (obtained as JSON)
            getMovieDataFromJSON(requestData, sp, context);



        } catch (IOException e) {
            Log.e(LOG_TAG, "api request failed", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON  failed", e);

        }



    }


    // parses Movie Data and adds to SQLite MovieEntry DB
    private void getMovieDataFromJSON(String movieJSONStr, String searchParameter, Context context) throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_POPULARITY = "popularity";
        final String MDB_VOTE_AVG = "vote_average";


        JSONObject movieJSON = new JSONObject(movieJSONStr);
        JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

        int arrayLength = newData.length();



        // ---- DB Data  ------
        ContentValues[] cvData = new ContentValues[arrayLength];
        Log.v(LOG_TAG, "before cvData length: " + cvData.length);



        // iterates through the JSON data and adds to ContentValues[]
        for(int i = 0; i < arrayLength; i++) {

            JSONObject movieItem = newData.getJSONObject(i);


            String iID = movieItem.getString(MDB_ID);
            String iTitle = movieItem.getString(MDB_TITLE);
            String iReleaseDate = movieItem.getString(MDB_RELEASE_DATE);
            String iOverview = movieItem.getString(MDB_OVERVIEW);
            String iPopularity = movieItem.getString(MDB_POPULARITY);
            Double iVoteAve = Double.parseDouble(movieItem.getString(MDB_VOTE_AVG));
            String iPosterPath = movieItem.getString(MDB_POSTER_PATH);




            // ---- DB Data  ------
            ContentValues tmpItem = new ContentValues();

            tmpItem.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, iID);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_NORMAL_RANK, i);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_TITLE, iTitle);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, iOverview);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, iReleaseDate);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_POPULARITY, iPopularity);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, iVoteAve);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, iPosterPath);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_FAVORITE, MovieContract.MovieEntry.VAL_IS_NOT_FAVORITE);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_DELETE, MovieContract.MovieEntry.VAL_KEEP_ENTRY);
            tmpItem.put(MovieContract.MovieEntry.COLUMN_SORT_TYPE, searchParameter);

            cvData[i] = tmpItem;


        }


        // this section inserts the parsed JSON values into the SQLite DB
        int inserted = 0;
        if (cvData.length > 0) {
            inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvData);

            Log.v(LOG_TAG, inserted + " Rows added/modified in db  -------!!!!");
        }

    }






    // parses Trailer Video links found in the JSON information, adds to SQLite DB
    private void getMovieVideoLinksFromJSON(String movieJSONStr, Context context) throws JSONException{

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



        try {
            JSONObject movieJSON = new JSONObject(movieJSONStr);
            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

            int arrayLength = newData.length();

            String iID = movieJSON.getString(MDB_ID);   // Movie ID


            // ---- DB Data  ------
            ContentValues[] cvData = new ContentValues[arrayLength];
            Log.v(LOG_TAG, "before cvData length: " + cvData.length);



            // iterates through the JSON data and adds to ContentValues[]
            for(int i = 0; i < arrayLength; i++) {
                JSONObject m_obj = newData.getJSONObject(i);

                String tID = m_obj.getString(MDB_V_ID);
                String tUriKey = m_obj.getString(MDB_V_KEY);
                String tName = m_obj.getString(MDB_V_NAME);
                String tSite = m_obj.getString(MDB_V_SITE);
                int tSize = m_obj.getInt(MDB_V_SIZE);

                String tType = m_obj.getString(MDB_V_TYPE);
                String tLanguage = m_obj.getString(MDB_V_LANGUAGE);



                ContentValues newTrailer = new ContentValues();

                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_API_ID, tID);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_MOVIE_ID, iID);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_URI, tUriKey);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_NAME, tName);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_SITE, tSite);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_RESOLUTION, tSize);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_TYPE, tType);
                newTrailer.put(MovieContract.MovieTrailers.COLUMN_TRAILER_LANGUAGE, tLanguage);

                cvData[i] = newTrailer;
            }



            // this section inserts the parsed JSON values into the SQLite DB
            int inserted = 0;
            if (cvData.length > 0) {
                Uri movieUri = MovieContract.MovieTrailers.buildMovieTrailersUri(iID); // generate the trailer movie Uri

                inserted = context.getContentResolver().bulkInsert(movieUri, cvData);

                Log.v(LOG_TAG, inserted + " Rows added to db --- used uri:" + movieUri);
            }



        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error" + e);

        }





    }


    // obtain Trailer information
    public void fetchMovieTrailers(String movieID, Context context) {

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


            // used to obtain the web data
            String requestData = getUrl(targetURL);

            // parse the string from the web (obtained as JSON)
            getMovieVideoLinksFromJSON(requestData, context);

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error: " + e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error: " + e);

        }

    }



    // parse the JSON Movie Review file
    // Updates the SQLite DB
    private  void getMovieReviewsFromJSON(String movieJSONStr, Context context) throws JSONException{

        final String LOG_TAG = "getMovieReviewsFromJSON";

        final String MDB_PAGE = "page";
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id"; // movie id

        final String MDB_R_ID = "id"; // result id = i.e. movietrailer id
        final String MDB_R_LANGUAGE = "iso_639_1";
        final String MDB_R_AUTHOR = "author";
        final String MDB_R_CONTENT = "content";
        final String MDB_R_URL = "url";



        try {
            JSONObject movieJSON = new JSONObject(movieJSONStr);
            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

            int arrayLength = newData.length();
            String movieID = movieJSON.getString(MDB_ID);


            // ---- DB Data  ------
            ContentValues[] cvData = new ContentValues[arrayLength];
            Log.v(LOG_TAG, "before cvData length: " + cvData.length);



            // iterates through the JSON data and adds to ContentValues[]
            for(int i = 0; i < arrayLength; i++) {
                JSONObject m_obj = newData.getJSONObject(i);

                String rID = m_obj.getString(MDB_R_ID);
                String rAuthor = m_obj.getString(MDB_R_AUTHOR);
                String rContent = m_obj.getString(MDB_R_CONTENT);

                // test content for values
                if (rContent.equalsIgnoreCase("null")) {
                    rContent = context.getString(R.string.empty_missing_description);
                    Log.v(LOG_TAG, "MovieReview - filled in missing overview data");
                }

                // add review to DB
                ContentValues newReview = new ContentValues();
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_API_ID, rID);
                newReview.put(MovieContract.MovieReviews.COLUMN_MOVIE_ID, movieID);
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_AUTHOR, rAuthor);
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT, rContent);


                cvData[i] = newReview;

            }

            Log.v(LOG_TAG, "after cvData length: " + cvData.length);



            // this section inserts the parsed JSON values into the SQLite DB
            int inserted = 0;
            if (cvData.length > 0) {
                Uri movieUri = MovieContract.MovieReviews.buildMovieReviewsUri(movieID); // generate the trailer movie Uri

                inserted = context.getContentResolver().bulkInsert(movieUri, cvData);

                Log.v(LOG_TAG, inserted + " Rows added to db --- used uri:" + movieUri);
            }





        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error" + e);

        }



    }




    // obtain Review information - note 'hardcoded' for english
    public void fetchMovieReviews(String movieID, Context context) {

        String pageNumber = "1";
        String languageSetting = context.getString( R.string.language_setting); // hardcoded language specific to english

        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieID)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", languageSetting)  // language setting for reviews - hardcoded
                    .appendQueryParameter("page", pageNumber);


            String targetURL = builder.build().toString();
            Log.v(LOG_TAG, " target URL: " + targetURL);


            // used to obtain the web data
            String requestData = getUrl(targetURL);

            // parse the string from the web (obtained as JSON)
            getMovieReviewsFromJSON(requestData, context);

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error: " + e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error: " + e);

        }


    }





}
