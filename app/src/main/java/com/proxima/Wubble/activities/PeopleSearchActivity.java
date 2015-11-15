package com.proxima.Wubble.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.FollowRecyclerAdapter;

import java.util.List;


public class PeopleSearchActivity extends ToolbarActivity {


    private String LOG_TAG = PeopleSearchActivity.class.getSimpleName();

    private Context context;

    private List<ParseUser> mUsers;
    private FollowRecyclerAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;


    private Button searchButton;
    private TextView searchBox;

    public Intent intent;

    private boolean isTransferCompleted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_people_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.peopleSearchRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        activateToolbar();

        searchButton = (Button) findViewById(R.id.searchButton);
        searchBox = (TextView) findViewById(R.id.searchBox);

        intent = getIntent();

        setResult(RESULT_OK, intent);


        final ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {

            ParseQuery<ParseUser> query = ParseUser.getQuery();

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {

                    if (e == null) {
                        mUsers = parseUsers;
                        adapter = new FollowRecyclerAdapter(mUsers, context);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        //TODO handle errors here
                    }
                }
            });
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferCompleted) {
                    isTransferCompleted = false;
                    String searchString = searchBox.getText().toString().trim();

                    if (searchString.isEmpty()) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();

                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> parseUsers, ParseException e) {

                                isTransferCompleted = true;
                                if (e == null) {
                                    mUsers = parseUsers;
                                    adapter = new FollowRecyclerAdapter(mUsers, context);
                                    mRecyclerView.setAdapter(adapter);
                                } else {
                                    //TODO handle errors here
                                }
                            }
                        });

                    } else {

                        ParseQuery<ParseUser> searchQuery = ParseUser.getQuery();

                        searchQuery.whereEqualTo("username", searchString);

                        searchQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    isTransferCompleted = true;
                                    mUsers.clear();
                                    mUsers.add(parseUser);
                                    adapter = new FollowRecyclerAdapter(mUsers, mRecyclerView.getContext());
                                    mRecyclerView.setAdapter(adapter);
                                } else {
                                    isTransferCompleted = true;
                                    Toast.makeText(getBaseContext(), "User not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people_search, menu);
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
            case R.id.newWubblePeopleSearch:

                Intent toNewWubble = new Intent(this, NewWubbleActivity.class);
                startActivity(toNewWubble);
                break;

            case R.id.profileActPeopleSearch:

                Intent toProfile = new Intent(PeopleSearchActivity.this, ProfileActivity.class);
                startActivity(toProfile);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
