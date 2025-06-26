// Configuration de l'API
const LISTING_API_URL = 'http://localhost:8081/api/listings';
const USER_API_URL = 'http://localhost:3001';

// Gestion des onglets
function showTab(tabName) {
    // Masquer tous les contenus d'onglets
    const tabContents = document.querySelectorAll('.tab-content');
    tabContents.forEach(content => {
        content.classList.remove('active');
    });

    // Désactiver tous les boutons d'onglets
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.classList.remove('active');
    });

    // Afficher l'onglet sélectionné
    document.getElementById(tabName).classList.add('active');
    
    // Activer le bouton correspondant
    event.target.classList.add('active');
}

// Gestion des notifications
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    // Masquer la notification après 5 secondes
    setTimeout(() => {
        notification.classList.remove('show');
    }, 5000);
}

// Validation des formulaires
function validateForm(formData) {
    const errors = [];
    
    // Validation de l'email
    const email = formData.get('email');
    if (email && !isValidEmail(email)) {
        errors.push('Format d\'email invalide');
    }
    
    // Validation du mot de passe
    const password = formData.get('password');
    if (password && password.length < 6) {
        errors.push('Le mot de passe doit contenir au moins 6 caractères');
    }
    
    // Validation de la date de naissance
    const dateOfBirth = formData.get('dateOfBirth');
    if (dateOfBirth) {
        const birthDate = new Date(dateOfBirth);
        const today = new Date();
        const age = today.getFullYear() - birthDate.getFullYear();
        if (age < 13) {
            errors.push('Vous devez avoir au moins 13 ans');
        }
    }
    
    return errors;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Redirection selon le rôle
function redirectBasedOnRole(userData) {
    const role = userData.role || userData.attributes?.role?.[0];
    const userId = userData.sub || userData.id;
    
    // Sauvegarder les informations utilisateur
    localStorage.setItem('userId', userId);
    localStorage.setItem('userRole', role);
    localStorage.setItem('userData', JSON.stringify(userData));
    
    // Redirection selon le rôle
    switch (role) {
        case 'SELLER':
            window.location.href = 'pages/seller-dashboard.html';
            break;
        case 'BUYER':
        case 'NGO':
            window.location.href = 'pages/buyer-dashboard.html';
            break;
        default:
            showNotification('Rôle non reconnu', 'error');
            break;
    }
}

// Gestion du formulaire d'inscription
document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    
    // Validation
    const errors = validateForm(formData);
    if (errors.length > 0) {
        showNotification(errors.join(', '), 'error');
        return;
    }
    
    // Préparation des données pour le backend
    const userData = {
        username: formData.get('username'),
        password: formData.get('password'),
        email: formData.get('email'),
        firstname: formData.get('firstname'),
        lastname: formData.get('lastname'),
        dateOfBirth: formData.get('dateOfBirth'),
        city: formData.get('city'),
        role: formData.get('role')
    };
    console.log('Données envoyées à l\'API:', userData); // LOG DEBUG
    let originalText;
    const submitBtn = this.querySelector('button[type="submit"]');
    try {
        // Ajout de l'état de chargement
        originalText = submitBtn.textContent;
        submitBtn.textContent = 'Inscription en cours...';
        submitBtn.disabled = true;
        
        // Utiliser l'endpoint du backend pour l'inscription
        const response = await fetch(`${USER_API_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        console.log('Réponse brute de l\'API:', response); // LOG DEBUG
        
        if (response.ok) {
            const result = await response.json();
            showNotification('Inscription réussie ! Redirection en cours...', 'success');
            
            // Créer un objet utilisateur pour la redirection
            const userInfo = {
                id: result.userId || 'temp-id',
                role: userData.role,
                firstname: userData.firstname,
                lastname: userData.lastname,
                email: userData.email,
                city: userData.city,
                username: userData.username
            };
            
            // Redirection selon le rôle après un délai
            setTimeout(() => {
                redirectBasedOnRole(userInfo);
            }, 1500);
        } else {
            let errorMsg = 'Erreur inconnue';
            try {
                const errorData = await response.json();
                errorMsg = errorData.error || errorData.details || JSON.stringify(errorData);
            } catch (e) {
                errorMsg = await response.text();
            }
            showNotification(`Erreur d'inscription: ${errorMsg}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion au serveur', 'error');
        console.error('Erreur:', error);
    } finally {
        if (submitBtn && originalText) submitBtn.textContent = originalText;
        if (submitBtn) submitBtn.disabled = false;
    }
});

// Gestion du formulaire de connexion
document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const username = formData.get('username');
    const password = formData.get('password');
    
    if (!username || !password) {
        showNotification('Veuillez remplir tous les champs', 'error');
        return;
    }
    let originalText;
    const submitBtn = this.querySelector('button[type="submit"]');
    try {
        // Ajout de l'état de chargement
        originalText = submitBtn.textContent;
        submitBtn.textContent = 'Connexion en cours...';
        submitBtn.disabled = true;
        
        // Utiliser le serveur proxy pour l'authentification
        const response = await fetch(`${USER_API_URL}/user-login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        
        if (response.ok) {
            const result = await response.json();
            console.log('Résultat login:', result);
            showNotification('Connexion réussie ! Redirection en cours...', 'success');
            
            // Sauvegarder le token et les informations utilisateur
            localStorage.setItem('accessToken', result.access_token);
            localStorage.setItem('userId', result.user_id);
            localStorage.setItem('userRole', result.role);
            
            // Créer un objet utilisateur pour la redirection
            const userInfo = {
                id: result.user_id,
                role: result.role,
                firstname: result.firstname,
                lastname: result.lastname,
                email: result.email,
                username: result.username
            };
            
            // Sauvegarder les données utilisateur
            localStorage.setItem('userData', JSON.stringify(userInfo));
            
            // Redirection selon le rôle
            setTimeout(() => {
                redirectBasedOnRole(userInfo);
            }, 1500);
        } else {
            const errorData = await response.json();
            showNotification(`Erreur de connexion: ${errorData.error || 'Identifiants invalides'}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion au serveur', 'error');
        console.error('Erreur:', error);
    } finally {
        if (submitBtn && originalText) submitBtn.textContent = originalText;
        if (submitBtn) submitBtn.disabled = false;
    }
});

// Validation en temps réel
document.addEventListener('DOMContentLoaded', function() {
    // Validation de l'email en temps réel
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            if (this.value && !isValidEmail(this.value)) {
                this.style.borderColor = '#dc3545';
                showNotification('Format d\'email invalide', 'error');
            } else {
                this.style.borderColor = '#e9ecef';
            }
        });
    }
    
    // Validation du mot de passe en temps réel
    const passwordInput = document.getElementById('password');
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            if (this.value.length > 0 && this.value.length < 6) {
                this.style.borderColor = '#dc3545';
            } else {
                this.style.borderColor = '#e9ecef';
            }
        });
    }
    
    // Validation de la date de naissance
    const dateOfBirthInput = document.getElementById('dateOfBirth');
    if (dateOfBirthInput) {
        dateOfBirthInput.addEventListener('change', function() {
            if (this.value) {
                const birthDate = new Date(this.value);
                const today = new Date();
                const age = today.getFullYear() - birthDate.getFullYear();
                if (age < 13) {
                    this.style.borderColor = '#dc3545';
                    showNotification('Vous devez avoir au moins 13 ans', 'error');
                } else {
                    this.style.borderColor = '#e9ecef';
                }
            }
        });
    }
});

// Gestion des erreurs réseau
window.addEventListener('online', function() {
    showNotification('Connexion rétablie', 'success');
});

window.addEventListener('offline', function() {
    showNotification('Connexion perdue. Vérifiez votre connexion internet.', 'error');
});

// Utilitaire pour obtenir le token
function getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    return token ? { 'Authorization': 'Bearer ' + token } : {};
} 