/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: GoBack
 *
 * Description: Sets a listener that responds when a user clicks on a "go back" button. When they
 * do so it will close the current frame and re-open the the most-recent frame.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */
package com.example.prototypehabitapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.prototypehabitapp.R;

public class GoBack extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.go_back, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // sets a listener for when the button is clicked
        Button goBackButton = view.findViewById(R.id.go_to_last_page);
        goBackButton.setOnClickListener(this::goBackButtonPressed);

    }
    private void goBackButtonPressed(View view) {
        // goes back to the previously opened activity
        getActivity().finish();
    }

}
