/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: SignUp
 *
 * Description: A class that handles any navigation actions away from the sign up screen as well as
 * provides the logic to obtain a users credentials and enter them into the online database. It then
 * opens to the default navigation bar screen
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.R;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {

    private final Context context = this;

    //get the list view for the all habits page
    ArrayAdapter<Habit> habitAdapter;
    ArrayList<Habit> habitDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);
    }

    public void signUpScreenLogInHyperlinkPressed(View view){
        // take this step so that when the user clicks the go back button
        // they go back to the boot screen
        finish();
        // go to the log in screen
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    public void signUpScreenSignInButtonPressed(View view){
        // get the Strings inside the editText views
        String name = findViewById(R.id.signupscreen_name_alias).toString();
        String email = findViewById(R.id.signupscreen_email).toString();
        String password = findViewById(R.id.signupscreen_password).toString();

        // TODO go to firebase and create a unique ID for this user and assign it to them

        // TODO figure out a way to move to the mainActivity class without allowing the back
        //  button to take the user back to the log in page
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);

    }

}
