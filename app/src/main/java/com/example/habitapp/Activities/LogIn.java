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
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Leah      Oct-30-2021   Added Firestore functionality
 *   1.2       Leah      Nov-02-2021   Fixed crash on blank field
 *   1.3       Eric      Nov-03-2021   Fixed other crashes on blank fields
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    private static final String MESSAGE = "loginhabitdatatransfer";
    private static final String TAG = "loginTAG";

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
        // prep Firestore
        // this can be moved to initialization of Login

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        // get the Strings inside the editText views
        EditText emailEdit = (EditText)findViewById(R.id.loginscreen_email);
        String email = emailEdit.getText().toString();
        EditText passwordEdit = (EditText)findViewById(R.id.loginscreen_password);
        String password = passwordEdit.getText().toString();

        // TODO: could use something other than an alertdialog, like a snackbar
        // initiates an alertdialog to use if there is an error logging in
        AlertDialog.Builder loginAlert = new AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setNegativeButton("OK", null)
                ;
        // save the context for the Firestore listener
        Context loginContext = this;

        // try obtaining a reference to the email field
        if(email.isEmpty() || password.isEmpty()){
            // username/password error
            loginAlert.setMessage("Please enter your username and password.");
            loginAlert.show();
        }
        else{
            final DocumentReference findUserRef = db.collection("Doers").document(email);
            findUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // Validates username exists in database
                        if (document.exists()) {
                            Map userData = document.getData();
                            // Validating password
                            if(((String) userData.get("password")).equals(password)){
                                // LOGIN HERE
                                // TODO figure out a way to move to the mainActivity class without allowing the back
                                //  button to take the user back to the log in page
                                //  send a state through the intent bundle?
                                Intent intent = new Intent(loginContext, Main.class);
                                //
                                intent.putExtra(MESSAGE, email);
                                // bundle the user info into Serializable and send through Intent
                                intent.putExtra("userData",(Serializable) userData);
                                // start Main Activity
                                startActivity(intent);
                            }
                            else{
                                // if the password is incorrect
                                loginAlert.setMessage("Incorrect Password");
                                loginAlert.show();
                            }

                        } else {
                            // if the username does not exist
                            loginAlert.setMessage("Username does not exist");
                            loginAlert.show();
                        }
                    } else {
                        // firebase error
                        loginAlert.setMessage("Connection Error");
                        loginAlert.show();
                    }
                }
            });
        }



    }


    public static String getMESSAGE(){
        return MESSAGE;
    }
}
