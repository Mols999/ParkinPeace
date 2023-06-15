package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerEditProfileController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSaveChangesButton() {
        // Get the values from the form fields
        String username = usernameField.getText();
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String email = emailField.getText();
        String password = passwordField.getText();

        // Update the customer's profile in the database
        DB db = new DB();
        String sql = "UPDATE tblCustomer SET fldUsername = ?, fldCustomerName = ?, fldCustomerAge = ?, fldEmail = ?, fldPassword = ? WHERE fldCustomerID = ?";
        boolean isUpdated = db.updateSQLWithParams(sql, username, name, age, email, password, LoginController.getCustomerID());
        if (isUpdated) {
            showAlert(AlertType.INFORMATION, "Success", "Profile changes saved!");
            navigateToHomePage();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to save changes. Please try again.");
        }
    }

    @FXML
    private void handleDeleteAccountButton() {
        // Ask for confirmation before deleting the account
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Account");
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        // Wait for the user's response
        if (alert.showAndWait().orElse(null) == ButtonType.OK) {
            // Delete the customer's account from the database
            DB db = new DB();
            String sql = "DELETE FROM tblCustomer WHERE fldCustomerID = ?";
            boolean isDeleted = db.updateSQLWithParams(sql, LoginController.getCustomerID());
            if (isDeleted) {
                showAlert(AlertType.INFORMATION, "Success", "Your account has been deleted.");
                System.exit(0);
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to delete account. Please try again.");
            }
        }
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToHomePage() {
        try {
            SceneSwitcher.switchToHomePage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
