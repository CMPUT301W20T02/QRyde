package com.example.qryde;

import android.content.Intent;
import android.media.Image;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Calendar.getInstance;

/**
 * This Class deals with user actions when a ride is completed
 * Rider offers Driver QR Bucks payment
 * Driver scans QR Bucks from Rider's phone
 */

public class RideComplete extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "RideInProgress";

    private String user;
    private Button ScanButton;
    private TextView rideComplete;

    private static RideComplete instance;


    private boolean positive = true;
    private GoogleMap ActualMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_complete);
        Bundle incomingData = getIntent().getExtras();
        ScanButton = findViewById(R.id.close_button);
        rideComplete = findViewById(R.id.ride_complete_text);

        instance = this; // for calling functions

        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        //initializing map in the background
        MapInit();

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
    }

    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(RideComplete.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 0);
        ActualMap = googleMap;
    }
}
