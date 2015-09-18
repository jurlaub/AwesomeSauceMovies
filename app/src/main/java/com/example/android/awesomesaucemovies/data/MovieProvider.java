package com.example.android.awesomesaucemovies.data;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;

/**
 * Created by dev on 9/18/15.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;


    // search: popular, votes, favorites, Movie poster URL, Movie video URL, Movie reviews
    static final int MOVIE = 100;
    static final int MOVIE_WITH_POSTER_URL = 105;
    static final int MOVIE_WITH_TRAILER_URLS = 110;
    static final int MOVIE_WITH_REVIEWS = 115;
    static final int POPULAR_MOVIES = 200;
    static final int MOST_VOTES_MOVIES = 300;
    static final int FAVORITE_MOVIES = 400;


    // static db search strings
    private static final String sMovieListByMostPopular = MovieContract.

     /*      Need
                in search order table, find search id
                filter movielist entry table by search id
                return a cursor set of movie entries in the list

      */
     private Cursor getMoviesBySortOrder(Uri uri, String[] projection, String sortOrder) {

     }




    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // create a code for each URI
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE );
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_WITH_POSTER_URL);
        matcher.addURI(authority, MovieContract.PATH_SORT_LIST, POPULAR_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_SORT_LIST, MOST_VOTES_MOVIES);
        //matcher.addURI(authority, MovieContract.PATH_SORT_LIST, FAVORITE_MOVIES);

        return matcher;
    }




    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOST_VOTES_MOVIES:
                return MovieContract.MovieListEntry.CONTENT_TYPE;
            case POPULAR_MOVIES:
                return MovieContract.MovieListEntry.CONTENT_TYPE;
            case MOVIE_WITH_POSTER_URL:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOST_VOTES_MOVIES:
                retCursor = null;

            case POPULAR_MOVIES:
                retCursor = getMoviesBySortOrder();

            case MOVIE_WITH_POSTER_URL:
                retCursor = null;

            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }


}
