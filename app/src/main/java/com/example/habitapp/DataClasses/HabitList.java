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
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Mathew    Oct-31-2021   Added Javadocs
 *   1.2       Leah      Nov-03-2021   Added addSnapshotQuery to better modularize data. Updated Javadocs accordingly
 *   1.3       Leah      Nov-03-2021   Fixed empty list glitch
 * =|=======|=|======|===|====|========|===========|================================================
 */


package com.example.habitapp.DataClasses;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.habitapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Habit habit = getHabitAtPosition(pos);

        // get the text views set up for each field
        TextView habitTitle = view.findViewById(R.id.habitentry_habit_title);
        TextView habitProgress = view.findViewById(R.id.habitentry_habit_progress);


        // set the text in each view to its corresponding data
        habitTitle.setText(habit.getTitle());
        habitProgress.setText(habit.getProgress().toString());


        return view;
    }

    /**
     * Sets a SnapshotListener to this HabitList such that it can be populated with Habits according to the input query.
     * @param query The document and query to find Habits from in the Firestore
     * @param TAG The tag associated with the context it is called from, in case an error occurs
     */
    @NonNull
    public void addSnapshotQuery(Query query, String TAG){
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                // check for errors in listen
                if (e != null) {
                    // if error occurs
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // if there are Habits
                clearHabitList();
                if (!querySnapshot.isEmpty()){
                    List<String> habits = new ArrayList<>();
                    for(QueryDocumentSnapshot doc : querySnapshot){
                        // make sure the title exists
                        if (doc.get("title") != null) {
                            // Convert Firestore's stored time to LocalDateTime
                            Map getDate = (Map) doc.get("dateStarted");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
                            String newDateString = getDate.get("year").toString() + "-" +
                                    getDate.get("monthValue").toString() + "-" +
                                    getDate.get("dayOfMonth").toString() + " 00:00:00";
                            LocalDateTime newDate = LocalDateTime.parse(newDateString, formatter);
                            LocalDateTime ldt = newDate;
                            // Convert Firestore's stored days of week to DaysOfWeek
                            Map<String, Boolean> docDaysOfWeek = (Map<String, Boolean>) doc.get("weekOccurence");
                            Habit habitToAdd;
                            if (doc.getBoolean("privacy") == null) {
                                 habitToAdd = new Habit(doc.getString("title"),doc.getString("reason"),ldt,new DaysOfWeek(docDaysOfWeek), true);
                            } else {
                                 habitToAdd = new Habit(doc.getString("title"),doc.getString("reason"),ldt,new DaysOfWeek(docDaysOfWeek), doc.getBoolean("privacy"));
                            }

                            // Set the document ID in case it needs to be fetched for delete/edits
                            habitToAdd.setFirestoreId(doc.getId());
                            // Add to the ListArray
                            addHabit(habitToAdd);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    public void addHabit(Habit habit){
        if (habitList.contains(habit)) {
            throw new IllegalArgumentException();
        }
        habitList.add(habit);
    }
    public List<Habit> getHabits() {
        return habitList;
    }



        public Habit getHabitAtPosition(Integer pos){
        return habitList.get(pos);
    }

    public void clearHabitList(){
        habitList.clear();
    }

    public Boolean getHabitListEmpty() {
        return habitList.isEmpty();
    }
}
