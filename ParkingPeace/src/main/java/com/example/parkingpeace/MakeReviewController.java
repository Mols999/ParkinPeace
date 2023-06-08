package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MakeReviewController {

    @FXML
    private TextField ratingValueField;
    @FXML
    private TextArea reviewCommentField;
    @FXML
    private Button saveButton;

    private int parkingSpotID;
    private int landlordID;
    private DB db;

    public MakeReviewController() {
        db = new DB();
    }

    public void setParkingSpotID(int parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
        this.landlordID = db.getLandlordIDFromParkingSpot(parkingSpotID);
    }

    @FXML
    protected void handleSaveButton() {
        int ratingValue = Integer.parseInt(ratingValueField.getText());
        String reviewComment = reviewCommentField.getText();

        // Save the review in the database
        boolean success = db.addReview(landlordID, ratingValue, reviewComment);

        if (success) {
            showAlert(AlertType.INFORMATION, "Review Saved", "Your review has been saved.");
        } else {
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
}
