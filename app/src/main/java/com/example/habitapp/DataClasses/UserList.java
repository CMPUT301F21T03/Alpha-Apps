/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: UserList
 *
 * Description: A class to convert a list of users into a graphical listView display
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Mathew    Nov-01-2021    Created
 *   1.1       Mathew    Nov-16-2021    Added an imageview to store a user's pfp
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.example.habitapp.R;
import java.net.URL;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserList extends ArrayAdapter<User> {
    private String TAG = "userListTAG";
    private ArrayList<User> profiles;
    private Context context;

    public UserList(Context context, ArrayList<User> profiles) {
        super(context, 0, profiles);
        this.context = context;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.profile_list_entry, parent, false);
        }

        User profile = profiles.get(position);
        TextView username = view.findViewById(R.id.profilelistentry_username);
        username.setEnabled(false);
        TextView id = view.findViewById(R.id.profilelistentry_id);
        ImageView photoView = view.findViewById(R.id.profilelistentry_photo);

        username.setText(profile.getName());
        id.setText(profile.getUniqueID());
        if(profile.getProfilePicURL() != null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(profile.getProfilePicURL().toString());
                        Bitmap imageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        profile.setProfilePic(imageBitmap);
                        new Handler(Looper.getMainLooper()).post(new Runnable(){
                            @Override
                            public void run() {
                                photoView.setImageBitmap(profile.getProfilePic());
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });

            thread.start();
        }

        return view;
    }
    /**
     * Grabs the User object at a specified index in the ArrayList
     * @param pos index of Habit object desired in ArrayList
     * @return Habit object at specified index
     */
    public User getUserAtPosition(Integer pos){
        return profiles.get(pos);
    }


}
