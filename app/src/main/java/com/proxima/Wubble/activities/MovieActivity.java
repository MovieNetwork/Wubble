package com.proxima.Wubble.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.Tabs.SlidingTabLayout;
import com.proxima.Wubble.URLBuilder;
import com.proxima.Wubble.anim.AnimationUtils;
import com.proxima.Wubble.fragments.FollowingCommentsFragment;
import com.proxima.Wubble.fragments.TopCommentsFragment;
import com.proxima.Wubble.movie.MovieInformation;
import com.proxima.Wubble.network.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.proxima.Wubble.Constants.GENRES;
import static com.proxima.Wubble.Constants.MOVIE_CONSTRUCTOR_EXTENDED;
import static com.proxima.Wubble.Constants.MOVIE_TAB_BEST_COMMENTS;
import static com.proxima.Wubble.Constants.MOVIE_TAB_FOLLOWING_COMMENTS;
import static com.proxima.Wubble.Constants.OMDB_IMDB_RATING;
import static com.proxima.Wubble.Constants.OMDB_METASCORE;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_CRITICS_SCORE;
import static com.proxima.Wubble.Constants.ROTTEN_TOMATOES_RATINGS;


public class MovieActivity extends ToolbarActivity {

    protected String LOG_TAG;

    private Context context;


    private ViewPager mPager;
    private SlidingTabLayout mTabs;

    private TextView movieDescriptionTextView;
    private TextView movieNameTextView;
    private ImageView movieImageImageView;
    private TextView movieReleaseDateTextView;
    private TextView movieGenresTextView;
    private TextView PopupDescTextView;

    private TextView imdbScoreTextView;
    private TextView rtScoreTextView;
    private TextView metascoreTextView;

    private PopupWindow pw;
    private RelativeLayout pwRelLay;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    private String movieName;
    private int movieId;
    private String movieComment;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isTransferComplete = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        activateToolbar();

        context = this;

        Bundle extras = getIntent().getExtras();

        LOG_TAG = getBaseContext().getClass().getSimpleName();

        movieDescriptionTextView = (TextView) findViewById(R.id.MoviePageMovieDescription);
        movieNameTextView = (TextView) findViewById(R.id.MoviePageMovieName);
        movieImageImageView = (ImageView) findViewById(R.id.MoviePageMovieImage);
        movieGenresTextView = (TextView) findViewById(R.id.MoviePageMovieGenres);
        movieReleaseDateTextView = (TextView) findViewById(R.id.MoviePageReleaseDate);
        imdbScoreTextView = (TextView) findViewById(R.id.MoviePageIMDBScore);
        metascoreTextView = (TextView) findViewById(R.id.MoviePageMetascore);
        rtScoreTextView = (TextView) findViewById(R.id.MoviePageRottenTomatoesScore);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        final String currentUserName = currentUser.getUsername();

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


        if (extras != null) {

            movieName = extras.getString("movieName");
            movieId = extras.getInt("movieId");

            movieNameTextView.setText(movieName);


            //String requestURL = TMDB_API_URL_BASE + TMDB_API_URL_MOVIE + Integer.toString(movieId) + TMDB_API_URL_API + TMDB_API_KEY;
            String requestURL = URLBuilder.getMovieWithIdURL(movieId);


            movieDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View PopupDesc = inflater.inflate(R.layout.description_popup, null, false);
                    pw = new PopupWindow(PopupDesc, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

                    pw.showAtLocation(findViewById(R.id.MoviePageRelLay), Gravity.CENTER, 0, 0);
                    PopupDescTextView = (TextView) PopupDesc.findViewById(R.id.popup_desc);
                    PopupDescTextView.setText(movieDescriptionTextView.getText().toString());

                    pwRelLay = (RelativeLayout) PopupDesc.findViewById(R.id.descPopupRelLay);
                    AnimationUtils.PopupAnimate(pwRelLay, true);
                    pwRelLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnimationUtils.PopupAnimate(pwRelLay, false);
                            pw.dismiss();

                        }

                    });


                }
            });


            JsonObjectRequest movieRequest = new JsonObjectRequest(Request.Method.GET, requestURL, (JSONObject) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                MovieInformation currentMovie = new MovieInformation(response, MOVIE_CONSTRUCTOR_EXTENDED);
                                //String imageURL = TMDB_IMAGE_BASE_URL + TMDB_POSTER_SIZE_DEFAULT + response.getString(TMDB_POSTER_PATH);
                                if (!currentMovie.poster_path.isEmpty()) {
                                    String imageURL = URLBuilder.getImageURL(currentMovie.poster_path);
                                    Picasso.with(getBaseContext()).load(imageURL)
                                            .error(R.drawable.ic_action_user)
                                            .into(movieImageImageView);
                                }

                                movieDescriptionTextView.setText(currentMovie.overview);

                                if (!currentMovie.release_date.isEmpty()) {
                                    Date date = dateFormat.parse(currentMovie.release_date);
                                }


                                movieReleaseDateTextView.setText(currentMovie.release_date);

                                String genresText = "";
                                if (currentMovie.genres.size() > 0) {
                                    genresText += GENRES.get(currentMovie.genres.get(0));

                                    for (int i = 1; i < currentMovie.genres.size(); i++) {
                                        genresText += ", ";
                                        genresText += GENRES.get(currentMovie.genres.get(i));
                                    }
                                }


                                movieGenresTextView.setText(genresText);


                                if (!currentMovie.imdbId.isEmpty()) {
                                    String rtScoreURL = URLBuilder.getRottenTomatoesResultWithIdURL(currentMovie.imdbId);

                                    String omdbScoreURL = URLBuilder.getOmdbResultWithIdURL(currentMovie.imdbId);
                                    Log.d(LOG_TAG, rtScoreURL);

                                    final JsonObjectRequest rtScoreRequest = new JsonObjectRequest(Request.Method.GET, rtScoreURL, (JSONObject) null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                String possibleError = response.optString("error");

                                                if (possibleError.equals("")) {
                                                    Log.d(LOG_TAG, Integer.toString(response.getInt("id")));
                                                    JSONObject ratingObject = response.getJSONObject(ROTTEN_TOMATOES_RATINGS);
                                                    double score = ratingObject.getInt(ROTTEN_TOMATOES_CRITICS_SCORE);
                                                    score /= 10;
                                                    String rtRating;
                                                    if (score >= 0) {
                                                        rtRating = Double.toString(score);
                                                    } else {
                                                        rtRating = "N/A";
                                                    }

                                                    rtScoreTextView.setText(rtRating);
                                                } else {
                                                    rtScoreTextView.setText("N/A");
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
                                    requestQueue.add(rtScoreRequest);

                                    JsonObjectRequest omdbScoreRequest = new JsonObjectRequest(Request.Method.GET, omdbScoreURL, (JSONObject) null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                String imdbRating = response.getString(OMDB_IMDB_RATING);
                                                imdbScoreTextView.setText(imdbRating);


                                                String metascore = response.getString(OMDB_METASCORE);
                                                if (!metascore.equals("N/A")) {
                                                    double score = (double) Integer.parseInt(metascore);
                                                    score /= 10;
                                                    metascoreTextView.setText(Double.toString(score));
                                                } else {
                                                    metascoreTextView.setText("N/A");
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
                                    requestQueue.add(omdbScoreRequest);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
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
            requestQueue.add(movieRequest);


        }

        mPager = (ViewPager) findViewById(R.id.MoviePagePager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.MoviePageTabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(mPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.newWubble:

                Intent toNewWubble = new Intent(this, NewWubbleActivity.class);
                startActivity(toNewWubble);
                break;

            case R.id.logOut:

                ParseUser.logOut();
                Intent toLogin = new Intent(this, LoginActivity.class);
                startActivity(toLogin);
                finish();
                break;


            case R.id.profileAct:

                Intent toProfile = new Intent(MovieActivity.this, ProfileActivity.class);
                startActivity(toProfile);
                break;

            case R.id.peopleSearch:

                Intent toPeopleSearch = new Intent(MovieActivity.this, PeopleSearchActivity.class);
                startActivity(toPeopleSearch);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    class MyPagerAdapter extends FragmentPagerAdapter {

        String[] MoviePageTabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            MoviePageTabs = getResources().getStringArray(R.array.MoviePageTabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case MOVIE_TAB_FOLLOWING_COMMENTS:
                    fragment = FollowingCommentsFragment.newInstance(movieId);
                    break;
                case MOVIE_TAB_BEST_COMMENTS:
                    fragment = TopCommentsFragment.newInstance(movieId);
                    break;
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MoviePageTabs[position];
        }

        @Override
        public int getCount() {
            return MoviePageTabs.length;
        }
    }

    public static class MyFragment extends Fragment {
        private TextView textView;

        public static MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_my, container, false);
            textView = (TextView) layout.findViewById(R.id.position);
            Bundle bundle = getArguments();
            if (bundle != null) {
                textView.setText("The selected page is " + bundle.getInt("position"));
            }


            return layout;
        }
    }

}

