package com.example.quiz_java;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private static final String STYLE_ARRIERE_PLAN =
            "-fx-background-color: linear-gradient(to bottom, #23150d, #5c250a);";

    private final List<EtapeDialogue> etapes = List.of(
            new EtapeDialogue(Orateur.CHEF,
                    "Bienvenue au Doner Imperial. Ici, le pain croustille avec une ambition professionnelle.",
                    "chef_doner.png"),
            new EtapeDialogue(Orateur.CLIENT,
                    "Bonsoir chef... je voudrais juste un doner simple. Enfin, simple si cela existe encore ici.",
                    "client_doner.png"),
            new EtapeDialogue(Orateur.CHEF,
                    "Simple ? Je peux faire simple, mais ce serait insultant pour la sauce blanche.",
                    "chef_doner.png"),
            new EtapeDialogue(Orateur.CLIENT,
                    "Dans ce cas, mettez-moi un kebab serieux, avec salade, tomates, oignons et un peu de dignite.",
                    "client_doner.png"),
            new EtapeDialogue(Orateur.CHEF,
                    "Je vais te servir un monument roulant de viande, de salade et d'espoir croustillant.",
                    "chef_doner.png"),
            new EtapeDialogue(Orateur.CLIENT,
                    "Je ne comprends pas tout, mais je sens que ce doner va me marquer spirituellement.",
                    "client_doner.png"),
            new EtapeDialogue(Orateur.CHEF,
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
    private Label indiceEspace;
    private Button boutonContinuer;

    private Timeline chronologieTexte;
    private PauseTransition pauseAuto;
    private int indexEtape;
    private boolean attenteAvance;

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

    public void configurer(Stage fenetre) {
        this.fenetre = fenetre;
        fenetre.setTitle("Briefing");
        fenetre.setScene(creerScene());
        fenetre.setOnShown(event -> Platform.runLater(racine::requestFocus));
    }

    public Scene creerScene() {
        if (scene == null) {
            construireScene();
            afficherEtape(0);
        }

        return scene;
    }

    private void construireScene() {
        racine = new StackPane();
        racine.setStyle(STYLE_ARRIERE_PLAN);
        racine.setPadding(new Insets(30));
        racine.setFocusTraversable(true);

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

        indiceEspace = new Label("Appuyez sur ESPACE ou cliquez sur Continuer.");
        indiceEspace.setId("space-hint");
        indiceEspace.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        indiceEspace.setStyle("-fx-text-fill: #f1d0a3;");
        indiceEspace.setVisible(false);
        indiceEspace.setManaged(false);

        boutonContinuer = new Button("Continuer");
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
        boutonContinuer.setFocusTraversable(false);
        boutonContinuer.setOnAction(event -> tenterAvancer());

        VBox contenu = new VBox(26, ligneDialogue, indiceEspace, boutonContinuer);
        contenu.setAlignment(Pos.CENTER);
        contenu.setFillWidth(true);
        contenu.setMaxWidth(1050);

        racine.getChildren().setAll(contenu);
        scene = new Scene(racine, 1120, 760);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::gererToucheDialogue);
    }

    private void afficherEtape(int nouvelIndex) {
        stopperAnimations();

        indexEtape = nouvelIndex;
        attenteAvance = false;
        EtapeDialogue etape = etapes.get(indexEtape);

        nomOrateur.setText(etape.orateur().libelle);
        portrait.setImage(chargerImage(etape.image()));
        texteDialogue.setText("");
        masquerIndiceEspace();
        masquerBouton();

        if (etape.orateur() == Orateur.CHEF) {
            ligneDialogue.getChildren().setAll(bulleDialogue, cadrePortrait);
        } else {
            ligneDialogue.getChildren().setAll(cadrePortrait, bulleDialogue);
        }

        lancerMachineAEcrire(etape.texte(), () -> terminerEtape(etape));
    }

    private void terminerEtape(EtapeDialogue etape) {
        if (indexEtape == etapes.size() - 1) {
            lancerVersFin();
            return;
        }

        if (etape.orateur() == Orateur.CLIENT) {
            activerControlesAvance();
            return;
        }

        pauseAuto = new PauseTransition(delaiAuto);
        pauseAuto.setOnFinished(event -> activerControlesAvance());
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
        attenteAvance = false;
        masquerIndiceEspace();
        masquerBouton();

        ImageView imageFinale = new ImageView(chargerImage("chef_2.png"));
        imageFinale.setPreserveRatio(true);
        imageFinale.setSmooth(true);
        imageFinale.fitWidthProperty().bind(scene.widthProperty().subtract(100));
        imageFinale.fitHeightProperty().bind(scene.heightProperty().multiply(0.62));

        StackPane conteneurImage = new StackPane(imageFinale);
        conteneurImage.setPadding(new Insets(10));
        conteneurImage.setStyle(
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
        boutonJeu.setOnAction(event -> new QuizScene().afficher(fenetre));

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

        VBox contenuFinal = new VBox(24, conteneurImage, actions);
        contenuFinal.setAlignment(Pos.CENTER);
        contenuFinal.setMaxWidth(1050);

        racine.getChildren().setAll(contenuFinal);
    }

    private void gererToucheDialogue(KeyEvent evenement) {
        if (!attenteAvance || evenement.getCode() != KeyCode.SPACE) {
            return;
        }

        tenterAvancer();
        evenement.consume();
    }

    private void activerControlesAvance() {
        attenteAvance = true;
        afficherIndiceEspace();
        afficherBouton();
        Platform.runLater(racine::requestFocus);
    }

    private void tenterAvancer() {
        if (!attenteAvance) {
            return;
        }

        attenteAvance = false;
        masquerIndiceEspace();
        masquerBouton();
        avancer();
    }

    private void afficherBouton() {
        boutonContinuer.setVisible(true);
        boutonContinuer.setManaged(true);
    }

    private void afficherIndiceEspace() {
        indiceEspace.setVisible(true);
        indiceEspace.setManaged(true);
    }

    private void masquerBouton() {
        boutonContinuer.setVisible(false);
        boutonContinuer.setManaged(false);
    }

    private void masquerIndiceEspace() {
        indiceEspace.setVisible(false);
        indiceEspace.setManaged(false);
    }

    private void stopperAnimations() {
        if (chronologieTexte != null) {
            chronologieTexte.stop();
        }

        if (pauseAuto != null) {
            pauseAuto.stop();
        }

        attenteAvance = false;
    }

    private Image chargerImage(String nomRessource) {
        try (InputStream flux = dialogue.class.getResourceAsStream(nomRessource)) {
            InputStream fluxImage = Objects.requireNonNull(flux, "Ressource introuvable: " + nomRessource);
            return new Image(fluxImage);
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

    private record EtapeDialogue(Orateur orateur, String texte, String image) {
    }

    private enum Orateur {
        CHEF("Chef kebabier"),
        CLIENT("Client");

        private final String libelle;

        Orateur(String libelle) {
            this.libelle = libelle;
        }
    }
}
