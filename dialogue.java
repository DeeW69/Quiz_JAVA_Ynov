import javafx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class dialogue extends Application {

    private final String texteComplet =
            "• Chef\n" +
                    "Écoute-moi bien. Une bombe a été placée quelque part en ville, et tout repose sur toi.\n" +
                    "Nous n'avons pas de temps à perdre. Chaque seconde compte.\n\n" +

                    "• Chef\n" +
                    "Voici la situation : tu vas devoir résoudre une série d'énigmes. Chacune te donnera des\n" +
                    "indices pour localiser la bombe. Le temps presse, mais nous avons encore une chance\n" +
                    "si tu agis rapidement et avec précision.\n\n" +

                    "• Chef\n" +
                    "Je sais que ce n'est pas facile, mais je crois en toi. Nous avons les outils nécessaires, et\n" +
                    "tu as l'intelligence pour déchiffrer ces énigmes. Chaque réponse correcte nous\n" +
                    "rapproche de la solution.\n\n" +

                    "• Chef\n" +
                    "Ne laisse pas la pression te faire trébucher. Résous les énigmes, trouve l’emplacement\n" +
                    "de la bombe, et nous pourrons la désamorcer avant qu'il ne soit trop tard. On compte\n" +
                    "sur toi. La ville compte sur toi.";

    @Override
    public void start(Stage fenetre) {

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

        fenetre.setTitle("Briefing");
        fenetre.setScene(scene);
        fenetre.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}