package com.example.qryde;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{
    private Solo solo;
    private FirebaseFirestore db;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void signupActivity() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.clickOnButton("Login");
        solo.clickOnText("Signup");
        solo.assertCurrentActivity("Wrong Activity", signup.class);
        solo.enterText((EditText) solo.getView(R.id.name_edittext), "Test User's Name");
        solo.enterText((EditText) solo.getView(R.id.phone_edittext), "12345678");
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "testUsername");
        solo.enterText((EditText) solo.getView(R.id.email_edittext), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "password");
        solo.enterText((EditText) solo.getView(R.id.reenterPassword_edittext), "password");
        solo.clickOnButton("Signup");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);




    }
}