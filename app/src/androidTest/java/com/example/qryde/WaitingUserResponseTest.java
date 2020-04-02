//package com.example.qryde;
//
//import android.app.Activity;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.ActivityTestRule;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.robotium.solo.Solo;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.HashMap;
//
///**
// * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
// used
// */
//@RunWith(AndroidJUnit4.class)
//public class WaitingUserResponseTest{
//    private Solo solo;
//    private FirebaseFirestore db;
//    @Rule
//    public ActivityTestRule<MainActivity> rule =
//            new ActivityTestRule<>(MainActivity.class, true, true);
//
//    @Before
//    public void setUp() throws Exception{
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
//        db = FirebaseFirestore.getInstance();
//    }
//
//    @Test public void start() throws Exception {
//        Activity activity = rule.getActivity();
//    }
//
//    @Test
//    public void checkWaitingUserResponseTest() throws Exception {
//        deleteAvailableRide();
//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
//        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
//        solo.clickOnButton("Login");
//        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
//        createAvailableRide();
//        solo.clickLongInList(0);
//        solo.assertCurrentActivity("Wrong Activity", AfterDriverSelects.class);
//        deleteAvailableRide();
//
//    }
//
//    @Test
//    public void acceptRequestThenCancel() throws Exception {
//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.enterText((EditText) solo.getView(R.id.username_edittext), "driver");
//        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
//        solo.clickOnButton("Login");
//        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
//        solo.clickLongInList(0);
//        solo.assertCurrentActivity("Wrong Activity", AfterDriverSelects.class);
//        solo.clickOnButton("cancel");
//        solo.assertCurrentActivity("Wrong Activity", DriverMainMap.class);
//    }
//
//    public void createAvailableRide() {
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("amount", 999);
//        data.put("datetime", "2020/03/12 19:39");
//        data.put("driver", "");
//        data.put("endLocation", "4794 94 Ave NW, Edmonton, AB T6B 2T3, Canada");
//        data.put("rider", "TESTING");
//        data.put("startLocation", "4794 94 Ave NW, Edmonton, AB T6B 2T3, Canada");
//        data.put("status", false);
//        db.collection("AvailableRides").document("TESTING").set(data);
//        solo.sleep(2000);
//    }
//
//    public void deleteAvailableRide() {
//        // delete the document in AvailableRides
//        db.collection("AvailableRides").document("TESTING")
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
//    }
//
//}