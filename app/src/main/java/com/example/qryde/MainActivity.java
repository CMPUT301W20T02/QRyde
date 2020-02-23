package com.example.qryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    FirebaseFirestore db;

    EditText username;
    EditText password;
    TextView signup;
    Button login;
    ImageView usernameBox;
    ImageView passwordBox;
    TextView incorrect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();


        username = findViewById(R.id.username_edittext);
        password = findViewById(R.id.password_edittext);
        signup = findViewById(R.id.SignupClick);
        login = findViewById(R.id.login_button);
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);
        incorrect = findViewById(R.id.incorrect);

        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users")
                        .whereEqualTo("username", username.getText().toString()).whereEqualTo("password", password.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {
                                        Intent intent = new Intent(getApplicationContext(), afterRequestCreated.class);
                                        intent.putExtra("username", username.getText().toString());
                                        startActivity(intent);
                                    } else {
                                        usernameBox.setImageResource(R.drawable.rounded_rectangle_red);
                                        passwordBox.setImageResource(R.drawable.rounded_rectangle_red);
                                        incorrect.setText("Incorrect Username or Password");
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: failed to execute query");

                                }
                            }
                        });
            }
        });

        signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });

    }




}
