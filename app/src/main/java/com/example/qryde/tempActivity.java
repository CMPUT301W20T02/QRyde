package com.example.qryde;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class tempActivity extends AppCompatActivity {

    String TAG = "temp";
    FirebaseFirestore db;

    TextView startLocation;
    TextView endLocation;
    String user;

    ImageView findingBox;
    TextView findingText;

    ImageView driverFoundBox;
    TextView driverFoundText;
    TextView driverName;
    TextView driverRating;

    String driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        startLocation = findViewById(R.id.startLocationText);
        endLocation = findViewById(R.id.endLocationText);

        findingBox = findViewById(R.id.findingDriverBox);
        findingText = findViewById(R.id.findingText);

        driverFoundBox = findViewById(R.id.driverFoundBox);
        driverFoundText = findViewById(R.id.findingDriverText);
        driverName = findViewById(R.id.driverName);
        driverRating = findViewById(R.id.driverRating);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        db = FirebaseFirestore.getInstance();

        db.collection("AvailableRides")
            .whereEqualTo("rider", user)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            startLocation.setText(document.getData().get("startLocation").toString());
                            endLocation.setText(document.getData().get("endLocation").toString());

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

        db.collection("AvailableRides").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("status").toString().equals("true")) {
                        findingBox.setVisibility(View.GONE);
                        findingText.setVisibility(View.GONE);

                        driverFoundBox.setVisibility(View.VISIBLE);
                        driverFoundText.setVisibility(View.VISIBLE);
                        driverName.setVisibility(View.VISIBLE);
                        driverRating.setVisibility(View.VISIBLE);

                        db.collection("AvailableRides").whereEqualTo("rider", user).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            driver = document.getData().get("driver").toString();
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                        Log.d(TAG, "onEvent: " + driver);



                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }

            }
        });
    }
}
