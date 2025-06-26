// Configuration des APIs
const LISTING_API_URL = 'http://localhost:3001/api/listings';
const USER_API_URL = 'http://localhost:3001';

// Variables globales
let currentUser = null;
let userListings = [];

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    loadUserData();
    loadUserListings();
    setupEventListeners();
});

// Vérification de l'authentification
function checkAuthentication() {
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');
    
    if (!userId || userRole !== 'SELLER') {
        window.location.href = '../index.html';
        return;
    }
    
    currentUser = {
        id: userId,
        role: userRole,
        data: JSON.parse(localStorage.getItem('userData') || '{}')
    };
}

// Chargement des données utilisateur
async function loadUserData() {
    try {
        const response = await fetch(`${USER_API_URL}/me`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const userData = await response.json();
            currentUser.data = userData;
            localStorage.setItem('userData', JSON.stringify(userData));
            displayUserInfo();
        }
    } catch (error) {
        console.error('Erreur lors du chargement des données utilisateur:', error);
        showNotification('Erreur lors du chargement du profil', 'error');
    }
}

// Affichage des informations utilisateur
function displayUserInfo() {
    const userNameElement = document.getElementById('userName');
    const profileInfoElement = document.getElementById('profileInfo');
    
    if (userNameElement && currentUser.data) {
        userNameElement.textContent = `${currentUser.data.firstname} ${currentUser.data.lastname}`;
    }
    
    if (profileInfoElement && currentUser.data) {
        profileInfoElement.innerHTML = `
            <div class="profile-field">
                <strong>Prénom:</strong>
                <span>${currentUser.data.firstname}</span>
            </div>
            <div class="profile-field">
                <strong>Nom:</strong>
                <span>${currentUser.data.lastname}</span>
            </div>
            <div class="profile-field">
                <strong>Nom d'utilisateur:</strong>
                <span>${currentUser.data.username}</span>
            </div>
            <div class="profile-field">
                <strong>Email:</strong>
                <span>${currentUser.data.email}</span>
            </div>
            <div class="profile-field">
                <strong>Ville:</strong>
                <span>${currentUser.data.city}</span>
            </div>
            <div class="profile-field">
                <strong>Rôle:</strong>
                <span>${getRoleDisplayName(currentUser.data.role)}</span>
            </div>
        `;
    }
}

// Utilitaire pour obtenir le token
function getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    return token ? { 'Authorization': 'Bearer ' + token } : {};
}

// Chargement des listings de l'utilisateur
async function loadUserListings() {
    try {
        const response = await fetch(`${LISTING_API_URL}/seller/${currentUser.id}`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            userListings = await response.json();
            displayListings();
        } else {
            showNotification('Erreur lors du chargement des listings', 'error');
        }
    } catch (error) {
        console.error('Erreur lors du chargement des listings:', error);
        showNotification('Erreur de connexion au serveur', 'error');
    }
}

// Affichage des listings
function displayListings() {
    const listingsGrid = document.getElementById('listingsGrid');
    
    if (userListings.length === 0) {
        listingsGrid.innerHTML = `
            <div class="empty-state">
                <h3>Aucun listing trouvé</h3>
                <p>Commencez par créer votre premier listing !</p>
                <button class="btn-primary" onclick="showSection('add-listing')">Créer un listing</button>
            </div>
        `;
        return;
    }
    
    listingsGrid.innerHTML = userListings.map(listing => `
        <div class="listing-card">
            <div class="listing-header">
                <h3 class="listing-title">${listing.title}</h3>
                <span class="listing-category">${getCategoryDisplayName(listing.category)}</span>
            </div>
            <p class="listing-description">${listing.description}</p>
            <div class="listing-details">
                <div class="listing-detail">
                    <strong>Quantité:</strong> ${listing.quantity}
                </div>
                <div class="listing-detail">
                    <strong>Prix:</strong> ${listing.price}€
                </div>
                <div class="listing-detail">
                    <strong>Localisation:</strong> ${listing.location}
                </div>
                <div class="listing-detail">
                    <strong>Expire le:</strong> ${formatDate(listing.expiryDate)}
                </div>
            </div>
            <div class="listing-actions">
                <button class="btn-secondary" onclick="editListing('${listing.id}')">Modifier</button>
                <button class="btn-danger" onclick="deleteListing('${listing.id}')">Supprimer</button>
            </div>
        </div>
    `).join('');
}

// Gestion des sections
function showSection(sectionId) {
    // Masquer toutes les sections
    const sections = document.querySelectorAll('.dashboard-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });
    
    // Désactiver tous les boutons de navigation
    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Afficher la section sélectionnée
    document.getElementById(sectionId).classList.add('active');
    
    // Activer le bouton correspondant
    const activeButton = document.querySelector(`[onclick="showSection('${sectionId}')"]`);
    if (activeButton) {
        activeButton.classList.add('active');
    }
}

// Configuration des événements
function setupEventListeners() {
    // Formulaire d'ajout de listing
    document.getElementById('addListingForm').addEventListener('submit', handleAddListing);
    
    // Formulaire de profil
    document.getElementById('profileForm').addEventListener('submit', handleUpdateProfile);
    
    // Bouton de déconnexion
    document.getElementById('logoutBtn').addEventListener('click', logout);
}

// Gestion de l'ajout de listing
async function handleAddListing(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const listingData = {
        title: formData.get('title'),
        description: formData.get('description'),
        category: formData.get('category'),
        quantity: parseInt(formData.get('quantity')),
        price: parseFloat(formData.get('price')),
        expiryDate: formData.get('expiryDate'),
        location: formData.get('location'),
        sellerId: currentUser.id,
        status: 'AVAILABLE'
    };
    
    try {
        const response = await fetch('http://localhost:3001/api/listings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(listingData)
        });
        
        if (response.ok) {
            showNotification('Listing créé avec succès !', 'success');
            e.target.reset();
            showSection('listings');
            loadUserListings(); // Recharger les listings
        } else {
            const errorData = await response.text();
            showNotification(`Erreur lors de la création: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion au serveur', 'error');
        console.error('Erreur:', error);
    }
}

// Gestion de la mise à jour du profil
async function handleUpdateProfile(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const updateData = {
        firstname: formData.get('firstname'),
        lastname: formData.get('lastname'),
        email: formData.get('email'),
        city: formData.get('city')
    };
    
    try {
        const response = await fetch(`http://localhost:3001/api/users/me`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(updateData)
        });
        
        if (response.ok) {
            showNotification('Profil mis à jour avec succès !', 'success');
            loadUserData(); // Recharger les données utilisateur
            cancelEditProfile();
        } else {
            const errorData = await response.text();
            showNotification(`Erreur lors de la mise à jour: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion au serveur', 'error');
        console.error('Erreur:', error);
    }
}

// Édition du profil
function editProfile() {
    const profileInfo = document.getElementById('profileInfo');
    const profileForm = document.getElementById('profileForm');
    
    // Remplir le formulaire avec les données actuelles
    document.getElementById('editFirstname').value = currentUser.data.firstname || '';
    document.getElementById('editLastname').value = currentUser.data.lastname || '';
    document.getElementById('editEmail').value = currentUser.data.email || '';
    document.getElementById('editCity').value = currentUser.data.city || '';
    
    // Afficher le formulaire
    profileInfo.classList.add('hidden');
    profileForm.classList.remove('hidden');
}

// Annuler l'édition du profil
function cancelEditProfile() {
    const profileInfo = document.getElementById('profileInfo');
    const profileForm = document.getElementById('profileForm');
    
    profileInfo.classList.remove('hidden');
    profileForm.classList.add('hidden');
}

// Suppression d'un listing
async function deleteListing(listingId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce listing ?')) {
        return;
    }
    
    try {
        const response = await fetch(`${LISTING_API_URL}/${listingId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        
        if (response.ok) {
            showNotification('Listing supprimé avec succès !', 'success');
            loadUserListings(); // Recharger les listings
        } else {
            showNotification('Erreur lors de la suppression', 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion au serveur', 'error');
        console.error('Erreur:', error);
    }
}

// Déconnexion
function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userData');
    window.location.href = '../index.html';
}

// Fonctions utilitaires
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 5000);
}

function getRoleDisplayName(role) {
    const roleNames = {
        'SELLER': 'Vendeur',
        'BUYER': 'Acheteur',
        'NGO': 'ONG',
        'ADMIN': 'Administrateur',
        'USER': 'Utilisateur'
    };
    return roleNames[role] || role;
}

function getCategoryDisplayName(category) {
    const categoryNames = {
        'FOOD': 'Nourriture',
        'CLOTHING': 'Vêtements',
        'ELECTRONICS': 'Électronique',
        'BOOKS': 'Livres',
        'OTHER': 'Autre'
    };
    return categoryNames[category] || category;
}

function formatDate(dateString) {
    if (!dateString) return 'Non spécifiée';
    return new Date(dateString).toLocaleDateString('fr-FR');
} 