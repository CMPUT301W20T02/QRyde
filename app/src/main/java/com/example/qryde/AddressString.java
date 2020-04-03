package com.example.qryde;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * This class converts a locations latitude and longitude information into an address string
 */


public class AddressString {

    private Activity activity;

    /**
     * This method gets the context  of the app activity for this class
     * @param activity
     */
    public AddressString(Activity activity) {
        this.activity = activity;

    }

    /**
     * This method deals with the conversion of latitude and longitude information into an address string that
     * the user can read and maybe identify
     * @param location contains the latitude and longitude location to be convertes
     * @return the address string representing the locations latitude and longitude information
     */
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
        Log.d("sdasdsa", returnedAddress);
        return returnedAddress;
        }
    }
