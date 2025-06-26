// Configuration des APIs
const LISTING_API_URL = 'http://localhost:3001/api/listings';
const ORDER_API_URL = 'http://localhost:3001/api/orders';
const USER_API_URL = 'http://localhost:3001';

// Variables globales
let currentUser = null;
let allListings = [];
let filteredListings = [];
let userOrders = [];
let selectedListing = null;

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    loadUserData();
    loadAllListings();
    loadUserOrders();
    setupEventListeners();
});

// Vérification de l'authentification
function checkAuthentication() {
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');
    
    if (!userId || (userRole !== 'BUYER' && userRole !== 'NGO')) {
        window.location.href = '../index.html';
        return;
    }
    
    currentUser = {
        id: userId,
        role: userRole,
        data: JSON.parse(localStorage.getItem('userData') || '{}')
    };
}

// Utilitaire pour obtenir le token
function getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    return token ? { 'Authorization': 'Bearer ' + token } : {};
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

// Chargement de tous les listings
async function loadAllListings() {
    try {
        const response = await fetch(`${LISTING_API_URL}/disponibles`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            allListings = await response.json();
            filteredListings = [...allListings];
            displayListings();
        } else {
            showNotification('Erreur lors du chargement des listings', 'error');
        }
    } catch (error) {
        console.error('Erreur lors du chargement des listings:', error);
        showNotification('Erreur de connexion au serveur', 'error');
    }

    // Vérification de l'URL d'appel API pour détecter un problème de cache
    if (window.location.href.includes('buyer-dashboard') && (
        typeof loadAllListings === 'function' && loadAllListings.toString().includes('3001')
    )) {
        console.error('ATTENTION : Le code JS utilise encore le port 3001 pour les listings ! Videz le cache du navigateur (Ctrl+F5) ou vérifiez le chargement du bon fichier JS.');
    }
}

// Affichage des listings
function displayListings() {
    const listingsGrid = document.getElementById('listingsGrid');
    
    if (filteredListings.length === 0) {
        listingsGrid.innerHTML = `
            <div class="empty-state">
                <h3>Aucun listing disponible</h3>
                <p>Aucun produit ne correspond à vos critères de recherche.</p>
            </div>
        `;
        return;
    }
    
    listingsGrid.innerHTML = filteredListings.map(listing => `
        <div class="listing-card">
            <div class="listing-header">
                <h3 class="listing-title">${listing.title}</h3>
                <span class="listing-category">${getCategoryDisplayName(listing.category)}</span>
            </div>
            <p class="listing-description">${listing.description}</p>
            <div class="listing-price">${listing.price}€</div>
            <div class="listing-details">
                <div class="listing-detail">
                    <strong>Quantité:</strong> ${listing.quantity}
                </div>
                <div class="listing-detail">
                    <strong>Localisation:</strong> ${listing.location}
                </div>
                <div class="listing-detail">
                    <strong>Expire le:</strong> ${formatDate(listing.expiryDate)}
                </div>
                <div class="listing-detail">
                    <strong>Vendeur:</strong> ${listing.sellerName || 'Anonyme'}
                </div>
            </div>
            <div class="listing-actions">
                <button class="btn-primary" onclick="openOrderModal('${listing.id}')">Commander</button>
            </div>
        </div>
    `).join('');
}

// Filtrage des listings
function filterListings() {
    const categoryFilter = document.getElementById('categoryFilter').value;
    const sortFilter = document.getElementById('sortFilter').value;
    const searchFilter = document.getElementById('searchFilter').value.toLowerCase();
    
    // Filtrage par catégorie et recherche
    filteredListings = allListings.filter(listing => {
        const matchesCategory = !categoryFilter || listing.category === categoryFilter;
        const matchesSearch = !searchFilter || 
            listing.title.toLowerCase().includes(searchFilter) ||
            listing.description.toLowerCase().includes(searchFilter);
        
        return matchesCategory && matchesSearch;
    });
    
    // Tri
    switch (sortFilter) {
        case 'price-asc':
            filteredListings.sort((a, b) => a.price - b.price);
            break;
        case 'price-desc':
            filteredListings.sort((a, b) => b.price - a.price);
            break;
        case 'expiry':
            filteredListings.sort((a, b) => new Date(a.expiryDate) - new Date(b.expiryDate));
            break;
        case 'date':
        default:
            filteredListings.sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate));
            break;
    }
    
    displayListings();
}

// Chargement des commandes de l'utilisateur
async function loadUserOrders() {
    try {
        const response = await fetch(`${ORDER_API_URL}/user/${currentUser.id}`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            userOrders = await response.json();
            displayOrders();
        } else {
            showNotification('Erreur lors du chargement des commandes', 'error');
        }
    } catch (error) {
        console.error('Erreur lors du chargement des commandes:', error);
        showNotification('Erreur de connexion au serveur', 'error');
    }
}

// Affichage des commandes
function displayOrders() {
    const ordersContainer = document.getElementById('ordersContainer');
    
    if (userOrders.length === 0) {
        ordersContainer.innerHTML = `
            <div class="empty-state">
                <h3>Aucune commande</h3>
                <p>Vous n'avez pas encore passé de commande.</p>
            </div>
        `;
        return;
    }
    
    ordersContainer.innerHTML = userOrders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <span class="order-id">Commande #${order.id}</span>
                <span class="order-status ${order.status.toLowerCase()}">${getOrderStatusDisplayName(order.status)}</span>
            </div>
            <div class="order-items">
                ${order.items.map(item => `
                    <div class="order-item">
                        <span>${item.listingTitle} x${item.quantity}</span>
                        <span>${item.price}€</span>
                    </div>
                `).join('')}
            </div>
            <div class="order-total">
                Total: ${order.totalAmount}€
            </div>
            <div class="order-details">
                <p><strong>Adresse:</strong> ${order.deliveryAddress}</p>
                <p><strong>Date:</strong> ${formatDate(order.createdDate)}</p>
                ${order.notes ? `<p><strong>Notes:</strong> ${order.notes}</p>` : ''}
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
    // Formulaire de profil
    document.getElementById('profileForm').addEventListener('submit', handleUpdateProfile);
    
    // Formulaire de commande
    document.getElementById('orderForm').addEventListener('submit', handlePlaceOrder);
    
    // Bouton de déconnexion
    document.getElementById('logoutBtn').addEventListener('click', logout);
    
    // Mise à jour du résumé de commande
    document.getElementById('orderQuantity').addEventListener('input', updateOrderSummary);
}

// Ouverture du modal de commande
async function openOrderModal(listingId) {
    selectedListing = allListings.find(listing => listing.id === listingId);
    
    if (!selectedListing) {
        showNotification('Listing non trouvé', 'error');
        return;
    }
    
    // Afficher les détails du listing
    document.getElementById('orderDetails').innerHTML = `
        <div class="listing-summary">
            <h4>${selectedListing.title}</h4>
            <p>${selectedListing.description}</p>
            <p><strong>Prix unitaire:</strong> ${selectedListing.price}€</p>
            <p><strong>Quantité disponible:</strong> ${selectedListing.quantity}</p>
        </div>
    `;
    
    // Réinitialiser le formulaire
    document.getElementById('orderForm').reset();
    document.getElementById('orderQuantity').max = selectedListing.quantity;
    document.getElementById('orderQuantity').value = 1;
    
    // Mettre à jour le résumé
    updateOrderSummary();
    
    // Afficher le modal
    document.getElementById('orderModal').classList.remove('hidden');
}

// Fermeture du modal de commande
function closeOrderModal() {
    document.getElementById('orderModal').classList.add('hidden');
    selectedListing = null;
}

// Mise à jour du résumé de commande
function updateOrderSummary() {
    if (!selectedListing) return;
    
    const quantity = parseInt(document.getElementById('orderQuantity').value) || 0;
    const total = quantity * selectedListing.price;
    
    document.getElementById('orderSummary').innerHTML = `
        <p><strong>Produit:</strong> ${selectedListing.title}</p>
        <p><strong>Quantité:</strong> ${quantity}</p>
        <p><strong>Prix unitaire:</strong> ${selectedListing.price}€</p>
        <p><strong>Total:</strong> ${total}€</p>
    `;
}

// Gestion de la commande
async function handlePlaceOrder(e) {
    e.preventDefault();
    
    if (!selectedListing) {
        showNotification('Aucun listing sélectionné', 'error');
        return;
    }
    
    const formData = new FormData(e.target);
    const quantity = parseInt(formData.get('quantity'));
    const address = formData.get('address');
    const notes = formData.get('notes');
    
    if (quantity > selectedListing.quantity) {
        showNotification('Quantité demandée supérieure à la disponibilité', 'error');
        return;
    }
    
    const orderData = {
        buyerId: currentUser.id,
        listingId: selectedListing.id,
        quantity: quantity,
        deliveryAddress: address,
        notes: notes,
        totalAmount: quantity * selectedListing.price
    };
    
    try {
        const response = await fetch(`${ORDER_API_URL}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(orderData)
        });
        
        if (response.ok) {
            showNotification('Commande passée avec succès !', 'success');
            closeOrderModal();
            loadUserOrders(); // Recharger les commandes
            loadAllListings(); // Recharger les listings (quantités mises à jour)
        } else {
            const errorData = await response.text();
            showNotification(`Erreur lors de la commande: ${errorData}`, 'error');
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
        const response = await fetch(`${USER_API_URL}/me`, {
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
        'FRUIT': 'Fruits',
        'LEGUME': 'Légumes',
        'BOISSON': 'Boissons',
        'VIANDE': 'Viandes',
        'POISSON': 'Poissons',
        'PRODUIT_LAITIER': 'Produits laitiers',
        'BOULANGERIE': 'Boulangerie',
        'AUTRE': 'Autre'
    };
    return categoryNames[category] || category;
}

function getOrderStatusDisplayName(status) {
    const statusNames = {
        'PENDING': 'En attente',
        'CONFIRMED': 'Confirmée',
        'DELIVERED': 'Livrée',
        'CANCELLED': 'Annulée'
    };
    return statusNames[status] || status;
}

function formatDate(dateString) {
    if (!dateString) return 'Non spécifiée';
    return new Date(dateString).toLocaleDateString('fr-FR');
} 