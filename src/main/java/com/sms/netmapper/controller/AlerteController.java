package com.sms.netmapper.controller;

import java.time.LocalDateTime;
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
import com.sms.netmapper.repository.AlerteRepository;
import com.sms.netmapper.repository.EquipementRepository;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des alertes système
 * 
 * @author SMS Informatique
 */
@RestController
@RequestMapping("/alertes")
@CrossOrigin(origins = "*")
public class AlerteController {

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private EquipementRepository equipementRepository;

    /**
     * GET /api/alertes
     * Récupère toutes les alertes
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlertes() {
        List<Alerte> alertes = alerteRepository.findAll();
        
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
        return alerteRepository.findById(id)
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
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        List<Alerte> alertes = alerteRepository.findAll().stream()
            .filter(a -> a.getDateAlerte().isAfter(depuis24h))
            .toList();
        
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
        List<Alerte> alertes = alerteRepository.findByNiveau(niveau);
        
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
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        List<Alerte> alertes = alerteRepository.findRecentCriticalAlertes(depuis24h);
        
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
        return equipementRepository.findById(idEquipement)
            .map(equipement -> {
                List<Alerte> alertes = equipement.getAlertes();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("equipement", equipement.getNom());
                response.put("total", alertes.size());
                response.put("data", alertes);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Équipement non trouvé avec l'ID: " + idEquipement);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/alertes/stats
     * Récupère les statistiques des alertes
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = alerteRepository.count();
        long critiques = alerteRepository.findByNiveau("Critique").size();
        long avertissements = alerteRepository.findByNiveau("Avertissement").size();
        long infos = alerteRepository.findByNiveau("Info").size();
        
        LocalDateTime depuis24h = LocalDateTime.now().minusHours(24);
        long actives = alerteRepository.findAll().stream()
            .filter(a -> a.getDateAlerte().isAfter(depuis24h))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("actives_24h", actives);
        stats.put("critiques", critiques);
        stats.put("avertissements", avertissements);
        stats.put("infos", infos);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/alertes
     * Crée une nouvelle alerte
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlerte(@Valid @RequestBody Alerte alerte) {
        Alerte savedAlerte = alerteRepository.save(alerte);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Alerte créée avec succès");
        response.put("data", savedAlerte);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/alertes/{id}
     * Met à jour une alerte
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAlerte(
            @PathVariable Integer id,
            @Valid @RequestBody Alerte alerteDetails) {
        
        return alerteRepository.findById(id)
            .map(alerte -> {
                alerte.setNiveau(alerteDetails.getNiveau());
                alerte.setMessage(alerteDetails.getMessage());
                alerte.setType(alerteDetails.getType());
                alerte.setEquipement(alerteDetails.getEquipement());
                
                Alerte updatedAlerte = alerteRepository.save(alerte);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Alerte mise à jour avec succès");
                response.put("data", updatedAlerte);
                
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
     * DELETE /api/alertes/{id}
     * Supprime une alerte
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlerte(@PathVariable Integer id) {
        return alerteRepository.findById(id)
            .map(alerte -> {
                alerteRepository.delete(alerte);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Alerte supprimée avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Alerte non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
}