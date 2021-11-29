package com.example.habitapp;

import android.app.Activity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.LogIn;
import com.example.habitapp.Activities.SignUp;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BootScreenTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<BootScreen> rule = new ActivityTestRule(BootScreen.class, true, true);

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
     * Asserting app opened correctly to BootScreen
     */
    @Test
    public void testCorrectActivity(){
        solo.assertCurrentActivity("Wrong Activity", BootScreen.class);
    }

    /**
     * Making sure Log In button brings us to LogIn Activity and we can press back button accordingly
     */
    @Test
    public void testLogIn(){
        // test Log In button, and then go back
        solo.assertCurrentActivity("Wrong Activity", BootScreen.class);
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.assertCurrentActivity("Wrong Activity", LogIn.class);
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", BootScreen.class);
    }

    /**
     * Making sure Sign Up button brings us to SignUp Activity and we can press back button accordingly
     */
    @Test
    public void testSignUp(){
        // test Sign Up button, and then go back
        // test Log In button, and then go back
        solo.assertCurrentActivity("Wrong Activity", BootScreen.class);
        solo.clickOnView(solo.getView(R.id.bootscreen_sign_up));
        solo.assertCurrentActivity("Wrong Activity", SignUp.class);
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", BootScreen.class);
    }

    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
