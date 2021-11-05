/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Habit
 *
 * Description: An object to represent a Habit as designated by the user. Data that describes a
 * habit would include a title, reason, date the habit is planned to start, what days of the week
 * it should occur, etc. etc.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Mathew    Oct-31-2021   Added Javadocs
 *   1.2       Leah      Nov-02-2021   Added FirestoreId to allow for edits and deletes
 *   1.3       Eric      Nov-03-2021   Firestore add, edit, delete now part of Habit class. Changes reflected here.
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class Habit implements Serializable {

    private final String TAG = "addhabitTAG";

    // User interactive elements
    //=================================================================================
    // records the title of the habit
    private String title;

    // records the reason the habit was started
    private String reason;

    // records the date the habit was started
    private LocalDateTime dateStarted;

    // records what days of the week the habit should occur
    private DaysOfWeek weekOccurence;

    // records all of the user created events of this type of habit
    private ArrayList<Event> eventList;

    // records how well the user is keeping up with this specific habit
    private Double progress;

    // records the Firestore document ID in case it must be fetched for edits/deletes
    private String firestoreId;
    //==================================================================================

    // program data objects
    //==================================================================================
    // records the date that the habit was last checked until for habit completion
    private LocalDateTime dateEventChecked;
    //==================================================================================



    /**
     * create a Habit object with the specified values
     * @param title a title to represent the kind of habit
     * @param reason a reason about why the user wants to complete the habit
     * @param dateStarted the date the habit is scheduled to start
     * @param weekOccurence the weekly frequency that the user specifies the habit should take place
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Habit(String title, String reason, LocalDateTime dateStarted, DaysOfWeek weekOccurence){
        // set the eventList to be empty
        setEventList(new ArrayList<Event>());
        // set the last day that was checked for habit completion to be one day ago
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        setDateEventChecked(yesterday);
        // set the current progress to 0
        setProgress(0.0);

        // set the parameters to their correlated values
        setTitle(title);
        setReason(reason);
        setDateStarted(dateStarted);
        setWeekOccurence(weekOccurence);
    }


    // =========================== GETTERS AND SETTERS ===========================
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws IllegalArgumentException{
        // if the title field is not empty and the title is over 20 characters, raise an error
        if (title != null && title.length() > 20){
            throw new IllegalArgumentException();
        }
        this.title = title;
    }

    public String getReason(){
        return reason;
    }

    public void setReason(String reason) throws IllegalArgumentException{
        // if the reason field is not empty and the reason is over 30 characters, raise an error
        if (reason != null && reason.length() > 30){
            throw new IllegalArgumentException();
        }
        this.reason = reason;
    }

    public LocalDateTime getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(LocalDateTime dateStarted) {
        this.dateStarted = dateStarted;
    }

    public DaysOfWeek getWeekOccurence() {
        return weekOccurence;
    }

    public void setWeekOccurence(DaysOfWeek weekOccurence) {
        this.weekOccurence = weekOccurence;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public LocalDateTime getDateEventChecked() {
        return dateEventChecked;
    }

    public void setDateEventChecked(LocalDateTime dateEventChecked) {
        this.dateEventChecked = dateEventChecked;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public void addHabitToFirestore(Map userData) {
        // add new Habit to Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
        //Toast.makeText(getContext(), "I came here", Toast.LENGTH_SHORT).show();
        habitsref.add(this)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"success");
                        setFirestoreId(documentReference.getId());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed: "+ e);
            }
        });
    }

    public void removeHabitFromFirestore(Map userData) {
        // remove current Habit from Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
        //System.out.println("Firestore ID is: " + getFirestoreId());
        habitsref.document(getFirestoreId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"successfully deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"failed delete");
                    }
                });
    }

    public void editHabitInFirestore(Map userData) {
        // add new Habit to Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
        //Toast.makeText(getContext(), "I came here", Toast.LENGTH_SHORT).show();
        habitsref.document(getFirestoreId())
                .set(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"failed update");
                    }
                });


    }
}