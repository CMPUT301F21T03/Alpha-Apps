/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: FollowingFollowers
 *
 * Description: Deals with the user interaction between the following and follower frames. Both are
 * identical apart from the data they are to display so they use the same class in order to function
 * in the exact same ways. The data they show is based off the information passed to the frame on
 * startup (whether it shows the "following" page or the "followers" page)
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Nov-01-2021   Created
 *   1.1       Mathew    Nov-03-2021   Forgot to add dynamic title last time (now added)
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.DataClasses.Event;
import com.example.prototypehabitapp.DataClasses.User;
import com.example.prototypehabitapp.DataClasses.UserList;
import com.example.prototypehabitapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FollowingFollowers extends AppCompatActivity {

    private static final String TAG = "followingfollowersTAG";

    // prep the following/followers screen related objects
    private ListView followListView;
    private ArrayAdapter<User> followAdapter;
    private ArrayList<User> followDataList;
    private boolean following;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_followers);

        // set if the page should show the following users or follower users
        setFrameType();

        getUserDataList();
        setUserListAdapter();
        // set an on click listener for if a habit is pressed
        followListView.setOnItemClickListener(this::followItemClicked);

    }

    private void setFrameType() {
        TextView titleText = findViewById(R.id.followingfollowers_title_type);

        Intent sentIntent = getIntent();
        String sentFollowString = (String) sentIntent.getStringExtra("FOLLOWING?");
        System.out.println(sentFollowString);
        if (sentFollowString.equalsIgnoreCase("following")){
            following = true;
            // set the title of the frame to be 'following'
            titleText.setText("Following");
        }else{
            // set the title of the frame to be 'followers'
            following = false;
            titleText.setText("Followers");
            System.out.println("here");
        }
    }

    private void followItemClicked(AdapterView<?> adapterView, View view, int pos, long l) {
        //TODO navigate to this user's page and show all of their public habits
        System.out.println("follower @ pos " + pos + " clicked");
    }

    public void getUserDataList(){
        followDataList = new ArrayList<>();
        //TODO get different data based on the 'following' attribute

//        // show your page from Firestore
//        FirebaseFirestore db;
//        db = FirebaseFirestore.getInstance();
//        final CollectionReference user = db.collection("Doers").document((String) userData.get("username")).collection("habits");
//        user.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot querySnapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    // if error occurs
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//                followDataList.clear();
//                for(QueryDocumentSnapshot doc : querySnapshot){
//                    //TODO get the data from firestore
//
//                    // then insert that data into a new user object which gets added to the list
//                    followDataList.add(new User();
//                }
//                followAdapter.notifyDataSetChanged();
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUserListAdapter() {
        followListView = (ListView) findViewById(R.id.followingfollowers_user_list);
        followAdapter = new UserList(this, followDataList);
        followListView.setAdapter(followAdapter);
    }


    public static String getTAG(){
        return TAG;
    }

}
