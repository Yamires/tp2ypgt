module Server {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens server.models to javafx.base;
    opens mvc;

}