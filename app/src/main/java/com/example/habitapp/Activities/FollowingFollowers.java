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
 *   1.2       Mathew    Nov-23-2021   Set the logic for clicking a user's profile, also added a search function
 *   1.3       Leah      Nov-24-2021   Added basic display functionality
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.User;
import com.example.habitapp.DataClasses.UserList;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class FollowingFollowers extends AppCompatActivity {

    private static final String TAG = "followingfollowersTAG";

    // prep the following/followers screen related objects
    private ListView followListView;
    private ArrayAdapter<User> followAdapter;
    private ArrayList<User> followDataList = new ArrayList();
    private ArrayList<String> followUsernameList;
    private boolean following;
    private int followType;
    private static final int FOLLOWING = 1;
    private static final int FOLLOWERS = 2;
    private static final int REQUESTED = 3;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_followers);

        // set if the page should show the following users or follower users
        setFrameType();
        setUserListAdapter();
        getUserDataList();

        // set an on click listener for if a habit is pressed

        // set an on click listener for if a follower is pressed

        followListView.setOnItemClickListener(this::followItemClicked);

        // set a listener for if the search button is pressed
        ImageButton searchButton = findViewById(R.id.followingfollowers_search);
        searchButton.setOnClickListener(this::searchButtonClicked);

    }

    private void searchButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter another user's ID");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openUserFrame(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openUserFrame(String userID) {
        Intent intent = new Intent(this, FollowUserView.class);
        intent.putExtra(userID, "userID");
        intent.putExtra("must_be_checked", "followStatus");
        startActivity(intent);
        System.out.println("here");
    }

    private void setFrameType() {
        // TODO: rename to something more appropriate, used to set all related info to following/followers
        TextView titleText = findViewById(R.id.followingfollowers_title_type);

        Intent sentIntent = getIntent();
        String sentFollowString = (String) sentIntent.getStringExtra("FOLLOWING?");
        System.out.println(sentFollowString);
        Map user = (Map) sentIntent.getSerializableExtra("userProfile");
        if (sentFollowString.equalsIgnoreCase("following")){
            followType = FOLLOWING;
            // set the title of the frame to be 'following'
            titleText.setText("Following");

            followUsernameList = (ArrayList<String>) user.get("following");


        }else if (sentFollowString.equalsIgnoreCase("follower")){
            // set the title of the frame to be 'followers'
            followType = FOLLOWERS;
            titleText.setText("Followers");
            followUsernameList = (ArrayList<String>) user.get("followers");
        }else if (sentFollowString.equalsIgnoreCase("requested")){
            // set the title of the frame to be 'pending'
            followType = REQUESTED;
            titleText.setText("Pending Requests");
        }
    }

    private void followItemClicked(AdapterView<?> adapterView, View view, int pos, long l) {
        Intent intent = new Intent(this, FollowUserView.class);
        intent.putExtra(followDataList.get(pos).getUniqueID(), "userID");
        if (followType == FOLLOWING){
            intent.putExtra("following", "followStatus");
        }
        if (followType == FOLLOWERS){
            intent.putExtra("must_be_checked", "followStatus");
        }
        if (followType == REQUESTED){
            intent.putExtra("requested", "followStatus");
        }
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getUserDataList(){

        // followDataList = new ArrayList<>();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        for(String e : followUsernameList){
            DocumentReference user = db.collection("Doers").document(e);
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map userData = document.getData();
                            followDataList.add(new User(userData.get("username").toString(),
                                                        userData.get("name").toString(),
                                                        userData.get("username").toString(),
                                                        userData.get("password").toString(),
                                                        userData.get("profilePic").toString()));
                            followAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "User does not exist");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

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
