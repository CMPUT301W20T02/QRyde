package com.example.qryde;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

import static java.lang.Float.parseFloat;

public class UserProfile extends AppCompatActivity {
    private String TAG = "temp";
    private FirebaseFirestore db;

    private String user;
    private TextView fullName;
    private TextView username;
    private TextView email;
    private TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullName = findViewById(R.id.user_full_name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);

        db = FirebaseFirestore.getInstance();

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }
        username.setText(user);

        db.collection("Users").whereEqualTo("username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fullName.setText(document.getData().get("name").toString());
                                phoneNumber.setText(document.getData().get("phoneNumber").toString());
                                email.setText(document.getData().get("email").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}

