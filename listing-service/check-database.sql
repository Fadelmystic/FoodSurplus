-- Vérifier la structure de la table
\d listings

-- Voir toutes les données de la table listings
SELECT * FROM listings;

-- Voir les informations détaillées d'un listing spécifique
SELECT 
    l.id,
    l.informations_nom as nom,
    l.informations_description as description,
    l.informations_image_url as image_url,
    l.stock_quantite as quantite,
    l.stock_disponible as disponible,
    l.metadata_date_creation as date_creation,
    l.metadata_date_modification as date_modification,
    l.supplier_id
FROM listings l; 