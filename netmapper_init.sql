-- ============================================================================
-- NETMAPPER - Script de réinitialisation des données avec UTF-8
-- ============================================================================
-- Projet: NetMapper
-- Version: 1.0
-- Date: Janvier 2026
-- Objectif: Supprimer et ré-insérer toutes les données avec encodage UTF-8
-- ============================================================================

USE netmapper;

-- Configuration UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_unicode_ci';

-- ============================================================================
-- ÉTAPE 1 : SUPPRESSION DES DONNÉES EXISTANTES
-- ============================================================================

-- Désactiver temporairement les contraintes de clés étrangères
SET FOREIGN_KEY_CHECKS = 0;

-- Vider toutes les tables
TRUNCATE TABLE alerte;
TRUNCATE TABLE ticket;
TRUNCATE TABLE connexion;
TRUNCATE TABLE equipement;
TRUNCATE TABLE salle;
TRUNCATE TABLE utilisateur;

-- Réactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- ÉTAPE 2 : INSERTION DES DONNÉES EN UTF-8
-- ============================================================================

-- ============================================================================
-- TABLE : SALLE
-- ============================================================================

INSERT INTO salle (nom, etage, plan_fichier, date_creation) VALUES
('Salle Serveurs', 'RDC', '/plans/salle_serveurs.png', NOW()),
('Bureau Technique', '1er étage', '/plans/bureau_tech.png', NOW()),
('Datacenter Principal', 'Sous-sol', '/plans/datacenter.png', NOW()),
('Salle Réseaux', 'RDC', '/plans/salle_reseaux.png', NOW()),
('Bureau Direction', '2e étage', '/plans/direction.png', NOW()),
('Open Space Dev', '1er étage', '/plans/openspace_dev.png', NOW()),
('Salle Réunion A', '1er étage', NULL, NOW()),
('Atelier Maintenance', 'RDC', NULL, NOW());

-- ============================================================================
-- TABLE : EQUIPEMENT
-- ============================================================================

INSERT INTO equipement (nom, type, adresse_ip, adresse_mac, fabricant, modele, etat, id_salle, date_ajout, date_maj) VALUES
-- Serveurs
('SRV-WEB-01', 'Serveur', '192.168.1.10', '00:1A:2B:3C:4D:01', 'Dell', 'PowerEdge R740', 'Actif', 1, NOW(), NOW()),
('SRV-BDD-01', 'Serveur', '192.168.1.11', '00:1A:2B:3C:4D:02', 'HP', 'ProLiant DL380', 'Actif', 1, NOW(), NOW()),
('SRV-FILE-01', 'Serveur', '192.168.1.12', '00:1A:2B:3C:4D:03', 'Lenovo', 'ThinkSystem SR650', 'En maintenance', 3, NOW(), NOW()),
('SRV-APP-01', 'Serveur', '192.168.1.13', '00:1A:2B:3C:4D:04', 'Dell', 'PowerEdge R640', 'Actif', 3, NOW(), NOW()),
('SRV-BACKUP-01', 'Serveur', '192.168.1.14', '00:1A:2B:3C:4D:05', 'HP', 'ProLiant DL360', 'Actif', 3, NOW(), NOW()),

-- Switches
('SW-CORE-01', 'Switch', '192.168.1.20', '00:1A:2B:3C:5D:01', 'Cisco', 'Catalyst 9300', 'Actif', 4, NOW(), NOW()),
('SW-ETG1-01', 'Switch', '192.168.1.21', '00:1A:2B:3C:5D:02', 'Cisco', 'Catalyst 2960', 'Actif', 2, NOW(), NOW()),
('SW-ETG2-01', 'Switch', '192.168.1.22', '00:1A:2B:3C:5D:03', 'Cisco', 'Catalyst 2960', 'Actif', 5, NOW(), NOW()),

-- Routeurs
('RTR-EDGE-01', 'Routeur', '192.168.1.1', '00:1A:2B:3C:6D:01', 'Cisco', 'ISR 4451', 'Actif', 4, NOW(), NOW()),
('RTR-EDGE-02', 'Routeur', '192.168.1.2', '00:1A:2B:3C:6D:02', 'Cisco', 'ISR 4331', 'Actif', 4, NOW(), NOW()),

-- Points d'accès WiFi
('AP-ETG1-01', 'Point d''accès', '192.168.1.30', '00:1A:2B:3C:7D:01', 'Ubiquiti', 'UniFi AP AC Pro', 'Actif', 6, NOW(), NOW()),
('AP-ETG1-02', 'Point d''accès', '192.168.1.31', '00:1A:2B:3C:7D:02', 'Ubiquiti', 'UniFi AP AC Pro', 'Actif', 2, NOW(), NOW()),
('AP-ETG2-01', 'Point d''accès', '192.168.1.32', '00:1A:2B:3C:7D:03', 'Ubiquiti', 'UniFi AP AC Pro', 'Actif', 5, NOW(), NOW()),
('AP-ETG2-02', 'Point d''accès', '192.168.1.33', '00:1A:2B:3C:7D:04', 'Ubiquiti', 'UniFi AP AC Pro', 'Hors service', 5, NOW(), NOW()),

-- Postes de travail
('PC-DEV-01', 'Poste de travail', '192.168.1.100', '00:1A:2B:3C:8D:01', 'Dell', 'OptiPlex 7090', 'Actif', 6, NOW(), NOW()),
('PC-DEV-02', 'Poste de travail', '192.168.1.101', '00:1A:2B:3C:8D:02', 'HP', 'EliteDesk 800', 'Actif', 6, NOW(), NOW()),
('PC-ADMIN-01', 'Poste de travail', '192.168.1.110', '00:1A:2B:3C:8D:03', 'Dell', 'Latitude 7420', 'Actif', 2, NOW(), NOW()),
('PC-DIR-01', 'Poste de travail', '192.168.1.120', '00:1A:2B:3C:8D:04', 'Apple', 'iMac 27"', 'Actif', 5, NOW(), NOW()),

-- Autres équipements
('IMP-OFFICE-01', 'Imprimante', '192.168.1.200', '00:1A:2B:3C:9D:01', 'HP', 'LaserJet Pro M404', 'Actif', 6, NOW(), NOW()),
('IMP-DIR-01', 'Imprimante', '192.168.1.201', '00:1A:2B:3C:9D:02', 'Canon', 'imageRUNNER C3025i', 'Actif', 5, NOW(), NOW()),
('CAM-ENTRY-01', 'Caméra IP', '192.168.1.210', '00:1A:2B:3C:AD:01', 'Hikvision', 'DS-2CD2143G0-I', 'Actif', 8, NOW(), NOW()),
('CAM-PARKING-01', 'Caméra IP', '192.168.1.211', '00:1A:2B:3C:AD:02', 'Hikvision', 'DS-2CD2143G0-I', 'Actif', 8, NOW(), NOW());

-- ============================================================================
-- TABLE : UTILISATEUR
-- ============================================================================

-- Mots de passe hashés avec BCrypt (tous = "password123")
INSERT INTO utilisateur (nom, prenom, role, login, mot_de_passe, date_creation) VALUES
('Admin', 'Système', 'Administrateur', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW()),
('GHERRAS', 'Salman', 'Administrateur', 'sgherras', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW()),
('CHERIFI', 'Sarah', 'Technicien', 'scherifi', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW()),
('ALI ASSOUMANE', 'Mtara', 'Technicien', 'maliassoumane', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW()),
('MARTIN', 'Sophie', 'Technicien', 'smartin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW()),
('DUBOIS', 'Pierre', 'Observateur', 'pdubois', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J7ESM8koB3GvKfJFCz3TQQz7g8p8EK', NOW());

-- ============================================================================
-- TABLE : ALERTE
-- ============================================================================

INSERT INTO alerte (type, date_alerte, niveau, message, id_equipement) VALUES
-- Alertes critiques
('Panne', NOW() - INTERVAL 1 HOUR, 'Critique', 'Serveur web injoignable depuis 15 minutes', 1),
('Connexion', NOW() - INTERVAL 30 MINUTE, 'Critique', 'Point d''accès AP-ETG2-02 complètement hors service. Utilisateurs du 2e étage Sud sans WiFi.', 14),
('Panne', NOW() - INTERVAL 2 HOUR, 'Critique', 'SRV-FILE-01 ne répond plus. Redémarrage effectué avec succès.', 3),

-- Alertes avertissement
('CPU', NOW() - INTERVAL 3 HOUR, 'Avertissement', 'Utilisation CPU à 85% sur SRV-BDD-01. Performance dégradée des requêtes.', 2),
('Mémoire', NOW() - INTERVAL 4 HOUR, 'Avertissement', 'Mémoire RAM à 90% sur SRV-APP-01', 4),
('Disque', NOW() - INTERVAL 5 HOUR, 'Avertissement', 'Espace disque faible (15% restant) sur SRV-BACKUP-01', 5),
('Connexion', NOW() - INTERVAL 6 HOUR, 'Avertissement', 'Latence élevée détectée sur SW-ETG1-01 (>100ms)', 7),

-- Alertes info
('Maintenance', NOW() - INTERVAL 2 DAY, 'Info', 'Mise en maintenance planifiée pour SRV-FILE-01', 3),
('Redémarrage', NOW() - INTERVAL 3 DAY, 'Info', 'Redémarrage programmé du routeur RTR-EDGE-01 effectué', 9),
('Mise à jour', NOW() - INTERVAL 4 DAY, 'Info', 'Mise à jour firmware appliquée sur tous les points d''accès WiFi', 11);

-- ============================================================================
-- TABLE : TICKET
-- ============================================================================

INSERT INTO ticket (date_ouverture, statut, gravite, description, id_equipement, id_utilisateur, date_fermeture) VALUES
-- Tickets ouverts
(NOW() - INTERVAL 2 HOUR, 'Ouvert', 'Critique', 'Point d''accès AP-ETG2-02 complètement hors service. Utilisateurs du 2e étage Sud sans WiFi.', 14, NULL, NULL),
(NOW() - INTERVAL 1 DAY, 'Ouvert', 'Élevée', 'Imprimante IMP-OFFICE-01 bourrage papier récurrent', 19, NULL, NULL),

-- Tickets en cours
(NOW() - INTERVAL 3 DAY, 'En cours', 'Élevée', 'SRV-FILE-01 performances très dégradées. Investigation en cours sur disques RAID.', 3, 3, NULL),
(NOW() - INTERVAL 2 DAY, 'En cours', 'Moyenne', 'PC-DEV-02 très lent au démarrage. Vérification disque dur nécessaire.', 16, 4, NULL),
(NOW() - INTERVAL 5 DAY, 'En cours', 'Faible', 'Caméra CAM-PARKING-01 image floue. Nettoyage optique prévu.', 22, 5, NULL),

-- Tickets fermés
(NOW() - INTERVAL 7 DAY, 'Fermé', 'Critique', 'Serveur SRV-WEB-01 injoignable. Problème résolu : câble réseau débranché accidentellement.', 1, 2, NOW() - INTERVAL 6 DAY),
(NOW() - INTERVAL 10 DAY, 'Fermé', 'Élevée', 'Switch SW-CORE-01 ports saturés. Ajout de ports supplémentaires effectué.', 6, 2, NOW() - INTERVAL 8 DAY),
(NOW() - INTERVAL 15 DAY, 'Fermé', 'Moyenne', 'Point d''accès AP-ETG1-01 redémarre aléatoirement. Firmware mis à jour.', 11, 3, NOW() - INTERVAL 14 DAY),
(NOW() - INTERVAL 20 DAY, 'Fermé', 'Faible', 'PC-ADMIN-01 mise à jour Windows bloquée. Problème résolu manuellement.', 17, 4, NOW() - INTERVAL 19 DAY);

-- ============================================================================
-- TABLE : CONNEXION
-- ============================================================================

INSERT INTO connexion (id_source, id_destination, type_connexion, debit, statut, date_creation) VALUES
-- Connexions réseau principales
(9, 6, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- RTR-EDGE-01 → SW-CORE-01
(10, 6, 'Ethernet', '10 Gbps', 'Actif', NOW()),   -- RTR-EDGE-02 → SW-CORE-01
(6, 7, 'Ethernet', '1 Gbps', 'Actif', NOW()),     -- SW-CORE-01 → SW-ETG1-01
(6, 8, 'Ethernet', '1 Gbps', 'Actif', NOW()),     -- SW-CORE-01 → SW-ETG2-01

-- Connexions serveurs
(6, 1, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- SW-CORE-01 → SRV-WEB-01
(6, 2, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- SW-CORE-01 → SRV-BDD-01
(6, 3, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- SW-CORE-01 → SRV-FILE-01
(6, 4, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- SW-CORE-01 → SRV-APP-01
(6, 5, 'Ethernet', '10 Gbps', 'Actif', NOW()),    -- SW-CORE-01 → SRV-BACKUP-01

-- Connexions WiFi
(7, 11, 'WiFi', '867 Mbps', 'Actif', NOW()),      -- SW-ETG1-01 → AP-ETG1-01
(7, 12, 'WiFi', '867 Mbps', 'Actif', NOW()),      -- SW-ETG1-01 → AP-ETG1-02
(8, 13, 'WiFi', '867 Mbps', 'Actif', NOW()),      -- SW-ETG2-01 → AP-ETG2-01
(8, 14, 'WiFi', '867 Mbps', 'Inactif', NOW()),    -- SW-ETG2-01 → AP-ETG2-02 (Hors service)

-- Connexions postes de travail
(7, 15, 'Ethernet', '1 Gbps', 'Actif', NOW()),    -- SW-ETG1-01 → PC-DEV-01
(7, 16, 'Ethernet', '1 Gbps', 'Actif', NOW()),    -- SW-ETG1-01 → PC-DEV-02
(7, 17, 'Ethernet', '1 Gbps', 'Actif', NOW()),    -- SW-ETG1-01 → PC-ADMIN-01
(8, 18, 'Ethernet', '1 Gbps', 'Actif', NOW()),    -- SW-ETG2-01 → PC-DIR-01

-- Connexions périphériques
(7, 19, 'Ethernet', '100 Mbps', 'Actif', NOW()),  -- SW-ETG1-01 → IMP-OFFICE-01
(8, 20, 'Ethernet', '100 Mbps', 'Actif', NOW()),  -- SW-ETG2-01 → IMP-DIR-01

-- Connexions caméras IP
(6, 21, 'Ethernet', '100 Mbps', 'Actif', NOW()),  -- SW-CORE-01 → CAM-ENTRY-01
(6, 22, 'Ethernet', '100 Mbps', 'Actif', NOW()),  -- SW-CORE-01 → CAM-PARKING-01

-- Redondance routeurs
(9, 10, 'Ethernet', '10 Gbps', 'Actif', NOW()),   -- RTR-EDGE-01 ↔ RTR-EDGE-02

-- ============================================================================
-- VÉRIFICATION DES DONNÉES INSÉRÉES
-- ============================================================================

SELECT 'Données insérées avec succès en UTF-8!' AS Statut;

-- Comptage des enregistrements
SELECT 
    (SELECT COUNT(*) FROM salle) AS Salles,
    (SELECT COUNT(*) FROM equipement) AS Equipements,
    (SELECT COUNT(*) FROM utilisateur) AS Utilisateurs,
    (SELECT COUNT(*) FROM alerte) AS Alertes,
    (SELECT COUNT(*) FROM ticket) AS Tickets,
    (SELECT COUNT(*) FROM connexion) AS Connexions;

-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================
