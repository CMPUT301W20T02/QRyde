package com.example.qryde;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.IOException;

/**
 * This class describes the app activeity when a driver
 * scans a riders QRcode to transfer QRBucks
 * This class is not yet completed!!
 */

public class ScanQRCode extends AppCompatActivity{
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    String user;
    Boolean read = false;

    private FirebaseFirestore db;

    String TAG = "ScanQR";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);
        surfaceView =  findViewById(R.id.cameraView);
        textView = findViewById(R.id.txtContext);
        db = FirebaseFirestore.getInstance();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1928,1080)
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback(){
            /**
             * This creates the QR code
             * @param holder a SurfaceHolder object
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder){
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                    return;
                }
                try {
                    cameraSource.start(holder);
                }catch(IOException e){
                    e.printStackTrace();
                }


            }


            /**
             * This changes the format of the surface holder already generated to new specified
             * @param holder
             * @param format
             * @param width
             * @param height
             */
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
                View view = findViewById(R.id.qr_scanner);
                String message = "Scan QR code To earn QR Bucks";
                int duration = Snackbar.LENGTH_INDEFINITE;
                showSnackbar(view,message,duration);
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder){
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){
            @Override

            public void release(){

            }

            /**
             * This methed handles detecting and reading the QRcode and opens a new activity after the
             * QRcode is read
             * @param detections
             */
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections){
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if( (qrCodes.size() != 0) && (!read))
                {
                    textView.post(() -> {
                        String msg = qrCodes.valueAt(0).displayValue;
                        Log.d(TAG, "run:" + msg);
                        read = true;

                        Intent intent = new Intent(getApplicationContext(), DriverQRComplete.class);
                        intent.putExtra("iou", msg);
                        startActivity(intent);

                        finish();
                    });
                }
            }
        });


    }

    /**
     * method for showing snackbar message during camera view
     * @param view
     * @param message
     * @param duration
     */
    public void showSnackbar(View view, String message, int duration){
        Snackbar.make(view,message,duration).show();
    }


}
