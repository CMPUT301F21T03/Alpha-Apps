/**
 * Copyright 2021 - 2021 CMPUT301F21T03 (Alpha-Apps). All rights reserved. This document nor any
 * part of it may be reproduced, stored in a retrieval system or transmitted in any for or by any
 * means without prior permission of the members of CMPUT301F21T03 or by the professor and any
 * authorized TAs of the CMPUT301 class at the University of Alberta, fall term 2021.
 *
 * Class: EventList
 *
 * Description: A class to convert a listview into a list of events.
 *
 * Changelog:
 * =|Version|=|User(s)|==|Date|========|Description|================================================
 *   1.0       Jesse     Oct-31-2021    Created
 *   1.1       Mathew    Oct-31-2021    Fix imports
 *   1.2     Jesse/Moe     Nov-03-2021    Add layout inflater
 *   1.3       Moe       Nov-04         Added addSnapshotQuery to display HabitEvent from Firestore
 *   1.4       Mathew    Nov-16-2021    Added an imageView to show the user selected image for an event
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.content.Context;
import android.os.Build;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventList extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EventList(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.events_listview_content, parent,false);
        }

        Event event = events.get(position);
        TextView name = view.findViewById(R.id.eventslistviewcontent_name_text);
        TextView comment = view.findViewById(R.id.eventslistviewcontent_comment_text);
        TextView date = view.findViewById(R.id.eventslistviewcontent_date_text);
        ImageView image = view.findViewById(R.id.eventslistviewcontent_image);

        name.setText(event.getName());
        comment.setText(event.getComment());
        date.setText(event.getDateCompleted().format(formatter));
        // if there is no photograph saved to the event object, make the imageView invisible
        if (event.getPhotograph() == null){
            image.setVisibility(View.GONE);
        }else{
            image.setImageBitmap(event.getPhotograph());
        }

        return view;
    }

    @NonNull
    public void addSnapshotQuery(Query query, String TAG) {
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null) {
                    Log.w(TAG, "Listener failed", e);
                    return;
                }
                clearEventList();
                if (!querySnapshot.isEmpty()) {
                    List<String> eventStrs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        if (doc.get("name") != null) {
                            Map getDate = (Map) doc.get("dateCompleted");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
                            String newDateStr = getDate.get("year").toString() + "-" +
                                    getDate.get("monthValue").toString() + "-" +
                                    getDate.get("dayOfMonth").toString() + " 00:00:00";;
                            LocalDateTime newDate = LocalDateTime.parse(newDateStr, formatter);
                            String comment = doc.getString("comment");
                            // TODO store location and photograph after halfway
                            Event eventToAdd = new Event(doc.getString("name"),newDate, comment, null, false);
                            eventToAdd.setFirestoreId(doc.getId());
                            if (!events.contains(eventToAdd)) {
                                events.add(eventToAdd);
                            }

                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    public void clearEventList() {
        events.clear();
    }
}
