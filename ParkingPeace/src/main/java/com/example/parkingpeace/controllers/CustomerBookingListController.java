package com.example.parkingpeace.controllers;

import com.example.parkingpeace.models.DB;
import com.example.parkingpeace.models.Booking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerBookingListController implements Initializable {
    @FXML
    private ListView<VBox> bookingListView;
    private DB db;
    private String customerID;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DB();
        customerID = LoginController.getCustomerID(); // Retrieve the customer ID from the LoginController
        loadBookingList();
    }


    // Load the list of bookings for the customer
    private void loadBookingList() {
        List<Booking> bookings = db.fetchBookingsByCustomer(Integer.parseInt(customerID));

        if (bookings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Booking List", "No bookings found.");
        } else {
            bookingListView.getItems().clear();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookings) {
                String bookingID = String.valueOf(booking.getBookingID());
                String customerName = db.getCustomerName(booking.getCustomerID());
                String parkingSpotID = String.valueOf(booking.getParkingSpotID());
                Timestamp startDateTime = Timestamp.valueOf(booking.getStartDateTime());
                Timestamp endDateTime = Timestamp.valueOf(booking.getEndDateTime());
                String bookingStatus = booking.getBookingStatus();

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
                reviewButton.setOnAction(event -> openReviewWindow(parkingSpotID));

                bookingDetailsBox.getChildren().addAll(bookingLabel, deleteButton, reviewButton);
                bookingListView.getItems().add(bookingDetailsBox);
            }
        }
    }


    // Open the review window for a specific parking spot
    private void openReviewWindow(String parkingSpotID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeCustomerReview.fxml"));
            Parent root = loader.load();
            MakeCustomerReviewController makeCustomerReviewController = loader.getController();
            makeCustomerReviewController.setParkingSpotID(Integer.parseInt(parkingSpotID));
            Scene scene = new Scene(root);
            Stage stage = (Stage) bookingListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Make Review");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Delete a booking
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


    // Show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Navigate to the home page
    @FXML
    private void navigateToHomePage() throws IOException {
        Stage stage = (Stage) bookingListView.getScene().getWindow();
        SceneSwitcher.switchToHomePage(stage);
    }
}
