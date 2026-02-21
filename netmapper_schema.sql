-- ============================================================================
-- NETMAPPER - Schéma de Base de Données (Version Simplifiée)
-- ============================================================================
-- Version: 1.0 - Sans procédures stockées ni triggers
-- Note: La base 'netmapper' doit déjà exister
-- ============================================================================

USE netmapper;

-- Suppression des tables si elles existent (pour réinitialisation)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS alerte;
DROP TABLE IF EXISTS connexion;
DROP TABLE IF EXISTS equipement;
DROP TABLE IF EXISTS salle;
DROP TABLE IF EXISTS utilisateur;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- TABLE: Salle
-- Description: Localisation physique des équipements
-- ============================================================================
CREATE TABLE salle (
    id_salle INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    etage VARCHAR(20),
    plan_fichier VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_salle (nom, etage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Salles et zones géographiques du réseau';

CREATE INDEX idx_salle_nom ON salle(nom);

-- ============================================================================
-- TABLE: Equipement
-- Description: Dispositifs réseau supervisés
-- ============================================================================
CREATE TABLE equipement (
    id_equipement INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    adresse_ip VARCHAR(45) NOT NULL,
    adresse_mac VARCHAR(17),
    fabricant VARCHAR(100),
    modele VARCHAR(100),
    etat VARCHAR(20) DEFAULT 'Actif',
    id_salle INT,
    date_ajout TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_maj TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_equipement_salle 
        FOREIGN KEY (id_salle) 
        REFERENCES salle(id_salle) 
        ON DELETE SET NULL,
    
    UNIQUE KEY unique_ip (adresse_ip),
    CHECK (etat IN ('Actif', 'Inactif', 'En maintenance', 'Hors service'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Équipements réseau détectés et supervisés';

CREATE INDEX idx_equipement_type ON equipement(type);
CREATE INDEX idx_equipement_etat ON equipement(etat);
CREATE INDEX idx_equipement_ip ON equipement(adresse_ip);

-- ============================================================================
-- TABLE: Connexion
-- Description: Liens réseau entre équipements
-- ============================================================================
CREATE TABLE connexion (
    id_connexion INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    id_source INT NOT NULL,
    id_destination INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_connexion_source 
        FOREIGN KEY (id_source) 
        REFERENCES equipement(id_equipement) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_connexion_destination 
        FOREIGN KEY (id_destination) 
        REFERENCES equipement(id_equipement) 
        ON DELETE CASCADE,
    
    UNIQUE KEY unique_connexion (id_source, id_destination),
    CHECK (id_source != id_destination)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Connexions réseau entre équipements';

CREATE INDEX idx_connexion_source ON connexion(id_source);
CREATE INDEX idx_connexion_destination ON connexion(id_destination);

-- ============================================================================
-- TABLE: Alerte
-- Description: Notifications et événements système
-- ============================================================================
CREATE TABLE alerte (
    id_alerte INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    date_alerte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    niveau VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    id_equipement INT NOT NULL,
    
    CONSTRAINT fk_alerte_equipement 
        FOREIGN KEY (id_equipement) 
        REFERENCES equipement(id_equipement) 
        ON DELETE RESTRICT,
    
    CHECK (niveau IN ('Info', 'Avertissement', 'Critique', 'Erreur'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Alertes et événements de monitoring';

CREATE INDEX idx_alerte_niveau ON alerte(niveau);
CREATE INDEX idx_alerte_date ON alerte(date_alerte);
CREATE INDEX idx_alerte_equipement ON alerte(id_equipement);

-- ============================================================================
-- TABLE: Utilisateur
-- Description: Comptes utilisateurs du système
-- ============================================================================
CREATE TABLE utilisateur (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP NULL,
    
    CHECK (role IN ('Administrateur', 'Technicien', 'Manager', 'Observateur'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Utilisateurs et authentification';

CREATE INDEX idx_utilisateur_login ON utilisateur(login);
CREATE INDEX idx_utilisateur_role ON utilisateur(role);

-- ============================================================================
-- TABLE: Ticket
-- Description: Système de ticketing
-- ============================================================================
CREATE TABLE ticket (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    date_ouverture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'Ouvert',
    gravite VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    id_equipement INT,
    id_utilisateur INT,
    date_fermeture TIMESTAMP NULL,
    
    CONSTRAINT fk_ticket_equipement 
        FOREIGN KEY (id_equipement) 
        REFERENCES equipement(id_equipement) 
        ON DELETE SET NULL,
    
    CONSTRAINT fk_ticket_utilisateur 
        FOREIGN KEY (id_utilisateur) 
        REFERENCES utilisateur(id_utilisateur) 
        ON DELETE RESTRICT,
    
    CHECK (statut IN ('Ouvert', 'En cours', 'Résolu', 'Fermé', 'Annulé')),
    CHECK (gravite IN ('Faible', 'Moyenne', 'Haute', 'Critique'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tickets d''intervention et maintenance';

CREATE INDEX idx_ticket_statut ON ticket(statut);
CREATE INDEX idx_ticket_gravite ON ticket(gravite);
CREATE INDEX idx_ticket_equipement ON ticket(id_equipement);
CREATE INDEX idx_ticket_utilisateur ON ticket(id_utilisateur);
CREATE INDEX idx_ticket_date ON ticket(date_ouverture);

-- ============================================================================
-- VUES UTILES
-- ============================================================================

-- Vue: Équipements avec localisation
CREATE OR REPLACE VIEW v_equipements_localises AS
SELECT 
    e.id_equipement,
    e.nom,
    e.type,
    e.adresse_ip,
    e.etat,
    s.nom AS salle_nom,
    s.etage AS salle_etage
FROM equipement e
LEFT JOIN salle s ON e.id_salle = s.id_salle;

-- Vue: Alertes actives
CREATE OR REPLACE VIEW v_alertes_actives AS
SELECT 
    a.id_alerte,
    a.type,
    a.date_alerte,
    a.niveau,
    a.message,
    e.nom AS equipement_nom,
    e.adresse_ip
FROM alerte a
INNER JOIN equipement e ON a.id_equipement = e.id_equipement
WHERE a.date_alerte >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY a.date_alerte DESC;

-- Vue: Tickets ouverts
CREATE OR REPLACE VIEW v_tickets_ouverts AS
SELECT 
    t.id_ticket,
    t.date_ouverture,
    t.statut,
    t.gravite,
    t.description,
    e.nom AS equipement_nom,
    CONCAT(u.prenom, ' ', u.nom) AS technicien_assigne
FROM ticket t
LEFT JOIN equipement e ON t.id_equipement = e.id_equipement
LEFT JOIN utilisateur u ON t.id_utilisateur = u.id_utilisateur
WHERE t.statut IN ('Ouvert', 'En cours')
ORDER BY t.gravite DESC, t.date_ouverture ASC;

-- ============================================================================
-- VÉRIFICATIONS
-- ============================================================================

SHOW TABLES;

SELECT 'Base de données NetMapper créée avec succès!' AS statut;
SELECT '6 tables + 3 vues créées' AS info;

-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================