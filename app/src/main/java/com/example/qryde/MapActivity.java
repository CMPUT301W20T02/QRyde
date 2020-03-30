package com.example.qryde;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class Map Activity creates the map fragment and has all of the functionality for the map shown to
 * user, it's linked to firebase to get driver info and transfer price.
 *
 *
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    //initialization of variables
    private GoogleMap ActualMap;
    private Location locationCurr;
    private final LatLng EarthDefaultLocation = new LatLng(0, 0); //just center of earth
    private AutocompleteSupportFragment autocompleteSupportFragment, autocompleteSupportFragmentdest;
    private GeoApiContext geoApiContext = null; //for directions api
    private Place startPos, endPos;
    private Polyline polyline;
    private View mapView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AddressString addressString;
    private MarkerPin markerPin;
    private MapMarker mapMarker, mapMarkerStart;
    private RideCalculator rideCalculator;

    private TextView usernameView;
    private TextView distanceView;
    private TextView durationView;
    private TextView costView;
    private Button markerBut;

    private double rideCost;
    private double rideDuration;
    private double rideDistance;

    Location latlngtotempEndLocation = new Location("");
    Location endPostotempEndLocation = new Location("");

    private String user;
    private boolean perms;
    private String pickupName;
    private String destinationName;
    private ImageView logo, rideLiner;
    private TextView logorequest;
    private LinearLayout rideCalLay;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView  = (NavigationView) findViewById(R.id.rider_nav_view);
        usernameView = findViewById(R.id.username_hamb);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.username_hamb);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        //Finding views from layout files
        autocompleteSupportFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragmentdest = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragmentdes);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteSupportFragmentdest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        //initializing countries to search from and adding hints to the search bar
        autocompleteSupportFragment.setCountries("CA"); // sets for now the location for autocomplete
        autocompleteSupportFragment.setHint("Pickup Location");
        autocompleteSupportFragmentdest.setHint("Where to?");
        autocompleteSupportFragmentdest.setCountries("CA"); //sets for now the location for autocomplete

        addressString = new AddressString(this);
        mapMarker = new MapMarker();
        mapMarkerStart = new MapMarker();
        markerPin = new MarkerPin();

        rideLiner = findViewById(R.id.rideline);
        rideCalLay = findViewById(R.id.rideCal);
        distanceView = findViewById(R.id.distance);
        durationView = findViewById(R.id.time);
        costView = findViewById(R.id.cost);

        // Getting username from logon activity
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            perms = incomingData.getBoolean("permissions");
            Log.d("LOL", String.valueOf(perms));
        }
        //checks if location perms are there
        if (perms) {
            MapInit();
        }
        markerBut = findViewById(R.id.MarkerDrop);
        markerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDirections();
            }
        });
        navUsername.setText(user);
        logo = findViewById(R.id.qryde_logo);
        logorequest = findViewById(R.id.request_text);
        logo.setOnClickListener(new View.OnClickListener() {
            /**
             * When the logo is clicked, it goes to the confirm amount activity to confirm amount
             * rider is willing to spend. It provides username, pickup location,
             * and name of destination to the confirmAmount activity
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfirmAmount.class);
                intent.putExtra("username", user);
                intent.putExtra("pickup", pickupName);
                intent.putExtra("destination", destinationName);
                intent.putExtra("ride_cost", rideCost);
                intent.putExtra("ride_distance", rideDistance);
                intent.putExtra("ride_duration", rideDuration);

                startActivity(intent);
            }
        });

        ImageButton navigationDrawer = (ImageButton) findViewById(R.id.hamburger_menu_button);
        navigationDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    //creates map fragment
    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(MapActivity.this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }

    /**
     * getting the google map ready once location is permitted
     * starts:
     * updateLocation UI
     * DeviceLocation
     * SearchInitializer
     * mapClicker
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 450, 0, 0);
        ActualMap = googleMap;
        if (perms) {
            updateLocationUI();
            DeviceLocation();
            searchInit();
            mapClicker();
        }
    }

    // adds destination marker and sets location end latitude longitude, sets destination text
    private void mapClicker() {
        ActualMap.setOnMapClickListener(point -> {
            if (endPos == null) {
                ActualMap.clear();
                markerBut.setVisibility(View.VISIBLE);
                latlngtotempEndLocation = new Location("");
                latlngtotempEndLocation.setLatitude(point.latitude);
                latlngtotempEndLocation.setLongitude(point.longitude);
                ActualMap.addMarker(new MarkerOptions().position(point).icon(markerPin.bitmapDescriptorFromVector(this, R.drawable.ic_place_black_24dp)));
                destinationName = addressString.getCompleteAddressString(latlngtotempEndLocation);
                autocompleteSupportFragmentdest.setText(String.format("%s", destinationName));
            }
        });
    }

    //getting the current GPS location of the user and setting that as the current location
    private void DeviceLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (perms) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        locationCurr = (Location) task.getResult();
                        Log.d("test", "TESTING PICKUPNAME22" + addressString.getCompleteAddressString(locationCurr));
                        autocompleteSupportFragment.setText(String.format("%s", addressString.getCompleteAddressString(locationCurr)));
                        pickupName = addressString.getCompleteAddressString(locationCurr);
                        if (endPos == null) {
                            mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()));
                        }

                    } else {
                        mapMove(new LatLng(EarthDefaultLocation.latitude, EarthDefaultLocation.longitude));
                        Toast.makeText(MapActivity.this, "Could not find your location.", Toast.LENGTH_SHORT).show();
                        ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    //moves the map camera
    private void mapMove(LatLng latLng) {
        ActualMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 15.0), 600, null);
    }

    //shows the blue dot on the map as the current GPs location of the user
    private void updateLocationUI() {
        if (ActualMap == null) {
            return;
        }
        try {
            if (perms) {
                ActualMap.setMyLocationEnabled(true);
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                locationButton.setOnClickListener(new View.OnClickListener() {
                    /**
                     * On Click of the location button it resets starting position,
                     * gets the current location and then calls method to calculate directions
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        ActualMap.clear();
                        DeviceLocation();
                        if (endPos != null || latlngtotempEndLocation !=null) {
                            startPos = null;
                            calculateDirections();
                        }
                    }
                });
                ActualMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                ActualMap.setMyLocationEnabled(false);
                ActualMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationCurr = null;
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    //searching for a location to route to using the autocomplete API methods provided by Google
    private void searchInit() {
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            /**
             * When place is selected it sets starting position to the selected place,
             * if the ending position is not empty then it calculates the directions to the place
             * @param place
             */
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i("AutoComplete", "Place: " + place.getName() + ", " + place.getId() + place.getLatLng());

                startPos = place;
                mapMove(place.getLatLng());
                if (endPos != null || latlngtotempEndLocation !=null) {
                    calculateDirections();
                }
            }

            /**
             * on error, it prints the error status onto the log,
             * then a message is displayed that it couldn't find the place
             * @param status
             */
            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });

        //clearing the user input in the autocomplete search fragment when clicked on
        Objects.requireNonNull(autocompleteSupportFragment.getView()).findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(v -> {
            logo.setVisibility(View.GONE);
            logorequest.setVisibility(View.GONE);
            rideLiner.setVisibility(View.GONE);
            rideCalLay.setVisibility(View.GONE);
            startPos = null;
            autocompleteSupportFragment.setText("");
            if (endPos != null || latlngtotempEndLocation != null) {
                calculateDirections();
            }
        });
        autocompleteSupportFragmentdest.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
            /**
             * when the clear button is pressed,
             * it clears the ending position,
             * if starting position is not set: moves the map to the current position of the user
             * if the starting position is filled: it moves the map to the set starting position
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                logo.setVisibility(View.GONE);
                logorequest.setVisibility(View.GONE);
                markerBut.setVisibility(View.GONE);
                rideLiner.setVisibility(View.GONE);
                rideCalLay.setVisibility(View.GONE);
                ActualMap.clear();
                endPos = null;
                if (startPos == null) {
                    mapMove(new LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()));
                    autocompleteSupportFragmentdest.setText("");
                } else {
                    mapMove(startPos.getLatLng());
                    autocompleteSupportFragmentdest.setText("");
                }

            }
        });

        //routing from the addresses provided in the fragments on click
        autocompleteSupportFragmentdest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            /**
             * when a destination is selected:
             * set the end position to the place selected, get the coordinates,
             * set long and lat to the the temp end location,
             * get the name of the destination
             * @param place
             */
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                ActualMap.clear();
                latlngtotempEndLocation = null;
                endPos = place;
                endPostotempEndLocation.setLatitude(endPos.getLatLng().latitude);
                endPostotempEndLocation.setLongitude(endPos.getLatLng().longitude);
                destinationName = addressString.getCompleteAddressString(endPostotempEndLocation);
                calculateDirections();
            }

            /**
             * error on failure to route:
             * on error, it prints the error status onto the log,
             * then a message is displayed that it couldn't find the place
             * @param status
             */

            @Override
            public void onError(@NonNull Status status) {
                Log.i("AutoComplete", "An error occurred: " + status);
                Toast.makeText(MapActivity.this, "Couldn't find place.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //method to add the best possible route from one point entered to another
    private void calculateDirections() {

        LatLng endPosLatLng;
        if (latlngtotempEndLocation != null) {
            endPosLatLng = new LatLng(latlngtotempEndLocation.getLatitude(),latlngtotempEndLocation.getLongitude());
        } else {
            endPosLatLng = new LatLng(endPos.getLatLng().latitude, endPos.getLatLng().longitude);
        }
        ActualMap.clear();

        mapMarker.MapMarkerAdd(ActualMap,endPosLatLng, MapActivity.this, R.drawable.ic_place_black_24dp);
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(endPosLatLng.latitude, endPosLatLng.longitude);

        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        if (startPos == null) {
            directions.origin(new com.google.maps.model.LatLng(locationCurr.getLatitude(), locationCurr.getLongitude()));
        } else {
            mapMarkerStart.MapMarkerAdd(ActualMap,startPos.getLatLng(), MapActivity.this, R.drawable.ic_person_pin_circle_black_24dp);
            directions.origin(new com.google.maps.model.LatLng(startPos.getLatLng().latitude, startPos.getLatLng().longitude));
        }

        Log.d("Directions", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            /**
             * Gets the distance, duration to location and call the cost calculator,
             * sets all of them to their corresponding text to display in view
             * all from the result of the route
             * @param result
             */
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Directions", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("Directions", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("Directions", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("Directions", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                //adding the route calculated to the map
                rideCalculator = new RideCalculator(result);
                addPolylinesToMap(result);

                //calculating the route variables
                rideCost = rideCalculator.getCost();
                rideDuration = rideCalculator.getMinutes();
                rideDistance = rideCalculator.getKilometres();

                distanceView.setText(String.format("Distance: %s km", Math.round(rideDistance)));
                durationView.setText(String.format("Time: %s mins", Math.round(rideDuration)));
                costView.setText(String.format("Cost: $%s", rideCost));
            }


            /**
             * throws an error on failure to route
             * @param e
             */
            @Override
            public void onFailure(Throwable e) {
                Log.e("Directions", "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }

    //adding the route polylines to the map
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addPolylinesToMap(final DirectionsResult result) {
        // for main thread
        new Handler(Looper.getMainLooper()).post(() -> {

            //getting best route only, so only one route
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(result.routes[0].overviewPolyline.getEncodedPath());
            List<LatLng> newDecodedPath = new ArrayList<>();

            //for loop goes through several lat/log to make the route. Array holds several lat/longs
            for (com.google.maps.model.LatLng latLng : decodedPath) {
                newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
            }
            Log.d("addPolylinesToMap", "run: leg: " + decodedPath.get(0).toString());

            polyline = ActualMap.addPolyline(new PolylineOptions()
                    .addAll(newDecodedPath)
                    .color(getColor(R.color.QrydeB)));

            markerBut.setVisibility(View.GONE);
            polylineZoom(polyline.getPoints());
            logo.setVisibility(View.VISIBLE);
            logorequest.setVisibility(View.VISIBLE);
            rideLiner.setVisibility(View.VISIBLE);
            rideCalLay.setVisibility(View.VISIBLE);
        });
    }

    /**
     * animates camera to zoom out or in to the route size
     *
     * @param lstLatLngRoute
     */
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


    /**
     * Allows users to navigate to user profile and QR Wallet
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile: {
                Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                intent.putExtra("username", user);
                startActivity(intent);
                Log.d("xd", "xd");
                break;
            }

            case R.id.nav_qr_wallet: {
                break;
            }
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }


}
