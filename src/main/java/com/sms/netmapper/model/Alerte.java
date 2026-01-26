package com.sms.netmapper.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Alerte - Notifications et événements système
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "alerte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte")
    private Integer idAlerte;

    @NotBlank(message = "Le type d'alerte est obligatoire")
    @Size(max = 50, message = "Le type ne peut pas dépasser 50 caractères")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // Panne, CPU, Mémoire, Disque, Connexion, etc.

    @Column(name = "date_alerte", nullable = false)
    private LocalDateTime dateAlerte;

    @NotBlank(message = "Le niveau est obligatoire")
    @Size(max = 20, message = "Le niveau ne peut pas dépasser 20 caractères")
    @Column(name = "niveau", nullable = false, length = 20)
    private String niveau; // Info, Avertissement, Critique, Erreur

    @NotBlank(message = "Le message est obligatoire")
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // Relation Many-To-One avec Equipement
    @NotNull(message = "L'équipement est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipement", nullable = false, foreignKey = @ForeignKey(name = "fk_alerte_equipement"))
    @JsonIgnoreProperties({"alertes", "tickets", "connexionsSource", "connexionsDestination", "hibernateLazyInitializer"})
    private Equipement equipement;

    @PrePersist
    protected void onCreate() {
        if (dateAlerte == null) {
            dateAlerte = LocalDateTime.now();
        }
    }

    /**
     * Constructeur simplifié
     */
    public Alerte(String type, String niveau, String message, Equipement equipement) {
        this.type = type;
        this.niveau = niveau;
        this.message = message;
        this.equipement = equipement;
        this.dateAlerte = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Alerte{" +
                "idAlerte=" + idAlerte +
                ", type='" + type + '\'' +
                ", niveau='" + niveau + '\'' +
                ", dateAlerte=" + dateAlerte +
                ", equipement=" + (equipement != null ? equipement.getNom() : "null") +
                '}';
    }
}
