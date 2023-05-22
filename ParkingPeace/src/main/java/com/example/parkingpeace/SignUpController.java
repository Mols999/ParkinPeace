package com.example.parkingpeace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private PasswordField passwordField;

    private String role = ""; // added this variable to hold the role

    private DB db; // Reference to the DB class

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
        String email = ""; // Get this value from the user input. You need to add an email field in your form.
        String password = passwordField.getText();

        if (role.equals("")) {
            // Handle case where neither Landlord nor Customer button was clicked
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
        try {
            // Load the Login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Get the controller of the Login.fxml file
            LoginController loginController = loader.getController();

            // Set the current stage to the login scene
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
