/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: MainActivity
 *
 * Description: The class that directs the program to any of the navigation bar fragments (addhabit,
 * feed, profile, allhabits, today). It navigates to the allhabits page by default.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2020   Created
 *   1.1       Leah      Oct-30-2020   Added firebase user info to bundle.
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.Context;
import android.content.Intent;
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
import com.example.habitapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final Context context = MainActivity.this;
    private static final String TAG = "mainTAG";
    private Map userData;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get the bundle with user data from firestore
        Intent intent = getIntent();
        userData = (Map) intent.getSerializableExtra("userData");
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


    public Map getUserData() {
        return userData;
    }

    public void setUserData(Map userData) {
        this.userData = userData;
    }
}