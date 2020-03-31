package com.example.qryde;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * List View class that allows drivers to view their Ride history offline
 * Data stored on cloud too
 */
public class RideHistoryList extends AppCompatActivity {
    ListView rideHistoryList;

    //initializing datalist and its objects
    ArrayList<RideInformation> rideInfoDataList;
    private ArrayList<RideInformation> rideInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history_list);

        //loading data
//        loadData();

        //initializing the data-list and the list from the view
        rideInfoDataList = new ArrayList<>();
//        rideHistoryList = findViewById(R.id.ride_history_list);

        //initializing the custom list adaptor
        final ArrayAdapter measurementAdaptor = new RideInfoAdapter(rideInfo, this);

        rideHistoryList.setAdapter(measurementAdaptor);
    }
}
