package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class DriverMainMap extends AppCompatActivity implements OnFragmentInteractionListener{

    AvailableRideAdapter rideAdapter;
    ArrayList<AvailableRide> dataList;
    EditText startLocationEditText;
    EditText endLocationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_map);

        ListView availableRideListView = findViewById(R.id.list_view);
        startLocationEditText = findViewById(R.id.start_location_et);
        endLocationEditText = findViewById(R.id.end_location_et);


        AvailableRide[] AvailableRideList = {
                new AvailableRide("Masahiro Sakurai", "Nintendo", "Super Smash Bros", 20.6f, 0.7f),
                new AvailableRide("Reggie Fils-Aime", "Nintendo", "Wii Fit", 52.9f, 2.3f),
                new AvailableRide("Hideo Kojima", "Kojima Productions", "Metal Gear Solid", 17.8f, 1.3f),
                new AvailableRide("Todd Howard", "Bethesda Softworks", "Fallout 76", 27.7f, 1.7f),
                new AvailableRide("Sean Murray", "Hello Games", "No Mans Sky", 30.8f, 1.9f),
        };
        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));

        rideAdapter = new AvailableRideAdapter(this, dataList);

        availableRideListView.setAdapter(rideAdapter);

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