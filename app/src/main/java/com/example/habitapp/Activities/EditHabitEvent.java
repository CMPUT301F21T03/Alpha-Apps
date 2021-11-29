/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: EditHabitEvent
 *
 * Description: Handles the user interactions of the event edit page
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-21-2021   Created
 *   1.1       Moe       Oct-29-2021   Set up complete button
 *   1.2       Moe       Nov-01-2021   Added receiving event from intent and editing event's comment
 *   1.3       Moe&Jesse Nov-03-2021   Added passing event to intent when complete button is pressed
 *   1.4       Moe       Nov-04-2021   Changed the editHabitEventCompleteButtonPressed function
 *                                      depending on the activity that is passed from
 *   1.5       Moe       Nov-04-2021   Firestore edit for HabitEvent
 *   1.6       Mathew    Nov-16-2021   Implemented camera functionality, and made some aesthetic changes
 *   1.7       Leah      Nov-23-2021   Implemented basic storage of images to Firebase Storage
 *   1.8       Jesse     Nov-27-2021   Implemented image onclick listener
 *   1.9       Mathew    Nov-28-2021   Implemented location functionality
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.LocationHandler;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditHabitEvent extends AppCompatActivity {
    private String TAG = "editHabitEventTAG";
    private Context context = (Context) this;
    private Event event;
    private Habit habit;
    private String prevActivity;
    private ComponentName previousActivity;
    private EditText comments;
    private ArrayList<Event> events;
    private Map userData;
    private Double selectedLatitude;
    private Double selectedLongitude;
    private EditText habitName;
    private Bitmap photoBitmap;

    private String extraFirestoreID;


    // camera related variables
    private static final int CAMERA_REQUEST = 1888;
    private ImageView cameraImage;
    private ImageView deleteImageButton;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.edit_habit_event);

        //get details from bundle
        Intent sentIntent = getIntent();
        event = (Event) sentIntent.getParcelableExtra("event");
        event.setFirestoreId(sentIntent.getStringExtra("firestoreId"));
        extraFirestoreID = sentIntent.getStringExtra("firestoreId");

        System.out.println("Starting EditHabitEvent, event id is:");
        System.out.println(extraFirestoreID);


        habit = (Habit) sentIntent.getSerializableExtra("habit");
        selectedLatitude = sentIntent.getDoubleExtra("selectedLatitude", 0.0);
        selectedLongitude = sentIntent.getDoubleExtra("selectedLongitude", 0.0);

        userData = (Map) sentIntent.getSerializableExtra("userData");
        prevActivity = (String) sentIntent.getSerializableExtra("activity");
        previousActivity = getCallingActivity();

        habitName = findViewById(R.id.edithabitevent_habitname);
        habitName.setEnabled(false);
        habitName.setText(event.getName());

        comments = findViewById(R.id.edithabitevent_comment);
        comments.setText(event.getComment());

        cameraImage = this.findViewById(R.id.edithabitevent_camera_image);
        deleteImageButton = this.findViewById(R.id.edithabitevent_delete_image_button);
      
        populateLocationInformation();
      
        if (event.getPhotograph() == null) {
            cameraImage.setVisibility(View.GONE);
            deleteImageButton.setVisibility(View.GONE);

        }
        // run a thread to set the photograph
        if(event.getPhotograph() != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(event.getPhotograph());
                        Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                cameraImage.setImageBitmap(imageBitmap);
                            }
                        });
                        Log.d(TAG, "Successfully set image");
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });

            thread.start();
        }
        setButtonListenters();
    }

    private void populateLocationInformation(){
        TextView latlongView = findViewById(R.id.edithabitevent_lat_long);
        EditText locName = findViewById(R.id.edithabitevent_location_name);
        // if the user has not yet selected a location
        if (selectedLatitude == 0.0 && selectedLongitude == 0.0){
            // dont show the location textview or edittext
            latlongView.setVisibility(View.GONE);
            locName.setVisibility(View.GONE);
        }else{
            // populate the fields with their values
            latlongView.setVisibility(View.VISIBLE);
            locName.setVisibility(View.VISIBLE);
            latlongView.setText("Latitude: " + Math.round(selectedLatitude*100.0)/100.0 + "\nLongitude: " + Math.round(selectedLongitude*100)/100.0);
            locName.setText(event.getLocationName());
        }
    }

    private void setButtonListenters(){
        Button completeButton = findViewById(R.id.edithabitevent_complete);
        completeButton.setOnClickListener(this::editHabitEventCompleteButtonPressed);

        cameraImage.setOnClickListener(this::editHabitEventCameraImagePressed);

        ImageView cameraButton = findViewById(R.id.edithabitevent_camera_button);
        cameraButton.setOnClickListener(this::editHabitEventCameraButtonPressed);

        ImageView deleteCameraButton = findViewById(R.id.edithabitevent_delete_image_button);
        deleteCameraButton.setOnClickListener(this::editHabitEventDeleteImageButtonPressed);

        ImageView locationButton = findViewById(R.id.edithabitevent_location_button);
        locationButton.setOnClickListener(this::editHabitEventLocationButtonPressed);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editHabitEventLocationButtonPressed(View view) {
        // save the stuff a user might have already put
        String commentStr = (String) comments.getText().toString();
        event.setComment(commentStr);
        EditText locationEditText = findViewById(R.id.edithabitevent_location_name);
        String locationName = locationEditText.getText().toString();
        event.setLocationName(locationName);
        event.setLatitude(selectedLatitude);
        event.setLongitude(selectedLongitude);

        // prepare references
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://alpha-apps-41471.appspot.com");
        StorageReference storageRef = storage.getReference();

        EditHabitEvent thisActivity = this;

        // set image
        if (photoBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // format bytes to be stored in storage
            StorageReference docuRef = storageRef.child("images/"+imageData.hashCode());
            UploadTask uploadTask = docuRef.putBytes(imageData);
            uploadTask.addOnFailureListener(exception -> Log.d(TAG,"Failed upload"))
                    .addOnSuccessListener(taskSnapshot -> Log.d(TAG,"Successful upload"));
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return docuRef.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d(TAG,downloadUri.toString());
                        // set the event photo URL
                        event.setPhotograph(downloadUri.toString());
                        event.editEventInFirestore(userData, habit);
                        // close this Activity

                        startMap();

                    } else {
                        Log.d(TAG,"Failed to get download URL");
                    }
                }
            });
        } else {
            startMap();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startMap() {
        // don't do photo stuff
        System.out.println("Trying to access the right ID here:");
        System.out.println(extraFirestoreID);
        event.setFirestoreId(extraFirestoreID);
        event.editEventInFirestore(userData, habit);

        // start the map activity with the user's current location as a starting point
        LocationHandler locationHandler = new LocationHandler(this);
        if (event.getLatitude() == 0.0) {
            event.setLatitude(locationHandler.getLatitude());
        }

        if (event.getLongitude() == 0.0) {
            event.setLongitude(locationHandler.getLongitude());
        }

        Intent intent = new Intent(this, MapSelector.class);

        intent.putExtra("event", event);
        intent.putExtra("habit", habit);
        intent.putExtra("firestoreId", extraFirestoreID);
        intent.putExtra("latitude", event.getLatitude());
        intent.putExtra("longitude", event.getLongitude());
        System.out.println("Sending to map ID:");
        System.out.println(extraFirestoreID);
        intent.putExtra("userData", (Serializable) userData);
        intent.putExtra("prevActivity", prevActivity);
        startActivity(intent);


        TextView latLongTextView = findViewById(R.id.edithabitevent_lat_long);
        latLongTextView.setText("Latitude: " + selectedLatitude + "\nLongitude: " + selectedLongitude);
    }

    // if the camera image was selected, treat it as if the add button was pressed
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void editHabitEventCameraImagePressed(View view){
        Intent intent = new Intent(EditHabitEvent.this, ImageDialog.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void editHabitEventCameraButtonPressed(View view){
        // check to make sure that the user allows the use of their camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            // if they don't allow the camera, ask them if they want to
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            // if the user allows the camera, request the phone for access to the camera
            // and start to take a picture
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    public void editHabitEventDeleteImageButtonPressed(View view){
        event.setPhotograph(null);
        cameraImage.setVisibility(View.GONE);
        deleteImageButton.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editHabitEventCompleteButtonPressed(View view) {
        EditText locationEditText = findViewById(R.id.edithabitevent_location_name);
        String locationName = locationEditText.getText().toString();
        if (TextUtils.isEmpty(locationName)){
            Toast.makeText(this, "Please enter a location nickname.", Toast.LENGTH_SHORT).show();
        } else {


            Button completeButton = findViewById(R.id.edithabitevent_complete);
            completeButton.setEnabled(false);

            String commentStr = (String) comments.getText().toString();
            event.setComment(commentStr);

            event.setLocationName(locationName);
            event.setLatitude(selectedLatitude);
            event.setLongitude(selectedLongitude);

            // prepare references
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://alpha-apps-41471.appspot.com");
            StorageReference storageRef = storage.getReference();

            EditHabitEvent thisActivity = this;

            // set image
            if (photoBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // format bytes to be stored in storage
                StorageReference docuRef = storageRef.child("images/" + imageData.hashCode());
                UploadTask uploadTask = docuRef.putBytes(imageData);
                uploadTask.addOnFailureListener(exception -> Log.d(TAG, "Failed upload"))
                        .addOnSuccessListener(taskSnapshot -> Log.d(TAG, "Successful upload"));
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return docuRef.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.d(TAG, downloadUri.toString());
                            // set the event photo URL
                            event.setPhotograph(downloadUri.toString());
                            event.editEventInFirestore(userData, habit);
                            // close this Activity
                            setResult(RESULT_OK);
                            completeButton.setEnabled(true);
                            finish();
                            Intent intent;

                            intent = new Intent(thisActivity, HabitEventDetails.class);
                            intent.putExtra("habit", habit);
                            intent.putExtra("event", event);
                            intent.putExtra("userData", (Serializable) userData);
                            intent.putExtra("firestoreId", event.getFirestoreId());

                            Intent test_intent = new Intent(thisActivity, Main.class);
                            test_intent.putExtra("userData", (Serializable) userData);
                            startActivity(test_intent);

                        } else {
                            Log.d(TAG, "Failed to get download URL");
                        }
                    }
                });
            } else {
                // don't do photo stuff
                System.out.println("Trying to access the right ID here:");
                System.out.println(extraFirestoreID);
                event.setFirestoreId(extraFirestoreID);
                event.editEventInFirestore(userData, habit);
                // close this Activity
                setResult(RESULT_OK);
                completeButton.setEnabled(true);
                finish();
                Intent intent;

                intent = new Intent(thisActivity, HabitEventDetails.class);
                intent.putExtra("habit", habit);
                intent.putExtra("event", event);
                intent.putExtra("userData", (Serializable) userData);
                intent.putExtra("firestoreId", event.getFirestoreId());
                Intent test_intent = new Intent(thisActivity, Main.class);
                test_intent.putExtra("userData", (Serializable) userData);
                startActivity(test_intent);
            }

        }



    }

    // this code executes after the user is asked about if they want to allow the app to use their camera
    // it will start the camera intent if the user allows access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // after the user takes an image, it stores the image as a bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // set the image view
            cameraImage.setVisibility(View.VISIBLE);
            cameraImage.setImageBitmap(photo);
            photoBitmap = photo;



        }
    }
}