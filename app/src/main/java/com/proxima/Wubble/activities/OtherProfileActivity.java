package com.proxima.Wubble.activities;

/**
 * Created by Epokhe on 08.02.2015.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.OtherProfileRecyclerAdapter;

import java.util.List;


public class OtherProfileActivity extends ToolbarActivity {

    ParseUser relatedUser;

    private Context context;

    protected static OtherProfileRecyclerAdapter adapter;
    protected static List<ParseObject> myWubbles;
    protected String curUsername;
    protected String incUsername = null;
    protected RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    protected static ParseUser currentUser;
    private String LOG_TAG = OtherProfileActivity.class.getSimpleName();
    private boolean isTransferComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        activateToolbar();

        context = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.otherProfileRecyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        final Bundle extras = getIntent().getExtras();


        if (extras != null) {
            incUsername = extras.getString("EXTRA_USER_NAME");
        } else {
            //handle errors
        }


        currentUser = ParseUser.getCurrentUser();
        curUsername = ParseUser.getCurrentUser().getUsername();

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", incUsername);
        try {
            relatedUser = userQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // init this user's own wubble list

        if (relatedUser != null) {
            String currentUserName = relatedUser.getUsername();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
            query.whereEqualTo("User", currentUserName);
            query.orderByDescending("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> wubbles, ParseException e) {
                    if (e == null) {
                        myWubbles = wubbles;
                        adapter = new OtherProfileRecyclerAdapter(context, myWubbles, incUsername);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        // handle problems here
                    }
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other_profile, menu);
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

            case R.id.logOut:

                ParseUser.logOut();
                Intent toLogin = new Intent(this, LoginActivity.class);
                startActivity(toLogin);
                finish();
                break;


            case R.id.profileAct:

                Intent toProfile = new Intent(OtherProfileActivity.this, ProfileActivity.class);
                startActivity(toProfile);
                break;

            case R.id.peopleSearch:

                Intent toPeopleSearch = new Intent(OtherProfileActivity.this, PeopleSearchActivity.class);
                startActivity(toPeopleSearch);
                break;


        }


        return super.onOptionsItemSelected(item);
    }


}

