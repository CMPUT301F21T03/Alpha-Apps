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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.Activities.HabitDetails;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.DataClasses.HabitList;
import com.example.prototypehabitapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class TodayHabits extends Fragment {
    public TodayHabits() {
        super(R.layout.today_habits);
    }

    // prep the today_habits screen related objects
    private ListView todaysHabitsListView;
    private ArrayAdapter<Habit> habitAdapter;
    private ArrayList<Habit> habitDataList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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


//        FirebaseFirestore db;
//        db = FirebaseFirestore.getInstance();
//        final DocumentReference user = db.collection("Doers").document((String) userData.get("username"));
//        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                habitDataList.clear();
//                if (queryDocumentSnapshot.exists()){
//                    Map HabitData = queryDocumentSnapshot.getData();
//
//                    // get the day of the week
//                    Integer dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//                    dayOfWeek -= 1;
//
//
//                    for (int i = 0; i < habitData.getSize(); i++){
//                        // checks to see if the habit is set to be done today
//                        DaysOfWeek freq = habitData.get("Days_of_week");
//                        ArrayList<Boolean> boolList = freq.getAll();
//                        if (boolList.get(dayOfWeek) == true){
//                            //title = habitData.get("habits" @ pos i)
//                            // repeat for reason, days of week, progress
//                            habitDataList.add(title, reason, dateStarted, daysOfWeek,)
//                        }
//
//                    }
//                if (habitDataList.isEmpty()){
//                    showPromptText(true);
//                }else{
//                    showPromptText(false);
//                }
//                }
//                habitAdapter.notifyDataSetChanged();
//            }
//        });
    }


    private void showPromptText(Boolean show){
        if (show){
            getView().findViewById(R.id.today_habits_hidden_textview).setVisibility(View.VISIBLE);
        }else{
            getView().findViewById(R.id.today_habits_hidden_textview).setVisibility(View.INVISIBLE);
        }
    }

}
