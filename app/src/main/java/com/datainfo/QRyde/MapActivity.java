package com.datainfo.QRyde;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private AutocompleteSupportFragment autocompleteSupportFragment, autocompleteSupportFragmentdest;
    private GeoApiContext geoApiContext = null; //for directions api
    private Place startPos, endPos;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragmentdest = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentdes);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragmentdest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteSupportFragment.setText(String.format("%s", getCompleteAddressString(locationCurr)));
        autocompleteSupportFragment.setCountries("CA", "US"); // sets for now the location for autocomplete
        autocompleteSupportFragmentdest.setHint("Enter a Destination");
        autocompleteSupportFragmentdest.setCountries("CA", "US"); //sets for now the location for autocomplete
//        PlacesClient placesClient = Places.createClient(this);


    }

    private String getCompleteAddressString(Location location) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private void MapInit() { //creates map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
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
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locationCurr = (Location) task.getResult();
                        mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()), 15f);

                    } else {
                        mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude), 15f);
                        Toast.makeText(MapActivity.this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                        ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
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
                ActualMap.getUiSettings().setZoomControlsEnabled(true);
                ActualMap.getUiSettings().setCompassEnabled(true);
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

                startPos = place;
                mapMove(place.getLatLng(),15f);
                if (endPos != null) {
                    calculateDirections();
                }
            }
            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });

        autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(v -> {
            startPos = null;
            autocompleteSupportFragment.setText("");
            if (endPos != null) {
                calculateDirections();
            }
        });

        autocompleteSupportFragmentdest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                endPos = place;
                calculateDirections();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDirections() {
        Log.d("Directions", "calculateDirections: calculating directions.");

        if (polyline !=null) { //removes a poly line if exists
            polyline.remove();
        }
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                endPos.getLatLng().latitude,
                endPos.getLatLng().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        if (startPos == null) {
            directions.origin(new com.google.maps.model.LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()));
        } else {
            directions.origin(new com.google.maps.model.LatLng(startPos.getLatLng().latitude,startPos.getLatLng().longitude));
        }

        Log.d("Directions", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Directions", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("Directions", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("Directions", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("Directions", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                //Toast.makeText(MapActivity.this, "Invalid Route", Toast.LENGTH_SHORT).show();
                Log.e("Directions", "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){ //draws the polylines
        new Handler(Looper.getMainLooper()).post(() -> { // for main thread
            //getting best route only, so only one route
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(result.routes[0].overviewPolyline.getEncodedPath());
            List<LatLng> newDecodedPath = new ArrayList<>();
            for(com.google.maps.model.LatLng latLng: decodedPath){ //for loop goes through several lat/log to make the route. Array holds several lat/longs
                newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
            }
            Log.d("addPolylinesToMap", "run: leg: " + decodedPath.get(0).toString());
            polyline = ActualMap.addPolyline(new PolylineOptions()
                    .addAll(newDecodedPath)
                    .color(getColor(R.color.QrydeB)));
            //Polyline polyline = ActualMap.addPolyline(new PolylineOptions().add(new LatLng(result.routes[0].legs[0].endLocation.lat, result.routes[0].legs[0].endLocation.lng )));//.addAll(newDecodedPath));
            //polyline.setClickable(true);
            polylineZoom(polyline.getPoints());
        });
    }
    public void polylineZoom(List<LatLng> lstLatLngRoute) { //this is for animating camera to zoom out or in to the route size

        if (ActualMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        ActualMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }
}

