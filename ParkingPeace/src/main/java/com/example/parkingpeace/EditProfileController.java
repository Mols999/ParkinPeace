package com.example.parkingpeace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditProfileController {

    private DB db;

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

    private String userID;
    private String role;

    public void setUserData(String userID, String role) {
        this.userID = userID;
        this.role = role;

        // Load user data from the database
        loadUserData();
    }

    public void initialize() {
        db = new DB();
    }

    private void loadUserData() {
        String sql = "";
        if (role.equals("Customer")) {
            sql = "SELECT * FROM tblCustomer WHERE fldCustomerID = ?";
        } else if (role.equals("Landlord")) {
            sql = "SELECT * FROM tblLandlord WHERE fldLandlordID = ?";
        } else if (role.equals("Admin")) {
            sql = "SELECT * FROM tblAdmin WHERE fldAdminID = ?";
        }

        try {
            ResultSet rs = db.selectSQLWithResultParams(sql, userID);
            if (rs != null && rs.next()) {
                usernameField.setText(rs.getString("fldUsername"));
                nameField.setText(rs.getString("fldName"));
                ageField.setText(String.valueOf(rs.getInt("fldAge")));
                emailField.setText(rs.getString("fldEmail"));
                passwordField.setText(rs.getString("fldPassword"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSaveChangesButton(ActionEvent event) {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText()); // Make sure to handle NumberFormatException
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        String sql = "";
        if (role.equals("Customer")) {
            sql = "UPDATE tblCustomer SET fldCustomerName = ?, fldCustomerAge = ?, fldUsername = ?, fldEmail = ?, fldPassword = ? WHERE fldCustomerID = ?";
        } else if (role.equals("Landlord")) {
            sql = "UPDATE tblLandlord SET fldLandlordName = ?, fldLandlordAge = ?, fldUsername = ?, fldEmail = ?, fldPassword = ? WHERE fldLandlordID = ?";
        } else if (role.equals("Admin")) {
            sql = "UPDATE tblAdmin SET fldAdminName = ?, fldAdminAge = ?, fldUsername = ?, fldEmail = ?, fldPassword = ? WHERE fldAdminID = ?";
        }

        boolean success = db.updateSQL(sql, name, age, username, email, password, userID);
        if (success) {
            System.out.println("User data updated successfully!");
        } else {
            System.out.println("There was an error updating the user data.");
        }
    }



}
