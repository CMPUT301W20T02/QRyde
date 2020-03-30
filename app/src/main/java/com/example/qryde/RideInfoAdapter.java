package com.example.qryde;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * This class creates one user ride mae by the dirver in a listview
 * accessed through the hamburger menu.
 * This list displays information of each ride made by the driver.
 */
public class RideInfoAdapter extends ArrayAdapter<RideInformation> {
    private ArrayList<RideInformation> rideinfo;
    private Context context;

    public RideInfoAdapter(ArrayList<RideInformation> rideinfo, Context context) {
        super(context, 0, rideinfo);
        this.rideinfo = rideinfo;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.rideinfo_list, parent, false);
        }

        RideInformation rideInfoObject = rideinfo.get(position);

        TextView date = view.findViewById(R.id.date);
        TextView rider = view.findViewById(R.id.rider);
        TextView amount = view.findViewById(R.id.amount);
        TextView distanceDuration = view.findViewById(R.id.distance_duration);
        TextView destination = view.findViewById(R.id.destination);

        //getting the variables from the rideInfo object
        String dateObject = rideInfoObject.getDate();
        String riderObject = rideInfoObject.getRider();
        String amountObject = rideInfoObject.getAmount();
        String distanceObject = rideInfoObject.getDistance();
        String durationObject = rideInfoObject.getDuration();
        String destinationObject = rideInfoObject.getDestination();

        date.setText(String.format("Date of Ride: %s", dateObject));
        rider.setText(String.format("Name of the Passenger: %s", riderObject));
        amount.setText(String.format("Amount Paid: %s", amountObject));
        distanceDuration.setText(String.format("%s km (%s minutes long)", distanceObject, durationObject));
        destination.setText(String.format("Passenger destination was %s.", destinationObject));

        return view;
    }
}
