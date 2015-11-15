package com.proxima.Wubble.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.proxima.Wubble.R;
import com.proxima.Wubble.URLBuilder;
import com.proxima.Wubble.adapters.SuggestionRecyclerAdapter;
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


//degisiklik
public class NewWubbleActivity extends ToolbarActivity {


    private Context context;

    private EditText wubbleEdit;
    private EditText wubbleAbout;
    private Button wubbleButton;

    private TextView listTextView;
    private SearchView searchView;


    private RecyclerView.LayoutManager suggestionLayoutManager;
    protected RecyclerView suggestionRecyclerView;

    private RequestQueue requestQueue;
    private VolleySingleton volleySingleton;

    private String LOG_TAG;
    private int currentMovieId;
    private String currentMovieName;
    private boolean selectionFinished = false;


    private Handler handler = new Handler();

    private boolean isTransferComplete = true;
    private boolean movieTransferComplete = true;


    private final Runnable checkTransfer = new Runnable() {

        public void run() {
            try {
                if (!movieTransferComplete) {
                    handler.postDelayed(this, 100);
                } else {
                    Intent toMain = new Intent(NewWubbleActivity.this, FeedActivity.class);

                    isTransferComplete = true;
                    startActivity(toMain);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.getBoolean("selected")) {
                currentMovieId = extras.getInt("id");
                currentMovieName = extras.getString("movieTitle");
                selectionFinished = true;

                wubbleEdit.setVisibility(View.VISIBLE);
                wubbleButton.setVisibility(View.VISIBLE);
                suggestionRecyclerView.setVisibility(View.GONE);
                searchView.setQuery(currentMovieName, false);
                wubbleEdit.requestFocus();
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
        setContentView(R.layout.activity_new_wubble);
        activateToolbar();

        context = this;

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


        LOG_TAG = this.getClass().getSimpleName();

        wubbleEdit = (EditText) findViewById(R.id.wubblePageEditText);
        wubbleButton = (Button) findViewById(R.id.wubblePageButton);
        suggestionRecyclerView = (RecyclerView) findViewById(R.id.movieRecyclerView);

        SlideInLeftAnimator animation = new SlideInLeftAnimator();
        animation.setChangeDuration(2000);
        suggestionRecyclerView.setItemAnimator(animation);

        suggestionLayoutManager = new LinearLayoutManager(this);
        suggestionRecyclerView.setLayoutManager(suggestionLayoutManager);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.movieSearchView);
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
                    //currentJSONTask = (JSONGetter) (new JSONGetter(getBaseContext()).execute(encodedURL));

                    JsonObjectRequest movieSearchRequest = new JsonObjectRequest(Request.Method.GET, encodedURL, (JSONObject) null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    try {

                                        int total_results = jsonObject.getInt("total_results");
                                        if (total_results == 0) {
                                            // There are no results
                                            //listTextView.setText(getString(R.string.no_result, new Object[]{"what"}));

                                            wubbleEdit.setVisibility(View.VISIBLE);
                                            wubbleButton.setVisibility(View.VISIBLE);
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

                                            SuggestionRecyclerAdapter movieAdapter = new SuggestionRecyclerAdapter(context, movieInformationList);


                                            wubbleEdit.setVisibility(View.INVISIBLE);
                                            wubbleButton.setVisibility(View.INVISIBLE);
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
                    Log.d(LOG_TAG, "Empty text");
                    wubbleEdit.setVisibility(View.VISIBLE);
                    wubbleButton.setVisibility(View.VISIBLE);
                    suggestionRecyclerView.setVisibility(View.GONE);
                }

                return false;
            }
        });
//
        wubbleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferComplete) {
                    isTransferComplete = false;


                    EditText comment = (EditText) findViewById(R.id.wubblePageEditText);


                    if (!comment.getText().toString().trim().isEmpty() && !searchView.getQuery().toString().trim().isEmpty()) {

                        ParseUser currentUser = ParseUser.getCurrentUser();
                        String currentUserName = currentUser.getUsername();


                        String wubbleStr = wubbleEdit.getText().toString();
                        //String aboutStr = wubbleAbout.getText().toString();

                        ParseObject wubbles = new ParseObject("Wubbles");

                        ParseObject movies = new ParseObject("Movies");

                        movies.put("Movie", currentMovieName);

                        wubbles.put("newWubble", wubbleStr);
                        wubbles.put("User", currentUserName);
                        wubbles.put("Movie", currentMovieName);
                        wubbles.put("movieId", currentMovieId);
                        ParseFile profilePic = (ParseFile) currentUser.get("profilepic");
                        if (profilePic != null)
                            wubbles.put("wubblePhoto", currentUser.get("profilepic"));


                        movieTransferComplete = false;
                        movies.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                movieTransferComplete = true;
                            }
                        });

                        wubbles.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    handler.post(checkTransfer);
                                } else {
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NewWubbleActivity.this);
                                    alertBuilder.setMessage(e.getMessage());
                                    alertBuilder.setTitle("Oops..");
                                    alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = alertBuilder.create();
                                    alertDialog.show();
                                }

                            }
                        });
                    } else {
                        isTransferComplete = true;
                    }
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_wubble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
