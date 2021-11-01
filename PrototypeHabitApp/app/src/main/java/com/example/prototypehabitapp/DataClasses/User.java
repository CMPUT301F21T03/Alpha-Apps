/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: User
 *
 * Description: An object to describe one user of the application. This user is defined by their
 * email and password they provide. Such a user has a uniqueID, an option to make their account
 * public or private, and possibly other enhancements that will be noted in the changelog.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Oct-13-2021   Created
 *   1.1       Mathew    Oct-31-2021   Added Javadocs
 *   1.2       Mathew    Nov-01-2021   Added a stand in attribute for a profile picture
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.DataClasses;

public class User {

    // a unique string of characters to organize users
    private String uniqueID;
    // the user's name/alias
    private String name;
    // the user's email
    private String email;
    // the user's password
    private String password;
    // whether or not the user has set their account to private
    private boolean privateAccount;
    // a profile picture of the user
    //TODO make this an actual picture
    private String photoStandIn;

    /**
     * create a User. Their profile picture is the default picture upon creation.
     * @param uniqueID an ID that is unique to the user so it can be used for identification in our
     *                 database
     * @param name the name that the user gives themselves
     * @param email the email that the user links with their account
     * @param password the password that the user sets for their account
     */
    public User(String uniqueID, String name, String email, String password){
        setUniqueID(uniqueID);
        setName(name);
        setEmail(email);
        setPassword(password);
        // default the account to be public, NOT private
        setPrivateAccount(false);
        // set the account's profile picture to be a default
        setPhotoStandIn("default photo");
    }

    // =========================== GETTERS AND SETTERS ===========================
    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPrivateAccount() {
        return privateAccount;
    }

    public void setPrivateAccount(boolean privateAccount) {
        this.privateAccount = privateAccount;
    }

    public String getPhotoStandIn() {
        return photoStandIn;
    }

    public void setPhotoStandIn(String photoStandIn) {
        this.photoStandIn = photoStandIn;
    }
}
