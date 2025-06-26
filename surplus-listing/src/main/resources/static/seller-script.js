// Configuration des APIs
const LISTING_API_URL = '/api/listings';
const USER_API_URL = '/api/users';

// Variables globales
let currentUser = null;
let userListings = [];

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();
    loadUserListings();
    setupEventListeners();
});

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
    event.target.classList.add('active');
}

// Notifications
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type} show`;
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 5000);
}

// Chargement des informations utilisateur
async function loadUserInfo() {
    try {
        const userId = localStorage.getItem('userId');
        if (!userId) {
            showNotification('Utilisateur non connecté', 'error');
            return;
        }

        const response = await fetch(`${USER_API_URL}/${userId}`);
        if (response.ok) {
            currentUser = await response.json();
            displayUserInfo();
        } else {
            showNotification('Erreur lors du chargement du profil', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showNotification('Erreur de connexion', 'error');
    }
}

// Affichage des informations utilisateur
function displayUserInfo() {
    if (!currentUser) return;

    const userNameElement = document.getElementById('userName');
    userNameElement.textContent = `${currentUser.firstname} ${currentUser.lastname}`;

    const profileInfo = document.getElementById('profileInfo');
    profileInfo.innerHTML = `
        <div class="profile-field">
            <strong>Prénom:</strong>
            <span>${currentUser.firstname}</span>
        </div>
        <div class="profile-field">
            <strong>Nom:</strong>
            <span>${currentUser.lastname}</span>
        </div>
        <div class="profile-field">
            <strong>Email:</strong>
            <span>${currentUser.email}</span>
        </div>
        <div class="profile-field">
            <strong>Ville:</strong>
            <span>${currentUser.city}</span>
        </div>
        <div class="profile-field">
            <strong>Rôle:</strong>
            <span>${currentUser.role}</span>
        </div>
        <div class="profile-field">
            <strong>Compte activé:</strong>
            <span>${currentUser.enabled ? 'Oui' : 'Non'}</span>
        </div>
    `;
}

// Chargement des listings de l'utilisateur
async function loadUserListings() {
    try {
        const userId = localStorage.getItem('userId');
        if (!userId) return;

        const response = await fetch(`${LISTING_API_URL}/seller/${userId}`);
        if (response.ok) {
            userListings = await response.json();
            displayUserListings();
        } else {
            showNotification('Erreur lors du chargement des listings', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showNotification('Erreur de connexion', 'error');
    }
}

// Affichage des listings de l'utilisateur
function displayUserListings() {
    const listingsGrid = document.getElementById('listingsGrid');
    
    if (userListings.length === 0) {
        listingsGrid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: #6c757d;">
                <p>Aucun listing trouvé. Créez votre premier listing !</p>
            </div>
        `;
        return;
    }

    listingsGrid.innerHTML = userListings.map(listing => `
        <div class="listing-card">
            <div class="listing-header">
                <h3 class="listing-title">${listing.title}</h3>
                <span class="listing-category">${listing.category}</span>
            </div>
            <p class="listing-description">${listing.description}</p>
            <div class="listing-details">
                <div class="listing-detail">
                    <strong>Prix:</strong> ${listing.price}€
                </div>
                <div class="listing-detail">
                    <strong>Quantité:</strong> ${listing.quantity}
                </div>
                <div class="listing-detail">
                    <strong>Localisation:</strong> ${listing.location}
                </div>
                <div class="listing-detail">
                    <strong>Expire le:</strong> ${new Date(listing.expiryDate).toLocaleDateString()}
                </div>
            </div>
            <div class="listing-actions">
                <button class="btn-secondary" onclick="editListing('${listing.id}')">Modifier</button>
                <button class="btn-danger" onclick="deleteListing('${listing.id}')">Supprimer</button>
            </div>
        </div>
    `).join('');
}

// Configuration des événements
function setupEventListeners() {
    const addListingForm = document.getElementById('addListingForm');
    const profileForm = document.getElementById('profileForm');
    const logoutBtn = document.getElementById('logoutBtn');

    addListingForm.addEventListener('submit', handleAddListing);
    profileForm.addEventListener('submit', handleUpdateProfile);
    logoutBtn.addEventListener('click', handleLogout);
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
        sellerId: localStorage.getItem('userId')
    };

    try {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Création en cours...';
        submitBtn.disabled = true;

        const response = await fetch(LISTING_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(listingData)
        });

        if (response.ok) {
            showNotification('Listing créé avec succès !', 'success');
            e.target.reset();
            showSection('listings');
            loadUserListings();
        } else {
            const errorData = await response.text();
            showNotification(`Erreur: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion', 'error');
        console.error('Erreur:', error);
    } finally {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// Modification d'un listing
function editListing(listingId) {
    const listing = userListings.find(l => l.id === listingId);
    if (!listing) return;

    document.getElementById('title').value = listing.title;
    document.getElementById('description').value = listing.description;
    document.getElementById('category').value = listing.category;
    document.getElementById('quantity').value = listing.quantity;
    document.getElementById('price').value = listing.price;
    document.getElementById('expiryDate').value = listing.expiryDate.split('T')[0];
    document.getElementById('location').value = listing.location;

    const submitBtn = document.querySelector('#addListingForm button[type="submit"]');
    submitBtn.textContent = 'Modifier le Listing';
    submitBtn.onclick = () => updateListing(listingId);

    showSection('add-listing');
}

// Mise à jour d'un listing
async function updateListing(listingId) {
    const formData = new FormData(document.getElementById('addListingForm'));
    const listingData = {
        title: formData.get('title'),
        description: formData.get('description'),
        category: formData.get('category'),
        quantity: parseInt(formData.get('quantity')),
        price: parseFloat(formData.get('price')),
        expiryDate: formData.get('expiryDate'),
        location: formData.get('location')
    };

    try {
        const response = await fetch(`${LISTING_API_URL}/${listingId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(listingData)
        });

        if (response.ok) {
            showNotification('Listing modifié avec succès !', 'success');
            showSection('listings');
            loadUserListings();
        } else {
            const errorData = await response.text();
            showNotification(`Erreur: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion', 'error');
        console.error('Erreur:', error);
    }
}

// Suppression d'un listing
async function deleteListing(listingId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce listing ?')) {
        return;
    }

    try {
        const response = await fetch(`${LISTING_API_URL}/${listingId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Listing supprimé avec succès !', 'success');
            loadUserListings();
        } else {
            const errorData = await response.text();
            showNotification(`Erreur: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion', 'error');
        console.error('Erreur:', error);
    }
}

// Gestion du profil
function editProfile() {
    if (!currentUser) return;

    document.getElementById('editFirstname').value = currentUser.firstname;
    document.getElementById('editLastname').value = currentUser.lastname;
    document.getElementById('editEmail').value = currentUser.email;
    document.getElementById('editCity').value = currentUser.city;

    document.getElementById('profileInfo').classList.add('hidden');
    document.getElementById('profileForm').classList.remove('hidden');
}

function cancelEditProfile() {
    document.getElementById('profileInfo').classList.remove('hidden');
    document.getElementById('profileForm').classList.add('hidden');
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const userData = {
        firstname: formData.get('firstname'),
        lastname: formData.get('lastname'),
        email: formData.get('email'),
        city: formData.get('city')
    };

    try {
        const userId = localStorage.getItem('userId');
        const response = await fetch(`${USER_API_URL}/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            showNotification('Profil mis à jour avec succès !', 'success');
            currentUser = { ...currentUser, ...userData };
            displayUserInfo();
            cancelEditProfile();
        } else {
            const errorData = await response.text();
            showNotification(`Erreur: ${errorData}`, 'error');
        }
    } catch (error) {
        showNotification('Erreur de connexion', 'error');
        console.error('Erreur:', error);
    }
}

// Déconnexion
function handleLogout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userToken');
    window.location.href = '/';
} 