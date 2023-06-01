package com.example.parkingpeace;

import javafx.beans.property.SimpleObjectProperty;
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

    private DB db;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<ParkingSpot> tableView;

    @FXML
    private TableColumn<ParkingSpot, LocalDate> dateColumn;

    @FXML
    private TableColumn<ParkingSpot, String> priceColumn;

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

                if (parkingSpot.isDateBooked(date.toString())) { // Pass the date as a string
                    setStyle("-fx-background-color: red;");
                } else {
                    setStyle("");
                }
            }
        };

        datePicker.setDayCellFactory(dayCellFactory);
    }

    private void configureTableView() {
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        priceColumn.setCellFactory(column -> new TableCell<ParkingSpot, String>() {
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

        // Create an ObservableList of ParkingSpot objects
        ObservableList<ParkingSpot> parkingSpots = FXCollections.observableArrayList();

        // Assuming you have a method in DB class that fetches a list of ParkingSpot objects
        //List<ParkingSpot> spotsFromDB = db.fetchParkingSpots();

        // Add the parking spots to the ObservableList
       // parkingSpots.addAll(spotsFromDB);

        // Set the items property of the TableView to the ObservableList
        tableView.setItems(parkingSpots);
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
