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
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
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
    private MenuButton menuButton;


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
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
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
        tableView.setItems(getParkingSpotData());
    }

    private ObservableList<ParkingSpotData> getParkingSpotData() {
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
                Image image = new Image("file:" + photoFilePath);
                photoColumn.setCellFactory(column -> new TableCell<ParkingSpotData, Image>() {
                    private final ImageView imageView = new ImageView();
                    private final Label failureLabel = new Label("Failed to load image");

                    {
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        failureLabel.setVisible(false);
                    }

                    @Override
                    protected void updateItem(Image item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            failureLabel.setVisible(false);
                        } else if (item.isError()) {
                            setGraphic(failureLabel);
                            failureLabel.setVisible(true);
                        } else {
                            imageView.setImage(item);
                            setGraphic(imageView);
                            failureLabel.setVisible(false);
                        }
                    }
                });


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

    public void setStage(Stage stage) {
    }

    @FXML
    private void handleLogoutClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (assuming this is the HomeController window)
            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Profile.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (assuming this is the HomeController window)
            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookingsClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Bookings.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (assuming this is the HomeController window)
            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMenuClick(MouseEvent event) {
        // Get the selected menu item
        MenuItem selectedItem = (MenuItem) event.getTarget();

        // Get the text of the selected menu item
        String menuText = selectedItem.getText();

        // Perform different actions based on the selected menu item
        if (menuText.equals("Profile")) {
            handleProfileClick();
        } else if (menuText.equals("Logout")) {
            handleLogoutClick();
        } else if (menuText.equals("Bookings")) {
            handleBookingsClick();
        } else if (menuText.equals("Ratings and Comments")) {
            handleRatingsCommentsClick();
        }
    }


    @FXML
    private void handleRatingsCommentsClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RatingsandComments.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window (assuming this is the HomeController window)
            Stage currentStage = (Stage) menuButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
