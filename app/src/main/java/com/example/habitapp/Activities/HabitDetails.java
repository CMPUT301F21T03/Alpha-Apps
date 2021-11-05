/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: HabitDetails
 *
 * Description: Handles the user interactions of the habit details page
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Moe       Oct-29-2021   Added popup menu when more button is pressed
 *   1.2       Jesse     Oct-31-2021   Set up array adapter and on click listener for event list
 *   1.3       Mathew    Oct-31-2021   Fix imports, add dummy test data
 *   1.4       Eric      Oct-31-2021   Linked EditTexts with data from Habit object passed in Intent
 *   1.5       Eric      Oct-31-2021   Added edit functionality
 *   1.6       Mathew    Oct-31-2021   Added a more descriptive tag by which to get the intent info
 *   1.7       Moe       Nov-01-2021   Added passing event object when log habit is selected in the
 *                                         popup menu
 *   1.8       Moe       Nov-01-2021   Removed log habit in the popup menu
 *   1.9       Jesse/Moe     Nov-02-2021   Added intent extra to send to habit event details
 *   1.10      Eric      Nov-03-2021   Firestore add, edit, delete now part of Habit class. Changes reflected here.
 *   1.11      Moe       Nov-04-2021   Deleted scroller for displaying HabitEvents
 *   1.12      Moe       Nov-04-2021   Changed custom dialog to alertDialog for adding habit event
 *   1.13      Moe       Nov-04-2021   Updated EventAdapter to display all the HabitEvents
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.DaysOfWeek;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HabitDetails extends AppCompatActivity{

    private static final String TAG = "habitdetailsTAG";
    // attributes
    private Habit habit;
    private boolean editing;
    private Event newHabitEvent;

    private EditText title;
    private EditText reason;
    private TextView date_started;
    private TextView habit_events_title;
    private TextView done_habit;
    private Button done_editing;


    CheckBox sunday_button;
    CheckBox monday_button;
    CheckBox tuesday_button;
    CheckBox wednesday_button;
    CheckBox thursday_button;
    CheckBox friday_button;
    CheckBox saturday_button;

    public ListView eventsListview;
    private EventList eventsAdapter;
    private ArrayList<Event> events = new ArrayList<>();


    private Map userData;


    PopupMenu popupMenu;

    Intent intent;

    ArrayList<CheckBox> weekButtons;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_details);

        editing = false;

        // connect to view elements
        title = findViewById(R.id.habitdetails_title);
        reason = findViewById(R.id.habitdetails_reason_text);
        date_started = findViewById(R.id.habitdetails_date_started);
        habit_events_title = findViewById(R.id.habitdetails_habit_events_text);
        done_habit = findViewById(R.id.habitdetails_done_habit);
        done_editing = findViewById(R.id.habitdetails_button_done_editing);

        // disable title and reason editablity onCreate
        title.setEnabled(false);
        title.setTextColor(Color.BLACK);
        reason.setEnabled(false);
        reason.setTextColor(Color.BLACK);

        weekButtons = prepareDayOfWeekButtons();

        // if a selected habit was sent over in the intent
        intent = getIntent();
        /*if (intent.getSerializableExtra(AllHabits.getTAG()) != null)  {
            // then we can work with it...
            // set the data to the proper fields in the activity
            Log.d(TAG,"2");
            habit = (Habit) intent.getSerializableExtra(AllHabits.getTAG());
            Log.d(TAG,habit.getTitle());
            title.setText(habit.getTitle());
            reason.setText(habit.getReason());
            date_started.setText(habit.getDateStarted().toString());
            ArrayList<Boolean> weekOccurenceList = habit.getWeekOccurence().getAll();
            for (int i = 0; i < 7; i++) {
                setBoxesChecked(weekButtons.get(i), weekOccurenceList.get(i));
            }

        }*/
        userData = (Map) intent.getSerializableExtra("userData");

        habit = (Habit) intent.getSerializableExtra("habit");
        Log.d(TAG,habit.getTitle());
        title.setText(habit.getTitle());
        reason.setText(habit.getReason());
        date_started.setText(habit.getDateStarted().toString());
        ArrayList<Boolean> weekOccurenceList = habit.getWeekOccurence().getAll();
        for (int i = 0; i < 7; i++) {
            setBoxesChecked(weekButtons.get(i), weekOccurenceList.get(i));
        }

        for (int i = 0; i < 7; i++) {
            weekButtons.get(i).setClickable(false);
        }

        setHabitEventAdapter();

        //set a listener for if the editHabit layout is pressed by the user
        eventsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = (Event) eventsListview.getItemAtPosition(i);
                habitDetailsHabitEventLayoutPressed(event);
            }
        });

        // most likely out of date code
        // set a listener for if the more button is pressed by the user
        Button moreButton = findViewById(R.id.habitdetails_more);
        moreButton.setOnClickListener(this::habitDetailsMoreButtonPressed);
    }

    private ArrayList<CheckBox> prepareDayOfWeekButtons(){
        ArrayList<CheckBox> weekButtons = new ArrayList<>();
        sunday_button = findViewById(R.id.sunday_checkbox);
        weekButtons.add(sunday_button);
        monday_button = findViewById(R.id.monday_checkbox);
        weekButtons.add(monday_button);
        tuesday_button = findViewById(R.id.tuesday_checkbox);
        weekButtons.add(tuesday_button);
        wednesday_button = findViewById(R.id.wednesday_checkbox);
        weekButtons.add(wednesday_button);
        thursday_button = findViewById(R.id.thursday_checkbox);
        weekButtons.add(thursday_button);
        friday_button = findViewById(R.id.friday_checkbox);
        weekButtons.add(friday_button);
        saturday_button = findViewById(R.id.saturday_checkbox);
        weekButtons.add(saturday_button);
        return weekButtons;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void habitDetailsMoreButtonPressed(View view) {
        //TODO open a dialog as defined in the figma storyboard
        popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.habit_more_menu, popupMenu.getMenu());
        if (editing) {
            popupMenu.getMenu().removeItem(R.id.edit_habit);
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.mark_done) {
                    addHabitEvent();
                } else if (menuItem.getItemId() == R.id.edit_habit) {
                    prepareForEdit();

                } else if (menuItem.getItemId() == R.id.delete_habit) {
                    // TODO delete habit in Firestore here
                    Habit habit_to_delete = (Habit) intent.getSerializableExtra("habit");
                    //System.out.println("ID to delete: " + habit_to_delete.getFirestoreId());
                    habit_to_delete.removeHabitFromFirestore(userData);
                    finish();
                }
                return true;
            }
        });
    }

    private void habitDetailsHabitEventLayoutPressed(Event event){
        Intent intent = new Intent(this, HabitEventDetails.class);
        intent.putExtra("event", event);
        intent.putExtra("habit", habit);
        intent.putExtra("userData", (Serializable) userData);
        startActivity(intent);
    }

    public void habitDetailsDoneEditingPressed(View view) {
        prepareForFinishEditing();
        // TODO update information in Firestore here
        Habit habit_to_edit = (Habit) intent.getSerializableExtra("habit");
        habit_to_edit.setTitle(title.getText().toString());
        habit_to_edit.setReason(reason.getText().toString());



        boolean sundayVal = sunday_button.isChecked();
        boolean mondayVal = monday_button.isChecked();
        boolean tuesdayVal = tuesday_button.isChecked();
        boolean wednesdayVal = wednesday_button.isChecked();
        boolean thursdayVal = thursday_button.isChecked();
        boolean fridayVal = friday_button.isChecked();
        boolean saturdayVal = saturday_button.isChecked();

        DaysOfWeek frequency = new DaysOfWeek(sundayVal,mondayVal, tuesdayVal, wednesdayVal,thursdayVal, fridayVal, saturdayVal);

        habit_to_edit.setWeekOccurence(frequency);
        habit_to_edit.editHabitInFirestore(userData);
    }

    private void setBoxesChecked(CheckBox button, boolean val) {
        if (val) {
            button.setChecked(true);
        } else {
            button.setChecked(false);
        }
    }

    private void prepareForEdit() {
        editing = true; // set editing flag to true (for popup menu)
        habit_events_title.setVisibility(View.GONE); // hide Habit Events
        done_editing.setVisibility(View.VISIBLE); // show done editing button
        title.setEnabled(true); // enable title and reason EditTexts
        reason.setEnabled(true);
        for (int i = 0; i < 7; i++) { // enable frequency click boxes
            weekButtons.get(i).setClickable(true);
        }
    }

    private void prepareForFinishEditing() {
        editing = false; // set editing flag to false (for popup menu)
        habit_events_title.setVisibility(View.VISIBLE); // show Habit Events
        done_editing.setVisibility(View.GONE); // hide done editing button
        title.setEnabled(false); // disable title and reason EditTexts
        title.setTextColor(Color.BLACK);
        reason.setEnabled(false);
        reason.setTextColor(Color.BLACK);

        for (int i = 0; i < 7; i++) { // disable frequency click boxes
            weekButtons.get(i).setClickable(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addHabitEvent() {

        AlertDialog.Builder markdoneBuilder = new AlertDialog.Builder(this);
        markdoneBuilder.setMessage("Do you want to mark this habit as done?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newHabitEvent = new Event(habit.getTitle(), LocalDateTime.now(), "", false, false);

                        newHabitEvent.addEventToFirestore(userData, habit);
                        done_habit.setVisibility(View.VISIBLE);

                        AlertDialog.Builder loghabitBuilder = new AlertDialog.Builder(HabitDetails.this);
                        loghabitBuilder.setMessage("Do you want to log this habit?")
                                .setPositiveButton("Log habit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(HabitDetails.this, EditHabitEvent.class);
                                        intent.putExtra("event", newHabitEvent);
                                        intent.putExtra("habit", habit);
                                        intent.putExtra("userData", (Serializable) userData);
                                        intent.putExtra("activity", "HabitDetails");
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        AlertDialog alert2 = loghabitBuilder.create();
                        alert2.show();
                    }
                })
                .setNegativeButton("Cancel", null);
        AlertDialog alert = markdoneBuilder.create();
        alert.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHabitEventAdapter() {
        eventsListview = findViewById(R.id.habitdetails_habit_event_list);
        eventsAdapter = new EventList(this, events);
        eventsListview.setAdapter(eventsAdapter);
        getHabitEventList(eventsAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getHabitEventList(EventList eventsAdapter) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        final Query user = db.collection("Doers")
                .document((String) userData.get("username"))
                .collection("habits")
                .document(habit.getFirestoreId())
                .collection("events")
                .orderBy("dateCompleted");

        eventsAdapter.addSnapshotQuery(user, TAG);

    }



}
