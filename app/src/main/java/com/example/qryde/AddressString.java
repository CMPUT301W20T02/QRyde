package com.example.qryde;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;
import java.util.Locale;

public class AddressString {

    private Activity activity;

    public AddressString(Activity activity) {
        this.activity = activity;
    }

    public String getCompleteAddressString(Location location) {
        //converting a location to an address
        String returnedAddress = "";
        Geocoder geocoder = new Geocoder(this.activity, Locale.getDefault());

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
    }
