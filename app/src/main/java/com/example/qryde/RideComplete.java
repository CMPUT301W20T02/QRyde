package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RideComplete extends AppCompatActivity {

    String TAG = "RideInProgress";


    FirebaseFirestore db;
    String user;
    String riderPicked;
    Button ScanButton;
    float amountOffered;
    String amountOfferedString;
    TextView amountOfferedTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_complete);
        Bundle incomingData = getIntent().getExtras();
        ScanButton = findViewById(R.id.scan_qr_button);
        amountOfferedTv = findViewById(R.id.qr_amount_text);


        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }

        amountOfferedString = "$"+amountOffered;
        amountOfferedTv.setText(amountOfferedString);

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
