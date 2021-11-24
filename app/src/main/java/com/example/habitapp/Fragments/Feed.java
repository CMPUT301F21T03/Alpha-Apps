/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Feed
 *
 * Description: Handles the user interactions of the Feed fragment
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Eric      Oct-21-2021   Created
 *   1.1       Moe       Nov-24-2021   Search functionality added
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Feed extends Fragment {
    public Feed() {
        super(R.layout.feed);
    }

    private EditText searchedUserNameEdit;
    private String searchedUserName;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        searchedUserNameEdit = (EditText) view.findViewById(R.id.feed_search_field);
        // do something
        ImageButton searchButton = view.findViewById(R.id.feed_search_button);
        searchButton.setOnClickListener(this::searchButtonPressed);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void searchButtonPressed(View view) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        searchedUserName = searchedUserNameEdit.getText().toString();

        AlertDialog.Builder searchAlert = new AlertDialog.Builder(getActivity())
                .setNegativeButton("OK", null)
                ;

        final DocumentReference findUserRef = db.collection("Doers").document(searchedUserName);
        findUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // checks if the username searched exists in database
                    if (document.exists()) {
                        Map UserData = document.getData();
                        // TODO move to user's page
                        searchAlert.setMessage("Username: \""+searchedUserName+"\"" + " exists!");
                        searchAlert.show();
                    } else {
                        searchAlert.setMessage("Username: \""+searchedUserName+"\"" + " doesn't exist");
                        searchAlert.show();
                    }
                }
            }
        });
    }

}
