package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.awesomesaucemovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by dev on 11/8/15.
 */

// will consider making this a separate class in order to simplify maintenance - at a later time.
public class MovieAdapter extends CursorAdapter{


    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;



    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
        //Log.v(LOG_TAG, "MovieAdapter ");
    }

//        public MovieAdapter ( Context context, int resourceID, ArrayList<MovieItem> movies ) {
//            super(context, resourceID, movies);
//            //iContext = context;
//            Log.v(LOG_TAG, "MovieAdapter Constructor 2");
//        }




    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        ImageView image = (ImageView) view.findViewById(R.id.list_item_movie_image);



        String moviePosterPath = cursor.getString(MovieFragment.COL_MOVIE_POSTER_PATH);
        Uri tmpPath = MovieContract.MovieEntry.buildMoviePosterUri(moviePosterPath);
        Picasso.with(context.getApplicationContext()).load(tmpPath).into(image);

        Log.v(LOG_TAG, "in MovieAdapter " + cursor.getString(MovieFragment.COL_MOVIE_TITLE) + " at position " + cursor.getPosition());


    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent ){
//
//        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (convertView == null) {
//
//            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
//            convertView = mInflater.inflate(R.layout.list_item_movie, parent, false);
//
//            Log.v(LOG_TAG, "convertView == null; position: " + Integer.toString(position));
//        }
//
//        MovieItem m = getItem(position);
//
//        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_movie_image);
//
//        // MovieItem builds the PosterPath url, if empty or null returns null.
//        Uri tmpPath = m.getPosterPathURL();
//        Picasso.with(getContext()).load(tmpPath).into(image);
//
//
//        Log.v(LOG_TAG, "in MovieAdapter " + m.getmTitle() + " at position" + Integer.toString(position));
//
//        return convertView;
//    }

}


