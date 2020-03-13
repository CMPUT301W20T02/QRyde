package com.example.qryde;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Driver;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class DriverMainMapTest{
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSlidingPane() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
    }

}