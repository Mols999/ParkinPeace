package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

// Controller for the CreateParkingSpot view
public class CreateParkingSpotController {
    // Declare FXML elements
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
    @FXML
    Button BackButton;

    // Declare the database object
    private DB db;

    // Constructor initializes the database object
    public CreateParkingSpotController() {
        db = new DB();
    }

    // Declare the landlord ID
    private String landlordID;

    // Setter for the landlord ID
    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }

    // Method to handle the Save button click event
    @FXML
    public void handleSaveButton() {
        // Retrieve the text from the form fields
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
            // Parse the price text to a double
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            displayErrorAlert("Price must be a valid number.");
            return;
        }

        // Create a new parking spot and get the result
        boolean success = createParkingSpot(location, services, price, landlordID, zipCode, city, photoFilePath);

        // Clear the form fields
        locationField.clear();
        priceField.clear();
        servicesField.clear();
        zipCodeField.clear();
        cityField.clear();
        photoFilePathField.clear();

        // Display the result
        if(success) {
            // If successful, display a success alert and navigate to the Landlord Dashboard
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Parking Spot Created Successfully.");
            alert.showAndWait();
            Stage stage = (Stage) locationField.getScene().getWindow();
            SceneSwitcher.switchToScene("LandlordDashboard.fxml", "Landlord Dashboard", stage);
        } else {
            // If unsuccessful, display an error alert
            displayErrorAlert("There was an error creating the parking spot.");
        }
    }

    // Method to display an error alert
    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to create a new parking spot in the database
    public boolean createParkingSpot(String location, String services, double price, String landlordId, String zipCode, String city, String photoFilePath) {
        // Generate a new parking spot ID
        String parkingSpotId = generateParkingSpotId();
        // Initially set availability to 0
        String availability = "0";

        // Define the SQL statement for inserting a new parking spot
        String sql = "INSERT INTO tblParkingSpot (fldParkingSpotID, fldLocation, fldAvailability, fldPrice, fldServices, fldLandlordID, fldZipCode, fldCity, fldPhotoFilePath) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Execute the SQL statement and return the result
        return db.insertSQL(sql, parkingSpotId, location, availability, price, services, landlordId, zipCode, city, photoFilePath);
    }

    // Method to generate a new parking spot ID
    private String generateParkingSpotId() {
        // Instantiate a Random object
        Random rand = new Random();
        // Generate a random 4-digit number and return it as a string
        return String.format("%04d", rand.nextInt(10000));
    }

    @FXML
    public void HandleBackButton() throws IOException {
        Stage stage = (Stage) BackButton.getScene().getWindow();
     SceneSwitcher.switchToLandlordDashboard(stage);

    }
}
