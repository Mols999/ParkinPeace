package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private Stage stage;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    // Static variables to store the IDs
    private static String customerID = "";
    private static String landlordID = "";
    private static String adminID = "";

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    // Getter for customerID
    public static String getCustomerID() {
        return customerID;
    }
    // Getter for landlordID
    public static String getLandlordID() {
        return landlordID;
    }


    @FXML
    public void handleGoToSignUpButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();
            SignUpController signUpController = loader.getController();
            signUpController.setStage(stage); // Pass the stage object to the SignUpController

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            DB db = new DB();

            String sql = "SELECT fldUsername, fldPassword, fldCustomerID FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?";
            ResultSet rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle customer login
                System.out.println("Login successful as customer!");
                customerID = rs.getString("fldCustomerID"); // Retrieve the customer ID from the database
                navigateToHomePage();
                return;
            }

            sql = "SELECT fldUsername, fldPassword, fldLandlordID FROM tblLandlord WHERE fldUsername = ? AND fldPassword = ?";
            rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle landlord login
                System.out.println("Login successful as landlord!");
                landlordID = rs.getString("fldLandlordID"); // Retrieve the landlord ID from the database
                navigateToHomePage();
                return;
            }

            sql = "SELECT fldUsername, fldPassword, fldAdminID FROM tblAdmin WHERE fldUsername = ? AND fldPassword = ?";
            rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle admin login
                System.out.println("Login successful as admin!");
                adminID = rs.getString("fldAdminID"); // Retrieve the admin ID from the database
                navigateToHomePage();
                return;
            }

            errorLabel.setText("Incorrect login credentials");
            errorLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException | SQLException e) {
            errorLabel.setText("An error occurred. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    private void navigateToHomePage() throws IOException {
        FXMLLoader loader;
        Parent root;
        if (!landlordID.isEmpty()) {
            loader = new FXMLLoader(getClass().getResource("LandlordDashboard.fxml"));
            root = loader.load();
            LandlordDashboardController landlordHomeController = loader.getController();
            stage.setTitle("Landlord Home Page");
        } else if (!adminID.isEmpty()) {
            loader = new FXMLLoader(getClass().getResource("AdminHomePage.fxml"));
            root = loader.load();
            // Placeholder
            stage.setTitle("Admin Dashboard");
        } else {
            loader = new FXMLLoader(getClass().getResource("CustomerDashboard.fxml"));
            root = loader.load();
            CustomerDashboardController customerDashboardController = loader.getController();
            stage.setTitle("Home Page");
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
