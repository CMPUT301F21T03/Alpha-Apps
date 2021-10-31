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
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.R;

import java.util.ArrayList;

public class HabitDetails extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    // attributes
    private Habit habit;
    private DaysOfWeek weekOccurence;

    boolean habit_exists = false;

    Button sunday_button;
    Button monday_button;
    Button tuesday_button;
    Button wednesday_button;
    Button thursday_button;
    Button friday_button;
    Button saturday_button;

    ArrayList<Boolean> weekOccurenceList;
    ArrayList<Button> weekButtons;


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

        weekButtons = new ArrayList<>();
        sunday_button = findViewById(R.id.dayofweekbar_sunday);
        weekButtons.add(sunday_button);
        monday_button = findViewById(R.id.dayofweekbar_monday);
        weekButtons.add(monday_button);
        tuesday_button = findViewById(R.id.dayofweekbar_tuesday);
        weekButtons.add(tuesday_button);
        wednesday_button = findViewById(R.id.dayofweekbar_wednesday);
        weekButtons.add(wednesday_button);
        thursday_button = findViewById(R.id.dayofweekbar_thursday);
        weekButtons.add(thursday_button);
        friday_button = findViewById(R.id.dayofweekbar_friday);
        weekButtons.add(friday_button);
        saturday_button = findViewById(R.id.dayofweekbar_saturday);
        weekButtons.add(saturday_button);

        // if a selected habit was sent over in the intent
        Intent intent = getIntent();
        if (intent.getSerializableExtra("selected_habit") != null)  {
            // then we can work with it...
            // set the data to the proper fields in the activity
            habit = (Habit) intent.getSerializableExtra("selected_habit");
            title.setText(habit.getTitle());
            reason.setText(habit.getReason());
            date_started.setText(habit.getDateStarted().toString());
            weekOccurenceList = habit.getWeekOccurence().getAll();
            for (int i = 0; i < 7; i++) {
                setButtonColor(weekButtons.get(i), weekOccurenceList.get(i));
            }

        }

        //set a listener for if the editHabit layout is pressed by the user
        LinearLayout eventLayout = findViewById(R.id.habitdetails_habit_event_layout);
        eventLayout.setOnClickListener(this::habitDetailsHabitEventLayoutPressed);

        // set a listener for if the more button is pressed by the user
        Button moreButton = findViewById(R.id.habitdetails_more);
        moreButton.setOnClickListener(this::habitDetailsMoreButtonPressed);
    }

    private void habitDetailsMoreButtonPressed(View view) {
        System.out.println("more button pressed");
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(HabitDetails.this);
        inflater.inflate(R.menu.habit_details_menu, popup.getMenu());
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreMenuMarkAsDone:
                // TODO call mark as done functionality
                System.out.println("mark as done pressed");
                return true;
            case R.id.moreMenuLogHabit:
                // TODO call log habit functionality
                System.out.println("log habit pressed");
                return true;
            case R.id.moreMenuEditHabit:
                // TODO edit habit functionality
                System.out.println("edit habit pressed");
                return true;
            case R.id.moreMenuDeleteHabit:
                // TODO delete habit functionality
                System.out.println("delete habit pressed");
                return true;
            default:
                return false;
        }
    }


    private void habitDetailsHabitEventLayoutPressed(View view) {
        // TODO get the habit event data and open the habit event details frame with said data
        Intent intent = new Intent(this, HabitEventDetails.class);
        // TODO bundle up the item to be sent to the next frame
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
