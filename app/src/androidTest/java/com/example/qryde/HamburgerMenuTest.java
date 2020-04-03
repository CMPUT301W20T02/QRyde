package com.example.qryde;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Driver;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class HamburgerMenuTest {
    private Solo solo;
    private FirebaseFirestore db;
    TextView emailTextView;
    TextView phoneTextView;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        db = FirebaseFirestore.getInstance();
    }
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    @Test
    public void openUserProfile() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
        ImageView HamburgerIcon = (ImageView) solo.getView(R.id.hamburger_menu_button);
        solo.clickOnView(HamburgerIcon);
        solo.clickOnText("Profile");
        solo.assertCurrentActivity("Wrong Activity", UserProfile.class);
    }

    @Test
    public void openRideHistories() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
        ImageView HamburgerIcon = (ImageView) solo.getView(R.id.hamburger_menu_button);
        solo.clickOnView(HamburgerIcon);
        solo.clickOnText("Trip History");
        solo.assertCurrentActivity("Wrong Activity", RideHistoryList.class);
    }

    @Test
    public void editInfo() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
        ImageView HamburgerIcon = (ImageView) solo.getView(R.id.hamburger_menu_button);
        solo.clickOnView(HamburgerIcon);
        solo.clickOnText("Profile");
        solo.assertCurrentActivity("Wrong Activity", UserProfile.class);
        ImageView EditIcon = (ImageView) solo.getView(R.id.editImage);
        solo.clickOnView(EditIcon);
        String oldEmail = ((EditText) solo.getView(R.id.email_editText)).getText().toString();
        String oldPhone = ((EditText) solo.getView(R.id.phone_editText)).getText().toString();
        solo.clearEditText((EditText) solo.getView(R.id.email_editText));
        solo.clearEditText((EditText) solo.getView(R.id.phone_editText));
        solo.enterText((EditText) solo.getView(R.id.email_editText), "12345@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.phone_editText), "123456789");
        solo.clickOnView(solo.getView(android.R.id.button1));
        emailTextView = (TextView) solo.getView(R.id.email);
        phoneTextView = (TextView) solo.getView(R.id.phone_number);
        assertEquals(emailTextView.getText().toString(), "12345@gmail.com");
        assertEquals(phoneTextView.getText().toString(), "123456789");
        solo.clickOnView(EditIcon);
        solo.clearEditText((EditText) solo.getView(R.id.email_editText));
        solo.clearEditText((EditText) solo.getView(R.id.phone_editText));
        solo.enterText((EditText) solo.getView(R.id.email_editText), oldEmail);
        solo.enterText((EditText) solo.getView(R.id.phone_editText), oldPhone);
        assertEquals(emailTextView.getText().toString(), oldEmail);
        assertEquals(phoneTextView.getText().toString(), oldPhone);
    }

    @Test
    public void logOut() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);
        ImageView HamburgerIcon = (ImageView) solo.getView(R.id.hamburger_menu_button);
        solo.clickOnView(HamburgerIcon);
        solo.clickOnText("Logout");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}