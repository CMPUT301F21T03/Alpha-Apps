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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.R;

public class HabitDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_details);

        //TODO get the intent and unpackage the habit data
        Intent intent = getIntent();

        // TODO set the data to the proper fields

        //set a listener for if the editHabit layout is pressed by the user
        LinearLayout eventLayout = findViewById(R.id.habitdetails_habit_event_layout);
        eventLayout.setOnClickListener(this::habitDetailsHabitEventLayoutPressed);

        // set a listener for if the more button is pressed by the user
        Button moreButton = findViewById(R.id.habitdetails_more);
        moreButton.setOnClickListener(this::habitDetailsMoreButtonPressed);
    }

    private void habitDetailsMoreButtonPressed(View view) {
        //TODO open a dialog as defined in the figma storyboard
        System.out.println("habitDetailsMoreButtonPressed");

    }

    private void habitDetailsHabitEventLayoutPressed(View view) {
        // TODO get the habit event data and open the habit event details frame with said data
        Intent intent = new Intent(this, HabitEventDetails.class);
        // TODO bundle up the item to be sent to the next frame
        startActivity(intent);

    }
}
