module com.example.quiz_java {
    requires com.fasterxml.jackson.databind;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.quiz_java to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.quiz_java;
}
