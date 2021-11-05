package com.example.habitapp;

import static org.junit.Assert.*;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.HabitDetails;
import com.example.habitapp.Activities.HabitEventDetails;
import com.example.habitapp.Activities.LogIn;
import com.example.habitapp.Activities.Main;
import com.example.habitapp.DataClasses.Event;
import com.example.habitapp.Fragments.AllHabits;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class HabitEventDetailsTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<BootScreen> rule = new ActivityTestRule(LogIn.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        // have to log in to test account first
        // makes use of test account in database:
        // name: Jane Doe
        // user: test
        // password: abc123
        solo.assertCurrentActivity("Wrong Activity", LogIn.class);
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        solo.sleep(5); // wait for communication w/ server
        solo.assertCurrentActivity("Wrong Activity", Main.class); //  just checks for main, once profile is set up check for right user

    }

    /**
     * Simple test case to verify if everything's ok
     */

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Asserting app opened correctly to BootScreen
     */
    @Test
    public void testCorrectActivity(){
        solo.assertCurrentActivity("Wrong Activity", Main.class);
    }

    /**
     * Asserts habit event is created and is found in listview
     */
    @Test
    public void addHabitEventAndPopulatesName() {
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_habit_name = "Running" + String.valueOf(random_userid);
        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("Select a date");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        String date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.waitForActivity("HabitDetails");

        //create new habit event
        solo.clickOnView(solo.getView(R.id.habitdetails_more));
        solo.waitForText("Mark as done", 2, 1000);
        solo.clickOnMenuItem("Mark as done");
        solo.waitForText("Confirm", 2, 1000);
        solo.clickOnButton("Confirm");
        solo.waitForText("Cancel", 2, 1000);
        solo.clickOnButton("Cancel");
        solo.sleep(1);

        HabitDetails activity = (HabitDetails) solo.getCurrentActivity();
        final ListView events = activity.eventsListview;
        Event event = (Event)events.getItemAtPosition(0);
        assertEquals(new_habit_name, event.getName());
    }

    /**
     * Asserts habit event is logged with comment and found in list view
     */
    @Test
    public void addHabitEventAndPopulatesComment() {
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_habit_name = "Running" + String.valueOf(random_userid);
        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("Select a date");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        String date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.waitForActivity("HabitDetails");

        //create new habit event
        solo.clickOnView(solo.getView(R.id.habitdetails_more));
        solo.waitForText("Mark as done", 2, 1000);
        solo.clickOnMenuItem("Mark as done");
        solo.waitForText("Confirm", 2, 1000);
        solo.clickOnButton("Confirm");
        solo.waitForText("Log habit", 2, 1000);
        solo.clickOnButton("Log habit");
        solo.enterText((EditText) solo.getView(R.id.edithabitevent_comment),  "this is a comment");
        solo.clickOnButton("complete");
        solo.waitForActivity("HabitDetails");

        HabitDetails activity = (HabitDetails) solo.getCurrentActivity();
        final ListView events = activity.eventsListview;
        Event event = (Event)events.getItemAtPosition(0);
        assertEquals("this is a comment", event.getComment());

    }

    /**
     * Asserts going to habit event details when item in listview is clicked
     */
    @Test
    public void goToHabitEventDetails() {
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_habit_name = "Running" + String.valueOf(random_userid);
        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("Select a date");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        String date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.waitForActivity("HabitDetails");

        //create new habit event
        solo.clickOnView(solo.getView(R.id.habitdetails_more));
        solo.waitForText("Mark as done", 2, 1000);
        solo.clickOnMenuItem("Mark as done");
        solo.waitForText("Confirm", 2, 1000);
        solo.clickOnButton("Confirm");
        solo.waitForText("Log habit", 2, 1000);
        solo.clickOnButton("Log habit");
        solo.enterText((EditText) solo.getView(R.id.edithabitevent_comment),  "this is a comment");
        solo.clickOnButton("complete");
        solo.waitForActivity("HabitDetails");

        //go to habiteventdetails page
        solo.clickInList(1);
        solo.waitForActivity("HabitEventDetails");
        solo.assertCurrentActivity("Wrong Activity", HabitEventDetails.class);
        solo.waitForText(new_habit_name);
        solo.waitForText("this is a comment");
        solo.waitForText(date_text);

    }

    /**
     * Asserts deleting habit event when delete button is clicked
     */
    @Test
    public void deleteHabitEvent() {
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_habit_name = "Running" + String.valueOf(random_userid);
        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("Select a date");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        String date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.waitForActivity("HabitDetails");

        //create new habit event
        solo.clickOnView(solo.getView(R.id.habitdetails_more));
        solo.waitForText("Mark as done", 2, 1000);
        solo.clickOnMenuItem("Mark as done");
        solo.waitForText("Confirm", 2, 1000);
        solo.clickOnButton("Confirm");
        solo.waitForText("Log habit", 2, 1000);
        solo.clickOnButton("Log habit");
        solo.enterText((EditText) solo.getView(R.id.edithabitevent_comment),  "this is a comment");
        solo.clickOnButton("complete");
        solo.waitForActivity("HabitDetails");

        //go to habiteventdetails page
        solo.clickInList(1);
        solo.waitForActivity("HabitEventDetails");
        solo.clickOnView(solo.getView(R.id.habiteventdetails_delete));
        solo.waitForActivity("HabitDetails");

        //check habit events list is empty
        HabitDetails activity = (HabitDetails) solo.getCurrentActivity();
        final ArrayList<Event> events = activity.events;
        assertTrue(events.isEmpty());

    }


    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}
