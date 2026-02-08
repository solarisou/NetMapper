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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.netmapper.model.Alerte;
import com.sms.netmapper.service.AlerteService;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des alertes système
 * REFACTORISÉ : Utilise AlerteService au lieu de AlerteRepository
 * 
 * @author SMS Informatique - NetMapper
 */
@RestController
@RequestMapping("/alertes")
@CrossOrigin(origins = "*")
public class AlerteController {

    @Autowired
    private AlerteService alerteService;

    /**
     * GET /api/alertes
     * Récupère toutes les alertes
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlertes() {
        List<Alerte> alertes = alerteService.getAllAlertes();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", alertes.size());
        response.put("data", alertes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/alertes/{id}
     * Récupère une alerte par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAlerteById(@PathVariable Integer id) {
        return alerteService.getAlerteById(id)
            .map(alerte -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", alerte);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Alerte non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/alertes/actives
     * Récupère toutes les alertes récentes (dernières 24h)
     */
    @GetMapping("/actives")
    public ResponseEntity<Map<String, Object>> getAlertesActives() {
        List<Alerte> alertes = alerteService.getAlertesActives();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", alertes.size());
        response.put("data", alertes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/alertes/niveau/{niveau}
     * Récupère les alertes par niveau
     */
    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<Map<String, Object>> getAlertesByNiveau(@PathVariable String niveau) {
        List<Alerte> alertes = alerteService.getByNiveau(niveau);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("niveau", niveau);
        response.put("total", alertes.size());
        response.put("data", alertes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/alertes/critiques
     * Récupère les alertes critiques récentes (dernières 24h)
     */
    @GetMapping("/critiques")
    public ResponseEntity<Map<String, Object>> getAlertesCritiques() {
        List<Alerte> alertes = alerteService.getAlertesCritiques();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", alertes.size());
        response.put("data", alertes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/alertes/equipement/{idEquipement}
     * Récupère les alertes d'un équipement spécifique
     */
    @GetMapping("/equipement/{idEquipement}")
    public ResponseEntity<Map<String, Object>> getAlertesByEquipement(@PathVariable Integer idEquipement) {
        List<Alerte> alertes = alerteService.getByEquipement(idEquipement);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", alertes.size());
        response.put("data", alertes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/alertes/stats
     * Récupère les statistiques des alertes
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = alerteService.getStatistiques();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/alertes
     * Crée une nouvelle alerte
     * La validation métier est gérée par le Service
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlerte(@Valid @RequestBody Alerte alerte) {
        try {
            Alerte savedAlerte = alerteService.createAlerte(alerte);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Alerte créée avec succès");
            response.put("data", savedAlerte);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * PUT /api/alertes/{id}
     * Met à jour une alerte
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAlerte(
            @PathVariable Integer id,
            @Valid @RequestBody Alerte alerteDetails) {
        
        try {
            Alerte updatedAlerte = alerteService.updateAlerte(id, alerteDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Alerte mise à jour avec succès");
            response.put("data", updatedAlerte);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * DELETE /api/alertes/{id}
     * Supprime une alerte
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlerte(@PathVariable Integer id) {
        try {
            alerteService.deleteAlerte(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Alerte supprimée avec succès");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}