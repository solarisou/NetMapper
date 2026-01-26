package com.sms.netmapper.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sms.netmapper.model.Ticket;
import com.sms.netmapper.model.Utilisateur;
import com.sms.netmapper.model.Equipement;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    
    /**
     * Trouver tous les tickets d'un équipement
     */
    List<Ticket> findByEquipement(Equipement equipement);
    
    /**
     * Trouver tous les tickets assignés à un utilisateur
     */
    List<Ticket> findByUtilisateur(Utilisateur utilisateur);
    
    /**
     * Trouver tous les tickets par statut
     */
    List<Ticket> findByStatut(String statut);
    
    /**
     * Trouver tous les tickets par gravité
     */
    List<Ticket> findByGravite(String gravite);
    
    /**
     * Trouver les tickets ouverts ou en cours
     */
    List<Ticket> findByStatutIn(List<String> statuts);
}
