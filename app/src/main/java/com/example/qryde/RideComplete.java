package com.example.qryde;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This Class deals with user actions when a ride is completed
 * Rider offers Driver QR Bucks payment
 * Driver scans QR Bucks from Rider's phone
 */

public class RideComplete extends AppCompatActivity {

    private String TAG = "RideInProgress";

    private FirebaseFirestore db;
    private String user;
    private String riderPicked;
    private Button ScanButton;
    private float amountOffered;
    private String amountOfferedString;
    private TextView amountOfferedTv;
    private TextView rideComplete;

    static String IOUmsg;
    private static RideComplete instance;

    int numTransactions;


    private ImageButton thumbsUp;
    private ImageButton thumbsDown;

    private boolean positive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_complete);
        Bundle incomingData = getIntent().getExtras();
        ScanButton = findViewById(R.id.close_button);
        amountOfferedTv = findViewById(R.id.qr_amount_text);
        rideComplete = findViewById(R.id.ride_complete_text);

        instance = this; // for calling functions

        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }

        amountOfferedString = "$"+amountOffered;
        amountOfferedTv.setText(amountOfferedString);

        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method starts a new app activity after the scan button is clicked
             * @param View
             * @return ScanQrCode.class             *
             */
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanQRCode.class);
                intent.putExtra("username", user);
                startActivity(intent);
                finish();
            }
        });

        thumbsUp = findViewById(R.id.thumbsUpButton);
        thumbsDown = findViewById(R.id.thumbsDownButton);

        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method allows the user to give the driver positive feedback
             * after the ride is over
             * @param View
             */
            public void onClick(View v) {
            }
        });

        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method allows the user to give the driver negative feedback
             * after the ride is over
             * @param View
             */
            public void onClick(View v) {
            }
        });
    }

}
