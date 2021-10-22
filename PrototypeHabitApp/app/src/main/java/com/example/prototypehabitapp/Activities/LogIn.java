/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: LogIn
 *
 * Description: A class that handles any navigation actions away from the log in screen as well as
 * provides the logic to check a users credentials, if the info exists, it passes it to the next
 * frame.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {

    private static final String MESSAGE = "loginhabitdatatransfer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_screen);
    }

    public void logInScreenSignUpHyperlinkPressed(View view) {
        // take this step so that when the user clicks the go back button
        // they go back to the boot screen
        finish();
        // go to the sign up screen
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logInScreenLogInButtonPressed(View view){
        // TODO: put your name here for Firestore. Remove when username system is implemented later.
        final String username = "test";
        //prep Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final DocumentReference user = db.collection("Doers").document(username);

        // get the Strings inside the editText views
        String email = findViewById(R.id.loginscreen_email).toString();
        String password = findViewById(R.id.loginscreen_password).toString();

        //TODO check firebase for an email and password combination matching the above
        // If one pair exists return the user's ID and bring them to their "All Habits" page

        String userID = "some_user";


        // TODO figure out a way to move to the mainActivity class without allowing the back
        //  button to take the user back to the log in page
        Intent intent = new Intent(this, Main.class);
        intent.putExtra(MESSAGE, userID);
        startActivity(intent);

    }


    public static String getMESSAGE(){
        return MESSAGE;
    }
}
