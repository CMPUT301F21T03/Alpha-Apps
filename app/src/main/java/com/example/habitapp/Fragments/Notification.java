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
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Mathew    Oct-24-2021   Test commit (no changes to the code)
 *   1.2       Mathew    Nov-12-2021   Changed the look of the notifications button to be an image
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Fragments;



import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.RequestList;
import com.example.habitapp.DataClasses.Request;
import com.example.habitapp.DataClasses.User;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Notification extends Fragment {
    ImageView notifButton;
    Dialog notiDialog;
    Button acceptButton;
    Button declineButton;

    private ListView notificationsListView;
    private RequestList notificationAdapter;
    private ArrayList<Request> notificationDataList = new ArrayList<>();
    private Map userData;
    private ArrayList<String> userIDs;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.notifications, parent, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        notifButton = view.findViewById(R.id.notification_image);
        Main activity = (Main) getActivity();
        userData = activity.getUserData();
        userIDs = (ArrayList<String>) userData.get("incomingrequest");
        setRequests(userIDs);
        if (notificationDataList.size() > 0 ){
            notifButton.setBackgroundResource(R.drawable.notification_bell_after);
        }
        notifButton.setOnClickListener(this::notificationsButtonPressed);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setRequests(ArrayList<String> userIDs){
        for(int i = 0; i < userIDs.size(); i++){
            String idToAdd = userIDs.get(i);
            Request newRequest = new Request(idToAdd);
            notificationDataList.add(newRequest);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationsButtonPressed(View view) {
        acceptButton = view.findViewById(R.id.accept_button);
        declineButton  = view.findViewById(R.id.decline_button);
        Main activity = (Main) getActivity();

        notificationAdapter = new RequestList(activity, notificationDataList);

        notiDialog = new Dialog(activity);

        int[] location = new int[2];
        notifButton.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        int bx = notifButton.getLeft();
        int by = notifButton.getTop();

        Window window = notiDialog.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        notiDialog.setContentView(R.layout.noti_popup);

//
        notificationsListView = (ListView) notiDialog.findViewById(R.id.noti_list);
        notificationsListView.setAdapter(notificationAdapter);
//        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Button decline = view.findViewById(R.id.decline_button);
//
//            }
//        });
        notiDialog.show();

    }



}
