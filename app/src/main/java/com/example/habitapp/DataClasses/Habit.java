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
 *   1.4       Mathew    Nov-23-2021   Added logic to update progress bars
 *   1.4       Eric      Nov-24-2021   Stores index for use with reordering on All/Today Habit frames
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

@RequiresApi(api = Build.VERSION_CODES.O)
public class Habit implements Serializable {

    private final String TAG = "addhabitTAG";

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

    // records whether a habit is private with friends or not
    private boolean privacy;

    // records placement in lists for user display
    private Integer allHabitsIndex;
    private Integer todayHabitsIndex;

    // records the Firestore document ID in case it must be fetched for edits/deletes
    private String firestoreId;

    // records the date that the habit was last checked until for habit completion
    private LocalDateTime dateLastChecked;
    private LocalDateTime today = LocalDateTime.now();
    private LocalDateTime yesterday = today.minusDays(1);

    private Integer daysCompleted = 0;
    private Integer daysTotal = 0;


    /**
     * create a Habit object with the specified values
     * @param title a title to represent the kind of habit
     * @param reason a reason about why the user wants to complete the habit
     * @param dateStarted the date the habit is scheduled to start
     * @param weekOccurence the weekly frequency that the user specifies the habit should take place
     * @param privacy the privacy setting of the habit
     * @param allHabitsIndex the index of where the habit should go on the All Habits frame
     * @param todayHabitsIndex the index of where the habits should go on the Today Habits frame
     * @param dateLastChecked the last date this habit was checked for completion
     * @param daysCompleted the number of days this habit has been successfully completed
     * @param daysTotal the total number of days this habit should have been completed
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Habit(String title, String reason, LocalDateTime dateStarted, DaysOfWeek weekOccurence, boolean privacy, Integer allHabitsIndex, Integer todayHabitsIndex, LocalDateTime dateLastChecked, Integer daysCompleted, Integer daysTotal){
        // set the eventList to be empty
        setEventList(new ArrayList<>());
        // set the last day that was checked for habit completion to be one day ago
        setDateLastChecked(yesterday);
        // set the current progress to 0
        setProgress(0.0);

        // set the parameters to their correlated values
        setTitle(title);
        setReason(reason);
        setDateStarted(dateStarted);
        setWeekOccurence(weekOccurence);
        setPrivacy(privacy);
        setAllHabitsIndex(allHabitsIndex);
        setTodayHabitsIndex(todayHabitsIndex);
        setDateLastChecked(dateLastChecked);
        setDaysCompleted(daysCompleted);
        setDaysTotal(daysTotal);
    }

    /**
     * Update the progress value to be the most up-to-date value
     */
    public void updateProgress() {

        // if the dateThe Event's were last checked until is not the most recent
        while (dateLastChecked.toLocalDate().isBefore(yesterday.toLocalDate())){
            //for each day of the week
            switch(dateLastChecked.getDayOfWeek()){
                case SUNDAY:
                    // if the habit was supposed to occur on that day
                    if (getWeekOccurence().getSunday()){
                        daysTotal++;
                    }
                    break;
                case MONDAY:
                    if (getWeekOccurence().getMonday()){
                        daysTotal++;
                    }
                    break;
                case TUESDAY:
                    if (getWeekOccurence().getTuesday()){
                        daysTotal++;
                    }
                    break;
                case WEDNESDAY:
                    if (getWeekOccurence().getWednesday()){
                        daysTotal++;
                    }
                    break;
                case THURSDAY:
                    if (getWeekOccurence().getThursday()){
                        daysTotal++;
                    }
                    break;
                case FRIDAY:
                    if (getWeekOccurence().getFriday()){
                        daysTotal++;
                    }
                    break;
                case SATURDAY:
                    if (getWeekOccurence().getSaturday()){
                        daysTotal++;
                    }
                    break;
            }
            // check the next day on the next loop
            dateLastChecked = dateLastChecked.plusDays(1);
        }
        if (daysTotal == 0){
            setProgress(0.0);
        }else{
            setProgress(((double)daysCompleted/((double)daysTotal))*100);
        }
    }

    // =========================== GETTERS AND SETTERS ===========================
    public LocalDateTime getDateLastChecked() {
        return dateLastChecked;
    }

    public void setDateLastChecked(LocalDateTime dateLastChecked) {
        this.dateLastChecked = dateLastChecked;
    }

    public Integer getDaysCompleted() {
        return daysCompleted;
    }

    public void setDaysCompleted(Integer daysCompleted) {
        this.daysCompleted = daysCompleted;
    }

    public Integer getDaysTotal() {
        return daysTotal;
    }

    public void setDaysTotal(Integer daysTotal) {
        this.daysTotal = daysTotal;
    }

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

    // get Progress will also call the update function to make sure that the progress will always
    // be the most up-to-date value
    public Double getProgress() {
        updateProgress();
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

    public boolean getPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public Integer getAllHabitsIndex() {
        return allHabitsIndex;
    }

    public void setAllHabitsIndex(Integer allHabitsIndex) {
        this.allHabitsIndex = allHabitsIndex;
    }

    public Integer getTodayHabitsIndex() {
        return todayHabitsIndex;
    }

    public void setTodayHabitsIndex(Integer todayHabitsIndex) {
        this.todayHabitsIndex = todayHabitsIndex;
    }

    /**
     * add the habit objects data to firestore to allow for data persistence
     * @param userData a collection of data of which a username attribute is taken from to build
     *                 the query to create a habit object
     */
    public void addHabitToFirestore(Map userData) {
        // add new Habit to Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
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

    /**
     * remove the habit objects data from firestore to allow for data persistence
     * @param userData a collection of data of which a username attribute is taken from to build
     *                 the query to create a habit object
     */
    public void removeHabitFromFirestore(Map userData) {
        // remove current Habit from Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
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

    /**
     * update the habit objects data to firestore to allow for data persistence
     * @param userData a collection of data of which a username attribute is taken from to build
     *                 the query to create a habit object
     */
    public void editHabitInFirestore(Map userData) {
        // add new Habit to Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference habitsref = db.collection("Doers").document((String)userData.get("username")).collection("habits");
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
