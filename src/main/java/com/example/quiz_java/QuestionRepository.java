package com.example.quiz_java;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public final class QuestionRepository {

    private final String nomRessource;
    private final ObjectMapper mappeurJson;

    public QuestionRepository() {
        this("questions_kebab.json");
    }

    QuestionRepository(String nomRessource) {
        this.nomRessource = Objects.requireNonNull(nomRessource, "nomRessource");
        this.mappeurJson = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public List<QuizQuestion> chargerQuestions() {
        try (InputStream fluxEntree = QuestionRepository.class.getResourceAsStream(nomRessource)) {
            InputStream fluxRessource = Objects.requireNonNull(fluxEntree, "Ressource manquante: " + nomRessource);
            List<QuestionJson> questionsJson = mappeurJson.readValue(fluxRessource, new TypeReference<>() {
            });

            if (questionsJson.isEmpty()) {
                throw new IllegalStateException("Aucune question trouvee dans " + nomRessource);
            }

            return questionsJson.stream()
                    .map(questionJson -> new QuizQuestion(
                            questionJson.difficulte(),
                            questionJson.question(),
                            questionJson.bonneReponse(),
                            questionJson.mauvaisesReponses()
                    ))
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de lire les questions depuis " + nomRessource, exception);
        }
    }

    private record QuestionJson(
            String type,
            @JsonProperty("difficulty") String difficulte,
            @JsonProperty("category") String categorie,
            String question,
            @JsonProperty("correct_answer") String bonneReponse,
            @JsonProperty("incorrect_answers") List<String> mauvaisesReponses
    ) {
    }
}
