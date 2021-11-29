package com.example.habitapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.habitapp.Activities.BootScreen;
import com.example.habitapp.Activities.FollowingFollowers;
import com.example.habitapp.Activities.LogIn;
import com.example.habitapp.Activities.Main;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileTest {
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

    @Test
    public void followUserAndAccept() {
        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.enterText((EditText) solo.getView(R.id.profile_search_field), "test2");
        solo.clickOnView(solo.getView(R.id.profile_search_button));
        solo.sleep(5); // wait for communication w/ server
        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Request to Follow");

        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test2");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_pending_button));
        solo.clickOnText("test");
        solo.clickOnText("Yes");

        //solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_followers));
        solo.sleep(5); // wait for communication w/ server
        assertTrue(solo.searchText("test2"));

        // then unfollow
        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_following));
        solo.clickOnText("test2");


        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Unfollow");

    }

    @Test
    public void followUserAndAcceptAndCheckInfo() {
        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.enterText((EditText) solo.getView(R.id.profile_search_field), "test2");
        solo.clickOnView(solo.getView(R.id.profile_search_button));
        solo.sleep(5); // wait for communication w/ server
        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Request to Follow");

        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test2");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_pending_button));
        solo.clickOnText("test");
        solo.clickOnText("Yes");

        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_followers));
        solo.sleep(5); // wait for communication w/ server
        assertTrue(solo.searchText("test2"));

        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_following));
        solo.clickOnText("test2");
        solo.sleep(5); // wait for communication w/ server
        assertTrue(solo.searchText("Running"));

        // then unfollow
        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_following));
        solo.clickOnText("test2");


        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Unfollow");

    }

    @Test
    public void followUserAndDeny() {
        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.enterText((EditText) solo.getView(R.id.profile_search_field), "test3");
        solo.clickOnView(solo.getView(R.id.profile_search_button));
        solo.sleep(5); // wait for communication w/ server
        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Request to Follow");

        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test3");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_pending_button));
        solo.clickOnText("test");
        solo.clickOnText("No");

        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_followers));
        solo.sleep(5); // wait for communication w/ server

    }

    @Test
    public void followUserAndCheckPending() {
        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.enterText((EditText) solo.getView(R.id.profile_search_field), "test2");
        solo.clickOnView(solo.getView(R.id.profile_search_button));
        solo.sleep(5); // wait for communication w/ server
        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Request to Follow");

        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test2");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.clickOnView(solo.getView(R.id.profile_pending_button));
        assertTrue(solo.searchText("test2"));

        // then unfollow
        solo.clickOnView(solo.getView(R.id.go_back_button));
        solo.clickOnView(solo.getView(R.id.profile_log_out));
        solo.clickOnView(solo.getView(R.id.bootscreen_log_in));
        solo.enterText((EditText) solo.getView(R.id.loginscreen_username), "test");
        solo.enterText((EditText) solo.getView(R.id.loginscreen_password), "abc123");
        solo.clickOnView(solo.getView(R.id.signupscreen_sign_up));

        solo.clickOnView(solo.getView(R.id.profileFragment));
        solo.waitForText("Profile", 2, 1000);
        solo.enterText((EditText) solo.getView(R.id.profile_search_field), "test2");
        solo.clickOnView(solo.getView(R.id.profile_search_button));
        solo.sleep(5); // wait for communication w/ server
        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Unfollow");



        solo.clickOnView(solo.getView(R.id.followuserview_follow_status));
        solo.clickOnText("Unfollow");

    }
}
