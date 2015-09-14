package com.example.android.awesomesaucemovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.awesomesaucemovies.data.MovieContract.MovieEntry;
import com.example.android.awesomesaucemovies.data.MovieContract.MovieListEntry;
import com.example.android.awesomesaucemovies.data.MovieContract.SortOrderEntry;

/**
 * Created by dev on 9/13/15.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 0;

    static final String DATABASE_NAME = "movieLibrary.db";

    public MovieDBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_MOVIE_KEY + " TEXT PRIMARY KEY" +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT" +
                " );";


        final String SQL_CREATE_SEARCH_PREFERENCE_TABLE = "CREATE TABLE " + SortOrderEntry.TABLE_NAME + " (" +
                SortOrderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SortOrderEntry.COLUMN_SORT_NAME + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_MOVIES_LISTS_TABLE = "CREATE TABLE " + MovieListEntry.TABLE_NAME + " (" +
                MovieListEntry.COLUMN_SORT_KEY + " INTEGER NOT NULL" +
                MovieListEntry.COLUMN_RANK + " INTEGER NOT NULL" +
                MovieListEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL" +

                // Foreign key Movie ID
                " FOREIGN KEY (" + MovieListEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_KEY + "), " +

                // Foreign key Sort ID
                " FOREIGN KEY (" + MovieListEntry.COLUMN_SORT_KEY + ") REFERENCES " +
                SortOrderEntry.TABLE_NAME + " (" + SortOrderEntry._ID + "), " +

                // Primary key Composite
                " PRIMARY KEY (" +  MovieListEntry.COLUMN_SORT_KEY + ", " +
                                    MovieListEntry.COLUMN_RANK + ") " +
                " );";




        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_PREFERENCE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_LISTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SortOrderEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
