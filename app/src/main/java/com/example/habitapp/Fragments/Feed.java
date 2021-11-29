/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 * <p>
 * Class: Feed
 * <p>
 * Description: Handles the user interactions of the Feed fragment
 * <p>
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 * 1.0       Eric      Oct-21-2021   Created
 * 1.1       Moe       Nov-24-2021   Search functionality added
 * 1.2       Eric      Nov-27-2021   Social feed functionality added
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Feed extends Fragment implements EventList.OnEventListener {
    public Feed() {
        super(R.layout.feed);
    }

    private EditText searchedUserNameEdit;
    private String searchedUserName;
    private RecyclerView feedRecyclerView;
    private EventList eventsAdapter;
    public ArrayList<Event> events = new ArrayList<>();
    private Map userData;

    private ArrayList<String> following_usernames;
    private ArrayList<ArrayList<String>> following_habits;
    private ArrayList<ArrayList<String>> following_events;

    int i;
    int j;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Main activity = (Main) getActivity();
        userData = activity.getUserData();

//        searchedUserNameEdit = (EditText) view.findViewById(R.id.feed_search_field);
//        // do something
//        ImageButton searchButton = view.findViewById(R.id.feed_search_button);
//        searchButton.setOnClickListener(this::searchButtonPressed);

        feedRecyclerView = view.findViewById(R.id.feed_recycler_view);
        eventsAdapter = new EventList(events, this, R.layout.feed_events_listview_content);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        feedRecyclerView.setAdapter(eventsAdapter);
        getHabitEventList(eventsAdapter);

        following_habits = new ArrayList<ArrayList<String>>();
        following_events = new ArrayList<ArrayList<String>>();

        i = 0;
        j = 0;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getHabitEventList(EventList eventsAdapter) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        // first, grab the current user's username
        final DocumentReference following = db.collection("Doers")
                .document((String) userData.get("username"));

        // ADD NULL CHECKS!!!
        following.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                // then, grab all of the usernames of those they follow
                following_usernames = (ArrayList<String>) documentSnapshot.get("following");

                for (i = 0; i < following_usernames.size(); i++) {

                    // for each followed user, grab all of their habits
                    final DocumentReference currentDoc = db.collection("Doers")
                            .document(following_usernames.get(i));

                    final Query individual_habits_2 = currentDoc
                            .collection("habits")
                            .whereEqualTo("privacy", false);

                    individual_habits_2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<String> temp_habits = new ArrayList<String>();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                temp_habits.add((String) doc.getId());

                            }
                            following_habits.add(temp_habits);


                            // and then, for each user's habits, grab all their habit events
                            for (j = 0; j < temp_habits.size(); j++) {
                                final Query individualEvents = currentDoc
                                        .collection("habits")
                                        .document(temp_habits.get(j))
                                        .collection("events")
                                        .orderBy("dateCompleted")
                                        .limit(10);

                                individualEvents.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                                Event eventToAdd = new Event(doc.getString("name"), newDate, comment, photograph,  username, latitude, longitude, locationName);

                                                eventToAdd.setFirestoreId(doc.getId());
                                                if (!events.contains(eventToAdd)) {
                                                    eventsAdapter.addEvent(eventToAdd);
                                                }

                                                eventsAdapter.sortEvents();

                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void searchButtonPressed(View view) {
//
//        FirebaseFirestore db;
//        db = FirebaseFirestore.getInstance();
//        searchedUserName = searchedUserNameEdit.getText().toString();
//
//        AlertDialog.Builder searchAlert = new AlertDialog.Builder(getActivity())
//                .setNegativeButton("OK", null);
//
//        final DocumentReference findUserRef = db.collection("Doers").document(searchedUserName);
//        findUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    // checks if the username searched exists in database
//                    if (document.exists()) {
//                        Map UserData = document.getData();
//                        // TODO move to user's page
//                        searchAlert.setMessage("Username: \"" + searchedUserName + "\"" + " exists!");
//                        searchAlert.show();
//                    } else {
//                        searchAlert.setMessage("Username: \"" + searchedUserName + "\"" + " doesn't exist");
//                        searchAlert.show();
//                    }
//                }
//            }
//        });
//    }

    @Override
    public void onEventClick(int position) {
        // do somn
    }


}
