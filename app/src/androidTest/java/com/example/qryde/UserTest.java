package com.example.qryde;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {
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
    public void requestRideThenCancel() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);

        solo.clickOnScreen(766,424);
        solo.sendKey(KeyEvent.KEYCODE_E);
        solo.sendKey(KeyEvent.KEYCODE_T);
        solo.sendKey(KeyEvent.KEYCODE_L);
        solo.sendKey(KeyEvent.KEYCODE_C);

        solo.clickOnScreen(744,430);

        View view = solo.getView(R.id.qryde_logo);
        solo.clickOnView(view);
        solo.enterText((EditText) solo.getView(R.id.amount), "25");
        solo.clickOnButton("confirm");

        solo.sleep(500);

        db.collection("AvailableRides")
                .whereEqualTo("rider", "bigtodd")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            assertTrue(true);
                        } else {
                            fail();
                        }
                    }
                });

        solo.sleep(500);

        db.collection("AvailableRides").document("bigtodd")
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

        solo.sleep(2000);

        solo.clickOnButton("cancel");


    }

    @Test
    public void requestRideComplete() throws InterruptedException{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username_edittext), "bigtodd");
        solo.enterText((EditText) solo.getView(R.id.password_edittext), "123");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MapActivity.class);

        solo.clickOnScreen(766,424);
        solo.sendKey(KeyEvent.KEYCODE_E);
        solo.sendKey(KeyEvent.KEYCODE_T);
        solo.sendKey(KeyEvent.KEYCODE_L);
        solo.sendKey(KeyEvent.KEYCODE_C);

        solo.clickOnScreen(744,430);

        View view = solo.getView(R.id.qryde_logo);
        solo.clickOnView(view);
        solo.enterText((EditText) solo.getView(R.id.amount), "25");
        solo.clickOnButton("confirm");

        solo.sleep(500);

        db.collection("AvailableRides")
                .whereEqualTo("rider", "bigtodd")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            assertTrue(true);
                        } else {
                            fail();
                        }
                    }
                });

        solo.sleep(500);

        // update firebase so that next action occurs
        db.collection("AvailableRides").document("bigtodd")
                .update("driver", "driver")
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
        db.collection("AvailableRides").document("bigtodd")
                .update("status", "true")
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
        solo.sleep(500);

        solo.clickOnButton("confirm");

        solo.sleep(2000);

        // check that firebase updated successfully
        db.collection("ActiveRides")
                .whereEqualTo("rider", "bigtodd")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            assertTrue(true);
                        } else {
                            fail();
                        }
                    }
                });

        solo.sleep(500);

        solo.clickOnButton("Ride Complete");
        solo.assertCurrentActivity("Wrong Activity", GenerateQRCode.class);

        db.collection("ActiveRides").document("bigtodd")
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


        solo.sleep(2000);


    }
}
