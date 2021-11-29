/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Notification
 *
 * Description: Sets a listener that responds when a user clicks on a "notifications" button.
 * When they do so it will open a dialog with any pertinent notification information
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Arthur   Nov-24-2021   Created
 *   1.2       Arthur   Nov-29-2021   Implemented functionality
 *
 * =|=======|=|======|===|====|========|===========|================================================
 */
package com.example.habitapp.DataClasses;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.habitapp.Activities.Main;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RequestList extends ArrayAdapter<Request> {
    private ArrayList<Request> requests;
    private Context context;

    public RequestList(Context context, ArrayList<Request> notifications) {
        super(context,R.layout.noti_item,notifications);
        this.context = context;
        this.requests = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View view = convertView;
        try{
            ViewHoldler viewHoldler;
            if (convertView == null){
                Log.d(">>>>>>>", "i got herererere");
                convertView = LayoutInflater.from(context).inflate(R.layout.noti_item, parent, false);
                viewHoldler = new ViewHoldler();
                viewHoldler.notiText = convertView.findViewById(R.id.noti_text);
                viewHoldler.acceptButton = convertView.findViewById(R.id.accept_button);
                viewHoldler.declineButton = convertView.findViewById(R.id.decline_button);
                viewHoldler.declineButton.setTag(position);
                viewHoldler.acceptButton.setTag(position);

                Request request = requests.get(position);
                String username = request.getUsername();
                String suffix = request.getSUFFIX();
                viewHoldler.notiText.setText(username +" "+  suffix);

                viewHoldler.declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = (int) view.getTag();
                        deleteRequest(pos);
                        requests.remove(pos);
                        RequestList.this.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Request Denied!", Toast.LENGTH_SHORT).show();
                    }
                });

                viewHoldler.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = (int) view.getTag();
                        addFollower(pos);
                        deleteRequest(pos);
                        requests.remove(pos);
                        RequestList.this.notifyDataSetChanged();
                        Toast.makeText(getContext(), "You have a new follower!", Toast.LENGTH_SHORT).show();
                    }
                });

                convertView.setTag(viewHoldler);
            }
            else{
                viewHoldler = (ViewHoldler) convertView.getTag();
            }
            return  convertView;
        }
        catch (Exception e ){
            return null;
        }
    }
    private static class ViewHoldler {
        TextView notiText;
        Button declineButton;
        Button acceptButton;
    }
    public void deleteRequest(int i ){
        Map userData;
        ArrayList<String> userIDs;
        Main activity = (Main) context;
        userData = activity.getUserData();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final DocumentReference findUserRef1 = db.collection("Doers").document(userData.get("username").toString());
        userIDs = (ArrayList<String>) userData.get("incomingrequest");
        String otherUserId = userIDs.get(i);
        userIDs.remove(i);
        userData.put("incomingrequest",userIDs);
        findUserRef1.update("incomingrequest", userIDs);
        final DocumentReference findUserRef2 = db.collection("Doers").document(otherUserId);
        findUserRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map userData = document.getData();
                        ArrayList<String> sentRequests = (ArrayList<String>) userData.get("requested");
                        sentRequests.remove(i);
                        findUserRef2.update("requested", sentRequests);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



    }

    public void addFollower(int i ){
        Map userData;
        ArrayList<String> userIDs;
        Main activity = (Main) context;
        userData = activity.getUserData();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final DocumentReference findUserRef1 = db.collection("Doers").document(userData.get("username").toString());
        userIDs = (ArrayList<String>) userData.get("followers");
        String newFollower = ((ArrayList<String>) userData.get("incomingrequest")).get(i);
        userIDs.add(newFollower);
        userData.put("followers",userIDs);
        findUserRef1.update("followers", userIDs);


    }



}
