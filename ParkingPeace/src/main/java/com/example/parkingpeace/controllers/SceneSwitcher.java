package com.example.parkingpeace.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher {
    private static Object currentController;

    public static void switchToScene(String fxmlFile, String title, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            // Check if the controller implements the setStage method
            Object controller = loader.getController();
            if (controller instanceof HomeController) {
                ((HomeController) controller).setStage(stage);
            }

            currentController = controller; // Save the controller
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Object getCurrentController() {
        return currentController; // Allow access to the controller
    }


    public static void switchToHomePage(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("HomePage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public static void switchToLandlordDashboard(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("LandlordDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }
}
