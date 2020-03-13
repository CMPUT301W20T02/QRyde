package com.example.qryde;


import android.app.Activity;
import android.app.Instrumentation;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkActivity() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.clickOnButton("Login");
        solo.clickOnText("Signup");
        solo.assertCurrentActivity("Wrong Activity", signup.class);
        solo.enterText((EditText) solo.getView(R.id.name_edittext), "Test User's Name");
        solo.enterText((EditText) solo.getView(R.id.phone_edittext), "12345678");
        solo.enterText((EditText) solo.getView(R.id.), "testUsername");




    }








    }
