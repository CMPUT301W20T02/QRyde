package com.example.qryde;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.qryde", appContext.getPackageName());
    }

    /**
     * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
     used
     */
    @RunWith(AndroidJUnit4.class)
    public static class MainActivityTest{
        private Solo solo;
        @Rule
        public ActivityTestRule<MainActivity> rule =
                new ActivityTestRule<>(MainActivity.class, true, true);
        /**
         * Runs before all tests and creates solo instance.
         * @throws Exception
         */
        @Before
        public void setUp() throws Exception{
            solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        }
        /**
         * Gets the Activity
         * @throws Exception
         */
        @Test
        public void start() throws Exception{
            Activity activity = rule.getActivity();
        }
        /**
         * Add a city to the listview and check the city name using assertTrue
         * Clear all the cities from the listview and check again with assertFalse
         */
        @Test
        public void checkList(){
    //Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            solo.clickOnButton("ADD CITY"); //Click ADD CITY Button
    //Get view for EditText and enter a city name
            solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
            solo.clickOnButton("CONFIRM"); //Select CONFIRM Button
            solo.clearEditText((EditText) solo.getView(R.id.editText_name)); //Clear the EditText
    /* True if there is a text: Edmonton on the screen, wait at least 2 seconds and
    find minimum one match. */
            TestCase.assertTrue(solo.waitForText("Edmonton", 1, 2000));
            solo.clickOnButton("ClEAR ALL"); //Select ClEAR ALL
    //True if there is no text: Edmonton on the screen
            assertFalse(solo.searchText("Edmonton"));
        }
        /**
         * Check item taken from the listview
         */
        @Test
        public void checkCiyListItem(){
            solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
            solo.clickOnButton("ADD CITY");
            solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
            solo.clickOnButton("CONFIRM");
            solo.waitForText("Edmonton", 1, 2000);
    // Get MainActivity to access its variables and methods.
            MainActivity activity = (MainActivity) solo.getCurrentActivity();
            final ListView cityList = activity.cityList; // Get the listview
            String city = (String) cityList.getItemAtPosition(0); // Get item from first position
            assertEquals("Edmonton", city);
        }
        /**
         * Close activity after each test
         * @throws Exception
         */
        @After
        public void tearDown() throws Exception{
            solo.finishOpenedActivities();
        }
    }
}
