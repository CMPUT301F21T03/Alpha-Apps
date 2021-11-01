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


package com.example.prototypehabitapp.DataClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototypehabitapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class HabitList extends ArrayAdapter<Habit> implements Serializable {

    private ArrayList<Habit> habitList;
    private Context context;

    /**
     * creates a list of habit objects. As this class extends an array adapter this object must
     * contain the context of its use so other functions may use this value later
     * @param context the context into which the habit list is created in
     * @param habitList the raw data of the habit list which will be formatted by this class
     */
    public HabitList(Context context, ArrayList<Habit> habitList){
        super(context, 0, habitList);
        this.habitList = habitList;
        this.context = context;
    }

    /**
     * Returns a View that represents a list of habitdata
     * @param pos the position in the habitdatalist that the function is on
     * @param convertView
     * @param parent
     * @return the view of the object after it has been formatted with habit data
     */
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
