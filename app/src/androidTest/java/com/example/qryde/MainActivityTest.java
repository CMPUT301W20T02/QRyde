package com.example.qryde;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;



/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest{
    private Solo solo;
    private FirebaseFirestore db;
    private String TAG = "robotium";

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

        solo.clickOnText("Signup");
        solo.assertCurrentActivity("Wrong Activity", signup.class);
        solo.enterText((EditText) solo.getView(R.id.name_edittext), "Test User's Name");
        solo.enterText((EditText) solo.getView(R.id.phone_edittext), "12345678");
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "testUsername");
        solo.enterText((EditText) solo.getView(R.id.email_edittext), "test@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "password");
        solo.enterText((EditText) solo.getView(R.id.reenterPassword_edittext), "password");
        solo.clickOnText("Signup");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.sleep(2000);

        // delete the newly created user
        db.collection("Users").document("testUsername")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Successfully deleted document");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete document");
                    }
                });
    }

    @Test
    public void driverSignUpTest() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
    }

    @Test
    public void riderSignUpTest() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
    }

    @Test
    public void signupFail() throws InterruptedException {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "nonexistent");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");

        solo.sleep(2000);

        TextView incorrect = (TextView) solo.getView(R.id.incorrect);

        assertEquals("Incorrect Username or Password", incorrect.getText().toString());



    }
}