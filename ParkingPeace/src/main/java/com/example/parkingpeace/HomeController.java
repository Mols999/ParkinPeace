package com.example.parkingpeace;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    private DB db = new DB();
    private ObservableList<ParkingSpot> parkingSpots = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> dropdownMenu;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<ParkingSpot> tableView;

    @FXML
    private TableColumn<ParkingSpot, String> photoColumn;

    @FXML
    private TableColumn<ParkingSpot, String> addressColumn;

    @FXML
    private TableColumn<ParkingSpot, String> zipCodeColumn;

    @FXML
    private TableColumn<ParkingSpot, String> cityColumn;

    @FXML
    private TableColumn<ParkingSpot, String> ratingColumn;

    @FXML
    private TableColumn<ParkingSpot, String> servicesColumn;

    @FXML
    private TableColumn<ParkingSpot, String> availabilityColumn;

    @FXML
    private TableColumn<ParkingSpot, String> priceColumn;

    private String customerID;
    private String landlordID;
    private String adminID;


    @FXML
    private void initialize() {
        populateTableView();
        configureTableColumns();
    }

    @FXML
    private void handleDropdownMenuAction(ActionEvent event) {
        String action = dropdownMenu.getValue();
        switch (action) {
            case "Edit Profile":
                SceneSwitcher.switchToScene("Login.fxml", "Login", (Stage) dropdownMenu.getScene().getWindow());
                break;
            case "Bookings":
                SceneSwitcher.switchToScene("BookingList.fxml", "BookingList", (Stage) dropdownMenu.getScene().getWindow());
                break;
            case "Ratings and Comments":
                SceneSwitcher.switchToScene("Ratings.fxml", "Ratings and comments", (Stage) dropdownMenu.getScene().getWindow());
                break;
            case "Logout":
                SceneSwitcher.switchToScene("Login.fxml", "Login", (Stage) dropdownMenu.getScene().getWindow());
                break;
            default:
                // Nothing for now
                break;
        }
    }


    @FXML
    public void handleSearch(KeyEvent event) {
        String searchQuery = searchTextField.getText().trim();
        if (searchQuery.isEmpty()) {
            populateTableView();
        } else {
            searchParkingSpots(searchQuery);
        }
    }

    private void searchParkingSpots(String searchQuery) {
        tableView.getItems().clear();
        parkingSpots.clear();


        String sql = "SELECT fldParkingSpotID, fldLocation, fldAvailability, fldPrice, fldServices, fldZipCode, fldCity, fldPhotoFilePath, fldRating " +
                "FROM tblParkingSpot " +
                "WHERE fldLocation LIKE ? OR fldZipCode LIKE ? OR fldCity LIKE ?";
        ResultSet rs = db.selectSQLWithResultParams(sql, "%" + searchQuery + "%", "%" + searchQuery + "%", "%" + searchQuery + "%");
        try {
            while (rs.next()) {
                String parkingSpotID = rs.getString("fldParkingSpotID");
                String location = rs.getString("fldLocation");
                String availability = rs.getString("fldAvailability");
                String price = rs.getString("fldPrice");
                String services = rs.getString("fldServices");
                String zipCode = rs.getString("fldZipCode");
                String city = rs.getString("fldCity");
                String photoFilePath = rs.getString("fldPhotoFilePath");
                String rating = rs.getString("fldRating");

                parkingSpots.add(new ParkingSpot(parkingSpotID, customerID, location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            db.disconnect();
        }
        tableView.setItems(parkingSpots);
    }


    private void populateTableView() {
        tableView.getItems().clear();
        parkingSpots.clear();

        String sql = "SELECT ps.fldLocation, ps.fldAvailability, ps.fldPrice, ps.fldServices, ps.fldZipCode, ps.fldCity, ps.fldPhotoFilePath, ll.fldRating, ps.fldParkingSpotID " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord ll ON ps.fldLandlordID = ll.fldLandlordID";

        ResultSet rs = db.selectSQLWithResultParams(sql);
        try {
            while (rs.next()) {
                String location = rs.getString(1);
                String availability = rs.getString(2);
                String price = rs.getString(3);
                String services = rs.getString(4);
                String zipCode = rs.getString(5);
                String city = rs.getString(6);
                String photoFilePath = rs.getString(7);
                String rating = rs.getString(8);
                String parkingSpotID = rs.getString(9);

                parkingSpots.add(new ParkingSpot(parkingSpotID, customerID, location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            db.disconnect();
        }
        tableView.setItems(parkingSpots);
    }

    private void configureTableColumns() {
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoFilePathProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());

        availabilityColumn.setCellValueFactory(cellData -> {
            String availabilityValue = cellData.getValue().getAvailability();
            String availabilityText = (availabilityValue.equals("1")) ? "Available" : "Unavailable";
            return new SimpleStringProperty(availabilityText);
        });

        photoColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
            }

            @Override
            protected void updateItem(String photoFilePath, boolean empty) {
                super.updateItem(photoFilePath, empty);
                if (empty || photoFilePath == null) {
                    imageView.setImage(null);
                } else {
                    Image image = getImageFromFilePath(photoFilePath);
                    if (image != null) {
                        imageView.setImage(image);
                    } else {
                        System.out.println("Failed to load image for: " + photoFilePath);
                    }
                }
                setGraphic(imageView);
            }
        });


        addressColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        zipCodeColumn.setCellValueFactory(cellData -> cellData.getValue().zipCodeProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
        servicesColumn.setCellValueFactory(cellData -> cellData.getValue().servicesProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().availabilityProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        TableColumn<ParkingSpot, Void> rentButtonColumn = new TableColumn<>("Rent");
        rentButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button rentButton = new Button("Rent");

            {
                rentButton.setOnAction(event -> {
                    ParkingSpot parkingSpot = getTableRow().getItem();
                    if (parkingSpot != null) {
                        String location = parkingSpot.getLocation();
                        String availability = parkingSpot.getAvailability();
                        String price = parkingSpot.getPrice();
                        String services = parkingSpot.getServices();
                        String zipCode = parkingSpot.getZipCode();
                        String city = parkingSpot.getCity();
                        String photoFilePath = parkingSpot.getPhotoFilePath();
                        String rating = parkingSpot.getRating();
                        String parkingSpotID = parkingSpot.getParkingSpotID(); // Get parkingSpotID

                        handleRentButton(location, availability, price, services, zipCode, city, photoFilePath, rating, parkingSpotID);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(rentButton);
                }
            }
        });
        tableView.getColumns().add(rentButtonColumn);
    }

    private Image getImageFromFilePath(String filePath) {
        try {
            // Check if the file path starts with "ParkingImage/"
            if (filePath.startsWith("ParkingImage/")) {
                // Remove the "ParkingImage/" prefix from the file path
                filePath = filePath.substring("ParkingImage/".length());
            }

            // Load the image using the modified file path
            return new Image(getClass().getResourceAsStream("/ParkingImage/" + filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleRentButton(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("Bookings.fxml"));
            Parent root = loader.load();
            BookingsController bookingsController = loader.getController();
            bookingsController.setBookingData(location, availability, price, services, zipCode, city, photoFilePath, rating, parkingSpotID);
            bookingsController.setCustomerID(customerID);
            bookingsController.setHomeController(this); // Pass the HomeController instance

            Scene scene = new Scene(root);
            Stage bookingsStage = new Stage();
            bookingsStage.setScene(scene);
            bookingsStage.setTitle("Bookings");

            // Pass the stage object to the BookingsController
            bookingsController.setStage(bookingsStage);

            bookingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;
    }

    public Stage getStage() {
        return this.stage;
    }
}
