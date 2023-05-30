module com.example.parkingpeace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;

    opens com.example.parkingpeace to javafx.fxml;
    exports com.example.parkingpeace;
}