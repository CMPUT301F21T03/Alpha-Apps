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
 *   1.1       Leah      Oct-30-2020   Added signup system
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private static final String MESSAGE = "loginhabitdatatransfer";

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

        EditText getName = (EditText)findViewById(R.id.signupscreen_name_alias);
        String name = getName.getText().toString();
        EditText getEmail = (EditText)findViewById(R.id.signupscreen_email);
        String email = getEmail.getText().toString();
        EditText getPassword = (EditText)findViewById(R.id.signupscreen_password);
        String password = getPassword.getText().toString();

        // TODO go to firebase and create a unique ID for this user and assign it to them

        // prep Firestore
        // this can be moved to initialization of Login
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();



        // TODO: could use something other than an alertdialog, like a snackbar
        // initiates an alertdialog to use if there is an error logging in
        AlertDialog.Builder signupAlert = new AlertDialog.Builder(this)
                .setTitle("Signup Error")
                .setNegativeButton("OK", null)
                ;
        // save the context for the Firestore listener
        Context signupContext = this;

        // try obtaining a reference to the email field
        final DocumentReference findUserRef = db.collection("Doers").document(email);
        findUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Validates username exists in database
                    if (!document.exists()) {
                        // TODO: initiate name and other user profile details here
                        // Must change habit format later
                        Map userData = new HashMap<>();
                        ArrayList<String> testHabits = new ArrayList<String>();
                        testHabits.add("My First Habit");  // you can add more here for your test data
                        userData.put("habits",testHabits);
                        userData.put("username",email);
                        userData.put("password",password);
                        userData.put("name",name);

                        // Validating password
                        // TODO change this later to something other than random 3
                        if(password.length() > 2){
                            findUserRef.set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        // if signup is successful
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // SIGNUP HERE
                                            // TODO figure out a way to move to the mainActivity class without allowing the back
                                            //  button to take the user back to the log in page
                                            //  send a state through the intent bundle?
                                            Intent intent = new Intent(signupContext, Main.class);
                                            intent.putExtra(MESSAGE, email);
                                            // bundle the user info into Serializable and send through Intent
                                            intent.putExtra("userData",(Serializable) userData);
                                            // start Main Activity
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        // if signup fails
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            signupAlert.setMessage("Connection Error");
                                            signupAlert.show();
                                        }
                                    });

                        }
                        else{
                            // if the password is incorrect
                            signupAlert.setMessage("Your password is too short.");
                            signupAlert.show();
                        }

                    } else {
                        // if the username does not exist
                        signupAlert.setMessage("Username already exists");
                        signupAlert.show();
                    }
                } else {
                    // firebase error
                    signupAlert.setMessage("Connection Error");
                    signupAlert.show();
                }
            }
        });


        // TODO figure out a way to move to the mainActivity class without allowing the back
        //  button to take the user back to the log in page


    }

}
