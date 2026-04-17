package com.example.quiz_java;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogueSceneTest {

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
    void firstStepShowsChefLayout() throws Exception {
        Stage stage = createConfiguredStage();

        assertEquals("Briefing", callFx(stage::getTitle));

        HBox conversationRow = callFx(() -> (HBox) stage.getScene().lookup("#conversation-row"));
        assertNotNull(conversationRow);
        assertEquals("dialogue-bubble", conversationRow.getChildren().get(0).getId());
        assertEquals("portrait-frame", conversationRow.getChildren().get(1).getId());
    }

    @Test
    void clientStepArrivesOnlyAfterChefPauseAndSpace() throws Exception {
        Stage stage = createConfiguredStage();

        waitUntil(() -> {
            Label spaceHint = callFx(() -> (Label) stage.getScene().lookup("#space-hint"));
            return spaceHint != null && spaceHint.isVisible();
        }, 5_000, "The chef step never entered SPACE wait mode");

        HBox conversationRow = callFx(() -> (HBox) stage.getScene().lookup("#conversation-row"));
        Label spaceHint = callFx(() -> (Label) stage.getScene().lookup("#space-hint"));
        Button continueButton = callFx(() -> (Button) stage.getScene().lookup("#continue-button"));

        assertEquals("dialogue-bubble", conversationRow.getChildren().get(0).getId());
        assertEquals("portrait-frame", conversationRow.getChildren().get(1).getId());
        assertTrue(spaceHint.getText().contains("ESPACE"));
        assertTrue(!continueButton.isVisible());

        pressSpace(stage);

        waitUntil(() -> {
            Button visibleContinueButton = callFx(() -> (Button) stage.getScene().lookup("#continue-button"));
            return visibleContinueButton != null && visibleContinueButton.isVisible();
        }, 5_000, "Pressing SPACE did not show the client step");

        conversationRow = callFx(() -> (HBox) stage.getScene().lookup("#conversation-row"));
        continueButton = callFx(() -> (Button) stage.getScene().lookup("#continue-button"));

        assertEquals("portrait-frame", conversationRow.getChildren().get(0).getId());
        assertEquals("dialogue-bubble", conversationRow.getChildren().get(1).getId());
        assertEquals("S'il te plait, chef !", continueButton.getText());
    }

    @Test
    void finalScreenLaunchesQuizScene() throws Exception {
        Stage stage = createConfiguredStage();

        waitUntil(() -> {
            Button nextSceneButton = callFx(() -> (Button) stage.getScene().lookup("#next-scene-button"));
            if (nextSceneButton != null && nextSceneButton.isVisible()) {
                return true;
            }

            Button continueButton = callFx(() -> (Button) stage.getScene().lookup("#continue-button"));
            if (continueButton != null && continueButton.isVisible()) {
                callFx(() -> {
                    continueButton.fire();
                    return null;
                });
            }

            Label spaceHint = callFx(() -> (Label) stage.getScene().lookup("#space-hint"));
            if (spaceHint != null && spaceHint.isVisible()) {
                pressSpace(stage);
            }

            return false;
        }, 12_000, "The intro never reached the final screen");

        Button nextSceneButton = callFx(() -> (Button) stage.getScene().lookup("#next-scene-button"));
        Button quitButton = callFx(() -> (Button) stage.getScene().lookup("#quit-button"));

        assertNotNull(nextSceneButton);
        assertNotNull(quitButton);
        assertEquals("Aller au jeu !", nextSceneButton.getText());
        assertEquals("Quitter", quitButton.getText());

        callFx(() -> {
            nextSceneButton.fire();
            return null;
        });

        waitUntil(() -> callFx(() -> stage.getScene().lookup("#quiz-root") != null),
                2_000,
                "The quiz scene did not appear");

        Label progressLabel = callFx(() -> (Label) stage.getScene().lookup("#progress-label"));
        assertTrue(progressLabel.getText().contains("Question 1"));
    }

    private Stage createConfiguredStage() throws Exception {
        System.setProperty("quiz.dialogue.letterDelayMillis", "1");
        System.setProperty("quiz.dialogue.autoAdvanceMillis", "25");

        return callFx(() -> {
            Stage stage = new Stage();
            new dialogue().configurer(stage);
            return stage;
        });
    }

    private static void pressSpace(Stage stage) throws Exception {
        callFx(() -> {
            Scene scene = stage.getScene();
            Event.fireEvent(scene, new KeyEvent(
                    KeyEvent.KEY_PRESSED,
                    " ",
                    " ",
                    KeyCode.SPACE,
                    false,
                    false,
                    false,
                    false
            ));
            return null;
        });
    }

    private static void waitUntil(CheckedBooleanSupplier supplier, long timeoutMillis, String failureMessage) throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (System.currentTimeMillis() < deadline) {
            if (supplier.getAsBoolean()) {
                return;
            }

            Thread.sleep(25);
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
