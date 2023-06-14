package com.example.parkingpeace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;


public class LandlordBookingListController {

    @FXML
    Button landlordHomeButton;

   @FXML
   public void navigateToLandlordDashboard() throws IOException {
       Stage stage = (Stage) landlordHomeButton.getScene().getWindow();
       SceneSwitcher.switchToLandlordDashboard(stage);

   }

}
