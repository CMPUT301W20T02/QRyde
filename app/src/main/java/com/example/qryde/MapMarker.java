package com.example.qryde;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker {

    private MarkerPin markerPin;
    public void MapMarkerAdd(GoogleMap map, LatLng latLng, Activity activity){
        markerPin = new MarkerPin();
        map.addMarker(new MarkerOptions().position(latLng).icon(markerPin.bitmapDescriptorFromVector(activity,R.drawable.ic_place_black_24dp)));

    }
}
