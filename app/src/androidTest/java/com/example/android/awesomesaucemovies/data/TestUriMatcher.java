package com.example.android.awesomesaucemovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Tests UriMatcher function
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String MOVIE_AVATAR = "avatar";  // test  movie id
    private static final String POPULARLIST = MovieContract.SortOrderElements.SORT_POPULAR;
    private static final String MOSTVOTES = MovieContract.SortOrderElements.SORT_MOST_VOTES;
   // private static final String FAVORITES = MovieContract.SortOrderElements.SORT_FAVORITES;


    // content://com.example.android.awesomesaucemovies/movies
    private static final Uri TEST_MOVIES_DIR = MovieContract.MovieEntry.CONTENT_URI;

    // content://com.example.android.awesomesaucemovies/movies/id
    private static final Uri TEST_MOVIEID_ITEM = MovieContract.MovieEntry.buildMovieUri(MOVIE_AVATAR);

//    // content://com.example.android.awesomesaucemovies/sortedlist/
//    private static final Uri TEST_SORTEDLIST_DIR = MovieContract.MovieEntry.CONTENT_URI;

    // content://com.example.android.awesomesaucemovies/sortedlist/popular
    private static final Uri TEST_POPULARLIST_DIR = MovieContract.MovieListEntry.buildSortedMovieListUri(POPULARLIST);

    // content://com.example.android.awesomesaucemovies/sortedlist/mostvotes
    private static final Uri TEST_MOSTVOTES_DIR = MovieContract.MovieListEntry.buildSortedMovieListUri(MOSTVOTES);

    // content://com.example.android.awesomesaucemovies/sortedlist/favorites
//    private static final Uri TEST_FAVORITES_DIR = MovieContract.MovieListEntry.buildSortedMovieListUri(FAVORITES);


    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The MOVIE_ID was matched incorrectly.",
                testMatcher.match(TEST_MOVIEID_ITEM), MovieProvider.MOVIE_ID);
        assertEquals("Error: The SORT SELECTION (POPULAR MOVIES) was matched incorrectly. ",
                testMatcher.match(TEST_POPULARLIST_DIR), MovieProvider.SORT_SELECTION);
        // pull last component and test to see if it matches POPULAR MOVIE
        assertEquals("Error: The SORT SELECTION (MOST VOTES) was matched incorrectly.  ",
                testMatcher.match(TEST_MOSTVOTES_DIR), MovieProvider.SORT_SELECTION);



    }
}
