package com.example.quiz_java;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label texteBienvenue;

    @FXML
    protected void surClicBoutonBonjour() {
        texteBienvenue.setText("Bienvenue dans l'application JavaFX !");
    }
}
