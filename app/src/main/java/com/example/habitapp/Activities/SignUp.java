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
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Leah      Oct-30-2021   Added signup system
 *   1.2       Leah      Nov-01-2021   Removed test habit data.
 *   1.3       Leah      Nov-02-2021   Fixed crash on blank field
 *   1.4       Eric      Nov-03-2021   Fixed other crashes on blank fields
 *   1.5       Eric      Nov-04-2021   Renamed email references to username
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.R;
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
    ArrayList<String> defaultFollowList = new ArrayList();
    String defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/alpha-apps-41471.appspot.com/o/images%2Fdefault_user_icon.png?alt=media&token=e43c5457-b853-4d35-94c8-bd27ca615370";

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
        EditText getUsername = (EditText)findViewById(R.id.signupscreen_username);
        String username = getUsername.getText().toString();
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
        if(name.isEmpty() || username.isEmpty() || password.isEmpty()){
            // name/username/password error
            signupAlert.setMessage("Please enter your name, your username and, password.");
            signupAlert.show();

        } else {
            final DocumentReference findUserRef = db.collection("Doers").document(username);
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
                            userData.put("username",username);
                            userData.put("password",password);
                            userData.put("name",name);
                            userData.put("following",defaultFollowList);
                            userData.put("followers",defaultFollowList);
                            userData.put("profilePic",defaultProfilePic);

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
                                                intent.putExtra(MESSAGE, username);
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

        }




        // TODO figure out a way to move to the mainActivity class without allowing the back
        //  button to take the user back to the log in page


    }

}
