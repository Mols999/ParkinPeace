package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class MakeLandlordReviewController {

    @FXML
    private TextField ratingValueField;
    @FXML
    private TextArea reviewCommentField;
    @FXML
    private Button saveButton;
    @FXML
    private Button homeButton;

    private int bookingID;
    private int customerID;
    private DB db;

    public MakeLandlordReviewController() {
        db = new DB();
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
        this.customerID = db.getCustomerIDFromBooking(bookingID);
    }

    @FXML
    protected void handleSaveButton() throws IOException {
        int ratingValue = Integer.parseInt(ratingValueField.getText());
        String reviewComment = reviewCommentField.getText();

        // Save the review in the database
        boolean success = db.addCustomerReview(customerID, ratingValue, reviewComment);

        if (success) {
            showAlert(AlertType.INFORMATION, "Review Saved", "Your review has been saved.");
            navigateToLandlordDashboard();
        } else {
            System.out.println(customerID);
            showAlert(AlertType.ERROR, "Error", "Failed to save the review.");
        }
    }



    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void navigateToLandlordDashboard() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        SceneSwitcher.switchToLandlordDashboard(stage);
    }

    public void setCustomerID(String customerIDString) {
    }
}
