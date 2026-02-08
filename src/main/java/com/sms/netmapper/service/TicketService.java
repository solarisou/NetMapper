package com.sms.netmapper.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sms.netmapper.model.Ticket;
import com.sms.netmapper.model.Utilisateur;
import com.sms.netmapper.repository.TicketRepository;
import com.sms.netmapper.repository.UtilisateurRepository;

/**
 * Service métier pour la gestion des tickets
 * Gère les workflows de ticketing
 * 
 * @author SMS Informatique - NetMapper
 */
@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // ========== CRUD DE BASE ==========

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    public Ticket createTicket(Ticket ticket) {
        // Validation : Statut valide
        if (ticket.getStatut() != null && !isValidStatut(ticket.getStatut())) {
            throw new IllegalArgumentException("Statut invalide : " + ticket.getStatut());
        }

        // Validation : Gravité valide
        if (!isValidGravite(ticket.getGravite())) {
            throw new IllegalArgumentException("Gravité invalide : " + ticket.getGravite());
        }

        // Valeurs par défaut
        if (ticket.getStatut() == null) {
            ticket.setStatut("Ouvert");
        }

        if (ticket.getDateOuverture() == null) {
            ticket.setDateOuverture(LocalDateTime.now());
        }

        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + id));

        ticket.setStatut(ticketDetails.getStatut());
        ticket.setGravite(ticketDetails.getGravite());
        ticket.setDescription(ticketDetails.getDescription());
        ticket.setUtilisateur(ticketDetails.getUtilisateur());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Integer id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket introuvable : ID " + id);
        }
        ticketRepository.deleteById(id);
    }

    // ========== FILTRES ==========

    public List<Ticket> getByStatut(String statut) {
        return ticketRepository.findByStatut(statut);
    }

    public List<Ticket> getByGravite(String gravite) {
        return ticketRepository.findByGravite(gravite);
    }

    public List<Ticket> getTicketsOuverts() {
        return ticketRepository.findByStatutIn(List.of("Ouvert", "En cours"));
    }

    // ========== WORKFLOWS MÉTIER ==========

    /**
     * Assigner un ticket à un technicien
     */
    public Ticket assignerTicket(Integer idTicket, Integer idTechnicien) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + idTicket));

        Utilisateur technicien = utilisateurRepository.findById(idTechnicien)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : ID " + idTechnicien));

        // Validation : Le technicien doit avoir le bon rôle
        if (!"Technicien".equals(technicien.getRole()) && !"Administrateur".equals(technicien.getRole())) {
            throw new IllegalArgumentException("Seuls les Techniciens et Administrateurs peuvent être assignés");
        }

        ticket.setUtilisateur(technicien);
        
        // Si le ticket était "Ouvert", le passer en "En cours"
        if ("Ouvert".equals(ticket.getStatut())) {
            ticket.setStatut("En cours");
        }

        return ticketRepository.save(ticket);
    }

    /**
     * Changer le statut d'un ticket
     */
    public Ticket changerStatut(Integer idTicket, String nouveauStatut) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + idTicket));

        if (!isValidStatut(nouveauStatut)) {
            throw new IllegalArgumentException("Statut invalide : " + nouveauStatut);
        }

        // Workflow : Si passage en "Résolu" ou "Fermé", définir date de fermeture
        if (("Résolu".equals(nouveauStatut) || "Fermé".equals(nouveauStatut)) 
                && ticket.getDateFermeture() == null) {
            ticket.setDateFermeture(LocalDateTime.now());
        }

        // Workflow : Si réouverture, supprimer date de fermeture
        if (("Ouvert".equals(nouveauStatut) || "En cours".equals(nouveauStatut)) 
                && ticket.getDateFermeture() != null) {
            ticket.setDateFermeture(null);
        }

        ticket.setStatut(nouveauStatut);
        return ticketRepository.save(ticket);
    }

    /**
     * Fermer un ticket (raccourci pour changerStatut)
     */
    public Ticket fermerTicket(Integer idTicket) {
        return changerStatut(idTicket, "Résolu");
    }

    /**
     * Rouvrir un ticket fermé
     */
    public Ticket rouvrirTicket(Integer idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + idTicket));

        if (!"Résolu".equals(ticket.getStatut()) && !"Fermé".equals(ticket.getStatut()) 
                && !"Annulé".equals(ticket.getStatut())) {
            throw new IllegalStateException("Seuls les tickets Résolu/Fermé/Annulé peuvent être rouverts");
        }

        ticket.setStatut("Ouvert");
        ticket.setDateFermeture(null);

        return ticketRepository.save(ticket);
    }

    /**
     * Annuler un ticket
     */
    public Ticket annulerTicket(Integer idTicket, String motif) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + idTicket));

        if ("Résolu".equals(ticket.getStatut()) || "Fermé".equals(ticket.getStatut())) {
            throw new IllegalStateException("Impossible d'annuler un ticket déjà résolu ou fermé");
        }

        ticket.setStatut("Annulé");
        ticket.setDateFermeture(LocalDateTime.now());
        
        // Ajouter le motif à la description
        ticket.setDescription(ticket.getDescription() + "\n\n[ANNULÉ] Motif : " + motif);

        return ticketRepository.save(ticket);
    }

    /**
     * Escalader un ticket (augmenter la gravité)
     */
    public Ticket escaladerTicket(Integer idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new IllegalArgumentException("Ticket introuvable : ID " + idTicket));

        String nouvelleGravite = switch (ticket.getGravite()) {
            case "Faible" -> "Moyenne";
            case "Moyenne" -> "Haute";
            case "Haute" -> "Critique";
            case "Critique" -> throw new IllegalStateException("Le ticket est déjà en gravité Critique");
            default -> throw new IllegalArgumentException("Gravité inconnue : " + ticket.getGravite());
        };

        ticket.setGravite(nouvelleGravite);
        return ticketRepository.save(ticket);
    }

    // ========== STATISTIQUES ==========

    public Map<String, Object> getStatistiques() {
        long total = ticketRepository.count();

        Map<String, Long> parStatut = new HashMap<>();
        parStatut.put("ouverts", (long) ticketRepository.findByStatut("Ouvert").size());
        parStatut.put("en_cours", (long) ticketRepository.findByStatut("En cours").size());
        parStatut.put("resolus", (long) ticketRepository.findByStatut("Résolu").size());
        parStatut.put("fermes", (long) ticketRepository.findByStatut("Fermé").size());
        parStatut.put("annules", (long) ticketRepository.findByStatut("Annulé").size());

        Map<String, Long> parGravite = new HashMap<>();
        parGravite.put("faible", (long) ticketRepository.findByGravite("Faible").size());
        parGravite.put("moyenne", (long) ticketRepository.findByGravite("Moyenne").size());
        parGravite.put("haute", (long) ticketRepository.findByGravite("Haute").size());
        parGravite.put("critique", (long) ticketRepository.findByGravite("Critique").size());

        long ticketsActifs = parStatut.get("ouverts") + parStatut.get("en_cours");

        // Temps moyen de résolution (simplification : derniers tickets fermés)
        double tempsMoyenResolution = calculerTempsMoyenResolution();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("par_statut", parStatut);
        stats.put("par_gravite", parGravite);
        stats.put("actifs", ticketsActifs);
        stats.put("temps_moyen_resolution_heures", tempsMoyenResolution);

        return stats;
    }

    /**
     * Calcul du temps moyen de résolution (en heures)
     */
    private double calculerTempsMoyenResolution() {
        List<Ticket> ticketsFermes = ticketRepository.findByStatutIn(List.of("Résolu", "Fermé"));
        
        if (ticketsFermes.isEmpty()) {
            return 0.0;
        }

        long totalHeures = ticketsFermes.stream()
                .filter(t -> t.getDateFermeture() != null)
                .mapToLong(t -> {
                    long heures = java.time.Duration.between(
                            t.getDateOuverture(), t.getDateFermeture()).toHours();
                    return heures;
                })
                .sum();

        return Math.round((double) totalHeures / ticketsFermes.size() * 100.0) / 100.0;
    }

    // ========== VALIDATIONS ==========

    private boolean isValidStatut(String statut) {
        return List.of("Ouvert", "En cours", "Résolu", "Fermé", "Annulé").contains(statut);
    }

    private boolean isValidGravite(String gravite) {
        return List.of("Faible", "Moyenne", "Haute", "Critique").contains(gravite);
    }
}