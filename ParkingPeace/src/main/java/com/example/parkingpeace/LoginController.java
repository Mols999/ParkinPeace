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

        try {
            DB db = new DB();

            String sql = "SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle customer login
                System.out.println("Login successful as customer!");
                navigateToHomePage();
                return;
            }

            sql = "SELECT fldUsername, fldPassword FROM tblLandlord WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle landlord login
                System.out.println("Login successful as landlord!");
                navigateToHomePage();
                return;
            }

            sql = "SELECT fldUsername, fldPassword FROM tblAdmin WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle admin login
                System.out.println("Login successful as admin!");
                navigateToHomePage();
                return;
            }

            // If we reach here, login has failed
            errorLabel.setText("Incorrect login credentials");
            errorLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException e) {
            errorLabel.setText("An error occurred. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void navigateToHomePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        Parent root = loader.load();
        HomeController homeController = loader.getController();
        homeController.setStage(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Home Page");
        stage.show();
    }
}