/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: MapSelector
 *
 * Description: Allows the user to scan google maps to select a latitude and longitude value to
 * represent the location of a habit event.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Nov-28-2021   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Map;

public class MapSelector extends AppCompatActivity implements OnMapReadyCallback {

    private Double selectedLatitude = null;
    private Double selectedLongitude = null;
    private Double givenLatitude = 0.0;
    private Double givenLongitude = 0.0;
    private Intent recievedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_screen);

        // get the default latitude and longitude from the previous activity
        recievedIntent = getIntent();
        givenLatitude = recievedIntent.getDoubleExtra("latitude", 0.0);
        givenLongitude = recievedIntent.getDoubleExtra("longitude", 0.0);


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button button = findViewById(R.id.actiivty_maps_close_button);
        button.setOnClickListener(this::buttonPressed);
    }

    private void buttonPressed(View view) {


        Intent intent = new Intent(this, EditHabitEvent.class);
        intent.putExtra("selectedLatitude", selectedLatitude);
        intent.putExtra("selectedLongitude", selectedLongitude);
        intent.putExtra("event", (Event) recievedIntent.getParcelableExtra("event"));
        intent.putExtra("habit", (Habit) recievedIntent.getSerializableExtra("habit"));
        intent.putExtra("firestoreId", (recievedIntent.getStringExtra("firestoreId")));
        System.out.println("Sending back to edit:");
        System.out.println(recievedIntent.getStringExtra("firestoreId"));
        intent.putExtra("userData", (Serializable) (Map) recievedIntent.getSerializableExtra("userData"));
        intent.putExtra("prevActivity", (String) recievedIntent.getSerializableExtra("activity"));

        startActivity(intent);
    }

    // basically an onCreate method for when the map is ready to be shown
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng defaultLoc = new LatLng(givenLatitude, givenLongitude);
        selectedLatitude = givenLatitude;
        selectedLongitude = givenLongitude;


        // move the camera to the lat and long, and zoom it to an acceptable level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 16));
        // whenever the camera moves the latitude and longitude should be changed
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                selectedLatitude = googleMap.getCameraPosition().target.latitude;
                selectedLongitude = googleMap.getCameraPosition().target.longitude;
            }
        });

    }
}
