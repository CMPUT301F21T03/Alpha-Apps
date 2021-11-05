/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: BootScreen
 *
 * Description: The class that handles the movement to and from the log in and sign up screen
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.R;

public class BootScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boot_screen);


    }

    // if a user clicks on the boot screen's log in button
    public void bootScreenLogInButtonPressed(View view){
        // move to the log in screen
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    public void bootScreenSignUpButtonPressed(View view){
        // move to the sign up screen
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}