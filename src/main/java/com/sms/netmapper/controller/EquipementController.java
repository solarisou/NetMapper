package com.sms.netmapper.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.netmapper.model.Equipement;
import com.sms.netmapper.repository.EquipementRepository;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des équipements réseau
 * 
 * @author SMS Informatique
 */
@RestController
@RequestMapping("/equipements")
@CrossOrigin(origins = "*")
public class EquipementController {

    @Autowired
    private EquipementRepository equipementRepository;

    /**
     * GET /api/equipements
     * Récupère tous les équipements
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEquipements() {
        List<Equipement> equipements = equipementRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", equipements.size());
        response.put("data", equipements);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/equipements/{id}
     * Récupère un équipement par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEquipementById(@PathVariable Integer id) {
        return equipementRepository.findById(id)
            .map(equipement -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", equipement);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Équipement non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/equipements/type/{type}
     * Récupère les équipements par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getEquipementsByType(@PathVariable String type) {
        List<Equipement> equipements = equipementRepository.findByType(type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("type", type);
        response.put("total", equipements.size());
        response.put("data", equipements);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/equipements/etat/{etat}
     * Récupère les équipements par état
     */
    @GetMapping("/etat/{etat}")
    public ResponseEntity<Map<String, Object>> getEquipementsByEtat(@PathVariable String etat) {
        List<Equipement> equipements = equipementRepository.findByEtat(etat);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("etat", etat);
        response.put("total", equipements.size());
        response.put("data", equipements);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/equipements/stats
     * Récupère les statistiques des équipements
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = equipementRepository.count();
        long actifs = equipementRepository.findByEtat("Actif").size();
        long inactifs = equipementRepository.findByEtat("Inactif").size();
        long maintenance = equipementRepository.findByEtat("En maintenance").size();
        long horsService = equipementRepository.findByEtat("Hors service").size();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("actifs", actifs);
        stats.put("inactifs", inactifs);
        stats.put("en_maintenance", maintenance);
        stats.put("hors_service", horsService);
        
        // Statistiques par type
        Map<String, Long> parType = new HashMap<>();
        parType.put("serveurs", (long) equipementRepository.findByType("Serveur").size());
        parType.put("switches", (long) equipementRepository.findByType("Switch").size());
        parType.put("routeurs", (long) equipementRepository.findByType("Routeur").size());
        parType.put("points_acces", (long) equipementRepository.findByType("Point d'accès").size());
        parType.put("postes_travail", (long) equipementRepository.findByType("Poste de travail").size());
        stats.put("par_type", parType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/equipements
     * Crée un nouvel équipement
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEquipement(@Valid @RequestBody Equipement equipement) {
        // Vérifier si l'IP existe déjà
        if (equipementRepository.existsByAdresseIp(equipement.getAdresseIp())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Un équipement avec cette adresse IP existe déjà");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        
        Equipement savedEquipement = equipementRepository.save(equipement);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Équipement créé avec succès");
        response.put("data", savedEquipement);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/equipements/{id}
     * Met à jour un équipement existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEquipement(
            @PathVariable Integer id,
            @Valid @RequestBody Equipement equipementDetails) {
        
        return equipementRepository.findById(id)
            .map(equipement -> {
                // Mise à jour des champs
                equipement.setNom(equipementDetails.getNom());
                equipement.setType(equipementDetails.getType());
                equipement.setAdresseIp(equipementDetails.getAdresseIp());
                equipement.setAdresseMac(equipementDetails.getAdresseMac());
                equipement.setFabricant(equipementDetails.getFabricant());
                equipement.setModele(equipementDetails.getModele());
                equipement.setEtat(equipementDetails.getEtat());
                equipement.setSalle(equipementDetails.getSalle());
                
                Equipement updatedEquipement = equipementRepository.save(equipement);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Équipement mis à jour avec succès");
                response.put("data", updatedEquipement);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Équipement non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * PATCH /api/equipements/{id}/etat
     * Met à jour uniquement l'état d'un équipement
     */
    @PatchMapping("/{id}/etat")
    public ResponseEntity<Map<String, Object>> updateEtat(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        
        String nouvelEtat = body.get("etat");
        
        return equipementRepository.findById(id)
            .map(equipement -> {
                equipement.setEtat(nouvelEtat);
                Equipement updatedEquipement = equipementRepository.save(equipement);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "État mis à jour avec succès");
                response.put("data", updatedEquipement);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Équipement non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * DELETE /api/equipements/{id}
     * Supprime un équipement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEquipement(@PathVariable Integer id) {
        return equipementRepository.findById(id)
            .map(equipement -> {
                equipementRepository.delete(equipement);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Équipement supprimé avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Équipement non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/equipements/search
     * Recherche d'équipements par IP
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByIp(@RequestParam String ip) {
        return equipementRepository.findByAdresseIp(ip)
            .map(equipement -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", equipement);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Aucun équipement trouvé avec l'IP: " + ip);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
}