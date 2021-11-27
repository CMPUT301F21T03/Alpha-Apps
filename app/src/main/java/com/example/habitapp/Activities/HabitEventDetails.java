/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: HabitEventDetails
 *
 * Description: Handles the user interactions of the event details page
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Jesse     Oct-31-2021   Implemented activity displaying event details
 *   1.2       Jesse     Oct-31-2021   Added getting the event from intent and sending when edit
 *                                       button is clicked
 *   1.3       Mathew    Oct-31-2021   Fix imports
 *   1.4       Moe       Nov-01-2021   Added delete button
 *   1.5       Jesse     Nov-02-2021   Implemented delete event
 *   1.6       Moe       Nov-04-2021   Added extra value for intent
 *   1.7       Moe       Nov-04-2021   Firestore delete for HabitEvent
 *   1.8       Leah      Nov-27-2021   Fixed bugs for Habit Event creation/deletion/edits
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HabitEventDetails extends AppCompatActivity {

    private Habit habit;
    private Event event;
    private String habitName;
    private String comment;
    //private String date;
    private Bitmap photograph;
    private Boolean hasLocation;
    //private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Map userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_event_details);

        //get details from bundle
        Intent sentIntent = getIntent();
        event = (Event) sentIntent.getParcelableExtra("event");
        event.setFirestoreId(sentIntent.getStringExtra("firestoreId"));
        habit = (Habit) sentIntent.getSerializableExtra("habit");
        userData = (Map) sentIntent.getSerializableExtra("userData");

        //update fields with Event info
        habitName = event.getName();
        comment = event.getComment();
        //date = event.getDateCompleted().format(formatter);

        TextView nameText = findViewById(R.id.habiteventdetails_title);
        TextView commentText = findViewById(R.id.habiteventdetails_comment);
        //TextView locationText = findViewById(R.id.habiteventdetails_location);

        nameText.setText("Habit Event: " + habitName);
        commentText.setText(comment);


        // set a listener for the edit button
        Button editButton = findViewById(R.id.habiteventdetails_edit);
        editButton.setOnClickListener(this::habitEventDetailsEditButtonPressed);

        //ImageView deleteButton = findViewById(R.id.habiteventdetails_delete);
        Button deleteButton = findViewById(R.id.habiteventdetails_delete);
        deleteButton.setOnClickListener(this::habitEventDetailsDeleteButtonPressed);
    }

    private void habitEventDetailsEditButtonPressed(View view) {
        // navigate to the edit an event activity
        Intent intent = new Intent(this, EditHabitEvent.class);
        intent.putExtra("event", event);
        intent.putExtra("firestoreId",event.getFirestoreId());
        intent.putExtra("habit", habit);
        intent.putExtra("userData", (Serializable) userData);
        intent.putExtra("activity", "HabitEventDetails");
        startActivity(intent);
    }

    private void habitEventDetailsDeleteButtonPressed(View view) {

        event.removeEventFromFirestore(userData, habit);
        Intent intent;
        intent = new Intent(this, HabitDetails.class);
        intent.putExtra("habit", habit);
        intent.putExtra("userData", (Serializable) userData);
        startActivity(intent);
    }


}
