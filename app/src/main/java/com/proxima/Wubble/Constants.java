package com.proxima.Wubble;

import android.util.SparseArray;

/**
 * Created by Epokhe on 09.03.2015.
 */
public final class Constants {

    private Constants() {

    }

    public static final class CustomSparseArray<E> extends SparseArray<E> {

        boolean mLocked = false;

        @Override
        public void append(int key, E value) {
            if (mLocked) {
                return;
            }
            super.append(key, value);
        }

        @Override
        public void clear() {
            if (mLocked) {
                return;
            }
            super.clear();
        }

        @Override
        public void setValueAt(int index, E value) {
            if (mLocked) {
                return;
            }
            super.setValueAt(index, value);
        }

        @Override
        public void put(int key, E value) {
            if (mLocked) {
                return;
            }
            super.put(key, value);
        }

        @Override
        public void removeAtRange(int index, int size) {
            if (mLocked) {
                return;
            }
            super.removeAtRange(index, size);
        }

        @Override
        public void removeAt(int index) {
            if (mLocked) {
                return;
            }
            super.removeAt(index);
        }

        @Override
        public void remove(int key) {
            if (mLocked) {
                return;
            }
            super.remove(key);
        }

        @Override
        public void delete(int key) {
            if (mLocked) {
                return;
            }
            super.delete(key);
        }

        public void lock() {
            mLocked = true;
        }


    }

    public static final String PARSE_APP_ID = "5Sqm8A6YXQ1heLwSU1dOvXTe6Gf026OoyowLbWNP";
    public static final String PARSE_CLIENT_KEY = "wRJsDUtcHiwgJjB3JzE3v0ViVabFnD5U7DaVMec2";

    public static final String TMDB_API_KEY = "dc7176817ac527fc0d4f384f311f1cb7";

    public static final String ROTTEN_TOMATOES_API_KEY = "jt33c9zrw6u7keppu32pc9vj";


    public static final int REQUEST_WUBBLE = 889;
    public static final int REQUEST_OWN_PROFILE = 890;
    public static final int REQUEST_OTHER_PROFILE = 891;
    public static final int REQUEST_MOVIE = 892;
    public static final int REQUEST_PEOPLE_SEARCH = 893;


    public static final int RESULT_UPDATED = 911;
    public static final int RESULT_NOT_UPDATED = 912;


    public static final int MOVIE_CONSTRUCTOR_EXTENDED = 1;
    public static final int MOVIE_CONSTRUCTOR_BASIC = 0;

    public static final int MOVIE_TAB_FOLLOWING_COMMENTS = 0;
    public static final int MOVIE_TAB_BEST_COMMENTS = 1;

    public static final String VOLLEY_TAG_LIVE_SEARCH = "LiveSearchRequest";


    public static final String TMDB_POSTER_PATH = "poster_path";
    public static final String TMDB_ADULT = "adult";
    public static final String TMDB_ID = "id";
    public static final String TMDB_GENRE_ID = "id";
    public static final String TMDB_POPULARITY = "popularity";
    public static final String TMDB_ORIGINAL_TITLE = "original_title";
    public static final String TMDB_RELEASE_DATE = "release_date";
    public static final String TMDB_TITLE = "title";
    public static final String TMDB_VIDEO = "video";
    public static final String TMDB_OVERVIEW = "overview";
    public static final String TMDB_VOTE_AVERAGE = "vote_average";
    public static final String TMDB_VOTE_COUNT = "vote_count";
    public static final String TMDB_BACKDROP_PATH = "backdrop_path";
    public static final String TMDB_GENRES = "genres";
    public static final String TMDB_IMDB_ID = "imdb_id";


    public static final String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String TMDB_SECURE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String TMDB_API_URL_BASE = "http://api.themoviedb.org/3/";
    public static final String TMDB_API_URL_MOVIE = "movie/";
    public static final String TMDB_API_URL_API = "api_key=";
    public static final String TMDB_POSTER_SIZE_DEFAULT = "w92";
    public static final String TMDB_API_URL_SEARCH = "search/movie";
    public static final String TMDB_API_URL_QUERY = "query=";
    public static final String TMDB_API_URL_UPCOMING = "upcoming";


    public static final String OMDB_BASE_URL = "http://www.omdbapi.com/";
    public static final String OMDB_IMDB_ID = "i=";
    public static final String OMDB_PLOT = "plot=short";
    public static final String OMDB_R = "r=json";
    public static final String OMDB_METASCORE = "Metascore";
    public static final String OMDB_IMDB_RATING = "imdbRating";


    public static final String ROTTEN_TOMATOES_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json";
    public static final String ROTTEN_TOMATOES_API = "apikey=";
    public static final String ROTTEN_TOMATOES_TYPE_IMDB = "type=imdb";
    public static final String ROTTEN_TOMATOES_IMDB_ID = "id=";


    public static final String ROTTEN_TOMATOES_RATINGS = "ratings";
    public static final String ROTTEN_TOMATOES_CRITICS_SCORE = "critics_score";


    public static final CustomSparseArray<String> GENRES;

    static {
        GENRES = new CustomSparseArray<String>();
        GENRES.append(28, "Action");
        GENRES.append(12, "Adventure");
        GENRES.append(16, "Animation");
        GENRES.append(35, "Comedy");
        GENRES.append(80, "Crime");
        GENRES.append(99, "Documentary");
        GENRES.append(18, "Drama");
        GENRES.append(10751, "Family");
        GENRES.append(14, "Fantasy");
        GENRES.append(10769, "Foreign");
        GENRES.append(36, "History");
        GENRES.append(27, "Horror");
        GENRES.append(10402, "Music");
        GENRES.append(9648, "Mystery");
        GENRES.append(10749, "Romance");
        GENRES.append(878, "Science Fiction");
        GENRES.append(10770, "TV Movie");
        GENRES.append(53, "Thriller");
        GENRES.append(10752, "War");
        GENRES.append(37, "Western");
        GENRES.lock();
    }


}
