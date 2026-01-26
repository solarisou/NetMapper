package com.sms.netmapper.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité Utilisateur - Comptes utilisateurs du système
 * Implémente UserDetails pour l'intégration avec Spring Security
 * 
 * @author SMS Informatique
 * @version 1.0
 */
@Entity
@Table(name = "utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer idUtilisateur;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "Le rôle est obligatoire")
    @Column(name = "role", nullable = false, length = 50)
    private String role; // Administrateur, Technicien, Manager, Observateur

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 50, message = "Le login ne peut pas dépasser 50 caractères")
    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse; // Hash BCrypt

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    // ========================================================================
    // Méthodes de UserDetails pour Spring Security
    // ========================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Conversion du rôle en GrantedAuthority
        // Le préfixe "ROLE_" est requis par Spring Security
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())
        );
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Les comptes n'expirent pas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Les comptes ne sont pas verrouillés
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Les credentials n'expirent pas
    }

    @Override
    public boolean isEnabled() {
        return true; // Tous les comptes sont activés
    }

    /**
     * Méthode pour affichage dans les logs (sans le mot de passe)
     */
    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role='" + role + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
