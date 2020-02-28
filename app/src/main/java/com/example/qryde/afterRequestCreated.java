package com.example.qryde;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

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

    Button confirm;
    Button cancel;

    boolean isCancelDriver = false;

    String driver;

    int animationDuration = 100;

    ObjectAnimator findingBoxAnimationDown;
    ObjectAnimator findingTextAnimationDown;
    ObjectAnimator driverNameAnimationDown;
    ObjectAnimator driverRatingAnimationDown;
    ObjectAnimator confirmAnimationDown;
    ObjectAnimator cancelAnimationDown;

    ObjectAnimator findingBoxAnimationUp;
    ObjectAnimator findingTextAnimationUp;
    ObjectAnimator driverNameAnimationUp;
    ObjectAnimator driverRatingAnimationUp;
    ObjectAnimator confirmAnimationUp;
    ObjectAnimator cancelAnimationUp;



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

        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);

        findingBoxAnimationDown = ObjectAnimator.ofFloat(findingBox, "translationY", 1000f);
        findingBoxAnimationDown.setDuration(animationDuration);

        findingTextAnimationDown = ObjectAnimator.ofFloat(findingText, "translationY", 1000f);
        findingTextAnimationDown.setDuration(animationDuration);

        driverNameAnimationDown = ObjectAnimator.ofFloat(driverName, "translationY", 1000f);
        driverNameAnimationDown.setDuration(animationDuration);

        driverRatingAnimationDown = ObjectAnimator.ofFloat(driverRating, "translationY", 1000f);
        driverRatingAnimationDown.setDuration(animationDuration);

        confirmAnimationDown = ObjectAnimator.ofFloat(confirm, "translationY", 1000f);
        confirmAnimationDown.setDuration(animationDuration);

        cancelAnimationDown = ObjectAnimator.ofFloat(cancel, "translationY", 1000f);
        cancelAnimationDown.setDuration(animationDuration);

        findingBoxAnimationUp = ObjectAnimator.ofFloat(findingBox, "translationY", 0f);
        findingBoxAnimationUp.setDuration(animationDuration);

        findingTextAnimationUp = ObjectAnimator.ofFloat(findingText, "translationY", 0f);
        findingTextAnimationUp.setDuration(animationDuration);

        driverNameAnimationUp = ObjectAnimator.ofFloat(driverName, "translationY", 0f);
        driverNameAnimationUp.setDuration(animationDuration);

        driverRatingAnimationUp = ObjectAnimator.ofFloat(driverRating, "translationY", 0f);
        driverRatingAnimationUp.setDuration(animationDuration);

        confirmAnimationUp = ObjectAnimator.ofFloat(confirm, "translationY", 0f);
        confirmAnimationUp.setDuration(animationDuration);

        cancelAnimationUp = ObjectAnimator.ofFloat(cancel, "translationY", 0f);
        cancelAnimationUp.setDuration(animationDuration);

        confirmAnimationDown.start();


        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }

        View.OnClickListener declineDriverOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("AvailableRides").document(user)
                        .update("driver", "")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Successfully deleted document");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to delete document");
                            }
                        });

                db.collection("AvailableRides").document(user)
                        .update("status", true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Successfully deleted document");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to delete document");
                            }
                        });

            }
        };


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
                        cancelAnimationDown.start();


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
                                                                        cancel.setText(" DECLINE DRIVER ");

                                                                        isCancelDriver = true;

                                                                        findingBoxAnimationUp.start();
                                                                        findingTextAnimationUp.start();
                                                                        driverNameAnimationUp.start();
                                                                        driverRatingAnimationUp.start();
                                                                        confirmAnimationUp.start();
                                                                        cancelAnimationUp.start();


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

        View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isCancelDriver) {
                    db.collection("AvailableRides").document(user)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Successfully deleted document");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Failed to delete document");
                                }
                            });
                } else {
                    db.collection("AvailableRides").document(user)
                            .update("driver", "")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: Successfully deleted document");

                                    db.collection("AvailableRides").document(user)
                                            .update("status", false)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onSuccess: Successfully deleted document");

                                                    findingBoxAnimationDown.start();
                                                    findingTextAnimationDown.start();
                                                    driverNameAnimationDown.start();
                                                    driverRatingAnimationDown.start();
                                                    cancelAnimationDown.start();
                                                    confirmAnimationDown.start();


                                                    driverName.setText("");
                                                    driverRating.setText("");
                                                    findingText.setText("Finding you a driver ...");
                                                    cancel.setText("Cancel");

                                                    isCancelDriver = false;

                                                    findingBoxAnimationUp.start();
                                                    findingTextAnimationUp.start();
                                                    driverNameAnimationUp.start();
                                                    driverRatingAnimationUp.start();
                                                    cancelAnimationUp.start();




                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: Failed to delete document");
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Failed to delete document");
                                }
                            });

                }



            }
        };

        cancel.setOnClickListener(cancelOnClickListener);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get all of the information from AvailableRides to migrate to ActiveRides
                db.collection("AvailableRides")
                        .whereEqualTo("rider", user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String old_amount = document.getData().get("amount").toString();
                                        String old_datetime = document.getData().get("datetime").toString();
                                        String old_driverName = document.getData().get("driver").toString();
                                        String old_endLocation = document.getData().get("endLocation").toString();
                                        String old_startLocation = document.getData().get("startLocation").toString();
                                        String old_rider = document.getData().get("rider").toString();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("amount", Float.parseFloat(old_amount));
                                        data.put("datetime", old_datetime);
                                        data.put("driver", old_driverName);
                                        data.put("endLocation", old_endLocation);
                                        data.put("startLocation", old_startLocation);
                                        data.put("rider", old_rider);
                                        data.put("status", false);
                                        db.collection("ActiveRides").document(user).set(data);

                                        // now change the text to ride in progress
                                        findingBoxAnimationDown.start();
                                        findingTextAnimationDown.start();
                                        driverNameAnimationDown.start();
                                        driverRatingAnimationDown.start();
                                        cancelAnimationDown.start();
                                        confirmAnimationDown.start();


                                        findingText.setText("Ride is currently in progress");
                                        cancel.setText("Cancel");

                                        isCancelDriver = false;

                                        findingBoxAnimationUp.start();
                                        findingTextAnimationUp.start();
                                        driverNameAnimationUp.start();
                                        driverRatingAnimationUp.start();
                                        cancelAnimationUp.start();


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