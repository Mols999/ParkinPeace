module com.example.parkingpeace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires junit;

    opens com.example.parkingpeace to javafx.fxml;
    exports com.example.parkingpeace;
    exports com.example.parkingpeace.controllers;
    opens com.example.parkingpeace.controllers to javafx.fxml;
    exports com.example.parkingpeace.models;
    opens com.example.parkingpeace.models to javafx.fxml;
    exports com.example.parkingpeace.db;
    opens com.example.parkingpeace.db to javafx.fxml;
}
