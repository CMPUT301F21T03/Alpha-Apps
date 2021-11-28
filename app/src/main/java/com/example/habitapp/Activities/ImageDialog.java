/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: ImageDialog
 *
 * Description: displays a popup image when clicked on
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Jesse     Nov-27-2021   Created
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.R;

import java.net.URI;
import java.net.URL;

public class ImageDialog extends AppCompatActivity {
    private static final String TAG = "imageDialogTag";
    private ImageView dialog;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);

        Intent sentIntent = getIntent();
        event = (Event) sentIntent.getParcelableExtra("event");

        dialog = (ImageView) findViewById(R.id.image_dialog_imageview);

        Log.d(TAG,"URL: " + event.getPhotograph());
        // make a thread to decode the image URL and convert to bitmap to display
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(event.getPhotograph());
                    Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            dialog.setImageBitmap(imageBitmap);
                        }
                    });
                    Log.d(TAG, "Successfully set image");
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

        thread.start();


        dialog.setClickable(true);

        //finish activity when clicked on
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
