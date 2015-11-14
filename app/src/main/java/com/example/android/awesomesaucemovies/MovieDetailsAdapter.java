package com.example.android.awesomesaucemovies;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.awesomesaucemovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dev on 10/7/15.
 */
public class MovieDetailsAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieDetailsAdapter.class.getSimpleName();


    private static final int VIEW_TYPE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;

    // ---!! must change !!----
    private static final int VIEW_TYPE_COUNT = 3;  //  sum of above VIEW_TYPE_<BLANK> values



    private CheckBox mFavoriteCheckBox;

    Context mContext;
    //ArrayList mDetailItems;  // Note: this ArrayList mixes 3 object types - MovieItem, MovieItem_Video, MovieItem_Reviews
    String movie_ID;


    public MovieDetailsAdapter (Context context, Cursor c, int flags){
        super(context, c, flags);

        this.mContext = context;

        if (c != null) {
            movie_ID = c.getString(MovieDetailsFragment.COL_DETAIL_ID);
            Log.v(LOG_TAG, "The MovieID is " + movie_ID);
        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutID = -1;

        switch (viewType) {

            case VIEW_TYPE_DETAIL:
                layoutID = R.layout.list_movie_details;

                break;

            case VIEW_TYPE_TRAILER:
                layoutID = R.layout.list_trailer;

                break;

            case VIEW_TYPE_REVIEW:
                layoutID = R.layout.list_review;

                break;
            default:
                Log.e(LOG_TAG, "newView no VIEW_TYPE found, layoutID = " + layoutID);
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);



        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {


        int viewType = getItemViewType(cursor.getPosition());

        Log.v(LOG_TAG, "bindView is viewType:" + viewType);

        switch (viewType) {
            case VIEW_TYPE_DETAIL:

                //final MovieItem movieItem = (MovieItem) getItem(position);



                TextView title = (TextView) view.findViewById(R.id.movie_detail_title);
                title.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_TITLE));

                TextView overview = (TextView) view.findViewById(R.id.movie_detail_overview);
                overview.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_OVERVIEW));

                ImageView imageView = (ImageView) view.findViewById(R.id.movie_detail_poster);


                String moviePosterPath = cursor.getString(MovieDetailsFragment.COL_DETAIL_POSTER_PATH);
                Uri tmpPath = MovieContract.MovieEntry.buildMoviePosterUri(moviePosterPath);
                Picasso.with(mContext.getApplicationContext()).load(tmpPath).into(imageView);

                TextView popularity = (TextView) view.findViewById(R.id.movie_popularity);
                popularity.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_POPULARITY));

                TextView averageVote = (TextView) view.findViewById(R.id.movie_average_vote);
                averageVote.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_VOTE_AVG));

                TextView releaseDate = (TextView) view.findViewById(R.id.movie_release_year);
                releaseDate.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_RELEASE_DATE));


                mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.movie_favorite_button);
                int favorite = cursor.getInt(MovieDetailsFragment.COL_DETAIL_FAVORITE);
                Log.v(LOG_TAG, "Favorite box is " + favorite);
                if (favorite == 0){
                    mFavoriteCheckBox.setChecked(false);
                } else {
                    mFavoriteCheckBox.setChecked(true);
                }

//                mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.movie_favorite_button);
//                if(movieItem.ismFavorite()){
//
//                } else {
//
//                }
//
                mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int fav;
                        ContentValues modifyFavorite = new ContentValues();
                        if(isChecked){
                            Log.v(LOG_TAG, "(true) isChecked:" +isChecked);
                            fav = 1;
                        } else {
                            fav = 0;
                            Log.v(LOG_TAG, "(false) isChecked:" +isChecked);
                        }

                        modifyFavorite.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movie_ID);
                        modifyFavorite.put(MovieContract.MovieEntry.COLUMN_FAVORITE, fav);


                        mContext.getContentResolver().update(MovieContract.MovieEntry.buildMovieUri(movie_ID),
                               modifyFavorite,
                                null,
                                null);

                        }


                });



                break;

            case VIEW_TYPE_TRAILER:


                TextView vidTitle = (TextView) view.findViewById(R.id.list_item_trailer_text);
                vidTitle.setText(cursor.getString(MovieDetailsFragment.COL_TRAILER_TITLE));


                ImageButton playTrailer = (ImageButton) view.findViewById(R.id.list_item_trailer_button);
                playTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Log.v("Play Trailer", "Played Trailer " + movieVideo.getVid_name());


                        playYouTubeVideo(cursor.getString(MovieDetailsFragment.COL_TRAILER_URI));

                    }
                });

                break;

            case VIEW_TYPE_REVIEW:


                TextView reviewAuthor = (TextView) view.findViewById(R.id.list_item_review_author);
                reviewAuthor.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_AUTHOR));

                TextView reviewContent = (TextView) view.findViewById(R.id.list_item_review_content);
                reviewContent.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_CONTENT));



                break;
        }



    }

//    @Override
//    public int getCount(){
//        Log.v("MDA_getCount", "at count:" + mDetailItems.size());
//        return mDetailItems.size();
//    }

//    @Override
//    public Object getItem(int position) {
//        return mDetailItems.get(position);
//    }

//    @Override
//    public long getItemId(int position) {
//        Log.v("MDA_getItemID", "position" + position + " index of:" + mDetailItems.indexOf(getItem(position)) );
//        return mDetailItems.indexOf(getItem(position));
//    }


    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;

    }


    @Override
    public int getItemViewType(int position) {

        // get count of movie Trailers
        //int trailerCount = sMovieLibrary.getMovieItemTrailerCount(movie_ID);
        int trailerCount = mContext.getContentResolver().query(MovieContract.MovieTrailers.buildMovieTrailersUri(movie_ID),
                null,
                null,
                null,
                null)
                .getCount();
        Log.v(LOG_TAG, "getItemViewType TrailerCount = " + trailerCount);

        // get count of movie Reviews
        int reviewCount = mContext.getContentResolver().query(MovieContract.MovieReviews.buildMovieReviewsUri(movie_ID),
                null,
                null,
                null,
                null)
                .getCount();
        Log.v(LOG_TAG, "getItemViewType Review Count = " + reviewCount);



        if (position == 0) {
            return VIEW_TYPE_DETAIL;
        } else if (position <= trailerCount) {

            return VIEW_TYPE_TRAILER;
        } else if (position <= (trailerCount + reviewCount)) {

            return VIEW_TYPE_REVIEW;
        } else {

            Log.e(LOG_TAG, "getItemViewType error " + Integer.toString(trailerCount + reviewCount));
            return -1;
        }



    }



//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        int viewType = getItemViewType(position);
//
//        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        switch (viewType) {
//            case VIEW_TYPE_DETAIL:
//                if (convertView == null) {
//                    convertView = mInflater.inflate(R.layout.list_movie_details, parent, false);
//
//                    Log.v(LOG_TAG, "getView: Detail- position: " + Integer.toString(position));
//                }
//
//
////                final String API_KEY =  //// See MovieFragment Note: Passing API Key
//
//                //updateTrailers(movieID);
//
//
//                // TODO check type?
//                final MovieItem movieItem = (MovieItem) getItem(position);
//
//
//                TextView title = (TextView) convertView.findViewById(R.id.movie_detail_title);
//                title.setText(movieItem.getmTitle());
//
//                TextView overview = (TextView) convertView.findViewById(R.id.movie_detail_overview);
//                overview.setText(movieItem.getmOverview());
//
//                ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_detail_poster);
//
//                Picasso.with(mContext).load(movieItem.getPosterPathURL()).into(imageView);
//
//                TextView popularity = (TextView) convertView.findViewById(R.id.movie_popularity);
//                popularity.setText(movieItem.getmPopularity().toString());
//
//                TextView averageVote = (TextView) convertView.findViewById(R.id.movie_average_vote);
//                averageVote.setText(movieItem.getmVoteAvg().toString());
//
//                TextView releaseDate = (TextView) convertView.findViewById(R.id.movie_release_year);
//                releaseDate.setText(movieItem.getmReleaseDate());
//
//
//                mFavoriteCheckBox = (CheckBox) convertView.findViewById(R.id.movie_favorite_button);
//                if(movieItem.ismFavorite()){
//                    mFavoriteCheckBox.setChecked(true);
//                } else {
//                    mFavoriteCheckBox.setChecked(false);
//                }
//
//                mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        movieItem.setmFavorite(isChecked);
//                    }
//                });
//
//
//                break;
//
//            case VIEW_TYPE_TRAILER:
//
//
//
//                if (convertView == null) {
//                    convertView = mInflater.inflate(R.layout.list_trailer, parent, false);
//
//                    Log.v(LOG_TAG, "VIEW_TYPE_TRAILER - position: " + Integer.toString(position));
//                }
//
//                // TODO check type?
//                final MovieItem_Video movieVideo = (MovieItem_Video) getItem(position);
//
//                TextView vidTitle = (TextView) convertView.findViewById(R.id.list_item_trailer_text);
//                vidTitle.setText(movieVideo.getVid_name());
//
//
//                ImageButton playTrailer = (ImageButton) convertView.findViewById(R.id.list_item_trailer_button);
//                playTrailer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //Log.v("Play Trailer", "Played Trailer " + movieVideo.getVid_name());
//
//
//                        playYouTubeVideo(movieVideo.getVid_key());
//
//                    }
//                });
//
//                break;
//
//            case VIEW_TYPE_REVIEW:
//
//                if (convertView == null) {
//                    convertView = mInflater.inflate(R.layout.list_review, parent, false);
//
//                    Log.v(LOG_TAG, "VIEW_TYPE_REVIEW- position: " + Integer.toString(position));
//                }
//
//                MovieItem_Reviews movieReview = (MovieItem_Reviews) getItem(position);
//
//                TextView reviewAuthor = (TextView) convertView.findViewById(R.id.list_item_review_author);
//                reviewAuthor.setText(movieReview.getReviewAuthor());
//
//                TextView reviewContent = (TextView) convertView.findViewById(R.id.list_item_review_content);
//                reviewContent.setText(movieReview.getReviewContent());
//
//
//                break;
//
//
//
//            default:
//
//                Log.e(LOG_TAG, "getView type not found position:" + Integer.toString(position) + " viewType:" + Integer.toString(viewType));
//        }
//
//
//        return convertView;
//    }



    // example taken from stackoverflow.com/question/574195/android-youtube-app-play-video-intent
    private void playYouTubeVideo(String videoID) {
        boolean isIntentSafe;
        PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> activities;
        Intent intent;

        try{

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));

            // from Android developer documentation
            activities = packageManager.queryIntentActivities(intent, 0);
            isIntentSafe= activities.size() > 0;

            if(isIntentSafe) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mContext.startActivity(intent);
                Log.v(LOG_TAG, "Playing on the YouTube App");

                return;

            }

            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));


            activities = packageManager.queryIntentActivities(intent, 0);
            isIntentSafe= activities.size() > 0;

            if(isIntentSafe) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mContext.startActivity(intent);
                Log.v(LOG_TAG, "Playing in browser");

            }


        } catch (ActivityNotFoundException e) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));
//            mContext.startActivity(intent);
            Log.v(LOG_TAG, "Exception " + e);
        }
    }



}
