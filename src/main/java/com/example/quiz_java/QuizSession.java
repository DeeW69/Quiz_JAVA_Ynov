package com.example.quiz_java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class QuizSession {

    private final List<QuizRound> manches;
    private int indexCourant;
    private int score;
    private boolean mancheCouranteRepondue;
    private AnswerResult dernierResultatReponse;

    private QuizSession(List<QuizRound> manches) {
        this.manches = List.copyOf(manches);
    }

    public static QuizSession creer(List<QuizQuestion> toutesLesQuestions, int nombreQuestionsDemande, Random aleatoire) {
        Objects.requireNonNull(toutesLesQuestions, "toutesLesQuestions");
        Objects.requireNonNull(aleatoire, "aleatoire");

        if (toutesLesQuestions.isEmpty()) {
            throw new IllegalArgumentException("La liste des questions du quiz ne peut pas etre vide");
        }

        int nombreQuestionsReel = Math.min(nombreQuestionsDemande, toutesLesQuestions.size());
        List<QuizQuestion> pioche = new ArrayList<>(toutesLesQuestions);
        Collections.shuffle(pioche, aleatoire);

        List<QuizRound> manches = pioche.stream()
                .limit(nombreQuestionsReel)
                .map(question -> versManche(question, aleatoire))
                .toList();

        return new QuizSession(manches);
    }

    public QuizRound mancheCourante() {
        if (estTerminee()) {
            throw new IllegalStateException("Aucune manche courante quand la session est terminee");
        }

        return manches.get(indexCourant);
    }

    public int numeroQuestionCourante() {
        return Math.min(indexCourant + 1, nombreTotalQuestions());
    }

    public int nombreTotalQuestions() {
        return manches.size();
    }

    public int score() {
        return score;
    }

    public boolean estTerminee() {
        return indexCourant >= manches.size();
    }

    public AnswerResult soumettreReponse(String reponseSelectionnee) {
        if (estTerminee()) {
            throw new IllegalStateException("Impossible de repondre apres la fin de la session");
        }

        if (mancheCouranteRepondue) {
            return dernierResultatReponse;
        }

        QuizRound manche = mancheCourante();
        boolean correcte = manche.bonneReponse().equals(reponseSelectionnee);
        if (correcte) {
            score++;
        }

        mancheCouranteRepondue = true;
        dernierResultatReponse = new AnswerResult(correcte, manche.bonneReponse());
        return dernierResultatReponse;
    }

    public void passerALaQuestionSuivante() {
        if (estTerminee()) {
            return;
        }

        if (!mancheCouranteRepondue) {
            throw new IllegalStateException("Impossible d'avancer avant d'avoir repondu a la manche courante");
        }

        indexCourant++;
        mancheCouranteRepondue = false;
        dernierResultatReponse = null;
    }

    private static QuizRound versManche(QuizQuestion question, Random aleatoire) {
        List<String> reponses = new ArrayList<>(question.mauvaisesReponses());
        reponses.add(question.bonneReponse());
        Collections.shuffle(reponses, aleatoire);

        return new QuizRound(question.difficulte(), question.question(), reponses, question.bonneReponse());
    }
}
