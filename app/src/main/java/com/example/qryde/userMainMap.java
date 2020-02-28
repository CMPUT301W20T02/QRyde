//package com.example.qryde;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class userMainMap extends AppCompatActivity {
//    FirebaseFirestore db;
//    String user;
//    ImageView logo;
//    Button button;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_main_map);
//
//        Bundle incomingData = getIntent().getExtras();
//        if (incomingData != null) {
//            user = incomingData.getString("username");
//        }
//
//        db = FirebaseFirestore.getInstance();
//
//        logo = findViewById(R.id.qryde_logo);
//
//        button = findViewById(R.id.buttonToMap);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MapsActivity();
//            }
//        });
//
//
//        logo.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), SelectLocation.class);
//                intent.putExtra("username", user);
//                startActivity(intent);
//            }
//        });
//
//
//    }
//
//    public void MapsActivity() {
//        Intent intent = new Intent(this, UserMapActivity.class);
//        intent.putExtra("username", user);
//        startActivity(intent);
//    }
//
//}
