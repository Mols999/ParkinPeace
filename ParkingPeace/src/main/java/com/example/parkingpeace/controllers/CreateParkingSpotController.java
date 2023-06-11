package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.util.Random;

public class CreateParkingSpotController {
    @FXML
    private TextField locationField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField servicesField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField photoFilePathField;

    private DB db;

    public CreateParkingSpotController() {
        db = new DB();
    }

    @FXML
    public void handleSaveButton() {
        String location = locationField.getText();
        double price = Double.parseDouble(priceField.getText());
        String services = servicesField.getText();
        String zipCode = zipCodeField.getText();
        String city = cityField.getText();
        String photoFilePath = photoFilePathField.getText();

        int landlordId = 1; // Replace with actual landlord ID

        createParkingSpot(location, services, price, landlordId, zipCode, city, photoFilePath);

        // Clear the form
        locationField.clear();
        priceField.clear();
        servicesField.clear();
        zipCodeField.clear();
        cityField.clear();
        photoFilePathField.clear();
    }

    public boolean createParkingSpot(String location, String services, double price, int landlordId, String zipCode, String city, String photoFilePath) {
        String parkingSpotId = generateParkingSpotId();
        String availability = "0";  // Initially set availability to 0

        String sql = "INSERT INTO tblParkingSpot (fldParkingSpotID, fldLocation, fldAvailability, fldPrice, fldServices, fldLandlordID, fldZipCode, fldCity, fldPhotoFilePath) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return db.insertSQL(sql, parkingSpotId, location, availability, price, services, landlordId, zipCode, city, photoFilePath);
    }

    private String generateParkingSpotId() {
        Random rand = new Random();
        return String.format("%04d", rand.nextInt(10000));  // Generate a random 4-digit number
    }
}
