package com.example.qryde;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class describes the app activity while the rider is waiting
 * for the ride to be accepted
 */
public class WaitingUserResponse extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "WaitingUserResponse";

    private FirebaseFirestore db;

    private TextView tvStartLocation;
    private TextView tvEndLocation;

    private String user;
    private String riderPicked;
    float amountOffered;
    private Button cancelButton;

    private GoogleMap ActualMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_response);

        tvStartLocation = findViewById(R.id.startLocationText);
        tvEndLocation = findViewById(R.id.endLocationText);
        cancelButton = findViewById(R.id.cancel);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }

        MapInit();

        db = FirebaseFirestore.getInstance();
        db.collection("AvailableRides")
                .whereEqualTo("driver", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tvStartLocation.setText(document.getData().get("startLocation").toString());
                                tvEndLocation.setText(document.getData().get("endLocation").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("ActiveRides").document(riderPicked).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("status").toString().equals("false")) {
                        Log.d(TAG, "xd.", e);
                        Intent intent = new Intent(getApplicationContext(), RideInProgress.class);
                        intent.putExtra("rider", riderPicked);
                        intent.putExtra("user", user);
                        intent.putExtra("amount", amountOffered);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(WaitingUserResponse.this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 0);
        ActualMap = googleMap;
    }
}
