package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ModifyParkingSpotController {

    @FXML
    private TextField parkingSpotIDField;

    @FXML
    private TextField customerIDField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField availabilityField;

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

    @FXML
    private TextField ratingField;

    private String parkingSpotID;

    private static ParkingSpot currentParkingSpot; // Make this static so it persists between instances

    @FXML
    public void initialize() {
        if (currentParkingSpot != null) {
            fillTextFieldsWithCurrentInfo();
            // Save the ID
            parkingSpotID = currentParkingSpot.getParkingSpotID();

        }
    }

    public void fillTextFieldsWithCurrentInfo() {
        if (currentParkingSpot != null) {
            // Set the specific fields to the current values
            locationField.setText(currentParkingSpot.getLocation());
            priceField.setText(currentParkingSpot.getPrice());
            servicesField.setText(currentParkingSpot.getServices());
            zipCodeField.setText(currentParkingSpot.getZipCode());
            cityField.setText(currentParkingSpot.getCity());
            photoFilePathField.setText(currentParkingSpot.getPhotoFilePath());

        }
    }

    public boolean updateParkingSpot(String location, double price, String services, String zipCode, String city, String photoFilePath) {
        String sql = "UPDATE tblParkingSpot SET fldLocation = '" + location + "', fldPrice = " + price + ", fldServices = '" + services + "', fldZipCode = '" + zipCode + "', fldCity = '" + city + "', fldPhotoFilePath = '" + photoFilePath + "' WHERE fldParkingSpotID = " + parkingSpotID;

        try {
            DB db = new DB();
            return db.updateSQL(sql);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    @FXML
    public void handleSaveButton(ActionEvent event) {
        // Get the field inputs
        String location = locationField.getText();
        double price = Double.parseDouble(priceField.getText());
        String services = servicesField.getText();
        String zipCode = zipCodeField.getText();
        String city = cityField.getText();
        String photoFilePath = photoFilePathField.getText();

        // Use the input to update the database
        updateParkingSpot(location, price, services, zipCode, city, photoFilePath);
    }

    public static void setCurrentParkingSpot(ParkingSpot parkingSpot) {
        currentParkingSpot = parkingSpot;
    }
}
