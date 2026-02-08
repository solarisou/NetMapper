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
import org.springframework.web.bind.annotation.RestController;

import com.sms.netmapper.model.Ticket;
import com.sms.netmapper.service.TicketService;

import jakarta.validation.Valid;

/**
 * Controller REST pour le système de ticketing
 * REFACTORISÉ : Utilise TicketService au lieu de TicketRepository
 * 
 * @author SMS Informatique - NetMapper
 */
@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * GET /api/tickets
     * Récupère tous les tickets
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        
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
        return ticketService.getTicketById(id)
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
        List<Ticket> tickets = ticketService.getByStatut(statut);
        
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
        List<Ticket> tickets = ticketService.getTicketsOuverts();
        
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
        List<Ticket> tickets = ticketService.getByGravite(gravite);
        
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
        Map<String, Object> stats = ticketService.getStatistiques();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/tickets
     * Crée un nouveau ticket
     * La validation métier est gérée par le Service
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicket(@Valid @RequestBody Ticket ticket) {
        try {
            Ticket savedTicket = ticketService.createTicket(ticket);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ticket créé avec succès");
            response.put("data", savedTicket);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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
        
        try {
            Ticket updatedTicket = ticketService.assignerTicket(id, idUtilisateur);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ticket assigné avec succès");
            response.put("data", updatedTicket);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PATCH /api/tickets/{id}/fermer
     * Ferme un ticket
     */
    @PatchMapping("/{id}/fermer")
    public ResponseEntity<Map<String, Object>> fermerTicket(@PathVariable Integer id) {
        try {
            Ticket updatedTicket = ticketService.fermerTicket(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ticket fermé avec succès");
            response.put("data", updatedTicket);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/tickets/{id}
     * Met à jour un ticket
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTicket(
            @PathVariable Integer id,
            @Valid @RequestBody Ticket ticketDetails) {
        
        try {
            Ticket updatedTicket = ticketService.updateTicket(id, ticketDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ticket mis à jour avec succès");
            response.put("data", updatedTicket);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * DELETE /api/tickets/{id}
     * Supprime un ticket
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTicket(@PathVariable Integer id) {
        try {
            ticketService.deleteTicket(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ticket supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}