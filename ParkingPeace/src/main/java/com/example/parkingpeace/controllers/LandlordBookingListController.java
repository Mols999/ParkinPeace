package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.Booking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LandlordBookingListController implements Initializable {
    @FXML
    private ListView<VBox> bookingListView;
    private DB db;
    private String landlordID;



    public void initialize(URL location, ResourceBundle resources) {
        db = new DB();
        landlordID = LoginController.getLandlordID();
        loadBookingList();
    }


    private void loadBookingList() {
        List<Booking> bookings = db.fetchBookingsByLandlord(Integer.parseInt(landlordID));

        if (bookings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Booking List", "No bookings found.");
        } else {
            bookingListView.getItems().clear();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookings) {
                String bookingID = String.valueOf(booking.getBookingID());
                String parkingSpotID = String.valueOf(booking.getParkingSpotID());
                Timestamp startDateTime = Timestamp.valueOf(booking.getStartDateTime());
                Timestamp endDateTime = Timestamp.valueOf(booking.getEndDateTime());
                String bookingStatus = booking.getBookingStatus();

                // Fetch the customer ID from the database
                int customerID = db.getCustomerIDFromBooking(Integer.parseInt(bookingID));

                // Fetch the customer name using the customer ID
                String customerName = db.getCustomerName(customerID);

                VBox bookingDetailsBox = new VBox();

                Label bookingLabel = new Label();
                bookingLabel.setText("Booking ID: " + bookingID + "\n"
                        + "Customer Name: " + customerName + "\n"
                        + "Parking Spot ID: " + parkingSpotID + "\n"
                        + "Start Date/Time: " + dateFormat.format(startDateTime) + "\n"
                        + "End Date/Time: " + dateFormat.format(endDateTime) + "\n"
                        + "Booking Status: " + bookingStatus + "\n");

                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(event -> deleteBooking(bookingID));

                Button reviewButton = new Button("Review");
                reviewButton.setOnAction(event -> openReviewWindow(bookingID, customerID));

                HBox buttonsBox = new HBox(deleteButton, reviewButton);
                buttonsBox.setSpacing(10);

                bookingDetailsBox.getChildren().addAll(bookingLabel, buttonsBox);
                bookingListView.getItems().add(bookingDetailsBox);
            }
        }
    }


    private void openReviewWindow(String bookingID, int customerID) {
        try {
            String customerIDString = String.valueOf(customerID);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeLandlordReview.fxml"));
            Parent root = loader.load();
            MakeLandlordReviewController makeLandlordReviewController = loader.getController();
            makeLandlordReviewController.setBookingID(Integer.parseInt(bookingID));
            makeLandlordReviewController.setCustomerID(customerIDString);

            Stage stage = (Stage) bookingListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Make Review");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteBooking(String bookingID) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this booking?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = db.deleteBooking(Integer.parseInt(bookingID));

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Booking Deletion", "Booking deleted successfully.");
                loadBookingList();
            } else {
                showAlert(Alert.AlertType.ERROR, "Booking Deletion", "Failed to delete booking.");
            }
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    private void navigateToHomePage() throws IOException {
        Stage stage = (Stage) bookingListView.getScene().getWindow();
        SceneSwitcher.switchToHomePage(stage);
    }
}
