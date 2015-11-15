package com.example.android.awesomesaucemovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.awesomesaucemovies.MovieFragment;

/**
 * Created by dev on 9/18/15.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;
    private final String LOG_TAG = MovieProvider.class.getSimpleName();


    // search: popular, votes, favorites, Movie poster URL, Movie video URL, Movie reviews
    static final int MOVIE = 100;
    static final int MOVIE_ID = 101;
    static final int MOVIE_WITH_TRAILER_URLS = 102;
    static final int MOVIE_WITH_REVIEWS = 103;
    static final int FAVORITE_MOVIES = 200;
    static final int FAVORITE_MOVIE_WITH_ID = 201;






    private static final String sMovieByID = MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry.COLUMN_MOVIE_KEY + " = ? ";


    private Cursor getMovieByID(Uri uri, String[] columns) {
        String movieID = MovieContract.MovieEntry.getMovieIDFromUri(uri);
        Log.i("MovieContract", "ID is:" + movieID);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        return db.query(MovieContract.MovieEntry.TABLE_NAME,
                columns,
                sMovieByID,
                new String[] { movieID },
                null,
                null,
                null
                );



    }



    // return all trailer Uri related data in db filtered by MovieID
    private Cursor getTrailerUriByMovieID(Uri uri, String[] columns) {
        String movieID = MovieContract.MovieTrailers.getMovieIDFromUriWithTrailer(uri);
        Log.v("getTrailersByMovieID", " ID is:" + movieID);

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();


        String selection = MovieContract.MovieTrailers.COLUMN_MOVIE_ID + "=?";

        return db.query(MovieContract.MovieTrailers.TABLE_NAME,
                columns,
                selection,
                new String[] {movieID},
                null,
                null,
                null
                );
    }




    // return all movie reviews uri related data in db filtered by MovieID
    private Cursor getReviewsUriByMovieID(Uri uri, String[] columns) {
        String movieID = MovieContract.MovieReviews.getMovieIDFromUriWithReview(uri);
        Log.v("getReviewsByMovieID", " ID is:" + movieID);

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        String selection = MovieContract.MovieReviews.COLUMN_MOVIE_ID + "=?";

        return db.query(MovieContract.MovieReviews.TABLE_NAME,
                columns,
                selection,
                new String[] {movieID},
                null,
                null,
                null);
    }



    // return a cursor of all the movies marked Favorite in the MovieEntry DB
    private Cursor getFavoriteMovies(String[] columns){

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        return db.query(MovieContract.MovieFavorites.TABLE_NAME,
                columns,
                MovieContract.MovieFavorites.WHERE_FAVORITE_CLAUSE,
                new String[] {Integer.toString(MovieContract.MovieFavorites.VAL_IS_FAVORITE)},
                null,
                null,
                null);

    }




    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // create a code for each URI
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE );   // all sort preference movies
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", MOVIE_ID );  // specific movie
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*/" + MovieContract.PATH_MOVIE_TRAILERS, MOVIE_WITH_TRAILER_URLS);  // all trailers (if only one is this a problem with return type??
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*/" + MovieContract.PATH_MOVIE_REVIEWS, MOVIE_WITH_REVIEWS);    // all reviews
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITE_MOVIES); // all favorite movies
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/*", FAVORITE_MOVIE_WITH_ID); // specific Movie


        return matcher;
    }




    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case FAVORITE_MOVIE_WITH_ID:
                return MovieContract.MovieFavorites.CONTENT_ITEM_TYPE;

            case FAVORITE_MOVIES:
                return MovieContract.MovieFavorites.CONTENT_TYPE;

            case MOVIE_WITH_REVIEWS:
                return MovieContract.MovieReviews.CONTENT_TYPE;

            case MOVIE_WITH_TRAILER_URLS:
                return MovieContract.MovieTrailers.CONTENT_TYPE;

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






    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case FAVORITE_MOVIES:  // I want all favorite movies
                retCursor = getFavoriteMovies(projection);

                break;


            case MOVIE_WITH_REVIEWS:  // I want all movie reviews
                retCursor = getReviewsUriByMovieID(uri, projection);
                break;


            case MOVIE_WITH_TRAILER_URLS:  // I want all movie Trailers
                retCursor = getTrailerUriByMovieID(uri, projection);
                break;


            case MOVIE_ID:  // I want one movie
                retCursor = getMovieByID(uri, projection);
                break;


            case MOVIE:     // I want all movies

                Log.v("Provider_Query", "In MOVIE");

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.v("Provider_Query", "In MOVIE with " + retCursor.getCount() + " number of entries");

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


            case MOVIE_ID:

                String movieID = MovieContract.MovieEntry.getMovieIDFromUri(uri);
                selection = MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "=?";

                Log.v(LOG_TAG, values.toString());
                Log.v(LOG_TAG, " integer value added = " +values.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVORITE));


                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, new String[] {movieID});
                Log.v(LOG_TAG, "updated movie:" + movieID + " and set the Favorite Button. " + rowsUpdated + " rows updated");



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
        > rarely used


     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;


            switch (match) {



                case MOVIE_ID:
                    String tmpID = MovieContract.MovieEntry.getMovieIDFromUri(uri);

                    _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                    if(_id > 0) {

                        Log.v("MovieProvider Insert", _id + " movie added; id:" + tmpID);
                        returnUri = MovieContract.MovieEntry.buildMovieUri(tmpID);
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




    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {

            case MOVIE_WITH_REVIEWS:
                // insert all reviews

                db.beginTransaction();

                try {
                    // delete all old entries
                    int deletedCount = delete(uri, null, null);
                    Log.v(LOG_TAG, deletedCount + " deleted Movie Review rows");

                    // add new values into DB
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieReviews.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;



            case MOVIE_WITH_TRAILER_URLS:
                // insert all trailers

                db.beginTransaction();

                try {
                    // delete all old entries
                    int deletedCount = delete(uri, null, null);
                    Log.v(LOG_TAG, deletedCount + " deleted Movie Trailer rows");

                    // add new values into DB
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieTrailers.TABLE_NAME, null, value);
                        if (_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;




            case MOVIE:
                /*
                BulkInsert MOVIE: Update existing MovieEntry database algorithm:

                Goal is to compare A (Old MovieEntries -cursor-) to B ( New MovieEntries - ContentValues[] -).

                Step0: Set All Favorites COLUMN_NORMAL_RANK to -1; Set COLUMN_SORT_TYPE to null;
                Step1: Set all A to Delete = 1 if not Favorite,
                Step2: For every B, if exists in A, then update (select) A values with B, Set delete to 0, (don't disturb Favorite)
                Step3: If B not exist in A, Insert as new,

                Step4: Run Delete Process.


                 */
                db.beginTransaction();

                try {

                    // whereClause = "favorite=?"
                    final String WHERE_FAVORITE = MovieContract.MovieEntry.COLUMN_FAVORITE + "=?";
                    final String WHERE_MOVIEID = MovieContract.MovieEntry.COLUMN_MOVIE_KEY + "=?";
                    final String WHERE_MARKED_DELETE = MovieContract.MovieEntry.COLUMN_DELETE + "=?";





                    // --------- Step0  - Prepare Favorite MovieEntries-------- //

                    int rowsOmittedFromRank;
                    ContentValues omitValues = new ContentValues();
                    omitValues.put(MovieContract.MovieEntry.COLUMN_NORMAL_RANK, MovieContract.MovieEntry.VAL_OMIT_FROM_RANK);
                    omitValues.putNull(MovieContract.MovieEntry.COLUMN_SORT_TYPE);

                    rowsOmittedFromRank = db.update(MovieContract.MovieEntry.TABLE_NAME,
                            omitValues,
                            WHERE_FAVORITE,
                            new String[] {Integer.toString(MovieContract.MovieEntry.VAL_IS_FAVORITE)});

                    Log.v(LOG_TAG, "Step0 of update MovieEntry db " + rowsOmittedFromRank + " rows omitted from Rank view");





                    // --------- Step1 - Prepare all other existing MovieEntries-------- //

                    int rowsMarkedForDelete;

                    // MovieEntry Columns to modify for Step1
                    ContentValues deleteValues = new ContentValues();
                    deleteValues.put(MovieContract.MovieEntry.COLUMN_DELETE, MovieContract.MovieEntry.VAL_DELETE_ENTRY); // Mark for deletion


                    // Mark all existing entries for deletion if not a favorite
                    rowsMarkedForDelete = db.update(MovieContract.MovieEntry.TABLE_NAME,
                            deleteValues,
                            WHERE_FAVORITE,
                            new String[] {Integer.toString(MovieContract.MovieEntry.VAL_IS_NOT_FAVORITE)});

                    Log.v(LOG_TAG, "Step1 of update MovieEntry db " + rowsMarkedForDelete + " rows marked for deletion");






                    // --------- Step2  &  Step3 - update / add new Values-------- //

                    for (ContentValues value: values) {

                        int rowUpdated = 0;
                        long rowAdded = 0;

                        String valueMovieID = value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_KEY); // get the movieID

                         Cursor entryExists = db.query(MovieContract.MovieEntry.TABLE_NAME,
                                 MovieFragment.UPDATE_COLUMNS,
                                 WHERE_MOVIEID,
                                 new String[] {valueMovieID},
                                 null,
                                 null,
                                 null);



                        // if true: B is in A;
                        if (entryExists != null && entryExists.getCount() > 0) {

                            entryExists.moveToFirst();
                            Log.v(LOG_TAG, "Step2, entryExists: " + entryExists.getString(MovieFragment.COL_UPDATE_MOVIE_ID) + " == " + valueMovieID);


                            // capture existing record favorite value
                            Log.v(LOG_TAG, "Step2, before value entry count:" + value.size() + " favorite: " + value.getAsString(MovieContract.MovieEntry.COLUMN_FAVORITE));
                            value.put(MovieContract.MovieEntry.COLUMN_FAVORITE, entryExists.getString(MovieFragment.COL_UPDATE_FAVORITE));
                            Log.v(LOG_TAG, "Step2, after value entry count:" + value.size() + " favorite: " + value.getAsString(MovieContract.MovieEntry.COLUMN_FAVORITE));




                            // update the existing value in the DB
                            rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                                    value,
                                    WHERE_MOVIEID,
                                    new String [] {valueMovieID});

                            Log.v(LOG_TAG,"Step2, where entry is updates, " + rowUpdated + " entries updated, movieID: " + valueMovieID);


                            returnCount++;
                        } else {

                            // add new value to db.
                            rowAdded = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                                   null, value);
                            Log.v(LOG_TAG,"Step3, where entry is insert, " + rowAdded + " entry added, movieID: " + valueMovieID);

                            returnCount++;
                        }



                        entryExists.close();

                    }


                    // --------- Step4 - Delete the old MovieEntries-------- //

                    // delete entries marked as delete
                    int rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                            WHERE_MARKED_DELETE,
                            new String[] {Integer.toString(MovieContract.MovieEntry.VAL_DELETE_ENTRY)});

                    Log.v(LOG_TAG, "Step4, " + rowsDeleted + " entries deleted");




                    db.setTransactionSuccessful();



                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;



            default:
                return super.bulkInsert(uri, values);

        }
    }







    /*
        Mainly used for deleting Review and Trailer entries.

        The other delete functions are handled with BulkInsert related to importing new data

     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";

        String movieID;
        String whereClause;
        String[] whereArgs;

        switch (match) {


            case MOVIE_WITH_REVIEWS:
                // delete all review entries with related movieID
                movieID = MovieContract.MovieReviews.getMovieIDFromUriWithReview(uri);
                whereClause = MovieContract.MovieReviews.COLUMN_MOVIE_ID + "=?";
                whereArgs = new String[]{movieID};

                rowsDeleted = db.delete(MovieContract.MovieReviews.TABLE_NAME, whereClause, whereArgs);

                break;


            case MOVIE_WITH_TRAILER_URLS:
                // delete all trailer entries with related movieID
                movieID = MovieContract.MovieTrailers.getMovieIDFromUriWithTrailer(uri);
                whereClause = MovieContract.MovieTrailers.COLUMN_MOVIE_ID + "=?";
                whereArgs = new String[]{movieID};

                rowsDeleted = db.delete(MovieContract.MovieTrailers.TABLE_NAME, whereClause, whereArgs);

                break;


            case MOVIE:
                //selection = "1"; // so that the number of rows deleted will be returned.
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

                break;


            case MOVIE_ID:
                // for individual movie deletions
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
