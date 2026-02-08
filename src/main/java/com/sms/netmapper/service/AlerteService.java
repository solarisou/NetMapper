package com.sms.netmapper.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sms.netmapper.model.Alerte;
import com.sms.netmapper.model.Equipement;
import com.sms.netmapper.repository.AlerteRepository;
import com.sms.netmapper.repository.EquipementRepository;

/**
 * Service métier pour la gestion des alertes
 * Gère la génération automatique d'alertes
 * 
 * @author SMS Informatique - NetMapper
 */
@Service
@Transactional
public class AlerteService {

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private EquipementRepository equipementRepository;

    // ========== CRUD DE BASE ==========

    public List<Alerte> getAllAlertes() {
        return alerteRepository.findAll();
    }

    public Optional<Alerte> getAlerteById(Integer id) {
        return alerteRepository.findById(id);
    }

    public Alerte createAlerte(Alerte alerte) {
        // Validation : Niveau valide
        if (!isValidNiveau(alerte.getNiveau())) {
            throw new IllegalArgumentException("Niveau d'alerte invalide : " + alerte.getNiveau());
        }

        // Validation : Équipement existe
        if (alerte.getEquipement() == null || !equipementRepository.existsById(alerte.getEquipement().getIdEquipement())) {
            throw new IllegalArgumentException("Équipement introuvable");
        }

        // Date automatique si non fournie
        if (alerte.getDateAlerte() == null) {
            alerte.setDateAlerte(LocalDateTime.now());
        }

        return alerteRepository.save(alerte);
    }

    public Alerte updateAlerte(Integer id, Alerte alerteDetails) {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alerte introuvable : ID " + id));

        alerte.setType(alerteDetails.getType());
        alerte.setNiveau(alerteDetails.getNiveau());
        alerte.setMessage(alerteDetails.getMessage());

        return alerteRepository.save(alerte);
    }

    public void deleteAlerte(Integer id) {
        if (!alerteRepository.existsById(id)) {
            throw new IllegalArgumentException("Alerte introuvable : ID " + id);
        }
        alerteRepository.deleteById(id);
    }

    // ========== FILTRES ==========

    public List<Alerte> getByNiveau(String niveau) {
        return alerteRepository.findByNiveau(niveau);
    }

    public List<Alerte> getByEquipement(Integer idEquipement) {
        return alerteRepository.findByEquipementIdEquipement(idEquipement);
    }

    public List<Alerte> getAlertesActives() {
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        return alerteRepository.findByDateAlerteAfter(depuis24h);
    }

    public List<Alerte> getAlertesCritiques() {
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        return alerteRepository.findByNiveauAndDateAlerteAfter("Critique", depuis24h);
    }

    // ========== GÉNÉRATION AUTOMATIQUE D'ALERTES ==========

    /**
     * Créer une alerte automatique suite à un événement système
     */
    public Alerte creerAlerteAutomatique(Equipement equipement, String type, String message, String niveau) {
        Alerte alerte = new Alerte();
        alerte.setType(type);
        alerte.setNiveau(niveau);
        alerte.setMessage(message);
        alerte.setEquipement(equipement);
        alerte.setDateAlerte(LocalDateTime.now());

        return alerteRepository.save(alerte);
    }

    /**
     * Alerte équipement hors service
     */
    public Alerte alerteEquipementHorsService(Equipement equipement) {
        String message = String.format("Équipement %s (%s) passé hors service", 
                equipement.getNom(), equipement.getAdresseIp());
        
        return creerAlerteAutomatique(equipement, "Panne", message, "Critique");
    }

    /**
     * Alerte équipement injoignable (ping failed)
     */
    public Alerte alerteEquipementInjoignable(Equipement equipement) {
        String message = String.format("Équipement %s (%s) injoignable depuis 5 minutes", 
                equipement.getNom(), equipement.getAdresseIp());
        
        return creerAlerteAutomatique(equipement, "Connexion", message, "Critique");
    }

    /**
     * Alerte utilisation CPU élevée
     */
    public Alerte alerteCPUElevee(Equipement equipement, int pourcentage) {
        String message = String.format("Utilisation CPU à %d%% sur %s", 
                pourcentage, equipement.getNom());
        
        String niveau = pourcentage >= 90 ? "Critique" : "Avertissement";
        
        return creerAlerteAutomatique(equipement, "CPU", message, niveau);
    }

    /**
     * Alerte mémoire insuffisante
     */
    public Alerte alerteMemoireInsuffisante(Equipement equipement, int pourcentage) {
        String message = String.format("Mémoire RAM à %d%% sur %s", 
                pourcentage, equipement.getNom());
        
        String niveau = pourcentage >= 95 ? "Critique" : "Avertissement";
        
        return creerAlerteAutomatique(equipement, "Mémoire", message, niveau);
    }

    /**
     * Alerte espace disque faible
     */
    public Alerte alerteDisqueFaible(Equipement equipement, int pourcentageRestant) {
        String message = String.format("Espace disque faible (%d%% restant) sur %s", 
                pourcentageRestant, equipement.getNom());
        
        String niveau = pourcentageRestant < 10 ? "Critique" : "Avertissement";
        
        return creerAlerteAutomatique(equipement, "Disque", message, niveau);
    }

    /**
     * Alerte maintenance planifiée
     */
    public Alerte alerteMaintenancePlanifiee(Equipement equipement) {
        String message = String.format("Maintenance planifiée pour %s", equipement.getNom());
        
        return creerAlerteAutomatique(equipement, "Maintenance", message, "Info");
    }

    // ========== STATISTIQUES ==========

    public Map<String, Object> getStatistiques() {
        long total = alerteRepository.count();
        
        Map<String, Long> parNiveau = new HashMap<>();
        parNiveau.put("info", (long) alerteRepository.findByNiveau("Info").size());
        parNiveau.put("avertissement", (long) alerteRepository.findByNiveau("Avertissement").size());
        parNiveau.put("critique", (long) alerteRepository.findByNiveau("Critique").size());
        parNiveau.put("erreur", (long) alerteRepository.findByNiveau("Erreur").size());

        // Alertes des dernières 24h
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        long actives = alerteRepository.findByDateAlerteAfter(depuis24h).size();
        long critiques24h = alerteRepository.findByNiveauAndDateAlerteAfter("Critique", depuis24h).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("par_niveau", parNiveau);
        stats.put("actives_24h", actives);
        stats.put("critiques_24h", critiques24h);

        return stats;
    }

    // ========== NETTOYAGE ==========

    /**
     * Supprimer les alertes de plus de X jours
     */
    public int nettoyerAnciennesAlertes(int joursConservation) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(joursConservation);
        List<Alerte> anciennes = alerteRepository.findAll().stream()
                .filter(a -> a.getDateAlerte().isBefore(dateLimite))
                .toList();
        
        alerteRepository.deleteAll(anciennes);
        return anciennes.size();
    }

    // ========== VALIDATIONS ==========

    private boolean isValidNiveau(String niveau) {
        return List.of("Info", "Avertissement", "Critique", "Erreur").contains(niveau);
    }
}