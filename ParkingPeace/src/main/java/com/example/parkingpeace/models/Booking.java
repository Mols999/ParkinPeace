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

    // Getter for bookingID
    public int getBookingID() {
        return bookingID;
    }


    // Getter for customerID
    public int getCustomerID() {
        return customerID;
    }


    // Getter for parkingSpotID
    public int getParkingSpotID() {
        return parkingSpotID;
    }


    // Getter for startDateTime
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }


    // Getter for endDateTime
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }


    // Getter for bookingStatus
    public String getBookingStatus() {
        return bookingStatus;
    }


    // Getter for price
    public String getPrice() {
        return price;
    }


    // Setter for price
    public void setPrice(String price) {
        this.price = price;
    }

}
