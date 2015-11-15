package com.proxima.Wubble.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.ProfileRecyclerAdapter;
import com.proxima.Wubble.misc.CustomImageView;

import java.util.List;


public class ProfileActivity extends ToolbarActivity {

    CustomImageView profilePic;

    private Context context;

    protected static ProfileRecyclerAdapter adapter;
    protected static List<ParseObject> myWubbles;
    protected TextView usernameTv;
    protected Button followerButton;
    protected Button followingButton;
    protected RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isTransferComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);

        mRecyclerView = (RecyclerView) findViewById(R.id.activityProfileRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        activateToolbar();
        context = this;
        followerButton = (Button) findViewById(R.id.followerButton);
        followingButton = (Button) findViewById(R.id.followingButton);

        profilePic = (CustomImageView) findViewById(R.id.profilePic);
        usernameTv = (TextView) findViewById(R.id.profileUsername);

        ParseUser currentUser = ParseUser.getCurrentUser();

        // init this user's own wubble list

        if (currentUser != null) {
            String currentUserName = currentUser.getUsername();
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
            query.whereEqualTo("User", currentUserName);
            query.orderByDescending("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> wubbles, ParseException e) {
                    if (e == null) {
                        myWubbles = wubbles;
                        adapter = new ProfileRecyclerAdapter(context, myWubbles);
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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


            case R.id.uploadPicture:

                Intent toUpload = new Intent(ProfileActivity.this, ImageUploadActivity.class);
                startActivity(toUpload);
                break;

            case R.id.peopleSearch:

                Intent toPeopleSearch = new Intent(ProfileActivity.this, PeopleSearchActivity.class);
                startActivity(toPeopleSearch);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


}
