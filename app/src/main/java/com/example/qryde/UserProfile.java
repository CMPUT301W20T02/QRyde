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
import java.util.Objects;

/**
 * This class displays the users profile and gives them the ability to edit that information
 */
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

        //Setting the username Textview since we received it from the intent extras
        usernameTextView.setText(user);

        //Query the users collection for the user passed through the intent
        db.collection("Users").whereEqualTo("username", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * This method displays the users information when the app loads this activity
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Set TextViews from the info retrieved from the document
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
        //Open dialog fragment to edit email and phone
        editSymbol.setOnClickListener(new View.OnClickListener() {
            /**
             * This method enables the user to laod the fragment that allows the user edit their account information
             * @param v
             */
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

    /**
     * This method allows the new account info to be stored after the ok button is pressed
     * @param newEmail
     * @param newPhone
     */
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

