package com.example.qryde;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    public boolean allPermissions;
    private Activity activity;

    public Permissions(Activity activity) {
        this.activity = activity;
    }
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
        }
        else {
            ActivityCompat.requestPermissions(this.activity, permissions, 4);
        }

    }
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d("LOLS", String.valueOf(allPermissions));
//        if (requestCode == 1 | requestCode == 2 | requestCode == 3 | requestCode == 4) {
//            for (int grantResult : grantResults) {
//                if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                    allPermissions = false;
//                    return;
//                }
//            }
//            allPermissions = true;
//        }
//    }

    public boolean HasPermissions() {
        return allPermissions;
    }


}
