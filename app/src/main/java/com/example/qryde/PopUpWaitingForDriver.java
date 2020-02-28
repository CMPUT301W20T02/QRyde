package com.example.qryde;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


// waiting for driver mini activity, comes from UserMapActivity and returns to UserMapActivity on cancel ride or back pressed

public class PopUpWaitingForDriver extends AppCompatActivity {

    FirebaseFirestore db;
    TextView waitingText;
    TextView estimateText;
    TextView driverNameText;
    Button cancelButton;
    Button viewDriverProfileButton;
    Button acceptDriverButton;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_waiting_for_driver);
        waitingText = (TextView) findViewById(R.id.waiting_text);
        estimateText = (TextView) findViewById(R.id.estimate_text);
        driverNameText = (TextView) findViewById(R.id.driver_name_text);
        cancelButton = findViewById(R.id.cancel_button);
        viewDriverProfileButton = findViewById(R.id.driver_profile_button);
        acceptDriverButton = findViewById(R.id.accept_driver_button);

        //temp
        estimateText.setText("QR 20");


        db = FirebaseFirestore.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, height/3);
        getWindow().setGravity(Gravity.BOTTOM);

        // gets bundle from UserMapActivity
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        checkAvailableRide();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("AvailableRides1").document(user)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Test", "DocumentSnapshot successfully deleted!");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TEST", "Error deleting document", e);
                            }
                        });
            }
        });
    }

    // overrides back to not close waiting for ride window
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        db.collection("AvailableRides1").document(user)
//                .delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("Test", "DocumentSnapshot successfully deleted!");
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("TEST", "Error deleting document", e);
//                    }
//                });
//    }

    // live update for AvailableRide

    // driverfound displays more options to the user
    // pending hides accept ride and view driver profile
    // cancel deletes the ride from database, and closes activity
    private void checkAvailableRide() {
        DocumentReference docRef = db.collection("AvailableRides1").document(user);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TEST", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(snapshot.get("status").equals("driverfound")) {
                        viewDriverProfileButton.setVisibility(View.VISIBLE);
                        acceptDriverButton.setVisibility(View.VISIBLE);
                        driverNameText.setText(snapshot.getString("driver"));
                    }
                    if(snapshot.get("status").equals("pending")) {
                        viewDriverProfileButton.setVisibility(View.GONE);
                        acceptDriverButton.setVisibility(View.GONE);
                    }
                    if(snapshot.get("status").equals("cancel")) {
                        db.collection("AvailableRides1").document(user)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Test", "DocumentSnapshot successfully deleted!");
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TEST", "Error deleting document", e);
                                    }
                                });
                    }
                } else {
                    Log.d("TEST", "Current data: null");
                }
            }
        });
    }
}
