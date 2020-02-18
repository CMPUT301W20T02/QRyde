package com.datainfo.QRyde;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class DriverMainMap extends AppCompatActivity {

    AvailableRideAdapter rideAdapter;
    ArrayList<AvailableRide> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_main_map);

        ListView availableRideListView = findViewById(R.id.list_view);
        final EditText startLocationEditText = findViewById(R.id.start_location_et);
        final EditText endLocationEditText = findViewById(R.id.end_location_et);


        AvailableRide [] AvailableRideList = {
                new AvailableRide("Masahiro Sakurai", "ur mom", "ur dad", 20.6f, 0.7f),
                new AvailableRide("Hideo Kojima", "ur mom", "ur dad", 17.8f, 1.3f),
                new AvailableRide("Todd Howard", "ur mom", "ur dad", 27.7f, 1.7f),
                new AvailableRide("Sean Murray", "ur mom", "ur dad", 30.8f, 1.9f),
                new AvailableRide("Reggie Fils-Aime", "ur mom", "ur dad", 52.9f, 2.3f)
        };
        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(AvailableRideList));

        rideAdapter = new AvailableRideAdapter(this, dataList);

        availableRideListView.setAdapter(rideAdapter);

        final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel);
        availableRideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                startLocationEditText.setText(dataList.get(position).getStartLocation());
                endLocationEditText.setText(dataList.get(position).getEndLocation());
                return true;
            }

        });

    }
}