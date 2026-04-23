package com.example.quiz_java;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class SelectionScene {

    private static final String STYLE_ARRIERE_PLAN =
            "-fx-background-color: linear-gradient(to bottom, #23150d, #5c250a);";

    private Stage fenetre;
    private Scene scene;

    public void configurer(Stage fenetre) {
        this.fenetre = fenetre;
        fenetre.setTitle("Selection des jeux");
        fenetre.setScene(creerScene());
    }

    private Scene creerScene() {
        if (scene == null) {
            scene = construireScene();
        }

        return scene;
    }

    private Scene construireScene() {
        StackPane racine = new StackPane();
        racine.setStyle(STYLE_ARRIERE_PLAN);
        racine.setPadding(new Insets(30));

        Label titre = new Label("Selectionnez un mode de jeu");
        titre.setFont(Font.font("Georgia", FontWeight.BOLD, 38));
        titre.setStyle("-fx-text-fill: #ffd28c;");

        Label sousTitre = new Label("Choisissez votre destination");
        sousTitre.setFont(Font.font("Georgia", 22));
        sousTitre.setStyle("-fx-text-fill: #fff4dd;");

        Button boutonQuiz = new Button("Aller au quiz");
        boutonQuiz.setId("go-quiz-button");
        boutonQuiz.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        boutonQuiz.setStyle(
                "-fx-background-color: #f1bc6b;" +
                        "-fx-text-fill: #3a1707;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 12 24;"
        );
        boutonQuiz.setOnAction(event -> new dialogue().configurer(fenetre));

        Button boutonAutresJeux = new Button("Autres jeux - A venir");
        boutonAutresJeux.setId("coming-soon-button");
        boutonAutresJeux.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        boutonAutresJeux.setDisable(true);
        boutonAutresJeux.setStyle(
                "-fx-opacity: 1;" +
                        "-fx-background-color: rgba(18, 13, 9, 0.88);" +
                        "-fx-text-fill: #f1d0a3;" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 12 24;"
        );

        Button boutonQuitter = new Button("Quitter");
        boutonQuitter.setId("quit-button");
        boutonQuitter.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        boutonQuitter.setStyle(
                "-fx-background-color: rgba(18, 13, 9, 0.88);" +
                        "-fx-text-fill: #fff4dd;" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 12 24;"
        );
        boutonQuitter.setOnAction(event -> fenetre.close());

        VBox contenu = new VBox(18, titre, sousTitre, boutonQuiz, boutonAutresJeux, boutonQuitter);
        contenu.setId("selection-root");
        contenu.setAlignment(Pos.CENTER);
        contenu.setMaxWidth(780);

        racine.getChildren().setAll(contenu);
        return new Scene(racine, 1120, 760);
    }
}
