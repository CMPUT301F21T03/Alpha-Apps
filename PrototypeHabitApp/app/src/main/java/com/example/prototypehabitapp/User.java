package com.example.prototypehabitapp;

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

    // create a User
    public User(String uniqueID, String name, String email, String password){
        setUniqueID(uniqueID);
        setName(name);
        setEmail(email);
        setPassword(password);
        // default the account to be public, NOT private
        setPrivateAccount(false);
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
}
