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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sms.netmapper.model.Ticket;
import com.sms.netmapper.repository.TicketRepository;
import com.sms.netmapper.repository.UtilisateurRepository;

import jakarta.validation.Valid;

/**
 * Controller REST pour le système de ticketing
 * 
 * @author SMS Informatique
 */
@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * GET /api/tickets
     * Récupère tous les tickets
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tickets.size());
        response.put("data", tickets);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tickets/{id}
     * Récupère un ticket par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTicketById(@PathVariable Integer id) {
        return ticketRepository.findById(id)
            .map(ticket -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", ticket);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * GET /api/tickets/statut/{statut}
     * Récupère les tickets par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<Map<String, Object>> getTicketsByStatut(@PathVariable String statut) {
        List<Ticket> tickets = ticketRepository.findByStatut(statut);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("statut", statut);
        response.put("total", tickets.size());
        response.put("data", tickets);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tickets/ouverts
     * Récupère tous les tickets ouverts ou en cours
     */
    @GetMapping("/ouverts")
    public ResponseEntity<Map<String, Object>> getTicketsOuverts() {
        List<String> statutsOuverts = List.of("Ouvert", "En cours");
        List<Ticket> tickets = ticketRepository.findByStatutIn(statutsOuverts);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("total", tickets.size());
        response.put("data", tickets);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tickets/gravite/{gravite}
     * Récupère les tickets par gravité
     */
    @GetMapping("/gravite/{gravite}")
    public ResponseEntity<Map<String, Object>> getTicketsByGravite(@PathVariable String gravite) {
        List<Ticket> tickets = ticketRepository.findByGravite(gravite);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("gravite", gravite);
        response.put("total", tickets.size());
        response.put("data", tickets);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tickets/stats
     * Récupère les statistiques des tickets
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long total = ticketRepository.count();
        long ouverts = ticketRepository.findByStatut("Ouvert").size();
        long enCours = ticketRepository.findByStatut("En cours").size();
        long fermes = ticketRepository.findByStatut("Fermé").size();
        
        long critiques = ticketRepository.findByGravite("Critique").size();
        long elevees = ticketRepository.findByGravite("Élevée").size();
        long moyennes = ticketRepository.findByGravite("Moyenne").size();
        long faibles = ticketRepository.findByGravite("Faible").size();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("ouverts", ouverts);
        stats.put("en_cours", enCours);
        stats.put("fermes", fermes);
        stats.put("critiques", critiques);
        stats.put("elevees", elevees);
        stats.put("moyennes", moyennes);
        stats.put("faibles", faibles);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/tickets
     * Crée un nouveau ticket
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicket(@Valid @RequestBody Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Ticket créé avec succès");
        response.put("data", savedTicket);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PATCH /api/tickets/{id}/assigner
     * Assigne un ticket à un utilisateur
     */
    @PatchMapping("/{id}/assigner")
    public ResponseEntity<Map<String, Object>> assignerTicket(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        
        Integer idUtilisateur = body.get("id_utilisateur");
        
        return ticketRepository.findById(id)
            .flatMap(ticket -> utilisateurRepository.findById(idUtilisateur)
                .map(utilisateur -> {
                    ticket.setUtilisateur(utilisateur);
                    ticket.setStatut("En cours");
                    Ticket updatedTicket = ticketRepository.save(ticket);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Ticket assigné à " + utilisateur.getPrenom() + " " + utilisateur.getNom());
                    response.put("data", updatedTicket);
                    
                    return ResponseEntity.ok(response);
                }))
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket ou utilisateur non trouvé");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * PATCH /api/tickets/{id}/fermer
     * Ferme un ticket
     */
    @PatchMapping("/{id}/fermer")
    public ResponseEntity<Map<String, Object>> fermerTicket(@PathVariable Integer id) {
        return ticketRepository.findById(id)
            .map(ticket -> {
                ticket.setStatut("Fermé");
                ticket.setDateFermeture(LocalDateTime.now());
                Ticket updatedTicket = ticketRepository.save(ticket);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Ticket fermé avec succès");
                response.put("data", updatedTicket);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * PUT /api/tickets/{id}
     * Met à jour un ticket
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTicket(
            @PathVariable Integer id,
            @Valid @RequestBody Ticket ticketDetails) {
        
        return ticketRepository.findById(id)
            .map(ticket -> {
                ticket.setDescription(ticketDetails.getDescription());
                ticket.setGravite(ticketDetails.getGravite());
                ticket.setStatut(ticketDetails.getStatut());
                ticket.setEquipement(ticketDetails.getEquipement());
                ticket.setUtilisateur(ticketDetails.getUtilisateur());
                
                Ticket updatedTicket = ticketRepository.save(ticket);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Ticket mis à jour avec succès");
                response.put("data", updatedTicket);
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }

    /**
     * DELETE /api/tickets/{id}
     * Supprime un ticket
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTicket(@PathVariable Integer id) {
        return ticketRepository.findById(id)
            .map(ticket -> {
                ticketRepository.delete(ticket);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Ticket supprimé avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Ticket non trouvé avec l'ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
    }
}