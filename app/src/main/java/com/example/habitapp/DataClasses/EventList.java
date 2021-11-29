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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class EventList extends RecyclerView.Adapter<EventList.ViewHolder>{
    private String TAG = "eventListTAG";
    private ArrayList<Event> events;
    private OnEventListener onEventListener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int layoutFile;

    /**
     * creates a list of event objects. As this class extends an array adapter this object must
     * contain the context of its use so other functions may use this value later
     *
     * @param events the raw data of the event list which will be formatted by this class
     */
    public EventList(ArrayList<Event> events, OnEventListener onEventListener, int layoutFile) {
        this.events = events;
        this.onEventListener = onEventListener;
        this.layoutFile = layoutFile;
    }

    /**
     * ViewHolder class specialized for RecyclerView
     * Attaches to views in each cell's layout, and provides getters to access them.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int layoutFile;
        private TextView name, comment, date, username, location;
        private ImageView image;
        OnEventListener onEventListener;

        /**
         * Constructor for ViewHolder. Brings in the current view, and the listener
         * allows for onClick handling
         * @param view current view
         * @param onEventListener listener instance to allow for onClick
         */
        public ViewHolder(View view, OnEventListener onEventListener, int layoutFile){
            super(view);
            name = view.findViewById(R.id.eventslistviewcontent_name_text);
            comment = view.findViewById(R.id.eventslistviewcontent_comment_text);
            date = view.findViewById(R.id.eventslistviewcontent_date_text);
            image = view.findViewById(R.id.eventslistviewcontent_image);
            location = view.findViewById(R.id.eventslistviewcontent_location_text);
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

        public TextView getLocation() {
            return location;
        }

        /**
         * onClick handler that passes the onClick functionality to the interface
         * @param view parent view
         */
        @Override
        public void onClick(View view) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    /**
     * Interface to allow for onClick method when tapping on
     * cell in RecyclerView
     */
    public interface OnEventListener{
        void onEventClick(int position);
    }

    /**
     * Once connected to a holder in the RecyclerView, inflates it with relevant cell layout
     * belonging to an event
     * @param parent ViewGroup parent to get context from
     * @param viewType viewType number
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(layoutFile, parent, false);
        return new ViewHolder(listItem, onEventListener, layoutFile);
    }

    /**
     * Once connected to a holder in the RecyclerView, populates it with relevant data
     * belonging to an event
     * @param holder the "cell" in question in the RecyclerView
     * @param position the index of the position of the cell in the RecyclerView
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        final Event event = events.get(position);
        holder.getName().setText(event.getName());
        holder.getComment().setText(event.getComment());
        holder.getDate().setText(event.getDateCompleted().format(formatter));
        holder.getLocation().setText(event.getLocationName());

        if (layoutFile == R.layout.feed_events_listview_content) {
            holder.getUsername().setText(event.getUsername());
        }

        // if there is no photograph saved to the event object, make the imageView invisible
        if (event.getPhotograph() == null){
            holder.getImage().setVisibility(View.GONE);
        }else{
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
                                holder.getImage().setImageBitmap(imageBitmap);
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


    /**
     * if the data that this object is based on is changed in any way (as notified by firestore)
     * update the values to reflect the new ones in firestore
     * @param query the query to reach the location of the data in firestore representative of this object
     * @param TAG a string to indicate where this function is being called from
     */
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
                            String locationName = doc.getString("locationName");
                            Double longitude = doc.getDouble("longitude");
                            Double latitude = doc.getDouble("latitude");
                            Event eventToAdd = new Event(doc.getString("name"), newDate, comment, photograph, username, latitude, longitude, locationName);


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

    /**
     * custom comparator class, first sorts events by the date they were completed, and if two are
     * the same then it sorts them alphabetically. Sorts newer dates first (first entry is at top
     * of the listview)
     */
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

    /**
     * Clears the ArrayList containing all the Event objects
     */
    public void clearEventList() {
        events.clear();
    }

    /**
     * Returns the size of the ArrayList containing the Event objects
     * @return the size of the ArrayList containing the Event objects
     */
    @Override
    public int getItemCount(){
        return events.size();
    }


    /**
     * Adds a Event type object to our internal ArrayList
     * Also executes a check to see if Event already exists in list,
     * and will throw an exception in that case.
     * @param event The Habit object to add to the ArrayList
     * @param notify whether or not to call notifyDataSetChanged()
     */
    public void addEvent(Event event, Boolean notify) {
        if (events.contains(event)) {
            throw new IllegalArgumentException();
        }
        events.add(event);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * Sorts the events in the ArrayList as per EventCompare;
     * helpful for feed population
     */
    public void sortEvents(Boolean notify) {
        Collections.sort(events, new EventCompare());
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * Gets the ArrayList of Event objects
     * @return the ArrayList of Event objects
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Returns a boolean that's true if the ArrayList storing the Event objects is empty,
     * false otherwise
     * @return boolean if Event ArrayList is empty or not
     */
    public Boolean getEventListEmpty() {
        return events.isEmpty();
    }

    
}