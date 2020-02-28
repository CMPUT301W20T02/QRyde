package com.example.qryde;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback, ConfirmAvailableRideFragment.OnFragmentInteractionListener {

    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location locationCurr;
    private Location locationEnd = new Location("");
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private String user;
    private EditText startAddress;
    private EditText endAddress;
    private Button confirmRequestButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        db = FirebaseFirestore.getInstance();

        startAddress = (EditText) findViewById(R.id.startLocationText);
        endAddress = (EditText) findViewById(R.id.destinationText);
        confirmRequestButton = (Button) findViewById(R.id.confirm_request_button);

        // getting user from MainActivity
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        checkIfUserHasRide();

        confirmRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmAvailableRideFragment().show(getSupportFragmentManager(), "Confirm Ride");
            }
        });

    }

    //creates map fragment
    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(UserMapActivity.this);
    }

    //checks for location permissions on phone
    private void getLocationPermission() {
        //checks for location permissions on phone
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationPermission = true;
                MapInit();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1515);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1515);
        }
    }

    //requests the location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //requests the location permissions
        LocationPermission = false;
        switch (requestCode) {
            case 1515: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        LocationPermission = false;
                        return;
                    }
                }
                LocationPermission = true;
                MapInit();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        ActualMap = googleMap;
        if (LocationPermission) {
            updateLocationUI();
            DeviceLocation();

            // adds destination marker and sets locationend latitude longitude, sets destination text
            ActualMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    endAddress.setText(LatLngToAddress(point.latitude, point.longitude));
                    ActualMap.clear();
                    ActualMap.addMarker(new MarkerOptions().position(point));
                    locationEnd.setLatitude(point.latitude);
                    locationEnd.setLongitude(point.longitude);
                }
            });
        }
    }


    private void DeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermission) {
                final Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            locationCurr = (Location) task.getResult();
                            mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()), 15f);
                            startAddress.setText(LatLngToAddress(locationCurr.getLatitude(), locationCurr.getLongitude()));

                        } else {
                            mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude), 15f);
                            Toast.makeText(UserMapActivity.this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                            ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }

                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // double, double to address string address, city, province, postal, country
    private String LatLngToAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getAddressLine(0));
                result = sb.toString();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    //method for map camera movement
    private void mapMove(LatLng latLng, float zoom) {
        ActualMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //shows the blue dot.
    private void updateLocationUI() {
        if (ActualMap == null) {
            return;
        }
        try {
            if (LocationPermission) {
                ActualMap.setMyLocationEnabled(true);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                ActualMap.setMyLocationEnabled(false);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationCurr = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Ok press receiver for confirm ride fragment, starts popup with created ride in database and sends username
    @Override
    public void onOkPressed(float amountOffered) {
        DBCreateAvailableRide.createRide(user, startAddress.getText().toString(),
                endAddress.getText().toString(), locationCurr.getLatitude(), locationCurr.getLongitude(),
                locationEnd.getLatitude(), locationEnd.getLongitude(), amountOffered, 50, "pending", "");

        Intent intent = new Intent(this, PopUpWaitingForDriver.class);
        intent.putExtra("username", user);
        startActivity(intent);
    }

    // checks if user has ride in database and opens popupwaitingfordriver, sending username there and fills endaddressedittext
    private void checkIfUserHasRide() {
        DocumentReference docRef = db.collection("AvailableRides1").document(user);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TEST", "DocumentSnapshot data: " + document.getId());
                        Intent intent = new Intent(UserMapActivity.this, PopUpWaitingForDriver.class);
                        endAddress.setText(document.toObject(AvailableRide.class).getEndAddress());
                        intent.putExtra("username", document.toObject(AvailableRide.class).getRiderUsername());
                        startActivity(intent);


                    } else {
                        Log.d("TEST", "No such document");
                    }
                } else {
                    Log.d("TEST", "get failed with ", task.getException());
                }
            }
        });
    }




}

