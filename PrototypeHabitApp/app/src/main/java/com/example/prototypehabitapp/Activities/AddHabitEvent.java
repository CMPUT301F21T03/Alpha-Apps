/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: AddHabitEvent
 *
 * Description: Handles the user interactions of the event add page
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Moe       Oct-29-2021   Created
 *   1.1       Moe       Nov-01-2021   Added addHabitEventCompleteButtonPressed()
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.DataClasses.Event;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddHabitEvent extends AppCompatActivity {

    private Habit habit;
    private String habitName;
    private String comment;
    private LocalDateTime dateCompleted;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_habit_event);

        //get details from bundle
        Intent sentIntent = getIntent();
        habit = (Habit) sentIntent.getSerializableExtra("HABIT");

        habitName = habit.getTitle();

        Button locationButton = findViewById(R.id.edithabitevent_location);
        locationButton.setOnClickListener(this::addHabitEventLocationButtonPressed);

        Button photographButton = findViewById(R.id.edithabitevent_photograph);
        photographButton.setOnClickListener(this::addHabitEventPhotographButtonPressed);

        Button completeButton = findViewById(R.id.edithabitevent_complete);
        completeButton.setOnClickListener(this::addHabitEventCompleteButtonPressed);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addHabitEventCompleteButtonPressed(View view) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        comment = findViewById(R.id.edithabitevent_comment).toString();
        dateCompleted = LocalDateTime.now();

        event = new Event(habitName, dateCompleted, comment, false, false);
        ArrayList<Event> events = habit.getEventList();
        events.add(event);
        habit.setEventList(events);

        finish();
    }

    public void addHabitEventLocationButtonPressed(View view) {}

    public void addHabitEventPhotographButtonPressed(View view) {}
}
