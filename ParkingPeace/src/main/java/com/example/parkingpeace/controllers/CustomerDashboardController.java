package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
import javafx.application.Platform;
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

public class CustomerDashboardController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Database connection and parking spots list
    private DB db = new DB();
    private ObservableList<ParkingSpot> parkingSpots = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> dropdownMenu;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView<ParkingSpot> tableView;

    // Table columns
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

    // User IDs
    private static String customerID;

    public static String getCustomerID() {
        return customerID;
    }

    @FXML
    private void initialize() {
        populateTableView();
        configureTableColumns();
        customerID = LoginController.getCustomerID();
    }

    // Handle dropdown menu actions
    @FXML
    private void handleDropdownMenuAction(ActionEvent event) throws IOException {
        String action = dropdownMenu.getValue();
        Stage stage = (Stage) dropdownMenu.getScene().getWindow();
        switch (action) {
            case "Edit Profile":
                navigateToEditProfile(stage);
                break;
            case "Bookings":
                navigateToBookings(stage);
                break;
            case "Ratings and Comments":
                navigateToRatings(stage);
                break;
            case "Logout":
                Platform.exit();
                break;
            default:
                break;
        }
    }

    // Navigate to ratings and comments view
    private void navigateToRatings(Stage stage) {
        try {
            FXMLLoader ratingsLoader = new FXMLLoader(getClass().getResource("CustomerReviews.fxml"));
            Parent ratingsRoot = ratingsLoader.load();
            CustomerReviewsController customerReviewsController = ratingsLoader.getController();
            customerReviewsController.setCustomerID(getCustomerID());

            // Set the root of the stage to the Ratings view
            stage.setScene(new Scene(ratingsRoot));
            stage.setTitle("Ratings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigate to bookings view
    private void navigateToBookings(Stage stage) {
        try {
            FXMLLoader bookingsLoader = new FXMLLoader(getClass().getResource("CustomerBookingList.fxml"));
            Parent bookingsRoot = bookingsLoader.load();
            CustomerBookingListController bookingListController = bookingsLoader.getController();


            // Set the root of the stage to the Bookings view
            stage.setScene(new Scene(bookingsRoot));
            stage.setTitle("Bookings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Navigate to edit profile view
    public void navigateToEditProfile(Stage currentStage) {
        try {
            FXMLLoader editProfileLoader = new FXMLLoader(getClass().getResource("CustomerEditProfile.fxml"));
            Parent editProfileRoot = editProfileLoader.load();
            CustomerEditProfileController editProfileController = editProfileLoader.getController();
            editProfileController.setStage(currentStage); // Pass the current stage to the CustomerEditProfileController

            Scene scene = new Scene(editProfileRoot);
            currentStage.setScene(scene);
            currentStage.setTitle("Edit Profile");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // Handle search by location, zip code, or city
    @FXML
    public void handleSearch(KeyEvent event) {
        String searchQuery = searchTextField.getText().trim();
        if (searchQuery.isEmpty()) {
            populateTableView();
        } else {
            searchParkingSpots(searchQuery);
        }
    }

    // Search for parking spots based on the search query
    private void searchParkingSpots(String searchQuery) {
        tableView.getItems().clear();
        parkingSpots.clear();

        String sql = "SELECT ps.fldParkingSpotID, ps.fldLocation, ps.fldAvailability, ps.fldPrice, ps.fldServices, ps.fldZipCode, ps.fldCity, ps.fldPhotoFilePath, ll.fldRating " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord ll ON ps.fldLandlordID = ll.fldLandlordID " +
                "WHERE ps.fldLocation LIKE ? OR ps.fldZipCode LIKE ? OR ps.fldCity LIKE ?";

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

                parkingSpots.add(new ParkingSpot(parkingSpotID, getCustomerID(), location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            db.disconnect();
        }
        tableView.setItems(parkingSpots);
    }

    // Populate the table view with parking spots from the database
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

                parkingSpots.add(new ParkingSpot(parkingSpotID, getCustomerID(), location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            db.disconnect();
        }
        tableView.setItems(parkingSpots);
    }

    // Configure table columns with cell factories and cell values
    private void configureTableColumns() {
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoFilePathProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());

        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(price + " DKK.");
                }
            }
        });

        availabilityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String availability, boolean empty) {
                super.updateItem(availability, empty);
                if (empty || availability == null) {
                    setText(null);
                } else {
                    int availabilityValue = Integer.parseInt(availability);
                    setText((availabilityValue == 1) ? "Availability" : "Unavailable");
                }
            }
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
                        String parkingSpotID = parkingSpot.getParkingSpotID();

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

    // Get image from file path
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

    // Handle rent button click
    private void handleRentButton(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Bookings.fxml"));
            Parent bookingsRoot = loader.load();
            BookingsController bookingsController = loader.getController();
            bookingsController.setBookingData(location, availability, price, services, zipCode, city, photoFilePath, rating, parkingSpotID);
            bookingsController.setCustomerID(getCustomerID());
            bookingsController.setHomeController(this); // Pass the HomeController instance

            // Get the current scene
            Scene currentScene = dropdownMenu.getScene();

            // Replace the root node of the current scene with the Bookings view
            currentScene.setRoot(bookingsRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the stage
    public Stage getStage() {
        return this.stage;
    }
}
