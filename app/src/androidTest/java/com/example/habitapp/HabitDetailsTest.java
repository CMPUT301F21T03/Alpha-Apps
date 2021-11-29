package com.example.habitapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.LogIn;
import com.example.habitapp.Activities.MainActivity;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.Random;

public class HabitDetailsTest {
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
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class); //  just checks for main, once profile is set up check for right user

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
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Asserting app successfully can add a habit by
     * adding the habit, and then checking for it in the all habits frame
     * as well as for its details in the habit details frame
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
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("yyyy-mm-dd");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));

        solo.waitForText(new_habit_name, 1, 5000);

        solo.clickOnText(new_habit_name);
        solo.waitForText("To stay healthy!", 1, 1000);
        solo.waitForText(new_habit_name, 1, 1000);
        solo.waitForText(date_text, 1, 1000);
        assertTrue(solo.isCheckBoxChecked("F"));
    }

    // add edit stuff once I know it works
    /**
     * Asserting app successfully can add a habit by
     * adding the habit, and then checking for it in the all habits frame.
     * Then we select it, edit some of its values, and return to the all habits
     * frame, to see if the edits persisted.
     */
    @Test
    public void editHabitAndPopulates() {
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        new_habit_name = "Running" + String.valueOf(random_userid);

        // add a new habit
        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.friday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("yyyy-mm-dd");
        TextView date_text_field = (TextView) solo.getView(R.id.addhabit_select_date);
        date_text = date_text_field.getText().toString();
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));

        // wait for it to reappear on All Habits list
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.sleep(5);

        // tap on it and edit all the fields
        solo.clickOnView(solo.getView(R.id.moreButton));
        solo.clickOnText("Edit habit");
        solo.clearEditText((EditText) solo.getView(R.id.habitdetails_title));
        random_userid = rand.nextInt(upper_bound);
        new_habit_name = "Walking" + String.valueOf(random_userid);
        solo.enterText((EditText) solo.getView(R.id.habitdetails_title), new_habit_name);
        solo.clearEditText((EditText) solo.getView(R.id.habitdetails_reason_text));
        solo.enterText((EditText) solo.getView(R.id.habitdetails_reason_text), "To stay healthier!");
        solo.clickOnView(solo.getView(R.id.wednesday_checkbox));
        solo.clickOnView(solo.getView(R.id.habitdetails_button_done_editing));

        solo.goBack();

        // wait for it to reappear on All Habits list
        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.sleep(5);

        // check for updated values
        solo.clickOnText(new_habit_name);
        solo.waitForText("To stay healthier!", 1, 1000);
        solo.waitForText(new_habit_name, 1, 1000);
        solo.waitForText(date_text, 1, 1000);
        assertTrue(solo.isCheckBoxChecked("F"));
        assertTrue(solo.isCheckBoxChecked("W"));

    }

    /**
     * Asserting app successfully can add a habit by
     * adding the habit, and then checking for it in the all habits frame.
     * Then we select it from all habits, delete it, and check for its absence in the list.
     */
    @Test
    public void deleteHabitAndPopulates(){
        solo.clickOnView(solo.getView(R.id.addHabitFragment));
        solo.waitForText("Add", 2, 1000);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_habit_name = "Running" + String.valueOf(random_userid);

        solo.enterText((EditText) solo.getView(R.id.addhabit_habit_title),  new_habit_name);
        solo.clickOnView(solo.getView(R.id.sunday_checkbox));
        solo.enterText((EditText) solo.getView(R.id.addhabit_reason),  "To stay healthy!");
        solo.clickOnText("yyyy-mm-dd");
        solo.clickOnText("OK");
        solo.clickOnView(solo.getView(R.id.addhabit_complete));

        solo.waitForText(new_habit_name, 1, 5000);
        solo.clickOnText(new_habit_name);
        solo.clickOnView(solo.getView(R.id.moreButton));
        solo.clickOnText("Delete habit");
        assertFalse(solo.searchText(new_habit_name));
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
