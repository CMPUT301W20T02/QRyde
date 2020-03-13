package com.example.qryde;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 * Generates qr code by utilizing driver, rider and cost,
 * creates the qrcode string to send to api and calls imageDownloader function to get qrcode img
 */
public class GenerateQRCode extends AppCompatActivity {

    private String TAG = "GenerateQRCode";

    private ImageView imageView;
    private String driver;
    private String rider;
    float amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);

        Bundle incomingData = getIntent().getExtras();
        if (incomingData != null) {
            rider = incomingData.getString("rider");
            driver = incomingData.getString("driver");
            amount = incomingData.getFloat("amount");
        }

        String qrMsg = rider + " owes " + driver + " $" + amount;

        imageView = findViewById(R.id.imageView);

        new ImageDownloaderTask(imageView).execute("https://api.qrserver.com/v1/create-qr-code/?size=1000x1000&data=" + qrMsg);





    }
}
