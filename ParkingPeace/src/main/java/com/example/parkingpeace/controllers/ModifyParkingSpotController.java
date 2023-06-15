package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifyParkingSpotController {

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
    Button deleteParkingspotButton;


    private String parkingSpotID;
    private String landlordID;

    private ParkingSpot currentParkingSpot;

    public void setCurrentParkingSpot(ParkingSpot parkingSpot) {
        this.currentParkingSpot = parkingSpot;
        fillTextFieldsWithCurrentInfo();
    }

    @FXML
    private void initialize() {
        if (currentParkingSpot != null) {
            fillTextFieldsWithCurrentInfo();
        } else {
            System.out.println("No current parking spot exists.");
        }
        landlordID = LoginController.getLandlordID();
    }

    private void fillTextFieldsWithCurrentInfo() {
        locationField.setText(currentParkingSpot.getLocation());
        priceField.setText(currentParkingSpot.getPrice());
        servicesField.setText(currentParkingSpot.getServices());
        zipCodeField.setText(currentParkingSpot.getZipCode());
        cityField.setText(currentParkingSpot.getCity());
        photoFilePathField.setText(currentParkingSpot.getPhotoFilePath());
    }

    public boolean updateParkingSpot(String location, double price, String services, String zipCode, String city, String photoFilePath) {
        String sql = "UPDATE tblParkingSpot SET fldLocation = ?, fldPrice = ?, fldServices = ?, fldZipCode = ?, fldCity = ?, fldPhotoFilePath = ? WHERE fldParkingSpotID = ?";

        try {
            DB db = new DB();
            return db.updateSQLWithParams(sql, location, price, services, zipCode, city, photoFilePath, parkingSpotID);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    @FXML
    public void handleSaveButton(ActionEvent event) {
        String location = locationField.getText();
        double price = Double.parseDouble(priceField.getText());
        String services = servicesField.getText();
        String zipCode = zipCodeField.getText();
        String city = cityField.getText();
        String photoFilePath = photoFilePathField.getText();

        // Set the parkingSpotID based on the currentParkingSpot
        if (currentParkingSpot != null) {
            parkingSpotID = currentParkingSpot.getParkingSpotID();
        }

        boolean success = updateParkingSpot(location, price, services, zipCode, city, photoFilePath);

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Parking Spot Modified Successfully.");
            alert.showAndWait();

            Stage stage = (Stage) locationField.getScene().getWindow();
            SceneSwitcher.switchToScene("LandlordDashboard.fxml", "Landlord Dashboard", stage);

            // Reload parking spots in the LandlordDashboardController
            LandlordDashboardController landlordDashboardController = (LandlordDashboardController) SceneSwitcher.getCurrentController();
            landlordDashboardController.loadParkingSpotsForLandlord(landlordID);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("There was an error modifying the parking spot.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleDeleteParkingspotButton (String location){

    }
}
