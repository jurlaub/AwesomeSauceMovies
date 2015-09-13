package com.example.android.awesomesaucemovies;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

/**
 * Created by dev on 7/22/15.
 *
 *  stores movie information
 *
 *
 *  MovieItem ID = mID
 *
 */
public class MovieItem {

    private String mID;   // movie ID  = should be key
    private String mTitle;// title
    private String mOverview;// overview
    private String mReleaseDate;// release date
    private Double mVoteAvg;// vote average
    private Double mVoteCount;
    private Double mPopularity;// popularity
    private String mPosterPath;// poster url from Movie API
    private String mLocalImagePath;
    private Bitmap mImage;// image?  --> translate to storage location local on device

    private final String OVERVIEWISNULL = "Not Available.";


    public MovieItem(){

    }

    public MovieItem(String id){
        mID = id;

    }

    public String getmID() {
        return mID;
    }


    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {

        if (mOverview == null) {
            this.mOverview = OVERVIEWISNULL;
        }
        this.mOverview = mOverview;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mDate) {

        this.mReleaseDate = mDate;
    }


    public Bitmap getmImage() {
        return mImage;
    }

    public void setmImage(Bitmap mImage) {
        this.mImage = mImage;
    }

    public Double getmVoteAvg() {
        return mVoteAvg;
    }

    public void setmVoteAvg(Double mVoteAvg) {
        this.mVoteAvg = mVoteAvg;
    }

    public Double getmPopularity() {
        return mPopularity;
    }

    public void setmPopularity(Double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public void setmPopularity(String mPopularity) {
        this.mPopularity = Double.parseDouble(mPopularity);
    }


    public Double getmVoteCount() {
        return mVoteCount;
    }

    public void setmVoteCount(Double mVoteCount) {
        this.mVoteCount = mVoteCount;
    }
    public void setmVoteCount(String mVoteCount) {
        this.mVoteCount = Double.parseDouble(mVoteCount);
    }


    public String getmPosterPath() {

        return mPosterPath;
    }


    /*
        Input: API_KEY     // See MovieFragment Note: Passing API Key
        Output: null or assembled URI reference.
     */
    public Uri getPosterPathURL() {

        final String API_KEY = new API().getAPI();

        final String SCHEME = "http";
        final String AUTHORITY = "image.tmdb.org";
        final String PARTONE = "t";
        final String PARTTWO = "p";
        final String IMAGESIZE = "w185";

        String tmpPosterPath = mPosterPath;

        if (mPosterPath != null) {

            if (!mPosterPath.isEmpty()) {

                // MovieDatabase may prepend '/' to their path. Uri.Builder must have a way to
                // handle this. But not work investigating at this time.
                if (mPosterPath.startsWith("/")){
                    tmpPosterPath = mPosterPath.substring(1);
                }

                Uri.Builder builder = new Uri.Builder();
                builder.scheme(SCHEME)
                        .authority(AUTHORITY)
                        .appendPath(PARTONE)
                        .appendPath(PARTTWO)
                        .appendPath(IMAGESIZE)
                        .appendPath(tmpPosterPath)
                        .appendQueryParameter("api_key", API_KEY);

                return builder.build();


            }

        }


        Log.v("MI.getPosterPathURL", "mPosterPath is null or empty; returning null ");
        // Picasso expects null or a well formed URL
        return null;
    }



    public void setmPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

//    public void setmPosterPath(URL mPosterPath) {
//        this.mPosterPath = mPosterPath;
//    }

    public String getmLocalImagePath() {
        return mLocalImagePath;
    }

    public void setmLocalImagePath(String mLocalImagePath) {
        this.mLocalImagePath = mLocalImagePath;
    }
}
