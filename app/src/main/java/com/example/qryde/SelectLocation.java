package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class SelectLocation extends AppCompatActivity {

    String TAG = "SelectLocation";


    FirebaseFirestore db;

    EditText pickupLocation;
    EditText destination;
    Button confirmButton;
    Button cancelButton;

    String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        db =  FirebaseFirestore.getInstance();
        pickupLocation = (EditText) findViewById(R.id.pickup);
        destination = (EditText) findViewById(R.id.destination);


        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);




        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pickupName = pickupLocation.getText().toString();
                final String destinationName = destination.getText().toString();
                if(pickupName.length() > 0 && destinationName.length() > 0){
                    Intent intent = new Intent(getApplicationContext(), ConfirmAmount.class);
                    intent.putExtra("pickup", pickupName);
                    intent.putExtra("destination", destinationName);
                    intent.putExtra("username", user);
                    startActivity(intent);


                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupLocation.setText("");
                destination.setText("");
            }
        });

    }
}
