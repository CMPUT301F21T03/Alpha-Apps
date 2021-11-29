package com.example.habitapp;

import static android.content.ContentValues.TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import com.example.habitapp.DataClasses.DaysOfWeek;
import com.example.habitapp.DataClasses.Habit;
import com.example.habitapp.DataClasses.HabitList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HabitListTest implements HabitList.OnHabitListener{
    public HabitList mockHabitList(){

        ArrayList<Habit> mockDataList = new ArrayList<>();
        //Context context = ApplicationProvider.getApplicationContext();
        mockDataList.add(mockHabit());
        HabitList habitListAdapter = new HabitList(mockDataList, this);
        return habitListAdapter;
    }
    @NonNull
    private Habit mockHabit(){
        DaysOfWeek frequency = new DaysOfWeek(true,false, true, false,true, false, true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        boolean privacy = true;
        Habit habit = new Habit("Exercise", "Get fit", date, frequency, privacy, -1, -1, date, 0, 0);
        return habit;
    }


    /**
     * Tests adding a habit to the list
     */
    @Test
    public void testAddHabit(){
        HabitList newHabitList = mockHabitList();
        assertEquals(1, newHabitList.getHabits().size());
        DaysOfWeek frequency = new DaysOfWeek(false,false, true, false,true, false, true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-09-20 00:00:00", formatter);
        boolean privacy = true;
        Habit habit = new Habit("New Habit", "For test", date, frequency, privacy, -1, -1, date, 0, 0);
        newHabitList.addHabit(habit);
        assertEquals(2, newHabitList.getHabits().size());
        assertTrue(newHabitList.getHabits().contains(habit));
    }

    /**
     * Tests adding an already existing habit to the list,
     * to make sure it throws an exception
     */
    @Test
    public void testAddHabitException(){
        HabitList newHabitList = mockHabitList();
        Habit newHabit = mockHabit();
        newHabitList.addHabit(newHabit);
        assertThrows(IllegalArgumentException.class, () -> {
            newHabitList.addHabit(newHabit);
        });
    }

    /**
     * Tests clearing the entire list
     */
    @Test
    public void testClearHabits(){
        HabitList newHabitList = mockHabitList();
        assertEquals(1, newHabitList.getHabits().size());
        DaysOfWeek frequency = new DaysOfWeek(false,false, true, false,true, false, true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-09-20 00:00:00", formatter);
        boolean privacy = true;
        Habit habit = new Habit("New Habit", "Test clear", date, frequency, privacy, -1, -1, date, 0, 0);
        newHabitList.addHabit(habit);
        newHabitList.clearHabitList();
        assertEquals(0, newHabitList.getHabits().size());
    }

    /**
     * Tests making sure list can be called upon to find out if empty
     */
    @Test
    public void testGetHabitListEmpty(){
        HabitList newHabitList = mockHabitList();
        assertEquals(false, newHabitList.getHabitListEmpty());
        newHabitList.clearHabitList();
        assertEquals(true, newHabitList.getHabitListEmpty());
    }


    @Override
    public void onHabitClick(int position) {
        // do nothing
    }
}
