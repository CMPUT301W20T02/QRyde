package com.example.qryde;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class describes the app activity when a new user is signing up into the app
 */

public class signup extends AppCompatActivity {

    private String TAG = "Signup";

    private FirebaseFirestore db;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText reenterPassword;
    private Button signupButton;
    private TextView error;
    private Switch userType;
    private EditText name;
    private EditText phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username_edittext);
        email = findViewById(R.id.email_edittext);
        password = findViewById(R.id.password_edittext);
        reenterPassword = findViewById(R.id.reenterPassword_edittext);
        signupButton = findViewById(R.id.signup_button);
        error = findViewById(R.id.incorrect);
        userType = findViewById(R.id.userType);
        name = findViewById(R.id.name_edittext);
        phoneNumber = findViewById(R.id.phone_edittext);

        signUpButton();
    }

    private void signUpButton() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * This method allows the user to sign up and create an account that allows
             * them use the app
             * @param View
             * @return Nothing if the error conditions are met
             */
            public void onClick(View v) {

                if (username.getText().toString().equals("") ||
                        email.getText().toString().equals("") ||
                        password.getText().toString().equals("") ||
                        reenterPassword.getText().toString().equals("") ||
                        name.getText().toString().equals("") ||
                        phoneNumber.getText().toString().equals("")) {
                    error.setText("Invalid information entered");
                    password.setText("");
                    reenterPassword.setText("");
                    return;
                }

                if (password.getText().toString().equals(reenterPassword.getText().toString())) {
                    Log.d(TAG, "onClick: passwords match");
                } else {
                    error.setText("Passwords do not match");
                    password.setText("");
                    reenterPassword.setText("");
                    return;
                }

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    Log.d(TAG, "onClick: email is valid");
                } else {
                    error.setText("Email is not valid");
                    password.setText("");
                    reenterPassword.setText("");
                    return;
                }

                db.collection("Users")
                        .whereEqualTo("username", username.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override

                            /**
                             * This method adds the users account information to the Users
                             * collection in the Firebase Database
                             */
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {
                                        Log.d(TAG, "onComplete: something went wrong");
                                        error.setText("Username already taken");
                                        password.setText("");
                                        reenterPassword.setText("");
                                        return;
                                    } else {
                                        final String newUsername = username.getText().toString();
                                        final String newEmail = email.getText().toString();
                                        final String newPassword = password.getText().toString();
                                        final Boolean newUserType = userType.isChecked();
                                        final Double newQRBank = 0.0;
                                        final String newRegDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                        final String newName = name.getText().toString();
                                        final String newPhone = phoneNumber.getText().toString();
                                        final Integer newThumbsUp = 0;
                                        final Integer newThumbsDown = 0;

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("QRBank", newQRBank);
                                        data.put("RegDate", newRegDate);
                                        data.put("email", newEmail);
                                        data.put("isDriver", newUserType);
                                        data.put("name", newName);
                                        data.put("password", newPassword);
                                        data.put("phoneNumber", newPhone);
                                        data.put("thumbsDown", newThumbsUp);
                                        data.put("thumbsUp", newThumbsDown);
                                        data.put("username", newUsername);
                                        db.collection("Users").document(newUsername).set(data);

                                        finish();

                                    }
                                } else {
                                    Log.d(TAG, "onComplete: query failed");
                                }
                            }
                        });
            }
        });
    }
}
