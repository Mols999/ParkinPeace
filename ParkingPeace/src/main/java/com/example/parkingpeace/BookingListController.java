package com.example.parkingpeace;

import com.example.parkingpeace.Booking;
import com.example.parkingpeace.DB;
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

public class BookingListController implements Initializable {

    @FXML
    private ListView<VBox> bookingListView;

    private DB db;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new DB();
        loadBookingList();
    }

    private void loadBookingList() {
        List<Booking> bookings = db.fetchBookings();

        if (bookings.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Booking List", "No bookings found.");
        } else {
            bookingListView.getItems().clear();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (Booking booking : bookings) {
                int bookingID = booking.getBookingID();
                String customerName = db.getCustomerName(booking.getCustomerID()); // Retrieve customer name
                int parkingSpotID = booking.getParkingSpotID();
                Timestamp startDateTime = Timestamp.valueOf(booking.getStartDateTime());
                Timestamp endDateTime = Timestamp.valueOf(booking.getEndDateTime());
                String bookingStatus = booking.getBookingStatus();

                VBox bookingDetailsBox = new VBox();

                // Create and configure Label for booking details
                Label bookingLabel = new Label();
                bookingLabel.setText("Booking ID: " + bookingID + "\n"
                        + "Customer Name: " + customerName + "\n" // Display customer name
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



    private void deleteBooking(int bookingID) {
        boolean success = db.deleteBooking(bookingID);

        if (success) {
            showAlert(AlertType.INFORMATION, "Booking Deletion", "Booking deleted successfully.");
            loadBookingList();
        } else {
            showAlert(AlertType.ERROR, "Booking Deletion", "Failed to delete booking.");
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
