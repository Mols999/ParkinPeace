package com.example.parkingpeace;

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


    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    public void handleGoToSignUpButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
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
        String customerID = ""; // Initialize an empty string to store the customer ID
        String landlordID = ""; // Initialize an empty string to store the landlord ID
        String adminID = ""; // Initialize an empty string to store the admin ID



        try {
            DB db = new DB();

            String sql = "SELECT fldUsername, fldPassword, fldCustomerID FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?";
            ResultSet rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle customer login
                System.out.println("Login successful as customer!");
                customerID = rs.getString("fldCustomerID"); // Retrieve the customer ID from the database
                navigateToHomePage(customerID, landlordID, adminID);
                return;
            }

            sql = "SELECT fldUsername, fldPassword, fldLandlordID FROM tblLandlord WHERE fldUsername = ? AND fldPassword = ?";
            rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle landlord login
                System.out.println("Login successful as landlord!");
                landlordID = rs.getString("fldLandlordID"); // Retrieve the landlord ID from the database
                navigateToHomePage(customerID, landlordID, adminID);
                return;
            }

            sql = "SELECT fldUsername, fldPassword, fldAdminID FROM tblAdmin WHERE fldUsername = ? AND fldPassword = ?";
            rs = db.selectSQLWithResultParams(sql, username, password);
            if (rs != null && rs.next()) {
                // Handle admin login
                System.out.println("Login successful as admin!");
                adminID = rs.getString("fldAdminID"); // Retrieve the admin ID from the database
                navigateToHomePage(customerID, landlordID, adminID);
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

    private void navigateToHomePage(String customerID, String landlordID, String adminID) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();
        homeController.setStage(stage);
        homeController.setIDs(customerID, landlordID, adminID); // Set the IDs in HomeController
        homeController.setCustomerID(customerID); // Pass the customerID to HomeController
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Home Page");
        stage.show();
    }

}