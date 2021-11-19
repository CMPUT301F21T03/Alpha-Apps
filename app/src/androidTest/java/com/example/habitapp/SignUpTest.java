package com.example.habitapp;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.Main;
import com.example.habitapp.Activities.SignUp;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

public class SignUpTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<BootScreen> rule = new ActivityTestRule(SignUp.class, true, true);

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
        solo.assertCurrentActivity("Wrong Activity", SignUp.class);
    }

    /**
     * Asserts that a successful registration occurred
     */
    @Test
    public void validSignUp(){
        solo.assertCurrentActivity("Wrong Activity", SignUp.class);

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_username = "test" + String.valueOf(random_userid);

        // enter data
        solo.enterText((EditText) solo.getView(R.id.signupscreen_name_alias), "John Doe");
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), new_username);
        solo.enterText((EditText) solo.getView(R.id.signupscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        solo.sleep(5); // wait for communication w/ server
        solo.assertCurrentActivity("Wrong Activity", Main.class); //  just checks for main, once profile is set up check for right user
    }

    /**
     * Asserts that a sign up using an existing username fails
     */
    @Test
    public void invalidSignUp(){
        solo.assertCurrentActivity("Wrong Activity", SignUp.class);

        // enter data
        solo.enterText((EditText) solo.getView(R.id.signupscreen_name_alias), "John Doe");
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.signupscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Username already exists", 1, 5000));
        solo.clickOnButton("OK");

        // generate random username
        // 1/1000 chance of failing if firestore db is not reset after testing
        Random rand = new Random();
        int upper_bound = 1000;
        int random_userid = rand.nextInt(upper_bound);
        String new_username = "test" + String.valueOf(random_userid);

        // enter data
        // name is still there from before
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_username)); // replace name w/ a unique username
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), new_username);
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_password));
        solo.enterText((EditText) solo.getView(R.id.signupscreen_password), "a");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Your password is too short", 1, 5000));
        solo.clickOnButton("OK");

    }

    /**
     * Asserting app fails to sign up
     * if either name, username, password, or any combo of fields are left blank
     * and according message appears
     */
    @Test
    public void blankCredentials() {
        solo.assertCurrentActivity("Wrong Activity", SignUp.class);

        // test a blank name w/ username and password entered
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_name_alias));
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.signupscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test a blank username w/ name and password entered
        solo.enterText((EditText) solo.getView(R.id.signupscreen_name_alias), "John Doe");
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_username));
        // password still there from before
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test a blank password w/ name and username entered
        // name still there from before
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), "test");
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test all three blank
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_name_alias));
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_username));
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test name and username blank w/ password entered
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_name_alias));
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_username));
        solo.enterText((EditText) solo.getView(R.id.signupscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test username and password blank w/ name entered
        solo.enterText((EditText) solo.getView(R.id.signupscreen_name_alias), "John Doe");
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_username));
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

        // test name and password blank w/ username entered
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_name_alias));
        solo.enterText((EditText) solo.getView(R.id.signupscreen_username), "test");
        solo.clearEditText((EditText) solo.getView(R.id.signupscreen_password));
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));
        assertTrue(solo.waitForText("Please enter your name, your username and, password", 1, 1000));
        solo.clickOnButton("OK");

    }



    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
