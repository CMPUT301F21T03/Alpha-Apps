/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: AddHabit
 *
 * Description: Handles the user interactions of the add habit fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2020   Created
 *   1.1       Mathew    Oct-21-2020   Added some navigation features to and from this page
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.Activities.Main;
import com.example.prototypehabitapp.R;

import java.time.LocalDateTime;


public class AddHabit extends Fragment {

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    public AddHabit() {
        super(R.layout.add_habit);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // set a listener for if the date hyperlink is pressed by the user
        TextView selectDateHyperlink = getActivity().findViewById(R.id.addhabit_select_date);
        selectDateHyperlink.setOnClickListener(this::addHabitSelectDateHyperlinkPressed);

        // set a listener for if the complete button is pressed
        Button completeButton = getActivity().findViewById(R.id.addhabit_complete);
        completeButton.setOnClickListener((this::addHabitCompleteButtonPressed));
    }

    public void addHabitCompleteButtonPressed(View view){
        //TODO update firestore with the user entered values

        navigateToMainAcitivity();
    }

    private void navigateToMainAcitivity(){
        Intent intent = new Intent(getContext(), Main.class);
        startActivity(intent);
    }

    //opens up a date picker dialog for the user (and handles the logic for the dialog)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addHabitSelectDateHyperlinkPressed(View view){
        // get the current date
        LocalDateTime today = LocalDateTime.now();
        Integer year = today.getYear();
        Integer month = today.getMonthValue();
        Integer day = today.getDayOfMonth();
        // create a date picker dialog set to todays date
        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                null,
                year, month, day);
        // make the white box around the date dialog invisible
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the logic that takes place when the user clicks the ok option
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                // format the selection into a string (yyyy-mm-dd)
                Integer year = (Integer) y;
                Integer month = m + 1; //counts the months from 0-11 not 1-12 (add one to correct)
                Integer day = (Integer) d;
                String selectedDate = year.toString() + "-" + month.toString() + "-" + day.toString();

                // set the hint text to the date that was selected
                TextView selectDateTextView = getActivity().findViewById(R.id.addhabit_select_date);
                selectDateTextView.setText(selectedDate);
            }
        });

        // make the dialog visible to the user
        dialog.show();
    }

}
