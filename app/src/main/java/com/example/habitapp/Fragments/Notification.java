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
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.RequestList;
import com.example.habitapp.DataClasses.Request;
import com.example.habitapp.R;

import java.util.ArrayList;

public class Notification extends Fragment {
    ImageView notifButton;
    Dialog notiDialog;

    private ListView notificationsListView;
    private RequestList notificationAdapter;
    private ArrayList<Request> notificationDataList = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.notifications, parent, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

//        Request test1 = new Request("Arthur");
//        notificationDataList.add(test1);

        // sets a listener for when the button is clicked
        //Main activity = (Main) getActivity();
        notifButton = view.findViewById(R.id.notification_image);
//        notificationAdapter = new NotificationList(view.getContext(), notificationDataList);

        notifButton.setOnClickListener(this::notificationsButtonPressed);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationsButtonPressed(View view) {

        Main activity = (Main) getActivity();

        notificationAdapter = new RequestList(activity, notificationDataList);

        notiDialog = new Dialog(activity);


        //TODO show all pertinent information in a dialog
        System.out.println("notification button pressed");
        Log.i("notification", "button pressed");
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


        //setNotificationListAdapter(view);
        Request test1 = new Request("Arthur");
        notificationDataList.add(test1);
//
        notificationsListView = (ListView) notiDialog.findViewById(R.id.noti_list);
        notificationsListView.setAdapter(notificationAdapter);
//        notificationsListView.setEmptyView(view.findViewById(R.id.allhabits_hidden_textview_1));
        // set the new location [you will need to play with this]

//        wlp.x = bx;
//        wlp.y = (by - notifButton.getHeight());
//
//        // add to your window
//        window.setAttributes(wlp);

//        window.getAttributes().x = 1;
//        window.getAttributes().y = 1;

        Log.d("X>>>>", ": this is X " +x);
        Log.d("Y>>>>", ": this is y " +y);

        notiDialog.show();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationListAdapter(View view) {
        notificationsListView = (ListView) view.findViewById(R.id.noti_list);
        notificationAdapter = new RequestList(view.getContext(), notificationDataList);
        notificationsListView.setAdapter(notificationAdapter);
//        getHabitDataList(habitAdapter);
        notificationsListView.setEmptyView(view.findViewById(R.id.allhabits_hidden_textview_1));
    }

}
