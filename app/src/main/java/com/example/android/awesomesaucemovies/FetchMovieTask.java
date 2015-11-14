//package com.example.android.awesomesaucemovies;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * Created by dev on 9/12/15.
// */
//          // PostExecute updates MovieLibrary - the sort order stored aligns with the
//            // contents of MovieLibrary mMovieItems.
//            String preferenceUsedInRequest = urls[0];
//
//
//            Uri.Builder builder = new Uri.Builder();
//            builder.scheme("http")
//                    .authority("api.themoviedb.org")
//                    .appendPath("3")
//                    .appendPath("discover")
//                    .appendPath("movie")
//                    .appendQueryParameter("sort_by", searchParameter)
//                    .appendQueryParameter("api_key", API_KEY);
//
//
//            URL url = new URL(builder.build().toString());
//            Log.v(LOG_TAG, " URL: " + url);
//
//
//
//
//            // Connection request
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//
//
//            // read input stream into string
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//            if(inputStream == null){
//                return null;
//            }
//
//            reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while((line = reader.readLine()) != null) {
//                // newline for debug readabiliy
//                buffer.append(line + "\n");
//            }
//
//
//            if(buffer.length() == 0) {
//                // stream is empty
//                return null;
//            }
//
//            movieJSONStr = buffer.toString();
//
//
//            Log.i(LOG_TAG, "urlConnection opened and data returned");
//
//            getMovieDataFromJSON(movieJSONStr, searchParameter, preferenceUsedInRequest);
//
//
//
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "IO Error ", e);
//            return null;
//
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "JSONException", e);
//            return null;
//
//        } finally {
//            if(urlConnection != null) {
//                urlConnection.disconnect();
//
//                Log.i(LOG_TAG, "urlConnection closed");
//            }
//
//            if(reader != null) {
//                try {
//                    reader.close();
//
//                } catch (final IOException e) {
//                    Log.e(LOG_TAG, "Error Closing stream", e);
//                }
//            }
//        }
//
//        return null;
//
//
//
//    }
//
////    @Override
////    protected void onPostExecute() {
////
////
////
////
////
////
////    }
//
//
//}
//
//
//
