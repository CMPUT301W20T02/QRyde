package com.example.qryde;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * Generates qr code by utilizing driver, rider and cost,
 * creates the qrcode string to send to api and calls imageDownloader function to get qrcode img
 */
public class GenerateQRCode extends AppCompatActivity {

    private String TAG = "GenerateQRCode";

    private ImageView imageView;
    private String driverName;
    private String driverUserName;
    private String rider;
    float amount;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        db = FirebaseFirestore.getInstance();

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            rider = incomingData.getString("rider");
            driverName = incomingData.getString("driver");
            amount = incomingData.getFloat("amount");
            driverUserName = incomingData.getString("driver_user_name");
        }

        String qrMsg = rider + " owes " + driverName + " $" + amount;

        imageView = findViewById(R.id.imageView);

        new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + qrMsg);


        //still figuring out how to transition from this activity to the RateDriver one, added this listener to test RateDriver for now
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RateDriver.class);
                intent.putExtra("driver", driverUserName);
                startActivity(intent);
            }
        });

        // add event listener for when the ActiveRide is completed and deleted from Firestore
        db.collection("ActiveRides").document(rider).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /**
             * when the ride is completed, set switch activity to generate a qrcode
             * @param documentSnapshot
             * @param e
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && !documentSnapshot.exists()) {
                    finish();
                }
            }
        });
    }
}
