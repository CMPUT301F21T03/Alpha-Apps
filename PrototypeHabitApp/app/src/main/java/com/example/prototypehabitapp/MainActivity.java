package com.example.prototypehabitapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;

    // prep the all_habits screen related objects
    private ListView allHabitsListView;
    private ArrayAdapter<Habit> habitAdapter;
    private ArrayList<Habit> habitDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boot_screen);
    }

    // ========================= BUTTON ACTIONS =============================

    // ~~~~~~~~~~~~~~~~~~~~~~~~ BOOT SCREEN ~~~~~~~~~~~~~~~~~~~~~~~~~~
    // if a user clicks on the boot screen's log in button
    public void bootScreenLogInButtonPressed(View view){
        // move to the log in screen
        setContentView(R.layout.log_in_screen);
    }

    public void bootScreenSignUpButtonPressed(View view){
        // move to the sign up screen
        setContentView(R.layout.sign_up_screen);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~ LOG IN SCREEN ~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void logInScreenGoBackButtonPressed(View view){
        // go back to the boot screen
        setContentView(R.layout.boot_screen);
    }

    public void logInScreenSignUpHyperlinkPressed(View view){
        // go to the sign up screen
        setContentView(R.layout.sign_up_screen);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void logInScreenLogInButtonPressed(View view){
        // TODO: put your name here for Firestore. Remove when username system is implemented later.
        final String username = "test";
        //prep Firestore
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final DocumentReference user = db.collection("Doers").document(username);

        // get the Strings inside the editText views
        String email = findViewById(R.id.loginscreen_email).toString();
        String password = findViewById(R.id.loginscreen_password).toString();

        //TODO check firebase for an email and password combination matching the above
        // If one pair exists return the user's ID and bring them to their "All Habits" page


        // move to the all habits page
        setContentView(R.layout.all_habits);

        //get the list view for the all habits page
        allHabitsListView = findViewById(R.id.allhabits_habit_list);

        //TODO one needs to get the habit array of the user once the user is known
        // for now it is an empty list
        habitDataList = new ArrayList<>();

        // add some test data
        DaysOfWeek testDaysOfWeek = new DaysOfWeek();
        Habit test_habit = new Habit("title", "reason", LocalDateTime.now(), testDaysOfWeek);
        test_habit.setProgress(100.0);
        habitDataList.add(test_habit);
        habitDataList.add(test_habit);

        habitAdapter = new HabitList(context, habitDataList);
        allHabitsListView.setAdapter(habitAdapter);

        //show your page from Firestore
        user.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot queryDocumentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                habitDataList.clear();
                assert queryDocumentSnapshot != null;
                if (queryDocumentSnapshot != null && queryDocumentSnapshot.exists()) {
                    // just takes the name for testing purposes
                    Map userData = queryDocumentSnapshot.getData();
                    for(String s: (ArrayList<String>) userData.get("habits")){
                        habitDataList.add(new Habit(s,"reason",LocalDateTime.now(),testDaysOfWeek));
                    }
                }
                habitAdapter.notifyDataSetChanged();
            }
        });

    }



    // ~~~~~~~~~~~~~~~~~~~~~~~ SIGN UP SCREEN ~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void signUpScreenGoBackButtonPressed(View view){
        // go back to the boot screen
        setContentView(R.layout.boot_screen);
    }

    public void signUpScreenLogInHyperlinkPressed(View view){
        // go to the sign up screen
        setContentView(R.layout.log_in_screen);
    }

    public void signUpScreenSignInButtonPressed(View view){
        // get the Strings inside the editText views
        String name = findViewById(R.id.signupscreen_name_alias).toString();
        String email = findViewById(R.id.signupscreen_email).toString();
        String password = findViewById(R.id.signupscreen_password).toString();

        //TODO go to firebase and create a unique ID for this user and assign it to them
        // then open the all habits page for the user

        // move to the all habits page
        setContentView(R.layout.all_habits);

    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~ NAV BAR ~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public void navBarTodayButtonPressed(View view){
        // move to the today screen
        setContentView(R.layout.today_habits);
    }

    public void navBarAllHabitsButtonPressed(View view){
        // move to the all habits screen
        setContentView(R.layout.all_habits);
    }

    public void navBarAddHabitButtonPressed(View view){
        // move to the add habit screen
        setContentView(R.layout.add_habit);
    }

    public void navBarFeedButtonPressed(View view){
        // move to the feed screen
        setContentView(R.layout.feed);
    }

    public void navBarProfileButtonPressed(View view){
        // move to the profile screen
        setContentView(R.layout.profile);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~ ADD HABIT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addHabitSelectDateHyperlinkPressed(View view){
        // get the current date
        LocalDateTime today = LocalDateTime.now();
        Integer year = today.getYear();
        Integer month = today.getMonthValue();
        Integer day = today.getDayOfMonth();
        // create a date picker dialog set to todays date
        DatePickerDialog dialog = new DatePickerDialog(
                MainActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                null,
                year, month, day);
        // make the white box around the date dialog invisible
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the logic that takes place when the user clicks the ok option
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                // format the selection into a string (yyyy-mm-dd)
                Integer year = (Integer) y;
                Integer month = m + 1; //counts the months from 0-11 not 1-12 (add one to correct)
                Integer day = (Integer) d;
                String selectedDate = year.toString() + "-" + month.toString() + "-" + day.toString();

                // set the hint text to the date that was selected
                TextView selectDateTextView = findViewById(R.id.addhabit_select_date);
                selectDateTextView.setText(selectedDate);
            }
        });

        // make the dialog visible to the user
        dialog.show();
    }

    public void addHabitCompleteButtonPressed(View view){
        // TODO move to another screen, this is not yet specified so it goes to all_habits by default
        setContentView(R.layout.all_habits);
    }

    public void addHabitGoBackButtonPressed(View view){
        //TODO move to the most recent previous screen
        // currently it goes to the all habits screen by default
        setContentView(R.layout.all_habits);
    }





}