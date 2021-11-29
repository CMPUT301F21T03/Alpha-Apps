/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: ContextGetter
 *
 * Description: Used to be able to get the context in classes that are not an activity. This is
 * esepcially useful to use the getResources() method in those classes which would otherwise be
 * unavailable.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Nov-16-2021   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.app.Application;
import android.content.Context;

public class ContextGetter extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    /**
     * Get the context that the application is currently in. This can usually be done by casting
     * 'this' to (Context), but in the cases where that is not possible, this function can be
     * used as an alternative.
     * @return context, a value that represents the context the application is currently in
     */
    public static Context getContext(){
        return context;
    }
}
