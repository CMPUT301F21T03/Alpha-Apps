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
 *   1.9       Jesse     Nov-27-2021   Implemented image onclick listener
 *   1.10      Jesse     Nov-28-2021   Added confirmation dialog when delete button is clicked
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HabitEventDetails extends AppCompatActivity {
    private String TAG = "habitEventDetailsTAG";
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
        if (comment.trim().isEmpty()) {
            comment = "No comment added";
        }
        //date = event.getDateCompleted().format(formatter);

        EditText nameText = findViewById(R.id.habiteventdetails_title);
        TextView commentText = findViewById(R.id.habiteventdetails_comment);
        ImageView photographView = findViewById(R.id.habiteventdetails_camera_image);
        //TextView locationText = findViewById(R.id.habiteventdetails_location);

        Button locationButton = findViewById(R.id.habiteventdetails_location);


        nameText.setText(habitName);
        nameText.setEnabled(false);
        commentText.setText(comment);

        // run a thread to set the photograph
        if(event.getPhotograph() != null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(event.getPhotograph());
                        Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                photographView.setImageBitmap(imageBitmap);
                            }
                        });
                        Log.d(TAG, "Successfully set image");
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });

            thread.start();
        }

        if (event.getLocationName() != null && event.getLatitude() != null && event.getLongitude() != null) {
            locationButton.setText(event.getLocationName());
        }

        if (TextUtils.isEmpty(event.getLocationName())) {
            locationButton.setText("No Location Specified");
            locationButton.setEnabled(false);
        }

        // set a listener for the edit button
        Button editButton = findViewById(R.id.habiteventdetails_edit);
        editButton.setOnClickListener(this::habitEventDetailsEditButtonPressed);

        //ImageView deleteButton = findViewById(R.id.habiteventdetails_delete);
        Button deleteButton = findViewById(R.id.habiteventdetails_delete);
        deleteButton.setOnClickListener(this::habitEventDetailsDeleteButtonPressed);

        photographView.setOnClickListener(this::editHabitEventCameraImagePressed);
    }

    private void editHabitEventCameraImagePressed(View view) {
        Intent intent = new Intent(HabitEventDetails.this, ImageDialog.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void habitEventDetailsEditButtonPressed(View view) {
        // navigate to the edit an event activity
        setResult(RESULT_OK);
        finish();
        Intent intent = new Intent(this, EditHabitEvent.class);
        intent.putExtra("event", event);
        intent.putExtra("firestoreId",event.getFirestoreId());
        intent.putExtra("habit", habit);
        intent.putExtra("selectedLatitude", event.getLatitude());
        intent.putExtra("selectedLongitude", event.getLongitude());
        intent.putExtra("userData", (Serializable) userData);
        intent.putExtra("activity", "HabitEventDetails");
        startActivity(intent);
    }

    private void habitEventDetailsDeleteButtonPressed(View view) {

        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        event.removeEventFromFirestore(userData, habit);
                        // close this Activity
                        setResult(RESULT_OK);
                        finish();
                        // start new Activity
                        Intent intent;
                        intent = new Intent(HabitEventDetails.this, HabitDetails.class);
                        intent.putExtra("habit", habit);
                        intent.putExtra("userData", (Serializable) userData);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog alert = deleteBuilder.create();
        alert.show();

    }

    public void habitEventDetailsLocationButtonPressed(View view) {
        Intent intent = new Intent(this, MapSelector.class);
        intent.putExtra("latitude", event.getLatitude());
        intent.putExtra("longitude", event.getLongitude());
        intent.putExtra("selectActive", false);
        startActivity(intent);
    }


}