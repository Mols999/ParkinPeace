package com.example.parkingpeace.controllers;

// Import necessary classes
import com.example.parkingpeace.models.Booking;
import com.example.parkingpeace.db.DB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

// Implement Initializable for initialization logic
public class BookingListController implements Initializable {

    // Declare a ListView of VBox (each VBox will represent a booking)
    @FXML
    private ListView<VBox> bookingListView;

    // Declare the database access object
    private DB db;

    private String customerID;


    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    // Initialize database object and load the booking list
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DB(); // Initialize the database access object
        loadBookingList(); // Load the booking list
    }

    // Method to load bookings into the ListView
    private void loadBookingList() {
        // Fetch bookings for the logged-in customer from the database
        List<Booking> bookings = db.fetchBookings(customerID);

        // Check if there are no bookings
        if (bookings.isEmpty()) {
            // If no bookings, display an informational alert
            showAlert(AlertType.INFORMATION, "Booking List", "No bookings found.");
        } else {
            // If there are bookings, clear old items in the list view
            bookingListView.getItems().clear();
            // Create a date format for displaying date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Loop through each booking
            for (Booking booking : bookings) {
                // Get booking details
                int bookingID = booking.getBookingID();
                String customerName = db.getCustomerName(booking.getCustomerID()); // Retrieve customer name
                int parkingSpotID = booking.getParkingSpotID();
                Timestamp startDateTime = Timestamp.valueOf(booking.getStartDateTime());
                Timestamp endDateTime = Timestamp.valueOf(booking.getEndDateTime());
                String bookingStatus = booking.getBookingStatus();

                // Create a VBox for each booking
                VBox bookingDetailsBox = new VBox();

                // Create and configure Label for booking details
                Label bookingLabel = new Label();
                bookingLabel.setText("Booking ID: " + bookingID + "\n"
                        + "Customer Name: " + customerName + "\n" // Display customer name
                        + "Parking Spot ID: " + parkingSpotID + "\n"
                        + "Start Date/Time: " + dateFormat.format(startDateTime) + "\n"
                        + "End Date/Time: " + dateFormat.format(endDateTime) + "\n"
                        + "Booking Status: " + bookingStatus + "\n");

                // Create delete button and attach delete action
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(event -> deleteBooking(bookingID));

                // Create review button and attach review window opening action
                Button reviewButton = new Button("Review");
                reviewButton.setOnAction(event -> openReviewWindow(parkingSpotID));

                // Add the label and buttons to the VBox
                bookingDetailsBox.getChildren().addAll(bookingLabel, deleteButton, reviewButton);

                // Add the VBox to the list view
                bookingListView.getItems().add(bookingDetailsBox);
            }
        }
    }

    // Method to open review window
    private void openReviewWindow(int parkingSpotID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeReview.fxml"));
            Parent root = loader.load();
            MakeReviewController makeReviewController = loader.getController();
            makeReviewController.setParkingSpotID(parkingSpotID);
            Scene scene = new Scene(root);
            Stage stage = (Stage) bookingListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Make Review");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a booking
    private void deleteBooking(int bookingID) {
        // Call the delete method in the DB class and get the result
        boolean success = db.deleteBooking(bookingID);

        // If deletion was successful
        if (success) {
            showAlert(AlertType.INFORMATION, "Booking Deletion", "Booking deleted successfully.");
            loadBookingList();
        } else {
            showAlert(AlertType.ERROR, "Booking Deletion", "Failed to delete booking.");
        }
    }

    // Method to show an alert
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigate back to the home page
    @FXML
    private void navigateToHomePage() throws IOException {
        Stage stage = (Stage) bookingListView.getScene().getWindow();
        SceneSwitcher.switchToHomePage(stage);
    }
}
