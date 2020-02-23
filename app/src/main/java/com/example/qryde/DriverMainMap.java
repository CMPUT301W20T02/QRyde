package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Float.parseFloat;

public class DriverMainMap extends AppCompatActivity implements OnFragmentInteractionListener{

    AvailableRideAdapter rideAdapter;
    ArrayList<AvailableRide> dataList;
    EditText startLocationEditText;
    EditText endLocationEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_map);

        db = FirebaseFirestore.getInstance();

        ListView availableRideListView = findViewById(R.id.list_view);
        startLocationEditText = findViewById(R.id.start_location_et);
        endLocationEditText = findViewById(R.id.end_location_et);


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
                final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                waitForUser(position);
                return true;
            }

        });

    }
    public void waitForUser(int position){
        startLocationEditText.setText(dataList.get(position).getStartLocation());
        endLocationEditText.setText(dataList.get(position).getEndLocation());
        WaitingUserResponseFragment newFrag = new WaitingUserResponseFragment();
        newFrag.show(getSupportFragmentManager(), "WAITING_USER_RESPONSE");
    }

    public void rideInProgress(int position) {
        RideInProgressFragment newFrag = new RideInProgressFragment();
        newFrag.show(getSupportFragmentManager(), "RIDE_IN_PROGRESS");
    }

    public void rideComplete(int position){
        Bundle bundle = new Bundle();
        bundle.putFloat("AMOUNT_OFFERED", dataList.get(position).getAmountOffered());
        RideCompleteFragment newFrag = new RideCompleteFragment();
        newFrag.show(getSupportFragmentManager(), "RIDE_COMPLETE");
    }

    public void onCanceledPressed(){
        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        startLocationEditText.setText("");
        endLocationEditText.setText("");
    }

}