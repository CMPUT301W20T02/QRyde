package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

/**
 * This class deals with the app activity while the ride is in progress.
 */
public class RideInProgress extends AppCompatActivity {

    private String TAG = "RideInProgress";

    private FirebaseFirestore db;
    private String user;
    private String riderPicked;
    float amountOffered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);
        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
            riderPicked = incomingData.getString("rider");
            amountOffered = incomingData.getFloat("amount");
        }

        db = FirebaseFirestore.getInstance();

        db.collection("ActiveRides").document(riderPicked).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            /**
             * adds the current ride to the firebase collection "Active Rides"
             * @param DocumentSnapshot
             * @param FirebaseException Exception to be thrown if error occurs
             */
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("status").toString().equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), RideComplete.class);
                        intent.putExtra("rider", riderPicked);
                        intent.putExtra("user", user);
                        intent.putExtra("amount", amountOffered);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

}
