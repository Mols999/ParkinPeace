package com.example.parkingpeace;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    public void initialize() {
    }

    @FXML
    private void handleEditButton() {
        // Handle the logic for editing the profile
        System.out.println("Edit button clicked");
    }

    @FXML
    private void handleChangePasswordButton() {
        // Handle the logic for changing the password
        System.out.println("Change Password button clicked");
    }

    public void setStage(Stage stage) {
        // Optionally, you can perform any additional initialization here
    }
}
