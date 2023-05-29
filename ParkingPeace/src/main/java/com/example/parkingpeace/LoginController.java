package com.example.parkingpeace;

import com.example.parkingpeace.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        SceneSwitcher.switchToScene("SignUp.fxml", "Sign Up", stage);
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        ResultSet resultSet = null;

        try {
            DB db = new DB();

            String sql = "SELECT * FROM tblCustomer WHERE fldUsername = ? AND fldPassword = ?";
            resultSet = db.selectSQLWithParams(sql, username, password);
            if (resultSet.next()) {
                System.out.println("SQL Query: " + sql);
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                // Handle customer login
                System.out.println("Login successful as customer!");
                int customerId = resultSet.getInt("fldCustomerId");
                String customerName = resultSet.getString("fldCustomerName");
                int customerAge = resultSet.getInt("fldCustomerAge");
                String email = resultSet.getString("fldEmail");
                double rating = resultSet.getDouble("fldRating");
                UserSession.getInstance(username, "customer", customerId, customerName, customerAge, email, password, rating);
                navigateToHomePage();
                return;
            }
            resultSet.close(); // Close the result set after use

            sql = "SELECT * FROM tblLandlord WHERE fldUsername = ? AND fldPassword = ?";
            resultSet = db.selectSQLWithParams(sql, username, password);
            if (resultSet.next()) {
                // Handle landlord login
                System.out.println("Login successful as landlord!");
                int landlordId = resultSet.getInt("fldLandlordId");
                String landlordName = resultSet.getString("fldLandlordName");
                int landlordAge = resultSet.getInt("fldLandlordAge");
                String email = resultSet.getString("fldEmail");
                double rating = resultSet.getDouble("fldRating");
                UserSession.getInstance(username, "landlord", landlordId, landlordName, landlordAge, email, password, rating);
                navigateToHomePage();
                return;
            }
            resultSet.close(); // Close the result set after use

            sql = "SELECT * FROM tblAdmin WHERE fldUsername = ? AND fldPassword = ?";
            resultSet = db.selectSQLWithParams(sql, username, password);
            if (resultSet.next()) {
                // Handle admin login
                System.out.println("Login successful as admin!");
                int adminId = resultSet.getInt("fldAdminId");
                String adminName = resultSet.getString("fldAdminName");
                int adminAge = resultSet.getInt("fldAdminAge");
                String email = resultSet.getString("fldEmail");
                double rating = resultSet.getDouble("fldRating");
                UserSession.getInstance(username, "admin", adminId, adminName, adminAge, email, password, rating);
                navigateToHomePage();
                return;
            }

            // If we reach here, login has failed
            System.out.println("Login failed. Incorrect login credentials.");
            errorLabel.setText("Incorrect login credentials");
            errorLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException e) {
            errorLabel.setText("An error occurred while accessing the database. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } catch (SQLException e) {
            errorLabel.setText("An error occurred while executing the SQL query. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void navigateToHomePage() throws IOException {
        Stage homeStage = (Stage) usernameField.getScene().getWindow();

        SceneSwitcher.switchToScene("HomePage.fxml", "Home Page", homeStage);

        // Close the current login stage
        stage.close();
    }
}
