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
 *   1.3       Arthur    Nov-29-2021   Added requested functionality
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.HabitList;
import com.example.habitapp.DataClasses.OldHabitList;
import com.example.habitapp.DataClasses.User;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class FollowUserView extends AppCompatActivity implements EventList.OnEventListener{

    private static final String TAG = "FollowUserViewTAG";
    private int followStatus;

    private static final int FOLLOWING = 1;
    private static final int REQUESTED = 2;
    private static final int NEITHER = 3;

    private ArrayList<Habit> habits = new ArrayList<Habit>();
    private OldHabitList habitAdapter;
    private ArrayList<Event> events = new ArrayList<Event>();
    private EventList eventsAdapter;
    private String followUserName;
    private String followUserID;

    private String followUserPFP;
    private Map userData;
    private String userID;
    private String thisUserID;

    private int i;
    private int j;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_user_view);

        Intent intent = getIntent();

        // user ID of user being viewed
        userID = intent.getStringExtra("userID");
        System.out.print(userID);
        // user ID of user currently logged in
        thisUserID = intent.getStringExtra("thisUserID");
        if (userID.equals(thisUserID)) {
            // disable following if user views own profile
            TextView followTag = this.findViewById(R.id.followuserview_follow_status);
            followTag.setEnabled(false);
            followTag.setVisibility(View.GONE);
        }

        RecyclerView userRecyclerView = findViewById(R.id.followeruserview_habit_event_recycler);
        eventsAdapter = new EventList(events, this, R.layout.events_listview_content);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        userRecyclerView.setAdapter(eventsAdapter);
        getUserData(userID);

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
        try {
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
        } catch (Exception e) {
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
        if (followStatus == FOLLOWING) {
            popupMenu.getMenu().removeItem(R.id.request_follow);
        }
        if (followStatus == REQUESTED) {
            popupMenu.getMenu().removeItem(R.id.request_follow);
        }
        if (followStatus == NEITHER) {
            popupMenu.getMenu().removeItem(R.id.unfollow);
        }

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.request_follow) {
                    // Add the user in question to this user's requested list, and change status to Requested
                    setRequested();
                    setIncomingRequests();
                    popupMenu.getMenuInflater().inflate(R.menu.following_status_menu, popupMenu.getMenu());
                    popupMenu.getMenu().removeItem(R.id.request_follow);


                } else if (menuItem.getItemId() == R.id.unfollow) {
                    if (followStatus == FOLLOWING) {
                        // Remove the user from the following list, remove from user's own following list accordingly, and change status to neither
                        user.update("followers", FieldValue.arrayRemove(thisUserID))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
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
                    } else if (followStatus == REQUESTED) {
                        //Remove the user from the requested list, and change status to neither
                        user.update("requested", FieldValue.arrayRemove(thisUserID))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
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
                .whereEqualTo("privacy", false);
        habitAdapter.addSnapshotQuery(userHabits, TAG);

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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
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
        if (userFollowers != null) {
            if (userFollowers.contains(thisUserID)) {
                type = "following";
            } else if (userRequested != null) {
                if (userRequested.contains(thisUserID)) {
                    type = "requested";
                }
            } else {
                type = "neither";
            }
        } else {
            type = "neither";
        }
        if (userID.equals(thisUserID)) {
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

        if (followStatus == FOLLOWING) { // show the user data
            setFollowStatusTag("following");

            this.findViewById(R.id.followuserview_no_data_textview).setVisibility(View.GONE);
        } else { // dont show the user data
            this.findViewById(R.id.layout).setVisibility(View.GONE);
            this.findViewById(R.id.followuserview_habit_event_list).setVisibility(View.GONE);
            this.findViewById(R.id.textView24).setVisibility(View.GONE);
            this.findViewById(R.id.textView27).setVisibility(View.GONE);
        }
        if (followStatus == REQUESTED) {
            setFollowStatusTag("requested");
        }
        if (followStatus == NEITHER) {
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
        if (type.equals("requested")) {
            followTag.setText("Requested");
            followTag.setTextColor(R.color.gray);
        }
        if (type.equals("nothing")) {

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
        //getHabitData(userID);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        // first, grab the current user's username
        final DocumentReference following = db.collection("Doers")
                .document(userID);

        // ADD NULL CHECKS!!!
        following.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // for the followed user, grab all of their habits
                final DocumentReference currentDoc = db.collection("Doers")
                        .document(userID);

                final Query individual_habits_2 = currentDoc
                        .collection("habits")
                        .whereEqualTo("privacy", false);

                individual_habits_2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> temp_habits = new ArrayList<String>();
                        ArrayList<String> temp_habits_names = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            temp_habits.add((String) doc.getId());
                            temp_habits_names.add((String) doc.get("title"));

                        }

                        // and then, for each user's habits, grab all their habit events
                        for (j = 0; j < temp_habits.size(); j++) {
                            final Query individualEvents = currentDoc
                                    .collection("habits")
                                    .document(temp_habits.get(j))
                                    .collection("events")
                                    .orderBy("dateCompleted")
                                    .limit(10);
                            String habit_name = temp_habits_names.get(j);
                            System.out.println(habit_name);
                            individualEvents.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ArrayList<String> temp_events = new ArrayList<String>();
                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                        // and add each habit to the RecyclerView
                                        if (doc.get("name") != null) {
                                            Map getDate = (Map) doc.get("dateCompleted");
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
                                            String newDateStr = getDate.get("year").toString() + "-" +
                                                    getDate.get("monthValue").toString() + "-" +
                                                    getDate.get("dayOfMonth").toString() + " 00:00:00";
                                            ;
                                            LocalDateTime newDate = LocalDateTime.parse(newDateStr, formatter);
                                            String comment = doc.getString("comment");
                                            String username = doc.getString("username");
                                            String photograph = doc.getString("photograph");
                                            Double latitude = doc.getDouble("latitude");
                                            Double longitude = doc.getDouble("longitude");
                                            String locationName = doc.getString("locationName");
                                            // TODO store location and photograph after halfway
                                            Event eventToAdd = new Event(habit_name, newDate, comment, photograph, username, latitude, longitude, locationName);

                                            eventToAdd.setFirestoreId(doc.getId());
                                            if (!events.contains(eventToAdd)) {
                                                eventsAdapter.addEvent(eventToAdd, true);
                                            }

                                            //eventsAdapter.sortEvents(true);

                                        }
                                    }
                                }
                            });
                        }
                    }
                });


            }
        });

    }

    private void getHabitData(String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query user = db.collection("Doers")
                .document(userID)
                .collection("habits")
                .orderBy("title");
        habitAdapter.addSnapshotQuery(user, TAG);
    }


    private void processUserData() {
        followUserName = (String) userData.get("name");
        followUserID = (String) userData.get("username");
        followUserPFP = (String) userData.get("profilePic");
    }


    public static String getTAG() {
        return TAG;
    }
    public void setRequested() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        ArrayList<String> requested = (ArrayList<String>) userData.get("requested");
        requested.add(userID);
        userData.put("requested", requested);
        final DocumentReference findUserRef = db.collection("Doers").document(thisUserID);
        findUserRef.update("requested", requested);
        setFollowStatusTag("requested");


    }
    public void setIncomingRequests() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final DocumentReference findUserRef1 = db.collection("Doers").document(userID);
        findUserRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map userData = document.getData();
                        ArrayList<String> incomingRequests = (ArrayList<String>) userData.get("incomingrequest");
                        incomingRequests.add(thisUserID);
                        findUserRef1.update("incomingrequest", incomingRequests);
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
    @Override
    public void onEventClick(int position) {
        // do nothing
    }
}


