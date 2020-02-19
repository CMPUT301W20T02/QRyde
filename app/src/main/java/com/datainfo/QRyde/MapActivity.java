package com.datainfo.QRyde;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Boolean LocationPermission = false;
    private GoogleMap ActualMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MapActivity", "Map is ready" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();


    }
    private void MapInit() {
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
            }else {
                ActivityCompat.requestPermissions(this, permissions, 1515);
            }
        }
    }

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
    }
}

