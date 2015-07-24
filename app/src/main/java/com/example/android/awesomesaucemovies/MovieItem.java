package com.example.android.awesomesaucemovies;

import java.util.Date;

/**
 * Created by dev on 7/22/15.
 *
 *  stores movie information
 *
 *
 *
 *
 */
public class MovieItem {

    private String mID;   // movie ID  = should be key
    private String mTitle;// title
    private String mOverview;// overview
    private Date mReleaseDate;// release date
    private Double mVoteAvg;// vote average
    private Double mPopularity;// popularity
    private String mURL;// poster url from Movie API
    private String mLocalImagePath;
    // image?  --> translate to storage location local on device


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
        this.mOverview = mOverview;
    }

//    public Date getmReleaseDate() {
//        return mReleaseDate;
//    }

//    public void setmReleaseDate(String mDate) {
//
//        this.mReleaseDate = mDate;
//    }

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

    public String getmURL() {
        return mURL;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

//    public void setmURL(URL mURL) {
//        this.mURL = mURL;
//    }

    public String getmLocalImagePath() {
        return mLocalImagePath;
    }

    public void setmLocalImagePath(String mLocalImagePath) {
        this.mLocalImagePath = mLocalImagePath;
    }
}
