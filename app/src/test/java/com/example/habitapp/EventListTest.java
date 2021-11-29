package com.example.habitapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.DataClasses.EventList;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventListTest implements EventList.OnEventListener{
    public EventList mockEventList() {
        ArrayList<Event> mockEventList = new ArrayList<Event>();
        mockEventList.add(mockEvent());
        // layout file doesn't matter for testing, just needs one for the constructor
        EventList eventListAdapter = new EventList(mockEventList, this, R.layout.events_listview_content);
        return eventListAdapter;
    }

    private Event mockEvent() {
        // creates a mock event for method testing
        // no need for photo/location stuff
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        Event event = new Event("Running", date, "Ran 5k!", null, "test", 0.0, 0.0, "Edmonton");
        return event;
    }

    /**
     * Tests adding an event to the list
     */
    @Test
    public void testAddEvent() {
        EventList newEventList = mockEventList();
        assertEquals(1, newEventList.getEvents().size());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        Event event = new Event("Jogging", date, "Ran 5k!", null, "test", 0.0, 0.0, "Edmonton");
        newEventList.addEvent(event, false); // don't call notifyDataSetChanged() in testing
        assertEquals(2, newEventList.getEvents().size());
        assertTrue(newEventList.getEvents().contains(event));
    }

    /**
     * Tests adding an already existing habit to the list,
     * to make sure it throws an exception
     */
    @Test
    public void testAddEventException(){
        EventList newEventList = mockEventList();
        Event newEvent = mockEvent();
        newEventList.addEvent(newEvent, false);
        assertThrows(IllegalArgumentException.class, () -> {
            newEventList.addEvent(newEvent, false);
        });
    }

    /**
     * Tests clearing the entire list
     */
    @Test
    public void testClearEvents(){
        EventList newEventList = mockEventList();
        assertEquals(1, newEventList.getEvents().size());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        Event event = new Event("Jogging", date, "Ran 5k!", null, "test", 0.0, 0.0, "Edmonton");
        newEventList.addEvent(event, false); // don't call notifyDataSetChanged() in testing
        newEventList.clearEventList();
        assertEquals(0, newEventList.getEvents().size());
    }

    /**
     * Tests making sure list can be called upon to find out if empty
     */
    @Test
    public void testGetEventListEmpty(){
        EventList newEventList = mockEventList();
        assertEquals(false, newEventList.getEventListEmpty());
        newEventList.clearEventList();
        assertEquals(true, newEventList.getEventListEmpty());
    }

    /**
     * Tests making sure it can sort based on date as expected
     */
    @Test
    public void testSortDate(){
        EventList newEventList = mockEventList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-12 00:00:00", formatter);
        Event event = new Event("Jogging", date, "Ran 5k!", null, "test", 0.0, 0.0, "Edmonton");
        newEventList.addEvent(event, false); // don't call notifyDataSetChanged() in testing
        newEventList.sortEvents(false);
        assertEquals(newEventList.getEvents().get(0), event); // despite being added second, should be in index 0 because of more recent date
    }

    /**
     * Tests making sure it can sort based on name as expected
     */
    @Test
    public void testSortName(){
        EventList newEventList = mockEventList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        Event event = new Event("Jogging", date, "Ran 5k!", null, "test", 0.0, 0.0, "Edmonton");
        newEventList.addEvent(event, false); // don't call notifyDataSetChanged() in testing
        newEventList.sortEvents(false);
        assertEquals(newEventList.getEvents().get(0), event); // despite being added second, should be in index 0 because of alphabetical order (Jogging before Running)
    }


    @Override
    public void onEventClick(int position) {
        // do nothing
    }
}
