package com.example.quiz_java;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class dialogue {

    private final String texteComplet =
            "\u2022 Chef\n" +
                    "\u00c9coute-moi bien. Une bombe a \u00e9t\u00e9 plac\u00e9e quelque part en ville, et tout repose sur toi.\n" +
                    "Nous n'avons pas de temps \u00e0 perdre. Chaque seconde compte.\n\n" +
                    "\u2022 Chef\n" +
                    "Voici la situation : tu vas devoir r\u00e9soudre une s\u00e9rie d'\u00e9nigmes. Chacune te donnera des\n" +
                    "indices pour localiser la bombe. Le temps presse, mais nous avons encore une chance\n" +
                    "si tu agis rapidement et avec pr\u00e9cision.\n\n" +
                    "\u2022 Chef\n" +
                    "Je sais que ce n'est pas facile, mais je crois en toi. Nous avons les outils n\u00e9cessaires, et\n" +
                    "tu as l'intelligence pour d\u00e9chiffrer ces \u00e9nigmes. Chaque r\u00e9ponse correcte nous\n" +
                    "rapproche de la solution.\n\n" +
                    "\u2022 Chef\n" +
                    "Ne laisse pas la pression te faire tr\u00e9bucher. R\u00e9sous les \u00e9nigmes, trouve l'emplacement\n" +
                    "de la bombe, et nous pourrons la d\u00e9samorcer avant qu'il ne soit trop tard. On compte\n" +
                    "sur toi. La ville compte sur toi.";

    public void configure(Stage fenetre) {
        fenetre.setTitle("Briefing");
        fenetre.setScene(createScene());
    }

    public Scene createScene() {
        Label etiquetteTexte = new Label();
        etiquetteTexte.setWrapText(true);
        etiquetteTexte.setTextAlignment(TextAlignment.LEFT);
        etiquetteTexte.setFont(Font.font("Consolas", 20));
        etiquetteTexte.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: rgba(0,0,0,0.75);" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;"
        );
        etiquetteTexte.setMaxWidth(900);

        StackPane racine = new StackPane(etiquetteTexte);
        racine.setPadding(new Insets(30));
        racine.setStyle("-fx-background-color: linear-gradient(to bottom, #111111, #2b0000);");

        Scene scene = new Scene(racine, 1000, 700);
        Timeline chronologie = new Timeline();
        Duration delaiParLettre = Duration.millis(35);
        StringBuilder texteAffiche = new StringBuilder();

        for (int i = 0; i < texteComplet.length(); i++) {
            final int index = i;
            chronologie.getKeyFrames().add(
                    new KeyFrame(delaiParLettre.multiply(i + 1), evenement -> {
                        texteAffiche.append(texteComplet.charAt(index));
                        etiquetteTexte.setText(texteAffiche.toString());
                    })
            );
        }

        chronologie.play();
        return scene;
    }
}
