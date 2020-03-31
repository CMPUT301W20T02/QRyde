package com.example.qryde;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ObjectStreamException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * class for functions after request has been created, includes cancel button, ride status listener,
 * confirm button, decline driver button, and active ride converter, linked to firebase
 */
public class afterRequestCreated extends AppCompatActivity {
    private String TAG = "temp";
    private FirebaseFirestore db;

    private TextView startLocation;
    private TextView endLocation;
    private String user;

    private ImageView findingBox;
    private TextView findingText;

    private ImageView driverFoundBox;
    private TextView driverName;
    private TextView driverRating;
    private TextView email;

    private Button confirm;
    private Button cancel;
    private float amount;

    private TextView phoneNumber;

    private boolean isCancelDriver = false;

    private String driver;

    private int animationDuration = 100;
    private static final int REQUEST_CALL = 1;


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
        phoneNumber = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);

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

        db = FirebaseFirestore.getInstance();
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        driverName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getUserInfo();
            }
        });


//        db.collection("AvailableRides")
//                .whereEqualTo("rider", user)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                startLocation.setText(document.getData().get("startLocation").toString());
////                                endLocation.setText(document.getData().get("endLocation").toString());
//
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
        setWindowSize();
        rideStatusListener();
        declineDriverButton();
        cancelButton();
        confirmButton();
        activeRideConverter();
    }

    private void activeRideConverter() {
        // listening for when activeRideRequest is changed to true
        db.collection("ActiveRides").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /**
             * when the ride is completed, set switch activity to generate a qrcode
             * @param documentSnapshot
             * @param e
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (documentSnapshot.getData().get("status").toString().equals("true")) {
                        Intent intent = new Intent(getApplicationContext(), GenerateQRCode.class);
                        String driverUserName = documentSnapshot.getData().get("driver").toString();
                        intent.putExtra("rider", user);
                        intent.putExtra("driver", driverName.getText().toString());
                        intent.putExtra("driver_user_name", driverUserName);
                        intent.putExtra("amount", amount);

                        startActivity(intent);
                        finish();


                    }
                }
            }
        });
    }


    private void rideStatusListener() {
        db.collection("AvailableRides").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /**
             * Listener for the ride status
             * @param documentSnapshot
             * @param e
             */
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

                                                Log.d(TAG, driver);

                                                db.collection("Users").whereEqualTo("username", driver)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            /**
                                                             * listener to when ride is complete
                                                             * @param task
                                                             */
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        float likes = parseFloat(document.getData().get("thumbsUp").toString());
                                                                        float dislikes = parseFloat(document.getData().get("thumbsDown").toString());
                                                                        DecimalFormat df = new DecimalFormat("#.#");

                                                                        driverName.setText(document.getData().get("name").toString());
                                                                        driverRating.setText(df.format(likes / (dislikes+likes) * 100)  + "%");
                                                                        findingText.setText("Driver found!");
                                                                        cancel.setText(" DECLINE ");
                                                                        phoneNumber.setText(document.getData().get("phoneNumber").toString());
                                                                        email.setText(document.getData().get("email").toString());

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
    }

    private void declineDriverButton() {
        View.OnClickListener declineDriverOnClickListener = new View.OnClickListener() {
            /**
             * when decline driver is pressed, update rider and driver in firebase accordingly
             * @param v
             */
            @Override
            public void onClick(View v) {
                email.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.GONE);
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
    }

    private void cancelButton() {
//        View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            /**
             * when cancel button is pressed, updated firebase to cancel current driver,
             * find a new driver as well afterward
             * @param v
             */
            @Override
            public void onClick(View v) {
                email.setText("");
                phoneNumber.setText("");
                db.collection("ActiveRides").document(user)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

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
        });
    }

    private void confirmButton() {
        confirm.setOnClickListener(new View.OnClickListener() {
            /**
             * on click for confirm button
             * updates firebase accordingly to link rider and driver with the appropiate info for ride
             * @param v
             */
            @Override
            public void onClick(View v) {
                // get all of the information from AvailableRides to migrate to ActiveRides
                if (confirm.getText().toString().equals("confirm")) {
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
                                            GeoPoint old_LatLng = document.getGeoPoint("LatLng");
                                            GeoPoint old_LatLngDest = document.getGeoPoint("LatLngDest");



                                            Map<String, Object> data = new HashMap<>();
                                            data.put("amount", Float.parseFloat(old_amount));
                                            data.put("datetime", old_datetime);
                                            data.put("driver", old_driverName);
                                            data.put("endLocation", old_endLocation);
                                            data.put("startLocation", old_startLocation);
                                            data.put("rider", old_rider);
                                            data.put("status", false);
                                            data.put("LatLng", old_LatLng);
                                            data.put("LatLngDest", old_LatLngDest);

                                            db.collection("ActiveRides").document(user).set(data);

                                            amount = Float.parseFloat(old_amount);

                                            // now change the text to ride in progress
                                            findingBoxAnimationDown.start();
                                            findingTextAnimationDown.start();
                                            driverNameAnimationDown.start();
                                            driverRatingAnimationDown.start();
                                            cancelAnimationDown.start();
                                            confirmAnimationDown.start();


                                            findingText.setText("Ride is currently in progress");
                                            cancel.setText("Cancel");
                                            confirm.setText("Ride Complete");

                                            isCancelDriver = false;

                                            findingBoxAnimationUp.start();
                                            findingTextAnimationUp.start();
                                            driverNameAnimationUp.start();
                                            driverRatingAnimationUp.start();
                                            cancelAnimationUp.start();
                                            confirmAnimationUp.start();

                                            // delete the document in AvailableRides
                                            db.collection("AvailableRides").document(user)
                                                    .delete()
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
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                } else if (confirm.getText().toString().equals("Ride Complete")) {
                    db.collection("ActiveRides").document(user)
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

    private void sendEmail(){
        String[] recipients = {email.getText().toString()};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private void getUserInfo(){
        String drivername = driverName.getText().toString();
        String number = phoneNumber.getText().toString();
        String recipient = email.getText().toString();
        Intent intent = new Intent(getApplicationContext(), UserInfo.class);
        intent.putExtra("fullname", drivername);
        intent.putExtra("number", number);
        intent.putExtra("email", recipient);
        startActivity(intent);

    }

    private void makePhoneCall(){
        //remember to source coding in flow!
        String number = phoneNumber.getText().toString();
        if (number.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
