package com.example.parkingpeace.controllers;

// Necessary imports
import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

// Implement Initializable for initialization logic
public class BookingsController implements Initializable {

    // Declare required fields
    private CustomerDashboardController customerDashboardController;
    private ParkingSpot parkingSpot;
    private DB db = new DB();
    @FXML
    private Label nightsLabel; // label to show nights for booking
    @FXML
    private Label pricePerNightLabel; // label to show price per night
    @FXML
    private Label totalPriceLabel; // label to show total price for the stay
    @FXML
    private DatePicker startDatePicker; // date picker to select start date of the booking
    @FXML
    private DatePicker endDatePicker; // date picker to select end date of the booking
    @FXML
    private Button homeButton;
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

    // Method to set the booking data
    public void setBookingData(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        // Assign data to the relevant fields
        this.location = location;
        this.availability = availability;
        this.price = price;
        this.services = services;
        this.zipCode = zipCode;
        this.city = city;
        this.photoFilePath = photoFilePath;
        this.rating = rating;
        this.parkingSpotID = parkingSpotID;

        // Initialize a parking spot object with the data
        this.parkingSpot = new ParkingSpot(parkingSpotID, "", location, availability, price, services, zipCode, city, photoFilePath, rating);
        if (customerDashboardController != null) {
            // get stage from home controller if not null
            this.stage = customerDashboardController.getStage();
        }
    }

    // Setter methods for different fields
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setParkingSpotID(String parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public void setHomeController(CustomerDashboardController customerDashboardController) {
        this.customerDashboardController = customerDashboardController;
    }

    // Method called during the initialization of the controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create a day cell factory to show which dates are booked and available
        Callback<DatePicker, DateCell> dayCellFactory = this::createDayCell;
        startDatePicker.setDayCellFactory(dayCellFactory);
        endDatePicker.setDayCellFactory(dayCellFactory);

        // Add listeners to update the nights and prices when the start and end dates are selected
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            startDate = newValue;
            handleDateSelection();
        });

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            endDate = newValue;
            handleDateSelection();
        });
    }

    // Handler to navigate to home page
    @FXML
    private void navigateToHomePage(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        SceneSwitcher.switchToHomePage(stage);
    }

    // Generate a random booking ID
    private String generateBookingID() {
        // Generate a random number between 1000 and 9999
        int randomNumber = (int) (Math.random() * 9000) + 1000;

        // Convert the random number to a string
        return String.valueOf(randomNumber);
    }

    // Calculate nights and prices based on the selected dates
    private void handleDateSelection() {
        // Continue if start date is not after end date
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
                pricePerNightLabel.setText("Price per night: DKK" + pricePerNight);
                totalPriceLabel.setText("Total price: DKK" + totalPrice);
            }
        } else {
            // Clear UI labels if dates are invalid
            nightsLabel.setText("Nights: ");
            pricePerNightLabel.setText("Price per night: ");
            totalPriceLabel.setText("Total price: ");
        }
    }

    // Handle the booking action when book button is clicked
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
                // make the booking
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

    // Insert booking data into the database
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
            alert.setContentText("Your booking has been successfully made. We have sent you an email to your account regarding the booking");
            alert.showAndWait();

            // Get the current stage
            Stage currentStage = (Stage) nightsLabel.getScene().getWindow();

            // Switch to the CustomerBookingList.fxml using the SceneSwitcher class
            SceneSwitcher.switchToScene("CustomerBookingList.fxml", "Booking List", currentStage);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Booking Failed");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while making the booking. Please try again.");
            alert.showAndWait();
        }
    }

    // Create a day cell showing booked dates with a specific color
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

    // Checks if a parking spot is booked for the selected dates
    private boolean isParkingSpotBooked(LocalDate startDate, LocalDate endDate) {
        return db.isParkingSpotBooked(parkingSpot.getParkingSpotID(), startDate);
    }

    // Calculate the number of nights for a given booking
    private int calculateNights(LocalDate startDate, LocalDate endDate) {
        return (int) startDate.until(endDate).getDays();
    }

    // Calculate the price per night for a given booking
    private double calculatePricePerNight(String price) {
        return Double.parseDouble(price);
    }
}
