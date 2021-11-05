package com.example.habitapp;

import android.app.Activity;
import android.widget.EditText;

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

public class MainTest {
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
        solo.enterText((EditText) solo.getView(R.id.loginscreen_email), "test");
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
     * Makes sure back and forth navigation between the five main frames of the app works
     * Searches for 2 instances of text; one in title, one in bottom nav bar
     */
    @Test
    public void canNavigate(){
        solo.assertCurrentActivity("Wrong Activity", Main.class);
        solo.clickOnMenuItem("Today");
        solo.waitForText("Today", 2, 1000);
        solo.goBack();

        solo.clickOnMenuItem("Add Habit");
        solo.waitForText("Add", 2, 1000);
        solo.goBack();

        solo.clickOnMenuItem("Feed");
        solo.waitForText("Feed", 2, 1000);
        solo.goBack();

        solo.clickOnMenuItem("Profile");
        solo.waitForText("Profile", 2, 1000);
        solo.goBack();

        solo.clickOnMenuItem("All Habits");
        solo.waitForText("All", 2, 1000);
    }



    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
