package com.example.qryde;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
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
public class DriverTest {
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

    @Test public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkDriverSequence() throws Exception {
        deleteAvailableRide();
        deleteActiveRide();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
        createAvailableRide();
        solo.sleep(2000);
        solo.clickLongInList(0);
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.assertCurrentActivity("Wrong Activity", AfterDriverSelects.class);
        TextView titleTextView = (TextView)solo.getView(R.id.box_title);
        assertEquals("Waiting for User Response", titleTextView.getText().toString());
        createActiveRide();
        assertEquals("Ride in Progress...", titleTextView.getText().toString());
        updateActiveRide();
        assertEquals("Ride is Complete!", titleTextView.getText().toString());
        solo.clickOnButton("Scan QR Bucks");
        solo.assertCurrentActivity("Wrong Activity", ScanQRCode.class);
        deleteAvailableRide();
        deleteActiveRide();
    }


    @Test
    public void riderCanceledTest() throws Exception{
        deleteAvailableRide();
        deleteActiveRide();
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
        createAvailableRide();
        solo.sleep(2000);
        solo.clickLongInList(0);
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.assertCurrentActivity("Wrong Activity", AfterDriverSelects.class);
        updateAvailableRide();
        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
    }

    public void createAvailableRide() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", 999);
        GeoPoint startGeoPoint = new GeoPoint(53.4787909, -113.5884941);
        data.put("LatLng", startGeoPoint);
        GeoPoint DestGeoPoint = new GeoPoint(53.5225151, -113.6241906);
        data.put("LatLngDest", DestGeoPoint);
        data.put("datetime", "1999/03/12 19:39");
        data.put("driver", "");
        data.put("endLocation", "4794 94 Ave NW, Edmonton, AB T6B 2T3, Canada");
        data.put("rider", "TESTING");
        data.put("distance", 9.93);
        data.put("startLocation", "958 Rice Rd NW, Edmonton, AB T6R 1A1, Canada");
        data.put("status", false);
        db.collection("AvailableRides").document("TESTING").set(data);
        solo.sleep(2000);
    }
    public void createActiveRide() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("amount", 999);
        GeoPoint startGeoPoint = new GeoPoint(53.4787909, -113.5884941);
        data.put("LatLng", startGeoPoint);
        GeoPoint DestGeoPoint = new GeoPoint(53.5225151, -113.6241906);
        data.put("LatLngDest", DestGeoPoint);
        data.put("datetime", "1999/03/12 19:39");
        data.put("driver", "");
        data.put("endLocation", "4794 94 Ave NW, Edmonton, AB T6B 2T3, Canada");
        data.put("rider", "TESTING");
        data.put("distance", 9.93);
        data.put("startLocation", "958 Rice Rd NW, Edmonton, AB T6R 1A1, Canada");
        data.put("status", false);
        db.collection("ActiveRides").document("TESTING").set(data);
        solo.sleep(2000);
    }
    public void updateAvailableRide(){
        HashMap<String, Object> data = new HashMap<>();
        data.put("driver", "");
        db.collection("AvailableRides").document("TESTING").update(data);
        solo.sleep(2000);
    }
    public void updateActiveRide(){
        HashMap<String, Object> data = new HashMap<>();
        data.put("status", true);
        db.collection("ActiveRides").document("TESTING").update(data);
        solo.sleep(2000);
    }
    public void deleteAvailableRide() {
        // delete the document in AvailableRides
        db.collection("AvailableRides").document("TESTING")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void deleteActiveRide() {
        db.collection("ActiveRides").document("TESTING")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @After
    public void tearDown() throws Exception{
        deleteActiveRide();
        deleteAvailableRide();
    }


}