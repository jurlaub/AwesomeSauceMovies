package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dev on 11/8/15.
 */

// will consider making this a separate class in order to simplify maintenance - at a later time.
public class MovieAdapter extends ArrayAdapter<MovieItem> {


    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;


    public MovieAdapter(Context context, ArrayList<MovieItem> movies) {
        super(context, 0, movies);
        this.mContext = context;
        Log.v(LOG_TAG, "MovieAdapter Constructor 1");
    }

//        public MovieAdapter ( Context context, int resourceID, ArrayList<MovieItem> movies ) {
//            super(context, resourceID, movies);
//            //iContext = context;
//            Log.v(LOG_TAG, "MovieAdapter Constructor 2");
//        }


    @Override
    public View getView(int position, View convertView, ViewGroup parent ){

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
            convertView = mInflater.inflate(R.layout.list_item_movie, parent, false);

            Log.v(LOG_TAG, "convertView == null; position: " + Integer.toString(position));
        }

        MovieItem m = getItem(position);

        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_image);

        // MovieItem builds the PosterPath url, if empty or null returns null.
        Uri tmpPath = m.getPosterPathURL();
        Picasso.with(getContext()).load(tmpPath).into(image);


        Log.v(LOG_TAG, "in MovieAdapter " + m.getmTitle() + " at position" + Integer.toString(position));

        return convertView;
    }

}


