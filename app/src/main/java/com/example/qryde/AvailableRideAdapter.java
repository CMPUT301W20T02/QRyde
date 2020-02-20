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
import java.util.List;


public class AvailableRideAdapter extends ArrayAdapter<AvailableRide> {

    private Context mContext;
    private List<AvailableRide> dataList = new ArrayList<>();

    //Constructor for the adapter
    public AvailableRideAdapter(@NonNull Context context, ArrayList<AvailableRide> list) {
        super(context, 0 , list);
        mContext = context;
        dataList = list;
    }

    @NonNull
    @Override


    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        //Layout inflater
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.available_ride, parent,false);

        AvailableRide currentRide = dataList.get(position);

        //get Textviews
        TextView tvRiderName = (TextView) listItem.findViewById(R.id.name_text_view);
        TextView tvDistanceAway = (TextView) listItem.findViewById(R.id.distance_text_view);
        TextView tvAmountOffered = (TextView) listItem.findViewById(R.id.amount_text_view);

        //Build new strings to display
        String nameText = currentRide.getRiderUsername();
        String distanceText = currentRide.getDistanceAway() + " km";
        String AmountText = "$" + currentRide.getAmountOffered();

        //Set the built strings to the textviews to display
        tvRiderName.setText(nameText);
        tvDistanceAway.setText(distanceText);
        tvAmountOffered.setText(AmountText);
        return listItem;
    }
}