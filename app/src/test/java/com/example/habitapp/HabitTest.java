package com.example.habitapp;

import static org.junit.Assert.assertEquals;

import com.example.habitapp.DataClasses.DaysOfWeek;
import com.example.habitapp.DataClasses.Habit;


import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class HabitTest {
    private Habit createHabit(){
        DaysOfWeek frequency = new DaysOfWeek(false,true, false, true,false, true, false);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-02-02 00:00:00", formatter);
        boolean privacy = true;
        Habit habit = new Habit("Exercise", "Get fit", date, frequency, privacy);

        return habit;
    }

    /** test if habit is created right and attributes are correct
     *
     */
    @Test
    public void testHabit(){
        Habit newHabit = createHabit();
        assertEquals("Exercise",newHabit.getTitle());
        assertEquals("Get fit",newHabit.getReason());
        DaysOfWeek frequency = new DaysOfWeek(false,true, false, true,false, true, false);
        assertEquals(frequency.getAll() , newHabit.getWeekOccurence().getAll());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime newDate = LocalDateTime.parse("2021-02-02 00:00:00", formatter);
        assertEquals(newDate, newHabit.getDateStarted());
    }
}
