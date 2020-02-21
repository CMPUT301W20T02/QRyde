package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_amount);

        db =  FirebaseFirestore.getInstance();
        summarytext = (TextView) findViewById(R.id.summary_text);
        amount = (EditText) findViewById(R.id.amount);
        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);

        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        Intent intent = getIntent();
        final String pickupName = intent.getStringExtra("pickup");
        final String destinationName = intent.getStringExtra("destination");

        summarytext.setText("Trip from " + pickupName + " to " + destinationName + ".\n The suggested Price is 20 QRbucks.");
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                Boolean status = false;
                String username = "Brian";
                String driver = "Driver";
                float amount_value = Float.valueOf(amount.getText().toString());
                HashMap<String, Object> data = new HashMap<>();
                if(amount.getText().toString().length() > 0){
                    data.put("amount", amount_value);
                    data.put("datetime", dateformat.format(date));
                    data.put("driver", driver);
                    data.put("endLocation", destinationName);
                    data.put("rider", username);
                    data.put("startLocation", pickupName);
                    data.put("status", status);
                    db.collection("AvailableRides").document(username).set(data);

                    Intent intent3 = new Intent(getApplicationContext(), WaitPage.class);
                    startActivity(intent3);


                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), SelectLocation.class);
                startActivity(intent2);
            }
        });
    }
}
