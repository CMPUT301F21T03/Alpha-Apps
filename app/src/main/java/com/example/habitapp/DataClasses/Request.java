package com.example.habitapp.DataClasses;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Request implements Serializable {
    private String username;
    final private String SUFFIX = "sent you a friend request";

    public Request(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSUFFIX() {
        return SUFFIX;
    }
}
