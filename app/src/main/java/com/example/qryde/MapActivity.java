package com.example.qryde;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //initialization of variables
    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private AutocompleteSupportFragment autocompleteSupportFragment, autocompleteSupportFragmentdest;
    private GeoApiContext geoApiContext = null; //for directions api
    private Place startPos, endPos;
    private Polyline polyline;
    private View mapView;

    private TextView distanceView;
    private TextView durationView;
    private TextView costView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        //Finding views from layout files
        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragmentdest = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentdes);

        distanceView = findViewById(R.id.distance);
        durationView = findViewById(R.id.time);
        costView = findViewById(R.id.cost);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragmentdest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        //initializing countries to search from and adding hints to the search bar
        autocompleteSupportFragment.setCountries("CA"); // sets for now the location for autocomplete
        autocompleteSupportFragment.setHint("Current Location");
        autocompleteSupportFragmentdest.setHint("Enter a Destination");
        autocompleteSupportFragmentdest.setCountries("CA"); //sets for now the location for autocomplete

        //Getting permission to access location from the user
        getLocationPermission();
    }

    //converting a location to an address
    private String getCompleteAddressString(Location location) {
        String returnedAddress = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //catching for null locations
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null) {
                returnedAddress = addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnedAddress;
    }

    //creates map fragment
    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(MapActivity.this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    //getting permissions to access location of the device from the user
    //if permission is granted current user location is accessed
    private void getLocationPermission() {
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

    //requests the location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermission = false;
        if (requestCode == 1515) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    LocationPermission = false;
                    return;
                }
            }
            LocationPermission = true;
            MapInit();
        }
    }

    //getting the google map ready once location is permitted
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setPadding(0,310,0,0);
        ActualMap = googleMap;
        if (LocationPermission) {
            updateLocationUI();
            DeviceLocation();
            searchInit();

            // adds destination marker and sets locationend latitude longitude, sets destination text
            ActualMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    Location tempEndLocation = new Location("");
                    tempEndLocation.setLatitude(point.latitude);
                    tempEndLocation.setLongitude(point.longitude);
                    autocompleteSupportFragmentdest.setText(String.format("%s", getCompleteAddressString((tempEndLocation))));
                }
            });
        }
    }

    //getting the current GPS location of the user and setting that as the current location
    private void DeviceLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermission) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locationCurr = (Location) task.getResult();
                        autocompleteSupportFragment.setText(String.format("%s", getCompleteAddressString((Location) task.getResult())));
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

    private void mapMove(LatLng latLng, float zoom) {
        //method for map camera movement
        ActualMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), 600, null);
    }

    //shows the blue dot on the map as the current GPs location of the user
    private void updateLocationUI() {
        if (ActualMap == null) {
            return;
        }
        try {
            if (LocationPermission) {
                ActualMap.setMyLocationEnabled(true);
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (polyline !=null) polyline.remove();
                        startPos = null;
                        autocompleteSupportFragment.setText(String.format("%s", getCompleteAddressString(locationCurr)));
                        calculateDirections();
                    }
                });

                ActualMap.getUiSettings().setMyLocationButtonEnabled(true);
                ActualMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                ActualMap.setMyLocationEnabled(false);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationCurr = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    //searching for a location to route to using the autocomplete API methods provided by Google
    private void searchInit() {
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
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

        //clearing the user input in the autocomplete search fragment when clicked on
        Objects.requireNonNull(autocompleteSupportFragment.getView()).findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(v -> {
            startPos = null;
            autocompleteSupportFragment.setText("");
            if (endPos != null) {
                calculateDirections();
            }
        });
        autocompleteSupportFragmentdest.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polyline.remove();
                endPos = null;
                if (startPos == null) {
                    mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()), 15f);
                    autocompleteSupportFragmentdest.setText("");
                }
                else {
                    mapMove(startPos.getLatLng(),15f);
                    autocompleteSupportFragmentdest.setText("");
                }

            }
        });

        //routing from the addresses provided in the fragments on click
        autocompleteSupportFragmentdest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                endPos = place;
                calculateDirections();
            }

            //error on failure to route
            @Override
            public void onError(@NonNull Status status) {
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //method to add the best possible route from one point entered to another
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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Directions", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("Directions", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("Directions", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("Directions", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                //adding the route calculated to the map
                addPolylinesToMap(result);

                double seconds = (double) result.routes[0].legs[0].duration.inSeconds;
                double minutes = roundUp(seconds/60, 1);

                double kilometres = result.routes[0].legs[0].distance.inMeters;
                kilometres = roundUp(kilometres/1000, 2);

                double cost = costCalculator(minutes, kilometres);
                cost = roundUp(cost, 2);

                //displaying the variables calculated onto the activity
                distanceView.setText(String.format("Distance: %s km", kilometres));
                durationView.setText(String.format("Time: %s minutes", minutes));
                costView.setText(String.format("Cost: $%s", cost));
            }

            //throwing an error on failure to route
            @Override
            public void onFailure(Throwable e) {
                Log.e("Directions", "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }

    //adding the route polylines to the map
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addPolylinesToMap(final DirectionsResult result){
        // for main thread
        new Handler(Looper.getMainLooper()).post(() -> {

            //getting best route only, so only one route
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(result.routes[0].overviewPolyline.getEncodedPath());
            List<LatLng> newDecodedPath = new ArrayList<>();

            //for loop goes through several lat/log to make the route. Array holds several lat/longs
            for(com.google.maps.model.LatLng latLng: decodedPath){
                newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
            }
            Log.d("addPolylinesToMap", "run: leg: " + decodedPath.get(0).toString());

            polyline = ActualMap.addPolyline(new PolylineOptions()
                    .addAll(newDecodedPath)
                    .color(getColor(R.color.QrydeB)));

            polylineZoom(polyline.getPoints());
        });
    }

    //this is for animating camera to zoom out or in to the route size
    public void polylineZoom(List<LatLng> lstLatLngRoute) {

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

    //calculating the cost for a ride using time and distance
    public double costCalculator(double minutes, double distance)
    {
        double baseCost = 2.00;
        double minimumFare = 4.00;

        double perKm = 0.85;
        double perMinute = 0.25;

        return (baseCost + minimumFare + (minutes*perMinute) + (distance*perKm));
    }

    //rounding up decimal numbers to a precision point
    public double roundUp(double number, int precision)
    {
        BigDecimal bd = new BigDecimal(number).setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}