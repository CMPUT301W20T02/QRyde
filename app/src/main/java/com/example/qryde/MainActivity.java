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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * First activity shown to the user,
 * login screen that checks with firebase whether user is a driver or a rider,
 * switches to appropriate activity
 *
 * includes sign up button method that switches to registration activity
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private FirebaseFirestore db;

    private EditText username;
    private EditText password;
    private TextView signup;
    private Button login;
    private ImageView usernameBox;
    private ImageView passwordBox;
    private TextView incorrect;

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

        loginButton();
        signUpButton();
    }

    private void loginButton() {
        login.setOnClickListener( new View.OnClickListener() {
            /**
             * on click of login button it checks whether username entered matches any in firebase
             * @param v
             */
            @Override
            public void onClick(View v) {
                db.collection("Users")
                        .whereEqualTo("username", username.getText().toString()).whereEqualTo("password", password.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            /**
                             * if task is successful it checks whether user is a driver or rider,
                             * sets intent to corresponding intent based on the user
                             * switches to that intent
                             * returns incorrect username or password if neither matches in firebase
                             * @param task
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) {

                                        // choose whether to start the driver or the user branches
                                        String isDriver = "";

                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            isDriver = document.getData().get("isDriver").toString();
                                        }

                                        Intent intent;

                                        if (isDriver == "false") {
                                            intent = new Intent(getApplicationContext(), MapActivity.class);
                                        } else {
                                            intent = new Intent(getApplicationContext(), DriverMainMap.class);
                                        }

                                        intent.putExtra("username", username.getText().toString());
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
    }

    private void signUpButton() {
        signup.setOnClickListener( new View.OnClickListener() {
            /**
             * when register button is clicked, activity for registration is started
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });
    }

}
