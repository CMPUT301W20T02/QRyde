package com.example.qryde;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes the app activity while the rider is waiting
 * for the ride to be accepted
 */
public class AfterDriverSelects extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "WaitingUserResponse";

    private FirebaseFirestore db;

    private TextView tvStartLocation;
    private TextView tvEndLocation;
    private TextView boxTitleTextView;
    private TextView amountOfferedTv;


    private Button ScanButton;
    private String user;
    private GeoApiContext geoApiContext = null;
    private GeoPoint geoPoint;
    private GeoPoint geoPointDest;
    private Polyline polyline;
    private String riderPicked;
    private float amountOffered;
//    private Button cancelButton;

    private GoogleMap ActualMap;
    private MapMarker mapMarkerStart, mapMarkerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_driver_selects);

        tvStartLocation = findViewById(R.id.tvStartLocation);
        tvEndLocation = findViewById(R.id.tvEndLocation);
        boxTitleTextView = findViewById(R.id.box_title);
        amountOfferedTv = findViewById(R.id.qr_amount_text);
        ScanButton = findViewById(R.id.close_button);

        ScanButton.setVisibility(View.GONE);

//        cancelButton = findViewById(R.id.cancel);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }

        db = FirebaseFirestore.getInstance();
        db.collection("AvailableRides")
                .whereEqualTo("driver", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tvStartLocation.setText("From: " + document.getData().get("startLocation").toString());
                                tvEndLocation.setText("To: "+ document.getData().get("endLocation").toString());
                                geoPoint = document.getGeoPoint("LatLng");
                                geoPointDest = document.getGeoPoint("LatLngDest");
                                mapMarkerStart = new MapMarker();
                                mapMarkerEnd = new MapMarker();
                            }
                            MapInit();
                            calculateDirections();
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
                        boxTitleTextView.setText("Ride in Progress...");
                    }
                    if (documentSnapshot.getData().get("status").toString().equals("true")) {
                        boxTitleTextView.setText("Ride is Complete!");
                        String amountOfferedString = "$ " + amountOffered;
                        amountOfferedTv.setText(amountOfferedString);
                        ScanButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        finish();
                        }
                    }
                }
        });
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method starts a new app activity after the scan button is clicked
             * @param View
             * @return ScanQrCode.class             *
             */
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanQRCode.class);
                intent.putExtra("username", user);
                startActivity(intent);
                finish();
            }
        });


        db.collection("AvailableRides").document(riderPicked).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("driver").toString().equals("")){
                        finish();
                    }
                }
            }
        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    private void MapInit() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(AfterDriverSelects.this);
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
        googleMap.setPadding(0, 0, 0, 450);
        ActualMap = googleMap;
    }

    private void calculateDirections() {
        Log.d("Directions", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                geoPointDest.getLatitude(), geoPointDest.getLongitude()
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(false);
        directions.origin(new com.google.maps.model.LatLng(geoPoint.getLatitude(), geoPoint.getLongitude())
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }

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
            mapMarkerEnd.MapMarkerAdd(ActualMap, new LatLng(geoPointDest.getLatitude(), geoPointDest.getLongitude()), AfterDriverSelects.this, R.drawable.ic_place_black_24dp);
            mapMarkerStart.MapMarkerAdd(ActualMap, new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()), AfterDriverSelects.this, R.drawable.ic_person_pin_circle_black_24dp);
            polylineZoom(polyline.getPoints());
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
}
