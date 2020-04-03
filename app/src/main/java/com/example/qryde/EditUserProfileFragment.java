package com.example.qryde;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditUserProfileFragment extends DialogFragment {
    //citation: University of Alberta, Lab 3, https://eclass.srv.ualberta.ca/course/view.php?id=57571
    private EditText emailEditText;
    private EditText phoneEditText;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void onOkPressed(String newEmail, String newPhone);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_dialog, null);
        emailEditText = view.findViewById(R.id.email_editText);
        phoneEditText = view.findViewById(R.id.phone_editText);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String oldPhoneText = bundle.getString("PHONE");
            String oldEmailText = bundle.getString("EMAIL");
            emailEditText.setText(oldEmailText);
            phoneEditText.setText(oldPhoneText);
            Log.d("ok", "THIS EXECUTED");
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit your email/Phone Number")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newEmailText = emailEditText.getText().toString();
                        String newPhoneText = phoneEditText.getText().toString();
                        listener.onOkPressed(newEmailText, newPhoneText);
                    }}).create();
    }
}