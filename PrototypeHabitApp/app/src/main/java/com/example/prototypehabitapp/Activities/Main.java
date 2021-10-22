/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Main
 *
 * Description: The class that directs the program to any of the navigation bar fragments (addhabit,
 * feed, profile, allhabits, today). It navigates to the allhabits page by default.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.prototypehabitapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main extends AppCompatActivity {

    private final Context context = Main.this;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.activity_main);
        setUpNavBar();
    }

    // populates navigation bar with menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    private void setUpNavBar(){
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // grab NavHostFragment and setup up controller, and nav bar accordingly
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottomAppBar);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    //TODO use the following code later when we need to refresh the data set

//        //show your page from Firestore
//        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot queryDocumentSnapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                habitDataList.clear();
//                assert queryDocumentSnapshot != null;
//                if (queryDocumentSnapshot != null && queryDocumentSnapshot.exists()) {
//                    // just takes the name for testing purposes
//                    Map userData = queryDocumentSnapshot.getData();
//                    for(String s: (ArrayList<String>) userData.get("habits")){
//                        habitDataList.add(new Habit(s,"reason",LocalDateTime.now(),testDaysOfWeek));
//                    }
//                }
//                habitAdapter.notifyDataSetChanged();
//            }
//        });
//
//    }


}