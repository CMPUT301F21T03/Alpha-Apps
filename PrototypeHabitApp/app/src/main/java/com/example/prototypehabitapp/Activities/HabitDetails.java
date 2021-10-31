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
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.R;

import java.util.ArrayList;

public class HabitDetails extends AppCompatActivity {

    Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_details);

        //TODO get the intent and unpackage the habit data
        Intent intent = getIntent();

        // TODO set the data to the proper fields

        ListView eventsListview = findViewById(R.id.habitdetails_habit_event_list);
        ArrayList<Event> events = habit.getEventList();
        ArrayAdapter<Event> eventsAdapter = new EventList(this, events);
        events.setAdapter(eventsAdapter);

        //set a listener for if the editHabit layout is pressed by the user
        events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = (Event) eventList.getItemAtPosition(i);
                habitDetailsHabitEventLayoutPressed(event);
            }
        });

        // set a listener for if the more button is pressed by the user
        Button moreButton = findViewById(R.id.habitdetails_more);
        moreButton.setOnClickListener(this::habitDetailsMoreButtonPressed);
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

    private void habitDetailsHabitEventLayoutPressed(Event event) {
        // TODO get the habit event data and open the habit event details frame with said data
        Intent intent = new Intent(this, HabitEventDetails.class);
        // TODO bundle up the item to be sent to the next frame
        intent.putExtra("EVENT", event);
        startActivity(intent);
    }
}
