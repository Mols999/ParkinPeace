package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    private String role = "";
    private DB db;
    private Stage stage; // Add the stage field




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

            // Navigate to the login screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parkingpeace/Login.fxml"));
                Parent root = loader.load();

                LoginController loginController = loader.getController();
                loginController.setStage(stage); // Pass the stage object to the LoginController

                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("There was an error signing up the user.");
        }
    }



    @FXML
    public void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/parkingpeace/Login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            loginController.setStage(stage); // Pass the stage object to the LoginController

            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
