package com.example.qryde;

import com.google.firebase.firestore.FirebaseFirestore;

// creates active driver object in database under drivername document

public class DBCreateActiveDriver {
    public static void activateDriver(String activeDriver, double driverLocationLat, double driverLocationLng, String servingRider) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final ActiveDriver createdActiveDriver = new ActiveDriver(activeDriver, driverLocationLat, driverLocationLng, servingRider);

        db.collection("ActiveDrivers").document(activeDriver).set(createdActiveDriver);

    }

}
