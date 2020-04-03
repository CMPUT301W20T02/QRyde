package com.example.qryde;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Class Permissions requests the required permissions for the app to fully work
 *
 *
 */

public class Permissions {
    private boolean allPermissions;
    private Activity activity;

    /**
     * This method gets the context of the app activity that requires permissions
     * @param activity
     */
    public Permissions(Activity activity) {
        this.activity = activity;
    }

    /**
     * This method requests the permissions if the app doesn't have them
     */
    public void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    allPermissions = true;
                            }else {
                                ActivityCompat.requestPermissions(this.activity, permissions, 1);
                            }
                        }else {
                            ActivityCompat.requestPermissions(this.activity, permissions, 1);
                        }
                    }else {
                        ActivityCompat.requestPermissions(this.activity, permissions, 2);
                    }
                }else {
                    ActivityCompat.requestPermissions(this.activity, permissions, 3);
                }
            }else {
                ActivityCompat.requestPermissions(this.activity, permissions, 4);
            }
        }else {
            ActivityCompat.requestPermissions(this.activity, permissions, 4);
        }

    }

    /**
     * This app checks to see if the app has been granted all permissions by the system
     * @return true if permissions are granted
     */
    public boolean HasPermissions() {
        return allPermissions;
    }


}
