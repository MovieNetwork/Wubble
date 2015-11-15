package com.proxima.Wubble.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.FollowRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends ToolbarActivity {

    private Context context;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseUser> mFollowUserList;
    private ArrayList<String> followList = new ArrayList<String>();
    final private String LOG_TAG = FollowActivity.class.getSimpleName();

    private TextView mTextView;


    private boolean isTransferComplete = true;

    private String mPageName; // following or follower . get it from extras
    private String currentUsername;
    private final String followerString = "Follower";
    private final String followingString = "Following";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_recycle);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTextView = (TextView) findViewById(R.id.followRecyclerTextView);
        activateToolbar();
        context = this;
        final ParseUser currentUser = ParseUser.getCurrentUser();
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Bundle extras = getIntent().getExtras();

        if (currentUser != null) {
            currentUsername = (String) extras.get("userType");
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser thisUser, ParseException e) {
                    if (e == null && extras != null) {

                        mPageName = (String) extras.get("followType");

                        if (mPageName.equals(followerString)) {
                            ParseQuery<ParseObject> getFollowList = new ParseQuery<ParseObject>("FollowRelations");
                            getFollowList.whereEqualTo("Username", currentUsername);
                            try {
                                followList = (ArrayList<String>) getFollowList.getFirst().get(followerString);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        } else if (mPageName.equals(followingString)) {
                            ParseQuery<ParseObject> getFollowList = new ParseQuery<ParseObject>("FollowRelations");
                            getFollowList.whereEqualTo("Username", currentUsername);
                            try {
                                followList = (ArrayList<String>) getFollowList.getFirst().get(followingString);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }

                        if (followList != null && followList.size() > 0) {
                            mTextView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                            query2.whereContainedIn("username", followList);
                            query2.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> followUser, ParseException e) {
                                    if (e == null) {
                                        if (followUser.size() > 0) {

                                            mTextView.setVisibility(View.GONE);
                                            mRecyclerView.setVisibility(View.VISIBLE);

                                            mFollowUserList = followUser;
                                            mAdapter = new FollowRecyclerAdapter(mFollowUserList, context);
                                            mRecyclerView.setAdapter(mAdapter);
                                        } else {
                                            mTextView.setVisibility(View.VISIBLE);
                                            mRecyclerView.setVisibility(View.GONE);

                                        }

                                    } else {
                                        // handle problems here
                                    }
                                }
                            });
                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);

                        }

                    }
                }
            });

        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mPageName.equals(followerString)) {
            ParseQuery<ParseObject> getFollowList = new ParseQuery<>("FollowRelations");
            getFollowList.whereEqualTo("Username", currentUsername);
            try {
                followList = (ArrayList<String>) getFollowList.getFirst().get(followerString);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        } else if (mPageName.equals(followingString)) {
            ParseQuery<ParseObject> getFollowList = new ParseQuery<>("FollowRelations");
            getFollowList.whereEqualTo("Username", currentUsername);
            try {
                followList = (ArrayList<String>) getFollowList.getFirst().get(followingString);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }

        if (followList != null && followList.size() > 0) {
            mTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            ParseQuery<ParseUser> query2 = ParseUser.getQuery();
            query2.whereContainedIn("username", followList);
            query2.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> followUser, ParseException e) {
                    if (e == null) {
                        if (followUser.size() > 0) {

                            mTextView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            mFollowUserList = followUser;
                            mAdapter = new FollowRecyclerAdapter(mFollowUserList, context);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);

                        }

                    } else {
                        // handle problems here
                    }
                }
            });
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        }
    }

}