package com.example.qryde;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserInfo extends AppCompatActivity {

    private String TAG = "USERINFO";
    private FirebaseFirestore db;

    private String name;
    private String phoneNumber;
    private String email;
    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;
    private ImageView editSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        db = FirebaseFirestore.getInstance();

        fullNameTextView = findViewById(R.id.user_full_name);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        phoneNumberTextView = findViewById(R.id.phone_number);
        editSymbol = findViewById(R.id.editImage);

        Bundle incomingData = getIntent().getExtras();
        if(incomingData != null){
            name = incomingData.getString("fullname");
            phoneNumber = incomingData.getString("number");
            email = incomingData.getString("email");
        }
        fullNameTextView.setText(name);

        db.collection("Users").whereEqualTo("name", name).whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                usernameTextView.setText(document.getData().get("username").toString());
                                phoneNumberTextView.setText(document.getData().get("phoneNumber").toString());
                                phoneNumber = document.getData().get("phoneNumber").toString();
                                emailTextView.setText(document.getData().get("email").toString());
                                email = document.getData().get("email").toString();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
