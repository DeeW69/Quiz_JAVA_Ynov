package com.example.quiz_java;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionRepositoryTest {

    @Test
    void chargerQuestionsLitLeJsonKebab() {
        List<QuizQuestion> questions = new QuestionRepository().chargerQuestions();

        assertNotNull(questions);
        assertTrue(questions.size() >= 25);
        assertFalse(questions.getFirst().difficulte().isBlank());
        assertFalse(questions.getFirst().question().isBlank());
        assertFalse(questions.getFirst().bonneReponse().isBlank());
        assertTrue(questions.getFirst().mauvaisesReponses().size() >= 3);
    }
}
