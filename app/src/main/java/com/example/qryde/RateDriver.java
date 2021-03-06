package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * This class deals with the functionality that allows the user to rate a driver at at the end of a ride
 */
public class RateDriver extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG = "temp";
    private FirebaseFirestore db;

    private String driver;
    private ImageView thumbsUp;
    private ImageView thumbsDown;
    private Button rideCompleteButton;
    private TextView titleText;

    private GoogleMap ActualMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);

        thumbsUp = findViewById(R.id.thumbsUpButton);
        thumbsDown = findViewById(R.id.thumbsDownButton);
        rideCompleteButton = findViewById(R.id.close_button);
        titleText = findViewById(R.id.ride_complete_text);

        //initializing map in the background
        MapInit();

        db = FirebaseFirestore.getInstance();

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            driver = incomingData.getString("driver");
        }
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens for whether the thumbs up button was clicked
             * @param v
             */
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("username", driver)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            /**
                             * This method increases a drivers thumbs up count which is used to calculate their rating
                             * @param task
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int newThumbsUpCount = parseInt(document.getData().get("thumbsUp").toString()) + 1;
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("thumbsUp", newThumbsUpCount);
                                        db.collection("Users").document(driver).update(data);
                                        thumbsUp.setVisibility(View.GONE);
                                        thumbsDown.setVisibility(View.GONE);
                                        titleText.setText("Thank you for your rating!");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        thumbsDown.setOnClickListener(new View.OnClickListener() {
            /**
             * This method listens to see if the thumbsdown button was clicked
             * @param v
             */
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("username", driver)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            /**
                             * This method updates a driver's thumbsdown count which is used to calculate their rating
                             * @param task
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        int newThumbsDownCount = parseInt(Objects.requireNonNull(document.getData().get("thumbsDown")).toString()) + 1;
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("thumbsDown", newThumbsDownCount);
                                        db.collection("Users").document(driver).update(data);
                                        thumbsUp.setVisibility(View.GONE);
                                        thumbsDown.setVisibility(View.GONE);
                                        titleText.setText("Thank you for your rating!");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        rideCompleteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method finishes the activity if the rideComplete button is pressed
             * @param v
             */
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //This initializes the map fragment to the app
    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(RateDriver.this);
    }

    /**
     * This method is called when the map is ready to be used
     * @param googleMap the map object to be used
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 0);
        ActualMap = googleMap;
    }
}

