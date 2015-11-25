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

    // ---!! must change if more views are added !!----
    private static final int VIEW_TYPE_COUNT = 3;  //  sum of above VIEW_TYPE_<BLANK> values



    private CheckBox mFavoriteCheckBox;

    Context mContext;
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
    public void bindView(View view,  Context context,  Cursor cursor) {



        int viewType = getItemViewType(cursor.getPosition());

        Log.v(LOG_TAG, "bindView is viewType:" + viewType);

        switch (viewType) {
            case VIEW_TYPE_DETAIL:


                TextView title = (TextView) view.findViewById(R.id.movie_detail_title);
                title.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_TITLE));

                TextView overview = (TextView) view.findViewById(R.id.movie_detail_overview);

               // description set to null
                String tmpOverview = cursor.getString(MovieDetailsFragment.COL_DETAIL_OVERVIEW);

                 if (tmpOverview.equalsIgnoreCase("null")) {
                    Log.v(LOG_TAG, "tmpOverview.equalsIgnoreCase('null')" + tmpOverview);
                    tmpOverview = context.getString(R.string.empty_missing_description);
                }

                overview.setText(tmpOverview);




                ImageView imageView = (ImageView) view.findViewById(R.id.movie_detail_poster);

                // obtain the poster image using  Picasso
                String moviePosterPath = cursor.getString(MovieDetailsFragment.COL_DETAIL_POSTER_PATH);
                Uri tmpPath = MovieContract.MovieEntry.buildMoviePosterUri(moviePosterPath);
                Picasso.with(mContext.getApplicationContext()).load(tmpPath).into(imageView);

//              removed as the calculation is not clear and not required
//                TextView popularity = (TextView) view.findViewById(R.id.movie_popularity);
//                String popularityText = cursor.getString(MovieDetailsFragment.COL_DETAIL_POPULARITY);
//                popularity.setText(popularityText.substring(0, Math.min(popularityText.length(), 4))); // reduce the number of digits

                TextView averageVote = (TextView) view.findViewById(R.id.movie_average_vote);
                String aveVoteText = cursor.getString(MovieDetailsFragment.COL_DETAIL_VOTE_AVG);
                averageVote.setText(aveVoteText.concat(" / 10"));

                TextView releaseDate = (TextView) view.findViewById(R.id.movie_release_year);
                releaseDate.setText(cursor.getString(MovieDetailsFragment.COL_DETAIL_RELEASE_DATE));


                // Favorite Checkbox functionality and behavior
                mFavoriteCheckBox = (CheckBox) view.findViewById(R.id.movie_favorite_button);
                int favorite = cursor.getInt(MovieDetailsFragment.COL_DETAIL_FAVORITE);
                Log.v(LOG_TAG, "Favorite box is " + favorite);

                if (favorite == 0){
                    mFavoriteCheckBox.setChecked(false);
                } else {
                    mFavoriteCheckBox.setChecked(true);
                }

                mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        int fav;

                        if(isChecked){
                            Log.v(LOG_TAG, "(true) isChecked:" +isChecked);
                            fav = 1;
                        } else {
                            fav = 0;
                            Log.v(LOG_TAG, "(false) isChecked:" +isChecked);
                        }


                        // values to update MovieEntry
                        ContentValues modifyFavorite = new ContentValues();
                        modifyFavorite.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movie_ID);
                        modifyFavorite.put(MovieContract.MovieEntry.COLUMN_FAVORITE, fav);

                        // update checkbox
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

                // setting tag to obtain the trailer uri onClick
                playTrailer.setTag(cursor.getString(MovieDetailsFragment.COL_TRAILER_URI));
                playTrailer.setOnClickListener(new View.OnClickListener() {



                    @Override
                    public void onClick(View v) {

                        Log.v(LOG_TAG, "VIEW_TYPE_TRAILER uriSegment: " +  v.getTag() );

                        playYouTubeVideo((String) v.getTag());

                    }
                });

                break;



            case VIEW_TYPE_REVIEW:

                // set Author
                final TextView reviewAuthor = (TextView) view.findViewById(R.id.list_item_review_author);
                reviewAuthor.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_AUTHOR));

                final TextView reviewContent = (TextView) view.findViewById(R.id.list_item_review_content);
                reviewContent.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_CONTENT));



                reviewContent.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {






                         // expand or contract the review
                         if(reviewContent.getLineCount() > 4) {

                             reviewContent.setLines(4);
                             Log.v(LOG_TAG, "Clicked Review box, set maxLines to  4 : " + reviewContent.getLineCount());


                         } else {
                             reviewContent.setMaxLines(Integer.MAX_VALUE);


                             //reviewContent.setLines(Integer.MAX_VALUE);
                             //reviewContent.setMaxLines(2);
                             Log.v(LOG_TAG, "Clicked Review box, set maxlines to max value : " + reviewContent.getLineCount());

//
                         }


                         reviewContent.invalidate();

                     }
                                        }
                );


                break;
        }



    }

//
//    private class ExpandableTextView extends TextView {
//
//        private boolean mIsExpanded;
//
//
//        public ExpandableTextView(Context context){
//            super(context);
//            this.mIsExpanded = false;
//
//
//        }
//
//
//
//        protected boolean isExpanded(){
//            return mIsExpanded;
//        }
//
//        public final void toggle(){
//            toggle(this);
//        }
//
//        private final void toggle(ExpandableTextView tv) {
//            if (tv.isExpanded()) {
//                tv.collapse();
//            }
//        }
//
//    }



    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;

    }


    @Override
    public int getItemViewType(int position) {

        // get count of movie Trailers
        int trailerCount = 0;
        Cursor trailerCursor = mContext.getContentResolver().query(MovieContract.MovieTrailers.buildMovieTrailersUri(movie_ID),
                null,
                null,
                null,
                null);


        if (trailerCursor != null) {
            trailerCount = trailerCursor.getCount();
            trailerCursor.close();
            Log.v(LOG_TAG, "getItemViewType TrailerCount = " + trailerCount);
        }




        // get count of movie Reviews
        int reviewCount = 0;
        Cursor reviewCursor = mContext.getContentResolver().query(MovieContract.MovieReviews.buildMovieReviewsUri(movie_ID),
                null,
                null,
                null,
                null);


        if (reviewCursor != null) {
            reviewCount = reviewCursor.getCount();
            reviewCursor.close();
            Log.v(LOG_TAG, "getItemViewType Review Count = " + reviewCount);
        }





        // in case there are any less then 0
        if (trailerCount < 0) { trailerCount = 0; }
        if (reviewCount < 0){ reviewCount = 0; }




        // determine VIEW_TYPE, first position is always the detail view
        //
        // preconditions:
        //      trailerCount >= 0
        //      reviewCount >= 0
        if (position == 0) {
            return VIEW_TYPE_DETAIL;

        // determine Trailer Type
        } else if (position <= trailerCount) {
            return VIEW_TYPE_TRAILER;

        // determine the Review Type
        } else if (position <= (trailerCount + reviewCount)) {
            return VIEW_TYPE_REVIEW;


        } else {

            Log.e(LOG_TAG, "getItemViewType error " + Integer.toString(trailerCount + reviewCount));
            return -1;
        }



    }




    // used to play a YouTube video in a webbrowser or the YouTube app
    // example taken from stackoverflow.com/question/574195/android-youtube-app-play-video-intent
    private void playYouTubeVideo(String videoID) {
        boolean isIntentSafe;
        PackageManager packageManager = mContext.getPackageManager();
        List<ResolveInfo> activities;
        Intent intent;
        Uri trailerUri;

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

            trailerUri = Uri.parse("http://www.youtube.com/watch?v=" + videoID);
            intent = new Intent(Intent.ACTION_VIEW, trailerUri );


            activities = packageManager.queryIntentActivities(intent, 0);
            isIntentSafe = activities.size() > 0;

            if(isIntentSafe) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mContext.startActivity(intent);
                Log.v(LOG_TAG, "Playing in browser, uri: " + trailerUri);

            }



        } catch (ActivityNotFoundException e) {

            Log.v(LOG_TAG, "Exception " + e);
        }
    }



}
