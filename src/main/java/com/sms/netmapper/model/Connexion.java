package com.sms.netmapper.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Connexion - Liens réseau entre équipements (topologie)
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "connexion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_source", "id_destination"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connexion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_connexion")
    private Integer idConnexion;

    @NotBlank(message = "Le type de connexion est obligatoire")
    @Size(max = 50, message = "Le type ne peut pas dépasser 50 caractères")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // Ethernet, Fibre, Wi-Fi, VPN

    // Relation Many-To-One avec Equipement (source)
    @NotNull(message = "L'équipement source est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_source", nullable = false, foreignKey = @ForeignKey(name = "fk_connexion_source"))
    @JsonIgnoreProperties({"connexionsSource", "connexionsDestination", "alertes", "tickets", "hibernateLazyInitializer"})
    private Equipement source;

    // Relation Many-To-One avec Equipement (destination)
    @NotNull(message = "L'équipement destination est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destination", nullable = false, foreignKey = @ForeignKey(name = "fk_connexion_destination"))
    @JsonIgnoreProperties({"connexionsSource", "connexionsDestination", "alertes", "tickets", "hibernateLazyInitializer"})
    private Equipement destination;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur simplifié
     */
    public Connexion(String type, Equipement source, Equipement destination) {
        this.type = type;
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Connexion{" +
                "idConnexion=" + idConnexion +
                ", type='" + type + '\'' +
                ", source=" + (source != null ? source.getNom() : "null") +
                ", destination=" + (destination != null ? destination.getNom() : "null") +
                '}';
    }
}
