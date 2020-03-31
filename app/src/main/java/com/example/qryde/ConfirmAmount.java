package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * function that contains the confirm and cancel buttons for the rider,
 * if confirm is pressed it sends all info to firebase for the ride,
 * cancel button finished activity
 */
public class ConfirmAmount extends AppCompatActivity {

    private DateFormat dateformat;

    private FirebaseFirestore db;

    private TextView timeDistance;
    private TextView costSummary;

    private EditText amount;
    private Button confirmButton;
    private Button cancelButton;
    private double rideCost;
    private double rideDuration;
    private double rideDistance;

    private double amount_value = 0;

    private String user;
    private String pickupName;
    private String destinationName;
    private double starLat, startLng, endLat, endLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_amount);

        db =  FirebaseFirestore.getInstance();
        timeDistance = findViewById(R.id.time_distance);
        costSummary = findViewById(R.id.cost_summary);
        amount = findViewById(R.id.amount);
        confirmButton = findViewById(R.id.confirm);
        cancelButton = findViewById(R.id.cancel);

        setWindowSize();
        getRideInfo();

        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        timeDistance.setText(String.format("%s min (%s km)", Math.round(rideDuration), Math.round(rideDistance)));
        costSummary.setText(String.format("Your suggested cost is $%s\nPlease enter your amount below", rideCost));
        amount.setText("");

        confirmButton();
        cancelButton();
    }

    private void confirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click of the confirm button put data of the active ride into firebase,
             * starts the afterRequestCreated activity
             * @param v
             */
            @Override
            public void onClick(View v) {
                Date date = new Date();
                Boolean status = false;

                HashMap<String, Object> data = new HashMap<>();
                if(amount.getText().toString().length() > 0){
                    amount_value = Float.parseFloat(amount.getText().toString());
                    data.put("LatLng",new GeoPoint(starLat, startLng));
                    data.put("LatLngDest", new GeoPoint(endLat, endLng));
                    data.put("amount", amount_value);
                    data.put("datetime", dateformat.format(date));
                    data.put("driver", "");
                    data.put("endLocation", destinationName);
                    data.put("rider", user);
                    data.put("startLocation", pickupName);
                    data.put("status", status);
                    data.put("distance", rideDistance);
                    db.collection("AvailableRides").document(user).set(data);

                    Intent intent3 = new Intent(getApplicationContext(), afterRequestCreated.class);
                    intent3.putExtra("username", user);
                    startActivity(intent3);
                    finish();
                }
            }
        });
    }

    private void cancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * when cancel button is clicked, finishes activity
             * @param v
             */
            @Override
            public void onClick(View v) {
                db.collection("AvailableRides").document(user)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "onSuccess: Successfully deleted document");
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onFailure: Failed to delete document");
                            }
                        });
                finish();
            }
        });
    }

    private void setWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, (height/9)*4);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    /**
     * gets the rider username, pickup and destination
     */
    public void getRideInfo() {
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");

        }
        Intent intent = getIntent();
        pickupName = intent.getStringExtra("pickup");
        destinationName = intent.getStringExtra("destination");
        rideCost = intent.getDoubleExtra("ride_cost", 0);
        rideDistance = intent.getDoubleExtra("ride_distance", 0);
        rideDuration = intent.getDoubleExtra("ride_duration", 0);
        starLat = intent.getDoubleExtra("startLat", 0);
        startLng = intent.getDoubleExtra("startLng", 0);
        endLat = intent.getDoubleExtra("endLat", 0);
        endLng = intent.getDoubleExtra("endLng", 0);

    }
}
