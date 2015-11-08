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


//    public ContentValues[] fetchMovieItems(String sp){
//        ContentValues[] movieItems;

    public ArrayList<MovieItem> fetchMovieItems(String sp, Context context){

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

            mMovieItems = getMovieDataFromJSON(requestData, sp, context);


            //movieItems = getMovieDataFromJSON(requestData, sp);

        } catch (IOException e) {
            Log.e(LOG_TAG, "api request failed", e);
            //movieItems = null;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON  failed", e);
            //movieItems = null;
        }

        return mMovieItems;

    }


    private ArrayList<MovieItem> getMovieDataFromJSON(String movieJSONStr, String searchParameter, Context context) throws JSONException {

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



        // ---- DB Data  ------
        ContentValues[] cvData = new ContentValues[arrayLength];
        Log.v(LOG_TAG, "before cvData length: " + cvData.length);


        Log.i(LOG_TAG, "JSON length: " + arrayLength + " sort: " + searchParameter);


        for(int i = 0; i < arrayLength; i++) {

            JSONObject movieItem = newData.getJSONObject(i);


            String iID = movieItem.getString(MDB_ID);
            String iTitle = movieItem.getString(MDB_TITLE);
            String iReleaseDate = movieItem.getString(MDB_RELEASE_DATE);
            String iOverview = movieItem.getString(MDB_OVERVIEW);
            String iPopularity = movieItem.getString(MDB_POPULARITY);
            Double iVoteAve = Double.parseDouble(movieItem.getString(MDB_VOTE_AVG));
            String iPosterPath = movieItem.getString(MDB_POSTER_PATH);


            // capture movie detailed data
            MovieItem newItem = new MovieItem(iID);
                newItem.setmTitle(iTitle);
                newItem.setmReleaseDate(iReleaseDate);
                newItem.setmOverview(iOverview);
                newItem.setmPopularity(iPopularity);
                newItem.setmVoteAvg(iVoteAve);

                // store path value
                Log.v("JSON_PosterPath", iPosterPath);
                newItem.setmPosterPath(iPosterPath);



                Log.v(LOG_TAG, i + " " + iTitle);


            mMovieItems.add(newItem);


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

            cvData[i] = tmpItem;


        }
        Log.v(LOG_TAG, "after cvData length: " + cvData.length);

        int inserted = 0;
        if (cvData.length > 0) {
            inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvData);

            Log.v(LOG_TAG, inserted + " Rows added to db  -------!!!!");
        }
//



        return mMovieItems;
    }



//    // adds to or updates MovieArray
//    private ContentValues[] getMovieDataFromJSON(String movieJSONStr, String searchParameter) throws JSONException {
//
//        final String MDB_RESULTS = "results";
//        final String MDB_ID = "id";
//        final String MDB_TITLE = "title";
//        final String MDB_OVERVIEW = "overview";
//        final String MDB_RELEASE_DATE = "release_date";
//        final String MDB_POSTER_PATH = "poster_path";
//        final String MDB_POPULARITY = "popularity";
//        final String MDB_VOTE_AVG = "vote_average";
//
////            final String MDB_ORIGINAL_TITLE = "original_title"; // not used
////            final String MDB_BACKDROP_PATH = "backdrop_path"; // not used
////            final String MDB_VOTE_COUNT = "vote_count";
////            final String MDB_GENRE_ID = "genre_ids";  // not used
////            final String MDB_ORIGINAL_LANGUAGE = "original_language";  // not used
//
//
//        JSONObject movieJSON = new JSONObject(movieJSONStr);
//        JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);
//
//        int arrayLength = newData.length();
//
//        // ---- DB Data  ------
//        ContentValues[] cvData = new ContentValues[arrayLength];
//
//
//        Log.i(LOG_TAG, "JSON length: " + arrayLength + " sort: " + searchParameter);
//
//
//        for(int i = 0; i < arrayLength; i++) {
//
//            JSONObject movieItem = newData.getJSONObject(i);
//
//
//            String iID = movieItem.getString(MDB_ID);
//            String iTitle = movieItem.getString(MDB_TITLE);
//            String iReleaseDate = movieItem.getString(MDB_RELEASE_DATE);
//            String iOverview = movieItem.getString(MDB_OVERVIEW);
//            String iPopularity = movieItem.getString(MDB_POPULARITY);
//            Double iVoteAve = Double.parseDouble(movieItem.getString(MDB_VOTE_AVG));
//            String iPosterPath = movieItem.getString(MDB_POSTER_PATH);
//
//
//            // ---- DB Data  ------
//            ContentValues tmpItem = new ContentValues();
//
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, iID);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_TITLE, iTitle);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, iOverview);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, iReleaseDate);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_POPULARITY, iPopularity);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, iVoteAve);
//            tmpItem.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, iPosterPath);
//
//            cvData[i] = tmpItem;
//
//
//        }
//
//
//
//
//        return cvData;
//    }
//




    private ArrayList<MovieItem_Video> getMovieVideoLinksFromJSON(String movieJSONStr, Context context) throws JSONException{

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

            String iID = movieJSON.getString(MDB_ID);   // Movie ID


            // ---- DB Data  ------
            ContentValues[] cvData = new ContentValues[arrayLength];
            Log.v(LOG_TAG, "before cvData length: " + cvData.length);


            Log.i("MovieItem_Video", "JSON length: " + arrayLength);

            for(int i = 0; i < arrayLength; i++) {
                JSONObject m_obj = newData.getJSONObject(i);

                String tID = m_obj.getString(MDB_V_ID);
                String tUriKey = m_obj.getString(MDB_V_KEY);
                String tName = m_obj.getString(MDB_V_NAME);
                String tSite = m_obj.getString(MDB_V_SITE);
                int tSize = m_obj.getInt(MDB_V_SIZE);

                String tType = m_obj.getString(MDB_V_TYPE);
                String tLanguage = m_obj.getString(MDB_V_LANGUAGE);





                // capture movie detailed data
                MovieItem_Video newItem = new MovieItem_Video(tID);
                newItem.setVid_language(tLanguage);
                newItem.setVid_key(tUriKey);
                newItem.setVid_name(tName);
                newItem.setVid_site(tSite);
                newItem.setVid_size(m_obj.getDouble(MDB_V_SIZE));
                newItem.setVid_type(tType);



                newItem.setMovie_id(iID);  // adding MovieID to the newItem


                Log.v(LOG_TAG, i + " " + newItem.getVid_name());
                Log.v(LOG_TAG, " This is the link: " + newItem.getVid_site() + " " + newItem.getVid_key());


                movieItemsVideo.add(newItem);


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

            Log.v(LOG_TAG, "after cvData length: " + cvData.length);

            int inserted = 0;
            if (cvData.length > 0) {
                Uri movieUri = MovieContract.MovieTrailers.buildMovieTrailersUri(iID); // generate the trailer movie Uri

                inserted = context.getContentResolver().bulkInsert(movieUri, cvData);

                Log.v(LOG_TAG, inserted + " Rows added to db --- used uri:" + movieUri);
            }



        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error" + e);

        }

        return movieItemsVideo;




    }


    public ArrayList<MovieItem_Video> fetchMovieTrailers(String movieID, Context context) {

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

            return getMovieVideoLinksFromJSON(requestData, context);

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error: " + e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error: " + e);

        }

        return null;
    }




    private ArrayList<MovieItem_Reviews> getMovieReviewsFromJSON(String movieJSONStr, Context context) throws JSONException{

        final String LOG_TAG = "getMovieReviewsFromJSON";

        final String MDB_PAGE = "page";
        final String MDB_RESULTS = "results";
        final String MDB_ID = "id"; // movie id

        final String MDB_R_ID = "id"; // result id = i.e. movietrailer id
        final String MDB_R_LANGUAGE = "iso_639_1";
        final String MDB_R_AUTHOR = "author";
        final String MDB_R_CONTENT = "content";
        final String MDB_R_URL = "url";


        ArrayList<MovieItem_Reviews> movieReviews = new ArrayList<>();

        try {
            JSONObject movieJSON = new JSONObject(movieJSONStr);
            JSONArray newData = movieJSON.getJSONArray(MDB_RESULTS);

            int arrayLength = newData.length();
            String movieID = movieJSON.getString(MDB_ID);


            // ---- DB Data  ------
            ContentValues[] cvData = new ContentValues[arrayLength];
            Log.v(LOG_TAG, "before cvData length: " + cvData.length);


            Log.i("MovieItem_Review", "JSON length: " + arrayLength);

            for(int i = 0; i < arrayLength; i++) {
                JSONObject m_obj = newData.getJSONObject(i);



                String rID = m_obj.getString(MDB_R_ID);
                String rAuthor = m_obj.getString(MDB_R_AUTHOR);
                String rContent = m_obj.getString(MDB_R_CONTENT);



                // capture movie detailed data
                MovieItem_Reviews newItem = new MovieItem_Reviews(m_obj.getString(MDB_R_ID),m_obj.getString(MDB_R_AUTHOR), m_obj.getString(MDB_R_CONTENT) );  //movieJSON.getString(MDB_ID)
                //newItem.setReviewAuthor(m_obj.getString(MDB_R_AUTHOR));
                //newItem.setReviewContent(m_obj.getString(MDB_R_CONTENT));
                //newItem.setReviewLanguage(m_obj.getString(MDB_R_LANGUAGE));
                newItem.setReviewUrl(m_obj.getString(MDB_R_URL));


                Log.v(LOG_TAG, i + " " + newItem.getReviewAuthor());


                movieReviews.add(newItem);

                // add review to DB
                ContentValues newReview = new ContentValues();
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_API_ID, rID);
                newReview.put(MovieContract.MovieReviews.COLUMN_MOVIE_ID, movieID);
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_AUTHOR, rAuthor);
                newReview.put(MovieContract.MovieReviews.COLUMN_REVIEW_CONTENT, rContent);


                cvData[i] = newReview;

            }

            Log.v(LOG_TAG, "after cvData length: " + cvData.length);

            int inserted = 0;
            if (cvData.length > 0) {
                Uri movieUri = MovieContract.MovieReviews.buildMovieReviewsUri(movieID); // generate the trailer movie Uri

                inserted = context.getContentResolver().bulkInsert(movieUri, cvData);

                Log.v(LOG_TAG, inserted + " Rows added to db --- used uri:" + movieUri);
            }





        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error" + e);

        }

        return movieReviews;




    }





    public ArrayList<MovieItem_Reviews> fetchMovieReviews(String movieID, Context context) {

        String pageNumber = "1";

        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieID)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("page", pageNumber);


            String targetURL = builder.build().toString();
            Log.v(LOG_TAG, " target URL: " + targetURL);

            String requestData = getUrl(targetURL);

            return getMovieReviewsFromJSON(requestData, context);

        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException error: " + e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error: " + e);

        }

        return null;
    }





}
