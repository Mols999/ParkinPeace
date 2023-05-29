package com.example.parkingpeace;

public class UserSession {
    private static UserSession instance;

    private String username;
    private String userType; // customer, landlord, admin
    private int customerID;
    private String customerName;
    private int customerAge;
    private String email;
    private String password;
    private double customerRating;

    private UserSession(String username, String userType, int customerID, String customerName, int customerAge, String email, String password, double customerRating) {
        this.username = username;
        this.userType = userType;
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.email = email;
        this.password = password;
        this.customerRating = customerRating;
    }

    public static UserSession getInstance(String username, String userType, int customerID, String customerName, int customerAge, String email, String password, double customerRating) {
        if(instance == null) {
            instance = new UserSession(username, userType, customerID, customerName, customerAge, email, password, customerRating);
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getCustomerRating() {
        return customerRating;
    }

    public void cleanUserSession() {
        username = "";// or null
        userType = "";// or null
        customerID = 0;
        customerName = "";// or null
        customerAge = 0;
        email = "";// or null
        password = "";// or null
        customerRating = 0.0;
        instance = null;
    }
}
