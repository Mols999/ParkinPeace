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

    public int getRatingID() {
        return ratingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    @Override
    public String toString() {
        return "Review{" +
                "ratingID=" + ratingID +
                ", customerID=" + customerID +
                ", ratingValue=" + ratingValue +
                ", ratingComment='" + ratingComment + '\'' +
                '}';
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }
}
