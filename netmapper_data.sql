-- ============================================================================
-- NETMAPPER - Données de Test et Démonstration
-- ============================================================================
-- Projet: NetMapper
-- Description: Données réalistes pour tests et démonstrations
-- Version: 1.0
-- Date: Janvier 2026
-- ============================================================================

USE netmapper;

-- ============================================================================
-- NETTOYAGE DES DONNÉES EXISTANTES (pour réinitialisation)
-- ============================================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE ticket;
TRUNCATE TABLE alerte;
TRUNCATE TABLE connexion;
TRUNCATE TABLE equipement;
TRUNCATE TABLE salle;
TRUNCATE TABLE utilisateur;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- UTILISATEURS
-- ============================================================================

-- Note: Les mots de passe doivent être hashés avec BCrypt en production
-- Format: $2a$10$[hash] (sera implémenté dans le backend Spring Boot)
-- Mot de passe par défaut pour tous: "password123"

INSERT INTO utilisateur (nom, prenom, role, login, mot_de_passe) VALUES
-- Administrateur système
('ADMIN', 'Système', 'Administrateur', 'admin', 'HASH_TO_BE_GENERATED'),

-- Équipe du projet
('GHERRAS', 'Salman', 'Technicien', 'sgherras', 'HASH_TO_BE_GENERATED'),
('CHERIFI', 'Sarah', 'Technicien', 'scherifi', 'HASH_TO_BE_GENERATED'),
('ALI ASSOUMANE', 'Mtara', 'Technicien', 'maliassoumane', 'HASH_TO_BE_GENERATED'),

-- Autres utilisateurs de test
('JACQUENET', 'François', 'Manager', 'fjacquenet', 'HASH_TO_BE_GENERATED'),
('DUPONT', 'Marie', 'Observateur', 'mdupont', 'HASH_TO_BE_GENERATED');

-- ============================================================================
-- SALLES
-- ============================================================================

INSERT INTO salle (nom, etage, plan_fichier) VALUES
-- Bâtiment principal
('Salle Serveurs', 'RDC', '/plans/salle_serveurs.png'),
('Bureau Technique', '1er étage', '/plans/bureau_technique.png'),
('Salle Réseau Principale', 'Sous-sol', '/plans/salle_reseau.png'),
('Open Space Nord', '2e étage', NULL),
('Open Space Sud', '2e étage', NULL),
('Salle de Réunion A', '1er étage', NULL),

-- Bâtiment annexe
('Datacenter Principal', 'RDC Annexe', '/plans/datacenter.png'),
('Local Technique Annexe', 'RDC Annexe', NULL);

-- ============================================================================
-- ÉQUIPEMENTS RÉSEAU
-- ============================================================================

-- Serveurs (Data Center)
INSERT INTO equipement (nom, type, adresse_ip, adresse_mac, fabricant, modele, etat, id_salle) VALUES
('SRV-WEB-01', 'Serveur', '192.168.1.10', '00:1A:2B:3C:4D:01', 'Dell', 'PowerEdge R740', 'Actif', 7),
('SRV-DB-01', 'Serveur', '192.168.1.11', '00:1A:2B:3C:4D:02', 'HP', 'ProLiant DL380 Gen10', 'Actif', 7),
('SRV-APP-01', 'Serveur', '192.168.1.12', '00:1A:2B:3C:4D:03', 'Dell', 'PowerEdge R640', 'Actif', 7),
('SRV-BACKUP-01', 'Serveur', '192.168.1.13', '00:1A:2B:3C:4D:04', 'Lenovo', 'ThinkSystem SR650', 'Actif', 7),
('SRV-FILE-01', 'Serveur', '192.168.1.14', '00:1A:2B:3C:4D:05', 'Dell', 'PowerEdge R740xd', 'En maintenance', 1),

-- Switches Core (Salle Réseau)
('SWITCH-CORE-01', 'Switch', '192.168.1.2', '00:1A:2B:3C:5D:01', 'Cisco', 'Catalyst 9300', 'Actif', 3),
('SWITCH-CORE-02', 'Switch', '192.168.1.3', '00:1A:2B:3C:5D:02', 'Cisco', 'Catalyst 9300', 'Actif', 3),

-- Switches Distribution
('SWITCH-ETG1-01', 'Switch', '192.168.1.20', '00:1A:2B:3C:5D:10', 'Cisco', 'Catalyst 2960', 'Actif', 2),
('SWITCH-ETG2-01', 'Switch', '192.168.1.21', '00:1A:2B:3C:5D:11', 'Cisco', 'Catalyst 2960', 'Actif', 4),
('SWITCH-ETG2-02', 'Switch', '192.168.1.22', '00:1A:2B:3C:5D:12', 'Cisco', 'Catalyst 2960', 'Actif', 5),

-- Routeurs
('RTR-MAIN-01', 'Routeur', '192.168.1.1', '00:1A:2B:3C:6D:01', 'Cisco', 'ISR 4451', 'Actif', 3),
('RTR-BACKUP-01', 'Routeur', '192.168.1.254', '00:1A:2B:3C:6D:02', 'Cisco', 'ISR 4331', 'Inactif', 3),

-- Points d'accès WiFi
('AP-ETG1-01', 'Point d''accès', '192.168.1.100', '00:1A:2B:3C:7D:01', 'Ubiquiti', 'UniFi AP AC Pro', 'Actif', 2),
('AP-ETG2-01', 'Point d''accès', '192.168.1.101', '00:1A:2B:3C:7D:02', 'Ubiquiti', 'UniFi AP AC Pro', 'Actif', 4),
('AP-ETG2-02', 'Point d''accès', '192.168.1.102', '00:1A:2B:3C:7D:03', 'Ubiquiti', 'UniFi AP AC Pro', 'Hors service', 5),

-- Postes de travail
('PC-TECH-01', 'Poste de travail', '192.168.1.50', '00:1A:2B:3C:8D:01', 'Dell', 'OptiPlex 7090', 'Actif', 2),
('PC-TECH-02', 'Poste de travail', '192.168.1.51', '00:1A:2B:3C:8D:02', 'HP', 'EliteDesk 800 G6', 'Actif', 2),
('PC-ADMIN-01', 'Poste de travail', '192.168.1.52', '00:1A:2B:3C:8D:03', 'Lenovo', 'ThinkCentre M90t', 'Actif', 6),

-- Imprimantes réseau
('IMP-ETG1-01', 'Imprimante', '192.168.1.150', '00:1A:2B:3C:9D:01', 'HP', 'LaserJet Pro M404dn', 'Actif', 2),
('IMP-ETG2-01', 'Imprimante', '192.168.1.151', '00:1A:2B:3C:9D:02', 'Canon', 'imageRUNNER C3025i', 'Actif', 4),

-- Équipements de surveillance
('CAM-ENTREE-01', 'Caméra IP', '192.168.1.200', '00:1A:2B:3C:AD:01', 'Axis', 'P3245-V', 'Actif', NULL),
('NVR-01', 'NVR', '192.168.1.201', '00:1A:2B:3C:AD:02', 'Synology', 'NVR1218', 'Actif', 8);

-- ============================================================================
-- CONNEXIONS RÉSEAU (Topologie)
-- ============================================================================

-- Connexions routeur principal
INSERT INTO connexion (type, id_source, id_destination) VALUES
-- Routeur vers switches core
('Fiber', 11, 6),   -- RTR-MAIN-01 → SWITCH-CORE-01
('Fiber', 11, 7),   -- RTR-MAIN-01 → SWITCH-CORE-02

-- Switches core vers serveurs
('Fiber', 6, 1),    -- SWITCH-CORE-01 → SRV-WEB-01
('Fiber', 6, 2),    -- SWITCH-CORE-01 → SRV-DB-01
('Fiber', 6, 3),    -- SWITCH-CORE-01 → SRV-APP-01
('Fiber', 7, 4),    -- SWITCH-CORE-02 → SRV-BACKUP-01
('Fiber', 7, 5),    -- SWITCH-CORE-02 → SRV-FILE-01

-- Switches core vers switches distribution
('Fiber', 6, 8),    -- SWITCH-CORE-01 → SWITCH-ETG1-01
('Fiber', 7, 9),    -- SWITCH-CORE-02 → SWITCH-ETG2-01
('Fiber', 7, 10),   -- SWITCH-CORE-02 → SWITCH-ETG2-02

-- Switches distribution vers postes et équipements
('Ethernet', 8, 16), -- SWITCH-ETG1-01 → PC-TECH-01
('Ethernet', 8, 17), -- SWITCH-ETG1-01 → PC-TECH-02
('Ethernet', 8, 19), -- SWITCH-ETG1-01 → IMP-ETG1-01
('Ethernet', 9, 20), -- SWITCH-ETG2-01 → IMP-ETG2-01
('Ethernet', 10, 18), -- SWITCH-ETG2-02 → PC-ADMIN-01

-- Points d'accès
('Ethernet', 8, 13), -- SWITCH-ETG1-01 → AP-ETG1-01
('Ethernet', 9, 14), -- SWITCH-ETG2-01 → AP-ETG2-01
('Ethernet', 10, 15), -- SWITCH-ETG2-02 → AP-ETG2-02

-- Équipements de surveillance
('Ethernet', 6, 21), -- SWITCH-CORE-01 → CAM-ENTREE-01
('Ethernet', 6, 22); -- SWITCH-CORE-01 → NVR-01

-- ============================================================================
-- ALERTES (Historique récent)
-- ============================================================================

INSERT INTO alerte (type, date_alerte, niveau, message, id_equipement) VALUES
-- Alertes critiques
('Panne', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'Critique', 'Équipement hors ligne - Aucune réponse ping', 15),
('CPU', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'Critique', 'Utilisation CPU à 98% pendant 15 minutes', 1),

-- Alertes d'avertissement
('Connexion', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'Avertissement', 'Latence élevée détectée (>100ms)', 11),
('Mémoire', DATE_SUB(NOW(), INTERVAL 5 HOUR), 'Avertissement', 'Utilisation mémoire à 85%', 2),
('Disque', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'Avertissement', 'Espace disque restant < 15%', 4),

-- Alertes informatives
('Découverte', DATE_SUB(NOW(), INTERVAL 12 HOUR), 'Info', 'Nouvel équipement détecté sur le réseau', 22),
('Maintenance', DATE_SUB(NOW(), INTERVAL 1 DAY), 'Info', 'Mise en maintenance planifiée', 5),
('Redémarrage', DATE_SUB(NOW(), INTERVAL 2 DAY), 'Info', 'Équipement redémarré avec succès', 6),

-- Alertes anciennes
('Connexion', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Avertissement', 'Perte de paquets détectée (5%)', 7),
('Température', DATE_SUB(NOW(), INTERVAL 4 DAY), 'Avertissement', 'Température élevée: 75°C', 1);

-- ============================================================================
-- TICKETS
-- ============================================================================

-- Tickets ouverts
INSERT INTO ticket (date_ouverture, statut, gravite, description, id_equipement, id_utilisateur, date_fermeture) VALUES
-- Tickets critiques ouverts
(DATE_SUB(NOW(), INTERVAL 2 HOUR), 'Ouvert', 'Critique', 
 'Point d''accès AP-ETG2-02 complètement hors service. Utilisateurs du 2e étage Sud sans WiFi.', 
 15, NULL, NULL),

(DATE_SUB(NOW(), INTERVAL 5 HOUR), 'En cours', 'Critique', 
 'SRV-WEB-01 présente une charge CPU anormalement élevée. Investigation en cours pour identifier le processus responsable.', 
 1, 2, NULL),

-- Tickets moyens
(DATE_SUB(NOW(), INTERVAL 1 DAY), 'En cours', 'Moyenne', 
 'Latence intermittente sur RTR-MAIN-01. À surveiller et diagnostiquer.', 
 11, 3, NULL),

(DATE_SUB(NOW(), INTERVAL 2 DAY), 'Ouvert', 'Moyenne', 
 'Imprimante IMP-ETG2-01 signale un niveau de toner faible. Remplacement nécessaire.', 
 20, NULL, NULL),

-- Tickets faibles
(DATE_SUB(NOW(), INTERVAL 3 DAY), 'Ouvert', 'Faible', 
 'PC-ADMIN-01 nécessite une mise à jour Windows. Planifier intervention.', 
 18, 4, NULL),

-- Tickets résolus (historique)
(DATE_SUB(NOW(), INTERVAL 5 DAY), 'Fermé', 'Haute', 
 'SRV-FILE-01 ne répond plus. Redémarrage effectué avec succès.', 
 5, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),

(DATE_SUB(NOW(), INTERVAL 7 DAY), 'Fermé', 'Moyenne', 
 'SWITCH-ETG1-01 présentait des ports défectueux. Ports remplacés.', 
 8, 3, DATE_SUB(NOW(), INTERVAL 6 DAY)),

(DATE_SUB(NOW(), INTERVAL 10 DAY), 'Résolu', 'Faible', 
 'Caméra CAM-ENTREE-01 image floue. Nettoyage de l''objectif effectué.', 
 21, 4, DATE_SUB(NOW(), INTERVAL 9 DAY)),

-- Ticket annulé
(DATE_SUB(NOW(), INTERVAL 15 DAY), 'Annulé', 'Faible', 
 'Fausse alerte - Équipement fonctionnait normalement.', 
 13, 2, DATE_SUB(NOW(), INTERVAL 14 DAY));

-- ============================================================================
-- STATISTIQUES DES DONNÉES INSÉRÉES
-- ============================================================================

SELECT 'Statistiques des données de test :' AS ' ';
SELECT '====================================' AS ' ';

SELECT 'Utilisateurs' AS Table_name, COUNT(*) AS Nombre_enregistrements FROM utilisateur
UNION ALL
SELECT 'Salles', COUNT(*) FROM salle
UNION ALL
SELECT 'Équipements', COUNT(*) FROM equipement
UNION ALL
SELECT 'Connexions', COUNT(*) FROM connexion
UNION ALL
SELECT 'Alertes', COUNT(*) FROM alerte
UNION ALL
SELECT 'Tickets', COUNT(*) FROM ticket;

-- Résumé par état des équipements
SELECT '
Résumé des équipements par état :' AS ' ';
SELECT etat, COUNT(*) AS nombre 
FROM equipement 
GROUP BY etat;

-- Résumé des tickets par statut
SELECT '
Résumé des tickets par statut :' AS ' ';
SELECT statut, COUNT(*) AS nombre 
FROM ticket 
GROUP BY statut;

-- Résumé des alertes par niveau
SELECT '
Résumé des alertes par niveau :' AS ' ';
SELECT niveau, COUNT(*) AS nombre 
FROM alerte 
GROUP BY niveau;

-- ============================================================================
-- FIN DU SCRIPT DE DONNÉES DE TEST
-- ============================================================================
