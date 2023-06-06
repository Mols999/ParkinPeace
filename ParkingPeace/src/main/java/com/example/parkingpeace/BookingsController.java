package com.example.parkingpeace;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

public class BookingsController implements Initializable {

    private HomeController homeController;
    private ParkingSpot parkingSpot;
    private DB db = new DB();
    @FXML
    private Label nightsLabel;

    @FXML
    private Label pricePerNightLabel;

    @FXML
    private Label totalPriceLabel;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    private LocalDate startDate;
    private LocalDate endDate;
    private String customerID;
    private String parkingSpotID;
    private Stage stage;
    private String location;
    private String availability;
    private String price;
    private String services;
    private String zipCode;
    private String city;
    private String photoFilePath;
    private String rating;

    public void setBookingData(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        this.location = location;
        this.availability = availability;
        this.price = price;
        this.services = services;
        this.zipCode = zipCode;
        this.city = city;
        this.photoFilePath = photoFilePath;
        this.rating = rating;
        this.parkingSpotID = parkingSpotID;

        this.parkingSpot = new ParkingSpot(parkingSpotID, "", location, availability, price, services, zipCode, city, photoFilePath, rating);
        if (homeController != null) {
            this.stage = homeController.getStage();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setParkingSpotID(String parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Callback<DatePicker, DateCell> dayCellFactory = this::createDayCell;
        startDatePicker.setDayCellFactory(dayCellFactory);
        endDatePicker.setDayCellFactory(dayCellFactory);

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            startDate = newValue;
            handleDateSelection();
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            endDate = newValue;
            handleDateSelection();
        });
    }



    private String generateBookingID() {
        // Generate a random number between 1000 and 9999
        int randomNumber = (int) (Math.random() * 9000) + 1000;

        // Convert the random number to a string
        return String.valueOf(randomNumber);
    }

    private void handleDateSelection() {
        if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
            if (db.isParkingSpotBooked(Integer.parseInt(parkingSpot.getParkingSpotID()), startDate) ||
                    db.isParkingSpotBooked(Integer.parseInt(parkingSpot.getParkingSpotID()), endDate)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Booking Unavailable");
                alert.setHeaderText(null);
                alert.setContentText("The selected dates are already booked. Please choose different dates.");
                alert.showAndWait();
            } else {
                // Calculate nights, price, and total price based on selected dates
                int nights = calculateNights(startDate, endDate);
                double pricePerNight = calculatePricePerNight(parkingSpot.getPrice());
                double totalPrice = nights * pricePerNight;

                // Update UI labels with the calculated values
                nightsLabel.setText("Nights: " + nights);
                pricePerNightLabel.setText("Price per night: $" + pricePerNight);
                totalPriceLabel.setText("Total price: $" + totalPrice);
            }
        } else {
            // Clear UI labels if dates are invalid
            nightsLabel.setText("Nights: ");
            pricePerNightLabel.setText("Price per night: ");
            totalPriceLabel.setText("Total price: ");
        }
    }


    @FXML
    private void handleBookButton(ActionEvent event) {
        if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
            if (db.isParkingSpotBooked(Integer.parseInt(parkingSpot.getParkingSpotID()), startDate) ||
                    db.isParkingSpotBooked(Integer.parseInt(parkingSpot.getParkingSpotID()), endDate)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Booking Unavailable");
                alert.setHeaderText(null);
                alert.setContentText("The selected dates are already booked. Please choose different dates.");
                alert.showAndWait();
            } else {
                makeBooking();
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Dates");
            alert.setHeaderText(null);
            alert.setContentText("Please select valid start and end dates for the booking.");
            alert.showAndWait();
        }
    }

    private void makeBooking() {
        String bookingID = generateBookingID();
        Timestamp startDateTime = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endDateTime = Timestamp.valueOf(endDate.atStartOfDay());

        // Insert into the booking table
        String sql = "INSERT INTO tblBooking (fldBookingID, fldCustomerID, fldParkingSpotID, fldStartDateTime, fldEndDateTime, fldBookingStatus) VALUES (?, ?, ?, CAST(? AS DATETIME), CAST(? AS DATETIME), ?)";
        boolean success = db.insertSQL(sql, bookingID, customerID, parkingSpotID, startDateTime, endDateTime, "Booked");
        if (success) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Booking Successful");
            alert.setHeaderText(null);
            alert.setContentText("Your booking has been successfully made.");
            alert.showAndWait();
            SceneSwitcher.switchToScene("Home.fxml", "Home", stage);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Booking Failed");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while making the booking. Please try again.");
            alert.showAndWait();
        }
    }

    private DateCell createDayCell(DatePicker datePicker) {
        Map<LocalDate, Boolean> bookingMap = new HashMap<>();
        List<LocalDate> bookedDates = db.getBookedDatesForParkingSpot(parkingSpot.getParkingSpotID());
        for (LocalDate bookedDate : bookedDates) {
            bookingMap.put(bookedDate, true);
        }

        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    if (bookingMap.containsKey(item)) {
                        setStyle("-fx-background-color: #FF0000;");
                    } else {
                        setStyle("");
                    }
                }
            }
        };
    }



    private boolean isParkingSpotBooked(LocalDate startDate, LocalDate endDate) {
        return db.isParkingSpotBooked(parkingSpot.getParkingSpotID(), startDate);
    }

    private int calculateNights(LocalDate startDate, LocalDate endDate) {
        return (int) startDate.until(endDate).getDays();
    }

    private double calculatePricePerNight(String price) {
        return Double.parseDouble(price);
    }
}
