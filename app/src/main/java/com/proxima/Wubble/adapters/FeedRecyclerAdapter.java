package com.proxima.Wubble.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.activities.FeedActivity;
import com.proxima.Wubble.activities.MovieActivity;
import com.proxima.Wubble.activities.OtherProfileActivity;
import com.proxima.Wubble.activities.ProfileActivity;
import com.proxima.Wubble.activities.WubbleActivity;
import com.proxima.Wubble.anim.AnimationUtils;
import com.proxima.Wubble.listeners.DislikeClickHandler;
import com.proxima.Wubble.listeners.LikeClickHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.proxima.Wubble.Constants.REQUEST_MOVIE;
import static com.proxima.Wubble.Constants.REQUEST_OTHER_PROFILE;
import static com.proxima.Wubble.Constants.REQUEST_OWN_PROFILE;
import static com.proxima.Wubble.Constants.REQUEST_WUBBLE;

/**
 * Created by MUSTAFA on 14.02.2015.
 */
public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {
    protected Context myContext;
    protected List<ParseObject> myWubbles;
    protected ParseObject wubbleObject;
    private ArrayList<ParseUser> mUserList;
    ArrayList<String> likeList = new ArrayList<>();
    ArrayList<String> dislikeList = new ArrayList<>();
    protected final String LOG_TAG = FeedRecyclerAdapter.class.getSimpleName();

    public ArrayList<Integer> noRefreshPositions = new ArrayList<>();


    private int previousPosition = 0;

    private LayoutInflater layoutInflater;

    public boolean isRefresh = true;

    private ParseFile userPhoto;
    private Map mPhotos = new HashMap();


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
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public FeedRecyclerAdapter(Context context, List<ParseObject> myWubbleDataSet, ArrayList<ParseUser> myUserDataSet) {
        myWubbles = myWubbleDataSet;


        mUserList = myUserDataSet;
        myContext = context;
        layoutInflater = LayoutInflater.from(myContext);


    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = layoutInflater.inflate(R.layout.row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        wubbleObject = myWubbles.get(position);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        final String currentUserName = currentUser.getUsername();

        final String objectId = wubbleObject.getObjectId();


        // set username
        final String username = wubbleObject.getString("User");
        holder.username.setText(username);

        // set content
        final String wubbleComment = wubbleObject.getString("newWubble");
        holder.wubbleMessage.setText(wubbleComment);


        // set movieName
        final String movieName = wubbleObject.getString("Movie");
        holder.movie.setText(movieName);

        final int movieId = wubbleObject.getInt("movieId");


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


        // new faster implementation to get photos. using hashmap and predownloaded content(userlist)
        if (mPhotos.get(username) == null) {
            for (int i = 0; i < mUserList.size(); i++) {
                ParseUser tempUser = mUserList.get(i);
                if (tempUser.get("username").equals(username)) {
                    userPhoto = (ParseFile) tempUser.get("profilepic");
                    break;
                }
            }

            if (userPhoto == null) {
                holder.profileImage.setImageResource(R.drawable.ic_action_user);
            } else {
                String mUrl = userPhoto.getUrl();
                Picasso.with(myContext).load(mUrl)
                        .error(R.drawable.ic_action_user)
                        .into(holder.profileImage);
                mPhotos.put(username, mUrl);
            }
        } else {
            String mUrl = (String) mPhotos.get(username);
            Picasso.with(myContext).load(mUrl)
                    .error(R.drawable.ic_action_user)
                    .into(holder.profileImage);
        }


        //called when user presses the profile image in a wubble
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String curUsername = currentUser.getUsername();
                if (curUsername.equals(username) && !(myContext instanceof ProfileActivity)) {
                    Intent intent = new Intent(myContext, ProfileActivity.class);

                    ((Activity) myContext).startActivityForResult(intent, REQUEST_OWN_PROFILE);
                } else if (!curUsername.equals(username) && !(myContext instanceof OtherProfileActivity)) {
                    Intent intent = new Intent(myContext, OtherProfileActivity.class);
                    intent.putExtra("EXTRA_USER_NAME", username);
                    ((Activity) myContext).startActivityForResult(intent, REQUEST_OTHER_PROFILE);
                } else {

                }
            }
        });


        //called when user presses to a wubble(the space in wubble not occupied by other buttons)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toWubble = new Intent(myContext, WubbleActivity.class);
                toWubble.putExtra("position", position);
                toWubble.putExtra("name", username);
                toWubble.putExtra("movieName", movieName);
                toWubble.putExtra("comment", wubbleComment);

                wubbleObject = myWubbles.get(position);

                likeList = ((ArrayList<String>) wubbleObject.get("likedBy") == null) ? new ArrayList<String>() : (ArrayList<String>) wubbleObject.get("likedBy");
                dislikeList = ((ArrayList<String>) wubbleObject.get("dislikedBy") == null) ? new ArrayList<String>() : (ArrayList<String>) wubbleObject.get("dislikedBy");


                toWubble.putExtra("likeList", likeList);
                toWubble.putExtra("dislikeList", dislikeList);

                toWubble.putExtra("objectId", objectId);

                ((FeedActivity) myContext).setLastPressedPosition(position);

                ((Activity) myContext).startActivityForResult(toWubble, REQUEST_WUBBLE);
                ((Activity) myContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


            }
        });

        holder.movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent toMovie = new Intent(myContext, MovieActivity.class);
                Intent toMovie = new Intent(myContext, MovieActivity.class);
                toMovie.putExtra("movieName", movieName);
                toMovie.putExtra("movieId", movieId);


                ((Activity) myContext).startActivityForResult(toMovie, REQUEST_MOVIE);
                ((Activity) myContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });


        wubbleObject = myWubbles.get(position);
        holder.likeButton.setOnClickListener(new LikeClickHandler(myContext, wubbleObject, likeList, dislikeList, currentUserName, holder.likeButton, holder.dislikeButton, holder.likeCounter, holder.dislikeCounter));
        holder.dislikeButton.setOnClickListener(new DislikeClickHandler(myContext, wubbleObject, likeList, dislikeList, currentUserName, holder.likeButton, holder.dislikeButton, holder.likeCounter, holder.dislikeCounter));


        if (noRefreshPositions.isEmpty()) {
            if (position >= previousPosition) {
                AnimationUtils.animate(holder, true);
            } else {
                AnimationUtils.animate(holder, false);
            }
        } else {
            noRefreshPositions.remove(0);
        }
        previousPosition = position;


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (myWubbles == null)
            return 0;
        else
            return myWubbles.size();
    }
}

