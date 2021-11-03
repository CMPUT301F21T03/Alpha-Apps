/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: AllHabits
 *
 * Description: Handles the user interactions of the all habits fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Mathew    Oct-21-2021   Added some navigation features, added test data
 *   1.2       Leah      Oct-30-2021   Now populates from user firestore document, does not use subcollection yet
 *   1.3       Leah      Nov-02-2021   Now uses Habits subcollection. Cleaned up test code.
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.Activities.Main;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.Activities.HabitDetails;
import com.example.prototypehabitapp.DataClasses.HabitList;
import com.example.prototypehabitapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AllHabits extends Fragment {

    private static final String TAG = "allhabitsTAG";

    public AllHabits() {
        super(R.layout.all_habits);
    }

    // prep the all_habits screen related objects
    private ListView allHabitsListView;
    private ArrayAdapter<Habit> habitAdapter;
    private ArrayList<Habit> habitDataList = new ArrayList<>();;
    private Map userData;
    private View mView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // get user data
        Main activity = (Main) getActivity();
        userData = activity.getUserData();
        Log.d(TAG,"Successfully logged in: " + (String) userData.get("username"));

        getHabitDataList();
        setHabitListAdapter(view);
        // set an on click listener for if a habit is pressed
        allHabitsListView.setOnItemClickListener(this::habitItemClicked);

        mView = getView();

    }

    private void habitItemClicked(AdapterView<?> adapterView, View view, int pos, long l) {
        // get the item that the user selected
        Habit itemToSend = (Habit) allHabitsListView.getItemAtPosition(pos);
        // TODO: serialize before bundling
        Log.d(TAG,itemToSend.toString());
        Intent intent = new Intent(getContext(), HabitDetails.class);
        // TODO bundle up the item to be sent to the next frame
        intent.putExtra(getTag(),itemToSend);
        startActivity(intent);
    }


    private void setHabitListAdapter(View view) {
        allHabitsListView = (ListView) view.findViewById(R.id.allhabits_habit_list);
        habitAdapter = new HabitList(view.getContext(), habitDataList);
        allHabitsListView.setAdapter(habitAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitDataList(){
        // Gets the list of Habits from the user logged in
        habitDataList = new ArrayList<>();

        // show your page from Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference user = db.collection("Doers").document((String) userData.get("username")).collection("habits");
        user.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                // check for errors in listen
                if (e != null) {
                    // if error occurs
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // if there are Habits
                if (!querySnapshot.isEmpty()){
                    List<String> habits = new ArrayList<>();
                    habitDataList.clear();
                    for(QueryDocumentSnapshot doc : querySnapshot){
                        // make sure the title exists
                        if (doc.get("title") != null) {
                            // Convert Firestore's stored time to LocalDateTime
                            // doc.getID() // use this later so deletes/edits are possible
                            Map getDate = (Map) doc.get("dateStarted");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
                            String newDateString = getDate.get("year").toString() + "-" +
                                                   getDate.get("monthValue").toString() + "-" +
                                                   getDate.get("dayOfMonth").toString() + " 00:00:00";
                            LocalDateTime newDate = LocalDateTime.parse(newDateString, formatter);
                            LocalDateTime ldt = newDate;
                            // Convert Firestore's stored days of week to DaysOfWeek
                            Map<String, Boolean> docDaysOfWeek = (Map<String, Boolean>) doc.get("weekOccurence");
                            Habit addHabit = new Habit(doc.getString("title"),doc.getString("reason"),ldt,new DaysOfWeek(docDaysOfWeek));
                            habitDataList.add(addHabit);
                        }
                    }
                }
                if (habitDataList.isEmpty()){
                    showPromptText(true);
                }else{
                    showPromptText(false);
                }
                habitAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showPromptText(Boolean show){
        if (show){
            mView.findViewById(R.id.allhabits_hidden_textview_1).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.allhabits_hidden_textview_2).setVisibility(View.VISIBLE);

        }else {
            mView.findViewById(R.id.allhabits_hidden_textview_1).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.allhabits_hidden_textview_2).setVisibility(View.INVISIBLE);

        }
    }

    public static String getTAG(){
        return TAG;
    }

}
