package com.example.parkingpeace.models;

import java.time.LocalDateTime;

public class Booking {
    private int bookingID;
    private int customerID;
    private int parkingSpotID;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String bookingStatus;
    private String price;



    // Constructor
    public Booking(int bookingID, int customerID, int parkingSpotID, LocalDateTime startDateTime, LocalDateTime endDateTime, String bookingStatus) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.parkingSpotID = parkingSpotID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.bookingStatus = bookingStatus;
        this.price = price;
    }



    // Getter and Setters
    public int getBookingID() {
        return bookingID;
    }
    public int getCustomerID() {
        return customerID;
    }
    public int getParkingSpotID() {
        return parkingSpotID;
    }
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public String getBookingStatus() {
        return bookingStatus;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
}
