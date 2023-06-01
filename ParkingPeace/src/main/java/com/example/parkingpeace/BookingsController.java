package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;

public class BookingsController implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Label monthLabel;
    private HomeController homeController;
    private ParkingSpot parkingSpot;
    private DB db = new DB();
    private String parkingSpotID;
    private int parkingSpotIDInt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDatePicker();
        parkingSpot = new ParkingSpot("Location", "Availability", "Price", "Services", "ZipCode", "City", "PhotoFilePath", "Rating");
        parkingSpotID = parkingSpot.getParkingSpotID().toString();
        parkingSpotIDInt = parkingSpotID.hashCode(); // Convert the UUID to an integer
        String monthName = LocalDate.now().getMonth().name();
        monthLabel.setText(monthName);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setBookingData(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating) {
        this.parkingSpot = new ParkingSpot(location, availability, price, services, zipCode, city, photoFilePath, rating);
    }




    private void configureDatePicker() {
        Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(!date.isAfter(LocalDate.now())); // Disable past dates

                if (datePicker.getChronology().dateNow().equals(date)) {
                    setTooltip(new Tooltip("Today"));
                }

                if (parkingSpot.isDateBooked(date.toString())) { // Check if the date is booked
                    setStyle("-fx-background-color: red;");
                } else {
                    setStyle("");
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }

    @FXML
    private void handleConfirmButton() {
        LocalDate selectedDate = datePicker.getValue();

        if (selectedDate != null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Booking Confirmation");
            alert.setContentText("You have successfully booked the parking spot for " + selectedDate + ".");
            alert.showAndWait();

            // TODO: Implement the database insertion for the booking

            // Switch back to HomeController
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Selection");
            alert.setHeaderText("Date Selection Required");
            alert.setContentText("Please select a date to proceed with the booking.");
            alert.showAndWait();
        }
    }
}
