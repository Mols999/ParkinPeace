package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LandlordDashboardController {
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
    @FXML
    private ComboBox<String> dropdownMenu;
    private static String landlordID; // Static variable to retain landlordID value
    private DB db;



    @FXML
    public void initialize() {
        db = new DB();
        landlordID = LoginController.getLandlordID(); // Retrieve landlord ID from LoginController
        configureTableColumns();
        loadParkingSpotsForLandlord(landlordID); // Load parking spots after ID is set
    }



    @FXML
    private void handleDropdownMenuAction(ActionEvent event) throws IOException {
        String action = dropdownMenu.getValue();
        Stage stage = (Stage) dropdownMenu.getScene().getWindow();
        switch (action) {
            case "Edit Profile":
                navigateToLandlordEditProfile(stage);
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



    private void navigateToRatings(Stage stage) {
        try {
            FXMLLoader ratingsLoader = new FXMLLoader(getClass().getResource("LandlordReviews.fxml"));
            Parent ratingsRoot = ratingsLoader.load();
            LandlordReviewsController landlordReviewsController = ratingsLoader.getController();

            // Set the root of the stage to the Ratings view
            stage.setScene(new Scene(ratingsRoot));
            stage.setTitle("Ratings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void navigateToBookings(Stage stage) {
        try {
            FXMLLoader bookingsLoader = new FXMLLoader(getClass().getResource("LandlordBookingList.fxml"));
            Parent bookingsRoot = bookingsLoader.load();
            LandlordBookingListController bookingListController = bookingsLoader.getController();

            // Set the root of the stage to the Bookings view
            stage.setScene(new Scene(bookingsRoot));
            stage.setTitle("Bookings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void navigateToLandlordEditProfile(Stage currentStage) {
        try {
            FXMLLoader editProfileLoader = new FXMLLoader(getClass().getResource("LandlordEditProfile.fxml"));
            Parent editProfileRoot = editProfileLoader.load();
            LandlordEditProfileController editProfileController = editProfileLoader.getController();

            // Pass the stage to the EditProfileController
            editProfileController.setStage(currentStage);

            // Create a new scene and set its root to the Landlord Edit Profile view
            Scene editProfileScene = new Scene(editProfileRoot);

            // Set the scene of the current stage to the Landlord Edit Profile scene
            currentStage.setScene(editProfileScene);
            currentStage.setTitle("Landlord Edit Profile");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void loadParkingSpotsForLandlord(String landlordID) {
        List<ParkingSpot> parkingSpots = getParkingSpotsForLandlord();
        tableView.getItems().clear(); // Clear existing items
        tableView.getItems().addAll(parkingSpots);
    }



    public List<ParkingSpot> getParkingSpotsForLandlord() {
        List<ParkingSpot> parkingSpots = new ArrayList<>();

        String sql = "SELECT ps.fldParkingSpotID, ps.fldLocation, ps.fldAvailability, ps.fldPrice, ps.fldServices, ps.fldZipCode, ps.fldCity, ps.fldPhotoFilePath, ll.fldRating " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord ll ON ps.fldLandlordID = ll.fldLandlordID " +
                "WHERE ps.fldLandlordID = ?";

        try {
            ResultSet rs = db.selectSQLWithResultParams(sql, this.landlordID);

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

                parkingSpots.add(new ParkingSpot(parkingSpotID, landlordID, location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }

        return parkingSpots;
    }



    private void configureTableColumns() {
        // Configure cell value factories for table columns
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoFilePathProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        zipCodeColumn.setCellValueFactory(cellData -> cellData.getValue().zipCodeProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
        servicesColumn.setCellValueFactory(cellData -> cellData.getValue().servicesProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().availabilityProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

        // Configure custom cell factories for specific columns
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

        addressColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String address, boolean empty) {
                super.updateItem(address, empty);
                if (empty || address == null) {
                    setText(null);
                } else {
                    setText(address);
                }
            }
        });



        TableColumn<ParkingSpot, Void> modifyButtonColumn = new TableColumn<>("Modify");
        modifyButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyButton = new Button("Modify");

            {
                modifyButton.setOnAction(event -> {
                    ParkingSpot parkingSpot = getTableRow().getItem();
                    if (parkingSpot != null) {
                        String parkingSpotID = parkingSpot.getParkingSpotID();
                        String customerID = parkingSpot.getCustomerID();
                        String location = parkingSpot.getLocation();
                        String availability = parkingSpot.getAvailability();
                        String price = parkingSpot.getPrice();
                        String services = parkingSpot.getServices();
                        String zipCode = parkingSpot.getZipCode();
                        String city = parkingSpot.getCity();
                        String photoFilePath = parkingSpot.getPhotoFilePath();
                        String rating = parkingSpot.getRating();

                        handleModifyButton(parkingSpotID, customerID, location, availability, price, services, zipCode, city, photoFilePath, rating);
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(modifyButton);
                }
            }
        });

        TableColumn<ParkingSpot, Void> createParkingSpotButtonColumn = new TableColumn<>("Create Parking Spot");
        createParkingSpotButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button createParkingSpotButton = new Button("Create Parking Spot");

            {
                createParkingSpotButton.setOnAction(event -> {
                    handleCreateParkingSpotButton();
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(createParkingSpotButton);
                }
            }
        });

        // Add the custom columns to the table view
        tableView.getColumns().addAll(modifyButtonColumn, createParkingSpotButtonColumn);
    }


    private void handleModifyButton(String parkingSpotID, String customerID, String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating) {
        Stage currentStage = (Stage) tableView.getScene().getWindow();
        SceneSwitcher.switchToScene("ModifyParkingSpot.fxml", "Modify Parking Spot", currentStage);

        ModifyParkingSpotController controller = (ModifyParkingSpotController) SceneSwitcher.getCurrentController();
        controller.setCurrentParkingSpot(new ParkingSpot(parkingSpotID, customerID, location, availability, price, services, zipCode, city, photoFilePath, rating));
    }


    @FXML
    private void handleCreateParkingSpotButton() {
        Stage currentStage = (Stage) tableView.getScene().getWindow();
        SceneSwitcher.switchToScene("CreateParkingSpot.fxml", "Create Parking Spot", currentStage);

        CreateParkingSpotController createParkingSpotController = (CreateParkingSpotController) SceneSwitcher.getCurrentController();
        createParkingSpotController.setLandlordID(landlordID);
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
        // Handle the rent button action
    }


    // Add the following method to retrieve the landlordID
    public static String getLandlordID() {
        return landlordID;
    }
}
