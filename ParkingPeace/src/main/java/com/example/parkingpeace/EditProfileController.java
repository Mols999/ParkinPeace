package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.util.Optional;

public class EditProfileController {
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

    private String username;
    private String name;
    private int age;
    private String email;
    private String password;

    private DB db; // Database connection object

    private String customerID;
    private String landlordID;
    private String adminID;

    public EditProfileController() {
        db = new DB();
        // Initialize these variables to empty strings
        this.customerID = "";
        this.landlordID = "";
        this.adminID = "";
    }

    @FXML
    protected void handleSaveChangesButton() {
        if (validateInput()) {
            username = usernameField.getText();
            name = nameField.getText(); // Get the value from the nameField
            age = Integer.parseInt(ageField.getText()); // Get the value from the ageField
            email = emailField.getText();
            password = passwordField.getText();

            boolean isUpdated = false;
            String sql;
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);

            // Check if user is a landlord
            if (!landlordID.isEmpty()) {
                // Update landlord in the database, including name and age
                sql = "UPDATE tblLandlord SET fldUsername = ?, fldLandlordName = ?, fldLandlordAge = ?, fldEmail = ?, fldPassword = ? WHERE fldLandlordID = ?";
                isUpdated = db.updateSQLWithParams(sql, username, name, age, email, password, landlordID);
            }
            // Check if user is a customer
            else if (!customerID.isEmpty()) {
                // Update customer in the database, including name and age
                sql = "UPDATE tblCustomer SET fldUsername = ?, fldCustomerName = ?, fldCustomerAge = ?, fldEmail = ?, fldPassword = ? WHERE fldCustomerID = ?";
                isUpdated = db.updateSQLWithParams(sql, username, name, age, email, password, customerID);
            }

            if (isUpdated) {
                alert.setContentText("Profile changes saved!");
                alert.showAndWait();

                // Switch to the Login scene
                Stage stage = (Stage) usernameField.getScene().getWindow();
                SceneSwitcher.switchToScene("Login.fxml", "Login", stage);
            } else {
                alert.setContentText("Failed to save changes. Please try again.");
                alert.showAndWait();
            }
        }
    }



    private boolean validateInput() {
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
        // Other validation could go here, for example checking that ageField contains a valid integer
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
        return true;
    }

    @FXML
    protected void handleDeleteAccountButton() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete Account");
        alert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        // Show the confirmation dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isDeleted = false;
            String sql;

            // Check if user is a landlord
            if (!landlordID.isEmpty()) {
                // Delete landlord from the database
                sql = "DELETE FROM tblLandlord WHERE fldLandlordID = ?";
                isDeleted = db.updateSQLWithParams(sql, landlordID);
            }
            // Check if user is a customer
            else if (!customerID.isEmpty()) {
                // Delete customer from the database
                sql = "DELETE FROM tblCustomer WHERE fldCustomerID = ?";
                isDeleted = db.updateSQLWithParams(sql, customerID);
            }

            if (isDeleted) {
                // Account successfully deleted
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Your account has been deleted.");
                successAlert.showAndWait();

                // Switch to the Login scene
                Stage stage = (Stage) usernameField.getScene().getWindow();
                SceneSwitcher.switchToScene("Login.fxml", "Login", stage);
            } else {
                // Failed to delete account
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to delete account. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }



    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;
    }
}
