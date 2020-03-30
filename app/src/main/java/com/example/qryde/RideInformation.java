package com.example.qryde;
public class RideInformation {
    private String date;
    private String rider;
    private String amount;
    private String destination;
    private String distance;
    private String duration;

    public RideInformation(String date, String user, String amount, String destination, String distance, String duration) {
        this.date = date;
        this.rider = rider;
        this.amount = amount;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

