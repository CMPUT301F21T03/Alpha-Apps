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
 *   1.0       Mathew    Oct-13-2020   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.DataClasses;

public class Event {

    // a subjective comment about the habit event entered by the user (optional)
    private String comment;

    // states if this event has been completed by the user
    private Boolean isCompleted;

    // TODO change the below attribute (and all other instances) to properly represent an image
    private Boolean hasPhotograph;

    // TODO change the below attribute (and all other instances) to properly represent a location
    private Boolean hasLocation;

    // creates an event with the specified values. If the value is null it means it was not given by the user
    public Event(String comment, Boolean isCompleted, Boolean hasPhotograph, Boolean hasLocation){
        try{
            setComment(comment);
        }catch (IllegalArgumentException ex){
            System.out.println("comment too long, programs fails");
            // TODO make a function that terminates the program (or handle the error in another way)
        }
        setCompleted(isCompleted);
        // TODO set the photograph and location attributes
        
    }
    
    // =========================== GETTERS AND SETTERS ===========================
    //TODO create getters and setters for the location and photograph as needed

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

    public Boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
