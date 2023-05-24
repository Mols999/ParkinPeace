package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import com.example.parkingpeace.DB;


public class HomeController {

    @FXML
    private TableView<ParkingSpotData> tableView;
    @FXML
    private TableColumn<ParkingSpotData, Image> photoColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> addressColumn;
    @FXML
    private TableColumn<ParkingSpotData, Double> ratingColumn;
    @FXML
    private TableColumn<ParkingSpotData, Double> priceColumn;

    public void initialize() {
        // Set up the cell value factories for each column
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Populate the TableView with data
        tableView.setItems(getParkingSpotData());
    }

    private ObservableList<ParkingSpotData> getParkingSpotData() {
        ObservableList<ParkingSpotData> data = FXCollections.observableArrayList();

        // Establish a database connection using the DB class
        DB db = new DB();
        db.selectSQL("SELECT ps.fldPhotoFilePath, ps.fldLocation, l.fldRating, ps.fldPrice " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord l ON ps.fldLandlordID = l.fldLandlordID");

        try {
            // Process each row in the result set and create ParkingSpotData objects
            while (db.hasMoreData()) {
                String photoFilePath = db.getData();
                String address = db.getData();
                double rating = Double.parseDouble(db.getData());
                double price = Double.parseDouble(db.getData());

                // Create an Image object from the photo file path
                Image photo = null;
                try {
                    photo = new Image(photoFilePath);
                } catch (IllegalArgumentException e) {
                    // Handle invalid URL or resource not found error
                    System.err.println("Invalid URL or resource not found for photo: " + photoFilePath);
                    e.printStackTrace();
                }

                // Create a ParkingSpotData object and add it to the list
                ParkingSpotData parkingSpotData = new ParkingSpotData(photo, address, rating, price);
                data.add(parkingSpotData);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            db.close(); // Close the database connection
        }

        return data;
    }

    public void setStage(Stage stage) {
    }

    public static class ParkingSpotData {
        private final Image photo;
        private final String address;
        private final double rating;
        private final double price;

        public ParkingSpotData(Image photo, String address, double rating, double price) {
            this.photo = photo;
            this.address = address;
            this.rating = rating;
            this.price = price;
        }

        public Image getPhoto() {
            return photo;
        }

        public String getAddress() {
            return address;
        }

        public double getRating() {
            return rating;
        }

        public double getPrice() {
            return price;
        }
    }
}
