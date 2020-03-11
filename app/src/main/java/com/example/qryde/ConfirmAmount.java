package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ConfirmAmount extends AppCompatActivity {

    DateFormat dateformat;

    FirebaseFirestore db;

    TextView summarytext;
    EditText amount;
    Button confirmButton;
    Button cancelButton;
//
//    TextView start;
//    TextView end;

    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_amount);

        db =  FirebaseFirestore.getInstance();
        summarytext = (TextView) findViewById(R.id.summary_text);
        amount = (EditText) findViewById(R.id.amount);
        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);

//        start = findViewById(R.id.startLocationText);
//        end = findViewById(R.id.endLocationText);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, (height/9)*4);
        getWindow().setGravity(Gravity.BOTTOM);

        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        Intent intent = getIntent();
        final String pickupName = intent.getStringExtra("pickup");
        final String destinationName = intent.getStringExtra("destination");

        summarytext.setText("The suggested Price is 20 QRbucks");
        summarytext.bringToFront();
//        start.setText(pickupName);
//        end.setText(destinationName);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                Boolean status = false;
                float amount_value = Float.valueOf(amount.getText().toString());
                HashMap<String, Object> data = new HashMap<>();
                if(amount.getText().toString().length() > 0){
                    data.put("amount", amount_value);
                    data.put("datetime", dateformat.format(date));
                    data.put("driver", "");
                    data.put("endLocation", destinationName);
                    data.put("rider", user);
                    data.put("startLocation", pickupName);
                    data.put("status", status);
                    db.collection("AvailableRides").document(user).set(data);

                    Intent intent3 = new Intent(getApplicationContext(), afterRequestCreated.class);
                    intent3.putExtra("username", user);
                    startActivity(intent3);
                    finish();


                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), SelectLocation.class);
                startActivity(intent2);
                finish();
            }
        });


    }
}
