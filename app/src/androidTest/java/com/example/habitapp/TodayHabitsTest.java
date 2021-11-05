package com.example.habitapp;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.LogIn;
import com.example.habitapp.Activities.Main;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class TodayHabitsTest {
    private Solo solo;
    private String new_habit_name;
    private String date_text;

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
     * Asserts app can successfully add a new habit, and have it populate
     * in the today view. Done by setting new habit's date to current date,
     * and by selecting all days of the week just in case.
     */
    @Test
    public void addHabitAndPopulatesDetails(){
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        new_habit_name = "Running" + String.valueOf(random_userid);

        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);

        solo.clickOnView(solo.getView(R.id.sunday_checkbox));
        solo.clickOnView(solo.getView(R.id.monday_checkbox));
        solo.clickOnView(solo.getView(R.id.tuesday_checkbox));
        solo.clickOnView(solo.getView(R.id.wednesday_checkbox));
        solo.clickOnView(solo.getView(R.id.thursday_checkbox));
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.clickOnView(solo.getView(R.id.sunday_checkbox));

        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("Select a date");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));

        solo.waitForText(new_habit_name, 1, 5000);

        solo.clickOnMenuItem("Today");
        solo.waitForText("Today", 2, 1000);

        solo.waitForText(new_habit_name, 1, 5000);

    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
