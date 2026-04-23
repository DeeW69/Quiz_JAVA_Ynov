package com.example.quiz_java;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        SelectionScene vueSelection = new SelectionScene();
        vueSelection.configurer(stage);
        stage.show();
    }
}
