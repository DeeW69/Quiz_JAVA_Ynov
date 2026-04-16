# Doner Dialogue Intro Design

**Goal:** Replace the current briefing text screen with a staged conversation between a kebab chef and a client, then end on a full-screen final image with navigation buttons.

## Current State

The app starts in `HelloApplication` and currently displays a JavaFX scene created by `dialogue.java`. That scene shows a single block of animated text on a dark background.

Three image assets are currently present in the repository root under `src\`:

- `src/chef_doner.png`
- `src/client_doner.png`
- `src/chef_2.png`

For reliable JavaFX resource loading, the implementation will normalize these assets under `src/main/resources/com/example/quiz_java/`.

## User Experience

The intro becomes a visual chat-like exchange:

- Chef messages use the layout from the provided mockup:
  - large text bubble on the left
  - chef portrait on the right
- Client messages mirror the layout:
  - client portrait on the left
  - text bubble on the right
- Each message is revealed with a typewriter effect.
- Chef lines advance automatically once their text has finished animating.
- Client lines wait for a button labeled `S'il te plaît, chef !` after the text is fully visible.

The tone of the dialogue is lightly absurd and funny, but still readable as a real exchange.

## Final Screen

After the last conversation line:

- the chat layout disappears
- `chef_2.png` fills the page as the dominant visual
- two buttons appear:
  - `Aller au jeu !`
  - `Quitter`

`Aller au jeu !` will call a placeholder transition because the next game scene does not exist yet. The placeholder scene will clearly state that the next scene is not created yet.

`Quitter` will close the application.

## Architecture

Keep a single reusable JavaFX scene builder for the intro, driven by structured dialogue data instead of hardcoded layout branches for every line.

### Dialogue Model

Each dialogue entry will define:

- speaker: `CHEF` or `CLIENT`
- text
- image resource
- progression mode: automatic or button-gated

### Screen States

The intro flow has two explicit states:

- `conversation`
- `final`

The conversation state reuses one layout container and swaps alignment, image placement, button visibility, and text content based on the current entry.

The final state replaces the conversation content with a full-page hero image and action buttons.

## Visual Structure

### Conversation State

- dark background kept from the current intro unless implementation reveals a better matching variant
- one large framed message area
- one portrait area sized consistently across all chat steps
- readable typography sized for full-screen use
- spacing that matches the user's ASCII mockup: text dominates the width, portrait is secondary but prominent

### Final State

- `chef_2.png` displayed prominently at large size
- buttons overlaid or placed in a clear lower action area
- no dialogue bubble remains visible

## Content Direction

The written dialogue should feature:

- a kebab chef with dramatic confidence
- a client who is polite, slightly bewildered, and part of the joke
- escalating absurdity around the doner order
- a finish where the chef triumphantly delivers the kebab

The humor should stay simple and accessible, not surreal to the point of confusion.

## Error Handling

- If an image resource is missing, the UI should fail clearly during development rather than silently showing a broken layout.
- If the next-scene placeholder is triggered, the user must see an explicit temporary screen rather than nothing happening.

## Testing

Verification should cover:

- the intro scene still attaches correctly to the startup stage
- the first dialogue step loads with the chef layout
- client steps expose the `S'il te plaît, chef !` button only after the text animation completes
- the final screen exposes `Aller au jeu !` and `Quitter`
- the placeholder path for the next scene can be entered without crashing

## Scope Boundaries

This change includes:

- replacing the existing intro content
- introducing image-backed dialogue bubbles
- adding the final full-screen handoff image
- adding a placeholder next-scene transition

This change does not include:

- building the actual game scene after the intro
- redesigning unrelated application screens
- creating a save system, skip system, or branching dialogue
