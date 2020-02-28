package com.example.qryde;

import com.google.firebase.firestore.FirebaseFirestore;

// creates available ride object in database under username document

public class DBCreateAvailableRide {
    public static void createRide(String riderUsername, String startAddress, String endAddress,
                                  double startLocationLat, double startLocationLng, double endLocationLat,
                                  double endLocationLng, float amountOffered, float distanceAway, String status, String driver) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final AvailableRide createdAvailableRide = new AvailableRide(riderUsername, startAddress, endAddress,
                startLocationLat, startLocationLng, endLocationLat, endLocationLng, amountOffered, distanceAway, status, driver);

        db.collection("AvailableRides1").document(riderUsername).set(createdAvailableRide);

    }

}
