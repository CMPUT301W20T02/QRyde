package com.example.qryde;

import android.animation.ObjectAnimator;
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

public class afterRequestCreated extends AppCompatActivity {
    String TAG = "temp";
    FirebaseFirestore db;

    TextView startLocation;
    TextView endLocation;
    String user;

    ImageView findingBox;
    TextView findingText;

    ImageView driverFoundBox;
    TextView driverName;
    TextView driverRating;

    String driver;

    int animationDuration = 60;

    ObjectAnimator findingBoxAnimationDown;
    ObjectAnimator findingTextAnimationDown;
    ObjectAnimator driverNameAnimationDown;
    ObjectAnimator driverRatingAnimationDown;

    ObjectAnimator findingBoxAnimationUp;
    ObjectAnimator findingTextAnimationUp;
    ObjectAnimator driverNameAnimationUp;
    ObjectAnimator driverRatingAnimationUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_temp);
        startLocation = findViewById(R.id.startLocationText);
        endLocation = findViewById(R.id.endLocationText);

        findingBox = findViewById(R.id.findingDriverBox);
        findingText = findViewById(R.id.findingText);

        driverFoundBox = findViewById(R.id.driverFoundBox);
        driverName = findViewById(R.id.driverName);
        driverRating = findViewById(R.id.driverRating);

        findingBoxAnimationDown = ObjectAnimator.ofFloat(findingBox, "translationY", 1000f);
        findingBoxAnimationDown.setDuration(animationDuration);

        findingTextAnimationDown = ObjectAnimator.ofFloat(findingText, "translationY", 1000f);
        findingTextAnimationDown.setDuration(animationDuration);

        driverNameAnimationDown = ObjectAnimator.ofFloat(driverName, "translationY", 1000f);
        driverNameAnimationDown.setDuration(animationDuration);

        driverRatingAnimationDown = ObjectAnimator.ofFloat(driverRating, "translationY", 1000f);
        driverRatingAnimationDown.setDuration(animationDuration);

        findingBoxAnimationUp = ObjectAnimator.ofFloat(findingBox, "translationY", 0f);
        findingBoxAnimationUp.setDuration(animationDuration);

        findingTextAnimationUp = ObjectAnimator.ofFloat(findingText, "translationY", 0f);
        findingTextAnimationUp.setDuration(animationDuration);

        driverNameAnimationUp = ObjectAnimator.ofFloat(driverName, "translationY", 0f);
        driverNameAnimationUp.setDuration(animationDuration);

        driverRatingAnimationUp = ObjectAnimator.ofFloat(driverRating, "translationY", 0f);
        driverRatingAnimationUp.setDuration(animationDuration);




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
                        findingBoxAnimationDown.start();
                        findingTextAnimationDown.start();
                        driverNameAnimationDown.start();
                        driverRatingAnimationDown.start();


                        db.collection("AvailableRides").whereEqualTo("rider", user).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                driver = document.getData().get("driver").toString();

                                                db.collection("Users").whereEqualTo("username", driver)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        driverName.setText(document.getData().get("name").toString());
                                                                        driverRating.setText(document.getData().get("thumbsUp").toString() + " | " + document.getData().get("thumbsDown").toString());
                                                                        findingText.setText("Driver found!");

                                                                        findingBoxAnimationUp.start();
                                                                        findingTextAnimationUp.start();
                                                                        driverNameAnimationUp.start();
                                                                        driverRatingAnimationUp.start();
                                                                    }
                                                                } else {
                                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                                }
                                                            }
                                                        });
                                            }

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });



                    }
                }
            }
        });
    }
}