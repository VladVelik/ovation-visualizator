module com.vmsosin.ovationvisualizator {
    requires javafx.controls;
    requires javafx.fxml;
    requires ghost4j;
    requires javafx.base;
    requires java.desktop;
    requires javafx.swing;

    opens com.vmsosin.ovationvisualizator to javafx.fxml;
    exports com.vmsosin.ovationvisualizator;
}