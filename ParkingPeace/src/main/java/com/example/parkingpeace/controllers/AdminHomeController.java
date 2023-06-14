package com.example.parkingpeace.controllers;

import com.example.parkingpeace.db.DB;
import com.example.parkingpeace.models.ParkingSpot;
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

public class AdminHomeController {


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
    @FXML
    private TableColumn<ParkingSpot, String> deleteColumn;
    @FXML
    private TableColumn<ParkingSpot, String> viewColumn;
    @FXML
    private final   Button deleteButton = new Button("Delete");
    private final   Button editButton = new Button("Edit");

    private String customerID;
    private String landlordID;
    private String adminID;

    @FXML
    private void initialize() {
        populateTableView();
        configureTableColumns();
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
      //  deleteColumn.setCellValueFactory(cellData -> cellData.getValue().deleteProperty());
       // viewColumn.setCellValueFactory(cellData -> cellData.getValue().viewProperty());

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



    public void setIDs(String customerID, String landlordID, String adminID) {
        this.customerID = customerID;
        this.landlordID = landlordID;
        this.adminID = adminID;
    }


    public Stage getStage() {
        return this.stage;
    }


    public void handleSearch(KeyEvent keyEvent) {


    }

    public void handleDropdownMenuAction(ActionEvent actionEvent) {


    }
}
