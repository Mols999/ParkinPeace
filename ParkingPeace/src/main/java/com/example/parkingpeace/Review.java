package com.example.parkingpeace;

public class Review {
    private int ratingID;
    private int customerID;
    private int landlordID;
    private int ratingValue;
    private String ratingComment;

    public Review(int ratingID, int customerID, int landlordID, int ratingValue, String ratingComment) {
        this.ratingID = ratingID;
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.ratingValue = ratingValue;
        this.ratingComment = ratingComment;
    }

    public int getRatingID() {
        return ratingID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getLandlordID() {
        return landlordID;
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
                ", landlordID=" + landlordID +
                ", ratingValue=" + ratingValue +
                ", ratingComment='" + ratingComment + '\'' +
                '}';
    }
}
