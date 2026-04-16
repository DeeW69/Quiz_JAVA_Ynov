package com.example.quiz_java;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class dialogue {

    private static final String BACKGROUND_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #23150d, #5c250a);";

    private final List<DialogueStep> etapes = List.of(
            new DialogueStep(Speaker.CHEF,
                    "Bienvenue au Doner Imperial. Ici, le pain croustille avec une ambition professionnelle.",
                    "chef_doner.png"),
            new DialogueStep(Speaker.CLIENT,
                    "Bonsoir chef... je voudrais juste un doner simple. Enfin, simple si cela existe encore ici.",
                    "client_doner.png"),
            new DialogueStep(Speaker.CHEF,
                    "Simple ? Je peux faire simple, mais ce serait insultant pour la sauce blanche.",
                    "chef_doner.png"),
            new DialogueStep(Speaker.CLIENT,
                    "Dans ce cas, mettez-moi un kebab sérieux, avec salade, tomates, oignons et un peu de dignite.",
                    "client_doner.png"),
            new DialogueStep(Speaker.CHEF,
                    "Je vais te servir un monument roulant de viande, de salade et d'espoir croustillant.",
                    "chef_doner.png"),
            new DialogueStep(Speaker.CLIENT,
                    "Je ne comprends pas tout, mais je sens que ce doner va me marquer spirituellement.",
                    "client_doner.png"),
            new DialogueStep(Speaker.CHEF,
                    "Parfait. Recois maintenant le doner officiel du quartier. Mange avec honneur.",
                    "chef_doner.png")
    );

    private final Duration delaiParLettre;
    private final Duration delaiAuto;

    private Stage fenetre;
    private Scene scene;
    private StackPane racine;
    private HBox ligneDialogue;
    private VBox bulleDialogue;
    private StackPane cadrePortrait;
    private ImageView portrait;
    private Label nomOrateur;
    private Label texteDialogue;
    private Button boutonContinuer;

    private Timeline chronologieTexte;
    private PauseTransition pauseAuto;
    private int indexEtape;

    public dialogue() {
        this(
                Duration.millis(lireDelai("quiz.dialogue.letterDelayMillis", 35)),
                Duration.millis(lireDelai("quiz.dialogue.autoAdvanceMillis", 650))
        );
    }

    dialogue(Duration delaiParLettre, Duration delaiAuto) {
        this.delaiParLettre = delaiParLettre;
        this.delaiAuto = delaiAuto;
    }

    public void configure(Stage fenetre) {
        this.fenetre = fenetre;
        fenetre.setTitle("Briefing");
        fenetre.setScene(createScene());
    }

    public Scene createScene() {
        if (scene == null) {
            construireScene();
            afficherEtape(0);
        }

        return scene;
    }

    private void construireScene() {
        racine = new StackPane();
        racine.setStyle(BACKGROUND_STYLE);
        racine.setPadding(new Insets(30));

        nomOrateur = new Label();
        nomOrateur.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        nomOrateur.setStyle("-fx-text-fill: #ffd28c;");

        texteDialogue = new Label();
        texteDialogue.setId("dialogue-text");
        texteDialogue.setWrapText(true);
        texteDialogue.setTextAlignment(TextAlignment.LEFT);
        texteDialogue.setFont(Font.font("Georgia", 28));
        texteDialogue.setStyle("-fx-text-fill: #fff4dd;");
        texteDialogue.setMinHeight(220);

        bulleDialogue = new VBox(18, nomOrateur, texteDialogue);
        bulleDialogue.setId("dialogue-bubble");
        bulleDialogue.setPadding(new Insets(28));
        bulleDialogue.setMaxWidth(760);
        bulleDialogue.setStyle(
                "-fx-background-color: rgba(18, 13, 9, 0.88);" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;"
        );
        HBox.setHgrow(bulleDialogue, Priority.ALWAYS);

        portrait = new ImageView();
        portrait.setFitWidth(220);
        portrait.setFitHeight(220);
        portrait.setPreserveRatio(true);
        portrait.setSmooth(true);

        cadrePortrait = new StackPane(portrait);
        cadrePortrait.setId("portrait-frame");
        cadrePortrait.setPadding(new Insets(14));
        cadrePortrait.setMinSize(250, 250);
        cadrePortrait.setPrefSize(250, 250);
        cadrePortrait.setStyle(
                "-fx-background-color: rgba(255, 244, 221, 0.12);" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;"
        );

        ligneDialogue = new HBox(28);
        ligneDialogue.setId("conversation-row");
        ligneDialogue.setAlignment(Pos.CENTER);

        boutonContinuer = new Button("S'il te plait, chef !");
        boutonContinuer.setId("continue-button");
        boutonContinuer.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        boutonContinuer.setStyle(
                "-fx-background-color: #f1bc6b;" +
                        "-fx-text-fill: #3a1707;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 12 22;"
        );
        boutonContinuer.setVisible(false);
        boutonContinuer.setManaged(false);
        boutonContinuer.setOnAction(event -> avancer());

        VBox contenu = new VBox(26, ligneDialogue, boutonContinuer);
        contenu.setAlignment(Pos.CENTER);
        contenu.setFillWidth(true);
        contenu.setMaxWidth(1050);

        racine.getChildren().setAll(contenu);
        scene = new Scene(racine, 1120, 760);
    }

    private void afficherEtape(int nouvelIndex) {
        stopperAnimations();

        indexEtape = nouvelIndex;
        DialogueStep etape = etapes.get(indexEtape);

        nomOrateur.setText(etape.orateur().libelle);
        portrait.setImage(chargerImage(etape.image()));
        texteDialogue.setText("");
        masquerBouton();

        if (etape.orateur() == Speaker.CHEF) {
            ligneDialogue.getChildren().setAll(bulleDialogue, cadrePortrait);
        } else {
            ligneDialogue.getChildren().setAll(cadrePortrait, bulleDialogue);
        }

        lancerMachineAEcrire(etape.texte(), () -> terminerEtape(etape));
    }

    private void terminerEtape(DialogueStep etape) {
        if (indexEtape == etapes.size() - 1) {
            lancerVersFin();
            return;
        }

        if (etape.orateur() == Speaker.CLIENT) {
            afficherBouton();
            return;
        }

        pauseAuto = new PauseTransition(delaiAuto);
        pauseAuto.setOnFinished(event -> afficherEtape(indexEtape + 1));
        pauseAuto.play();
    }

    private void lancerMachineAEcrire(String texteComplet, Runnable fin) {
        if (delaiParLettre.lessThanOrEqualTo(Duration.ZERO)) {
            texteDialogue.setText(texteComplet);
            fin.run();
            return;
        }

        StringBuilder texteAffiche = new StringBuilder();
        chronologieTexte = new Timeline();

        for (int i = 0; i < texteComplet.length(); i++) {
            final int indexCaractere = i;
            chronologieTexte.getKeyFrames().add(
                    new KeyFrame(delaiParLettre.multiply(i + 1), evenement -> {
                        texteAffiche.append(texteComplet.charAt(indexCaractere));
                        texteDialogue.setText(texteAffiche.toString());
                    })
            );
        }

        chronologieTexte.setOnFinished(event -> fin.run());
        chronologieTexte.play();
    }

    private void avancer() {
        if (indexEtape < etapes.size() - 1) {
            afficherEtape(indexEtape + 1);
        } else {
            afficherEcranFinal();
        }
    }

    private void lancerVersFin() {
        pauseAuto = new PauseTransition(delaiAuto);
        pauseAuto.setOnFinished(event -> afficherEcranFinal());
        pauseAuto.play();
    }

    private void afficherEcranFinal() {
        stopperAnimations();

        ImageView imageFinale = new ImageView(chargerImage("chef_2.png"));
        imageFinale.setPreserveRatio(false);
        imageFinale.fitWidthProperty().bind(scene.widthProperty().subtract(60));
        imageFinale.fitHeightProperty().bind(scene.heightProperty().subtract(150));

        StackPane imageContainer = new StackPane(imageFinale);
        imageContainer.setPadding(new Insets(10));
        imageContainer.setStyle(
                "-fx-background-color: rgba(18, 13, 9, 0.55);" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;"
        );

        Button boutonJeu = new Button("Aller au jeu !");
        boutonJeu.setId("next-scene-button");
        boutonJeu.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        boutonJeu.setStyle(
                "-fx-background-color: #f1bc6b;" +
                        "-fx-text-fill: #3a1707;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 12 22;"
        );
        boutonJeu.setOnAction(event -> afficherPlaceholderJeu());

        Button boutonQuitter = new Button("Quitter");
        boutonQuitter.setId("quit-button");
        boutonQuitter.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        boutonQuitter.setStyle(
                "-fx-background-color: rgba(18, 13, 9, 0.88);" +
                        "-fx-text-fill: #fff4dd;" +
                        "-fx-border-color: #f1bc6b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-padding: 12 22;"
        );
        boutonQuitter.setOnAction(event -> fenetre.close());

        HBox actions = new HBox(18, boutonJeu, boutonQuitter);
        actions.setAlignment(Pos.CENTER);

        VBox contenuFinal = new VBox(24, imageContainer, actions);
        contenuFinal.setAlignment(Pos.CENTER);
        contenuFinal.setMaxWidth(1050);

        racine.getChildren().setAll(contenuFinal);
    }

    private void afficherPlaceholderJeu() {
        Label titre = new Label("La scene de jeu n'est pas encore creee.");
        titre.setId("placeholder-label");
        titre.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        titre.setStyle("-fx-text-fill: #fff4dd;");

        Label sousTitre = new Label("Placeholder temporaire: branchez ici l'ecran suivant du quiz.");
        sousTitre.setWrapText(true);
        sousTitre.setTextAlignment(TextAlignment.CENTER);
        sousTitre.setFont(Font.font("Georgia", 22));
        sousTitre.setStyle("-fx-text-fill: #f1d0a3;");

        BorderPane placeholder = new BorderPane();
        placeholder.setStyle(BACKGROUND_STYLE);
        placeholder.setPadding(new Insets(30));
        placeholder.setCenter(new VBox(18, titre, sousTitre));
        BorderPane.setAlignment(placeholder.getCenter(), Pos.CENTER);

        fenetre.setScene(new Scene(placeholder, 1120, 760));
    }

    private void afficherBouton() {
        boutonContinuer.setVisible(true);
        boutonContinuer.setManaged(true);
    }

    private void masquerBouton() {
        boutonContinuer.setVisible(false);
        boutonContinuer.setManaged(false);
    }

    private void stopperAnimations() {
        if (chronologieTexte != null) {
            chronologieTexte.stop();
        }

        if (pauseAuto != null) {
            pauseAuto.stop();
        }
    }

    private Image chargerImage(String nomRessource) {
        try (InputStream flux = dialogue.class.getResourceAsStream(nomRessource)) {
            InputStream imageFlux = Objects.requireNonNull(flux, "Ressource introuvable: " + nomRessource);
            return new Image(imageFlux);
        } catch (Exception exception) {
            throw new IllegalStateException("Impossible de charger l'image " + nomRessource, exception);
        }
    }

    private static long lireDelai(String cle, long valeurParDefaut) {
        String valeur = System.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            return valeurParDefaut;
        }

        return Long.parseLong(valeur);
    }

    private record DialogueStep(Speaker orateur, String texte, String image) {
    }

    private enum Speaker {
        CHEF("Chef kebabier"),
        CLIENT("Client");

        private final String libelle;

        Speaker(String libelle) {
            this.libelle = libelle;
        }
    }
}
