package com.proxima.Wubble.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.proxima.Wubble.R;
import com.proxima.Wubble.URLBuilder;
import com.proxima.Wubble.adapters.MovieSearchAdapter;
import com.proxima.Wubble.movie.MovieInformation;
import com.proxima.Wubble.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.proxima.Wubble.Constants.MOVIE_CONSTRUCTOR_BASIC;
import static com.proxima.Wubble.Constants.VOLLEY_TAG_LIVE_SEARCH;

public class MovieSearchActivity extends ToolbarActivity {

    private Context context;

    private SearchView searchView;


    private RecyclerView.LayoutManager suggestionLayoutManager;
    protected RecyclerView suggestionRecyclerView;

    private RequestQueue requestQueue;
    private VolleySingleton volleySingleton;

    private String LOG_TAG;
    private int currentMovieId;
    private String currentMovieName;
    private boolean selectionFinished = false;


    private boolean isTransferComplete = true;
    private boolean movieTransferComplete = true;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.getBoolean("selected")) {
                currentMovieId = extras.getInt("id");
                currentMovieName = extras.getString("movieTitle");
                selectionFinished = true;
                suggestionRecyclerView.setVisibility(View.GONE);
                searchView.setQuery(currentMovieName, false);

                selectionFinished = false;
            }

        } else {
            //handle errors
            Log.d(LOG_TAG, "Error at intent");
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);
        activateToolbar();

        context = this;

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


        LOG_TAG = getBaseContext().getClass().getSimpleName();

        suggestionRecyclerView = (RecyclerView) findViewById(R.id.movieSearchRecyclerView);

        SlideInLeftAnimator animation = new SlideInLeftAnimator();
        animation.setChangeDuration(2000);
        suggestionRecyclerView.setItemAnimator(animation);

        suggestionLayoutManager = new LinearLayoutManager(this);
        suggestionRecyclerView.setLayoutManager(suggestionLayoutManager);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.movieSearchSearchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (requestQueue != null) {
                    requestQueue.cancelAll(VOLLEY_TAG_LIVE_SEARCH);
                }

                if (selectionFinished) {
                    return false;
                }


                String encodedURL = null;
                if (!newText.trim().isEmpty()) {
                    try {
                        encodedURL = URLBuilder.getMovieSearchURL(newText);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest movieSearchRequest = new JsonObjectRequest(Request.Method.GET, encodedURL, (JSONObject) null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {

                                        int total_results = jsonObject.getInt("total_results");
                                        if (total_results == 0) {
                                            // There are no results

                                            suggestionRecyclerView.setVisibility(View.GONE);
                                        } else {
                                            // Display the number of results
                                            int count = 10;
                                            JSONArray resultArray = jsonObject.getJSONArray("results");
                                            final List<MovieInformation> movieInformationList = new ArrayList<MovieInformation>();
                                            for (int i = 0; i < 10; i++) {
                                                JSONObject currentMovie = (JSONObject) resultArray.get(i);
                                                MovieInformation infoToAdd = new MovieInformation(currentMovie, MOVIE_CONSTRUCTOR_BASIC);

                                                movieInformationList.add(infoToAdd);
                                            }

                                            Collections.sort(movieInformationList);

                                            MovieSearchAdapter movieAdapter = new MovieSearchAdapter(context, movieInformationList);


                                            suggestionRecyclerView.setVisibility(View.VISIBLE);
                                            suggestionRecyclerView.setAdapter(movieAdapter);


                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(LOG_TAG, "ERROR: " + error.getMessage());

                                }
                            }
                    );
                    movieSearchRequest.setTag(VOLLEY_TAG_LIVE_SEARCH);
                    requestQueue.add(movieSearchRequest);

                } else {
                    suggestionRecyclerView.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "Empty text");
                }

                return false;
            }
        });

        final List<MovieInformation> upcomingMoviesList = new ArrayList<>();

        String upcomingMoviesURL = URLBuilder.getUpcomingMoviesURL();
        JsonObjectRequest upcomingMoviesRequest = new JsonObjectRequest(Request.Method.GET, upcomingMoviesURL, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int total_results = response.getInt("total_results");
                    if (total_results != 0) {
                        int count = 20;

                        JSONArray resultArray = response.getJSONArray("results");

                        for (int i = 0; i < count; i++) {
                            JSONObject currentMovie = (JSONObject) resultArray.get(i);
                            MovieInformation infoToAdd = new MovieInformation(currentMovie, MOVIE_CONSTRUCTOR_BASIC);

                            upcomingMoviesList.add(infoToAdd);
                        }

                        Collections.sort(upcomingMoviesList);

                        MovieSearchAdapter upcomingMoviesAdapter = new MovieSearchAdapter(context, upcomingMoviesList);
                        suggestionRecyclerView.setAdapter(upcomingMoviesAdapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "ERROR: " + error.getMessage());
            }
        });
        requestQueue.add(upcomingMoviesRequest);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.newWubble:

                Intent toNewWubble = new Intent(this, NewWubbleActivity.class);
                startActivity(toNewWubble);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
