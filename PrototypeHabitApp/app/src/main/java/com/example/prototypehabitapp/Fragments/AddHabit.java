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
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Mathew    Oct-21-2021   Added some navigation features to and from this page
 *   1.2       Arthur    Oct-31-2021   Added full functionality
 *   1.3       Leah      Nov-1-2021    Added ability to write to Firestore
 *   1.4       Eric      Nov-03-2021   Changed date picker dialog to display current date by default
 *   1.5       Eric      Nov-03-2021   Firestore add, edit, delete now part of Habit class. Changes reflected here.
 *   1.6       Eric      Nov-03-2021   Fixed empty list glitch
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prototypehabitapp.Activities.Main;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class AddHabit extends Fragment {

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }
    private final String TAG = "addhabitTAG";

    public AddHabit() {
        super(R.layout.add_habit);
    }
    private Map userData;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Main activity = (Main) getActivity();
        userData = activity.getUserData();
        Log.d(TAG,(String) userData.get("username"));

        // set a listener for if the date hyperlink is pressed by the user
        TextView selectDateHyperlink = getActivity().findViewById(R.id.addhabit_select_date);
        selectDateHyperlink.setOnClickListener(this::addHabitSelectDateHyperlinkPressed);

        // set a listener for if the complete button is pressed
        Button completeButton = getActivity().findViewById(R.id.addhabit_complete);
        completeButton.setOnClickListener((this::addHabitCompleteButtonPressed));

       // initialize spinner for public/private habit
        Spinner privacySpinner = getActivity().findViewById(R.id.habitprivacy_spinner);
        String[] items = new String[]{"Private", "Public"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        privacySpinner.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addHabitCompleteButtonPressed(View view){
        //TODO update firestore with the user entered values
        //Get all buttons
        EditText nameText = getActivity().findViewById(R.id.addhabit_habit_title);
        EditText reasonText = getActivity().findViewById(R.id.addhabit_reason);
        TextView dateText = getActivity().findViewById(R.id.addhabit_select_date);
        Spinner privacyText = (Spinner) getActivity().findViewById(R.id.habitprivacy_spinner);

        CheckBox sunday = getActivity().findViewById((R.id.sunday_checkbox));
        CheckBox monday = getActivity().findViewById((R.id.monday_checkbox));
        CheckBox tuesday = getActivity().findViewById((R.id.tuesday_checkbox));
        CheckBox wednesday = getActivity().findViewById((R.id.wednesday_checkbox));
        CheckBox thursday = getActivity().findViewById((R.id.thursday_checkbox));
        CheckBox friday = getActivity().findViewById((R.id.friday_checkbox));
        CheckBox saturday = getActivity().findViewById((R.id.saturday_checkbox));

        //get values
        String habitName = nameText.getText().toString();
        String habitReason = reasonText.getText().toString();
        String habitDate = dateText.getText().toString() + " 00:00:00";
        String habitPrivacy = privacyText.getSelectedItem().toString();

        boolean sundayVal = sunday.isChecked();
        boolean mondayVal = monday.isChecked();
        boolean tuesdayVal = tuesday.isChecked();
        boolean wednesdayVal = wednesday.isChecked();
        boolean thursdayVal = thursday.isChecked();
        boolean fridayVal = friday.isChecked();
        boolean saturdayVal = saturday.isChecked();

        Log.i("date", habitDate);

        if (TextUtils.isEmpty(habitReason) || TextUtils.isEmpty(habitName) || habitDate.equals("Select a date 00:00:00")){
            Toast.makeText(getContext(), "Complete habit details!", Toast.LENGTH_SHORT).show();
        }
        else{
            // format the input data into a new Habit
            DaysOfWeek frequency = new DaysOfWeek(sundayVal,mondayVal, tuesdayVal, wednesdayVal,thursdayVal, fridayVal, saturdayVal);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
            LocalDateTime newDate = LocalDateTime.parse(habitDate, formatter);
            Habit newHabit = new Habit(habitName, habitReason, newDate, frequency);

            newHabit.addHabitToFirestore(userData);



            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_addHabitFragment_to_allHabitsFragment);


        }

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
        Integer month = today.getMonthValue() - 1;
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
