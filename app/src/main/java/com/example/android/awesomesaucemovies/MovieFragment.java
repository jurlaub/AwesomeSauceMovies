package com.example.android.awesomesaucemovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private ArrayAdapter<String> mMovieAdapter;
    private final String API_KEY = "c422814518841cc4217951ad333a15f4";


    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] dList = {"pepper", "Die Hard", "Right Round", "Holder", "Flo Rida", "Shawtie"};

        mMovieAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.list_item_movie, R.id.list_item_movie_textview );

        for(String s: dList) {
            mMovieAdapter.add(s);
        }


        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(mMovieAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CharSequence text = mMovieAdapter.getItem(position);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(v.getContext(), text + " " + position, duration ).show();
            }


        });

        return rootView;
    }


    @Override
    public void onStart(){
        super.onStart();
        updateMovie();


    }

    private void updateMovie() {

        FetchMovieTask movieTask = new FetchMovieTask();

        movieTask.execute();
    }


    public class FetchMovieTask extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        public FetchMovieTask(){

        }


//        private String[] getMovieDataFromJSON() {
//
//
//        }


        @Override
        protected String doInBackground(Void... urls){

            // here so they can be closed in the finally block if connection error
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // raw JSON response
            String movieJSONStr;



            try {

                // URL builder
                String testUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&page=2&api_key=c422814518841cc4217951ad333a15f4";

                URL url = new URL(testUrl);


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

                Log.v(LOG_TAG, movieJSONStr);

                return movieJSONStr;



            } catch (IOException e) {
                Log.e(LOG_TAG, "IO Error ", e);
                return null;

            }
//            catch (JSONException e) {
//                Log.e(LOG_TAG, "JSONException", e);
//                return null;
//
//            }
                finally {
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


    }



}
