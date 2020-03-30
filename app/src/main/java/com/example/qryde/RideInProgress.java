package com.example.qryde;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
 * This class deals with the app activity while the ride is in progress.
 */
public class RideInProgress extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "RideInProgress";

    private FirebaseFirestore db;
    private String user;
    private String riderPicked;
    private String riderName;
    private String number;
    private String email;
    float amountOffered;
    private TextView riderTextView;




    private GoogleMap ActualMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);

        riderTextView = findViewById(R.id.rider);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }


        MapInit();

        riderTextView.setText(riderPicked);


        db = FirebaseFirestore.getInstance();

        db.collection("ActiveRides").document(riderPicked).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            /**
             * adds the current ride to the firebase collection "Active Rides"
             * @param DocumentSnapshot
             * @param FirebaseException Exception to be thrown if error occurs
             */
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("status").toString().equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), RideComplete.class);
                        intent.putExtra("rider", riderPicked);
                        intent.putExtra("user", user);
                        intent.putExtra("amount", amountOffered);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    finish();
                }
            }
        });
        riderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("username", riderPicked)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        riderName = document.getData().get("name").toString();
                                        number = document.getData().get("phoneNumber").toString();
                                        email = document.getData().get("email").toString();

                                    }
                                }
                            }
                        });

                Intent intent = new Intent(getApplicationContext(), UserInfo.class);
                intent.putExtra("fullname", riderName);
                intent.putExtra("number", number);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
    }


    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(RideInProgress.this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 0);
        ActualMap = googleMap;
    }



}
