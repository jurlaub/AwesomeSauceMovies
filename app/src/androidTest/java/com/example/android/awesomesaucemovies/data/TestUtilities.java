package com.example.android.awesomesaucemovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by dev on 10/3/15.
 */
/*
public class TestUtilities extends AndroidTestCase {


    static ContentValues createMovieEntryValues(String movie_id) {
        ContentValues movieValue = new ContentValues();
        movieValue.put(MovieContract.MovieEntry.COLUMN_MOVIE_KEY, movie_id);
        movieValue.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "TestMovie is awesome " + movie_id);
        movieValue.put(MovieContract.MovieEntry.COLUMN_TITLE, "MovieTitle_"+ movie_id);

        return movieValue;

    }

    static ContentValues createMovieList(String sortOrder, long rank, String movie_id) {
        ContentValues testvalues = new ContentValues();

        testvalues.put(MovieContract.MovieListEntry.COLUMN_SORT, sortOrder);
        testvalues.put(MovieContract.MovieListEntry.COLUMN_RANK, rank);
        testvalues.put(MovieContract.MovieListEntry.COLUMN_MOVIE_KEY, movie_id);

        return testvalues;
    }

    static long insertMovieEntryValue(Context context) {
        String testMovieID = MovieContract.TEST_MOVIE1;

        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieEntryValues(testMovieID);

        long entryMovieRowID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Failure to insert TestMovieEntry: " + testMovieID, entryMovieRowID != -1 );

        return entryMovieRowID;

    }

}
*/