package com.sms.netmapper.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sms.netmapper.model.Utilisateur;

/**
 * Repository pour l'entité Utilisateur
 * 
 * @author SMS Informatique
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    
    /**
     * Trouver un utilisateur par son login
     */
    Optional<Utilisateur> findByLogin(String login);
    
    /**
     * Vérifier si un login existe déjà
     */
    boolean existsByLogin(String login);
}
