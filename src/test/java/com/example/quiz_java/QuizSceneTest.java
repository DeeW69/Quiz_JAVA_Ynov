package com.example.quiz_java;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizSceneTest {

    @BeforeAll
    static void initToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX toolkit startup timed out");
        }

        Platform.setImplicitExit(false);
    }

    @Test
    void laSceneQuizAfficheProgressionDifficulteEtReponses() throws Exception {
        Stage stage = createQuizStage();

        VBox root = callFx(() -> (VBox) stage.getScene().lookup("#quiz-root"));
        Label progressLabel = callFx(() -> (Label) stage.getScene().lookup("#progress-label"));
        Label scoreLabel = callFx(() -> (Label) stage.getScene().lookup("#score-label"));
        Label difficultyBadge = callFx(() -> (Label) stage.getScene().lookup("#difficulty-badge"));
        VBox answersBox = callFx(() -> (VBox) stage.getScene().lookup("#answers-box"));

        assertNotNull(root);
        assertEquals("Question 1 / 2", progressLabel.getText());
        assertEquals("Score: 0", scoreLabel.getText());
        assertEquals("easy", difficultyBadge.getText());
        assertTrue(difficultyBadge.getStyleClass().contains("difficulty-easy"));
        assertEquals(4, answersBox.getChildren().size());
    }

    @Test
    void laFinDuQuizAfficheScoreRejouerEtQuitter() throws Exception {
        Stage stage = createQuizStage();

        clickAnswer(stage, "A1");
        waitUntil(() -> callFx(() -> "Question 2 / 2".equals(((Label) stage.getScene().lookup("#progress-label")).getText())),
                2_000,
                "The quiz did not advance to question 2");

        clickAnswer(stage, "B2");
        waitUntil(() -> callFx(() -> stage.getScene().lookup("#result-root") != null),
                2_000,
                "The quiz did not reach the result screen");

        Label resultLabel = callFx(() -> (Label) stage.getScene().lookup("#result-label"));
        Button replayButton = callFx(() -> (Button) stage.getScene().lookup("#replay-button"));
        Button quitButton = callFx(() -> (Button) stage.getScene().lookup("#quit-button"));

        assertTrue(resultLabel.getText().contains("1 / 2"));
        assertNotNull(replayButton);
        assertNotNull(quitButton);

        callFx(() -> {
            replayButton.fire();
            return null;
        });

        waitUntil(() -> callFx(() -> stage.getScene().lookup("#quiz-root") != null),
                2_000,
                "Replay did not restart the quiz");

        Label progressLabel = callFx(() -> (Label) stage.getScene().lookup("#progress-label"));
        assertEquals("Question 1 / 2", progressLabel.getText());
    }

    private static Stage createQuizStage() throws Exception {
        QuizScene quizScene = new QuizScene(
                List.of(
                        new QuizQuestion("easy", "Question 1", "A1", List.of("B1", "C1", "D1")),
                        new QuizQuestion("hard", "Question 2", "A2", List.of("B2", "C2", "D2"))
                ),
                new Random(0),
                javafx.util.Duration.millis(1)
        );

        return callFx(() -> {
            Stage stage = new Stage();
            quizScene.afficher(stage);
            return stage;
        });
    }

    private static void clickAnswer(Stage stage, String answerText) throws Exception {
        callFx(() -> {
            VBox answersBox = (VBox) stage.getScene().lookup("#answers-box");

            for (javafx.scene.Node node : answersBox.getChildren()) {
                Button button = (Button) node;
                if (button.getText().equals(answerText)) {
                    button.fire();
                    return null;
                }
            }

            throw new AssertionError("Answer button not found: " + answerText);
        });
    }

    private static void waitUntil(CheckedBooleanSupplier supplier, long timeoutMillis, String failureMessage) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < deadline) {
            if (supplier.getAsBoolean()) {
                return;
            }

            Thread.sleep(20);
        }

        throw new AssertionError(failureMessage);
    }

    private static <T> T callFx(Callable<T> callable) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(callable.call());
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX operation timed out");
        }

        if (failure.get() != null) {
            throw new AssertionError(failure.get());
        }

        return result.get();
    }

    @FunctionalInterface
    private interface CheckedBooleanSupplier {
        boolean getAsBoolean() throws Exception;
    }
}
