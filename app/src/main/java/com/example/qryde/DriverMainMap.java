package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

        final ListView availableRideListView = findViewById(R.id.list_view);


        AvailableRide[] AvailableRideList = {};

        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));

        rideAdapter = new AvailableRideAdapter(this, dataList);

        availableRideListView.setAdapter(rideAdapter);

        final CollectionReference collectionReference = db.collection("AvailableRides");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                dataList.clear();;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    AvailableRide temp = new AvailableRide(doc.getData().get("rider").toString(),
                            doc.getData().get("startLocation").toString(),
                            doc.getData().get("endLocation").toString(),
                            parseFloat(doc.getData().get("amount").toString()),
                            1.3f);
                    dataList.add(temp);
                    rideAdapter.notifyDataSetChanged();
                }
            }
        });


        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        availableRideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WaitingUserResponse.class);
                intent.putExtra("START_LOCATION", dataList.get(position).getStartLocation());
                intent.putExtra("END_LOCATION", dataList.get(position).getEndLocation());
                startActivity(intent);
                return true;
            }

        });


    }


}