package com.example.quiz_java;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloApplicationTest {

    @BeforeAll
    static void initToolkit() throws InterruptedException {
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
    void startConfigureLaFenetreDeSelection() throws Exception {
        HelloApplication application = new HelloApplication();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicReference<String> title = new AtomicReference<>();
        AtomicReference<Scene> scene = new AtomicReference<>();
        AtomicReference<Button> boutonQuiz = new AtomicReference<>();
        AtomicReference<Button> boutonAutresJeux = new AtomicReference<>();
        AtomicReference<Button> boutonQuitter = new AtomicReference<>();
        AtomicReference<String> titreApresClicQuiz = new AtomicReference<>();
        AtomicReference<HBox> ligneDialogueApresClicQuiz = new AtomicReference<>();

        Platform.runLater(() -> {
            Stage stage = new Stage();

            try {
                application.start(stage);
                title.set(stage.getTitle());
                scene.set(stage.getScene());
                boutonQuiz.set((Button) stage.getScene().lookup("#go-quiz-button"));
                boutonAutresJeux.set((Button) stage.getScene().lookup("#coming-soon-button"));
                boutonQuitter.set((Button) stage.getScene().lookup("#quit-button"));
                boutonQuiz.get().fire();
                titreApresClicQuiz.set(stage.getTitle());
                ligneDialogueApresClicQuiz.set((HBox) stage.getScene().lookup("#conversation-row"));
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                stage.close();
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX stage setup timed out");
        }

        if (failure.get() != null) {
            throw new AssertionError(failure.get());
        }

        assertEquals("Selection des jeux", title.get());
        assertNotNull(scene.get());
        assertNotNull(boutonQuiz.get());
        assertNotNull(boutonAutresJeux.get());
        assertNotNull(boutonQuitter.get());
        assertEquals("Aller au quiz", boutonQuiz.get().getText());
        assertTrue(boutonAutresJeux.get().isDisable());
        assertEquals("Quitter", boutonQuitter.get().getText());
        assertEquals("Briefing", titreApresClicQuiz.get());
        assertNotNull(ligneDialogueApresClicQuiz.get());
    }
}
