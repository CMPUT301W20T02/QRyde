package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverQRComplete extends AppCompatActivity {

    String TAG = "DriverQRComplete";

    String IOUMsg;
    TextView msg;
    private FirebaseFirestore db;
    int numTransactions;
    Button completeButton;

    private String old_amount;
    private String old_datetime;
    private String old_driverName;
    private String old_endLocation;
    private String old_startLocation;
    private String old_rider;

    private RideInformation rideInformationObj;
    private ArrayList<RideInformation> rideInfoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_qr_complete);

        db = FirebaseFirestore.getInstance();

        //loading shared preferences data
        loadData();

        msg = findViewById(R.id.qr_amount_text);
        completeButton = findViewById(R.id.close_button);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            IOUMsg = incomingData.getString("iou");
        }

        // get num of transactions from metadata
        db.collection("QRTransactions")
                .document("metadata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                numTransactions = Integer.parseInt(document.getString("numOfTransactions")) + 1;

                                // update the firebase
                                db.collection("QRTransactions").document("metadata")
                                        .update("numOfTransactions", Integer.toString(numTransactions))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: Successfully updated document");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Failed to update document");
                                            }
                                        });

                                // deconstruct the msg to get the amount
                                String[] msg_split = IOUMsg.split(" ");
                                Log.d(TAG, "onCreate: " + msg_split[0] + msg_split[1] + msg_split[2] + msg_split[3]);

                                String owed = msg_split[3].substring(1);
                                Log.d(TAG, "onCreate: " + owed);

                                // now we can add all of this to the firebase
                                Map<String, Object> data = new HashMap<>();
                                data.put("amount", Float.parseFloat(owed));
                                data.put("driver", msg_split[2]);
                                data.put("id", numTransactions);
                                data.put("rider", msg_split[0]);
                                db.collection("QRTransactions").document(Integer.toString(numTransactions)).set(data);

                                // transfer the document from ActiveRides to RideHistories
                                db.collection("ActiveRides")
                                        .whereEqualTo("rider", msg_split[0])
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                         get all of the information
                                                        old_amount = document.getData().get("amount").toString();
                                                        old_datetime = document.getData().get("datetime").toString();
                                                        old_driverName = document.getData().get("driver").toString();
                                                        old_endLocation = document.getData().get("endLocation").toString();
                                                        old_startLocation = document.getData().get("startLocation").toString();
                                                        old_rider = document.getData().get("rider").toString();

                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("amount", Float.parseFloat(old_amount));
                                                        data.put("datetime", old_datetime);
                                                        data.put("driver", old_driverName);
                                                        data.put("endLocation", old_endLocation);
                                                        data.put("startLocation", old_startLocation);
                                                        data.put("rider", old_rider);
                                                        data.put("id", numTransactions);

                                                        db.collection("RideHistories").document(Integer.toString(numTransactions)).set(data);

                                                        db.collection("ActiveRides").document(msg_split[0])
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

                                                        //saving variables to Driver Ride History
//                                                        rideHistorySave();
                                                    }
                                                } else {
                                                    Log.d(TAG, "onComplete: failed to retrieve document");
                                                }
                                            }
                                        });
                            } else {
                                Log.d(TAG, "onComplete: No such document");
                            }
                        } else {
                            Log.d(TAG, "onComplete: could not access firebase");
                        }
                    }
                });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        msg.setText(IOUMsg);
    }

    /**
     * Saving data that is entered locally
     */
    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(rideInfoList);
        editor.putString("task list", json);
        editor.apply();
    }

    /**
     * Method for loading data(called onCreate)
     */
    private void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<RideInformation>>() {}.getType();
        rideInfoList = gson.fromJson(json, type);

        if(rideInfoList == null)
        {
            rideInfoList = new ArrayList<>();
        }
    }

    /**
     * Methods to save varibles from Firebase to Driver Ride History
     */
//    private void rideHistorySave()
//    {
//        //initializing the custom list adaptor
//        final ArrayAdapter rideInfoAdapter = new RideInfoAdapter(rideInfoList, getApplicationContext());
//
//        //saving ridehistory data into a class object
//        rideInformationObj = new RideInformation(old_datetime, old_rider, old_amount, old_endLocation, "10.0", "10.0");
//
//        rideInfoList.add(rideInformationObj);
//
//        //saving data locally
//        saveData();
//
//        //notifying adapter of change to list
//        rideInfoAdapter.notifyDataSetChanged();
//    }
}
