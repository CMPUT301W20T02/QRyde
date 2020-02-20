package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SelectLocation extends AppCompatActivity {

    String TAG = "SelectLocation";
    DateFormat dateformat;

    FirebaseFirestore db;

    EditText pickupLocation;
    EditText destination;
    EditText amount;
    Button confirmButton;
    Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        db =  FirebaseFirestore.getInstance();
        pickupLocation = (EditText) findViewById(R.id.pickup);
        destination = (EditText) findViewById(R.id.destination);
        amount = (EditText) findViewById(R.id.amount);

        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);

        dateformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pickupName = pickupLocation.getText().toString();
                final String destinationName = destination.getText().toString();
//                Intent intent = new Intent(getApplicationContext(), ConfirmAmount.class);
//                intent.putExtra("pickup", pickupName);
//                intent.putExtra("destination", destinationName);
//                startActivity(intent);
                Date date = new Date();
                Boolean status = false;
                String username = "Brian";
                String driver = "Driver";
                float amount_value = Float.valueOf(amount.getText().toString());
                HashMap<String, Object> data = new HashMap<>();
                if(pickupName.length() > 0 && destinationName.length() > 0){
                    data.put("amount", amount_value);
                    data.put("datetime", dateformat.format(date));
                    data.put("driver", driver);
                    data.put("endLocation", destinationName);
                    data.put("rider", username);
                    data.put("startLocation", pickupName);
                    data.put("status", status);
                    db.collection("AvailableRides").document(username).set(data);


                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupLocation.setText("");
                destination.setText("");
                amount.setText("");

            }
        });

    }
}
