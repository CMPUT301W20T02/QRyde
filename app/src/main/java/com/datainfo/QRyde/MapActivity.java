package com.datainfo.QRyde;
import android.location.Location;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private AutocompleteSupportFragment autocompleteSupportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        PlacesClient placesClient = Places.createClient(this);

        getLocationPermission();
    }
    private void MapInit() { //creates map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        //checks for location permissions on phone
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                LocationPermission = true;
                MapInit();
            }else {
                ActivityCompat.requestPermissions(this, permissions, 1515);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, 1515);
        }
    }

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
            searchInit();

        }
    }

    private void DeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermission) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            locationCurr = (Location) task.getResult();
                            mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()), 15f);

                        } else {
                            mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude), 15f);
                            Toast.makeText(MapActivity.this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                            ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void mapMove(LatLng latLng, float zoom) { //method for map camera movement
        ActualMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void updateLocationUI() { //shows the blue dot. 
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
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void searchInit() {

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("AutoComplete", "Place: " + place.getName() + ", " + place.getId() + place.getLatLng());
                mapMove(place.getLatLng(),15f);
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

