/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: AllHabits
 *
 * Description: Handles the user interactions of the all habits fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Mathew    Oct-21-2021   Added some navigation features, added test data
 *   1.2       Leah      Oct-30-2021   Now populates from user firestore document, does not use subcollection yet
 *   1.3       Leah      Nov-02-2021   Now uses Habits subcollection. Cleaned up test code. Adds Firestore document ID.
 *   1.4       Leah      Nov-03-2021   Changed empty habit list text to use emptyListView, moved list population to HabitList
 *   1.5       Eric      Nov-03-2021   Firestore add, edit, delete now part of Habit class. Changes reflected here.
 *   1.6       Eric      Nov-24-2021   Changed to RecyclerView to allow for reorderability
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.Activities.HabitDetails;
import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.EventList;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.HabitList;
import com.example.habitapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class AllHabits extends Fragment implements HabitList.OnHabitListener{

    private static final String TAG = "allhabitsTAG";

    public AllHabits() {
        super(R.layout.all_habits);
    }

    // prep the all_habits screen related objects
    private RecyclerView allHabitsRecyclerView;
    private HabitList habitAdapter;
    private ArrayList<Habit> habitDataList = new ArrayList<>();
    private Map userData;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // get user data
        Main activity = (Main) getActivity();
        userData = activity.getUserData();
        Log.d(TAG,"Successfully logged in: " + (String) userData.get("username"));

        setHabitRecyclerAdapter();

        // set up reorderability
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(allHabitsRecyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHabitRecyclerAdapter() {

        allHabitsRecyclerView = getView().findViewById(R.id.allhabits_habit_list);
        habitAdapter = new HabitList(habitDataList, this);
        allHabitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allHabitsRecyclerView.setAdapter(habitAdapter);
        getHabitDataList(habitAdapter);
        habitAdapter.registerAdapterDataObserver(myObserver);

    }

    // manages showing/hiding the empty list message
    private RecyclerView.AdapterDataObserver myObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();

            if (getView()  != null) {
                TextView hiddenText = getView().findViewById(R.id.allhabits_hidden_textview_1);
                if (habitAdapter.getItemCount() == 0) {
                    hiddenText.setVisibility(View.VISIBLE);
                    hiddenText.setTextColor(Color.parseColor("#000000"));
                } else {
                    hiddenText.setTextColor(Color.parseColor("#00000000"));
                    hiddenText.setVisibility(View.INVISIBLE);
                }
            }

        }
    };

    // handles reorderability
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(habitDataList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            for (int i = 0; i < habitDataList.size(); i++) {
                habitDataList.get(i).setAllHabitsIndex(i);
            }

            // update in firestore
            habitDataList.get(fromPosition).editHabitInFirestore(userData);
            habitDataList.get(toPosition).editHabitInFirestore(userData);


            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }
    } ;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHabitDataList(HabitList habitAdapter){
        // show your page from Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        final Query user = db.collection("Doers")
                                         .document((String) userData.get("username"))
                                         .collection("habits")
                                         .orderBy("allHabitsIndex");
        habitAdapter.addSnapshotQuery(user,TAG);


    }

    @Override
    public void onHabitClick(int position) {
        // get the item that the user selected
        Habit itemToSend = (Habit) habitDataList.get(position);
        Intent intent = new Intent(getContext(), HabitDetails.class);

        // Put pressed habit into bundle to send to HabitDetails
        intent.putExtra("habit",itemToSend);
        intent.putExtra("userData", (Serializable) userData);
        startActivity(intent);
    }
}
