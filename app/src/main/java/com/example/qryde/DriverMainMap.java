package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.parseFloat;

public class DriverMainMap extends AppCompatActivity implements OnMapReadyCallback{

    private String TAG = "DriverMainMap";

    private AvailableRideAdapter rideAdapter;
    private ArrayList<AvailableRide> dataList;
    private EditText startLocationEditText;
    private EditText endLocationEditText;
    private FirebaseFirestore db;
    private String user;

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

        final ListView availableRideListView = findViewById(R.id.list_view);


        AvailableRide[] AvailableRideList = {};

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));

        rideAdapter = new AvailableRideAdapter(this, dataList);

        availableRideListView.setAdapter(rideAdapter);

        final CollectionReference collectionReference = db.collection("AvailableRides");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                dataList.clear();;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    AvailableRide temp = new AvailableRide(doc.getData().get("rider").toString(),
                            doc.getData().get("startLocation").toString(),
                            doc.getData().get("endLocation").toString(),
                            parseFloat(doc.getData().get("amount").toString()),
                            1.3f);
                    dataList.add(temp);

                    // creating marker from temp object lat/long
                    LatLng tempLatLng;
                    tempLatLng = getLocationFromAddress(DriverMainMap.this, temp.getStartLocation());

                    // adding marker to show on map
                    Marker marker = ActualMap.addMarker(new MarkerOptions().position(tempLatLng).title(
                            temp.getRiderUsername() + markernumber));

                    // setting integer id for each marker and temp object
                    marker.setTag(markernumber);

                    // moving to next object, next marker
                    markernumber++;

                    rideAdapter.notifyDataSetChanged();
                }
            }
        });


        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        availableRideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WaitingUserResponse.class);
                intent.putExtra("rider", dataList.get(position).getRiderUsername());
                intent.putExtra("username", user);
                intent.putExtra("amount", dataList.get(position).getAmountOffered());

                // updating firebase
                db.collection("AvailableRides").document(dataList.get(position).getRiderUsername())
                        .update("driver", user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Successfully updated document");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to updated document");
                            }
                        });
                db.collection("AvailableRides").document(dataList.get(position).getRiderUsername())
                        .update("status", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Successfully updated document");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to updated document");
                            }
                        });

                startActivity(intent);
                return true;
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
            //marker click position listener
            ActualMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
                            mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()), 11f);

                        } else {
                            mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude), 11f);
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

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;
    }


}