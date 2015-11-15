package com.proxima.Wubble;

/**
 * Created by Epokhe on 10.03.2015.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.proxima.Wubble.Constants.OMDB_BASE_URL;
import static com.proxima.Wubble.Constants.OMDB_IMDB_ID;
import static com.proxima.Wubble.Constants.OMDB_PLOT;
import static com.proxima.Wubble.Constants.OMDB_R;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_API;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_API_KEY;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_BASE_URL;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_IMDB_ID;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_TYPE_IMDB;
import static com.proxima.Wubble.Constants.TMDB_API_KEY;
import static com.proxima.Wubble.Constants.TMDB_API_URL_API;
import static com.proxima.Wubble.Constants.TMDB_API_URL_BASE;
import static com.proxima.Wubble.Constants.TMDB_API_URL_MOVIE;
import static com.proxima.Wubble.Constants.TMDB_API_URL_QUERY;
import static com.proxima.Wubble.Constants.TMDB_API_URL_SEARCH;
import static com.proxima.Wubble.Constants.TMDB_API_URL_UPCOMING;
import static com.proxima.Wubble.Constants.TMDB_IMAGE_BASE_URL;
import static com.proxima.Wubble.Constants.TMDB_POSTER_SIZE_DEFAULT;

public class URLBuilder {
    public static String getMovieWithIdURL(int movieId) {
        String url =
                TMDB_API_URL_BASE +
                        TMDB_API_URL_MOVIE +
                        Integer.toString(movieId) +
                        "?" +
                        TMDB_API_URL_API +
                        TMDB_API_KEY;
        return url;
    }

    public static String getMovieSearchURL(String movieTitle) throws UnsupportedEncodingException {
        String url =
                TMDB_API_URL_BASE +
                        TMDB_API_URL_SEARCH +
                        "?" +
                        TMDB_API_URL_API +
                        TMDB_API_KEY +
                        "&" +
                        TMDB_API_URL_QUERY +
                        URLEncoder.encode(movieTitle, "UTF-8");
        return url;
    }

    public static String getImageURL(String path) {
        String url =
                TMDB_IMAGE_BASE_URL +
                        TMDB_POSTER_SIZE_DEFAULT +
                        path;
        return url;
    }

    public static String getUpcomingMoviesURL() {
        String url =
                TMDB_API_URL_BASE +
                        TMDB_API_URL_MOVIE +
                        TMDB_API_URL_UPCOMING +
                        "?" +
                        TMDB_API_URL_API +
                        TMDB_API_KEY;
        return url;
    }

    public static String getOmdbResultWithIdURL(String imdbId) {
        String url =
                OMDB_BASE_URL +
                        "?" +
                        OMDB_IMDB_ID +
                        imdbId +
                        "&" +
                        OMDB_PLOT +
                        "&" +
                        OMDB_R;
        return url;
    }

    public static String getRottenTomatoesResultWithIdURL(String imdbId) {
        StringBuilder stringBuilder = new StringBuilder(imdbId.length() - 2);
        stringBuilder.append(imdbId, 2, imdbId.length());
        String trimmedImdbId = stringBuilder.toString();
        String url =
                ROTTEN_TOMATOES_BASE_URL +
                        "?" +
                        ROTTEN_TOMATOES_API +
                        ROTTEN_TOMATOES_API_KEY +
                        "&" +
                        ROTTEN_TOMATOES_TYPE_IMDB +
                        "&" +
                        ROTTEN_TOMATOES_IMDB_ID +
                        trimmedImdbId;
        return url;
    }


}
