package com.example.qryde;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
        loadData();

        //initializing the data-list and the list from the view
        rideInfoDataList = new ArrayList<>();
        rideHistoryList = findViewById(R.id.ride_history_list);

        //initializing the custom list adaptor
        final ArrayAdapter rideInfoAdapter = new RideInfoAdapter(rideInfo, this);

        rideHistoryList.setAdapter(rideInfoAdapter);
    }

    //method for loading data(called onCreate)
    private void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<RideInformation>>() {}.getType();
        rideInfo = gson.fromJson(json, type);

        if(rideInfo == null)
        {
            rideInfo = new ArrayList<>();
        }
    }
}
