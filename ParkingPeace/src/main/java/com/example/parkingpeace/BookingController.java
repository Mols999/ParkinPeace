package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.sql.SQLException;

public class BookingController {
    private HomeController.ParkingSpotData parkingSpotData;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button bookButton;

    @FXML
    private void handleBookButton() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Please select both a start and end date.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("End date must be after start date.");
            return;
        }

        DB db = new DB();
        String sql = "SELECT * FROM tblBooking WHERE fldParkingSpotID = ? AND fldStartDateTime <= ? AND fldEndDateTime >= ?";


        if (db.hasMoreData()) {
            showAlert("Selected dates are already booked. Please select different dates.");
        } else {
            // Code to insert the new booking into the database
        }
    }


    public void setData(HomeController.ParkingSpotData parkingSpotData) {
        this.parkingSpotData = parkingSpotData;
    }


    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
