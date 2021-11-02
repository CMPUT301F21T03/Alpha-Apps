/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: HabitEventDialog
 *
 * Description:
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Moe       Nov-01-2021   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.prototypehabitapp.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.prototypehabitapp.DataClasses.Event;
import com.example.prototypehabitapp.R;

public class HabitEventDialog extends Dialog implements android.view.View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private Button loghabitButton;
    private Button cancelButton;
    private Event event;

    public HabitEventDialog(Activity activity, Event event) {
        super(activity);
        this.activity = activity;
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_event_dialog);
        loghabitButton = (Button) findViewById(R.id.habiteventdialog_loghabit);
        cancelButton = (Button) findViewById(R.id.habiteventdialog_cancel);

        loghabitButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.habiteventdialog_loghabit:
                dismiss();
                Intent intent = new Intent(getContext(), EditHabitEvent.class);
                intent.putExtra("EVENT", event);
                getContext().startActivity(intent);
                break;
            case R.id.habiteventdialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
