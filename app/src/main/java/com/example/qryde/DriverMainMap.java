package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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

/**
 * Driver side of the app after login, gets info of ride requests, shows them on the map,
 * Ride request displays necessary info,
 * sliding up panel is implemented to take a look at the rest of the available requests,
 * ride requests are pulled from firebase and updated once driver selects one via a longpress
 */
public class DriverMainMap extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    private String TAG = "DriverMainMap";

    private AvailableRideAdapter rideAdapter;
    private ArrayList<AvailableRide> dataList;
    private EditText startLocationEditText;
    private EditText endLocationEditText;
    private FirebaseFirestore db;
    private String user;

    private GoogleMap ActualMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private String driver;
    private Integer markernumber = 0;
    private DrawerLayout drawerLayout;

    private boolean perms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_map);
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        final ListView availableRideListView = findViewById(R.id.list_view);


        AvailableRide[] AvailableRideList = {};

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            perms = incomingData.getBoolean("permissions");
        }
        if (perms) {
            MapInit();
        }



        ImageButton navigationDrawer = (ImageButton) findViewById(R.id.hamburger_menu_button);
        navigationDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        db = FirebaseFirestore.getInstance();

        final ListView availableRideListView = findViewById(R.id.list_view);
        AvailableRide[] AvailableRideList = {};
        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));
        rideAdapter = new AvailableRideAdapter(this, dataList);
        availableRideListView.setAdapter(rideAdapter);

        final CollectionReference collectionReference = db.collection("AvailableRides");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * sets the rider name, start location,
             * end location and cost amount to instance of available ride.
             * Adds marker to the map according to coordinates and notifies rideAdapter data has changed
             * @param queryDocumentSnapshots
             * @param e
             */
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
                    tempLatLng = getLocationFromAddress(temp.getStartLocation());

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
            /**
             * When an object from the slide up panel is longPressed:
             * Updates the firebase with the selection for the ride request,
             * checks whether request is still active
             * if request is active, activity is switched to waiting for user response
             * @param parent
             * @param view
             * @param position
             * @param id
             * @return
             */
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

    /**
     * once the map is ready update the location with device location and the marker click function
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 0);
        ActualMap = googleMap;
        if (perms) {
            updateLocationUI();
            DeviceLocation();
            markerClick();
        }
    }

    private void markerClick() {
        // ADDED CODE IMPORTANT
        //marker click position listener
        ActualMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            /**
             * on marker click call function to show rider info
             * @param marker
             * @return
             */
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

    private void DeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (perms) {
                final Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    /**
                     * Once the location for device is accessed,
                     * if location is accessed successfully set the current location and move the map to it
                     * otherwise move map to default location
                     * @param task
                     */
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
        ActualMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 600, null);
    }

    //shows the blue dot.
    private void updateLocationUI() {
        if (ActualMap == null) {
            return;
        }
        try {
            if (perms) {
                ActualMap.setMyLocationEnabled(true);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                ActualMap.setMyLocationEnabled(false);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationCurr = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * gets the coordinates of location from address and returns it
     * @param strAddress
     * @return
     */
    public LatLng getLocationFromAddress(String strAddress)
    {
        Geocoder coder= new Geocoder(DriverMainMap.this);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_profile:{
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("username", user);
                startActivity(intent);
                Log.d("xd", "xd");
                break;
            }

            case R.id.nav_qr_wallet:{
                break;
            }
        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }


}
