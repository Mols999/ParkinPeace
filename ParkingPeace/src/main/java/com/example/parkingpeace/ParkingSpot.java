package com.example.parkingpeace;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class ParkingSpot {
    private DB db = new DB();
    private final SimpleStringProperty location;
    private final SimpleStringProperty availability;
    private final SimpleStringProperty price;
    private final SimpleStringProperty services;
    private final SimpleStringProperty zipCode;
    private final SimpleStringProperty city;
    private final SimpleStringProperty photoFilePath;
    private final SimpleStringProperty rating;
    private final SimpleStringProperty parkingSpotID;
    private final SimpleObjectProperty<LocalDate> date;




    public ParkingSpot(String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating) {
        this.location = new SimpleStringProperty(location);
        this.availability = new SimpleStringProperty(availability);
        this.price = new SimpleStringProperty(price);
        this.services = new SimpleStringProperty(services);
        this.zipCode = new SimpleStringProperty(zipCode);
        this.city = new SimpleStringProperty(city);
        this.photoFilePath = new SimpleStringProperty(photoFilePath);
        this.rating = new SimpleStringProperty(rating);
        this.parkingSpotID = new SimpleStringProperty(generateParkingSpotID());// Initialize the parking spot ID
        this.date = new SimpleObjectProperty<>();
    }



    public String getParkingSpotID() {
        return parkingSpotID.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getAvailability() {
        return availability.get();
    }


    public String getPrice() {
        return price.get();
    }

    public String getServices() {
        return services.get();
    }

    public String getZipCode() {
        return zipCode.get();
    }

    public String getCity() {
        return city.get();
    }

    public String getRating() {
        return rating.get();
    }

    public String getPhotoFilePath() {
        return photoFilePath.get();
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }
    public void setAvailability(String availability) {
        this.availability.set(availability.equals("1") ? "Available" : "Unavailable");
    }


    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    public SimpleStringProperty locationProperty() {
        return location;
    }

    public SimpleStringProperty availabilityProperty() {
        return availability;
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public SimpleStringProperty servicesProperty() {
        return services;
    }

    public SimpleStringProperty zipCodeProperty() {
        return zipCode;
    }

    public SimpleStringProperty cityProperty() {
        return city;
    }

    public SimpleStringProperty photoFilePathProperty() {
        return photoFilePath;
    }

    public SimpleStringProperty ratingProperty() {
        return rating;
    }

    public SimpleStringProperty parkingSpotIDProperty() {
        return parkingSpotID;
    }

    public boolean isDateBooked(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        String parkingSpotIdString = getParkingSpotID();
        boolean isBooked = db.isParkingSpotBooked(parkingSpotIdString, date);
        db.disconnect();
        return isBooked;
    }

    public ObservableList<ParkingSpot> getParkingSpotsForBooking() {
        ObservableList<ParkingSpot> parkingSpots = FXCollections.observableArrayList();

        String sql = "SELECT ps.fldLocation, ps.fldAvailability, ps.fldPrice, ps.fldServices, ps.fldZipCode, ps.fldCity, ps.fldPhotoFilePath, ll.fldRating " +
                "FROM tblParkingSpot ps " +
                "INNER JOIN tblLandlord ll ON ps.fldLandlordID = ll.fldLandlordID " +
                "WHERE ps.fldAvailability = 'Available'";
        ResultSet rs = db.selectSQLWithResultParams(sql);

        try {
            while (rs.next()) {
                String location = rs.getString("fldLocation");
                String availability = rs.getString("fldAvailability");
                String price = rs.getString("fldPrice");
                String services = rs.getString("fldServices");
                String zipCode = rs.getString("fldZipCode");
                String city = rs.getString("fldCity");
                String photoFilePath = rs.getString("fldPhotoFilePath");
                String rating = rs.getString("fldRating");

                parkingSpots.add(new ParkingSpot(location, availability, price, services, zipCode, city, photoFilePath, rating));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.disconnect();
        }

        return parkingSpots;
    }



    public Image getImageFromFilePath() {
        try {
            // Load the image using the file path
            return new Image("file:" + getPhotoFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ImageView getImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        Image image = getImageFromFilePath();
        if (image != null) {
            imageView.setImage(image);
        } else {
            // Set a default image here
            imageView.setImage(new Image("ParkingImage/image1.jpg"));
        }
        return imageView;
    }

    private String generateParkingSpotID() {
        return UUID.randomUUID().toString(); // Generate a unique ID using UUID
    }
}
