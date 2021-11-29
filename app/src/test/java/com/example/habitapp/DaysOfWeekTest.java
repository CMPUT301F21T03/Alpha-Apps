package com.example.habitapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.habitapp.DataClasses.DaysOfWeek;

import org.junit.Test;

public class DaysOfWeekTest {
    private DaysOfWeek mockFrequencyMixed() {
        DaysOfWeek frequency = new DaysOfWeek(false,false, true, false,true, false, true);
        return frequency;
    }

    private DaysOfWeek mockAllFalse() {
        DaysOfWeek frequency = new DaysOfWeek(false,false, false, false,false, false, false);
        return frequency;
    }

    private DaysOfWeek mockAllTrue() {
        DaysOfWeek frequency = new DaysOfWeek(true,true, true, true,true, true, true);
        return frequency;
    }

    /**
     * tests the method that checks if the whole DaysOfWeek object is completely false,
     * with a mix of setups
     */
    @Test
    public void testAllFalseMethod() {
        DaysOfWeek new_frequency = mockFrequencyMixed();
        assertEquals(false, new_frequency.areAllFalse());

        new_frequency = mockAllFalse();
        assertEquals(true, new_frequency.areAllFalse());

        new_frequency = mockAllTrue();
        assertEquals(false, new_frequency.areAllFalse());

    }
}
