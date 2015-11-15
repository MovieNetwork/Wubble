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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.proxima.Wubble.R;
import com.proxima.Wubble.adapters.TopCommentsAdapter;
import com.proxima.Wubble.network.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopCommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopCommentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters


    private int movieIdParam;


    private ParseUser currentUser;
    private String currentUsername;
    private int movieId;
    private RecyclerView topCommentsRecyclerView;
    private TopCommentsAdapter topCommentsAdapter;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    private ArrayList<String> mFollowedUsers = new ArrayList<>();
    protected static List<ParseObject> myWubbles;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TopCommentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopCommentsFragment newInstance(int param1) {
        TopCommentsFragment fragment = new TopCommentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    public TopCommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_comments, container, false);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        currentUser = ParseUser.getCurrentUser();
        currentUsername = currentUser.getUsername();


        topCommentsRecyclerView = (RecyclerView) view.findViewById(R.id.topCommentsRecyclerView);
        topCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TopCommentsAdapter emptyAdapter = new TopCommentsAdapter(getActivity());
        topCommentsRecyclerView.setAdapter(emptyAdapter);


        if (currentUser != null) {
            ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("FollowRelations");
            userQuery.whereEqualTo("Username", currentUser.getUsername());


            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Wubbles");

            query.whereEqualTo("movieId", movieId);
            query.orderByDescending("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> wubbles, ParseException e) {
                    if (e == null) {

                        //mSpinner.setVisibility(View.GONE);

                        Comparator<ParseObject> comparator = new Comparator<ParseObject>() {
                            @Override
                            public int compare(ParseObject lhs, ParseObject rhs) {
                                ArrayList<String> lhLikeArray, lhDislikeArray, rhLikeArray, rhDislikeArray;
                                lhLikeArray = ((ArrayList<String>) lhs.get("likedBy") == null) ? (new ArrayList<String>()) : (ArrayList<String>) lhs.get("likedBy");
                                lhDislikeArray = ((ArrayList<String>) lhs.get("dislikedBy") == null) ? (new ArrayList<String>()) : (ArrayList<String>) lhs.get("dislikedBy");
                                rhLikeArray = ((ArrayList<String>) rhs.get("likedBy") == null) ? (new ArrayList<String>()) : (ArrayList<String>) rhs.get("likedBy");
                                rhDislikeArray = ((ArrayList<String>) rhs.get("dislikedBy") == null) ? (new ArrayList<String>()) : (ArrayList<String>) rhs.get("dislikedBy");

                                return (lhLikeArray.size() - lhDislikeArray.size()) - (rhLikeArray.size() - rhDislikeArray.size());
                            }
                        };

                        Comparator<ParseObject> reverseComparator = Collections.reverseOrder(comparator);

                        Collections.sort(wubbles, reverseComparator);


                        myWubbles = wubbles;
                        if (myWubbles != null && myWubbles.size() > 0) {


                            topCommentsAdapter = new TopCommentsAdapter(getActivity(), myWubbles, mFollowedUsers, movieId);
                            topCommentsRecyclerView.setAdapter(topCommentsAdapter);

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


        return view;
    }


}
