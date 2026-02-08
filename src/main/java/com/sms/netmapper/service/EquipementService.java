package com.sms.netmapper.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sms.netmapper.model.Equipement;
import com.sms.netmapper.repository.EquipementRepository;

/**
 * Service métier pour la gestion des équipements réseau
 * Centralise la logique métier et les validations
 * 
 * @author SMS Informatique - NetMapper
 */
@Service
@Transactional
public class EquipementService {

    @Autowired
    private EquipementRepository equipementRepository;

    // ========== CRUD DE BASE ==========

    public List<Equipement> getAllEquipements() {
        return equipementRepository.findAll();
    }

    public Optional<Equipement> getEquipementById(Integer id) {
        return equipementRepository.findById(id);
    }

    public Equipement createEquipement(Equipement equipement) {
        // Validation : IP unique
        if (equipementRepository.existsByAdresseIp(equipement.getAdresseIp())) {
            throw new IllegalArgumentException("Un équipement avec l'IP " + equipement.getAdresseIp() + " existe déjà");
        }

        // Validation : Type valide
        if (!isValidType(equipement.getType())) {
            throw new IllegalArgumentException("Type d'équipement invalide : " + equipement.getType());
        }

        // Normalisation : Nom en majuscules
        equipement.setNom(equipement.getNom().toUpperCase());

        // État par défaut
        if (equipement.getEtat() == null) {
            equipement.setEtat("Actif");
        }

        return equipementRepository.save(equipement);
    }

    public Equipement updateEquipement(Integer id, Equipement equipementDetails) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable : ID " + id));

        // Vérifier IP unique (si changée)
        if (!equipement.getAdresseIp().equals(equipementDetails.getAdresseIp())) {
            if (equipementRepository.existsByAdresseIp(equipementDetails.getAdresseIp())) {
                throw new IllegalArgumentException("L'IP " + equipementDetails.getAdresseIp() + " est déjà utilisée");
            }
        }

        equipement.setNom(equipementDetails.getNom().toUpperCase());
        equipement.setType(equipementDetails.getType());
        equipement.setAdresseIp(equipementDetails.getAdresseIp());
        equipement.setAdresseMac(equipementDetails.getAdresseMac());
        equipement.setFabricant(equipementDetails.getFabricant());
        equipement.setModele(equipementDetails.getModele());
        equipement.setEtat(equipementDetails.getEtat());
        equipement.setSalle(equipementDetails.getSalle());

        return equipementRepository.save(equipement);
    }

    public void deleteEquipement(Integer id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable : ID " + id));

        // Validation : Pas d'alertes/tickets
        if (equipement.getAlertes() != null && !equipement.getAlertes().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer : cet équipement a des alertes");
        }

        if (equipement.getTickets() != null && !equipement.getTickets().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer : cet équipement a des tickets");
        }

        equipementRepository.delete(equipement);
    }

    // ========== FILTRES ==========

    public List<Equipement> getByType(String type) {
        return equipementRepository.findByType(type);
    }

    public List<Equipement> getByEtat(String etat) {
        return equipementRepository.findByEtat(etat);
    }

    public Optional<Equipement> findByIp(String ip) {
        return equipementRepository.findByAdresseIp(ip);
    }

    // ========== OPÉRATIONS MÉTIER ==========

    public Equipement changerEtat(Integer id, String nouvelEtat) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Équipement introuvable : ID " + id));

        if (!isValidEtat(nouvelEtat)) {
            throw new IllegalArgumentException("État invalide : " + nouvelEtat);
        }

        equipement.setEtat(nouvelEtat);
        return equipementRepository.save(equipement);
    }

    public Map<String, Object> getStatistiques() {
        long total = equipementRepository.count();

        Map<String, Long> parEtat = new HashMap<>();
        parEtat.put("actifs", (long) equipementRepository.findByEtat("Actif").size());
        parEtat.put("inactifs", (long) equipementRepository.findByEtat("Inactif").size());
        parEtat.put("en_maintenance", (long) equipementRepository.findByEtat("En maintenance").size());
        parEtat.put("hors_service", (long) equipementRepository.findByEtat("Hors service").size());

        Map<String, Long> parType = new HashMap<>();
        parType.put("serveurs", (long) equipementRepository.findByType("Serveur").size());
        parType.put("switches", (long) equipementRepository.findByType("Switch").size());
        parType.put("routeurs", (long) equipementRepository.findByType("Routeur").size());
        parType.put("points_acces", (long) equipementRepository.findByType("Point d'accès").size());
        parType.put("postes_travail", (long) equipementRepository.findByType("Poste de travail").size());

        double tauxDispo = total > 0 ? (double) parEtat.get("actifs") / total * 100 : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("par_etat", parEtat);
        stats.put("par_type", parType);
        stats.put("taux_disponibilite", Math.round(tauxDispo * 100.0) / 100.0);

        return stats;
    }

    // ========== VALIDATIONS ==========

    private boolean isValidType(String type) {
        return List.of("Serveur", "Switch", "Routeur", "Point d'accès",
                "Poste de travail", "Imprimante", "Caméra IP").contains(type);
    }

    private boolean isValidEtat(String etat) {
        return List.of("Actif", "Inactif", "En maintenance", "Hors service").contains(etat);
    }

    public boolean existsById(Integer id) {
        return equipementRepository.existsById(id);
    }
}