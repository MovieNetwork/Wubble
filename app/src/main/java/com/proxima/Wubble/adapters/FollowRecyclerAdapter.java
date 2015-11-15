package com.proxima.Wubble.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.proxima.Wubble.R;
import com.proxima.Wubble.activities.OtherProfileActivity;
import com.proxima.Wubble.activities.ProfileActivity;
import com.proxima.Wubble.anim.AnimationUtils;
import com.proxima.Wubble.misc.CustomImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class FollowRecyclerAdapter extends RecyclerView.Adapter<FollowRecyclerAdapter.ViewHolder> {
    protected List<ParseUser> myFollowUsers;
    protected Context mContext;

    private ArrayList<String> mFollowedUsers = new ArrayList<>();

    private final String followerString = "Follower";
    private final String followingString = "Following";

    private ArrayList<String> followedUserList = new ArrayList<>();

    private String LOG_TAG;

    private boolean isTransferComplete = true;

    private ParseObject currentUserRelationObject;
    private ParseObject userToFollow;

    private int previousPosition = 0;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView username;
        public CustomImageView profileImage;
        public ImageButton followButton;

        public ViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.followRowUserName);
            profileImage = (CustomImageView) v.findViewById(R.id.followRowImage);
            followButton = (ImageButton) v.findViewById(R.id.peopleSearchFollowButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FollowRecyclerAdapter(List<ParseUser> myDataset, Context context) {
        myFollowUsers = myDataset;
        mContext = context;
        LOG_TAG = mContext.getClass().getSimpleName();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_row, parent, false);
        // set the view's size, margins, paddings and layout parameters


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final ParseObject userObject = myFollowUsers.get(position);
        final String username = userObject.getString("username");
        holder.username.setText(username);


        isTransferComplete = false;

        //set username image (profile image of that user) if exists

        ParseFile userProfilePic = (ParseFile) userObject.get("profilepic");

        if (userProfilePic != null) {

            String wubbleOwnerImageUrl = userProfilePic.getUrl();
            Picasso.with(mContext).load(wubbleOwnerImageUrl)
                    .error(R.drawable.ic_action_user)
                    .into(holder.profileImage);

        }
        // else set a default picture
        else {

            holder.profileImage.setImageResource(R.drawable.ic_action_user);
        }


        final ParseUser currentUser = ParseUser.getCurrentUser();
        final String currentUsername = currentUser.getUsername();

        ParseQuery<ParseObject> getFollowList = new ParseQuery<ParseObject>("FollowRelations");

        getFollowList.whereEqualTo("Username", currentUsername);

        getFollowList.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                followedUserList = (ArrayList<String>) parseObject.get("Following");


                if (followedUserList != null) {
                    if (followedUserList.contains(username)) {
                        holder.followButton.setImageResource(R.drawable.ic_action_accept);
                        holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonFollowedColor));

                    } else {
                        holder.followButton.setImageResource(R.drawable.ic_action_add_person);
                        holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));
                    }
                } else {
                    holder.followButton.setImageResource(R.drawable.ic_action_add_person);
                    holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));
                }

                if (currentUsername.equals(username)) {
                    holder.followButton.setVisibility(View.GONE);
                }
                isTransferComplete = true;
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferComplete) {

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    String curUsername = currentUser.getUsername();
                    String usernameToGo = myFollowUsers.get(position).getUsername();

                    if (curUsername.equals(usernameToGo) && !(mContext instanceof ProfileActivity)) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);

                        mContext.startActivity(intent);
                    } else if (!curUsername.equals(usernameToGo) && !(mContext instanceof OtherProfileActivity)) {
                        Intent intent = new Intent(mContext, OtherProfileActivity.class);
                        intent.putExtra("EXTRA_USER_NAME", usernameToGo);

                        mContext.startActivity(intent);
                        //overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
                    }
                }
            }


        });


        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferComplete) {
                    isTransferComplete = false;

                    ParseQuery<ParseObject> getUserToFollow = new ParseQuery<ParseObject>("FollowRelations");
                    getUserToFollow.whereEqualTo("Username", username);
                    try {
                        userToFollow = getUserToFollow.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ParseQuery<ParseObject> getFollowList = new ParseQuery<ParseObject>("FollowRelations");
                    getFollowList.whereEqualTo("Username", currentUsername);
                    try {
                        currentUserRelationObject = getFollowList.getFirst();
                        mFollowedUsers = (ArrayList<String>) currentUserRelationObject.get(followingString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (username != null) {

                        if (mFollowedUsers != null) {

                            if (!mFollowedUsers.contains(username)) {

                                currentUserRelationObject.addUnique(followingString, username);
                                userToFollow.addUnique(followerString, currentUsername);
                                try {
                                    currentUserRelationObject.save();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                userToFollow.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        isTransferComplete = true;
                                    }
                                });

                                holder.followButton.setImageResource(R.drawable.ic_action_accept);
                                holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonFollowedColor));

                            } else {
                                ArrayList<String> toRemove = new ArrayList<>();
                                ArrayList<String> toRemove2 = new ArrayList<>();
                                toRemove.add(username);
                                toRemove2.add(currentUsername);
                                currentUserRelationObject.removeAll(followingString, toRemove);
                                try {
                                    currentUserRelationObject.save();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //userObject.saveInBackground();

                                userToFollow.removeAll(followerString, toRemove2);
                                userToFollow.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        isTransferComplete = true;
                                    }
                                });

                                holder.followButton.setImageResource(R.drawable.ic_action_add_person);
                                holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));


                            }
                        } else {
                            currentUserRelationObject.addUnique(followingString, username);
                            userToFollow.addUnique(followerString, currentUsername);
                            try {
                                userObject.save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            currentUserRelationObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    isTransferComplete = true;
                                }
                            });
                            holder.followButton.setImageResource(R.drawable.ic_action_add_person);
                            holder.followButton.setBackgroundColor(mContext.getResources().getColor(R.color.FollowButtonNotFollowedColor));

                        }
                    } else {
                        isTransferComplete = true;
                    }
                }
            }
        });


        if (position >= previousPosition) {
            AnimationUtils.animate(holder, true);
        } else {
            AnimationUtils.animate(holder, false);
        }
        previousPosition = position;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myFollowUsers.size();
    }
}
