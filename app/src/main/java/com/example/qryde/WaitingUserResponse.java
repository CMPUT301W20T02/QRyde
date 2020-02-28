package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class WaitingUserResponse extends AppCompatActivity {

    String TAG = "WaitingUserResponse";


    FirebaseFirestore db;

    TextView tvStartLocation;
    TextView tvEndLocation;

    String stringEndLocation;
    String stringStartLcation;

    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_response);

        tvStartLocation = findViewById(R.id.startLocationText);
        tvEndLocation = findViewById(R.id.endLocationText);
        cancelButton = findViewById(R.id.cancel);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            stringStartLcation = incomingData.getString("START_LOCATION");
            stringEndLocation = incomingData.getString("END_LOCATION");

            tvStartLocation.setText(stringStartLcation);
            tvEndLocation.setText(stringEndLocation);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }
}
