# Listing Service

Ce service gère les listings de surplus alimentaires pour la plateforme FoodSurplus.

## Fonctionnalités principales

- Création, affichage et filtrage des listings par catégorie
- Upload d'images pour chaque listing
- Filtrage dynamique côté frontend (HTML/JS)
- Stockage des catégories via enum Java

## Installation

1. Cloner le dépôt
2. Configurer la base de données PostgreSQL (voir `src/main/resources/application.properties`)
3. Lancer le backend :
   ```bash
   mvn spring-boot:run
   ```
4. Accéder à l'interface :
   ```
   http://localhost:8080/index.html
   ```

## Technologies

- Java 21 / Spring Boot 3
- PostgreSQL
- HTML/CSS/JS (frontend statique)
- Cloudinary (pour l'upload d'images)

## Auteur

- Faduul
