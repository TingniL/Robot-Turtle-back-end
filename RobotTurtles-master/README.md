# Robot Turtles AI Game

[🇫🇷 Version française plus bas](#robot-turtles-jeu-avec-ia)

## Overview
Robot Turtles is a board game turned digital, enhanced with reinforcement learning AI. Players navigate turtles through an 8x8 grid to reach jewels while avoiding obstacles. Our implementation uses a PPO (Proximal Policy Optimization) reinforcement learning model to control turtle movements or suggest optimal moves.

## Features
- Interactive game board with customizable layouts
- AI model trained using PPO reinforcement learning
- Multiple play modes (manual, AI-assisted, AI demo)
- Visualization of AI decision-making process
- User progress tracking and statistics

## Technical Stack
- **Backend**: Python with PyTorch for the AI model
- **Frontend**: React with TypeScript
- **Database**: MongoDB for user data and game states
- **Cache**: Redis for active sessions and leaderboards

## AI Model
Our AI uses a PPO reinforcement learning architecture with the following characteristics:
- 64-input state representation (8x8 flattened board)
- 4-action output (forward, turn left, turn right, laser)
- Custom reward function encouraging efficient path finding
- Trained over 1000 epochs with 90%+ success rate

The AI can:
- Navigate to jewels on its own
- Use lasers strategically to remove walls
- Adapt to different board layouts
- Provide optimal move suggestions

## Installation & Setup

### Backend
```bash
# Clone the repository
git clone https://github.com/yourusername/robot-turtles.git
cd robot-turtles

# Install dependencies
pip install -r requirements.txt

# Run the server
python server.py
```

### Frontend
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Run development server
npm start
```

### Database Setup
```bash
# Start MongoDB
docker-compose up -d mongodb redis

# Initialize database
python scripts/init_db.py
```

## Usage
1. Create an account or play as guest
2. Select a game mode (Practice, Challenge, AI Demo)
3. Use the control buttons to navigate your turtle
4. Complete levels to unlock more difficult challenges
5. View your statistics to track improvement

## Why This Model
We chose the PPO reinforcement learning algorithm for several reasons:
1. **Stability**: PPO offers stable training through clipped objective function, preventing radical policy updates
2. **Sample Efficiency**: Effective learning from limited interactions
3. **Performance**: Achieves 100% success rate on test scenarios
4. **Adaptability**: Handles unexpected states gracefully
5. **Interpretability**: Action probabilities provide insight into the model's decision-making

## Future Enhancements
- Multiplayer mode
- Custom level creator
- Advanced AI with different playing styles
- Mobile application

---

# Robot Turtles: Jeu avec IA

## Aperçu
Robot Turtles est un jeu de plateau devenu numérique, amélioré avec une IA d'apprentissage par renforcement. Les joueurs guident des tortues à travers une grille 8x8 pour atteindre des joyaux tout en évitant des obstacles. Notre implémentation utilise un modèle d'apprentissage par renforcement PPO (Proximal Policy Optimization) pour contrôler les mouvements des tortues ou suggérer des coups optimaux.

## Fonctionnalités
- Plateau de jeu interactif avec dispositions personnalisables
- Modèle d'IA entraîné par apprentissage par renforcement PPO
- Plusieurs modes de jeu (manuel, assisté par IA, démo IA)
- Visualisation du processus de prise de décision de l'IA
- Suivi des progrès et statistiques des utilisateurs

## Stack Technique
- **Backend** : Python avec PyTorch pour le modèle d'IA
- **Frontend** : React avec TypeScript
- **Base de données** : MongoDB pour les données utilisateur et les états de jeu
- **Cache** : Redis pour les sessions actives et les classements

## Modèle d'IA
Notre IA utilise une architecture d'apprentissage par renforcement PPO avec les caractéristiques suivantes :
- Représentation d'état à 64 entrées (plateau 8x8 aplati)
- Sortie à 4 actions (avancer, tourner à gauche, tourner à droite, laser)
- Fonction de récompense personnalisée encourageant la recherche de chemin efficace
- Entraîné sur plus de 1000 époques avec un taux de réussite de plus de 90%

L'IA peut :
- Naviguer seule vers les joyaux
- Utiliser les lasers de façon stratégique pour éliminer les murs
- S'adapter à différentes dispositions de plateau
- Fournir des suggestions de coups optimaux

## Installation et Configuration

### Backend
```bash
# Cloner le dépôt
git clone https://github.com/votrenomdutilisateur/robot-turtles.git
cd robot-turtles

# Installer les dépendances
pip install -r requirements.txt

# Lancer le serveur
python server.py
```

### Frontend
```bash
# Naviguer vers le répertoire frontend
cd frontend

# Installer les dépendances
npm install

# Lancer le serveur de développement
npm start
```

### Configuration de la Base de Données
```bash
# Démarrer MongoDB
docker-compose up -d mongodb redis

# Initialiser la base de données
python scripts/init_db.py
```

## Utilisation
1. Créez un compte ou jouez en tant qu'invité
2. Sélectionnez un mode de jeu (Pratique, Défi, Démo IA)
3. Utilisez les boutons de contrôle pour naviguer avec votre tortue
4. Complétez les niveaux pour débloquer des défis plus difficiles
5. Consultez vos statistiques pour suivre votre progression

## Pourquoi Ce Modèle
Nous avons choisi l'algorithme d'apprentissage par renforcement PPO pour plusieurs raisons :
1. **Stabilité** : PPO offre un entraînement stable grâce à une fonction objective écrêtée, empêchant les mises à jour radicales de politique
2. **Efficacité d'échantillonnage** : Apprentissage efficace à partir d'interactions limitées
3. **Performance** : Atteint un taux de réussite de 100% sur les scénarios de test
4. **Adaptabilité** : Gère élégamment les états inattendus
5. **Interprétabilité** : Les probabilités d'action donnent un aperçu du processus de prise de décision du modèle

## Améliorations Futures
- Mode multijoueur
- Créateur de niveaux personnalisés
- IA avancée avec différents styles de jeu
- Application mobile
