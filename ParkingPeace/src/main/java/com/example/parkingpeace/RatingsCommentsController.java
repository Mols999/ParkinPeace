package com.example.parkingpeace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class RatingsCommentsController {

    @FXML
    private TextArea ratingsTextArea;

    @FXML
    private TextArea commentsTextArea;

    @FXML
    private Button submitButton;

    public void initialize() {
        // Initialize the ratings and comments text areas
        ratingsTextArea.setText("");
        commentsTextArea.setText("");
    }

    @FXML
    private void handleSubmitButton() {
        // Get the entered ratings and comments
        String ratings = ratingsTextArea.getText();
        String comments = commentsTextArea.getText();

        // Perform the submission logic here
        System.out.println("Ratings: " + ratings);
        System.out.println("Comments: " + comments);

        // Clear the text areas after submission
        ratingsTextArea.setText("");
        commentsTextArea.setText("");
    }

    public void setStage(Stage stage) {
        // Optionally, you can perform any additional initialization here
    }
}
