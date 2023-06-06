package com.example.parkingpeace;

import java.time.LocalDateTime;

public class Booking {

    private int bookingID;
    private int customerID;
    private int parkingSpotID;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String bookingStatus;
    private String price;

    public Booking(int bookingID, int customerID, int parkingSpotID, LocalDateTime startDateTime, LocalDateTime endDateTime, String bookingStatus) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.parkingSpotID = parkingSpotID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.bookingStatus = bookingStatus;
        this.price = price;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getParkingSpotID() {
        return parkingSpotID;
    }

    public void setParkingSpotID(int parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDateTime getBookingDate() {
        return startDateTime;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.startDateTime = bookingDate;
    }
}
