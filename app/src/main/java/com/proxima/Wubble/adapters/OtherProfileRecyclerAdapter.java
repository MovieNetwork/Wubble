package com.proxima.Wubble.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.activities.FollowActivity;
import com.proxima.Wubble.activities.OtherProfileActivity;
import com.proxima.Wubble.activities.ProfileActivity;
import com.proxima.Wubble.activities.WubbleActivity;
import com.proxima.Wubble.listeners.DislikeClickHandler;
import com.proxima.Wubble.listeners.LikeClickHandler;
import com.proxima.Wubble.misc.CustomImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MUSTAFA on 15.02.2015.
 */
public class OtherProfileRecyclerAdapter extends RecyclerView.Adapter<OtherProfileRecyclerAdapter.ViewHolder> {
    protected Context mContext;
    protected List<ParseObject> myWubbles;
    protected ParseObject wubbleObject;
    ArrayList<String> likeList = new ArrayList<>();
    ArrayList<String> dislikeList = new ArrayList<>();
    protected final String LOG_TAG = OtherProfileRecyclerAdapter.class.getSimpleName();

    private String incUsername;
    protected static ParseUser currentUser;
    protected static ParseObject userObjectToFollow;
    protected static ParseObject currentUserObject;
    private final String followerString = "Follower";
    private final String followingString = "Following"; //oha object oriented :DD
    private ParseUser relatedUser;
    protected ArrayList<String> mFollowedUsers = new ArrayList<>();
    private Map mPhotos = new HashMap<>();
    private ParseFile userPhoto;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView username;
        private TextView wubbleMessage;
        private ImageView profileImage;
        private TextView movie;
        private ImageView likeButton;
        private ImageView dislikeButton;
        private TextView likeCounter;
        private TextView dislikeCounter;
        private ImageButton mFollowButton;
        private Button mFollowingButton;
        private Button mFollowerButton;
        private TextView mUsernameTv;
        private CustomImageView mProfilePic;
        private ProgressBar mSpinner;

        public ViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.userNameRow);
            wubbleMessage = (TextView) v.findViewById(R.id.commentTextView);
            profileImage = (ImageView) v.findViewById(R.id.imageView);
            movie = (TextView) v.findViewById(R.id.movieView);
            likeButton = (ImageView) v.findViewById(R.id.rowLikeButton);
            dislikeButton = (ImageView) v.findViewById(R.id.rowDislikeButton);
            likeCounter = (TextView) v.findViewById(R.id.rowLikeCounter);
            dislikeCounter = (TextView) v.findViewById(R.id.rowDislikeCounter);

            mProfilePic = (CustomImageView) v.findViewById(R.id.otherProfilePic);
            mUsernameTv = (TextView) v.findViewById(R.id.otherProfileUsername);

            mFollowButton = (ImageButton) v.findViewById(R.id.followMeButton);
            mFollowingButton = (Button) v.findViewById(R.id.otherProfileFollowings);
            mFollowerButton = (Button) v.findViewById(R.id.otherProfileFollowers);


            mSpinner = (ProgressBar) v.findViewById(R.id.otherProfileSpinner);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OtherProfileRecyclerAdapter(Context context, List<ParseObject> myDataset, String aUser) {
        myWubbles = myDataset;
        mContext = context;
        incUsername = aUser;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OtherProfileRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = null;

        if (viewType != 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.otherprofilerow, parent, false);
        }

        ViewHolder vh = new ViewHolder(v);


        return vh;


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element


        if (position != 0) {
            wubbleObject = myWubbles.get(position - 1);

            final ParseUser currentUser = ParseUser.getCurrentUser();
            final String currentUserName = currentUser.getUsername();

            final String objectId = wubbleObject.getObjectId();

            // set username
            final String username = wubbleObject.getString("User");
            holder.username.setText(username);

            // set content
            final String wubbleComment = wubbleObject.getString("newWubble");
            holder.wubbleMessage.setText(wubbleComment);


            // set movie
            final String movie = wubbleObject.getString("Movie");
            holder.movie.setText(movie);

            //set like and dislike count
            likeList = (ArrayList<String>) wubbleObject.get("likedBy");
            if (likeList == null)
                holder.likeCounter.setText("0");
            else
                holder.likeCounter.setText(String.valueOf(likeList.size()));

            dislikeList = (ArrayList<String>) wubbleObject.get("dislikedBy");
            if (dislikeList == null)
                holder.dislikeCounter.setText("0");
            else
                holder.dislikeCounter.setText(String.valueOf(dislikeList.size()));

            //set like and dislike button colour initially
            if (likeList != null && likeList.contains(currentUserName))
                holder.likeButton.setImageResource(R.drawable.ic_action_good_2);
            else
                holder.likeButton.setImageResource(R.drawable.ic_action_good);

            if (dislikeList != null && dislikeList.contains(currentUserName))
                holder.dislikeButton.setImageResource(R.drawable.ic_action_bad_2);
            else
                holder.dislikeButton.setImageResource(R.drawable.ic_action_bad);


            if (mPhotos.get(username) == null) {
                //Log.d(LOG_TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
                ParseQuery userQuery = ParseUser.getQuery();
                userQuery.whereEqualTo("username", username);

                userQuery.getFirstInBackground(new GetCallback() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            userPhoto = (ParseFile) parseObject.get("profilepic");
                            if (userPhoto == null) {
                                holder.profileImage.setImageResource(R.drawable.ic_action_user);
                            } else {
                                String mUrl = userPhoto.getUrl();
                                Picasso.with(mContext).load(mUrl)
                                        .error(R.drawable.ic_action_user)
                                        .into(holder.profileImage);
                                mPhotos.put(username, mUrl);
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            } else {
                String mUrl = (String) mPhotos.get(username);
                Picasso.with(mContext).load(mUrl)
                        .error(R.drawable.ic_action_user)
                        .into(holder.profileImage);
            }

            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    String curUsername = currentUser.getUsername();
                    if (curUsername.equals(username) && !(mContext instanceof ProfileActivity)) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        mContext.startActivity(intent);
                    } else if (!curUsername.equals(username) && !(mContext instanceof OtherProfileActivity)) {
                        Intent intent = new Intent(mContext, OtherProfileActivity.class);
                        intent.putExtra("EXTRA_USER_NAME", username);
                        mContext.startActivity(intent);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toWubble = new Intent(mContext, WubbleActivity.class);
                    toWubble.putExtra("name", username);
                    toWubble.putExtra("movie", movie);
                    toWubble.putExtra("comment", wubbleComment);

                    wubbleObject = myWubbles.get(position - 1);

                    likeList = null;
                    likeList = (ArrayList<String>) wubbleObject.get("likedBy");

                    dislikeList = null;
                    dislikeList = (ArrayList<String>) wubbleObject.get("dislikedBy");

                    if (likeList != null)
                        toWubble.putExtra("likeList", likeList);

                    if (dislikeList != null)
                        toWubble.putExtra("dislikeList", dislikeList);

                    toWubble.putExtra("objectId", objectId);

                    mContext.startActivity(toWubble);
                }
            });


            wubbleObject = myWubbles.get(position - 1);
            holder.likeButton.setOnClickListener(new LikeClickHandler(mContext, wubbleObject, likeList, dislikeList, currentUserName, holder.likeButton, holder.dislikeButton, holder.likeCounter, holder.dislikeCounter));
            holder.dislikeButton.setOnClickListener(new DislikeClickHandler(mContext, wubbleObject, likeList, dislikeList, currentUserName, holder.likeButton, holder.dislikeButton, holder.likeCounter, holder.dislikeCounter));


        } else {
            holder.mProfilePic.setVisibility(View.GONE);
            holder.mSpinner.setVisibility(View.VISIBLE);
            currentUser = ParseUser.getCurrentUser();
            final String curUsername = ParseUser.getCurrentUser().getUsername();

            ParseQuery<ParseObject> getFollowList = new ParseQuery<ParseObject>("FollowRelations");
            getFollowList.whereEqualTo("Username", curUsername);
            try {
                currentUserObject = getFollowList.getFirst();
                mFollowedUsers = (ArrayList<String>) currentUserObject.get(followingString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<ParseObject> getUserToFollow = new ParseQuery<ParseObject>("FollowRelations");
            getUserToFollow.whereEqualTo("Username", incUsername);
            try {
                userObjectToFollow = getUserToFollow.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (mFollowedUsers != null) {
                if (mFollowedUsers.contains(incUsername)) {
                    holder.mFollowButton.setImageResource(R.drawable.ic_action_accept);
                    holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonFollowedColor));
                } else {
                    holder.mFollowButton.setImageResource(R.drawable.ic_action_add_person);
                    holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));

                }
            } else {
                holder.mFollowButton.setImageResource(R.drawable.ic_action_add_person);
                holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));
            }

            holder.mUsernameTv.setText(incUsername);

            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("username", incUsername);
            try {
                relatedUser = userQuery.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            int defaultPicId = mContext.getResources().getIdentifier("ic_action_user", "drawable", mContext.getPackageName());
            boolean hasPicture = relatedUser.getBoolean("hasPicture");

            if (hasPicture) {
                ParseFile currentProfileImage = (ParseFile) relatedUser.get("profilepic");

                currentProfileImage.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Bitmap tempBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            holder.mProfilePic.setImageBitmap(tempBmp);
                            holder.mProfilePic.setVisibility(View.VISIBLE);
                            holder.mSpinner.setVisibility(View.GONE);

                        } else {
                            holder.mProfilePic.setVisibility(View.VISIBLE);
                            holder.mSpinner.setVisibility(View.GONE);
                            Toast.makeText(mContext, "Profile Picture can not be shown", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                holder.mProfilePic.setVisibility(View.VISIBLE);
                holder.mSpinner.setVisibility(View.GONE);
                holder.mProfilePic.setImageResource(defaultPicId);
            }


            holder.mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFollowedUsers = (ArrayList<String>) currentUserObject.get("Following");


                    if (incUsername != null) {
                        if (mFollowedUsers != null) {
                            if (!mFollowedUsers.contains(incUsername)) {
                                currentUserObject.addUnique(followingString, incUsername);
                                userObjectToFollow.addUnique(followerString, curUsername);
                                currentUserObject.saveInBackground();
                                userObjectToFollow.saveInBackground();
                                //mFollowButton.setText("Following");
                                holder.mFollowButton.setImageResource(R.drawable.ic_action_accept);
                                holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonFollowedColor));
                                //mFollowButton.setBackgroundColor(Color.rgb(240, 136, 7));
                            } else {
                                ArrayList<String> toRemove = new ArrayList<>();
                                ArrayList<String> toRemove2 = new ArrayList<>();
                                toRemove.add(incUsername);
                                toRemove2.add(curUsername);
                                currentUserObject.removeAll(followingString, toRemove);
                                currentUserObject.saveInBackground();
                                userObjectToFollow.removeAll(followerString, toRemove2);
                                userObjectToFollow.saveInBackground();
                                //mFollowButton.setText("Follow");
                                holder.mFollowButton.setImageResource(R.drawable.ic_action_add_person);
                                holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));
                                //mFollowButton.setBackgroundColor(Color.rgb(167, 189, 199));
                            }
                        } else {
                            currentUserObject.addUnique(followingString, incUsername);
                            userObjectToFollow.addUnique(followerString, curUsername);
                            currentUserObject.saveInBackground();
                            userObjectToFollow.saveInBackground();
                            //mFollowButton.setText("Following");
                            holder.mFollowButton.setImageResource(R.drawable.ic_action_accept);
                            holder.mFollowButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonFollowedColor));
                            //mFollowButton.setBackgroundColor(Color.rgb(240, 136, 7));
                        }
                    }
                }
            });

            holder.mFollowerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToFollowsWithFollower = new Intent(mContext, FollowActivity.class);
                    goToFollowsWithFollower.putExtra("followType", "Follower");
                    goToFollowsWithFollower.putExtra("userType", incUsername);
                    mContext.startActivity(goToFollowsWithFollower);
                }
            });

            holder.mFollowingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToFollowsWithFollowing = new Intent(mContext, FollowActivity.class);
                    goToFollowsWithFollowing.putExtra("followType", "Following");
                    goToFollowsWithFollowing.putExtra("userType", incUsername);
                    mContext.startActivity(goToFollowsWithFollowing);
                }
            });


        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (myWubbles == null)
            return 1;
        else
            return myWubbles.size() + 1;
    }
}