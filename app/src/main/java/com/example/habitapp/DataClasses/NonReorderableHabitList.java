/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: NonReorderableHabitList
 *
 * Description: A class that holds a list of Habit objects
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Mathew    Oct-31-2021   Added Javadocs
 *   1.2       Leah      Nov-03-2021   Added addSnapshotQuery to better modularize data. Updated Javadocs accordingly
 *   1.3       Leah      Nov-03-2021   Fixed empty list glitch
 *   1.4       Mathew    Nov-12-2021   Updated the look of the HabitListView to be more aesthetically pleasing
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
import android.widget.ProgressBar;
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

public class NonReorderableHabitList extends ArrayAdapter<Habit> implements Serializable {

    private ArrayList<Habit> habitList;
    private Context context;

    /**
     * creates a list of habit objects. As this class extends an array adapter this object must
     * contain the context of its use so other functions may use this value later
     * @param context the context into which the habit list is created in
     * @param habitList the raw data of the habit list which will be formatted by this class
     */
    public NonReorderableHabitList(Context context, ArrayList<Habit> habitList){
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int pos, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.habit_entry, parent, false);
        }
        Habit habit = getHabitAtPosition(pos);

        // get the text views set up for each field
        TextView habitTitle = view.findViewById(R.id.habitentry_habit_name);
        TextView habitProgressText = view.findViewById(R.id.habitentry_habit_progress);
        ProgressBar habitProgressBar = view.findViewById(R.id.habitentry_progress_bar);


        // set the text in each view to its corresponding data
        habitTitle.setText(habit.getTitle());
        habitProgressText.setText(habit.getProgress().toString()+ "%");
        habitProgressBar.setProgress(habit.getProgress().intValue());



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
                            // Convert Firestore's stored time to LocalDateTime
                            Map getDateChecked = (Map) doc.get("dateLastChecked");
                            String newDateStringChecked = getDateChecked.get("year").toString() + "-" +
                                    getDateChecked.get("monthValue").toString() + "-" +
                                    getDateChecked.get("dayOfMonth").toString() + " 00:00:00";
                            LocalDateTime newDateChecked = LocalDateTime.parse(newDateStringChecked, formatter);
                            LocalDateTime ldtCheck = newDateChecked;
                            // Convert Firestore's stored days of week to DaysOfWeek
                            Map<String, Boolean> docDaysOfWeek = (Map<String, Boolean>) doc.get("weekOccurence");
                            Habit habitToAdd;
                            if (doc.getBoolean("privacy") == null || (doc.get("allHabitsIndex") == null) || (doc.get("todayHabitsIndex") == null) ) {
                                habitToAdd = new Habit(doc.getString("title"),
                                        doc.getString("reason"),
                                        ldt,
                                        new DaysOfWeek(docDaysOfWeek),
                                        true,
                                        -1,
                                        -1,
                                        ldtCheck,
                                        ((Long) doc.get("daysCompleted")).intValue(),
                                        ((Long) doc.get("daysTotal")).intValue());
                            } else {
                                habitToAdd = new Habit(doc.getString("title"),
                                        doc.getString("reason"),
                                        ldt,
                                        new DaysOfWeek(docDaysOfWeek),
                                        doc.getBoolean("privacy"),
                                        ((Long) doc.get("allHabitsIndex")).intValue(),
                                        ((Long) doc.get("todayHabitsIndex")).intValue(),
                                        ldtCheck,
                                        ((Long) doc.get("daysCompleted")).intValue(),
                                        ((Long) doc.get("daysTotal")).intValue());
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

    /**
     * Adds a Habit type object to our internal ArrayList
     * Also executes a check to see if Habit already exists in list,
     * and will throw an exception in that case.
     * @param habit The Habit object to add to the ArrayList
     */
    public void addHabit(Habit habit){
        if (habitList.contains(habit)) {
            throw new IllegalArgumentException();
        }
        habitList.add(habit);
    }

    /**
     * Grabs the entire ArrayList containing the habits for use in certain siutations.
     * @return habitlist attribute that is the ArrayList containing the Habit objects
     */
    public List<Habit> getHabits() {
        return habitList;
    }

    /**
     * Grabs the Habit object at a specified index in the ArrayList
     * @param pos index of Habit object desired in ArrayList
     * @return Habit object at specified index
     */
    public Habit getHabitAtPosition(Integer pos){
        return habitList.get(pos);
    }

    /**
     * Clears the ArrayList containing all the Habit objects
     */
    public void clearHabitList(){
        habitList.clear();
    }

    /**
     * Returns a boolean that's true if the ArrayList storing the Habit objects is empty,
     * false otherwise
     * @return boolean if Habit ArrayList is empty or not
     */
    public Boolean getHabitListEmpty() {
        return habitList.isEmpty();
    }
}