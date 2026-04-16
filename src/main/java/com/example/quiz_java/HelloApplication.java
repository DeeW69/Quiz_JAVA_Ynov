package com.example.quiz_java;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        dialogue dialogueView = new dialogue();
        dialogueView.configure(stage);
        stage.show();
    }
}
