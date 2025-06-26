// Configuration des APIs
const LISTING_API_URL = '/api/listings';
const ORDER_API_URL = '/api/orders';
const USER_API_URL = '/api/users';

// Variables globales
let currentUser = null;
let allListings = [];
let userOrders = [];
let currentOrderItem = null;

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();
    loadAllListings();
    loadUserOrders();
    setupEventListeners();
});

// Gestion des sections
function showSection(sectionId) {
    const sections = document.querySelectorAll('.dashboard-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(btn => {
        btn.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');
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

// Chargement de tous les listings
async function loadAllListings() {
    try {
        const response = await fetch(LISTING_API_URL);
        if (response.ok) {
            allListings = await response.json();
            displayListings(allListings);
        } else {
            showNotification('Erreur lors du chargement des listings', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showNotification('Erreur de connexion', 'error');
    }
}

// Affichage des listings
function displayListings(listings) {
    const listingsGrid = document.getElementById('listingsGrid');
    
    if (listings.length === 0) {
        listingsGrid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: #6c757d;">
                <p>Aucun listing disponible pour le moment.</p>
            </div>
        `;
        return;
    }

    listingsGrid.innerHTML = listings.map(listing => `
        <div class="listing-card">
            <div class="listing-expiry">Expire le ${new Date(listing.expiryDate).toLocaleDateString()}</div>
            <div class="listing-header">
                <h3 class="listing-title">${listing.title}</h3>
                <span class="listing-category">${listing.category}</span>
            </div>
            <div class="listing-seller">Vendeur: ${listing.sellerName || 'Anonyme'}</div>
            <p class="listing-description">${listing.description}</p>
            <div class="listing-details">
                <div class="listing-detail">
                    <strong>Quantité:</strong> ${listing.quantity}
                </div>
                <div class="listing-detail">
                    <strong>Localisation:</strong> ${listing.location}
                </div>
            </div>
            <div class="listing-price">${listing.price}€</div>
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

    let filteredListings = allListings.filter(listing => {
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

    displayListings(filteredListings);
}

// Chargement des commandes de l'utilisateur
async function loadUserOrders() {
    try {
        const userId = localStorage.getItem('userId');
        if (!userId) return;

        const response = await fetch(`${ORDER_API_URL}/buyer/${userId}`);
        if (response.ok) {
            userOrders = await response.json();
            displayUserOrders();
        } else {
            showNotification('Erreur lors du chargement des commandes', 'error');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showNotification('Erreur de connexion', 'error');
    }
}

// Affichage des commandes de l'utilisateur
function displayUserOrders() {
    const ordersList = document.getElementById('ordersList');
    
    if (userOrders.length === 0) {
        ordersList.innerHTML = `
            <div style="text-align: center; padding: 2rem; color: #6c757d;">
                <p>Aucune commande trouvée.</p>
            </div>
        `;
        return;
    }

    ordersList.innerHTML = userOrders.map(order => `
        <div class="order-card">
            <div class="order-header">
                <span class="order-id">Commande #${order.id}</span>
                <span class="order-status ${order.status.toLowerCase()}">${getStatusText(order.status)}</span>
            </div>
            <div class="order-details">
                <div class="order-item">
                    <strong>Produit:</strong> ${order.listingTitle}
                </div>
                <div class="order-item">
                    <strong>Quantité:</strong> ${order.quantity}
                </div>
                <div class="order-item">
                    <strong>Prix total:</strong> ${order.totalPrice}€
                </div>
                <div class="order-item">
                    <strong>Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}
                </div>
            </div>
            <div class="order-address">
                <strong>Adresse de livraison:</strong><br>
                ${order.deliveryAddress}
            </div>
            ${order.notes ? `<div class="order-notes"><strong>Notes:</strong> ${order.notes}</div>` : ''}
        </div>
    `).join('');
}

// Obtenir le texte du statut
function getStatusText(status) {
    const statusMap = {
        'PENDING': 'En attente',
        'CONFIRMED': 'Confirmée',
        'SHIPPED': 'Expédiée',
        'DELIVERED': 'Livrée',
        'CANCELLED': 'Annulée'
    };
    return statusMap[status] || status;
}

// Configuration des événements
function setupEventListeners() {
    const profileForm = document.getElementById('profileForm');
    const logoutBtn = document.getElementById('logoutBtn');
    const orderForm = document.getElementById('orderForm');
    const orderQuantity = document.getElementById('orderQuantity');

    profileForm.addEventListener('submit', handleUpdateProfile);
    logoutBtn.addEventListener('click', handleLogout);
    orderForm.addEventListener('submit', handleCreateOrder);
    orderQuantity.addEventListener('input', updateOrderSummary);
}

// Gestion du modal de commande
function openOrderModal(listingId) {
    const listing = allListings.find(l => l.id === listingId);
    if (!listing) return;

    currentOrderItem = listing;
    
    document.getElementById('orderItemInfo').innerHTML = `
        <div style="background: #f8f9fa; padding: 1rem; border-radius: 5px; margin-bottom: 1rem;">
            <h4>${listing.title}</h4>
            <p>${listing.description}</p>
            <p><strong>Prix unitaire:</strong> ${listing.price}€</p>
            <p><strong>Quantité disponible:</strong> ${listing.quantity}</p>
        </div>
    `;
    
    document.getElementById('orderQuantity').max = listing.quantity;
    document.getElementById('orderQuantity').value = 1;
    document.getElementById('orderAddress').value = '';
    document.getElementById('orderNotes').value = '';
    
    updateOrderSummary();
    document.getElementById('orderModal').classList.remove('hidden');
}

function closeOrderModal() {
    document.getElementById('orderModal').classList.add('hidden');
    currentOrderItem = null;
}

function updateOrderSummary() {
    if (!currentOrderItem) return;
    
    const quantity = parseInt(document.getElementById('orderQuantity').value) || 0;
    const unitPrice = currentOrderItem.price;
    const totalPrice = quantity * unitPrice;
    
    document.getElementById('unitPrice').textContent = `${unitPrice}€`;
    document.getElementById('totalQuantity').textContent = quantity;
    document.getElementById('totalPrice').textContent = `${totalPrice}€`;
}

// Gestion de la création de commande
async function handleCreateOrder(e) {
    e.preventDefault();
    
    if (!currentOrderItem) return;
    
    const formData = new FormData(e.target);
    const orderData = {
        listingId: currentOrderItem.id,
        buyerId: localStorage.getItem('userId'),
        quantity: parseInt(formData.get('quantity')),
        deliveryAddress: formData.get('address'),
        notes: formData.get('notes'),
        totalPrice: parseInt(formData.get('quantity')) * currentOrderItem.price
    };

    try {
        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Création en cours...';
        submitBtn.disabled = true;

        const response = await fetch(ORDER_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(orderData)
        });

        if (response.ok) {
            showNotification('Commande créée avec succès !', 'success');
            closeOrderModal();
            loadUserOrders();
            loadAllListings();
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