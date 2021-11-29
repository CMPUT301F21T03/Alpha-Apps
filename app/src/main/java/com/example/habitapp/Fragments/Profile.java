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
 *   1.3       Leah      Nov-05-2021   Added profile info from user. Sends data back to Firestore if edited.
 *   1.4       Mathew    Nov-16-2021   Added profile picture selection from the users gallery, made aesthetic changes
 *                                     updated some of the frame logic
 *   1.5       Mathew    Nov-24-2021   Added a pending button to see all the pending follow requests of a user
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.FollowingFollowers;
import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.User;
import com.example.habitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.FileNotFoundException;


public class Profile extends Fragment {
    public Profile() {
        super(R.layout.profile);
    }

    private static final String TAG = "profileTAG";
    private static final int GALLERY_PICK = 1234;

    private boolean allowedToEdit = false;
    private User profile;
    private View usernameView;
    private EditText usernameEditText;
    private ImageView profilePicView;
    private Map userData;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // set the editability of the profile editText to false

        usernameView = view.findViewById(R.id.profilelistentry_username);
        usernameView.setEnabled(false);
        view.findViewById(R.id.profile_save_button).setVisibility(View.GONE);

        // get the current user's data
        getUserData();

        profilePicView = view.findViewById(R.id.profilelistentry_photo);
        // set the current user's data
        setUserData(view);

        //init button listeners
        setButtonListeners(view);

    }

    private void setUserData(View view) {
        usernameEditText = view.findViewById(R.id.profilelistentry_username);
        TextView idView = view.findViewById(R.id.profilelistentry_id);
        profilePicView.setImageBitmap(profile.getProfilePic());
        usernameEditText.setText(profile.getName());
        idView.setText("@"+profile.getUniqueID());
    }

    public void getUserData(){
        // fetch user data from activity in Main
        Main activity = (Main) getActivity();
        userData = activity.getUserData();
        // convert to a User
        // TODO: initialize profilePic, follower, and following at default
        profile = new User(userData.get("username").toString(),
                           userData.get("name").toString(),
                           userData.get("username").toString(),
                           userData.get("password").toString(),
                           userData.get("profilePic").toString());
        // check for following/followers
        if(userData.containsKey("following")){
            profile.setFollowingList((ArrayList<String>) userData.get("following"));
        }
        // check for profile picture
        if(userData.containsKey("profilePic")){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(userData.get("profilePic").toString());
                        Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        profile.setProfilePic(imageBitmap);
                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                profilePicView.setImageBitmap(profile.getProfilePic());
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
    }

    private void setButtonListeners(View view){

        // set a listener for if the pending button is pressed
        Button pendingButton = view.findViewById(R.id.profile_pending_button);
        pendingButton.setOnClickListener(this::profilePendingButtonPressed);

        // set a listener for if the edit button is pressed
        ImageButton editButton = view.findViewById(R.id.profile_edit);
        editButton.setOnClickListener(this::profileEditButtonPressed);

        // set a listener for if the save button is pressed
        ImageButton saveButton = view.findViewById(R.id.profile_save_button);
        saveButton.setOnClickListener(this::profileSaveButtonPressed);


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
        profilePicView.setSoundEffectsEnabled(false);
        profilePicView.setOnClickListener(this::profilePhotoPressed);
    }

    private void profilePendingButtonPressed(View view){
        Intent intent = new Intent(getContext(), FollowingFollowers.class);
        intent.putExtra("FOLLOWING?", "requested");
        startActivity(intent);
    }

    private void profilePhotoPressed(View view) {
        if (allowedToEdit) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_PICK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getContext(), this);
        }

        // after cropping the image, set the result to the profile picture bitmap
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // prepare references
            FirebaseFirestore db;
            db = FirebaseFirestore.getInstance();
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://alpha-apps-41471.appspot.com");
            StorageReference storageRef = storage.getReference();

            CropImage.ActivityResult imageData = CropImage.getActivityResult(data);

            // set profile image
            Uri imageUri = imageData.getUri();
            profilePicView.setImageURI(imageUri);

            // format bytes to be stored in storage
            StorageReference docuRef = storageRef.child("images/"+imageUri.getLastPathSegment());
            UploadTask uploadTask = docuRef.putFile(imageUri);
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
                        // upload to the user document in Firestore
                        Map userData = new HashMap<>();
                        userData.put("profilePic",downloadUri.toString());
                        db.collection("Doers").document(profile.getUniqueID())
                                .update(userData);


                    } else {
                        Log.d(TAG,"Failed to get download URL");
                    }
                }
            });
        }

    }

    private void profileLogOutButtonPressed(View view) {
        //TODO sign the user out AND dont allow them to get back to this page using the back button
        Intent intent = new Intent(getContext(), BootScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void profileFollowersButtonPressed(View view) {
        this.navigateFollowingFollowers("follower");
    }

    private void profileFollowingButtonPressed(View view) {
        this.navigateFollowingFollowers("following");

    }

    private void navigateFollowingFollowers(String follow){
        Intent intent = new Intent(getContext(), FollowingFollowers.class);
        intent.putExtra("FOLLOWING?", follow);
        intent.putExtra("userProfile", (Serializable) userData);
        startActivity(intent);
    }

    private void profileEditButtonPressed(View view) {
        setAllowedToEditColorScheme(false);
        allowedToEdit = true;
        usernameView.setEnabled(true);
        showEditButton(true);
    }

    private void profileSaveButtonPressed(View view){
        setAllowedToEditColorScheme(true);
        allowedToEdit = false;
        usernameView.setEnabled(false);
        showEditButton(false);

        // get the information on screen to send to firebase (also set the data objects attributes)
        EditText usernameEditText = (EditText) usernameView;
        String newUsername = usernameEditText.getText().toString();
        Bitmap imageBitMap = profilePicView.getDrawingCache();

        profile.setName(newUsername);
        profile.setProfilePic(imageBitMap);

        //TODO put all of the updated info above into firebase in place of the old data
        // Puts new username into Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("name", newUsername);
        db.collection("Doers").document(userData.get("username").toString())
                .set(data, SetOptions.merge());
    }

    @SuppressLint("ResourceAsColor")
    private void setAllowedToEditColorScheme(boolean allowedToEdit) {
        CardView card = getActivity().findViewById(R.id.profilelistentry_pfp_edit_background);
        if (allowedToEdit) {
            usernameEditText.setTextColor(R.color.black);
            card.setVisibility(View.INVISIBLE);
        }else{
            usernameEditText.setTextColor(R.color.purple);
            card.setVisibility(View.VISIBLE);
        }
    }

    private void showEditButton(boolean allowedToEdit){
        if (allowedToEdit){
            getActivity().findViewById(R.id.profile_save_button).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.profile_edit).setVisibility(View.GONE);
        }else{
            getActivity().findViewById(R.id.profile_save_button).setVisibility(View.GONE);
            getActivity().findViewById(R.id.profile_edit).setVisibility(View.VISIBLE);
        }
    }

    private static String getTAG(){
        return TAG;
    }


}
