package com.example.android.awesomesaucemovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.android.awesomesaucemovies.API;

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
    public static final String PATH_MOVIES = "movies";          // movies per sort preference
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_MOVIE_TRAILERS = "trailers";
    public static final String PATH_MOVIE_REVIEWS = "reviews";




    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIES;



        public static final String TABLE_NAME = "movieItems";

        public static final String COLUMN_MOVIE_KEY = "_id";   // id of movie from Movie Database API
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";


        // COLUMN_NORMAL_RANK
        // indicates the rank to show the entry  -  (Positive values)
        // AND
        // if the entry should be omitted from the rank - ( -1 )
        public static final String COLUMN_NORMAL_RANK = "normal_rank";
        public static final int VAL_OMIT_FROM_RANK = -1;
        public static final String WHERE_NOT_RANKED_CLAUSE = COLUMN_NORMAL_RANK + "!=?";


        // COLUMN_DELETE is a helper column to assist with deleting entries
        // default, keep entry = (false) 0,
        // Mark for delete = (true) 1,
        // custom method should clean up row,
        //  note: if an entry is marked delete(true), this will delete the setting with no other regard
        public static final String COLUMN_DELETE = "column_delete";
        public static final int VAL_KEEP_ENTRY = 0;  // used with COLUMN_DELETE
        public static final int VAL_DELETE_ENTRY = 1; // used with COLUMN_DELETE



        // COLUMN_FAVORITE +COLUMN_FAVORITE_RANK supports the favorite ability
        // boolean default false, user changes in detail view screen
        //
        // ---!--- Favorites are treated differently when comes to Marking for Deletion  ----!----
        public static final String COLUMN_FAVORITE = "favorite";
        public static final int VAL_IS_NOT_FAVORITE = 0;
        public static final int VAL_IS_FAVORITE = 1;


        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_average";

        public static final String COLUMN_SORT_TYPE = "sort_type";







        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildMovieUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        // generates the Uri for the MoviePoster
        public static Uri buildMoviePosterUri(String posterPathId) {
            final String API_KEY = new API().getAPI();

            final String SCHEME = "http";
            final String AUTHORITY = "image.tmdb.org";
            final String PARTONE = "t";
            final String PARTTWO = "p";
            final String IMAGESIZE = "w185";



            if (posterPathId != null) {

                String tmpPosterPath = posterPathId;

                if (!posterPathId.isEmpty()) {

                    // MovieDatabase may prepend '/' to their path. Uri.Builder must have a way to
                    // handle this. But not work investigating at this time.
                    if (posterPathId.startsWith("/")){
                        tmpPosterPath = posterPathId.substring(1);
                    }

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme(SCHEME)
                            .authority(AUTHORITY)
                            .appendPath(PARTONE)
                            .appendPath(PARTTWO)
                            .appendPath(IMAGESIZE)
                            .appendPath(tmpPosterPath)
                            .appendQueryParameter("api_key", API_KEY);

                    return builder.build();


                }

            }


            Log.v("buildMoviePosterUri", "mPosterPath is null or empty; returning null ");
            // Picasso expects null or a well formed URL
            return null;
        }



    }


    // MovieFavorites uses MovieEntries table
    public static final class MovieFavorites implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_FAVORITES;


        // table = MovieEntry table.
        public static final String TABLE_NAME = MovieEntry.TABLE_NAME;

        public static final String COLUMN_FAVORITE = MovieEntry.COLUMN_FAVORITE;
        public static final int VAL_IS_NOT_FAVORITE = MovieEntry.VAL_IS_NOT_FAVORITE;
        public static final int VAL_IS_FAVORITE = MovieEntry.VAL_IS_FAVORITE;


        public static final String WHERE_FAVORITE_CLAUSE = COLUMN_FAVORITE + "=?";




    }


    public static final class MovieTrailers implements BaseColumns {

        // MovieTrailers Content uri is not intended to be used.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_TRAILERS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_TRAILERS;


        public static final String TABLE_NAME = "movieTrailers";

        public static final String COLUMN_TRAILER_KEY = "_id";  // db autogenerated
        public static final String COLUMN_TRAILER_API_ID = "api_id_key";  // ID used by MovieDatabase API
        public static final String COLUMN_MOVIE_ID = "movie_id_key"; // ID used by MovieDatabase API & Key for MovieEntry
        public static final String COLUMN_TRAILER_LANGUAGE = "language";
        public static final String COLUMN_TRAILER_URI = "trailer_uri"; // source reference Uri segment
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_SITE = "website";
        public static final String COLUMN_TRAILER_RESOLUTION = "resolution";
        public static final String COLUMN_TRAILER_TYPE = "trailer_type";





        public static Uri buildMovieTrailersUri(String id) {
            return MovieEntry.CONTENT_URI.buildUpon().appendPath(id).appendPath(PATH_MOVIE_TRAILERS).build();
        }

        // uses MovieEntry method to obtain Movie ID
        public static String getMovieIDFromUriWithTrailer(Uri uri) {
            return MovieEntry.getMovieIDFromUri(uri);
        }



    }



    public static final class MovieReviews implements BaseColumns {


        // MovieReviews Content uri is not intended to be used.
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEWS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEWS;


        public static final String TABLE_NAME = "movieReviews";

        public static final String COLUMN_REVIEW_KEY = "_id";  // db autogenerated
        public static final String COLUMN_REVIEW_API_ID = "api_id_key";  // ID used by MovieDatabase API
        public static final String COLUMN_MOVIE_ID = "movie_id_key"; // ID used by MovieDatabase API & Key for MovieEntry
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        // skipping language & url


        public static Uri buildMovieReviewsUri(String id) {
            return MovieEntry.CONTENT_URI.buildUpon().appendPath(id).appendPath(PATH_MOVIE_REVIEWS).build();
        }

        // uses MovieEntry method to obtain Movie ID
        public static String getMovieIDFromUriWithReview(Uri uri) {
            return MovieEntry.getMovieIDFromUri(uri);
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

//    public static final class SortOrderElements {
//
//        public static final String SORT_POPULAR = "popular";
//        public static final String SORT_MOST_VOTES = "mostvotes";
//        public static final String SORT_FAVORITES = "favorites";
//
//    }
////

    /*
     This class represents a table containing:
     > The sort method - (user defined order, favorite)
     > the rank (display order)
     > the movie_id



    */
//    public static final class MovieSortedList implements BaseColumns {
//
//    }

//    // this is PATH_MOVIE_LIST or "sortedList"
//    public static final class MovieListEntry implements BaseColumns {
//
//        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_LIST).build();
//
//        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_MOVIE_LIST;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
//                CONTENT_AUTHORITY + "/" + PATH_MOVIE_LIST;
//
//
//        public static final String TABLE_NAME = "movieLists";
//
//        // sort order type (per SortOrderElements)
//        public static final String COLUMN_SORT = "sort_type";
//
//        // rank is tied to the sort order. COLUMN_SORT_KEY & COLUMN_RANK are Compound Primary Key
//        public static final String COLUMN_RANK = "rank";
//
//
//        public static final String COLUMN_MOVIE_KEY = "movie_id";
//
//
//        public static Uri buildMovieUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//
//        public static Uri buildSortedMovieListUri(String s) {
//            return CONTENT_URI.buildUpon().appendPath(s).build();
//        }
//
//        public static String getSortOrderFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//
//        }
//
//
//    }


}
