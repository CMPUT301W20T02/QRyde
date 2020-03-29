package com.example.qryde;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapMarker {

    private MarkerPin markerPin;
    public void MapMarkerAdd(GoogleMap map, LatLng latLng, Activity activity, int drawable){
        markerPin = new MarkerPin();
        map.addMarker(new MarkerOptions().position(latLng).icon(markerPin.bitmapDescriptorFromVector(activity,drawable)));

    }
}
