module com.desarrollo.proyectohibernateg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;


    opens com.desarrollo.proyectohibernateg3.controller to javafx.fxml;
    opens com.desarrollo.proyectohibernateg3.model;

    exports com.desarrollo.proyectohibernateg3;
}