package com.example.android.awesomesaucemovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

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
    static final int SORTED_OPTIONS = 200;
    static final int POPULAR_MOVIES = 210;
    static final int MOST_VOTES_MOVIES = 220;
    static final int FAVORITE_MOVIES = 230;


    // static db search strings
    private static final SQLiteQueryBuilder sMoviesBySortQueryBuilder;

    static {

        sMoviesBySortQueryBuilder = new SQLiteQueryBuilder();

        sMoviesBySortQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieListEntry.TABLE_NAME + " ON " +
                        MovieContract.MovieEntry.TABLE_NAME + "." +
                        MovieContract.MovieEntry.COLUMN_MOVIE_KEY +
                        " = " +
                        MovieContract.MovieListEntry.TABLE_NAME + "." +
                        MovieContract.MovieListEntry.COLUMN_MOVIE_KEY);

        }

    // movieLists.sort_type = ?
    private static final String sOrderedMovieListBySortType = MovieContract.MovieListEntry.TABLE_NAME +
            "." + MovieContract.MovieListEntry.COLUMN_SORT + " = ? ";


     /*      Need
                in search order table, find search id
                filter movielist entry table by search id
                return a cursor set of movie entries in the list

      */
     private Cursor getMoviesBySortOrder(Uri uri, String[] projection, String sortOrder) {

         String sortCode = MovieContract.MovieListEntry.getSortOrderFromUri(uri);
         Log.i("MovieProvider", "Uri(1) is:" + sortCode);

         //String sortCodeToUse = MovieContract.SortOrderElements.SORT_POPULAR;

         return sMoviesBySortQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                 projection,
                 sOrderedMovieListBySortType,
                 new String[] {"%" + sortCode} ,
                 null,
                 null,
                 sortOrder
                 );

     }




    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // create a code for each URI
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE );
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_WITH_POSTER_URL);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST, SORTED_OPTIONS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST + "/*", POPULAR_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST + "/*", MOST_VOTES_MOVIES);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST, FAVORITE_MOVIES);

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
//            case SORTED_OPTIONS:
//                return
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
                retCursor = getMoviesBySortOrder(uri, projection, sortOrder);
                break;

            case POPULAR_MOVIES:
                retCursor = getMoviesBySortOrder(uri, projection, sortOrder);
                break;

            case MOVIE_WITH_POSTER_URL:
                // should be same as movie
                Log.i("query", "should drop into Movie");

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

    /*
        Update the device information with Webinformation
            > Sort order changes
                >> Movie rank could be adjusted/removed
                >> Movie could not be in the order (but be in the other lists)
            > Movie information changes
                >> update the movie entry


     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch(match) {
//
//            case SORTED_OPTIONS:
//

            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /*
        Insert to a specific sort list
        > add a movie by list

     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /*
        Delete a movie means the movie reference should also be deleted from the sort order.
        Individual deletes are most likely only possible if the movie was a favorite - did not exist
        in any other list and the user wants to delete. Otherwise the sortList should be used.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int rowsDeleted;
//
//        if (null == selection) selection = "1";
//
//        switch (match) {
////            case MOVIE:
////
////                break;
//            default:
////                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }

        return 0;

    }


}
