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

import com.sms.netmapper.model.Salle;
import com.sms.netmapper.repository.SalleRepository;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des salles/localisations
 * 
 * @author SMS Informatique
 */
@RestController
@RequestMapping("/salles")
@CrossOrigin(origins = "*")
public class SalleController {

    @Autowired
    private SalleRepository salleRepository;

    /**
     * GET /api/salles
     * Récupère toutes les salles
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSalles() {
        List<Salle> salles = salleRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", salles.size());
        response.put("data", salles);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/salles/{id}
     * Récupère une salle par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSalleById(@PathVariable Integer id) {
        return salleRepository.findById(id)
            .map(salle -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", salle);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Salle non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/salles/{id}/equipements
     * Récupère les équipements d'une salle
     */
    @GetMapping("/{id}/equipements")
    public ResponseEntity<Map<String, Object>> getEquipementsBySalle(@PathVariable Integer id) {
        return salleRepository.findById(id)
            .map(salle -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("salle", salle.getNom());
                response.put("total", salle.getEquipements().size());
                response.put("data", salle.getEquipements());
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Salle non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/salles/stats
     * Récupère les statistiques des salles
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Salle> salles = salleRepository.findAll();
        
        long totalSalles = salles.size();
        long totalEquipements = salles.stream()
            .mapToLong(s -> s.getEquipements().size())
            .sum();
        
        // Salle avec le plus d'équipements
        Salle sallePlusEquipee = salles.stream()
            .max((s1, s2) -> Integer.compare(s1.getEquipements().size(), s2.getEquipements().size()))
            .orElse(null);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_salles", totalSalles);
        stats.put("total_equipements", totalEquipements);
        
        if (sallePlusEquipee != null) {
            stats.put("salle_plus_equipee", Map.of(
                "nom", sallePlusEquipee.getNom(),
                "nb_equipements", sallePlusEquipee.getEquipements().size()
            ));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/salles
     * Crée une nouvelle salle
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSalle(@Valid @RequestBody Salle salle) {
        Salle savedSalle = salleRepository.save(salle);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Salle créée avec succès");
        response.put("data", savedSalle);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/salles/{id}
     * Met à jour une salle
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSalle(
            @PathVariable Integer id,
            @Valid @RequestBody Salle salleDetails) {
        
        return salleRepository.findById(id)
            .map(salle -> {
                salle.setNom(salleDetails.getNom());
                salle.setEtage(salleDetails.getEtage());
                salle.setPlanFichier(salleDetails.getPlanFichier());
                
                Salle updatedSalle = salleRepository.save(salle);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Salle mise à jour avec succès");
                response.put("data", updatedSalle);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Salle non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * DELETE /api/salles/{id}
     * Supprime une salle (seulement si elle n'a pas d'équipements)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSalle(@PathVariable Integer id) {
        return salleRepository.findById(id)
            .map(salle -> {
                // Vérifier qu'il n'y a pas d'équipements
                if (!salle.getEquipements().isEmpty()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Impossible de supprimer une salle contenant des équipements");
                    response.put("nb_equipements", salle.getEquipements().size());
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                
                salleRepository.delete(salle);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Salle supprimée avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Salle non trouvée avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
}