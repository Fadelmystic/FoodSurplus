# Instructions de démarrage du Frontend Surplus

## Prérequis

1. **Node.js** installé sur votre machine
2. **Keycloak** démarré sur `http://localhost:8180`
3. **Services backend** démarrés (optionnel pour tester l'authentification)

## Étapes de démarrage

### 1. Installer les dépendances du serveur proxy

```bash
cd frontend
npm install
```

### 2. Démarrer le serveur proxy

```bash
npm start
```

Le serveur proxy sera accessible sur `http://localhost:3001`

### 3. Accéder au frontend

Vous pouvez maintenant :

**Option A : Ouvrir directement les fichiers HTML**
- Double-cliquer sur `frontend/index.html`
- Ou ouvrir `file:///C:/Users/Zbook%20%20studio%20G8/Desktop/Surplus/frontend/index.html`

**Option B : Utiliser un serveur local simple**
```bash
# Dans un autre terminal, depuis le dossier frontend
python -m http.server 3000
# Puis aller sur http://localhost:3000
```

## Fonctionnalités disponibles

### Serveur Proxy (Port 3001)
- `POST /admin-token` - Obtenir le token admin Keycloak
- `POST /create-user` - Créer un utilisateur via Keycloak Admin API
- `POST /user-login` - Authentification utilisateur

### Frontend (Port 3000 ou fichier direct)
- Page d'inscription/connexion
- Dashboard vendeur
- Dashboard acheteur/ONG

## Test de l'inscription

1. Ouvrir `index.html`
2. Aller sur l'onglet "Inscription"
3. Remplir le formulaire avec :
   - Prénom : Test
   - Nom : User
   - Nom d'utilisateur : testuser
   - Email : test@example.com
   - Mot de passe : password123
   - Date de naissance : 1990-01-01
   - Ville : Paris
   - Rôle : SELLER (ou BUYER/NGO)

4. Cliquer sur "S'inscrire"
5. L'utilisateur sera créé dans Keycloak et vous serez redirigé vers le dashboard approprié

## Test de la connexion

1. Utiliser les identifiants créés lors de l'inscription
2. Se connecter via l'onglet "Connexion"
3. Vous serez redirigé vers le dashboard approprié selon votre rôle

## Dépannage

### Erreur "Cannot connect to proxy server"
- Vérifier que le serveur proxy est démarré : `npm start`
- Vérifier que le port 3001 n'est pas utilisé par un autre service

### Erreur "Keycloak connection failed"
- Vérifier que Keycloak est démarré sur `http://localhost:8180`
- Vérifier que le realm "surplus" existe
- Vérifier que le client "surplus-client" est configuré

### Erreur "User already exists"
- Changer le nom d'utilisateur dans le formulaire d'inscription
- Ou supprimer l'utilisateur existant dans Keycloak Admin Console

## Sécurité

⚠️ **Important** : Les credentials admin sont stockés dans le serveur proxy pour la démonstration. En production, utilisez des variables d'environnement :

```bash
export KEYCLOAK_ADMIN_USERNAME=admin-surplus
export KEYCLOAK_ADMIN_PASSWORD=ossama
export KEYCLOAK_CLIENT_SECRET=5ZKddttd05J6mG7G8SqtgfedsuUjAg5C
```

## Structure des URLs

- **Frontend principal** : `http://localhost:3000` ou fichier direct
- **Serveur proxy** : `http://localhost:3001`
- **Keycloak** : `http://localhost:8180`
- **Service utilisateur** : `http://localhost:8080`
- **Service listing** : `http://localhost:8081`
- **Service commande** : `http://localhost:8082` 