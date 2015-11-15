package com.proxima.Wubble.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.FollowingCommentsAdapter;
import com.proxima.Wubble.adapters.TopCommentsAdapter;
import com.proxima.Wubble.network.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Use the {@link FollowingCommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingCommentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters


    private ParseUser currentUser;
    private String currentUsername;
    private int movieId;
    private RecyclerView followingCommentsRecyclerView;
    private FollowingCommentsAdapter followingCommentsAdapter;
    private TopCommentsAdapter topCommentsAdapter;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    private ArrayList<String> mFollowedUsers = new ArrayList<>();
    protected static List<ParseObject> myWubbles;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingCommentsFragment newInstance(int param2) {
        FollowingCommentsFragment fragment = new FollowingCommentsFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FollowingCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following_comments, container, false);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        currentUser = ParseUser.getCurrentUser();
        currentUsername = currentUser.getUsername();


        followingCommentsRecyclerView = (RecyclerView) view.findViewById(R.id.followingCommentsRecyclerView);
        followingCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FollowingCommentsAdapter emptyAdapter = new FollowingCommentsAdapter(getActivity());
        followingCommentsRecyclerView.setAdapter(emptyAdapter);


        if (currentUser != null) {
            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("FollowRelations");
            userQuery.whereEqualTo("Username", currentUser.getUsername());


            userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {

                        mFollowedUsers = (ArrayList<String>) parseObject.get("Following");
                        if (mFollowedUsers != null && mFollowedUsers.size() > 0) {

                            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");
                            query.whereContainedIn("User", mFollowedUsers);
                            query.whereEqualTo("movieId", movieId);
                            query.orderByDescending("createdAt");

                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> wubbles, ParseException e) {
                                    if (e == null) {

                                        //mSpinner.setVisibility(View.GONE);
                                        myWubbles = wubbles;
                                        if (myWubbles != null && myWubbles.size() > 0) {


                                            followingCommentsAdapter = new FollowingCommentsAdapter(getActivity(), myWubbles, mFollowedUsers, movieId);
                                            followingCommentsRecyclerView.setAdapter(followingCommentsAdapter);

                                            //mTextView.setVisibility(View.GONE);
                                            //mSpinner.setVisibility(View.GONE);
                                            //feedRecyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            //mTextView.setVisibility(View.VISIBLE);
                                            //mSpinner.setVisibility(View.GONE);
                                        }


                                    } else {
                                        // handle problems here
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

        return view;
    }


}
