package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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


    private String landlordID;

    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }



    @FXML
    public void handleSaveButton() {
        String location = locationField.getText();
        String priceText = priceField.getText();
        String services = servicesField.getText();
        String zipCode = zipCodeField.getText();
        String city = cityField.getText();
        String photoFilePath = photoFilePathField.getText();

        // Check if any of the fields are empty
        if(location.isEmpty() || services.isEmpty() || zipCode.isEmpty() || city.isEmpty() || photoFilePath.isEmpty() || priceText.isEmpty()){
            displayErrorAlert("Please fill in all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            displayErrorAlert("Price must be a valid number.");
            return;
        }

        boolean success = createParkingSpot(location, services, price, landlordID, zipCode, city, photoFilePath);

        // Clear the form
        locationField.clear();
        priceField.clear();
        servicesField.clear();
        zipCodeField.clear();
        cityField.clear();
        photoFilePathField.clear();

        // Display result
        if(success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Parking Spot Created Successfully.");
            alert.showAndWait();
            Stage stage = (Stage) locationField.getScene().getWindow();
            SceneSwitcher.switchToScene("LandlordDashboard.fxml", "Landlord Dashboard", stage);
        } else {
            displayErrorAlert("There was an error creating the parking spot.");
        }
    }

    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    public boolean createParkingSpot(String location, String services, double price, String landlordId, String zipCode, String city, String photoFilePath) {
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
