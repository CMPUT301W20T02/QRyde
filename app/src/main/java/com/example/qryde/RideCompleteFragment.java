package com.example.qryde;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This class generates a dialogue box when the ride is completed
 * Shows the amount offered by the rider to the driver
 */
public class RideCompleteFragment extends DialogFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    /**
     * This method creates the dialogue and shows information on ride
     * completion status and the amount of QRBucks offered by the rider to the driver
     * @param InstanceState
     * @return Dialogue.builder
     */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_ride_complete, null);
        Bundle bundle = getArguments();
        if (bundle != null) {
            float Amount = bundle.getFloat("AMOUNT_OFFERED");
            String AmountText = "$" + Amount;

        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Ride Complete").create();
    }
}