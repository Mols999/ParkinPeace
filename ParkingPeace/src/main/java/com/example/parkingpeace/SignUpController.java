package com.example.parkingpeace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

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
    private String role = "";
    private DB db;

    public void initialize() {
        db = new DB();
    }

    @FXML
    public void handleLandlordButton(ActionEvent event) {
        role = "Landlord";
    }

    @FXML
    public void handleCustomerButton(ActionEvent event) {
        role = "Customer";
    }

    @FXML
    public void handleSignUpButton(ActionEvent event) {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText()); // Make sure to handle NumberFormatException
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (role.equals("")) {
            System.out.println("Please select a role (Landlord or Customer)");
            return;
        }

        String sql = "";
        if (role.equals("Customer")) {
            sql = "INSERT INTO tblCustomer (fldCustomerName, fldCustomerAge, fldUsername, fldEmail, fldPassword) VALUES (?, ?, ?, ?, ?)";
        } else if (role.equals("Landlord")) {
            sql = "INSERT INTO tblLandlord (fldLandlordName, fldLandlordAge, fldUsername, fldEmail, fldPassword) VALUES (?, ?, ?, ?, ?)";
        }

        boolean success = db.insertSQL(sql, name, age, username, email, password);
        if (success) {
            System.out.println("User signed up successfully!");
        } else {
            System.out.println("There was an error signing up the user.");
        }
    }

    @FXML
    public void handleBackButton(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneSwitcher.switchToScene("Login.fxml", "Login", stage);
    }
}