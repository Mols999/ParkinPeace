package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Add any additional methods or logic for the home page here

    @FXML
    public void initialize() {
        // Perform any initialization tasks for the home page here
    }
}
