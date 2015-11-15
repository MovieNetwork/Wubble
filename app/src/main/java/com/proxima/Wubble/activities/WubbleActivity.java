package com.proxima.Wubble.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.listeners.DislikeClickHandler;
import com.proxima.Wubble.listeners.LikeClickHandler;
import com.proxima.Wubble.misc.CustomImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class WubbleActivity extends ToolbarActivity {

    private Context context;

    private TextView mProfileName;
    private TextView mMovieName;
    private TextView mMovieComment;
    private TextView mWhoLiked;
    private TextView mWhoDisliked;
    private ImageView mWhoLikedLine;
    private ImageView mWhoDislikedLine;
    private TextView mLikeCounter;
    private TextView mDislikeCounter;

    private ImageView mLikeButton;
    private ImageView mDislikeButton;
    private CustomImageView mProfileImage;

    private String mObjectId;
    private String profileName;
    private String movieName;
    private String movieComment;
    public ArrayList<String> likeList;
    public ArrayList<String> dislikeList;
    private ParseFile mPhoto;

    private int lastPressedPosition = 0;

    private boolean isTransferComplete = true;

    public Intent intent;

    Bundle extras;
    ParseUser currentUser;
    String currentUserName;


    private ParseObject mObject;


    private String LOG_TAG = FeedActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wubble);
        activateToolbar();

        context = this;

        mProfileName = (TextView) findViewById(R.id.wubbleprofilename);
        mMovieName = (TextView) findViewById(R.id.wubblemoviename);
        mMovieComment = (TextView) findViewById(R.id.wubblemoviecomment);
        mWhoLiked = (TextView) findViewById(R.id.wubblewholiked);
        mWhoDisliked = (TextView) findViewById(R.id.wubblewhodisliked);
        mWhoLikedLine = (ImageView) findViewById(R.id.wubblePageLikeLine);
        mWhoDislikedLine = (ImageView) findViewById(R.id.wubblePageDislikeLine);
        mProfileImage = (CustomImageView) findViewById(R.id.wubbleprofilepicture);
        mLikeButton = (ImageView) findViewById(R.id.wubblePageLikeButton);
        mDislikeButton = (ImageView) findViewById(R.id.wubblePageDislikeButton);
        mLikeCounter = (TextView) findViewById(R.id.wubblePageLikeCounter);
        mDislikeCounter = (TextView) findViewById(R.id.wubblePageDislikeCounter);


        intent = getIntent();
        extras = intent.getExtras();

        setResult(RESULT_OK, intent);
        currentUser = ParseUser.getCurrentUser();
        currentUserName = currentUser.getUsername();

        if (extras != null) {


            profileName = extras.getString("name");
            movieName = extras.getString("movieName");
            movieComment = extras.getString("comment");

            likeList = new ArrayList<String>();
            likeList = extras.getStringArrayList("likeList");

            dislikeList = new ArrayList<String>();
            dislikeList = extras.getStringArrayList("dislikeList");


            refreshLikeList();

            ParseQuery<ParseObject> objectQuery = ParseQuery.getQuery("Wubbles");

            objectQuery.getInBackground(mObjectId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {

                    mObject = parseObject;

                    mLikeButton.setOnClickListener(new LikeClickHandler(context, mObject, likeList, dislikeList, currentUserName, mLikeButton, mDislikeButton, mLikeCounter, mDislikeCounter));
                    mDislikeButton.setOnClickListener(new DislikeClickHandler(context, mObject, likeList, dislikeList, currentUserName, mLikeButton, mDislikeButton, mLikeCounter, mDislikeCounter));

                }
            });

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", profileName);

            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    mPhoto = (ParseFile) parseUser.get("profilepic");

                    if (mPhoto == null)
                        mProfileImage.setImageResource(R.drawable.ic_action_user);
                    else {
                        Picasso.with(WubbleActivity.this).load(mPhoto.getUrl())
                                .error(R.drawable.ic_action_user)
                                .into(mProfileImage);
                    }
                }
            });


        }
    }

    public void refreshLikeList() {
        if (likeList == null)
            mLikeCounter.setText(Integer.toString(0));
        else
            mLikeCounter.setText(Integer.toString(likeList.size()));

        if (dislikeList == null)
            mDislikeCounter.setText(Integer.toString(0));
        else
            mDislikeCounter.setText(Integer.toString(dislikeList.size()));

        mObjectId = extras.getString("objectId");

        mProfileName.setText(profileName + " ");
        mMovieName.setText(movieName);
        mMovieComment.setText(movieComment);


        String likeString = new String();
        if (likeList != null && likeList.size() != 0) {
            if (likeList.size() == 1)
                likeString = likeList.get(0) + " liked this.";
            else if (likeList.size() == 2)
                likeString = likeList.get(0) + " and " + likeList.get(1) + " liked this.";
            else
                likeString = likeList.get(0) + ", " + likeList.get(1) + " and " + (likeList.size() - 2) + ((likeList.size() == 3) ? " other" : " others") + " liked this.";
        }

        String dislikeString = new String();
        if ((currentUserName.equals(profileName)) && dislikeList != null && dislikeList.size() != 0) {
            if (dislikeList.size() == 1)
                dislikeString = dislikeList.get(0) + " disliked this.";
            else if (dislikeList.size() == 2)
                dislikeString = dislikeList.get(0) + " and " + dislikeList.get(1) + " disliked this.";
            else
                dislikeString = dislikeList.get(0) + ", " + dislikeList.get(1) + " and " + (dislikeList.size() - 2) + ((dislikeList.size() == 3) ? " other" : " others") + " disliked this.";
        }

        if (!currentUserName.equals(profileName)) {
            mWhoDisliked.setVisibility(View.GONE);
            mWhoDislikedLine.setVisibility(View.GONE);
        } else if (dislikeList.size() > 0) {
            mWhoDisliked.setVisibility(View.VISIBLE);
            mWhoDislikedLine.setVisibility(View.VISIBLE);
        } else {
            mWhoDisliked.setVisibility(View.GONE);
            mWhoDislikedLine.setVisibility(View.GONE);
        }

        if (likeList == null || likeList.size() == 0) {
            mWhoLiked.setVisibility(View.GONE);
            mWhoLikedLine.setVisibility(View.GONE);
        } else if (likeList.size() > 0) {
            mWhoLiked.setVisibility(View.VISIBLE);
            mWhoLikedLine.setVisibility(View.VISIBLE);
        }


        if (likeList != null && likeList.contains(currentUserName))
            mLikeButton.setImageResource(R.drawable.ic_action_good_2);
        if (dislikeList != null && dislikeList.contains(currentUserName))
            mDislikeButton.setImageResource(R.drawable.ic_action_bad_2);


        mWhoLiked.setText(likeString);
        mWhoDisliked.setText(dislikeString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wubble, menu);
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

                Intent toProfile = new Intent(WubbleActivity.this, ProfileActivity.class);
                startActivity(toProfile);
                break;

            case R.id.peopleSearch:

                Intent toPeopleSearch = new Intent(WubbleActivity.this, PeopleSearchActivity.class);
                startActivity(toPeopleSearch);
                break;


        }

        return super.onOptionsItemSelected(item);
    }
}
