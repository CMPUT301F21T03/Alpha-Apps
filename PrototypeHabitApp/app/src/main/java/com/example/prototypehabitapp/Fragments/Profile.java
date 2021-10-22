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
 *   1.0       Eric      Oct-21-2020   Created
 *   1.1       Mathew    Oct-21-2020   Added some navigation features to and from this page
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.Activities.EditPersonalProfile;
import com.example.prototypehabitapp.R;


public class Profile extends Fragment {
    public Profile() {
        super(R.layout.profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // set a listener for if the edit button is pressed
        Button editButton = view.findViewById(R.id.profile_edit);
        editButton.setOnClickListener(this::profileEditButtonPressed);


        // set a listener for if the following button is pressed
        Button followingButton = view.findViewById(R.id.profile_following);
        followingButton.setOnClickListener(this::profileFollowingButtonPressed);


        // set a listener for if the followers button is pressed
        Button followersButton = view.findViewById(R.id.profile_followers);
        followersButton.setOnClickListener(this::profileFollowersButtonPressed);
    }

    private void profileFollowersButtonPressed(View view) {
        //TODO create and navigate to the followers/following page with the proper data
    }

    private void profileFollowingButtonPressed(View view) {
        //TODO create and navigate to the followers/following page with the proper data

    }

    private void profileEditButtonPressed(View view) {
        //navigate to the edit profile page
        Intent intent = new Intent(getContext(), EditPersonalProfile.class);
        // TODO bundle up the item to be sent to the next frame
        startActivity(intent);

    }


}
