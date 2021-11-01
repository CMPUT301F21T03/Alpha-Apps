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
 *   1.0       Eric      Oct-21-2020   Created
 *   1.1       Mathew    Oct-31-2020   Added logic to only show today's habits
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    }

    private void habitItemClicked(AdapterView<?> adapterView, View view, int pos, long l) {
        // get the item that the user selected
        Habit itemToSend = (Habit) todaysHabitsListView.getItemAtPosition(pos);

        // go to the habit details class with that item
        Intent intent = new Intent(getContext(), HabitDetails.class);
        intent.putExtra(getTag(), itemToSend);
        startActivity(intent);
    }


    private void setHabitListAdapter(View view) {
        todaysHabitsListView = (ListView) view.findViewById(R.id.todayhabits_habit_list);
        habitAdapter = new HabitList(view.getContext(), habitDataList);
        todaysHabitsListView.setAdapter(habitAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitDataList() {
        //TODO one needs to get the habit array of the user once the user is known
        // for now it is an empty list
        habitDataList = new ArrayList<>();

        // add some test data
        showPromptText(false);
        DaysOfWeek testDaysOfWeek = new DaysOfWeek();
        Habit test_habit = new Habit("title", "reason", LocalDateTime.now(), testDaysOfWeek);
        test_habit.setProgress(100.0);
        habitDataList.add(test_habit);
        habitDataList.add(test_habit);


        // REMOVE THIS IF YOU NEED TO TEST WITHOUT THE FIRESTORE
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference user = db.collection("Doers").document((String) userData.get("username")).collection("habits");
        user.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // if error occurs
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                List<String> habits = new ArrayList<>();
                habitDataList.clear();
                // get the day of the week with 0 = sunday 1 = monday... 6 = saturday;
                Integer dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;

                for(QueryDocumentSnapshot doc : querySnapshot){
                    // make sure the title exists
                    if (doc.get("title") != null) {
                        // get the days of the week and see if the habit is schedule for today
                        DaysOfWeek freq = new DaysOfWeek((Map<String, Boolean>) doc.get("weekOccurence"));
                        // if it is scheduled for today, add the data to the list
                        if(freq.getAll().get(dayWeek) == true){
                            // convert firestore timestamp to local time
                            LocalDateTime ldt = LocalDateTime.ofInstant(doc.getDate("dateStarted").toInstant(),
                                    ZoneId.systemDefault());
                            habitDataList.add(new Habit(doc.getString("title"),doc.getString("reason"),ldt, freq));
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
        // END REMOVE HERE
    }


    private void showPromptText(Boolean show){
        if (show){
            getView().findViewById(R.id.today_habits_hidden_textview).setVisibility(View.VISIBLE);
        }else{
            getView().findViewById(R.id.today_habits_hidden_textview).setVisibility(View.INVISIBLE);
        }
    }

    public void setUserData(Map userData) {
        this.userData = userData;
    }

    public static String getTAG(){
        return TAG;
    }

}
