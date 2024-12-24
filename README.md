# Car Sharing Application

Une application moderne de partage de voitures permettant aux utilisateurs de gérer et suivre leurs trajets partagés.

## Fonctionnalités

- Inscription et connexion des utilisateurs
- Création de nouveaux trajets
- Visualisation des statistiques personnelles
- Suivi des statistiques globales de l'application

## Stack Technique

### Backend
- Scala 3
- ZIO 2 pour la programmation fonctionnelle asynchrone
- Tapir pour la création d'API type-safe
- PostgreSQL comme base de données
- sbt pour la gestion des dépendances

### Frontend
- React 18
- TypeScript pour un typage statique
- Tailwind CSS pour le styling
- Vite comme bundler
- pnpm comme gestionnaire de paquets

## Installation

1. Backend
```bash
cd backend
sbt run
```

2. Frontend
```bash
cd frontend
pnpm install
pnpm dev
```

## Architecture

L'application suit une architecture moderne et scalable :
- Backend RESTful avec authentification JWT
- Frontend modulaire avec gestion d'état
- API typée de bout en bout
- Interface responsive et moderne

## Développement

Pour contribuer au projet :
1. Cloner le repository
2. Installer les dépendances du backend et du frontend
3. Suivre les conventions de code
4. Créer une pull request avec vos modifications
