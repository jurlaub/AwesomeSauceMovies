package com.example.android.awesomesaucemovies.data;

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




    public static final class MovieEntry implements BaseColumns {


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



    }


    public static final class SortOrderEntry implements BaseColumns {

        public static final String TABLE_NAME = "sortOrder";

        // type of sort in table
        public static final String COLUMN_SORT_NAME = "sortName";

    }

    public static final class MovieListEntry implements BaseColumns {

        public static final String TABLE_NAME = "movieLists";

        // sort order primary key
        public static final String COLUMN_SORT_KEY = "sort_id";

        // rank is tied to the sort order. COLUMN_SORT_KEY & COLUMN_RANK are Compound Primary Key
        public static final String COLUMN_RANK = "rank";


        public static final String COLUMN_MOVIE_KEY = "movie_id";





    }


}
