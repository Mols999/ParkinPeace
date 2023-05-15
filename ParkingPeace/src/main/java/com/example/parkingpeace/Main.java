package com.example.parkingpeace;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
        // Load the fxml file and create a new stage for the popup
        stage.setFullScreen(true);
        //Scene scene = new Scene(fxmlLoader.load(), 1000, 600 );
        stage.setTitle("Park in Peace");
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}