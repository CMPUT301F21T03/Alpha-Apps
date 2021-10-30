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
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.R;

public class HabitEventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.habit_event_details);

        // set a listener for the edit button
        Button editButton = findViewById(R.id.habiteventdetails_edit);
        editButton.setOnClickListener(this::habitEventDetailsEditButtonPressed);
    }

    private void habitEventDetailsEditButtonPressed(View view) {
        // navigate to the edit an event activity
        Intent intent = new Intent(this, EditHabitEvent.class);
        // TODO bundle up the item to be sent to the next frame
        startActivity(intent);
    }
}
