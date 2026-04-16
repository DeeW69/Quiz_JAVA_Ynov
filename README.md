# Quiz_JAVA_Ynov

Étapes du Gameplay
1. Dialogue d’Introduction
   Description
   • Le joueur commence par une séquence de dialogues avec un personnage
   expliquant la situation (la menace d’une bombe). Il doit avoir le texte qui
   s’affiche et une image représentant la personne qui dialogue.
   Implémentation
   • Données : Les dialogues sont gérés par un contrôleur qui détermine quel
   dialogue afficher et à quel moment.
   • Méthodes principales : Afficher le texte avec un effet de type écriture : machine
   à écrire. On peut aussi passer le dialogue rapidement en appuyant sur espace.
   • Package : javafx.animation.Timeline : permet de faire l’animation machine à
   écrire
   Visuel
+-------------------------------------------------------------------------------------+
|                                                                   +-----------------+
|                                                                   |                 | 
|            {TEXT_DU_CHEF}                                         |   {IMAGE_CHEF}  |
|                                                                   |                 |
|                                                                   +-----------------+
+-------------------------------------------------------------------------------------+

7. Dialogue
   • Chef
   Écoute-moi bien. Une bombe a été placée quelque part en ville, et tout repose sur toi.
   Nous n'avons pas de temps à perdre. Chaque seconde compte.
   • Chef
   Voici la situation : tu vas devoir résoudre une série d'énigmes. Chacune te donnera des
   indices pour localiser la bombe. Le temps presse, mais nous avons encore une chance
   si tu agis rapidement et avec précision.
   • Chef
   Je sais que ce n'est pas facile, mais je crois en toi. Nous avons les outils nécessaires, et
   tu as l'intelligence pour déchiffrer ces énigmes. Chaque réponse correcte nous
   rapproche de la solution.
   • Chef
   Ne laisse pas la pression te faire trébucher. Résous les énigmes, trouve l’emplacement
   de la bombe, et nous pourrons la désamorcer avant qu'il ne soit trop tard. On compte
   sur toi. La ville compte sur toi.
2. Jeu de quizz
   Description
   • Le joueur doit bien répondre à des questions fournies par une API de quizz, si le
   joueur arrive à 5 bonne réponses il passe à la suite sinon il continue à répondre
   aux questions jusqu’à passer à 5 bonne réponses.
   • L’API de trivial pursuite peut être utiliser pour ce jeu :
   https://opentdb.com/api_config.php mais vous pouvez aussi en utiliser une
   autre.
   Implémentation
   • Logique : Faire une requête à l’API en HTTP pour obtenir une question et vérifier
   la réponse.
   • Gestion des erreurs : Donner un feedback ("Correct" ou "Incorrect")