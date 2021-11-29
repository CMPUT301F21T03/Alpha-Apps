/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: Notification
 *
 * Description: Sets a listener that responds when a user clicks on a "notifications" button.
 * When they do so it will open a dialog with any pertinent notification information
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Arthur   Nov-24-2021   Created
 *   1.2       Arthur   Nov-29-2021   Implemented functionality
 *
 * =|=======|=|======|===|====|========|===========|================================================
 */
package com.example.habitapp.DataClasses;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Request implements Serializable {
    private String username;
    final private String SUFFIX = "sent you a follow request";

    /**
     * a simple follow request object.
     * @param username userid of incoming request
     */

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
