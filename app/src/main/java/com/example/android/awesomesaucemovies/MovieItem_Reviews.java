package com.example.android.awesomesaucemovies;

/**
 * Created by dev on 10/4/15.
 *
 * NOTE: these are not saved
 *
 */
public class MovieItem_Reviews {

    private String movieID; // movie id

    private String reviewID; //  review id
    private String reviewLanguage;
    private String reviewAuthor;
    private String reviewContent;
    private String reviewUrl;

//    public MovieItem_Reviews(String reviewID, String movieID ) {
//        this.movieID = movieID;
//        this.reviewID = reviewID;
//    }

    public MovieItem_Reviews(String rID, String rAuthor, String rContent ) {
        //this.movieID = mID;
        this.reviewID = rID;
        this.reviewAuthor = rAuthor;
        this.reviewContent = rContent;

    }

//
//    public MovieItem_Reviews(String rID, String rAuthor, String rContent, String mID ) {
//        //this.movieID = mID;
//        this.reviewID = rID;
//        this.reviewAuthor = rAuthor;
//        this.reviewContent = rContent;
//    }



    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getReviewLanguage() {
        return reviewLanguage;
    }

    public void setReviewLanguage(String reviewLanguage) {
        this.reviewLanguage = reviewLanguage;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.reviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }
}
