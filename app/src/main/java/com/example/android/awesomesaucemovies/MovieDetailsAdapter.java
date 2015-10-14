package com.example.android.awesomesaucemovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dev on 10/7/15.
 */
public class MovieDetailsAdapter extends BaseAdapter {

    private final String LOG_TAG = MovieDetailsAdapter.class.getSimpleName();

    private final static String API_KEY = new API().getAPI();

    private static final int VIEW_TYPE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;

    // ---!! must change !!----
    private static final int VIEW_TYPE_COUNT = 3;  //  sum of above VIEW_TYPE_<BLANK> values


    private MovieLibrary sMovieLibrary;

    Context mContext;
    ArrayList mDetailItems;  // Note: this ArrayList mixes 3 object types - MovieItem, MovieItem_Video, MovieItem_Reviews
    String movie_ID;


    public MovieDetailsAdapter (Context c, ArrayList a){
        this.mContext = c;
        this.mDetailItems = a;

        if (this.mDetailItems != null) {
            MovieItem m = (MovieItem)this.mDetailItems.get(0);
            this.movie_ID = m.getmID();
        }


        sMovieLibrary = MovieLibrary.get(c);

    }

    @Override
    public int getCount(){
        return mDetailItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDetailItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDetailItems.indexOf(getItemId(position));
    }


    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;

    }


    @Override
    public int getItemViewType(int position) {

        // get count of movie Trailers
        int trailerCount = sMovieLibrary.getMovieItemTrailerCount(movie_ID);
        // get count of movie Reviews
        int reviewCount = sMovieLibrary.getMovieItemReviewCount(movie_ID);


        // TODO
        // add the other two types to the decision flow
        if (position == 0) {
            return VIEW_TYPE_DETAIL;
        } else if (position <= trailerCount) {

            return VIEW_TYPE_TRAILER;
        } else if (position <= (trailerCount + reviewCount)) {

            return VIEW_TYPE_REVIEW;
        } else {

            Log.e(LOG_TAG, "getItemViewType error" + Integer.toString(trailerCount + reviewCount));
            return -1;
        }



    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (viewType) {
            case VIEW_TYPE_DETAIL:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_movie_details, parent, false);

                    Log.v(LOG_TAG, "getView: Detail- position: " + Integer.toString(position));
                }


//                final String API_KEY =  //// See MovieFragment Note: Passing API Key

                //updateTrailers(movieID);


                // TODO check type?
                MovieItem movieItem = (MovieItem) getItem(position);


                TextView title = (TextView) convertView.findViewById(R.id.movie_detail_title);
                title.setText(movieItem.getmTitle());

                TextView overview = (TextView) convertView.findViewById(R.id.movie_detail_overview);
                overview.setText(movieItem.getmOverview());

                ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_detail_poster);

                Picasso.with(mContext).load(movieItem.getPosterPathURL(API_KEY)).into(imageView);

                TextView popularity = (TextView) convertView.findViewById(R.id.movie_popularity);
                popularity.setText(movieItem.getmPopularity().toString());

                TextView averageVote = (TextView) convertView.findViewById(R.id.movie_average_vote);
                averageVote.setText(movieItem.getmVoteAvg().toString());

                TextView releaseDate = (TextView) convertView.findViewById(R.id.movie_release_year);
                releaseDate.setText(movieItem.getmReleaseDate());


                break;

            case VIEW_TYPE_TRAILER:



                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_trailer, parent, false);

                    Log.v(LOG_TAG, "VIEW_TYPE_TRAILER - position: " + Integer.toString(position));
                }

                // TODO check type?
                MovieItem_Video movieVideo = (MovieItem_Video) getItem(position);

                TextView vidTitle = (TextView) convertView.findViewById(R.id.list_item_trailer_text);
                vidTitle.setText(movieVideo.getVid_name());

                break;

            case VIEW_TYPE_REVIEW:

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list_review, parent, false);

                    Log.v(LOG_TAG, "VIEW_TYPE_REVIEW- position: " + Integer.toString(position));
                }

                MovieItem_Reviews movieReview = (MovieItem_Reviews) getItem(position);

                TextView reviewAuthor = (TextView) convertView.findViewById(R.id.list_item_review_author);
                reviewAuthor.setText(movieReview.getReviewAuthor());

                TextView reviewContent = (TextView) convertView.findViewById(R.id.list_item_review_content);
                reviewContent.setText(movieReview.getReviewContent());


                break;



            default:

                Log.e(LOG_TAG, "getView type not found position:" + Integer.toString(position) + " viewType:" + Integer.toString(viewType));
        }


        return convertView;
    }
}
