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

    @FXML
    private HomeController homeController;

    private Stage stage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private DB db;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        db = new DB();
    }

    @FXML
    public void handleGoToSignUpButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sign Up"); // Set the title of the new scene
            stage.show(); // Make the new scene visible
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            String sql = "SELECT fldUsername, fldPassword FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle customer login
                System.out.println("Login successful as customer!");
                // Load home page FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
                Parent root = loader.load();
                homeController = loader.getController();
                homeController.setStage(stage);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Home Page");
                stage.show();
                return;
            }

            sql = "SELECT fldUsername, fldPassword FROM tblLandlord WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle landlord login
                System.out.println("Login successful as landlord!");
                // Load home page FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
                Parent root = loader.load();
                homeController = loader.getController();
                homeController.setStage(stage);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Home Page");
                stage.show();
                return;
            }

            sql = "SELECT fldUsername, fldPassword FROM tblAdmin WHERE fldUsername = ? AND fldPassword = ?";
            if (db.selectSQLWithParams(sql, username, password)) {
                // Handle admin login
                System.out.println("Login successful as admin!");
                // Load home page FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
                Parent root = loader.load();
                homeController = loader.getController();
                homeController.setStage(stage);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Home Page");
                stage.show();
                return;
            }

            // If we reach here, login has failed
            errorLabel.setText("Incorrect login credentials");
            errorLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException e) {
            errorLabel.setText("An error occurred. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } finally {
            db.close(); // Close the database connection or result set
        }
    }
}
