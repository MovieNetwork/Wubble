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
public class LikeClickHandler implements View.OnClickListener {

    private Context myContext;

    private VolleySingleton volleySingleton;

    private ImageView dislikeButton;
    private ImageView likeButton;

    private TextView likeCounter;
    private TextView dislikeCounter;

    private ParseObject wubbleObject;


    private ArrayList<String> likeList;
    private ArrayList<String> dislikeList;

    private String currentUserName;

    public LikeClickHandler(Context myContext,
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
            likeList = null;
            likeList = (ArrayList<String>) wubbleObject.get("likedBy");
            int likeCount = 0;
            if (likeList != null && likeList.size() > 0) {
                if (likeList.contains(currentUserName)) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    toRemove.add(currentUserName);
                    likeList.remove(currentUserName);
                    wubbleObject.removeAll("likedBy", toRemove);
                    likeButton.setImageResource(R.drawable.ic_action_good);
                    wubbleObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            volleySingleton.setTransferState(true);//isTransferComplete = true;
                        }
                    });
                    likeCount = likeList.size();
                    likeCounter.setText(String.valueOf(likeCount));
                } else {
                    likeList.add(currentUserName);
                    wubbleObject.addUnique("likedBy", currentUserName);
                    dislikeList = (ArrayList<String>) wubbleObject.get("dislikedBy");
                    if (dislikeList != null && dislikeList.contains(currentUserName)) {
                        ArrayList<String> toRemove = new ArrayList<>();
                        toRemove.add(currentUserName);
                        dislikeList.remove(currentUserName);
                        wubbleObject.removeAll("dislikedBy", toRemove);
                        dislikeCounter.setText(String.valueOf(dislikeList.size()));
                        dislikeButton.setImageResource(R.drawable.ic_action_bad);

                    }

                    likeButton.setImageResource(R.drawable.ic_action_good_2);
                    wubbleObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            volleySingleton.setTransferState(true);//isTransferComplete = true;
                        }
                    });

                    likeCount = likeList.size();
                    likeCounter.setText(String.valueOf(likeCount));
                }


            } else {

                likeList = new ArrayList<>();
                likeList.add(currentUserName);
                wubbleObject.addUnique("likedBy", currentUserName);
                dislikeList = (ArrayList<String>) wubbleObject.get("dislikedBy");
                if (dislikeList != null && dislikeList.contains(currentUserName)) {
                    ArrayList<String> toRemove = new ArrayList<>();
                    dislikeList.remove(currentUserName);
                    toRemove.add(currentUserName);
                    wubbleObject.removeAll("dislikedBy", toRemove);
                    dislikeCounter.setText(String.valueOf(dislikeList.size()));
                    dislikeButton.setImageResource(R.drawable.ic_action_bad);
                }
                likeButton.setImageResource(R.drawable.ic_action_good_2);
                wubbleObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        volleySingleton.setTransferState(true);//isTransferComplete = true;
                    }
                });

                likeCount = 1;
                likeCounter.setText(String.valueOf(likeCount));
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
