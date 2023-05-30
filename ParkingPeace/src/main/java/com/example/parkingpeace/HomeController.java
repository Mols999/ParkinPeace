package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class HomeController {

    @FXML
    private TableView<ParkingSpotData> tableView;
    @FXML
    private TableColumn<ParkingSpotData, Image> photoColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> addressColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> zipCodeColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> cityColumn;
    @FXML
    private TableColumn<ParkingSpotData, Double> ratingColumn;
    @FXML
    private TableColumn<ParkingSpotData, Double> priceColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> servicesColumn;
    @FXML
    private TableColumn<ParkingSpotData, String> availabilityColumn;
    @FXML
    private TableColumn<ParkingSpotData, Button> rentColumn;





    public void initialize() {
        // Set up the cell value factories for each column
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        zipCodeColumn.setCellValueFactory(new PropertyValueFactory<>("zipCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        servicesColumn.setCellValueFactory(new PropertyValueFactory<>("services"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        rentColumn.setCellValueFactory(new PropertyValueFactory<>("rentButton"));

        // Set up the custom cell factory for the photoColumn
        photoColumn.setCellFactory(column -> new TableCell<ParkingSpotData, Image>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
            }

            @Override
            protected void updateItem(Image item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item);
                    setGraphic(imageView);
                }
            }
        });




        // Set up the custom cell factory for the rentColumn
        rentColumn.setCellFactory(column -> new TableCell<ParkingSpotData, Button>() {
            private final Button rentButton = new Button("Rent");


            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    rentButton.setOnAction(event -> {
                        ParkingSpotData parkingSpotData = getTableView().getItems().get(getIndex());
                        // Perform the rent logic here
                        System.out.println("Rent button clicked for: " + parkingSpotData.getAddress());
                    });
                    setGraphic(rentButton);
                }
            }
        });

        // Populate the TableView with data
        try {
            tableView.setItems(getParkingSpotData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private ObservableList<ParkingSpotData> getParkingSpotData() throws SQLException {
        ObservableList<ParkingSpotData> data = FXCollections.observableArrayList();

        // Establish a database connection using the DB class
        DB db = new DB();
        db.selectSQL("SELECT ps.fldPhotoFilePath, ps.fldLocation, ps.fldZipCode, ps.fldCity, l.fldRating, ps.fldPrice, ps.fldServices, ps.fldAvailability " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord l ON ps.fldLandlordID = l.fldLandlordID");

        try {
            // Process each row in the result set and create ParkingSpotData objects
            while (db.moreData) {
                String photoFilePath = db.getData();
                String address = db.getData();
                String zipCode = db.getData();
                String city = db.getData();
                double rating = Double.parseDouble(db.getData());
                double price = Double.parseDouble(db.getData());
                String services = db.getData();
                boolean availability = db.getData().equalsIgnoreCase("1");

                // Create an Image object from the photo file path
                Image image = getImageFromFilePath(photoFilePath);

                // Create a Rent button
                Button rentButton = new Button("Rent");

                // Create a ParkingSpotData object and add it to the list
                ParkingSpotData parkingSpotData = new ParkingSpotData(image, address, zipCode, city, rating, price, services, availability, rentButton);
                data.add(parkingSpotData);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            db.close(); // Close the database connection
        }

        return data;
    }




    private Image getImageFromFilePath(String filePath) {
        try {
            // Load the image from the resource path
            return new Image(getClass().getResourceAsStream("/" + filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void setStage(Stage stage) {
    }


    public static class ParkingSpotData {
        private final Image photo;
        private final String address;
        private final String zipCode;
        private final String city;
        private final double rating;
        private final double price;
        private final String services;
        private final String availability;
        private final Button rentButton;

        public ParkingSpotData(Image photo, String address, String zipCode, String city, double rating, double price, String services, boolean availability, Button rentButton) {
            this.photo = photo;
            this.address = address;
            this.zipCode = zipCode;
            this.city = city;
            this.rating = rating;
            this.price = price;
            this.services = services;
            this.availability = availability ? "Available" : "Not Available";
            this.rentButton = rentButton;
        }

        public Image getPhoto() {
            return photo;
        }

        public String getAddress() {
            return address;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCity() {
            return city;
        }

        public double getRating() {
            return rating;
        }

        public double getPrice() {
            return price;
        }

        public String getServices() {
            return services;
        }

        public String getAvailability() {
            return availability;
        }

        public Button getRentButton() {
            return rentButton;
        }
    }
}
