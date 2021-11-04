package com.example.prototypehabitapp;

import static android.content.ContentValues.TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import com.example.prototypehabitapp.Activities.Main;
import com.example.prototypehabitapp.DataClasses.DaysOfWeek;
import com.example.prototypehabitapp.DataClasses.Habit;
import com.example.prototypehabitapp.DataClasses.HabitList;

import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HabitListTest {
    

    public HabitList mockHabitList(){
        ArrayList<Habit> mockDataList = new ArrayList<>();
        HabitList habitList = new HabitList(null, mockDataList);
        habitList.add(mockHabit());
        return habitList;

    }
    private Habit mockHabit(){
        DaysOfWeek frequency = new DaysOfWeek(true,false, true, false,true, false, true);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2021-08-11 00:00:00", formatter);
        Habit habit = new Habit("Exercise", "Get fit", date, frequency);
        return habit;
    }

    //@Rule
    //public ActivityTestRule<Main> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testAdd(){
        HabitList newHabitList = mockHabitList();
        assertEquals(1, newHabitList.getHabits().size());


    }
}
