package com.proxima.Wubble.listeners;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.proxima.Wubble.R;
import com.proxima.Wubble.activities.WubbleActivity;
import com.proxima.Wubble.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Epokhe on 21.02.2015.
 */
public class DislikeClickHandler implements View.OnClickListener {

    private Context myContext;

    VolleySingleton volleySingleton;

    private ImageView dislikeButton;
    private ImageView likeButton;

    private TextView likeCounter;
    private TextView dislikeCounter;

    private ParseObject wubbleObject;


    private ArrayList<String> likeList;
    private ArrayList<String> dislikeList;

    private String currentUserName;

    public DislikeClickHandler(Context myContext,
                               ParseObject wubbleObject,
                               ArrayList<String> likeList,
                               ArrayList<String> dislikeList,
                               String currentUserName,
                               ImageView likeButton,
                               ImageView dislikeButton,
                               TextView likeCounter,
                               TextView dislikeCounter) {

        this.wubbleObject = wubbleObject;
        this.likeList = likeList;
        this.dislikeList = dislikeList;
        this.currentUserName = currentUserName;
        this.dislikeButton = dislikeButton;
        this.likeButton = likeButton;
        this.likeCounter = likeCounter;
        this.dislikeCounter = dislikeCounter;

        this.myContext = myContext;


    }

    @Override
    public void onClick(View v) {


        volleySingleton = VolleySingleton.getInstance();

        if (volleySingleton.getTransferState()) {

            volleySingleton.setTransferState(false);//isTransferComplete = false;
            dislikeList = (ArrayList<String>) wubbleObject.get("dislikedBy");
            int dislikeCount = 0;

            if (dislikeList != null && dislikeList.size() > 0) {
                if (dislikeList.contains(currentUserName)) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    toRemove.add(currentUserName);
                    dislikeList.remove(currentUserName);
                    wubbleObject.removeAll("dislikedBy", toRemove);
                    dislikeButton.setImageResource(R.drawable.ic_action_bad);
                    wubbleObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            volleySingleton.setTransferState(true);//isTransferComplete = true;
                        }
                    });
                    dislikeCount = dislikeList.size();
                    dislikeCounter.setText(String.valueOf(dislikeCount));
                } else {
                    dislikeList.add(currentUserName);
                    wubbleObject.addUnique("dislikedBy", currentUserName);
                    likeList = (ArrayList<String>) wubbleObject.get("likedBy");
                    if (likeList != null && likeList.contains(currentUserName)) {
                        ArrayList<String> toRemove = new ArrayList<>();
                        toRemove.add(currentUserName);
                        likeList.remove(currentUserName);
                        wubbleObject.removeAll("likedBy", toRemove);
                        likeCounter.setText(String.valueOf(likeList.size()));
                        likeButton.setImageResource(R.drawable.ic_action_good);
                    }
                    dislikeButton.setImageResource(R.drawable.ic_action_bad_2);
                    wubbleObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            volleySingleton.setTransferState(true);//isTransferComplete = true;
                        }
                    });
                    dislikeCount = dislikeList.size();
                    dislikeCounter.setText(String.valueOf(dislikeCount));
                }

            } else {
                dislikeList = new ArrayList<>();
                dislikeList.add(currentUserName);
                wubbleObject.addUnique("dislikedBy", currentUserName);
                likeList = (ArrayList<String>) wubbleObject.get("likedBy");
                if (likeList != null && likeList.contains(currentUserName)) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    toRemove.add(currentUserName);
                    likeList.remove(currentUserName);
                    wubbleObject.removeAll("likedBy", toRemove);
                    likeCounter.setText(String.valueOf(likeList.size()));
                    likeButton.setImageResource(R.drawable.ic_action_good);
                }
                dislikeButton.setImageResource(R.drawable.ic_action_bad_2);
                wubbleObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        volleySingleton.setTransferState(true);//isTransferComplete = true;
                    }
                });
                dislikeCount = 1;
                dislikeCounter.setText(String.valueOf(dislikeCount));
            }

            if (myContext instanceof WubbleActivity) {
                ((WubbleActivity) myContext).intent.putExtra("likeList", likeList);
                ((WubbleActivity) myContext).intent.putExtra("dislikeList", dislikeList);

                ((WubbleActivity) myContext).likeList = this.likeList;
                ((WubbleActivity) myContext).dislikeList = this.dislikeList;

                ((WubbleActivity) myContext).refreshLikeList();
            }
        }

    }
}
