package com.example.android.awesomesaucemovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by dev on 9/30/15.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final String TEST_ID = "gute";




    public void testGetMovieIDFromUri(){

           Uri movieIDUri = MovieContract.MovieEntry.buildMovieUri(TEST_ID);

        assertNotNull("Error, buildMovieUri is null and should have a value : ", movieIDUri);

        assertEquals("Error: Movie ID  not properly appended to the end of the Uri",
                TEST_ID, movieIDUri.getLastPathSegment());

        assertEquals("Error: Movie ID Uri doesn't match our expected result",
                movieIDUri.toString(), "content://com.example.android.awesomesaucemovies/movies/gute");

    }


}
