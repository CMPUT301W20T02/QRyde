package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DriverMainMap extends AppCompatActivity implements OnMapReadyCallback {

    AvailableRideAdapter rideAdapter;
    ArrayList<AvailableRide> dataList;
    FirebaseFirestore db;

    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private String driver;
    private Integer markernumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_map);
        getLocationPermission();
        db = FirebaseFirestore.getInstance();

        ListView availableRideListView = findViewById(R.id.list_view);
        AvailableRide[] AvailableRideList = {};
        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));
        rideAdapter = new AvailableRideAdapter(this, dataList);
        availableRideListView.setAdapter(rideAdapter);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            driver = incomingData.getString("username");
        }

        // gets all available rides and puts them in rideadapter
        // gets each mapmarker from availablerides
        // rightnow mapmarkersare set to endlocation because start locations are all the same
        //
        db.collection("AvailableRides1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // adding object to datalist/view adapter
                                AvailableRide temp = document.toObject(AvailableRide.class);
                                dataList.add(temp);

                                // creating marker from temp object lat/long
                                LatLng tempLatLng = new LatLng(temp.getEndLocationLat(), temp.getEndLocationLng());

                                // adding marker to show on map
                                Marker marker = ActualMap.addMarker(new MarkerOptions().position(tempLatLng).title(
                                        temp.getRiderUsername() + markernumber));

                                // setting integer id for each marker and temp object
                                marker.setTag(markernumber);

                                // moving to next object, next marker
                                markernumber++;
                                rideAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("xd", "Error getting documents: ", task.getException());
                        }
                    }
                });


        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
            availableRideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WaitingUserResponse.class);
                startActivity(intent);
                return true;
            }
        });
    }

    // on leaving driver map activity, deletes activedriver document
    @Override
    protected void onStop() {
        super.onStop();
        db.collection("ActiveDrivers").document(driver)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Test", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TEST", "Error deleting document", e);
                    }
                });
    }

    //creates map fragment
    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(DriverMainMap.this);
    }

    //checks for location permissions on phone
    private void getLocationPermission() {
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

            // ADDED CODE IMPORTANT
            //marker number position listener
            ActualMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // TODO Auto-generated method stub
                    if(marker.equals(marker)){
                        Log.d("TEST", "test" + marker.getId());
                        marker.showInfoWindow();
                        return true;
                    }

                    return false;
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

                            // ADDED CODE IMPORTANT
                            // Creates active driver document with driver name, location and blank serving rider needed later
                            // needed to be here because it breaks otherwise IDK
                            DBCreateActiveDriver.activateDriver(driver, locationCurr.getLatitude(), locationCurr.getLongitude(), "");
                        } else {
                            mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude), 15f);
                            Toast.makeText(DriverMainMap.this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                            ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
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

    public void rideComplete(int position){
        Bundle bundle = new Bundle();
        bundle.putFloat("AMOUNT_OFFERED", dataList.get(position).getAmountOffered());
        RideCompleteFragment newFrag = new RideCompleteFragment();
        newFrag.show(getSupportFragmentManager(), "RIDE_COMPLETE");
    }

}