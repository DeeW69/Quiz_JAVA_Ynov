package com.example.quiz_java;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizSessionTest {

    @Test
    void creerSessionSelectionneVingtCinqQuestions() {
        QuizSession session = QuizSession.creer(construireQuestions(40), 25, new Random(7));

        assertEquals(25, session.nombreTotalQuestions());
        assertEquals(1, session.numeroQuestionCourante());
        assertEquals(0, session.score());
        assertFalse(session.estTerminee());
    }

    @Test
    void lesChoixGardentLaBonneReponseApresMelange() {
        QuizSession session = QuizSession.creer(construireQuestions(30), 25, new Random(2));

        QuizRound manche = session.mancheCourante();

        assertEquals(4, manche.reponses().size());
        assertTrue(manche.reponses().contains(manche.bonneReponse()));

        AnswerResult resultat = session.soumettreReponse(manche.bonneReponse());

        assertTrue(resultat.correcte());
        assertEquals(1, session.score());

        session.soumettreReponse(manche.bonneReponse());
        assertEquals(1, session.score());
    }

    private static List<QuizQuestion> construireQuestions(int count) {
        List<QuizQuestion> questions = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            questions.add(new QuizQuestion(
                    index % 3 == 0 ? "easy" : index % 3 == 1 ? "medium" : "hard",
                    "Question " + index,
                    "Correct " + index,
                    List.of("Wrong A " + index, "Wrong B " + index, "Wrong C " + index)
            ));
        }

        return questions;
    }
}
