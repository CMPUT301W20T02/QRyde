package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Generates qr code by utilizing driver, rider and cost,
 * creates the qrcode string to send to api and calls imageDownloader function to get qrcode img
 */
public class GenerateQRCode extends AppCompatActivity {

    private String TAG = "GenerateQRCode";

    private ImageView imageView;
    private String driverName;
    private String driverUserName;
    private String rider;
    float amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            rider = incomingData.getString("rider");
            driverName = incomingData.getString("driver");
            amount = incomingData.getFloat("amount");
            driverUserName = incomingData.getString("driver_user_name");
        }

        String qrMsg = rider + " owes " + driverName + " $" + amount;

        imageView = findViewById(R.id.imageView);

        new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + qrMsg);


        //still figuring out how to transition from this activity to the RateDriver one, added this listener to test RateDriver for now
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RateDriver.class);
                intent.putExtra("driver", driverUserName);
                startActivity(intent);
            }
        });
    }
}
