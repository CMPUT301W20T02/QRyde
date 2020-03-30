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
        TextView distance = view.findViewById(R.id.distance_duration);
        TextView destination = view.findViewById(R.id.destination);

        return view;
    }
}
