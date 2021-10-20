/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: DaysOfWeek
 *
 * Description: A class that represents what days of the week have been selected by the user
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp;

import java.util.ArrayList;

public class DaysOfWeek {

    // each attribute records if that day of the week has been selected by the user
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;

    // on creation all of the days of the week are set to be not selected
    public DaysOfWeek(){
        setAll(false);
    }

    // sets all of the days of the week to the given value (t/f)
    public void setAll(Boolean setTo){
        setSunday(setTo);
        setMonday(setTo);
        setTuesday(setTo);
        setWednesday(setTo);
        setThursday(setTo);
        setFriday(setTo);
        setSaturday(setTo);
    }
    
    // returns an arrayList of each value, position 0 = sunday, pos 1 = monday, etc. etc.
    public ArrayList<Boolean> getAll(){
        ArrayList<Boolean> weeklyList = new ArrayList<>();

        weeklyList.add(getSunday());
        weeklyList.add(getMonday());
        weeklyList.add(getTuesday());
        weeklyList.add(getWednesday());
        weeklyList.add(getThursday());
        weeklyList.add(getFriday());
        weeklyList.add(getSaturday());

        return weeklyList;
    }

    // =========================== GETTERS AND SETTERS ===========================


    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }
}
