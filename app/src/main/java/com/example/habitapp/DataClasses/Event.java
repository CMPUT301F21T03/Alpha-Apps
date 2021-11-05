/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Event
 *
 * Description: An object to represent a specific instance of a Habit object. Things such as
 * location or an optional comment can be noted by the user.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Jesse     Oct-27-2021   Added name and dateCompleted components and their
 *                                      getters/setters
 *   1.2       Jesse     Oct-31-2021   Altered Event to implement Serializable
 *   1.3       Mathew    Oct-31-2021   Added Javadocs
 *   1.4       Moe       Nov-04-2021   Firestore add, delete, edit for Event
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class Event implements Serializable {

    private final String TAG = "addEventTAG";

    private String name;

    private LocalDateTime dateCompleted;

    // a subjective comment about the habit event entered by the user (optional)
    private String comment;

    // TODO change the below attribute (and all other instances) to properly represent an image
    private Boolean hasPhotograph;

    // TODO change the below attribute (and all other instances) to properly represent a location
    private Boolean hasLocation;

    private String firestoreId;

    /**
     * creates an event with the specified values. If the value is null it means it was not given by the user
     * @param name the name of the habit event
     * @param dateCompleted the day that the event was completed
     * @param comment an optional comment about the event
     * @param hasPhotograph a placeholder for the photograph object that will be implemented later
     * @param hasLocation a placeholder for the location object that will be implemented later
     */
    public Event(String name, LocalDateTime dateCompleted, String comment, Boolean hasPhotograph, Boolean hasLocation){

        setName(name);
        setDateCompleted(dateCompleted);

        try{
            setComment(comment);
        }catch (IllegalArgumentException ex){
            System.out.println("comment too long, programs fails");
            // TODO make a function that terminates the program (or handle the error in another way)
        }
        // TODO set the photograph and location attributes

    }

    // =========================== GETTERS AND SETTERS ===========================
    //TODO create getters and setters for the location and photograph as needed


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) throws IllegalArgumentException{
        // if the comment field is not empty and the comment is over 20 characters, raise an error
        if (comment != null && comment.length() > 20){
            throw new IllegalArgumentException();
        }
        this.comment = comment;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public void addEventToFirestore(Map userData, Habit habit) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference eventsref = db.collection("Doers")
                .document((String)userData.get("username"))
                .collection("habits")
                .document(habit.getFirestoreId())
                .collection("events");

        eventsref.add(this)
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

    public void removeEventFromFirestore(Map userData, Habit habit) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference eventsref = db.collection("Doers")
                .document((String)userData.get("username"))
                .collection("habits")
                .document(habit.getFirestoreId())
                .collection("events");
        eventsref.document(getFirestoreId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "successfully deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed deletion");

                    }
                });
    }

    public void editEventInFirestore(Map userData, Habit habit) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference eventsref = db.collection("Doers")
                .document((String)userData.get("username"))
                .collection("habits")
                .document(habit.getFirestoreId())
                .collection("events");

        eventsref.document(getFirestoreId())
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