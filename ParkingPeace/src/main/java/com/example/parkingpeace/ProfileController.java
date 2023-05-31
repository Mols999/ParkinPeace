package com.example.parkingpeace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProfileController {

    private Stage stage;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Retrieve the profile details from the database and populate the fields
    public void initialize() throws SQLException {
        // Retrieve profile details based on the username
        DB db = new DB();
        String username = "username"; // Replace "username" with the actual username

        String sql = "SELECT fldCustomerName, fldCustomerAge, fldEmail FROM tblCustomer WHERE fldUsername = ?";
        boolean exists = db.selectSQLWithParams(sql, username);

        if (exists) {
            String name = ""; // Initialize with empty values
            String age = "";
            String email = "";

            // Retrieve the profile details
            // Assign retrieved values to the name, age, and email variables

            nameField.setText(name);
            ageField.setText(age);
            emailField.setText(email);
        }
    }

    @FXML
    public void handleSaveChangesButton(ActionEvent event) {
        // Retrieve the updated profile details from the fields
        String name = nameField.getText();
        String age = ageField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Update the profile details in the database
        DB db = new DB();
        String username = "username"; // Replace "username" with the actual username

        String sql = "UPDATE tblCustomer SET fldCustomerName = ?, fldCustomerAge = ?, fldEmail = ?, fldPassword = ? WHERE fldUsername = ?";
        boolean updated = db.updateSQLWithParams(sql, name, age, email, password, username);

        if (updated) {
            statusLabel.setText("Profile details updated successfully");
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setText("An error occurred. Please try again.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        SceneSwitcher.switchToScene("HomePage.fxml", "Home Page", stage);
    }
}
