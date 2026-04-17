package com.example.quiz_java;

import java.util.List;
import java.util.Objects;

public record QuizRound(String difficulte, String question, List<String> reponses, String bonneReponse) {

    public QuizRound {
        difficulte = Objects.requireNonNull(difficulte, "difficulte");
        question = Objects.requireNonNull(question, "question");
        reponses = List.copyOf(Objects.requireNonNull(reponses, "reponses"));
        bonneReponse = Objects.requireNonNull(bonneReponse, "bonneReponse");
    }
}
