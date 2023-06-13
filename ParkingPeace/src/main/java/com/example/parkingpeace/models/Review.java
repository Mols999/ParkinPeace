package com.example.parkingpeace.models;

public class Review {
    private int ratingID;
    private int customerID;
    private int ratingValue;
    private String ratingComment;

    public Review(int ratingID, int customerID, int ratingValue, String ratingComment) {
        this.ratingID = ratingID;
        this.customerID = customerID;
        this.ratingValue = ratingValue;
        this.ratingComment = ratingComment;
    }

    // Getter for ratingValue
    public int getRatingValue() {
        return ratingValue;
    }

    // Getter for ratingComment
    public String getRatingComment() {
        return ratingComment;
    }

    // toString method to provide a string representation of the Review object
    @Override
    public String toString() {
        return "Review{" +
                "ratingID=" + ratingID +
                ", customerID=" + customerID +
                ", ratingValue=" + ratingValue +
                ", ratingComment='" + ratingComment + '\'' +
                '}';
    }
}
