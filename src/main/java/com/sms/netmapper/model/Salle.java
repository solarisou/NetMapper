package com.sms.netmapper.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Salle - Localisation physique des équipements
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "salle", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nom", "etage"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salle")
    private Integer idSalle;

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Size(max = 20, message = "L'étage ne peut pas dépasser 20 caractères")
    @Column(name = "etage", length = 20)
    private String etage;

    @Size(max = 255, message = "Le chemin du fichier ne peut pas dépasser 255 caractères")
    @Column(name = "plan_fichier", length = 255)
    private String planFichier;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Relation One-To-Many avec Equipement
    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("salle") // Éviter la récursion infinie lors de la sérialisation JSON
    private List<Equipement> equipements = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur simplifié sans équipements
     */
    public Salle(String nom, String etage, String planFichier) {
        this.nom = nom;
        this.etage = etage;
        this.planFichier = planFichier;
    }

    @Override
    public String toString() {
        return "Salle{" +
                "idSalle=" + idSalle +
                ", nom='" + nom + '\'' +
                ", etage='" + etage + '\'' +
                ", planFichier='" + planFichier + '\'' +
                '}';
    }
}
