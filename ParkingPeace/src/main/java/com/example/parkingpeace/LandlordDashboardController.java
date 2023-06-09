package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
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

    private String landlordID;

    private DB db;
    private String customerID;
    private String adminID;

    @FXML
    public void initialize() {
        db = new DB();
        configureTableColumns();
    }


    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;

        System.out.println("Landlord ID: " + landlordID); // Print the landlordID

        loadParkingSpotsForLandlord(landlordID); // Load parking spots after IDs are set
    }


    public void loadParkingSpotsForLandlord(String landlordID) {
        List<ParkingSpot> parkingSpots = getParkingSpotsForLandlord();
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

                parkingSpots.add(new ParkingSpot(parkingSpotID, customerID, location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }

        return parkingSpots;
    }



    private void configureTableColumns() {
        photoColumn.setCellValueFactory(cellData -> cellData.getValue().photoFilePathProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        zipCodeColumn.setCellValueFactory(cellData -> cellData.getValue().zipCodeProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
        servicesColumn.setCellValueFactory(cellData -> cellData.getValue().servicesProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().availabilityProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());

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
                        String location = parkingSpot.getLocation();
                        String availability = parkingSpot.getAvailability();
                        String price = parkingSpot.getPrice();
                        String services = parkingSpot.getServices();
                        String zipCode = parkingSpot.getZipCode();
                        String city = parkingSpot.getCity();
                        String photoFilePath = parkingSpot.getPhotoFilePath();
                        String rating = parkingSpot.getRating();
                        String parkingSpotID = parkingSpot.getParkingSpotID();

                        handleModifyButton(location, availability, price, services, zipCode, city, photoFilePath, rating, parkingSpotID);
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

        tableView.getColumns().add(modifyButtonColumn);

    }

    private void handleModifyButton(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating, String parkingSpotID) {
        Stage currentStage = (Stage) tableView.getScene().getWindow();

        // Switch to new scene
        SceneSwitcher.switchToScene("ModifyParkingSpot.fxml", "Modify Parking Spot", currentStage);

        // Retrieve controller and set parking spot details
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyParkingSpot.fxml"));
        ModifyParkingSpotController controller = loader.getController();


        controller.setParkingSpotDetails(location, availability, price, services, zipCode, city, photoFilePath, rating, parkingSpotID);

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
}
