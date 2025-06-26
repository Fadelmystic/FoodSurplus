# Frontend Surplus

Ce dossier contient l'interface utilisateur frontend pour l'application Surplus, séparée du backend pour éviter les problèmes d'authentification Keycloak.

## Structure du projet

```
frontend/
├── index.html                 # Page principale (connexion/inscription)
├── pages/
│   ├── seller-dashboard.html  # Dashboard vendeur
│   └── buyer-dashboard.html   # Dashboard acheteur/ONG
├── assets/
│   ├── css/
│   │   ├── styles.css         # Styles pour la page principale
│   │   ├── seller-styles.css  # Styles pour le dashboard vendeur
│   │   └── buyer-styles.css   # Styles pour le dashboard acheteur
│   └── js/
│       ├── script.js          # Script pour la page principale
│       ├── seller-script.js   # Script pour le dashboard vendeur
│       └── buyer-script.js    # Script pour le dashboard acheteur
└── README.md                  # Ce fichier
```

## Fonctionnalités

### Page principale (index.html)
- **Connexion** : Authentification utilisateur
- **Inscription** : Création de compte avec sélection de rôle (SELLER, BUYER, NGO)
- **Redirection automatique** selon le rôle après connexion/inscription

### Dashboard Vendeur (seller-dashboard.html)
- **Gestion des listings** : Voir, créer, modifier, supprimer ses listings
- **Profil utilisateur** : Consulter et modifier les informations personnelles
- **Interface dédiée** aux vendeurs avec fonctionnalités de gestion

### Dashboard Acheteur/ONG (buyer-dashboard.html)
- **Marketplace** : Parcourir tous les listings disponibles avec filtres
- **Passation de commandes** : Commander des produits via un modal
- **Suivi des commandes** : Voir l'historique et le statut des commandes
- **Profil utilisateur** : Consulter et modifier les informations personnelles

## URLs d'accès

- **Page principale** : `http://localhost:3000/index.html` (ou ouvrir directement le fichier)
- **Dashboard vendeur** : `http://localhost:3000/pages/seller-dashboard.html`
- **Dashboard acheteur/ONG** : `http://localhost:3000/pages/buyer-dashboard.html`

## Configuration des APIs

Les URLs des APIs sont configurées dans les fichiers JavaScript :

- **Service utilisateur** : `http://localhost:8080/api/users`
- **Service listing** : `http://localhost:8081/api/listings`
- **Service commande** : `http://localhost:8082/api/orders`

## Démarrage

### Option 1 : Serveur local simple
```bash
# Dans le dossier frontend
python -m http.server 3000
# ou
npx serve -p 3000
```

### Option 2 : Ouverture directe
Ouvrir directement le fichier `index.html` dans un navigateur web.

## Authentification

Le frontend utilise le localStorage pour stocker les informations d'authentification :
- `userId` : ID de l'utilisateur
- `userRole` : Rôle de l'utilisateur
- `userData` : Données complètes de l'utilisateur

## Fonctionnalités par rôle

### SELLER (Vendeur)
- Créer et gérer des listings
- Voir ses listings existants
- Modifier les informations de profil
- Supprimer des listings

### BUYER/NGO (Acheteur/ONG)
- Parcourir la marketplace
- Filtrer et rechercher des produits
- Passer des commandes
- Suivre l'historique des commandes
- Modifier les informations de profil

## Filtres disponibles (Marketplace)

- **Catégorie** : Nourriture, Vêtements, Électronique, Livres, Autre
- **Tri** : Plus récents, Prix croissant/décroissant, Expiration proche
- **Recherche** : Recherche textuelle dans le titre et la description

## Responsive Design

L'interface est entièrement responsive et s'adapte aux écrans mobiles et tablettes.

## Notes techniques

- **Pas d'authentification Keycloak** : Le frontend est séparé pour éviter les problèmes d'authentification
- **APIs REST** : Communication avec les services backend via des appels REST
- **LocalStorage** : Stockage local des données utilisateur
- **Validation côté client** : Validation des formulaires en JavaScript
- **Notifications** : Système de notifications pour les retours utilisateur

## Dépendances

Aucune dépendance externe requise. Le frontend utilise uniquement :
- HTML5
- CSS3
- JavaScript ES6+
- APIs Web standard (Fetch, LocalStorage, etc.) 