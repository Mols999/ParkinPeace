package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class MakeReviewController {
    @FXML
    private TextArea reviewTextArea;

    private int parkingSpotID;

    private DB db;

    public MakeReviewController() {
        db = new DB();
    }

    public void setParkingSpotID(int parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

}
