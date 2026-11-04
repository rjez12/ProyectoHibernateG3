module com.desarrollo.proyectohibernateg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;


    opens com.desarrollo.proyectohibernateg3 to javafx.fxml;
    exports com.desarrollo.proyectohibernateg3;
}