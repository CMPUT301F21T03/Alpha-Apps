package com.example.habitapp;

import static org.junit.Assert.assertTrue;

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

public class LogInTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<BootScreen> rule = new ActivityTestRule(LogIn.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    /**
     * Simple test case to verify if everything's ok
     */

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Asserting app opened correctly to LogIn
     */
    @Test
    public void testCorrectActivity(){
        solo.assertCurrentActivity("Wrong Activity", LogIn.class);
    }

    /**
     * Asserting app can successfully log in using an existing user
     */
    @Test
    public void validLogIn()  {
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
     * Asserting app fails to login when user enters combinations of
     * both bad username and password
     */
    @Test
    public void invalidCredentials() {
        // makes use of test account in database:
        // name: Jane Doe
        // user: test
        // password: abc123

        solo.assertCurrentActivity("Wrong Activity", LogIn.class);

        // first test bad username w/ password entered (since the password entered won't matter)
        solo.enterText((EditText) solo.getView(R.id.loginscreen_email), "tes");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Username does not exist", 1, 5000));
        solo.clickOnButton("OK");

        // then test good username w/ bad password
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_email));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_email), "test");
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_password));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Incorrect Password", 1, 5000));
        solo.clickOnButton("OK");


    }

    /**
     * Asserting app fails to login
     * if either username, password, or both fields are left blank
     * and according message appears
     */
    @Test
    public void blankCredentials() {
        solo.assertCurrentActivity("Wrong Activity", LogIn.class);

        // test blank username w/ password entered
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_email));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Please enter your username and password", 1, 1000));
        solo.clickOnButton("OK");

        // then test blank password w/ valid username entered
        solo.enterText((EditText) solo.getView(R.id.loginscreen_email), "tes");
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Please enter your username and password", 1, 1000));
        solo.clickOnButton("OK");

        // then test blank password w/ invalid username entered
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_email));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_email), "tes");
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Please enter your username and password", 1, 1000));
        solo.clickOnButton("OK");

        // then test both blank user and pass
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_email));
        solo.clearEditText((EditText) solo.getView(R.id.loginscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up)); // misleading button name
        assertTrue(solo.waitForText("Please enter your username and password", 1, 1000));
        solo.clickOnButton("OK");

    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
