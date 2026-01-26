package com.sms.netmapper.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Equipement - Dispositifs réseau supervisés
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "equipement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipement")
    private Integer idEquipement;

    @NotBlank(message = "Le nom de l'équipement est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le type est obligatoire")
    @Size(max = 50, message = "Le type ne peut pas dépasser 50 caractères")
    @Column(name = "type", nullable = false, length = 50)
    private String type; // Serveur, Switch, Routeur, etc.

    @NotBlank(message = "L'adresse IP est obligatoire")
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Format d'adresse IP invalide")
    @Column(name = "adresse_ip", nullable = false, unique = true, length = 45)
    private String adresseIp;

    @Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$", message = "Format d'adresse MAC invalide")
    @Column(name = "adresse_mac", length = 17)
    private String adresseMac;

    @Size(max = 100, message = "Le fabricant ne peut pas dépasser 100 caractères")
    @Column(name = "fabricant", length = 100)
    private String fabricant;

    @Size(max = 100, message = "Le modèle ne peut pas dépasser 100 caractères")
    @Column(name = "modele", length = 100)
    private String modele;

    @Column(name = "etat", nullable = false, length = 20)
    private String etat = "Actif"; // Actif, Inactif, En maintenance, Hors service

    // Relation Many-To-One avec Salle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salle", foreignKey = @ForeignKey(name = "fk_equipement_salle"))
    @JsonIgnoreProperties({"equipements", "hibernateLazyInitializer"})
    private Salle salle;

    @Column(name = "date_ajout", nullable = false, updatable = false)
    private LocalDateTime dateAjout;

    @Column(name = "date_maj", nullable = false)
    private LocalDateTime dateMaj;

    // Relations One-To-Many
    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("equipement")
    private List<Alerte> alertes = new ArrayList<>();

    @OneToMany(mappedBy = "equipement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("equipement")
    private List<Ticket> tickets = new ArrayList<>();

    // Relations pour les connexions (source et destination)
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"source", "destination"})
    private List<Connexion> connexionsSource = new ArrayList<>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"source", "destination"})
    private List<Connexion> connexionsDestination = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateAjout = LocalDateTime.now();
        dateMaj = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateMaj = LocalDateTime.now();
    }

    /**
     * Constructeur simplifié
     */
    public Equipement(String nom, String type, String adresseIp, String adresseMac, 
                      String fabricant, String modele, String etat, Salle salle) {
        this.nom = nom;
        this.type = type;
        this.adresseIp = adresseIp;
        this.adresseMac = adresseMac;
        this.fabricant = fabricant;
        this.modele = modele;
        this.etat = etat;
        this.salle = salle;
    }

    @Override
    public String toString() {
        return "Equipement{" +
                "idEquipement=" + idEquipement +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", adresseIp='" + adresseIp + '\'' +
                ", etat='" + etat + '\'' +
                '}';
    }
}
