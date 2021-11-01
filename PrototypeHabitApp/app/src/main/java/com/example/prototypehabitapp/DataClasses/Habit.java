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
 *   1.0       Mathew    Oct-13-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.DataClasses;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Habit implements Serializable {

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
    //==================================================================================

    // program data objects
    //==================================================================================
    // records the date that the habit was last checked until for habit completion
    private LocalDateTime dateEventChecked;
    //==================================================================================


    // create a Habit with the specified values
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

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(getTitle());
//        parcel.writeString(getReason());
//        parcel.writeValue(getDateStarted());
//        parcel.writeValue(getWeekOccurence());
//        parcel.writeValue(getEventList());
//        parcel.writeValue(getProgress());
//    }
}
