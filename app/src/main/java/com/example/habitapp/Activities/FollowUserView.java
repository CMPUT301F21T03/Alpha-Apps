/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: FollowingFollowers
 *
 * Description: Shows the user information about another profiles data. It shows their profile info,
 * their list of public habits, and the most recent habit event for each public habit.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Nov-23-2021   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.HabitList;
import com.example.habitapp.DataClasses.OldHabitList;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Map;

public class FollowUserView extends AppCompatActivity {

    private static final String TAG = "FollowUserViewTAG";
    private int followStatus;

    private static final int FOLLOWING = 1;
    private static final int REQUESTED = 2;
    private static final int NEITHER = 3;

    private ArrayList<Habit> habits = new ArrayList<Habit>();
    private OldHabitList habitAdapter;
    private ArrayList<Event> events;
    private String followUserName;
    private String followUserID;
    private Bitmap followUserPFP;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_user_view);

        habitAdapter = new OldHabitList(this, habits);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String intentFollowStatus = intent.getStringExtra("followStatus");

        setFollowStatusFrame(intentFollowStatus);

        if (followStatus == FOLLOWING){
            getUserData(userID);
            filterPrivateData();
            populateFrame();
        }

        TextView followStatus = findViewById(R.id.followuserview_follow_status);
        followStatus.setOnClickListener(this::followStatusClicked);
    }

    private void followStatusClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.following_status_menu, popupMenu.getMenu());

        // remove buttons based on what status this user has
        if (followStatus == FOLLOWING){
            popupMenu.getMenu().removeItem(R.id.request_follow);
        }
        if (followStatus == REQUESTED){
            popupMenu.getMenu().removeItem(R.id.request_follow);
        }
        if (followStatus == NEITHER){
            popupMenu.getMenu().removeItem(R.id.unfollow);
        }

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.request_follow) {
                    // TODO add the user in question to this user's requested list
                } else if (menuItem.getItemId() == R.id.unfollow) {
                    if (followStatus == FOLLOWING){
                        // TODO remove the user from the following list
                    }else if (followStatus == REQUESTED){
                        //TODO remove the user from the requested list
                    }
                }
                return true;
            }
        });


    }


    private void populateFrame() {
        //TODO set the habit entry

        // populate the habit list using the provided data set/adapter
        ListView allHabitsListView = (ListView) this.findViewById(R.id.allhabits_habit_list);
        allHabitsListView.setAdapter(habitAdapter);

        // populate the profile information
        TextView usernameEditText = this.findViewById(R.id.profilelistentry_username);
        TextView idView = this.findViewById(R.id.profilelistentry_id);
        ImageView profilePicView = this.findViewById(R.id.profilelistentry_photo);
        //profilePicView.setImageBitmap(followUserPFP);
        usernameEditText.setText(followUserName);
        idView.setText(followUserID);

    }

    private void setFollowStatusFrame(String type) {
        if (type == "must_be_checked"){
            //TODO check to see which category this user falls into and then set it
        }

        if (type == "following"){
            followStatus = FOLLOWING;
        }else if (type == "requested"){
            followStatus = REQUESTED;
        }else if (type == "neither"){
            followStatus = NEITHER;
        }


        if (followStatus == FOLLOWING){ // show the user data
            setFollowStatusTag("following");
            findViewById(R.id.followuserview_no_data_textview).setVisibility(View.GONE);
        }else{ // dont show the user data
            findViewById(R.id.layout).setVisibility(View.GONE);
            findViewById(R.id.followuserview_habit_event_list).setVisibility(View.GONE);
            findViewById(R.id.textView24).setVisibility(View.GONE);
            findViewById(R.id.textView27).setVisibility(View.GONE);
        }
        if (followStatus == REQUESTED){
            setFollowStatusTag("requested");
        }
        if (followStatus == NEITHER){
            setFollowStatusTag("nothing");
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setFollowStatusTag(String type) {
        TextView followTag = findViewById(R.id.followuserview_follow_status);
        if (type == "following"){
            followTag.setText("Following");
            followTag.setTextColor(R.color.green);
        }
        if (type == "requested"){
            followTag.setText("Requested");
            followTag.setTextColor(R.color.gray);
        }
        if (type == "nothing"){
            followTag.setText("Follow");
            followTag.setTextColor(R.color.blue);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterPrivateData() {
        for (Habit item : habits) {
            if (item.getPrivacy() == false) {
                habits.remove(item);
            }
        }
    }

    private void getUserData(String userID) {
        //TODO get this users habit events (the first one from each habit)
        getProfileData(userID);
        getHabitData(userID);

    }

    private void getHabitData(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query user = db.collection("Doers")
                .document(userID)
                .collection("habits")
                .orderBy("title");
        habitAdapter.addSnapshotQuery(user,TAG);
    }

    private void getProfileData(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("Doers").document(userID);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map userData = document.getData();

                        processUserData(userData);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void processUserData(Map userData){
        followUserName = (String) userData.get("name");
        followUserID = (String) userData.get("username");
        // TODO get the profile picture and set it
        //followUserPFP = (Bitmap) processPic(userData.get("profilePicture"));
    }


    public static String getTAG(){
        return TAG;
    }

}
