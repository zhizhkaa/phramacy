module com.example.pharmacy {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.json;
    requires poi;

    opens com.pharmacy to javafx.fxml;
    exports com.pharmacy;
}