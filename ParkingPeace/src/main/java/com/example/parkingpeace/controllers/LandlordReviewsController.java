package com.example.parkingpeace.controllers;

import com.example.parkingpeace.models.DB;
import com.example.parkingpeace.models.Review;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LandlordReviewsController implements Initializable {
    @FXML
    private ListView<Review> ratingsListView;
    @FXML
    private Button homeButton;
    private DB db;
    private String landlordID;
    private ObservableList<Review> reviewsList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DB();
        reviewsList = FXCollections.observableArrayList();
        landlordID = LoginController.getLandlordID(); // Get the landlordID from LoginController
        loadRatings();
    }


    // Load the ratings and comments for the landlord
    private void loadRatings() {
        List<Review> reviews = db.fetchLandlordReviews(landlordID);

        System.out.println("Landlord ID: " + landlordID); // Add this line to check the value of landlordID

        if (reviews.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Ratings and Comments", "No ratings and comments found.");
            closeStage();
        } else {
            reviewsList.setAll(reviews);
            ratingsListView.setItems(reviewsList);
            ratingsListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<Review>() {
                @Override
                public String toString(Review review) {
                    return "Rating Value: " + review.getRatingValue() + ", Comment: " + review.getRatingComment();
                }

                @Override
                public Review fromString(String string) {
                    return null;
                }
            }));
        }
    }


    // Show an alert dialog with the specified type, title, and content
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Close the current stage
    private void closeStage() {
        Stage currentStage = (Stage) ratingsListView.getScene().getWindow();
        currentStage.close();
    }


    // Navigate to the landlord dashboard scene
    @FXML
    private void navigateToLandlordDashboard() throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        SceneSwitcher.switchToLandlordDashboard(stage);
    }
}
