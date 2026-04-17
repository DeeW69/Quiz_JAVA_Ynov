# Quiz Scene Design

**Goal:** Replace the placeholder after the kebab dialogue with a playable quiz scene backed by `questions_kebab.json`.

## Current State

The application currently ends the intro flow on a final kebab handoff screen. The `Aller au jeu !` button routes to a placeholder scene because no playable game scene exists yet.

The project already contains a JSON dataset at `src/main/resources/com/example/quiz_java/questions_kebab.json`. Each entry includes:

- `difficulty`
- `question`
- `correct_answer`
- `incorrect_answers`

## User Experience

Clicking `Aller au jeu !` should start a quiz session.

Each session:

- draws 25 random questions from the JSON file
- keeps repeated variants if the dataset happens to contain them
- displays one question at a time
- mixes the correct and incorrect answers so the correct answer is not always first

Each question screen shows:

- current progress such as `Question 7 / 25`
- current score
- difficulty badge with a distinct style for `easy`, `medium`, or `hard`
- question text
- four answer buttons

When the user clicks an answer:

- the question locks
- the UI shows immediate feedback: `Bonne reponse` or `Mauvaise reponse`
- the correct answer is visually identifiable
- the quiz advances after a short pause

At the end of the 25 questions:

- the final score is displayed
- a `Rejouer` button starts a fresh random session
- a `Quitter` button closes the application

## Architecture

Introduce a dedicated quiz scene instead of extending `dialogue.java` with gameplay logic.

### Responsibilities

- `dialogue.java`
  - remains responsible for the kebab intro flow
  - transfers control to the quiz scene when `Aller au jeu !` is clicked

- `QuizScene` or equivalent scene builder
  - owns JavaFX layout for the quiz
  - displays question state, feedback state, and final score state

- `QuestionRepository` or equivalent loader
  - reads `questions_kebab.json`
  - maps JSON records into Java objects

- `QuizSession` or equivalent state holder
  - selects 25 random questions
  - shuffles answers per question
  - tracks index and score

This separation keeps resource loading, game rules, and JavaFX rendering testable and easier to evolve independently.

## Data Model

The quiz needs two levels of data:

### Raw Question

Represents one JSON entry:

- difficulty
- question text
- correct answer
- list of incorrect answers

### Playable Question

Represents one displayed round:

- difficulty
- question text
- shuffled answer list
- correct answer

The playable question can be derived from the raw question when a session starts.

## Visual Direction

The quiz scene should be more readable and game-oriented than the intro dialogue.

- warm or neutral background
- central question card
- clear hierarchy for badge, question text, and answers
- large answer buttons for quick interaction

Difficulty styling:

- `easy`: green
- `medium`: amber/orange
- `hard`: red

Feedback should be immediate but brief. The visual treatment must make it clear which answer was correct before the next question appears.

## Flow

### Quiz Start

When the intro ends and the user clicks `Aller au jeu !`:

1. load all questions from JSON
2. randomly select 25 of them
3. create a fresh session state
4. display question 1

### During a Question

1. render progress, score, difficulty, prompt, and answers
2. wait for one answer click
3. lock the answers
4. show feedback and highlight correctness
5. advance after a short delay

### End of Quiz

After question 25:

1. render score summary
2. show `Rejouer`
3. show `Quitter`

`Rejouer` starts a new 25-question random session from the same JSON source.

## Error Handling

- If the JSON file is missing, malformed, or empty, the application should fail clearly in development rather than silently showing a broken screen.
- If there are fewer than 25 questions in the dataset, the code should either cap to the available count or fail with an explicit message. Given the current dataset size, this is not expected to be a runtime problem, but the behavior should still be defined.
- Answer buttons must ignore repeated clicks once a choice is locked.

## Testing

Verification should cover:

- JSON loading into question objects
- random session creation with 25 questions
- answer shuffling preserving the correct answer
- score updates only for correct choices
- scene transition from intro final screen to quiz scene
- final result screen with `Rejouer` and `Quitter`

## Scope Boundaries

This change includes:

- replacing the placeholder next scene with a real quiz scene
- reading the existing kebab JSON dataset
- random 25-question sessions
- difficulty-based styling
- immediate correctness feedback
- replay and quit actions

This change does not include:

- multiplayer or timed mode
- persistence of high scores
- category selection
- deduplication of repeated question variants already present in the JSON
