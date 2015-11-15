package com.proxima.Wubble.movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.proxima.Wubble.Constants.TMDB_ADULT;
import static com.proxima.Wubble.Constants.TMDB_BACKDROP_PATH;
import static com.proxima.Wubble.Constants.TMDB_GENRES;
import static com.proxima.Wubble.Constants.TMDB_GENRE_ID;
import static com.proxima.Wubble.Constants.TMDB_ID;
import static com.proxima.Wubble.Constants.TMDB_IMDB_ID;
import static com.proxima.Wubble.Constants.TMDB_ORIGINAL_TITLE;
import static com.proxima.Wubble.Constants.TMDB_OVERVIEW;
import static com.proxima.Wubble.Constants.TMDB_POPULARITY;
import static com.proxima.Wubble.Constants.TMDB_POSTER_PATH;
import static com.proxima.Wubble.Constants.TMDB_RELEASE_DATE;
import static com.proxima.Wubble.Constants.TMDB_VIDEO;
import static com.proxima.Wubble.Constants.TMDB_VOTE_AVERAGE;
import static com.proxima.Wubble.Constants.TMDB_VOTE_COUNT;

/**
 * Created by Epokhe on 09.03.2015.
 */
public class MovieInformation implements Comparable<MovieInformation> {

    public boolean adult;
    public String backdrop_path;
    public int id;
    public String original_title;
    public String release_date;
    public String poster_path;
    public String overview;
    public List<Integer> genres;
    public int popularity;
    public String title;
    public boolean video;
    public float vote_average;
    public int vote_count;
    public String imdbId;


    public MovieInformation(JSONObject movieObject, int constructType) throws JSONException {

        adult = movieObject.getBoolean(TMDB_ADULT);
        backdrop_path = movieObject.getString(TMDB_BACKDROP_PATH);
        id = movieObject.getInt(TMDB_ID);
        original_title = movieObject.getString(TMDB_ORIGINAL_TITLE);
        release_date = movieObject.getString(TMDB_RELEASE_DATE);
        poster_path = movieObject.getString(TMDB_POSTER_PATH);
        popularity = movieObject.getInt(TMDB_POPULARITY);
        video = movieObject.getBoolean(TMDB_VIDEO);
        vote_average = movieObject.getInt(TMDB_VOTE_AVERAGE);
        vote_count = movieObject.getInt(TMDB_VOTE_COUNT);


        switch (constructType) {
            case 0: //BASIC
                overview = "";
                imdbId = "";
                genres = new ArrayList<Integer>();
                break;
            case 1: //EXTENDED

                imdbId = movieObject.getString(TMDB_IMDB_ID);

                overview = movieObject.getString(TMDB_OVERVIEW);

                JSONArray jsonArray = movieObject.getJSONArray(TMDB_GENRES);

                genres = new ArrayList<Integer>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    genres.add(jsonArray.getJSONObject(i).getInt(TMDB_GENRE_ID));
                }
                break;
        }

        nullFix();
    }

    private void nullFix() {
        if (backdrop_path == null) {
            backdrop_path = "";
        }
        if (original_title == null) {
            original_title = "";
        }
        if (release_date == null) {
            release_date = "";
        }
        if (poster_path == null) {
            poster_path = "";
        }
        if (overview == null) {
            overview = "";
        }
    }


    public int compareTo(MovieInformation movieInformation) {
        float comparePopularity = movieInformation.popularity;
        float diff = comparePopularity - this.popularity;
        if (Math.abs(diff) > 1) {
            return (int) diff;
        } else if (diff < 0) {
            return -1;
        } else {
            return 1;
        }

    }
}
