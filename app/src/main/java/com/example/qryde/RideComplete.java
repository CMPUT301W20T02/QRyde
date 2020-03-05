package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RideComplete extends AppCompatActivity {

    String TAG = "RideInProgress";


    FirebaseFirestore db;
    String user;
    String riderPicked;
    Button ScanButton;
    int amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_complete);
        Bundle incomingData = getIntent().getExtras();
        ScanButton = findViewById(R.id.scan_qr_button);
        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
        }
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanQRCode.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });
    }

}
