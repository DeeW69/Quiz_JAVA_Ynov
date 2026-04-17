package com.example.quiz_java;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public final class QuizScene {

    private static final String STYLE_ARRIERE_PLAN =
            "-fx-background-color: linear-gradient(to bottom, #f6ead7, #f2c38e);";

    private final Supplier<List<QuizQuestion>> fournisseurQuestions;
    private final Random aleatoire;
    private final Duration delaiRetour;

    private Stage fenetre;
    private Scene scene;
    private StackPane conteneurRacine;
    private QuizSession session;
    private PauseTransition pauseRetour;

    public QuizScene() {
        this(new QuestionRepository()::chargerQuestions, new Random(), Duration.millis(lireDelai("quiz.scene.feedbackDelayMillis", 900)));
    }

    QuizScene(List<QuizQuestion> questions, Random aleatoire, Duration delaiRetour) {
        this(() -> questions, aleatoire, delaiRetour);
    }

    private QuizScene(Supplier<List<QuizQuestion>> fournisseurQuestions, Random aleatoire, Duration delaiRetour) {
        this.fournisseurQuestions = Objects.requireNonNull(fournisseurQuestions, "fournisseurQuestions");
        this.aleatoire = Objects.requireNonNull(aleatoire, "aleatoire");
        this.delaiRetour = Objects.requireNonNull(delaiRetour, "delaiRetour");
    }

    public void afficher(Stage fenetre) {
        this.fenetre = fenetre;

        if (scene == null) {
            conteneurRacine = new StackPane();
            conteneurRacine.setStyle(STYLE_ARRIERE_PLAN);
            conteneurRacine.setPadding(new Insets(28));
            scene = new Scene(conteneurRacine, 1120, 760);
        }

        fenetre.setTitle("Quiz kebab");
        fenetre.setScene(scene);
        demarrerNouvelleSession();
    }

    private void demarrerNouvelleSession() {
        arreterPauseRetour();
        session = QuizSession.creer(fournisseurQuestions.get(), 25, aleatoire);
        afficherQuestion();
    }

    private void afficherQuestion() {
        QuizRound manche = session.mancheCourante();

        Label etiquetteProgression = creerLabelMeta("progress-label", "Question " + session.numeroQuestionCourante() + " / " + session.nombreTotalQuestions());
        Label etiquetteScore = creerLabelMeta("score-label", "Score: " + session.score());
        Region espaceur = new Region();
        HBox.setHgrow(espaceur, Priority.ALWAYS);

        HBox entete = new HBox(16, etiquetteProgression, espaceur, etiquetteScore);
        entete.setAlignment(Pos.CENTER_LEFT);

        Label badgeDifficulte = new Label(manche.difficulte());
        badgeDifficulte.setId("difficulty-badge");
        badgeDifficulte.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        badgeDifficulte.getStyleClass().setAll("difficulty-badge", "difficulty-" + manche.difficulte());
        badgeDifficulte.setStyle(stylePourDifficulte(manche.difficulte()));

        Label etiquetteQuestion = new Label(manche.question());
        etiquetteQuestion.setId("question-label");
        etiquetteQuestion.setWrapText(true);
        etiquetteQuestion.setTextAlignment(TextAlignment.CENTER);
        etiquetteQuestion.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
        etiquetteQuestion.setStyle("-fx-text-fill: #3f1f0e;");

        Label etiquetteRetour = new Label();
        etiquetteRetour.setId("feedback-label");
        etiquetteRetour.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        etiquetteRetour.setVisible(false);
        etiquetteRetour.setManaged(false);

        VBox boiteReponses = new VBox(14);
        boiteReponses.setId("answers-box");
        boiteReponses.setAlignment(Pos.CENTER);

        for (String reponse : manche.reponses()) {
            Button boutonReponse = new Button(reponse);
            boutonReponse.setMaxWidth(Double.MAX_VALUE);
            boutonReponse.setFont(Font.font("Georgia", 20));
            boutonReponse.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.82);" +
                            "-fx-text-fill: #311607;" +
                            "-fx-background-radius: 14;" +
                            "-fx-padding: 16 18;"
            );
            boutonReponse.setOnAction(event -> traiterReponse(boutonReponse, boiteReponses, etiquetteRetour));
            boiteReponses.getChildren().add(boutonReponse);
        }

        VBox carte = new VBox(18, entete, badgeDifficulte, etiquetteQuestion, etiquetteRetour, boiteReponses);
        carte.setAlignment(Pos.CENTER);
        carte.setMaxWidth(860);
        carte.setPadding(new Insets(30));
        carte.setStyle(
                "-fx-background-color: rgba(255, 250, 241, 0.9);" +
                        "-fx-border-color: #7b3f1b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;"
        );

        VBox racineQuiz = new VBox(carte);
        racineQuiz.setId("quiz-root");
        racineQuiz.setAlignment(Pos.CENTER);

        conteneurRacine.getChildren().setAll(racineQuiz);
    }

    private void traiterReponse(Button boutonSelectionne, VBox boiteReponses, Label etiquetteRetour) {
        AnswerResult resultat = session.soumettreReponse(boutonSelectionne.getText());

        for (javafx.scene.Node noeud : boiteReponses.getChildren()) {
            Button bouton = (Button) noeud;
            bouton.setDisable(true);

            if (bouton.getText().equals(resultat.bonneReponse())) {
                bouton.setStyle(
                        "-fx-background-color: #9fd58f;" +
                                "-fx-text-fill: #15340d;" +
                                "-fx-background-radius: 14;" +
                                "-fx-padding: 16 18;"
                );
            } else if (bouton == boutonSelectionne && !resultat.correcte()) {
                bouton.setStyle(
                        "-fx-background-color: #e89999;" +
                                "-fx-text-fill: #4b1010;" +
                                "-fx-background-radius: 14;" +
                                "-fx-padding: 16 18;"
                );
            }
        }

        etiquetteRetour.setText(resultat.correcte() ? "Bonne reponse" : "Mauvaise reponse");
        etiquetteRetour.setStyle(resultat.correcte() ? "-fx-text-fill: #216118;" : "-fx-text-fill: #8d1f1f;");
        etiquetteRetour.setVisible(true);
        etiquetteRetour.setManaged(true);

        boolean derniereQuestion = session.numeroQuestionCourante() == session.nombreTotalQuestions();
        arreterPauseRetour();
        pauseRetour = new PauseTransition(delaiRetour);
        pauseRetour.setOnFinished(event -> {
            if (derniereQuestion) {
                afficherResultat();
            } else {
                session.passerALaQuestionSuivante();
                afficherQuestion();
            }
        });
        pauseRetour.play();
    }

    private void afficherResultat() {
        arreterPauseRetour();

        Label titre = new Label("Service termine");
        titre.setFont(Font.font("Georgia", FontWeight.BOLD, 34));
        titre.setStyle("-fx-text-fill: #3f1f0e;");

        Label etiquetteResultat = new Label("Score final: " + session.score() + " / " + session.nombreTotalQuestions());
        etiquetteResultat.setId("result-label");
        etiquetteResultat.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        etiquetteResultat.setStyle("-fx-text-fill: #5a2307;");

        Button boutonRejouer = new Button("Rejouer");
        boutonRejouer.setId("replay-button");
        boutonRejouer.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        boutonRejouer.setStyle(
                "-fx-background-color: #7bb85f;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 12 22;"
        );
        boutonRejouer.setOnAction(event -> demarrerNouvelleSession());

        Button boutonQuitter = new Button("Quitter");
        boutonQuitter.setId("quit-button");
        boutonQuitter.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        boutonQuitter.setStyle(
                "-fx-background-color: #7b3f1b;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 12 22;"
        );
        boutonQuitter.setOnAction(event -> fenetre.close());

        HBox actions = new HBox(16, boutonRejouer, boutonQuitter);
        actions.setAlignment(Pos.CENTER);

        VBox racineResultat = new VBox(22, titre, etiquetteResultat, actions);
        racineResultat.setId("result-root");
        racineResultat.setAlignment(Pos.CENTER);
        racineResultat.setPadding(new Insets(36));
        racineResultat.setStyle(
                "-fx-background-color: rgba(255, 250, 241, 0.92);" +
                        "-fx-border-color: #7b3f1b;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;"
        );

        conteneurRacine.getChildren().setAll(racineResultat);
    }

    private Label creerLabelMeta(String id, String texte) {
        Label etiquette = new Label(texte);
        etiquette.setId(id);
        etiquette.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        etiquette.setStyle("-fx-text-fill: #5a2307;");
        return etiquette;
    }

    private String stylePourDifficulte(String difficulte) {
        return switch (difficulte) {
            case "easy" -> "-fx-background-color: #9fd58f; -fx-text-fill: #15340d; -fx-background-radius: 999; -fx-padding: 8 16;";
            case "medium" -> "-fx-background-color: #efb45f; -fx-text-fill: #4b2506; -fx-background-radius: 999; -fx-padding: 8 16;";
            case "hard" -> "-fx-background-color: #d87474; -fx-text-fill: #4b1010; -fx-background-radius: 999; -fx-padding: 8 16;";
            default -> "-fx-background-color: #cccccc; -fx-text-fill: #222222; -fx-background-radius: 999; -fx-padding: 8 16;";
        };
    }

    private void arreterPauseRetour() {
        if (pauseRetour != null) {
            pauseRetour.stop();
        }
    }

    private static long lireDelai(String cle, long valeurParDefaut) {
        String valeur = System.getProperty(cle);
        if (valeur == null || valeur.isBlank()) {
            return valeurParDefaut;
        }

        return Long.parseLong(valeur);
    }
}
