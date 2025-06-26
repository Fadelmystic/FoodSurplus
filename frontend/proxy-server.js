const express = require('express');
const cors = require('cors');
const axios = require('axios');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = 3001;

// Middleware
app.use(cors());
app.use(express.json());

// Credentials admin (normalement dans des variables d'environnement)
const ADMIN_CREDENTIALS = {
    username: 'admin',
    password: 'fadel',
    client_id: 'surplus-client',
    client_secret: 'VnCbRm4iJzc7HU1opi2RkSnxKakG0GkD',
    realm: 'surplus',
    keycloak_url: 'http://localhost:8180'
};

// Configuration des services backend
const BACKEND_SERVICES = {
    userManagement: 'http://localhost:8080',
    listingService: 'http://localhost:8081',
    orderService: 'http://localhost:8082'
};

// Endpoint pour obtenir le token admin
app.post('/admin-token', async (req, res) => {
    try {
        const tokenResponse = await axios.post(
            `${ADMIN_CREDENTIALS.keycloak_url}/realms/${ADMIN_CREDENTIALS.realm}/protocol/openid-connect/token`,
            new URLSearchParams({
                username: ADMIN_CREDENTIALS.username,
                password: ADMIN_CREDENTIALS.password,
                grant_type: 'password',
                client_id: ADMIN_CREDENTIALS.client_id,
                client_secret: ADMIN_CREDENTIALS.client_secret
            }),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }
        );

        res.json({
            access_token: tokenResponse.data.access_token,
            expires_in: tokenResponse.data.expires_in
        });
    } catch (error) {
        console.error('Erreur lors de l\'obtention du token admin:', error.response?.data || error.message);
        res.status(500).json({
            error: 'Erreur lors de l\'obtention du token admin',
            details: error.response?.data || error.message
        });
    }
});

// Endpoint pour créer un utilisateur avec le token admin
app.post('/create-user', async (req, res) => {
    try {
        // D'abord obtenir le token admin
        const tokenResponse = await axios.post(
            `${ADMIN_CREDENTIALS.keycloak_url}/realms/${ADMIN_CREDENTIALS.realm}/protocol/openid-connect/token`,
            new URLSearchParams({
                username: ADMIN_CREDENTIALS.username,
                password: ADMIN_CREDENTIALS.password,
                grant_type: 'password',
                client_id: ADMIN_CREDENTIALS.client_id,
                client_secret: ADMIN_CREDENTIALS.client_secret
            }),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }
        );

        const adminToken = tokenResponse.data.access_token;

        // Créer l'utilisateur dans Keycloak
        const userData = {
            username: req.body.username,
            email: req.body.email,
            firstName: req.body.firstname,
            lastName: req.body.lastname,
            enabled: true,
            credentials: [{
                type: 'password',
                value: req.body.password,
                temporary: false
            }],
            attributes: {
                city: [req.body.city],
                dateOfBirth: [req.body.dateOfBirth],
                role: [req.body.role]
            }
        };

        const createUserResponse = await axios.post(
            `${ADMIN_CREDENTIALS.keycloak_url}/admin/realms/${ADMIN_CREDENTIALS.realm}/users`,
            userData,
            {
                headers: {
                    'Authorization': `Bearer ${adminToken}`,
                    'Content-Type': 'application/json'
                }
            }
        );

        // Retourner les informations de l'utilisateur créé
        res.json({
            success: true,
            message: 'Utilisateur créé avec succès',
            userId: createUserResponse.headers.location?.split('/').pop()
        });

    } catch (error) {
        console.error('Erreur lors de la création de l\'utilisateur:', error.response?.data || error.message);
        res.status(500).json({
            error: 'Erreur lors de la création de l\'utilisateur',
            details: error.response?.data || error.message
        });
    }
});

// Endpoint pour l'authentification utilisateur - REDIRIGE VERS LE BACKEND
app.post('/user-login', async (req, res) => {
    try {
        const { username, password } = req.body;

        // Rediriger vers le backend user-management
        const response = await axios.post(
            `${BACKEND_SERVICES.userManagement}/api/users/login`,
            {
                username: username,
                password: password
            },
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );

        res.json(response.data);

    } catch (error) {
        console.error('Erreur lors de la connexion:', error.response?.data || error.message);
        res.status(401).json({
            error: 'Identifiants invalides',
            details: error.response?.data || error.message
        });
    }
});

// Proxy pour les listings (avec JWT)
app.use('/api/listings', (req, res, next) => {
    const token = req.headers['authorization'] || req.query.token || req.body.token || req.headers['x-access-token'];
    if (token) {
        req.headers['authorization'] = token;
    }
    next();
}, createProxyMiddleware({
    target: BACKEND_SERVICES.listingService,
    changeOrigin: true,
    pathRewrite: { '^/api/listings': '/api/listings' }
}));

// Proxy pour les users (avec JWT)
app.use('/api/users', (req, res, next) => {
    // Ne pas ajouter d'Authorization pour register et login
    if (
        req.path === '/register' ||
        req.path === '/login'
    ) {
        if (req.headers['authorization']) {
            console.log(`[Proxy] Suppression du header Authorization pour ${req.path}`);
            delete req.headers['authorization'];
        }
    } else {
        const token = req.headers['authorization'] || req.query.token || req.body.token || req.headers['x-access-token'];
        if (token) {
            req.headers['authorization'] = token;
            console.log(`[Proxy] Ajout du header Authorization pour ${req.path}`);
        }
    }
    // LOGS DEBUG
    console.log('[Proxy] Headers envoyés à /api/users:', req.headers);
    console.log('[Proxy] Body envoyé à /api/users:', req.body);
    console.log('[Proxy] URL cible:', req.originalUrl);
    next();
}, createProxyMiddleware({
    target: BACKEND_SERVICES.userManagement,
    changeOrigin: true
}));

// Proxy pour les orders (avec JWT)
app.use('/api/orders', (req, res, next) => {
    const token = req.headers['authorization'] || req.query.token || req.body.token || req.headers['x-access-token'];
    if (token) {
        req.headers['authorization'] = token;
    }
    next();
}, createProxyMiddleware({
    target: BACKEND_SERVICES.orderService,
    changeOrigin: true,
    pathRewrite: { '^/api/orders': '/api/orders' }
}));

app.get('/me', async (req, res) => {
    try {
        const token = req.headers['authorization'];
        if (!token) return res.status(401).json({ error: 'No token provided' });

        // Rediriger vers le backend user-management
        const response = await axios.get(
            `${BACKEND_SERVICES.userManagement}/api/users/me`,
            {
                headers: {
                    'Authorization': token
                }
            }
        );
        res.json(response.data);
    } catch (error) {
        res.status(401).json({ error: 'Invalid token', details: error.response?.data || error.message });
    }
});

app.listen(PORT, () => {
    console.log(`Serveur proxy démarré sur le port ${PORT}`);
    console.log(`URLs disponibles:`);
    console.log(`- POST /admin-token - Obtenir le token admin`);
    console.log(`- POST /create-user - Créer un utilisateur`);
    console.log(`- POST /user-login - Authentification utilisateur (via backend)`);
    console.log(`- GET /me - Informations utilisateur (via backend)`);
}); 