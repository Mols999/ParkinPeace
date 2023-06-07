package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

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

    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;
    }
}
