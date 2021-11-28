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
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.Habit;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditHabitEvent extends AppCompatActivity {
    private String TAG = "editHabitEventTAG";
    private Event event;
    private Habit habit;
    private String prevActivity;
    private ComponentName previousActivity;
    private EditText comments;
    private ArrayList<Event> events;
    private Map userData;
    private TextView habitName;
    private Bitmap photoBitmap;

    // camera related variables
    private static final int CAMERA_REQUEST = 1888;
    private ImageView cameraImage;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the display to be the main page
        setContentView(R.layout.edit_habit_event);

        //get details from bundle
        Intent sentIntent = getIntent();
        event = (Event) sentIntent.getParcelableExtra("event");
        event.setFirestoreId(sentIntent.getStringExtra("firestoreId"));
        habit = (Habit) sentIntent.getSerializableExtra("habit");
        userData = (Map) sentIntent.getSerializableExtra("userData");
        prevActivity = (String) sentIntent.getSerializableExtra("activity");
        previousActivity = getCallingActivity();

        habitName = findViewById(R.id.edithabitevent_habitname);
        habitName.setText("Log Habit: " + event.getName());

        comments = findViewById(R.id.edithabitevent_comment);
        comments.setText(event.getComment());

        cameraImage = this.findViewById(R.id.edithabitevent_camera_image);
        if (event.getPhotograph() == null){
            cameraImage.setVisibility(View.GONE);
        }

        setButtonListenters();
    }

    private void setButtonListenters(){
        Button completeButton = findViewById(R.id.edithabitevent_complete);
        completeButton.setOnClickListener(this::editHabitEventCompleteButtonPressed);

        cameraImage.setOnClickListener(this::editHabitEventCameraImagePressed);

        ImageView cameraButton = findViewById(R.id.edithabitevent_camera_button);
        cameraButton.setOnClickListener(this::editHabitEventCameraButtonPressed);

        ImageView deleteCameraButton = findViewById(R.id.edithabitevent_delete_image_button);
        deleteCameraButton.setOnClickListener(this::editHabitEventDeleteImageButtonPressed);
    }

    // if the camera image was selected, treat it as if the add button was pressed
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void editHabitEventCameraImagePressed(View view){
        editHabitEventCameraButtonPressed(view);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void editHabitEventCompleteButtonPressed(View view) {
        Button completeButton = findViewById(R.id.edithabitevent_complete);
        completeButton.setEnabled(false);

        String commentStr = (String) comments.getText().toString();
        event.setComment(commentStr);

        // prepare references
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://alpha-apps-41471.appspot.com");
        StorageReference storageRef = storage.getReference();

        // set image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        EditHabitEvent thisActivity = this;

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
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG,downloadUri.toString());
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
                    startActivity(intent);



                } else {
                    Log.d(TAG,"Failed to get download URL");
                }
            }
        });

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