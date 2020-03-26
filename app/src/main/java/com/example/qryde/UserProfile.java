package com.example.qryde;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements EditUserProfileFragment.OnFragmentInteractionListener {
    private String TAG = "temp";
    private FirebaseFirestore db;

    private String user;
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
        setContentView(R.layout.activity_user_profile);

        fullNameTextView = findViewById(R.id.user_full_name);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        phoneNumberTextView = findViewById(R.id.phone_number);
        editSymbol = findViewById(R.id.editImage);

        db = FirebaseFirestore.getInstance();

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            user = incomingData.getString("username");
        }
        usernameTextView.setText(user);

        db.collection("Users").whereEqualTo("username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fullNameTextView.setText(document.getData().get("name").toString());
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
        editSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("EMAIL", email);
                bundle.putString("PHONE", phoneNumber);
                EditUserProfileFragment editProfileFrag = new EditUserProfileFragment();
                editProfileFrag.setArguments(bundle);
                editProfileFrag.show(getSupportFragmentManager(), "EDIT_PROFILE");
            }
        });

    }

    @Override
    public void onOkPressed(String newEmail, String newPhone) {
        emailTextView.setText(newEmail);
        phoneNumberTextView.setText(newPhone);
        db.collection("Users").whereEqualTo("username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("email", newEmail);
                                data.put("phoneNumber", newPhone);
                                db.collection("Users").document(user).update(data);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        email = newEmail;
        phoneNumber = newPhone;
    }
}

