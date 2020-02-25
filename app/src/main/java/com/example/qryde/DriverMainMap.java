package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Float.parseFloat;

public class DriverMainMap extends AppCompatActivity {

    AvailableRideAdapter rideAdapter;
    ArrayList<AvailableRide> dataList;
    EditText startLocationEditText;
    EditText endLocationEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_map);

        db = FirebaseFirestore.getInstance();

        ListView availableRideListView = findViewById(R.id.list_view);


        AvailableRide[] AvailableRideList = {};

        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));

        rideAdapter = new AvailableRideAdapter(this, dataList);

        availableRideListView.setAdapter(rideAdapter);

        db.collection("AvailableRides")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("xd", document.getId() + " => " + document.get("rider"));
                                AvailableRide temp = new AvailableRide(document.getData().get("rider").toString(),
                                        document.getData().get("startLocation").toString(),
                                        document.getData().get("endLocation").toString(),
                                        parseFloat(document.getData().get("amount").toString()),
                                        1.3f);
                                dataList.add(temp);
                                rideAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("xd", "Error getting documents: ", task.getException());
                        }
                    }
                });


        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        availableRideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WaitingUserResponse.class);
                startActivity(intent);
                return true;
            }

        });

    }


    public void rideComplete(int position){
        Bundle bundle = new Bundle();
        bundle.putFloat("AMOUNT_OFFERED", dataList.get(position).getAmountOffered());
        RideCompleteFragment newFrag = new RideCompleteFragment();
        newFrag.show(getSupportFragmentManager(), "RIDE_COMPLETE");
    }

}