package com.example.android.awesomesaucemovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dev on 10/4/15.
 *
    Android Programming used as a guide for MovieFetcher.

 Hardy, Brian; Phillips, Bill (2013-04-09). Android Programming: The Big Nerd Ranch Guide (Big Nerd Ranch Guides) Pearson Education. Kindle Edition.
 */
public class MovieFetcher {

    private final String LOG_TAG = MovieFetcher.class.getSimpleName();

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
    }
}
