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
 *   1.5       Jesse     Nov-22-2021    Changed from ListView to RecyclerView
 * =|=======|=|======|===|====|========|===========|================================================
 */

package com.example.habitapp.DataClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaDrm;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventList extends RecyclerView.Adapter<EventList.ViewHolder>{ //ArrayAdapter<Event> {
    private String TAG = "eventListTAG";
    private ArrayList<Event> events;
    private Habit habit;
    private Map userData;
    private OnEventListener onEventListener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int layoutFile;

    public EventList(ArrayList<Event> events, OnEventListener onEventListener, int layoutFile) {
        //super(context, 0, events);
        this.events = events;
        this.onEventListener = onEventListener;
        this.layoutFile = layoutFile;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int layoutFile;
        private TextView name, comment, date, username;
        private ImageView image;
        OnEventListener onEventListener;

        public ViewHolder(View view, OnEventListener onEventListener, int layoutFile){
            super(view);
            name = view.findViewById(R.id.eventslistviewcontent_name_text);
            comment = view.findViewById(R.id.eventslistviewcontent_comment_text);
            date = view.findViewById(R.id.eventslistviewcontent_date_text);
            image = view.findViewById(R.id.eventslistviewcontent_image);
            this.onEventListener = onEventListener;
            this.layoutFile = layoutFile;

            if (layoutFile == R.layout.feed_events_listview_content) {
                username = view.findViewById(R.id.feed_listview_username);
            }

            view.setOnClickListener(this);
        }

        public TextView getName() {
            return name;
        }

        public TextView getComment() {
            return comment;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getUsername() {
            return username;
        }

        @Override
        public void onClick(View view) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventListener{
        void onEventClick(int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //View listItem = layoutInflater.inflate(R.layout.events_listview_content, parent, false);
        View listItem = layoutInflater.inflate(layoutFile, parent, false);
        return new ViewHolder(listItem, onEventListener, layoutFile);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final Event event = events.get(position);
        holder.getName().setText(event.getName());
        holder.getComment().setText(event.getComment());
        holder.getDate().setText(event.getDateCompleted().format(formatter));

        if (layoutFile == R.layout.feed_events_listview_content) {
            holder.getUsername().setText(event.getUsername());
        }

        // if there is no photograph saved to the event object, make the imageView invisible
        if (event.getPhotograph() == null){
            Log.d(TAG,"Null");
            holder.getImage().setVisibility(View.GONE);
        }else{
            // holder.getImage().setImageBitmap(event.getPhotograph());
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
                                holder.getImage().setImageBitmap(imageBitmap); //Bitmap.createScaledBitmap(imageBitmap, 300, 200, false));
                            }
                        });
                        Log.d(TAG, "Successfully set image");
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });

            thread.start();

        }


    }

    @Override
    public int getItemCount(){
        return events.size();
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
//        View view = convertView;
//        if(view == null){
//            view = LayoutInflater.from(context).inflate(R.layout.events_listview_content, parent,false);
//        }
//
//        Event event = events.get(position);
//        TextView name = view.findViewById(R.id.eventslistviewcontent_name_text);
//        TextView comment = view.findViewById(R.id.eventslistviewcontent_comment_text);
//        TextView date = view.findViewById(R.id.eventslistviewcontent_date_text);
//        ImageView image = view.findViewById(R.id.eventslistviewcontent_image);
//
//        name.setText(event.getName());
//        comment.setText(event.getComment());
//        date.setText(event.getDateCompleted().format(formatter));
//        // if there is no photograph saved to the event object, make the imageView invisible
//        if (event.getPhotograph() == null){
//            image.setVisibility(View.GONE);
//        }else{
//            image.setImageBitmap(event.getPhotograph());
//        }
//
//        return view;
//    }

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
                            String photograph = doc.getString("photograph");
                            String username = doc.getString("username");
                            // TODO store location and photograph after halfway
                            Event eventToAdd;
                            if (username == null) {
                                eventToAdd = new Event(doc.getString("name"),newDate, comment, photograph, "");
                            } else {
                                eventToAdd = new Event(doc.getString("name"),newDate, comment, photograph, username);

                            }

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

    public void addEvent(Event event) {
        events.add(event);
        notifyDataSetChanged();
    }

    public void sortEvents() {
        Collections.sort(events, new EventCompare());
        notifyDataSetChanged();
    }

    public class EventCompare implements Comparator<Event> {
        @Override
        public int compare(Event event_1, Event event_2) {
            int date_compare = event_2.getDateCompleted().compareTo(event_1.getDateCompleted());
            if (date_compare != 0) {
                return date_compare;
            }

            int name_compare = event_1.getName().compareTo(event_2.getName());
            return name_compare;


        }
    }
}