package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class describes the app activity when the after the user logs in. It currently displays a
 * map image and a clickable logo that takes the user to the select location page
 */

public class userMainMap extends AppCompatActivity {
    private FirebaseFirestore db;
    private String user;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_map);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        db = FirebaseFirestore.getInstance();

        logo = findViewById(R.id.qryde_logo);




        logo.setOnClickListener( new View.OnClickListener() {
            @Override
            /**
             * This method allows the user select a button and takes them to the
             * SelectLocation activity
             * @param v View object to be clicked
             * @return SelectLocation.class
             */
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectLocation.class);
                intent.putExtra("username", user);
                startActivity(intent);
            }
        });


    }
}
