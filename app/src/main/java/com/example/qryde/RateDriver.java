package com.example.qryde;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class RateDriver extends AppCompatActivity {
    private String TAG = "temp";
    private FirebaseFirestore db;

    private String driver;
    private ImageView thumbsUp;
    private ImageView thumbsDown;
    private Button rideCompleteButton;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);

        thumbsUp = findViewById(R.id.thumbsUpButton);
        thumbsDown = findViewById(R.id.thumbsDownButton);
        rideCompleteButton = findViewById(R.id.close_button);
        titleText = findViewById(R.id.ride_complete_text);

        db = FirebaseFirestore.getInstance();

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            driver = incomingData.getString("driver");
        }

        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("username", driver)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int newThumbsUpCount = parseInt(document.getData().get("thumbsUp").toString()) + 1;
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("thumbsUp", newThumbsUpCount);
                                        db.collection("Users").document(driver).update(data);
                                        thumbsUp.setVisibility(View.GONE);
                                        thumbsDown.setVisibility(View.GONE);
                                        titleText.setText("Thank your for rating!");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users").whereEqualTo("username", driver)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        int newThumbsDownCount = parseInt(document.getData().get("thumbsDown").toString()) + 1;
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("thumbsDown", newThumbsDownCount);
                                        db.collection("Users").document(driver).update(data);
                                        thumbsUp.setVisibility(View.GONE);
                                        thumbsDown.setVisibility(View.GONE);
                                        titleText.setText("Thank your for rating!");
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

    }

}

