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
 *   1.1       Leah      Nov-28-2021   Finished following/follower functionality
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.net.URL;
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
    private String followUserPFP;
    private Map userData;
    private String userID;
    private String thisUserID;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_user_view);

        Intent intent = getIntent();
        // user ID of user being viewed
        userID = intent.getStringExtra("userID");
        // user ID of user currently logged in
        thisUserID = intent.getStringExtra("thisUserID");
        if(userID.equals(thisUserID)){
            // disable following if user views own profile
            TextView followTag = this.findViewById(R.id.followuserview_follow_status);
            followTag.setEnabled(false);
            followTag.setVisibility(View.GONE);
        }

        Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No such user exists.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        // check if user exists, if not, direct user back
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try{
            DocumentReference user = db.collection("Doers").document(userID);
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userData = document.getData();

                            processUserData();
                            populateFrame();
                            setFollowStatusFrame();

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            hidePage();
                            builder.show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        hidePage();
                        builder.show();
                    }
                }
            });
        }
        catch(Exception e){
            hidePage();
            builder.show();
        }

        TextView followStatus = findViewById(R.id.followuserview_follow_status);
        followStatus.setOnClickListener(this::followStatusClicked);
    }

    private void followStatusClicked(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("Doers").document(userID);
        DocumentReference thisUser = db.collection("Doers").document(thisUserID);
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
                    // Add the user in question to this user's requested list, and change status to Requested
                    user.update("requested", FieldValue.arrayUnion(thisUserID))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    // Change status to requested
                                    setFollowStatusTag("requested");
                                    popupMenu.getMenuInflater().inflate(R.menu.following_status_menu, popupMenu.getMenu());
                                    popupMenu.getMenu().removeItem(R.id.request_follow);
                                }
                            }
                        });

                } else if (menuItem.getItemId() == R.id.unfollow) {
                    if (followStatus == FOLLOWING){
                        // Remove the user from the following list, remove from user's own following list accordingly, and change status to neither
                        user.update("followers", FieldValue.arrayRemove(thisUserID))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            // update own following list
                                            thisUser.update("following", FieldValue.arrayRemove(userID))
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            // Change status to none
                                                            setFollowStatusTag("nothing");
                                                            popupMenu.getMenuInflater().inflate(R.menu.following_status_menu, popupMenu.getMenu());
                                                            popupMenu.getMenu().removeItem(R.id.unfollow);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }else if (followStatus == REQUESTED){
                        //Remove the user from the requested list, and change status to neither
                        user.update("requested", FieldValue.arrayRemove(thisUserID))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            // Change status to none
                                            setFollowStatusTag("nothing");
                                            popupMenu.getMenuInflater().inflate(R.menu.following_status_menu, popupMenu.getMenu());
                                            popupMenu.getMenu().removeItem(R.id.unfollow);
                                        }
                                    }
                                });
                    }
                }
                return true;
            }
        });


    }

    private void hidePage() {
        this.findViewById(R.id.follow_user_view_userdetail).setVisibility(View.GONE);
        this.findViewById(R.id.followuserview_no_data_textview).setVisibility(View.GONE);
    }

    private void populateFrame() {

        // populate the habit list using the provided data set/adapter
        ListView allHabitsListView = (ListView) this.findViewById(R.id.allhabits_habit_list);
        habitAdapter = new OldHabitList(this, habits);
        allHabitsListView.setAdapter(habitAdapter);

        // make a query for all the user habits that are public
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final Query userHabits = db.collection("Doers")
                .document(userID)
                .collection("habits")
                .whereEqualTo("privacy",false);
        habitAdapter.addSnapshotQuery(userHabits,TAG);

        // populate the profile information
        TextView usernameEditText = this.findViewById(R.id.profilelistentry_username);
        usernameEditText.setEnabled(false);
        TextView idView = this.findViewById(R.id.profilelistentry_id);
        ImageView profilePicView = this.findViewById(R.id.profilelistentry_photo);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(followUserPFP);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            profilePicView.setImageBitmap(imageBitmap);
                        }
                    });
                    Log.d(TAG, "Successfully set image");
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

        thread.start();
        usernameEditText.setText(followUserName);
        idView.setText("@"+followUserID);

    }

    private void setFollowStatusFrame() {
        String type = "neither";
        ArrayList<String> userFollowers = (ArrayList<String>) userData.get("followers");
        ArrayList<String> userRequested = (ArrayList<String>) userData.get("requested");
        if(userFollowers != null){
            if(userFollowers.contains(thisUserID)){
                type = "following";
            }
            else if(userRequested != null){
                if(userRequested.contains(thisUserID)){
                    type = "requested";
                }
            }
            else{
                type = "neither";
            }
        }
        else{
            type = "neither";
        }
        if(userID.equals(thisUserID)){
            type = "following";
        }

        switch (type) {
            case "following":
                Log.d(TAG, type);
                followStatus = FOLLOWING;
                break;
            case "requested":
                followStatus = REQUESTED;
                break;
            case "neither":
                followStatus = NEITHER;
                break;
        }

        if (followStatus == FOLLOWING){ // show the user data
            setFollowStatusTag("following");
            this.findViewById(R.id.followuserview_no_data_textview).setVisibility(View.GONE);
        }else{ // dont show the user data
            this.findViewById(R.id.layout).setVisibility(View.GONE);
            this.findViewById(R.id.followuserview_habit_event_list).setVisibility(View.GONE);
            this.findViewById(R.id.textView24).setVisibility(View.GONE);
            this.findViewById(R.id.textView27).setVisibility(View.GONE);
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
        TextView followTag = this.findViewById(R.id.followuserview_follow_status);
        if ("following".equals(type)) {
            followTag.setText("Following");
            followTag.setTextColor(R.color.green);
        }
        if (type.equals("requested")){
            followTag.setText("Requested");
            followTag.setTextColor(R.color.gray);
        }
        if (type.equals("nothing")){
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

    private void processUserData(){
        followUserName = (String) userData.get("name");
        followUserID = (String) userData.get("username");
        followUserPFP = (String) userData.get("profilePic") ;
    }

    public static String getTAG(){
        return TAG;
    }

}
