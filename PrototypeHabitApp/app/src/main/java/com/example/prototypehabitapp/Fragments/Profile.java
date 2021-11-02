/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Profile
 *
 * Description: Handles the user interactions of the profile fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Mathew    Oct-21-2021   Added some navigation features to and from this page
 *   1.2       Mathew    Nov-01-2021   Added logic to the frame as well as changed its associated
 *                                      layout file. Refactored navigation to go to new frames.
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.Activities.BootScreen;
import com.example.prototypehabitapp.Activities.FollowingFollowers;
import com.example.prototypehabitapp.DataClasses.User;
import com.example.prototypehabitapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class Profile extends Fragment {
    public Profile() {
        super(R.layout.profile);
    }

    private static final String TAG = "profileTAG";

    private boolean allowedToEdit = false;
    private User profile;
    private View usernameView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // set the editability of the profile editText to false
        usernameView = view.findViewById(R.id.profilelistentry_username);
        usernameView.setEnabled(false);
        // get the current user's data
        getUserData();

        // set the current user's data
        setUserData(view);

        //init button listeners
        setButtonListeners(view);

    }

    private void setUserData(View view) {
        TextView photoView = view.findViewById(R.id.profilelistentry_photo);
        EditText usernameEditText = view.findViewById(R.id.profilelistentry_username);
        TextView idView = view.findViewById(R.id.profilelistentry_id);
        photoView.setText(profile.getPhotoStandIn());
        usernameEditText.setText(profile.getName());
        idView.setText(profile.getUniqueID());
    }

    public void getUserData(){

        //add some test data
        profile = new User("uniqueID1234567890", "my_name2", "my_email2", "my_password2");

//        // show your page from Firestore
//        FirebaseFirestore db;
//        db = FirebaseFirestore.getInstance();
//        final CollectionReference user = db.collection("Doers").document((String) userData.get("username")).collection("habits");
//        user.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot querySnapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    // if error occurs
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//
//                //TODO grab this user's profile data (only 1 item should be retrieved)
//                profile = new User();
//
//
//            }
//        });
//        // END REMOVE HERE
//
    }

    private void setButtonListeners(View view){
        // set a listener for if the edit button is pressed
        Button editButton = view.findViewById(R.id.profile_edit);
        editButton.setOnClickListener(this::profileEditButtonPressed);


        // set a listener for if the following button is pressed
        Button followingButton = view.findViewById(R.id.profile_following);
        followingButton.setOnClickListener(this::profileFollowingButtonPressed);


        // set a listener for if the followers button is pressed
        Button followersButton = view.findViewById(R.id.profile_followers);
        followersButton.setOnClickListener(this::profileFollowersButtonPressed);

        // set a listener for if the log out button is pressed
        Button logOutButton = view.findViewById(R.id.profile_log_out);
        logOutButton.setOnClickListener(this::profileLogOutButtonPressed);

        // set a listener for if the photo of the persons profile is pressed
        TextView profilePictureStandIn = view.findViewById(R.id.profilelistentry_photo);
        profilePictureStandIn.setOnClickListener(this::profilePhotoStandInPressed);
    }

    private void profilePhotoStandInPressed(View view) {
        if (allowedToEdit){
            //TODO allow the user to change their profile picture
        }
    }

    private void profileLogOutButtonPressed(View view) {
        //TODO sign the user out AND dont allow them to get back to this page using the back button
        Intent intent = new Intent(getContext(), BootScreen.class);
        startActivity(intent);
    }

    private void profileFollowersButtonPressed(View view) {
        Intent intent = new Intent(getContext(), FollowingFollowers.class);
        intent.putExtra("FOLLOWING?", "follower");
        startActivity(intent);
    }

    private void profileFollowingButtonPressed(View view) {
        Intent intent = new Intent(getContext(), FollowingFollowers.class);
        intent.putExtra("FOLLOWING?", "following");
        startActivity(intent);

    }

    private void profileEditButtonPressed(View view) {
        if (allowedToEdit == false){
            allowedToEdit = true;
            usernameView.setEnabled(true);
            Button editButton = view.findViewById(R.id.profile_edit);
            editButton.setText("Save");
        }else{
            allowedToEdit = false;
            usernameView.setEnabled(false);
            Button editButton = view.findViewById(R.id.profile_edit);
            editButton.setText("Edit");
            // get the information on screen to send to firebase
            EditText usernameEditText = (EditText) usernameView;
            String newUsername = usernameEditText.getText().toString();
            //TODO get the updated profile picture

            //TODO put all of the updated info above into firebase in place of the old data



        }



    }

    private static String getTAG(){
        return TAG;
    }


}
