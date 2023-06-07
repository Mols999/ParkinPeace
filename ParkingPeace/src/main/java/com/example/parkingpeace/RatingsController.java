package com.example.parkingpeace;

import com.example.parkingpeace.DB;
import com.example.parkingpeace.Review;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RatingsController implements Initializable {

    @FXML
    private ListView<Review> ratingsListView;

    private DB db;
    private String customerID;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DB();
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
        loadRatings();
    }

    private void loadRatings() {
        List<Review> reviews = db.fetchCustomerReviews(customerID);

        System.out.println("Customer ID: " + customerID); // Add this line to check the value of customerID

        if (reviews.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Ratings and Comments", "No ratings and comments found.");
        } else {
            ratingsListView.getItems().addAll(reviews);
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


    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
