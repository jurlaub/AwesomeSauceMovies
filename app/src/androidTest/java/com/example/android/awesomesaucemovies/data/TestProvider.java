package com.example.android.awesomesaucemovies.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by dev on 10/2/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();


    // delete classes
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MovieListEntry.CONTENT_URI,
                null,
                null
        );



        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from MovieEntry table during delete", 0, cursor.getCount());
        cursor.close();



        cursor = mContext.getContentResolver().query(
                MovieContract.MovieListEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from MovieListEntry table during delete", 0, cursor.getCount());
        cursor.close();

    }

    public void deleteAllRecords() { deleteAllRecordsFromProvider();}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }



    public void testProviderRegistery() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try {
            // fetch provider using component name from PackageManager
            // throws exception if not registered
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // make sure teh registered authority matches the contract
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);

        } catch (PackageManager.NameNotFoundException e) {
            // provider not registered correctly
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(), false);

        }
    }

        // tests contentProvider returns correct typ for eacy type of URI

    public void testGetType() {

        // MOVIE
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);

        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);




        // MOVIE_ID
        String testMovie = "avatar";
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(testMovie));

        assertEquals("Error: the MovieEntry CONTENT_ENTRY with MovieID should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);




        // SORT_SELECTION
        String testSort = MovieContract.SortOrderElements.SORT_POPULAR;
        type = mContext.getContentResolver().getType(MovieContract.MovieListEntry.buildSortedMovieListUri(testSort));

        assertEquals("Error: the MovieListEntry CONTENT_URI with a sort element should return MovieListEntry.CONTENT_TYPE",
                MovieContract.MovieListEntry.CONTENT_TYPE, type);



    }

    public void testBasicMovieQuery() {

        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieEntryID = TestUtilities.insertMovieEntryValue(mContext);

        assertTrue("Basic TestMovieEntry was not added to DB", movieEntryID != -1);

        db.close();

        // provider query test

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Log.i(LOG_TAG, movieCursor.toString());


    }


}


