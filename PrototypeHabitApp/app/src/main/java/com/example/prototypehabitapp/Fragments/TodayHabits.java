/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: TodayHabits
 *
 * Description: Handles the user interactions of the today habits fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Mathew    Oct-31-2021   Added logic to only show today's habits
 *   1.2       Leah      Nov-02-2021   Fixed crashing when opening this Fragment
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Fragments;

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

import com.example.prototypehabitapp.Activities.HabitDetails;
import com.example.prototypehabitapp.Activities.Main;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.DataClasses.HabitList;
import com.example.prototypehabitapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TodayHabits extends Fragment {
    public TodayHabits() {
        super(R.layout.today_habits);
    }

    private static final String TAG = "todayhabitsTAG";

    // prep the today_habits screen related objects
    private ListView todaysHabitsListView;
    private ArrayAdapter<Habit> habitAdapter;
    private ArrayList<Habit> habitDataList;
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
        todaysHabitsListView.setOnItemClickListener(this::habitItemClicked);

        mView = getView();
    }

    private void habitItemClicked(AdapterView<?> adapterView, View view, int pos, long l) {
        // get the item that the user selected
        Habit itemToSend = (Habit) todaysHabitsListView.getItemAtPosition(pos);
        Intent intent = new Intent(getContext(), HabitDetails.class);

        // go to the habit details class with that item
        intent.putExtra("habit",itemToSend);
        startActivity(intent);
    }


    private void setHabitListAdapter(View view) {
        todaysHabitsListView = (ListView) view.findViewById(R.id.todayhabits_habit_list);
        habitAdapter = new HabitList(view.getContext(), habitDataList);
        todaysHabitsListView.setAdapter(habitAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitDataList() {
        // Gets list of habits for today
        habitDataList = new ArrayList<>();

        // get the day of the week
        String dayWeek = LocalDate.now().getDayOfWeek().name().toLowerCase(Locale.ROOT);

        // initialize firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        // only get the ones for today
        final Query user = db.collection("Doers")
                                           .document((String) userData.get("username"))
                                           .collection("habits")
                                           .whereEqualTo("weekOccurence."+dayWeek,true);
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
                            // Set the document ID in case it needs to be fetched for delete/edits
                            addHabit.setFirestoreId(doc.getId());
                            // Add to the ListArray
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
            mView.findViewById(R.id.today_habits_hidden_textview).setVisibility(View.VISIBLE);
        }else{
            mView.findViewById(R.id.today_habits_hidden_textview).setVisibility(View.INVISIBLE);
        }
    }

    public void setUserData(Map userData) {
        this.userData = userData;
    }

    public static String getTAG(){
        return TAG;
    }

}
