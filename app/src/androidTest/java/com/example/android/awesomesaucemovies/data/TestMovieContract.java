package com.example.android.awesomesaucemovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by dev on 9/30/15.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final String TEST_ID = "gute";

    private static final Uri TEST_MOVIEID_URI = MovieContract.MovieEntry.buildMovieUri(TEST_ID);


    public void testGetMovieIDFromUri(){



    }

}
