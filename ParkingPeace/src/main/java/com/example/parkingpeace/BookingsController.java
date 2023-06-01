package com.example.parkingpeace;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BookingsController implements Initializable {

    private DB db = new DB();


    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, LocalDate> dateColumn;

    @FXML
    private TableColumn<Booking, String> priceColumn;
    private HomeController homeController;
    private ParkingSpot parkingSpot;




    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setBookingData(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating) {
        this.parkingSpot = new ParkingSpot(location, availability, price, services, zipCode, city, photoFilePath, rating);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureDatePicker();
        configureTableView();
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

    private void configureTableView() {
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBookingDate().toLocalDate()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));

        priceColumn.setCellFactory(column -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(price);
                }
            }
        });

        // Create an ObservableList of Booking objects
        ObservableList<Booking> bookings = FXCollections.observableArrayList();

        // Fetch Booking objects from the database
        List<Booking> bookingsFromDB = db.fetchBookings();

        // Add the bookings to the ObservableList
        bookings.addAll(bookingsFromDB);

        // Set the items property of the TableView to the ObservableList
        tableView.setItems(bookings);
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
