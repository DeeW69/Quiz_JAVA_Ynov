package com.example.quiz_java;

import java.util.List;
import java.util.Objects;

public record QuizQuestion(String difficulte, String question, String bonneReponse, List<String> mauvaisesReponses) {

    public QuizQuestion {
        difficulte = Objects.requireNonNull(difficulte, "difficulte");
        question = Objects.requireNonNull(question, "question");
        bonneReponse = Objects.requireNonNull(bonneReponse, "bonneReponse");
        mauvaisesReponses = List.copyOf(Objects.requireNonNull(mauvaisesReponses, "mauvaisesReponses"));
    }
}
