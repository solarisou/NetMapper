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
import com.sms.netmapper.service.EquipementService;
import com.sms.netmapper.service.NotificationService;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des équipements réseau
 * REFACTORISÉ : Utilise EquipementService au lieu de EquipementRepository
 * 
 * @author SMS Informatique - NetMapper
 */
@RestController
@RequestMapping("/equipements")
@CrossOrigin(origins = "*")
public class EquipementController {

    @Autowired
    private EquipementService equipementService;
    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/equipements
     * Récupère tous les équipements
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEquipements() {
        List<Equipement> equipements = equipementService.getAllEquipements();
        
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
        return equipementService.getEquipementById(id)
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
        List<Equipement> equipements = equipementService.getByType(type);
        
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
        List<Equipement> equipements = equipementService.getByEtat(etat);
        
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
        Map<String, Object> stats = equipementService.getStatistiques();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/equipements/search
     * Recherche d'équipements par IP
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByIp(@RequestParam String ip) {
        return equipementService.findByIp(ip)
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

    /**
     * POST /api/equipements
     * Crée un nouvel équipement
     * La validation métier est gérée par le Service
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEquipement(@Valid @RequestBody Equipement equipement) {
        try {
            Equipement savedEquipement = equipementService.createEquipement(equipement);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Équipement créé avec succès");
            response.put("data", savedEquipement);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * PUT /api/equipements/{id}
     * Met à jour un équipement existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEquipement(
            @PathVariable Integer id,
            @Valid @RequestBody Equipement equipementDetails) {
        
        try {
            Equipement updatedEquipement = equipementService.updateEquipement(id, equipementDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Équipement mis à jour avec succès");
            response.put("data", updatedEquipement);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
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
    
    try {
        // Récupérer l'ancien état AVANT la mise à jour
        String ancienEtat = equipementService.getEquipementById(id)
                .map(Equipement::getEtat)
                .orElse("Inconnu");

        Equipement updatedEquipement = equipementService.changerEtat(id, nouvelEtat);

        // Notifier le frontend via WebSocket
        notificationService.notifierChangementEtat(
                id,
                updatedEquipement.getNom(),
                ancienEtat,
                nouvelEtat
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "État mis à jour avec succès");
        response.put("data", updatedEquipement);
        
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

    /**
     * DELETE /api/equipements/{id}
     * Supprime un équipement
     * La validation métier (alertes/tickets) est gérée par le Service
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEquipement(@PathVariable Integer id) {
        try {
            equipementService.deleteEquipement(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Équipement supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}