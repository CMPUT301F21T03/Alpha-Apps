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
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.R;

public class AddHabitEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_habit_event);

        Button locationButton = findViewById(R.id.edithabitevent_location);
        locationButton.setOnClickListener(this::addHabitEventLocationButtonPressed);

        Button photographButton = findViewById(R.id.edithabitevent_photograph);
        photographButton.setOnClickListener(this::addHabitEventPhotographButtonPressed);

        Button completeButton = findViewById(R.id.edithabitevent_complete);
        completeButton.setOnClickListener(this::addHabitEventCompleteButtonPressed);
    }

    public void addHabitEventCompleteButtonPressed(View view) {
        finish();
    }

    public void addHabitEventLocationButtonPressed(View view) {}

    public void addHabitEventPhotographButtonPressed(View view) {}
}
