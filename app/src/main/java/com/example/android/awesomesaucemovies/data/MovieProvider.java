package com.example.android.awesomesaucemovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    static final int MOVIE_ID = 101;
    static final int MOVIE_WITH_POSTER_URL = 105;
    static final int MOVIE_WITH_TRAILER_URLS = 110;
    static final int MOVIE_WITH_REVIEWS = 115;
    static final int SORT_OPTIONS = 200;
    static final int SORT_SELECTION = 201;
    static final int POPULAR_MOVIES = 210;
    static final int MOST_VOTES_MOVIES = 220;
    static final int FAVORITE_MOVIES = 230;


    // static db search strings
    //private static final SQLiteQueryBuilder sMoviesBySortQueryBuilder;

//    static {
//
//        sMoviesBySortQueryBuilder = new SQLiteQueryBuilder();
//
//        sMoviesBySortQueryBuilder.setTables(
//                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
//                        MovieContract.MovieListEntry.TABLE_NAME + " ON " +
//                        MovieContract.MovieEntry.TABLE_NAME + "." +
//                        MovieContract.MovieEntry.COLUMN_MOVIE_KEY +
//                        " = " +
//                        MovieContract.MovieListEntry.TABLE_NAME + "." +
//                        MovieContract.MovieListEntry.COLUMN_MOVIE_KEY);
//
//        }

    private static final String sMovieByID = MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ? ";

//    // movieLists.sort_type = ?
//    private static final String sOrderedMovieListBySortType = MovieContract.MovieListEntry.TABLE_NAME +
//            "." + MovieContract.MovieListEntry.COLUMN_SORT + " = ? ";

     /*
        Filters rows (from combined MovieEntry & MovieEntryList) by Column_Sort (i.e. Sort Order)
      */
//     private Cursor getMoviesBySortOrder(Uri uri, String[] projection, String sortOrder) {
//
//         String sortCode = MovieContract.MovieListEntry.getSortOrderFromUri(uri);
//         Log.i("MovieProvider", "Uri(1) is:" + sortCode);
//
//         //String sortCodeToUse = MovieContract.SortOrderElements.SORT_POPULAR;
//
//         return sMoviesBySortQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                 projection,
//                 sOrderedMovieListBySortType,
//                 new String[] {"%" + sortCode} ,
//                 null,
//                 null,
//                 sortOrder
//                 );
//
//     }


    private Cursor getMovieByID(Uri uri) {
        String movieID = MovieContract.MovieEntry.getMovieIDFromUri(uri);
        Log.i("MovieContract", "ID is:" + movieID);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        return db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                sMovieByID,
                new String[] {"%" + movieID },
                null,
                null,
                null
                );



    }



    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // create a code for each URI
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE );   // all movies
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_ID );  // specific movie
        //matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_WITH_POSTER_URL);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST, SORT_OPTIONS);
//        matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST + "/*", SORT_SELECTION);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST + "/*", MOST_VOTES_MOVIES);
        //matcher.addURI(authority, MovieContract.PATH_MOVIE_LIST, FAVORITE_MOVIES);

        return matcher;
    }




    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

//            case SORT_SELECTION:
//                return MovieContract.MovieListEntry.CONTENT_TYPE;

            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }


    /*
        First request is a list of movie information
            >> Each movie item is a movie entry
            >> all items comprise a sorted list



        Next and subsequent requests
            NewSL - New JSON from MovieAPI
            OldSL - Existing data
            RemoveList = MovieEntries in OldSL that are not in NewSL

            for each element in NewSL:
                compare to OldSL



                >>> check sorted list,
                    >> if SL Movie_ID and New List Movie_ID are the same - update movie entry
                    >> if they are different, check Remove_List for movie_ID, if present
            >> else insert

          SQL Query?
             Unique elements in SL1 that are not in SL2 & SL3





       Plan 2
       Get movie entry into and out of a database (Insert, delete)
       test
       movie entry update
       test
       get movieSortedList into and out of the data base (Insert, delete)
       test


       Plan EasyCake:
       > 1 table
       > first iteration will be naive approach
       > MovieEntry Columns + Each sort method.
            > SortMethod columns will be positive numbers or -1. (-1 means not in sort order)

       New data in
       > update db - all sort order numbers are set to -1
       > for each new movieEntry
       >> if movieID is in DB, update relevant sort number column and update data
       >> if not in db, insert with sortnumber filled in









     */



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

//            case SORT_SELECTION:
//                retCursor = getMoviesBySortOrder(uri, projection, sortOrder);
//                break;

            case MOVIE_ID:  // I want one movie
                retCursor = getMovieByID(uri);
                break;

            case MOVIE:     // I want all movies
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

//            case SORT_SELECTION:
//                rowsUpdated = db.update(MovieContract.MovieListEntry.TABLE_NAME, values, selection, selectionArgs);
//                break;

            case MOVIE_ID:
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
        long _id;

        switch (match) {

//            case SORT_SELECTION:
//                _id = db.insert(MovieContract.MovieListEntry.TABLE_NAME, null, values);
//                if (_id > 0) {
//                    returnUri = MovieContract.MovieListEntry.buildMovieUri(_id);
//                } else {
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                }
//                break;

            case MOVIE_ID:
                 _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
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




        Plan:

        delete all

         delete a specific movie - used for deleting favorites
            really an update - remove the favorite designation from the sort list

         delete a sort list




     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";

        switch (match) {

//            case SORT_SELECTION:
//                // delete the whole sort list
//                rowsDeleted = db.delete(MovieContract.MovieListEntry.TABLE_NAME, selection, selectionArgs);
//                break;

            case MOVIE_ID:
                // for the case where a favorited movie is not in any  of the other lists
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;


    }


}
