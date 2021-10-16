/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: HabitList
 *
 * Description: A class that holds a list of Habit objects
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */


package com.example.prototypehabitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HabitList extends ArrayAdapter<Habit> {

    private ArrayList<Habit> habitList;
    private Context context;

    public HabitList(Context context, ArrayList<Habit> habitList){
        super(context, 0, habitList);
        this.habitList = habitList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View convertView, @NonNull ViewGroup parent) {
        //TODO change the second text view to whatever is necessary to show the progress report
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habit_entry, parent, false);
        }
        Habit habit = habitList.get(pos);

        // get the text views set up for each field
        TextView habitTitle = view.findViewById(R.id.habitentry_habit_title);
        TextView habitProgress = view.findViewById(R.id.habitentry_habit_progress);


        // set the text in each view to its corresponding data
        habitTitle.setText(habit.getTitle());
        habitProgress.setText(habit.getProgress().toString());


        return view;
    }
}
