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

    private FirebaseFirestore db;
    private String user;
    private String riderPicked;
    private Button ScanButton;
    private float amountOffered;
    private String amountOfferedString;
    private TextView amountOfferedTv;
    private TextView rideComplete;
    private String destinationName;
    private String rideDistance;
    private String rideDuration;
    private Date todayDate;

    private static RideComplete instance;



    private ImageButton thumbsUp;
    private ImageButton thumbsDown;

    private boolean positive = true;
    private GoogleMap ActualMap;

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
            destinationName = incomingData.getString("destination");
            rideDistance = String.valueOf(incomingData.getDouble("ride_distance", 0));
            rideDuration = String.valueOf(incomingData.getDouble("ride_duration", 0));
        }

        //initializing map in the background
        MapInit();

        amountOfferedString = "$"+amountOffered;
        amountOfferedTv.setText(amountOfferedString);

        //todays date
        todayDate = new Date();
        todayDate = getInstance().getTime();

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

//    //saving data that is entered locally
//    private void saveData(ArrayList<String> rideInfo)
//    {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(rideInfo);
//        editor.putString("ride_info", json);
//        editor.apply();
//    }
}
