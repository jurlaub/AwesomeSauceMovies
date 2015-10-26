package com.example.android.awesomesaucemovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dev on 9/13/15.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.example.android.awesomesaucemovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_SORT = "sort";
    public static final String PATH_MOVIE_LIST = "sortedlist";

    public static final String TEST_MOVIE1 = "gravity";



    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "movieItems";

        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_average";

//            public static final String COLUMN_ORIGINAL_TITLE = "original_title"; // not used
//            public static final String COLUMN_BACKDROP_PATH = "backdrop_path"; // not used
//            public static final String COLUMN_VOTE_COUNT = "vote_count";
//            public static final String COLUMN_GENRE_ID = "genre_ids";  // not used
//            public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";  // not used


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildMovieUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }



    // this is PATH_SORT or "sort"
//    public static final class SortOrderEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORT).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_SORT;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_SORT;
//
//
//
//        public static final String TABLE_NAME = "sortOrder";
//
//        // type of sort in table - the different sort option names would go here.
//        public static final String COLUMN_SORT_NAME = "sortName";
//        //public static final String
//
//        public static final String ENTRY_POPULAR = "popular";
//        public static final String ENTRY_MOST_VOTES = "mostVotes";
//        public static final String ENTRY_FAVORITES = "favorites";
//
//
//
//    }

    public static final class SortOrderElements {

        public static final String SORT_POPULAR = "popular";
        public static final String SORT_MOST_VOTES = "mostvotes";
        public static final String SORT_FAVORITES = "favorites";

    }


    // this is PATH_MOVIE_LIST or "sortedList"
    public static final class MovieListEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_LIST).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_LIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_LIST;


        public static final String TABLE_NAME = "movieLists";

        // sort order type (per SortOrderElements)
        public static final String COLUMN_SORT = "sort_type";

        // rank is tied to the sort order. COLUMN_SORT_KEY & COLUMN_RANK are Compound Primary Key
        public static final String COLUMN_RANK = "rank";


        public static final String COLUMN_MOVIE_KEY = "movie_id";


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSortedMovieListUri(String s) {
            return CONTENT_URI.buildUpon().appendPath(s).build();
        }

        public static String getSortOrderFromUri(Uri uri) {
            return uri.getPathSegments().get(1);

        }


    }


}
