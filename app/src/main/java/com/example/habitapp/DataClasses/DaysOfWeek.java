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
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Leah      Oct-31-2021   Now implements serializable, added new Map constructor
 *   1.2       Mathew    Oct-31-2021   Added Javadocs
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class DaysOfWeek implements Serializable {

    // each attribute records if that day of the week has been selected by the user
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;

    /**
     * on creation with no parameters all of the days of the week are set to be not selected
     */
    public DaysOfWeek(){
        setAll(false);
    }

    /**
     * Uses a map obtained from firebase to create a DaysOfWeek object that properly represents
     * which days should be set to true or false
     * @param daysMap the map off which to base which days are set to true or false
     */
    public DaysOfWeek(Map<String, Boolean> daysMap){
        this.sunday = daysMap.get("sunday");
        this.monday = daysMap.get("monday");
        this.tuesday = daysMap.get("tuesday");
        this.wednesday = daysMap.get("wednesday");
        this.thursday = daysMap.get("thursday");
        this.friday = daysMap.get("friday");
        this.saturday = daysMap.get("saturday");
    }

    /**
     * Creates a DaysOfWeek object by sepcifying each day as true or false individually
     * goes from to sunday -> monday ... -> saturday
     * @param sunday is sunday selected
     * @param monday is monday selected
     * @param tuesday is tuesday selected
     * @param wednesday is wednesday selected
     * @param thursday is thursday selected
     * @param friday is friday selected
     * @param saturday is saturday selected
     */
    public DaysOfWeek(Boolean sunday, Boolean monday, Boolean tuesday, Boolean wednesday,
                      Boolean thursday, Boolean friday, Boolean saturday) {
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
    }

    // sets all of the days of the week to the given value (t/f)

    /**
     * set all of the days of the week to the given value
     * @param setTo the value to set all of the days of the week to
     */
    public void setAll(Boolean setTo){
        setSunday(setTo);
        setMonday(setTo);
        setTuesday(setTo);
        setWednesday(setTo);
        setThursday(setTo);
        setFriday(setTo);
        setSaturday(setTo);
    }

    /**
     * returns an arrayList of each value, position 0 = sunday, pos 1 = monday, etc. etc.
     * @return an arrayList of each value, position 0 = sunday, pos 1 = monday, etc. etc.
     */
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

    /**
     * Returns a boolean telling us whether or not all of the days in the week are false
     * comes in handy for making sure users didn't leave the field blank when adding a new habit/editing
     * @return a boolean telling us whether or not all of the days in the week are false
     */
    public boolean areAllFalse() {
        ArrayList<Boolean> weeklyList = this.getAll() ;
        Boolean all_false = true;
        for (int i = 0; i < weeklyList.size(); i++)  {
            if (weeklyList.get(i) == true) {
                all_false = false;
            }
        }

        return all_false;
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
