package com.example.parkingpeace;

import javafx.event.ActionEvent;
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
    @FXML
    private ComboBox<String> dropdownMenu;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    public void handleDropdownMenuAction(ActionEvent event) {
        String menuItem = dropdownMenu.getValue().toString();
        String username = ""; // Obtain the username from your application

        if (menuItem.equals("Edit Profile")) {
            try {
                Stage profileStage = new Stage();
                profileStage.setTitle("Edit Profile");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("EditProfile.fxml"));
                Parent profileRoot = loader.load();
                ProfileController profileController = loader.getController();

                profileController.setStage(profileStage);

                Scene profileScene = new Scene(profileRoot);
                profileStage.setScene(profileScene);
                profileStage.showAndWait();

                initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (menuItem.equals("Another Action")) {
            // Code to handle the "Another Action" action
        }
    }

    private FXMLLoader loader;

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

            {
                rentButton.setOnAction(event -> {
                    ParkingSpotData parkingSpotData = getTableView().getItems().get(getIndex());
                    Stage stage = (Stage) rentButton.getScene().getWindow();
                    SceneSwitcher.switchToScene("Bookings.fxml", "Bookings", stage);
                    // Pass the necessary data to the BookingController here
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Bookings.fxml"));
                        Parent root = loader.load();
                        BookingController bookingController = loader.getController();
                        bookingController.setData(parkingSpotData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(rentButton);
                }
            }
        });

        // Populate the TableView with data
        tableView.setItems(getParkingSpotData());
    }

    private ObservableList<ParkingSpotData> getParkingSpotData() {
        ObservableList<ParkingSpotData> data = FXCollections.observableArrayList();

        // Establish a database connection using the DB class
        DB db = new DB();
        String sql = "SELECT fldParkingSpotID, fldLocation, fldPrice, fldAvailability, fldServices, fldLandlordID, fldZipCode, fldCity, fldPhotoFilePath " +
                "FROM tblParkingSpot";

        try {
            db.selectSQLWithParams(sql);
            while (db.hasMoreData()) {
                int spotID = Integer.parseInt(db.getData());
                String location = db.getData();
                double price = Double.parseDouble(db.getData());
                boolean availability = db.getData().equals("1");
                String services = db.getData();
                int landlordID = Integer.parseInt(db.getData());
                String zipCode = db.getData();
                String city = db.getData();
                String photoFilePath = db.getData();
                Image photo = getImageFromFilePath(photoFilePath);
                Button rentButton = new Button("Rent");

                // Create a ParkingSpotData object and add it to the list
                ParkingSpotData parkingSpotData = new ParkingSpotData(photo, location, zipCode, city, price, services, availability, rentButton);

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
            // Load the image using the file path
            return new Image("file:" + filePath);
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
        private final double price;
        private final String services;
        private final boolean availability;
        private final Button rentButton;

        public ParkingSpotData(Image photo, String address, String zipCode, String city, double price, String services, boolean availability, Button rentButton) {
            this.photo = photo;
            this.address = address;
            this.zipCode = zipCode;
            this.city = city;
            this.price = price;
            this.services = services;
            this.availability = availability;
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

        public double getPrice() {
            return price;
        }

        public String getServices() {
            return services;
        }

        public boolean getAvailability() {
            return availability;
        }

        public Button getRentButton() {
            return rentButton;
        }
    }
}
