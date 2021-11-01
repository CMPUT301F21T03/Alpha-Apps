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
 *   1.5       Mathew    Oct-31-2021   Added a more descriptive tag by which to get the intent info
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ListView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.DataClasses.Event;
import com.example.prototypehabitapp.Fragments.AllHabits;
import com.example.prototypehabitapp.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HabitDetails extends AppCompatActivity{

    // attributes
    private Habit habit;
    private DaysOfWeek weekOccurence;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_details);

        // connect to view elements
        EditText title = findViewById(R.id.habitdetails_title);
        // frequency needs to be worked on later
        EditText reason = findViewById(R.id.habitdetails_reason_text);
        TextView date_started = findViewById(R.id.habitdetails_date_started);

        KeyListener titleKeyListener = title.getKeyListener();
        title.setKeyListener(null);

        KeyListener reasonKeyListener = reason.getKeyListener();
        reason.setKeyListener(null);

        ArrayList<Button> weekButtons = prepareDayOfWeekButtons();


        // if a selected habit was sent over in the intent
        Intent intent = getIntent();
        if (intent.getSerializableExtra(AllHabits.getTAG()) != null)  {
            // then we can work with it...
            // set the data to the proper fields in the activity
            habit = (Habit) intent.getSerializableExtra(AllHabits.getTAG());
            title.setText(habit.getTitle());
            reason.setText(habit.getReason());
            date_started.setText(habit.getDateStarted().toString());
            ArrayList<Boolean> weekOccurenceList = habit.getWeekOccurence().getAll();
            for (int i = 0; i < 7; i++) {
                setButtonColor(weekButtons.get(i), weekOccurenceList.get(i));
            }

        }

        ListView eventsListview = findViewById(R.id.habitdetails_habit_event_list);

        //add test habit data (remove later)
        habit = new Habit("title", "reason", LocalDateTime.now(), new DaysOfWeek());

        ArrayList<Event> events = habit.getEventList();
        ArrayAdapter<Event> eventsAdapter = new EventList(this, events);
        eventsListview.setAdapter(eventsAdapter);

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


    private ArrayList<Button> prepareDayOfWeekButtons(){
        ArrayList<Button> weekButtons = new ArrayList<>();
        Button sunday_button = findViewById(R.id.sunday_checkbox);
        weekButtons.add(sunday_button);
        Button monday_button = findViewById(R.id.monday_checkbox);
        weekButtons.add(monday_button);
        Button tuesday_button = findViewById(R.id.tuesday_checkbox);
        weekButtons.add(tuesday_button);
        Button wednesday_button = findViewById(R.id.wednesday_checkbox);
        weekButtons.add(wednesday_button);
        Button thursday_button = findViewById(R.id.thursday_checkbox);
        weekButtons.add(thursday_button);
        Button friday_button = findViewById(R.id.friday_checkbox);
        weekButtons.add(friday_button);
        Button saturday_button = findViewById(R.id.saturday_checkbox);
        weekButtons.add(saturday_button);
        return weekButtons;
    }

    private void habitDetailsMoreButtonPressed(View view) {
        //TODO open a dialog as defined in the figma storyboard
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.habit_more_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.mark_done) {
                    // TODO mark as done
                } else if (menuItem.getItemId() == R.id.log_habit) {
                    Intent intent = new Intent(HabitDetails.this, AddHabitEvent.class);
                    // TODO indicate that it's a new entry
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.edit_habit) {
                    // TODO move to EditHabit class
                } else if (menuItem.getItemId() == R.id.delete_habit) {
                    // TODO delete habit
                }
                return true;
            }
        });
    }

    private void habitDetailsHabitEventLayoutPressed(Event event){
        Intent intent = new Intent(this, HabitEventDetails.class);
        intent.putExtra("EVENT", event);
        startActivity(intent);
    }

    private void setButtonColor(Button button, boolean val) {
        if (val) {
            button.setBackgroundColor(Color.BLUE);
        } else {
            button.setBackgroundColor(Color.GRAY);
        }
    }
}
