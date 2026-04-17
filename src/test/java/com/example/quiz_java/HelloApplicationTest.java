package com.example.quiz_java;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void startConfigureLaFenetreBriefing() throws Exception {
        System.setProperty("quiz.dialogue.letterDelayMillis", "1");
        System.setProperty("quiz.dialogue.autoAdvanceMillis", "25");

        HelloApplication application = new HelloApplication();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicReference<String> title = new AtomicReference<>();
        AtomicReference<Scene> scene = new AtomicReference<>();

        Platform.runLater(() -> {
            Stage stage = new Stage();

            try {
                application.start(stage);
                title.set(stage.getTitle());
                scene.set(stage.getScene());
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

        assertEquals("Briefing", title.get());
        assertNotNull(scene.get());
    }
}
