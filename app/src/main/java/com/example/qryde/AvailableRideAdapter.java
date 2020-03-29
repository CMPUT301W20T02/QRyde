package com.example.qryde;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * sets constructor for the available ride adapter and sets strings for the textviews in rideRequests
 */
public class AvailableRideAdapter extends ArrayAdapter<AvailableRide> {

    private Context mContext;
    private List<AvailableRide> dataList = new ArrayList<>();

    /**
     * Constructor for the adapter
     * @param context
     * @param list
     */

    public AvailableRideAdapter(@NonNull Context context, ArrayList<AvailableRide> list) {
        super(context, 0 , list);
        mContext = context;
        dataList = list;
    }

    @NonNull
    @Override

    /**
     * gets textviews, builds new strings to display and sets the string to textviews to display
     */
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
        TextView startLocation = (TextView) listItem.findViewById(R.id.startLocation);
        TextView endLocation = (TextView) listItem.findViewById(R.id.endLocation);

        //Build new strings to display
        String nameText = currentRide.getRiderUsername();
        String distanceText = currentRide.getDistanceAway() + " km";
        String AmountText = "$" + currentRide.getAmountOffered();
        String startText = currentRide.getStartLocation();
        String endText = currentRide.getEndLocation();

        //Set the built strings to the textviews to display
        tvRiderName.setText(nameText);
        tvDistanceAway.setText(distanceText);
        tvAmountOffered.setText(AmountText);
        startLocation.setText(startText);
        endLocation.setText(endText);
        return listItem;
    }
}