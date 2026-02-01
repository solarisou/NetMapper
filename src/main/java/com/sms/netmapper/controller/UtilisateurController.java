package com.sms.netmapper.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.netmapper.model.Utilisateur;
import com.sms.netmapper.repository.UtilisateurRepository;

import jakarta.validation.Valid;

/**
 * Controller REST pour la gestion des utilisateurs
 * 
 * @author SMS Informatique
 */
@RestController
@RequestMapping("/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * GET /api/utilisateurs
     * Récupère tous les utilisateurs (sans les mots de passe)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        
        // Masquer les mots de passe
        utilisateurs.forEach(u -> u.setMotDePasse("***HIDDEN***"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", utilisateurs.size());
        response.put("data", utilisateurs);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/utilisateurs/{id}
     * Récupère un utilisateur par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUtilisateurById(@PathVariable Integer id) {
        return utilisateurRepository.findById(id)
            .map(utilisateur -> {
                utilisateur.setMotDePasse("***HIDDEN***");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", utilisateur);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/utilisateurs/login/{login}
     * Récupère un utilisateur par son login
     */
    @GetMapping("/login/{login}")
    public ResponseEntity<Map<String, Object>> getUtilisateurByLogin(@PathVariable String login) {
        return utilisateurRepository.findByLogin(login)
            .map(utilisateur -> {
                utilisateur.setMotDePasse("***HIDDEN***");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", utilisateur);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé avec le login: " + login);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/utilisateurs/stats
     * Récupère les statistiques des utilisateurs
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = utilisateurRepository.count();
        
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        
        long administrateurs = utilisateurs.stream()
            .filter(u -> "Administrateur".equals(u.getRole()))
            .count();
        
        long techniciens = utilisateurs.stream()
            .filter(u -> "Technicien".equals(u.getRole()))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("administrateurs", administrateurs);
        stats.put("techniciens", techniciens);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/utilisateurs
     * Crée un nouvel utilisateur
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        // Vérifier si le login existe déjà
        if (utilisateurRepository.existsByLogin(utilisateur.getLogin())) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Un utilisateur avec ce login existe déjà");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        
        // Hasher le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        savedUtilisateur.setMotDePasse("***HIDDEN***");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Utilisateur créé avec succès");
        response.put("data", savedUtilisateur);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/utilisateurs/{id}
     * Met à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUtilisateur(
            @PathVariable Integer id,
            @Valid @RequestBody Utilisateur utilisateurDetails) {
        
        return utilisateurRepository.findById(id)
            .map(utilisateur -> {
                utilisateur.setNom(utilisateurDetails.getNom());
                utilisateur.setPrenom(utilisateurDetails.getPrenom());
                utilisateur.setLogin(utilisateurDetails.getLogin());
                utilisateur.setRole(utilisateurDetails.getRole());
                
                // Ne mettre à jour le mot de passe que s'il est fourni et non vide
                if (utilisateurDetails.getMotDePasse() != null 
                    && !utilisateurDetails.getMotDePasse().isEmpty()
                    && !"***HIDDEN***".equals(utilisateurDetails.getMotDePasse())) {
                    utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDetails.getMotDePasse()));
                }
                
                Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);
                updatedUtilisateur.setMotDePasse("***HIDDEN***");
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Utilisateur mis à jour avec succès");
                response.put("data", updatedUtilisateur);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * PATCH /api/utilisateurs/{id}/mot-de-passe
     * Change le mot de passe d'un utilisateur
     */
    @PatchMapping("/{id}/mot-de-passe")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        
        String nouveauMotDePasse = body.get("nouveau_mot_de_passe");
        
        if (nouveauMotDePasse == null || nouveauMotDePasse.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Le nouveau mot de passe est requis");
            return ResponseEntity.badRequest().body(response);
        }
        
        return utilisateurRepository.findById(id)
            .map(utilisateur -> {
                utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
                utilisateurRepository.save(utilisateur);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Mot de passe modifié avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * DELETE /api/utilisateurs/{id}
     * Supprime un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUtilisateur(@PathVariable Integer id) {
        return utilisateurRepository.findById(id)
            .map(utilisateur -> {
                utilisateurRepository.delete(utilisateur);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Utilisateur supprimé avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
}