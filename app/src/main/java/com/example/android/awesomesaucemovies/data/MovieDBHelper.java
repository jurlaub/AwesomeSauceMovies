package com.example.android.awesomesaucemovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.awesomesaucemovies.data.MovieContract.MovieEntry;
import com.example.android.awesomesaucemovies.data.MovieContract.MovieReviews;
import com.example.android.awesomesaucemovies.data.MovieContract.MovieTrailers;
//import com.example.android.awesomesaucemovies.data.MovieContract.SortOrderEntry;

/**
 * Created by dev on 9/13/15.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;

    static final String DATABASE_NAME = "movieLibrary.db";

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String DOUBLE_TYPE = " REAL";  // may not be correct
    private static final String INTEGER_TYPE = " INTEGER";


    public MovieDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // movieEntries table -- stores all movie entry data
        final String SQL_CREATE_MOVIES_TABLE = CREATE_TABLE + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_MOVIE_KEY + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP +
                MovieEntry.COLUMN_NORMAL_RANK + INTEGER_TYPE + COMMA_SEP +
                MovieEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieEntry.COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                MovieEntry.COLUMN_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                MovieEntry.COLUMN_POPULARITY + TEXT_TYPE + COMMA_SEP +
                MovieEntry.COLUMN_VOTE_AVG + DOUBLE_TYPE + COMMA_SEP +
                MovieEntry.COLUMN_POSTER_PATH + TEXT_TYPE  +  ");";



        //MovieTrailers table -- stores all trailer related data
        final String SQL_CREATE_MOVIE_TRAILERS_TABLE = CREATE_TABLE + MovieTrailers.TABLE_NAME + " (" +
                MovieTrailers.COLUMN_TRAILER_KEY + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +//https://www.sqlite.org/autoinc.html
                MovieTrailers.COLUMN_TRAILER_API_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieTrailers.COLUMN_MOVIE_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_LANGUAGE + TEXT_TYPE + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_URI + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_NAME + TEXT_TYPE + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_SITE + TEXT_TYPE + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_RESOLUTION + INTEGER_TYPE + COMMA_SEP +
                MovieTrailers.COLUMN_TRAILER_TYPE + TEXT_TYPE + ");";


        final String SQL_CREATE_MOVIE_REVIEWS_TABLE = CREATE_TABLE + MovieReviews.TABLE_NAME + " (" +
                MovieReviews.COLUMN_REVIEW_KEY + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                MovieReviews.COLUMN_REVIEW_API_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieReviews.COLUMN_MOVIE_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                MovieReviews.COLUMN_REVIEW_AUTHOR + TEXT_TYPE + COMMA_SEP +
                MovieReviews.COLUMN_REVIEW_CONTENT + TEXT_TYPE + ");";

//        final String SQL_CREATE_SEARCH_PREFERENCE_TABLE = "CREATE TABLE " + SortOrderEntry.TABLE_NAME + " (" +
//                SortOrderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                SortOrderEntry.COLUMN_SORT_NAME + " TEXT NOT NULL" +
//                " );";
//
//        final String SQL_CREATE_MOVIES_LISTS_TABLE = "CREATE TABLE " + MovieListEntry.TABLE_NAME + " (" +
//        MovieListEntry.COLUMN_SORT + " TEXT NOT NULL" +
//                MovieListEntry.COLUMN_RANK + " INTEGER NOT NULL" +
//                MovieListEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL" +
//
//                // Foreign key Movie ID
//                " FOREIGN KEY (" + MovieListEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
//                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_KEY + "), " +
//
//                // Foreign key Sort ID
////                " FOREIGN KEY (" + MovieListEntry.COLUMN_SORT_KEY + ") REFERENCES " +
////                SortOrderEntry.TABLE_NAME + " (" + SortOrderEntry._ID + "), " +
//
//                // Primary key Composite
//                " PRIMARY KEY (" +  MovieListEntry.COLUMN_SORT + ", " +
//                MovieListEntry.COLUMN_RANK + ") " +
//                " );";
//
//


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
//        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_PREFERENCE_TABLE);
       // sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_LISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieTrailers.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieReviews.TABLE_NAME);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SortOrderEntry.TABLE_NAME);
       // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
