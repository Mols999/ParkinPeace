package com.example.parkingpeace;

import com.example.parkingpeace.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 400);
        LoginController controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setTitle("Park in Peace");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/Logo/Trailer.png")));
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
