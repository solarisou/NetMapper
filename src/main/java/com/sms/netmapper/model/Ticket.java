package com.sms.netmapper.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Ticket - Système de ticketing pour interventions
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Integer idTicket;

    @Column(name = "date_ouverture", nullable = false, updatable = false)
    private LocalDateTime dateOuverture;

    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 50, message = "Le statut ne peut pas dépasser 50 caractères")
    @Column(name = "statut", nullable = false, length = 50)
    private String statut = "Ouvert"; // Ouvert, En cours, Résolu, Fermé, Annulé

    @NotBlank(message = "La gravité est obligatoire")
    @Size(max = 20, message = "La gravité ne peut pas dépasser 20 caractères")
    @Column(name = "gravite", nullable = false, length = 20)
    private String gravite; // Faible, Moyenne, Haute, Critique

    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    // Relation Many-To-One avec Equipement (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipement", foreignKey = @ForeignKey(name = "fk_ticket_equipement"))
    @JsonIgnoreProperties({"alertes", "tickets", "connexionsSource", "connexionsDestination", "hibernateLazyInitializer"})
    private Equipement equipement;

    // Relation Many-To-One avec Utilisateur (technicien assigné, optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", foreignKey = @ForeignKey(name = "fk_ticket_utilisateur"))
    @JsonIgnoreProperties({"motDePasse", "hibernateLazyInitializer"})
    private Utilisateur utilisateur;

    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;

    @PrePersist
    protected void onCreate() {
        dateOuverture = LocalDateTime.now();
        if (statut == null || statut.isEmpty()) {
            statut = "Ouvert";
        }
    }

    /**
     * Constructeur simplifié
     */
    public Ticket(String gravite, String description, Equipement equipement, Utilisateur utilisateur) {
        this.gravite = gravite;
        this.description = description;
        this.equipement = equipement;
        this.utilisateur = utilisateur;
        this.statut = "Ouvert";
    }

    /**
     * Méthode pour fermer un ticket
     */
    public void fermer() {
        this.statut = "Fermé";
        this.dateFermeture = LocalDateTime.now();
    }

    /**
     * Méthode pour assigner un technicien
     */
    public void assignerTechnicien(Utilisateur technicien) {
        this.utilisateur = technicien;
        if ("Ouvert".equals(this.statut)) {
            this.statut = "En cours";
        }
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "idTicket=" + idTicket +
                ", statut='" + statut + '\'' +
                ", gravite='" + gravite + '\'' +
                ", dateOuverture=" + dateOuverture +
                ", equipement=" + (equipement != null ? equipement.getNom() : "null") +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getLogin() : "null") +
                '}';
    }
}
