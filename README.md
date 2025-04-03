# Robot Turtles

[Français](#français) | [English](#english)

## Français

Robot Turtles est un projet de jeu développé en Java, inspiré du jeu de société éducatif du même nom. Ce projet comprend la logique principale du jeu et une implémentation côté serveur.

### Structure du Projet

Le projet est divisé en deux parties principales :

1. **RobotTurtles-master** - Implémentation principale du jeu
   - `src/com/company` - Contient les classes et la logique principales du jeu
   - `imgCartes` - Ressources d'images utilisées dans le jeu

2. **robot-turtles-server** - Implémentation côté serveur
   - Projet Java basé sur Maven
   - Fournit un support pour le jeu multijoueur en réseau

### Introduction au Jeu

Robot Turtles est un jeu qui enseigne les concepts de base de la programmation. Les joueurs contrôlent leur tortue robot en utilisant des cartes d'instruction (comme avancer, tourner à gauche, tourner à droite, etc.), avec l'objectif d'atteindre la position du joyau. Le jeu comprend divers obstacles, et les joueurs doivent écrire des "programmes" (séquences d'instructions) pour éviter ces obstacles et atteindre leur objectif.

### Fonctionnalités Principales

- Support multijoueur
- Interface graphique
- Système de cartes d'instruction
- Système d'obstacles
- Fonctionnalité d'exécution de programme

### Stack Technique

- Java
- Swing (GUI)
- Maven (côté serveur)

### Comment Exécuter

#### Client

1. Cloner le dépôt
2. Ouvrir le répertoire RobotTurtles-master avec un IDE Java (comme IntelliJ IDEA)
3. Exécuter le fichier `src/com/company/Main.java`

#### Serveur

1. Accéder au répertoire robot-turtles-server
2. Construire le projet avec Maven : `mvn clean install`
3. Exécuter le fichier JAR généré

### Contribution

Les problèmes et suggestions d'amélioration sont les bienvenus !

### Licence

[MIT](LICENSE)

---

## English

Robot Turtles is a game project developed in Java, inspired by the educational board game of the same name. This project includes the core game logic and a server-side implementation.

### Project Structure

The project is divided into two main parts:

1. **RobotTurtles-master** - Core game implementation
   - `src/com/company` - Contains the main game classes and logic
   - `imgCartes` - Image resources used in the game

2. **robot-turtles-server** - Server-side implementation
   - Maven-based Java server project
   - Provides network multiplayer game support

### Game Introduction

Robot Turtles is a game that teaches basic programming concepts. Players control their robot turtle using instruction cards (such as move forward, turn left, turn right, etc.), with the goal of reaching the jewel position. The game includes various obstacles, and players need to write "programs" (instruction sequences) to avoid these obstacles and reach their goal.

### Main Features

- Multiplayer support
- Graphical user interface
- Instruction card system
- Obstacle system
- Program execution functionality

### Tech Stack

- Java
- Swing (GUI)
- Maven (server-side)

### How to Run

#### Client

1. Clone the repository
2. Open the RobotTurtles-master directory with a Java IDE (such as IntelliJ IDEA)
3. Run the `src/com/company/Main.java` file

#### Server

1. Navigate to the robot-turtles-server directory
2. Build the project with Maven: `mvn clean install`
3. Run the generated JAR file

### Contribution

Issues and improvement suggestions are welcome!

### License

[MIT](LICENSE) 