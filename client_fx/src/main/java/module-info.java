module client_fx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.client_fx to javafx.fxml;
    exports com.example.client_fx;
}