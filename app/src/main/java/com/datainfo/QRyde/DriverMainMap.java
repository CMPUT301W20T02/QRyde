package com.datainfo.QRyde;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    }
}