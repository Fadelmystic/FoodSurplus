# Interface Frontend - Surplus Platform

## 📋 Description

Système complet d'interfaces frontend pour la plateforme Surplus avec redirection automatique selon les rôles utilisateur.

## 🏗️ Architecture

### Services et Interfaces

1. **User Management** (`surplus-user-management`)
   - Page principale : `index.html`
   - Connexion/Inscription
   - Redirection selon le rôle

2. **Listing Service** (`surplus-listing`)
   - Interface Vendeur : `seller-dashboard.html`
   - Gestion des listings
   - Profil vendeur

3. **Order Service** (`surplus-order`)
   - Interface Acheteur/ONG : `buyer-dashboard.html`
   - Marketplace
   - Système de commandes

## 🔄 Flux de Redirection

```
Connexion/Inscription → Vérification du rôle → Redirection automatique
                                                      ↓
                    ┌─────────────────┬─────────────────┐
                    ↓                 ↓                 ↓
              SELLER              BUYER               NGO
                    ↓                 ↓                 ↓
            seller-dashboard   buyer-dashboard   buyer-dashboard
```

## 🚀 Démarrage Rapide

### 1. Démarrer les Services
```bash
# Service User Management
cd surplus-user-management
mvn spring-boot:run

# Service Listing
cd surplus-listing
mvn spring-boot:run

# Service Order
cd surplus-order
mvn spring-boot:run
```

### 2. Accéder aux Interfaces
- **Page principale** : `http://localhost:8080`
- **Interface Vendeur** : `http://localhost:8081/seller-dashboard.html`
- **Interface Acheteur** : `http://localhost:8082/buyer-dashboard.html`

## ⚙️ Configuration Requise

### Endpoints API à Implémenter

#### User Management Service
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @PostMapping("/register")
    @GetMapping("/{userId}")
    @PutMapping("/{userId}")
    @GetMapping("/{userId}/enabled")
}
```

#### Listing Service
```java
@RestController
@RequestMapping("/api/listings")
public class ListingController {
    @GetMapping
    @PostMapping
    @GetMapping("/seller/{sellerId}")
    @PutMapping("/{id}")
    @DeleteMapping("/{id}")
}
```

#### Order Service
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @PostMapping
    @GetMapping("/buyer/{buyerId}")
    @PutMapping("/{id}")
}
```

### Authentification Keycloak
```javascript
// Endpoint à configurer dans script.js
const response = await fetch('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
});
```

## 🐛 Dépannage

### Erreurs Courantes

#### 1. "Utilisateur non connecté"
**Cause** : Pas d'ID utilisateur dans localStorage
**Solution** : Vérifier que l'inscription/connexion fonctionne

#### 2. "Erreur de connexion au serveur"
**Cause** : Service non démarré ou endpoint incorrect
**Solution** : 
- Vérifier que tous les services sont démarrés
- Vérifier les URLs des APIs dans les fichiers JavaScript

#### 3. "Rôle non reconnu"
**Cause** : Rôle non géré dans la fonction de redirection
**Solution** : Vérifier que le rôle est dans la liste autorisée

#### 4. Page blanche
**Cause** : Erreur JavaScript
**Solution** : 
- Ouvrir la console du navigateur (F12)
- Vérifier les erreurs JavaScript
- Vérifier que tous les fichiers sont chargés

### Debug

#### Console Browser
```javascript
// Vérifier les données utilisateur
console.log(localStorage.getItem('userId'));
console.log(localStorage.getItem('userRole'));

// Vérifier les appels API
// Ouvrir l'onglet Network dans les outils de développement
```

#### Logs Serveur
```bash
# Vérifier les logs Spring Boot
tail -f logs/application.log
```

## 📱 Fonctionnalités par Rôle

### SELLER
- ✅ Créer des listings
- ✅ Modifier des listings
- ✅ Supprimer des listings
- ✅ Voir ses listings
- ✅ Gérer son profil

### BUYER/NGO
- ✅ Parcourir le marketplace
- ✅ Filtrer et rechercher
- ✅ Passer des commandes
- ✅ Voir l'historique des commandes
- ✅ Gérer son profil

## 🎨 Personnalisation

### Couleurs
- **Vendeur** : Dégradé bleu/violet (`#667eea` → `#764ba2`)
- **Acheteur/ONG** : Dégradé vert (`#28a745` → `#20c997`)

### Modifier les Styles
```css
/* Dans seller-styles.css ou buyer-styles.css */
.dashboard-header {
    background: linear-gradient(135deg, #VOTRE_COULEUR1 0%, #VOTRE_COULEUR2 100%);
}
```

## 🔒 Sécurité

### Recommandations
1. Implémenter l'authentification Keycloak
2. Ajouter la validation côté serveur
3. Utiliser HTTPS en production
4. Implémenter CSRF protection
5. Valider tous les inputs

### Stockage Local
```javascript
// Données stockées dans localStorage
localStorage.setItem('userId', userId);
localStorage.setItem('userRole', role);
localStorage.setItem('userData', JSON.stringify(userData));
```

## 📞 Support

### En cas de problème
1. Vérifier la console du navigateur
2. Vérifier les logs des services
3. Vérifier la configuration des endpoints
4. Vérifier que tous les services sont démarrés

### Logs utiles
```bash
# User Management
curl http://localhost:8080/api/users/health

# Listing Service  
curl http://localhost:8081/api/listings

# Order Service
curl http://localhost:8082/api/orders
``` 