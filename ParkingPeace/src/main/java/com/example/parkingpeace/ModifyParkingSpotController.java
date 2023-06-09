package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ModifyParkingSpotController {

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

    private String parkingSpotID; // Keep track of the ID to update the right spot

    public void setParkingSpotDetails(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        this.parkingSpotID = parkingSpotID; // Save the ID

        // Set the fields to the current values
        locationField.setText(location);
        availabilityField.setText(availability);
        priceField.setText(price);
        servicesField.setText(services);
        zipCodeField.setText(zipCode);
        cityField.setText(city);
        photoFilePathField.setText(photoFilePath);
    }

    @FXML
    public void handleSaveButton() {
        // Here you will update the parking spot in the database using the new values and the parkingSpotID
        // String newLocation = locationField.getText();
        // String newAvailability = availabilityField.getText();
        // ...
        // db.updateParkingSpot(newLocation, newAvailability, ..., parkingSpotID);
    }
}
