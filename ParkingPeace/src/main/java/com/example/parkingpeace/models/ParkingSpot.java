package com.example.parkingpeace.models;

import com.example.parkingpeace.db.DB;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;



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
    private final SimpleStringProperty customerID;
    private final SimpleObjectProperty<LocalDate> date;



    // Constructor
    public ParkingSpot(String parkingSpotID, String customerID, String location, String availability, String price, String services, String zipCode, String city, String photoFilePath, String rating) {
        this.parkingSpotID = new SimpleStringProperty(parkingSpotID);
        this.customerID = new SimpleStringProperty(customerID);
        this.location = new SimpleStringProperty(location);
        this.availability = new SimpleStringProperty(availability);
        this.price = new SimpleStringProperty(price);
        this.services = new SimpleStringProperty(services);
        this.zipCode = new SimpleStringProperty(zipCode);
        this.city = new SimpleStringProperty(city);
        this.photoFilePath = new SimpleStringProperty(photoFilePath);
        this.rating = new SimpleStringProperty(rating);
        this.date = new SimpleObjectProperty<>();
    }



    // Getters for the properties
    public String getParkingSpotID() {
        return parkingSpotID.get();
    }

    public String getCustomerID() {
        return customerID.get();
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


    // Property getters
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


    // Get an image from the file path
    public Image getImageFromFilePath() {
        try {
            // Load the image using the file path
            return new Image("file:" + getPhotoFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    // Get an ImageView for the parking spot's image
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

    public ObservableValue<String> deleteProperty() {
            // TODO: 12-06-2023


        return null;
    }

    public ObservableValue<String> viewProperty() {
        // TODO: 12-06-2023
        return null;
    }
}
