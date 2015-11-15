package com.proxima.Wubble.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.FeedRecyclerAdapter;
import com.proxima.Wubble.network.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

import static com.proxima.Wubble.Constants.REQUEST_MOVIE;
import static com.proxima.Wubble.Constants.REQUEST_OTHER_PROFILE;
import static com.proxima.Wubble.Constants.REQUEST_OWN_PROFILE;
import static com.proxima.Wubble.Constants.REQUEST_PEOPLE_SEARCH;
import static com.proxima.Wubble.Constants.REQUEST_WUBBLE;

public class FeedActivity extends ToolbarActivity {

    private Context context;
    protected static FeedRecyclerAdapter adapter;
    protected RecyclerView.LayoutManager feedLayoutManager;
    protected RecyclerView feedRecyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected boolean isRefreshing = false;
    private Handler handler = new Handler();

    private int lastPressedPosition = 0;
    private boolean isTransferComplete = true;

    private RequestQueue requestQueue;
    private VolleySingleton volleySingleton;

    private ProgressWheel mSpinner;


    private ParseUser currentUser;
    private String currentUsername;

    private ArrayList<String> followedUserList = new ArrayList<String>();
    private ArrayList<ParseUser> followedParseUserList = new ArrayList<ParseUser>();
    private TextView mTextView;

    protected static ArrayList<ParseObject> wubbleList = new ArrayList<>();
    private String LOG_TAG = FeedActivity.class.getSimpleName();

    public void setLastPressedPosition(int pos) {
        lastPressedPosition = pos;
    }


    private final Runnable refreshing = new Runnable() {

        public void run() {
            try {
                if (isRefreshing) {
                    handler.postDelayed(this, 500);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    /*adapter = new FeedRecyclerAdapter(context, wubbleList, followedParseUserList);
                    feedRecyclerView.setAdapter(adapter);*/

                    for (int i = ((LinearLayoutManager) feedLayoutManager).findFirstVisibleItemPosition(); i <= ((LinearLayoutManager) feedLayoutManager).findLastVisibleItemPosition(); i++) {
                        adapter.noRefreshPositions.add(i);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) feedLayoutManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_WUBBLE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        ArrayList<String> likeList = (extras.getStringArrayList("likeList") == null) ? (new ArrayList<String>()) : extras.getStringArrayList("likeList");
                        ArrayList<String> dislikeList = (extras.getStringArrayList("dislikeList") == null) ? (new ArrayList<String>()) : extras.getStringArrayList("dislikeList");

                        ParseObject lastPressedObject = wubbleList.get(lastPressedPosition);
                        lastPressedObject.put("likedBy", likeList);
                        lastPressedObject.put("dislikedBy", dislikeList);


                        wubbleList.set(lastPressedPosition, lastPressedObject);


                        for (int i = ((LinearLayoutManager) feedLayoutManager).findFirstVisibleItemPosition(); i <= ((LinearLayoutManager) feedLayoutManager).findLastVisibleItemPosition(); i++) {
                            adapter.noRefreshPositions.add(i);
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                }
                break;
            case REQUEST_MOVIE:
                if (resultCode == RESULT_OK) {
                }
                break;
            case REQUEST_OTHER_PROFILE:
                if (resultCode == RESULT_OK) {
                }
                break;
            case REQUEST_OWN_PROFILE:
                if (resultCode == RESULT_OK) {
                }
                break;
            case REQUEST_PEOPLE_SEARCH:
                if (resultCode == RESULT_OK) {
                    ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("FollowRelations");
                    userQuery.whereEqualTo("Username", currentUser.getUsername());
                    userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {


                                followedUserList.clear();
                                if (parseObject.get("Following") == null) {
                                    followedUserList.addAll(new ArrayList<String>());
                                } else {
                                    followedUserList.addAll((ArrayList<String>) parseObject.get("Following"));
                                }
                                followedUserList.add(currentUsername);

                                ParseQuery userQuery = ParseUser.getQuery();
                                userQuery.whereContainedIn("username", followedUserList);

                                userQuery.findInBackground(new FindCallback() {
                                    @Override
                                    public void done(List list, ParseException e) {
                                        if (e == null) {
                                            followedParseUserList.clear();
                                            if (list == null) {
                                                followedUserList.addAll(new ArrayList<String>());
                                            } else {
                                                followedParseUserList.addAll((ArrayList<ParseUser>) list);
                                            }

                                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
                                            query.whereContainedIn("User", followedUserList);
                                            query.orderByDescending("createdAt");

                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> wubbles, ParseException e) {
                                                    if (e == null) {
                                                        mSpinner.setVisibility(View.GONE);
                                                        wubbleList.clear();
                                                        if (wubbles == null) {
                                                            wubbleList.addAll(new ArrayList<ParseObject>());
                                                            mTextView.setVisibility(View.VISIBLE);
                                                        } else if (wubbles.size() == 0) {
                                                            wubbleList.addAll(new ArrayList<ParseObject>());
                                                            mTextView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            wubbleList.addAll(wubbles);
                                                            mTextView.setVisibility(View.GONE);
                                                        }


                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        });


                                                        mSpinner.setVisibility(View.GONE);
                                                        feedRecyclerView.setVisibility(View.VISIBLE);

                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        context = this;
        activateToolbar();


        adapter = new FeedRecyclerAdapter(context, wubbleList, followedParseUserList);

        mSpinner = (ProgressWheel) findViewById(R.id.feedSpinner);
        mTextView = (TextView) findViewById(R.id.feedTextView);
        feedRecyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);


        feedLayoutManager = new LinearLayoutManager(this);
        feedRecyclerView.setLayoutManager(feedLayoutManager);

        feedRecyclerView.setAdapter(adapter);

        feedRecyclerView.setVisibility(View.GONE);
        mSpinner.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);

        currentUser = ParseUser.getCurrentUser();
        currentUsername = currentUser.getUsername();

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();


        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("FollowRelations");
        userQuery.whereEqualTo("Username", currentUser.getUsername());
        userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {


                    followedUserList.clear();
                    if (parseObject.get("Following") == null) {
                        followedUserList.addAll(new ArrayList<String>());
                    } else {
                        followedUserList.addAll((ArrayList<String>) parseObject.get("Following"));
                    }
                    followedUserList.add(currentUsername);

                    ParseQuery userQuery = ParseUser.getQuery();
                    userQuery.whereContainedIn("username", followedUserList);

                    userQuery.findInBackground(new FindCallback() {
                        @Override
                        public void done(List list, ParseException e) {
                            if (e == null) {
                                followedParseUserList.clear();
                                if (list == null) {
                                    followedUserList.addAll(new ArrayList<String>());
                                } else {
                                    followedParseUserList.addAll((ArrayList<ParseUser>) list);
                                }

                                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
                                query.whereContainedIn("User", followedUserList);
                                query.orderByDescending("createdAt");

                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> wubbles, ParseException e) {
                                        if (e == null) {
                                            mSpinner.setVisibility(View.GONE);
                                            wubbleList.clear();
                                            if (wubbles == null) {
                                                //Toast.makeText(context,"ASDASDAS",Toast.LENGTH_SHORT).show();
                                                wubbleList.addAll(new ArrayList<ParseObject>());
                                                mTextView.setVisibility(View.VISIBLE);
                                            } else if (wubbles.size() == 0) {
                                                wubbleList.addAll(new ArrayList<ParseObject>());
                                                mTextView.setVisibility(View.VISIBLE);
                                            } else {
                                                //Toast.makeText(context,"FFFF",Toast.LENGTH_SHORT).show();
                                                wubbleList.addAll(wubbles);
                                                mTextView.setVisibility(View.GONE);
                                            }


                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });


                                            mSpinner.setVisibility(View.GONE);
                                            feedRecyclerView.setVisibility(View.VISIBLE);

                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                mTextView.setVisibility(View.GONE);

                ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("FollowRelations");
                userQuery.whereEqualTo("Username", currentUsername);
                userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            followedUserList.clear();
                            if (parseObject.get("Following") == null) {
                                followedUserList.addAll(new ArrayList<String>());
                            } else {
                                followedUserList.addAll((ArrayList<String>) parseObject.get("Following"));
                            }
                            followedUserList.add(currentUsername);

                            feedRecyclerView.setVisibility(View.VISIBLE);

                            ParseQuery userQuery = ParseUser.getQuery();
                            userQuery.whereContainedIn("username", followedUserList);

                            userQuery.findInBackground(new FindCallback() {
                                @Override
                                public void done(List list, ParseException e) {
                                    if (e == null) {
                                        followedParseUserList.clear();
                                        if (list == null) {
                                            followedUserList.addAll(new ArrayList<String>());
                                        } else {
                                            followedParseUserList.addAll((ArrayList<ParseUser>) list);
                                        }

                                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
                                        query.whereContainedIn("User", followedUserList);
                                        query.orderByDescending("createdAt");

                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> wubbles, ParseException e) {
                                                if (e == null) {
                                                    wubbleList.clear();
                                                    if (wubbles == null) {
                                                        wubbleList.addAll(new ArrayList<ParseObject>());
                                                        mTextView.setVisibility(View.VISIBLE);
                                                    } else if (wubbles.size() == 0) {
                                                        wubbleList.addAll(new ArrayList<ParseObject>());
                                                        mTextView.setVisibility(View.VISIBLE);
                                                    } else {
                                                        wubbleList.addAll(wubbles);
                                                        mTextView.setVisibility(View.GONE);
                                                    }
                                                    isRefreshing = false;
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
                handler.post(refreshing);
            }

        });

        feedRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (feedRecyclerView != null && feedRecyclerView.getChildCount() > 0) {

                    // check if the first item of the list is visible
                    boolean canScrollUp = feedRecyclerView.canScrollVertically(-1);
                    boolean enable = false;
                    if (!canScrollUp) enable = true;

                    swipeRefreshLayout.setHorizontalScrollBarEnabled(true);
                    swipeRefreshLayout.setEnabled(enable);

                }

            }
        });


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.searchMovie:

                Intent toSearchMovie = new Intent(this, MovieSearchActivity.class);
                startActivity(toSearchMovie);
                break;

            case R.id.logOut:

                ParseUser.logOut();
                Intent toLogin = new Intent(this, LoginActivity.class);
                startActivity(toLogin);
                finish();
                break;


            case R.id.profileAct:

                Intent toProfile = new Intent(FeedActivity.this, ProfileActivity.class);
                startActivity(toProfile);
                break;

            case R.id.peopleSearch:

                Intent toPeopleSearch = new Intent(FeedActivity.this, PeopleSearchActivity.class);
                startActivityForResult(toPeopleSearch, REQUEST_PEOPLE_SEARCH);
                break;


        }


        return super.onOptionsItemSelected(item);
    }


}
