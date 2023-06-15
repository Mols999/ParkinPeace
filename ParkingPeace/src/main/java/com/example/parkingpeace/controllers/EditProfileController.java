package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

// Controller for the EditProfile view
public class EditProfileController {
    // Declare FXML elements
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

    @FXML
    Button homeFromEditLandlord;


    // Declare user details variables
    private String username;
    private String name;
    private int age;
    private String email;
    private String password;

    // Declare the database object
    private DB db;

    // Declare IDs for different types of users
    private String customerID;
    private String landlordID;
    private String adminID;

    // Constructor initializes the database object and user IDs
    public EditProfileController() {
        db = new DB();
        this.customerID = "";
        this.landlordID = "";
        this.adminID = "";
    }

    // Method to handle the Save Changes button click event
    @FXML
    protected void handleSaveChangesButton() {
        // Validate the input before proceeding
        if (validateInput()) {
            // Get the values from the form fields
            username = usernameField.getText();
            name = nameField.getText();
            age = Integer.parseInt(ageField.getText());
            email = emailField.getText();
            password = passwordField.getText();

            boolean isUpdated = false;
            String sql;

            // Prepare an alert for successful operation
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);

            // If user is a landlord, update the landlord's details in the database
            if (!landlordID.isEmpty()) {
                sql = "UPDATE tblLandlord SET fldUsername = ?, fldLandlordName = ?, fldLandlordAge = ?, fldEmail = ?, fldPassword = ? WHERE fldLandlordID = ?";
                isUpdated = db.updateSQLWithParams(sql, username, name, age, email, password, landlordID);
            }
            // If user is a customer, update the customer's details in the database
            else if (!customerID.isEmpty()) {
                sql = "UPDATE tblCustomer SET fldUsername = ?, fldCustomerName = ?, fldCustomerAge = ?, fldEmail = ?, fldPassword = ? WHERE fldCustomerID = ?";
                isUpdated = db.updateSQLWithParams(sql, username, name, age, email, password, customerID);
            }

            // Show success message and redirect user to Login scene if update successful
            if (isUpdated) {
                alert.setContentText("Profile changes saved!");
                alert.showAndWait();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                SceneSwitcher.switchToScene("Login.fxml", "Login", stage);
            } else {
                alert.setContentText("Failed to save changes. Please try again.");
                alert.showAndWait();
            }
        }
    }

    // Method to validate the input
    private boolean validateInput() {
        // Check if any of the fields are empty
        if (usernameField.getText().isEmpty() || nameField.getText().isEmpty() ||
                ageField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return false;
        }
        // Validate that the age field contains a valid integer
        try {
            Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid age.");
            alert.showAndWait();
            return false;
        }
        return true; // Return true if all fields are valid
    }

    // Method to handle the Delete Account button click event
    @FXML
    protected void handleDeleteAccountButton() {
        // Ask for confirmation before deleting the account
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Account");
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        // Wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isDeleted = false;
            String sql;

            // If user is a landlord, delete the landlord's account from the database
            if (!landlordID.isEmpty()) {
                sql = "DELETE FROM tblLandlord WHERE fldLandlordID = ?";
                isDeleted = db.updateSQLWithParams(sql, landlordID);
            }
            // If user is a customer, delete the customer's account from the database
            else if (!customerID.isEmpty()) {
                sql = "DELETE FROM tblCustomer WHERE fldCustomerID = ?";
                isDeleted = db.updateSQLWithParams(sql, customerID);
            }

            // Show success message and redirect user to Login scene if account deleted successfully
            if (isDeleted) {
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Your account has been deleted.");
                successAlert.showAndWait();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                SceneSwitcher.switchToScene("Login.fxml", "Login", stage);
            } else {
                // Show error message if account deletion failed
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to delete account. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }

    // Method to navigate to the home page
    @FXML
    private void navigateToLandlordDashboard(ActionEvent event) throws IOException {

        }

    @FXML
    public void navigateToLandlordDashboard() throws IOException {
        if (LoginController.getLandlordID() == null) {
            Stage stage = (Stage) homeFromEditLandlord.getScene().getWindow();
            SceneSwitcher.switchToLandlordDashboard(stage);
        }
        else if (LoginController.getCustomerID()){
                Stage stage = (Stage) homeFromEditLandlord.getScene().getWindow();
                SceneSwitcher.switchToHomePage(stage);
        }
    }

    // Method to set the user IDs
    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;
    }


}
