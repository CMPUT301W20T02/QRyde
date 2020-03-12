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

    private DateFormat dateformat;

    private FirebaseFirestore db;

    private TextView summarytext;
    private EditText amount;
    private Button confirmButton;
    private Button cancelButton;

    private float amount_value = 0;

    private String user;
    private String pickupName;
    private String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_amount);

        db =  FirebaseFirestore.getInstance();
        summarytext = (TextView) findViewById(R.id.summary_text);
        amount = (EditText) findViewById(R.id.amount);
        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);

        setWindowSize();
        getRideInfo();

        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        summarytext.setText("The suggested Price is 20 QRbucks");
        amount.setText("");

        confirmButton();
        cancelButton();
    }

    private void confirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                Boolean status = false;

                HashMap<String, Object> data = new HashMap<>();
                if(amount.getText().toString().length() > 0){
                    amount_value = Float.valueOf(amount.getText().toString());
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
    }

    private void cancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setWindowSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, (height/9)*4);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    public void getRideInfo() {
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }
        Intent intent = getIntent();
        pickupName = intent.getStringExtra("pickup");
        destinationName = intent.getStringExtra("destination");
    }
}
